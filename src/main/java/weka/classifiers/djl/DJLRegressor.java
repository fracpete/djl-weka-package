/*
 *   This program is free software: you can redistribute it and/or modify
 *   it under the terms of the GNU General Public License as published by
 *   the Free Software Foundation, either version 3 of the License, or
 *   (at your option) any later version.
 *
 *   This program is distributed in the hope that it will be useful,
 *   but WITHOUT ANY WARRANTY; without even the implied warranty of
 *   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *   GNU General Public License for more details.
 *
 *   You should have received a copy of the GNU General Public License
 *   along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

/*
 * DJLRegressor.java
 * Copyright (C) 2025 University of Waikato, Hamilton, New Zealand
 */

package weka.classifiers.djl;

import ai.djl.Model;
import ai.djl.basicdataset.tabular.ListFeatures;
import ai.djl.inference.Predictor;
import ai.djl.ndarray.types.Shape;
import ai.djl.repository.zoo.ZooModel;
import ai.djl.training.EasyTrain;
import ai.djl.training.Trainer;
import ai.djl.training.TrainingConfig;
import ai.djl.training.dataset.Dataset;
import ai.djl.translate.Translator;
import weka.classifiers.RandomizableClassifier;
import weka.classifiers.djl.dataset.InstancesDataset;
import weka.classifiers.djl.idgenerator.FixedID;
import weka.classifiers.djl.idgenerator.IDGenerator;
import weka.classifiers.djl.networkgenerator.NetworkGenerator;
import weka.classifiers.djl.networkgenerator.TabularRegressionGenerator;
import weka.classifiers.djl.outputdirgenerator.FixedDir;
import weka.classifiers.djl.outputdirgenerator.OutputDirGenerator;
import weka.classifiers.djl.trainingconfiggenerator.TabNetRegressionLossGenerator;
import weka.classifiers.djl.trainingconfiggenerator.TrainingConfigGenerator;
import weka.core.Capabilities;
import weka.core.Capabilities.Capability;
import weka.core.Instance;
import weka.core.Instances;
import weka.core.Option;
import weka.core.UniqueIDs;
import weka.core.Utils;

import java.io.Closeable;
import java.io.File;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Vector;

/**
 <!-- globalinfo-start -->
 * Uses Deep Java Library for building a regression model.
 * <br><br>
 <!-- globalinfo-end -->
 *
 <!-- options-start -->
 * Valid options are: <p>
 *
 * <pre> -network &lt;classname + options&gt;
 *  The network generator to use.
 *  (default: weka.classifiers.djl.networkgenerator.TabularRegressionGenerator)</pre>
 *
 * <pre> -train-percentage &lt;int&gt;
 *  The percentage of the dataset to use for training (1-99).
 *  The rest will get used for validation.
 *  (default: 80)</pre>
 *
 * <pre> -mini-batch-size &lt;int&gt;
 *  The size to use for the mini batches.
 *  (default: 32)</pre>
 *
 * <pre> -num-epochs &lt;int&gt;
 *  The number of epochs to use for training.
 *  (default: 20)</pre>
 *
 * <pre> -id &lt;classname + options&gt;
 *  The ID generator to use (ID = prefix of model).
 *  (default: weka.classifiers.djl.idgenerator.FixedID)</pre>
 *
 * <pre> -output-dir &lt;classname + options&gt;
 *  The output directory generator to use.
 *  (default: weka.classifiers.djl.outputdirgenerator.FixedDir)</pre>
 *
 * <pre> -training-config &lt;classname + options&gt;
 *  The training config generator to use.
 *  (default: weka.classifiers.djl.trainingconfiggenerator.TabNetRegressionLossGenerator)</pre>
 *
 * <pre> -support-parallel-execution
 *  Whether to enable support for parallel execution,
 *  requires user to manually delete left-over model files (.params).
 *  (default: disabled)</pre>
 *
 * <pre> -S &lt;num&gt;
 *  Random number seed.
 *  (default 1)</pre>
 *
 * <pre> -output-debug-info
 *  If set, classifier is run in debug mode and
 *  may output additional info to the console</pre>
 *
 * <pre> -do-not-check-capabilities
 *  If set, classifier capabilities are not checked before classifier is built
 *  (use with caution).</pre>
 *
 * <pre> -num-decimal-places
 *  The number of decimal places for the output of numbers in the model (default 2).</pre>
 *
 * <pre> -batch-size
 *  The desired batch size for batch prediction  (default 100).</pre>
 *
 <!-- options-end -->
 *
 * @author fracpete (fracpete at waikato dot ac dot nz)
 */
public class DJLRegressor
  extends RandomizableClassifier
  implements AutoCloseable {

  private static final long serialVersionUID = -8361229968357782660L;

  /** for keeping track of models. */
  protected static Map<String,Model> m_Models = new HashMap<>();

  /** the network generator to use. */
  protected NetworkGenerator m_Network = new TabularRegressionGenerator();

  /** the percentage for the network's training set. */
  protected int m_TrainPercentage = 80;

  /** the batchsize. */
  protected int m_MiniBatchSize = 32;

  /** the number of epochs to train. */
  protected int m_NumEpochs = 20;

  /** the model ID/prefix generator. */
  protected IDGenerator m_ID = new FixedID();

  /** the output dir generator. */
  protected OutputDirGenerator m_OutputDir = new FixedDir();

  /** the training config generator. */
  protected TrainingConfigGenerator m_TrainingConfig = new TabNetRegressionLossGenerator();

  /** whether to support parallel execution. */
  protected boolean m_SupportParallelExecution = false;

  /** the header. */
  protected Instances m_Header;

  /** the DJL dataset. */
  protected transient InstancesDataset m_Dataset;

  /** the feature translator to use. */
  protected transient Translator<ListFeatures, Float> m_Translator;

  /** the model. */
  protected transient Model m_Model;

  /** the predictor to use. */
  protected transient Predictor<ListFeatures, Float> m_Predictor;

  /** the dataset config. */
  protected String m_DatasetConfig;

  /**
   * Returns a string describing the object.
   *
   * @return a description suitable for displaying in the gui
   */
  public String globalInfo() {
    return "Uses Deep Java Library for building a regression model.";
  }

  /**
   * Returns an enumeration of all the available options..
   *
   * @return an enumeration of all available options.
   */
  @Override
  public Enumeration<Option> listOptions() {
    Vector<Option> 	result;
    Enumeration<Option>	enm;

    result = new Vector<>();

    result.add(new Option(
      "\tThe network generator to use.\n"
	+ "\t(default: " + TabularRegressionGenerator.class.getName() + ")",
      "network", 1, "-network <classname + options>"));

    result.add(new Option(
      "\tThe percentage of the dataset to use for training (1-99).\n"
	+ "\tThe rest will get used for validation.\n"
	+ "\t(default: 80)",
      "train-percentage", 1, "-train-percentage <int>"));

    result.add(new Option(
      "\tThe size to use for the mini batches.\n"
	+ "\t(default: 32)",
      "mini-batch-size", 1, "-mini-batch-size <int>"));

    result.add(new Option(
      "\tThe number of epochs to use for training.\n"
	+ "\t(default: 20)",
      "num-epochs", 1, "-num-epochs <int>"));

    result.add(new Option(
      "\tThe ID generator to use (ID = prefix of model).\n"
	+ "\t(default: " + FixedID.class.getName() + ")",
      "id", 1, "-id <classname + options>"));

    result.add(new Option(
      "\tThe output directory generator to use.\n"
	+ "\t(default: " + FixedDir.class.getName() + ")",
      "output-dir", 1, "-output-dir <classname + options>"));

    result.add(new Option(
      "\tThe training config generator to use.\n"
	+ "\t(default: " + TabNetRegressionLossGenerator.class.getName() + ")",
      "training-config", 1, "-training-config <classname + options>"));

    result.add(new Option(
      "\tWhether to enable support for parallel execution, \n"
	+ "\trequires user to manually delete left-over model files (.params).\n"
	+ "\t(default: disabled)",
      "support-parallel-execution", 0, "-support-parallel-execution"));

    enm = super.listOptions();
    while (enm.hasMoreElements())
      result.add(enm.nextElement());

    return result.elements();
  }

  /**
   * Sets the OptionHandler's options using the given list. All options
   * will be set (or reset) during this call (i.e. incremental setting
   * of options is not possible).
   *
   * @param options the list of options as an array of strings
   * @throws Exception if an option is not supported
   */
  @Override
  public void setOptions(String[] options) throws Exception {
    String	tmpStr;
    String[]	tmpOpts;

    tmpStr = Utils.getOption("network", options);
    if (tmpStr.isEmpty()) {
      setNetwork(new TabularRegressionGenerator());
    }
    else {
      tmpOpts    = Utils.splitOptions(tmpStr);
      tmpStr     = tmpOpts[0];
      tmpOpts[0] = "";
      setNetwork((NetworkGenerator) Utils.forName(NetworkGenerator.class, tmpStr, tmpOpts));
    }

    tmpStr = Utils.getOption("train-percentage", options);
    if (tmpStr.isEmpty())
      setTrainPercentage(80);
    else
      setTrainPercentage(Integer.parseInt(tmpStr));

    tmpStr = Utils.getOption("mini-batch-size", options);
    if (tmpStr.isEmpty())
      setMiniBatchSize(32);
    else
      setMiniBatchSize(Integer.parseInt(tmpStr));

    tmpStr = Utils.getOption("num-epochs", options);
    if (tmpStr.isEmpty())
      setNumEpochs(20);
    else
      setNumEpochs(Integer.parseInt(tmpStr));

    tmpStr = Utils.getOption("id", options);
    if (tmpStr.isEmpty()) {
      setID(new FixedID());
    }
    else {
      tmpOpts    = Utils.splitOptions(tmpStr);
      tmpStr     = tmpOpts[0];
      tmpOpts[0] = "";
      setID((IDGenerator) Utils.forName(IDGenerator.class, tmpStr, tmpOpts));
    }

    tmpStr = Utils.getOption("output-dir", options);
    if (tmpStr.isEmpty()) {
      setOutputDir(new FixedDir());
    }
    else {
      tmpOpts    = Utils.splitOptions(tmpStr);
      tmpStr     = tmpOpts[0];
      tmpOpts[0] = "";
      setOutputDir((OutputDirGenerator) Utils.forName(OutputDirGenerator.class, tmpStr, tmpOpts));
    }

    tmpStr = Utils.getOption("training-config", options);
    if (tmpStr.isEmpty()) {
      setTrainingConfig(new TabNetRegressionLossGenerator());
    }
    else {
      tmpOpts    = Utils.splitOptions(tmpStr);
      tmpStr     = tmpOpts[0];
      tmpOpts[0] = "";
      setTrainingConfig((TrainingConfigGenerator) Utils.forName(TrainingConfigGenerator.class, tmpStr, tmpOpts));
    }

    setSupportParallelExecution(Utils.getFlag("support-parallel-execution", options));

    super.setOptions(options);
  }

  /**
   * Gets the current option settings for the OptionHandler.
   *
   * @return the array of current option settings as an array of strings
   */
  @Override
  public String[] getOptions() {
    List<String> 	result;

    result = new ArrayList<>();

    result.add("-network");
    result.add(Utils.toCommandLine(getNetwork()));

    result.add("-train-percentage");
    result.add("" + getTrainPercentage());

    result.add("-mini-batch-size");
    result.add("" + getMiniBatchSize());

    result.add("-num-epochs");
    result.add("" + getNumEpochs());

    result.add("-id");
    result.add(Utils.toCommandLine(getID()));

    result.add("-output-dir");
    result.add(Utils.toCommandLine(getOutputDir()));

    result.add("-training-config");
    result.add(Utils.toCommandLine(getTrainingConfig()));

    if (getSupportParallelExecution())
      result.add("-support-parallel-execution");

    result.addAll(Arrays.asList(super.getOptions()));

    return result.toArray(new String[0]);
  }

  /**
   * Sets the network generator to use.
   *
   * @param value 	the generator
   */
  public void setNetwork(NetworkGenerator value) {
    m_Network = value;
  }

  /**
   * Gets the network generator to use.
   *
   * @return 		the generator
   */
  public NetworkGenerator getNetwork() {
    return m_Network;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return		tip text for this property suitable for
   * 			displaying in the explorer/experimenter gui
   */
  public String networkTipText() {
    return "The generator to create the network structure with.";
  }

  /**
   * Sets the percentage to use for the internal training set.
   *
   * @param value 	the percentage
   */
  public void setTrainPercentage(int value) {
    if ((value > 0) && (value < 100))
      m_TrainPercentage = value;
  }

  /**
   * Gets the percentage to use for the internal training set.
   *
   * @return 		the percentage
   */
  public int getTrainPercentage() {
    return m_TrainPercentage;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return		tip text for this property suitable for
   * 			displaying in the explorer/experimenter gui
   */
  public String trainPercentageTipText() {
    return "The percentage to use for splitting the data into internal train/validation sets.";
  }

  /**
   * Sets the batch size to use.
   *
   * @param value 	the batch size
   */
  public void setMiniBatchSize(int value) {
    if (value > 0)
      m_MiniBatchSize = value;
  }

  /**
   * Gets the batch size to use.
   *
   * @return 		the batch size
   */
  public int getMiniBatchSize() {
    return m_MiniBatchSize;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return		tip text for this property suitable for
   * 			displaying in the explorer/experimenter gui
   */
  public String miniBatchSizeTipText() {
    return "The batch size to use.";
  }

  /**
   * Sets the number of epochs to train for.
   *
   * @param value 	the epochs
   */
  public void setNumEpochs(int value) {
    if (value > 0)
      m_NumEpochs = value;
  }

  /**
   * Gets the number of epochs to train for.
   *
   * @return 		the epochs
   */
  public int getNumEpochs() {
    return m_NumEpochs;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return		tip text for this property suitable for
   * 			displaying in the explorer/experimenter gui
   */
  public String numEpochsTipText() {
    return "The number of epochs to train for.";
  }

  /**
   * Sets the ID/prefix generator for saving the model.
   *
   * @param value 	the ID/prefix
   */
  public void setID(IDGenerator value) {
    m_ID = value;
  }

  /**
   * Gets the ID/prefix generator for saving the model.
   *
   * @return 		the ID/prefix
   */
  public IDGenerator getID() {
    return m_ID;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return		tip text for this property suitable for
   * 			displaying in the explorer/experimenter gui
   */
  public String IDTipText() {
    return "The ID/prefix generator to use for saving the model.";
  }

  /**
   * Sets the output directory generator to use.
   *
   * @param value 	the generator
   */
  public void setOutputDir(OutputDirGenerator value) {
    m_OutputDir = value;
  }

  /**
   * Gets the output directory generator to use.
   *
   * @return 		the generator
   */
  public OutputDirGenerator getOutputDir() {
    return m_OutputDir;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return		tip text for this property suitable for
   * 			displaying in the explorer/experimenter gui
   */
  public String outputDirTipText() {
    return "The generator to use for generating the output directory.";
  }

  /**
   * Sets the training config generator to use.
   *
   * @param value 	the generator
   */
  public void setTrainingConfig(TrainingConfigGenerator value) {
    m_TrainingConfig = value;
  }

  /**
   * Gets the training config generator to use.
   *
   * @return 		the generator
   */
  public TrainingConfigGenerator getTrainingConfig() {
    return m_TrainingConfig;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return		tip text for this property suitable for
   * 			displaying in the explorer/experimenter gui
   */
  public String trainingConfigTipText() {
    return "The generator to use for generating the configuration to train the network.";
  }

  /**
   * Sets whether to enable support for parallel execution.
   * If enabled, a unique ID gets appended to model IDs, requiring the user to manually delete unwanted .params files
   *
   * @param value 	true if to enable
   */
  public void setSupportParallelExecution(boolean value) {
    m_SupportParallelExecution = value;
  }

  /**
   * Gets the output directory generator to use.
   * If enabled, a unique ID gets appended to model IDs, requiring the user to manually delete unwanted .params files
   *
   * @return 		true if enabled
   */
  public boolean getSupportParallelExecution() {
    return m_SupportParallelExecution;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return		tip text for this property suitable for
   * 			displaying in the explorer/experimenter gui
   */
  public String supportParallelExecutionTipText() {
    return "If enabled, a unique ID gets appended to model IDs, requiring the user to manually delete unwanted .params files.";
  }

  /**
   * Returns the Capabilities of this classifier. Maximally permissive
   * capabilities are allowed by default. Derived classifiers should override
   * this method and first disable all capabilities and then enable just those
   * capabilities that make sense for the scheme.
   *
   * @return the capabilities of this object
   * @see Capabilities
   */
  @Override
  public Capabilities getCapabilities() {
    Capabilities result = super.getCapabilities();
    result.disableAll();
    result.enable(Capability.NUMERIC_ATTRIBUTES);
    result.enable(Capability.NUMERIC_CLASS);
    return result;
  }

  /**
   * Generates a classifier. Must initialize all fields of the classifier
   * that are not being set via options (ie. multiple calls of buildClassifier
   * must always lead to the same result). Must not change the dataset
   * in any way.
   *
   * @param data set of instances serving as training data
   * @throws Exception if the classifier has not been
   *                   generated successfully
   */
  @Override
  public void buildClassifier(Instances data) throws Exception {
    Dataset[] 				splitDataset;
    Dataset 				trainDataset;
    Dataset 				validateDataset;
    TrainingConfig 			trainingConfig;
    ZooModel<ListFeatures, Float> 	zooModel;
    String 				modelID;
    File 				modelDir;
    Path 				modelPath;
    String				modelName;

    getCapabilities().test(data);

    modelID   = m_ID.generate() + (m_SupportParallelExecution ? UniqueIDs.next() : "");
    modelDir  = m_OutputDir.generate().getAbsoluteFile();
    modelPath = modelDir.toPath();
    modelName = modelDir + "|" + modelID;

    // delete any left-over .params files
    for (File f: modelDir.listFiles()) {
      if (f.getName().matches("^" + modelID + "-[0-9]+.params$")) {
	if (getDebug())
	  System.out.println("Removing: " + f);
	try {
	  if (!f.delete())
	    System.err.println("Failed to delete: " + f);
	}
	catch (Exception e) {
	  System.err.println("Failed to delete: " + f);
	  e.printStackTrace();
	}
      }
    }

    if (getDebug())
      System.out.println("Training model: " + modelID);

    m_Dataset = InstancesDataset.builder()
		  .setSampling(m_MiniBatchSize, true)
		  .data(data)
		  .addAllFeatures()
		  .build();

    m_DatasetConfig = m_Dataset.toJson().toString();
    splitDataset    = m_Dataset.randomSplit(m_TrainPercentage, 100 - m_TrainPercentage);
    trainDataset    = splitDataset[0];
    validateDataset = splitDataset[1];

    DJLUtils.initClassLoader(this);
    DJLUtils.registerPytorch();
    DJLUtils.setPyTorchSeed(m_Seed);

    synchronized (m_Models) {
      if (m_Models.containsKey(modelName)) {
	m_Models.get(modelName).close();
	m_Models.remove(modelName);
      }
      m_Model = Model.newInstance(modelName);
      m_Model.setBlock(m_Network.generate(m_Dataset));
      m_Models.put(modelName, m_Model);
    }

    trainingConfig = m_TrainingConfig.generate();

    try (Trainer trainer = m_Model.newTrainer(trainingConfig)) {
      trainer.initialize(new Shape(1, m_Dataset.getFeatureSize()));
      EasyTrain.fit(trainer, m_NumEpochs, trainDataset, validateDataset);
    }

    m_Translator = m_Dataset.matchingTranslatorOptions().option(ListFeatures.class, Float.class);
    m_Header     = new Instances(data, 0);

    if (getDebug())
      System.out.println("Saving model '" + modelID + "' to: " + modelPath);
    zooModel = new ZooModel<>(m_Model, m_Translator);
    zooModel.save(modelPath, modelID);
  }

  /**
   * Prepares the classifier for predictions.
   */
  public void initPrediction() {
    String 	modelID;
    Path 	modelPath;

    modelID   = m_ID.generate();
    modelPath = m_OutputDir.generate().toPath();

    if (m_Model == null) {
      if (getDebug())
	System.out.println("Loading model '" + modelID + "' from: " + modelPath);
      try {
	m_Dataset = InstancesDataset.builder()
		      .setSampling(m_MiniBatchSize, true)
		      .data(m_Header)
		      .fromJson(m_DatasetConfig)
		      .build();
	m_Translator = m_Dataset.matchingTranslatorOptions().option(ListFeatures.class, Float.class);
	m_Model = Model.newInstance(modelID);
	m_Model.setBlock(m_Network.generate(m_Dataset));
	m_Model.load(modelPath);
      }
      catch (Exception e) {
	throw new IllegalStateException("Failed to recreate DJL dataset from config!", e);
      }
    }

    if (m_Predictor == null) {
      if (getDebug())
	System.out.println("Instantiating predictor for model: " + modelID);
      m_Predictor = m_Model.newPredictor(m_Translator);
    }
  }

  /**
   * Classifies the given test instance. The instance has to belong to a dataset
   * when it's being classified. Note that a classifier MUST implement either
   * this or distributionForInstance().
   *
   * @param instance the instance to be classified
   * @return the predicted most likely class for the instance or
   *         Utils.missingValue() if no prediction is made
   * @throws Exception if an error occurred during the prediction
   */
  @Override
  public double classifyInstance(Instance instance) throws Exception {
    ListFeatures 	input;
    int			i;
    int			index;
    Float 		pred;

    initPrediction();

    input = new ListFeatures();
    for (i = 0; i < m_Dataset.getFeatureSize(); i++) {
      index = instance.dataset().attribute(m_Dataset.getFeatures().get(i).getName()).index();
      if (instance.attribute(index).isNumeric())
	input.add("" + instance.value(index));
      else
	input.add(instance.stringValue(index));
    }
    pred = m_Predictor.predict(input);
    return pred.doubleValue();
  }

  /**
   * Returns a short description of the setup.
   *
   * @return		the description
   */
  @Override
  public String toString() {
    StringBuilder	result;

    result = new StringBuilder();
    result.append("Network generator...: ").append(Utils.toCommandLine(getNetwork())).append("\n");
    result.append("Train %.............: ").append(getTrainPercentage()).append("\n");
    result.append("Mini batch size.....: ").append(getMiniBatchSize()).append("\n");
    result.append("# epochs............: ").append(getNumEpochs()).append("\n");
    result.append("ID generator........: ").append(Utils.toCommandLine(getID())).append("\n");
    result.append("Output dir generator: ").append(Utils.toCommandLine(getOutputDir())).append("\n");

    return result.toString();
  }

  /**
   * Runs the classifier from the command-line with the specified options.
   *
   * @param args	the options for the classifier
   * @throws Exception	if execution fails
   */
  public static void main(String[] args) throws Exception {
    runClassifier(new DJLRegressor(), args);
  }

  /**
   * Closes this resource, relinquishing any underlying resources.
   * This method is invoked automatically on objects managed by the
   * {@code try}-with-resources statement.
   *
   * <p>While this interface method is declared to throw {@code
   * Exception}, implementers are <em>strongly</em> encouraged to
   * declare concrete implementations of the {@code close} method to
   * throw more specific exceptions, or to throw no exception at all
   * if the close operation cannot fail.
   *
   * <p> Cases where the close operation may fail require careful
   * attention by implementers. It is strongly advised to relinquish
   * the underlying resources and to internally <em>mark</em> the
   * resource as closed, prior to throwing the exception. The {@code
   * close} method is unlikely to be invoked more than once and so
   * this ensures that the resources are released in a timely manner.
   * Furthermore it reduces problems that could arise when the resource
   * wraps, or is wrapped, by another resource.
   *
   * <p><em>Implementers of this interface are also strongly advised
   * to not have the {@code close} method throw {@link
   * InterruptedException}.</em>
   * <p>
   * This exception interacts with a thread's interrupted status,
   * and runtime misbehavior is likely to occur if an {@code
   * InterruptedException} is {@linkplain Throwable#addSuppressed
   * suppressed}.
   * <p>
   * More generally, if it would cause problems for an
   * exception to be suppressed, the {@code AutoCloseable.close}
   * method should not throw it.
   *
   * <p>Note that unlike the {@link Closeable#close close}
   * method of {@link Closeable}, this {@code close} method
   * is <em>not</em> required to be idempotent.  In other words,
   * calling this {@code close} method more than once may have some
   * visible side effect, unlike {@code Closeable.close} which is
   * required to have no effect if called more than once.
   * <p>
   * However, implementers of this interface are strongly encouraged
   * to make their {@code close} methods idempotent.
   *
   * @throws Exception if this resource cannot be closed
   */
  @Override
  public void close() throws Exception {
    if (m_Model != null) {
      m_Model.close();
      m_Model = null;
    }
  }
}
