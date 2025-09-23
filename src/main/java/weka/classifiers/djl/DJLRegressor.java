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
import ai.djl.training.DefaultTrainingConfig;
import ai.djl.training.EasyTrain;
import ai.djl.training.Trainer;
import ai.djl.training.TrainingConfig;
import ai.djl.training.dataset.Dataset;
import ai.djl.training.listener.TrainingListener;
import ai.djl.training.loss.TabNetRegressionLoss;
import ai.djl.translate.Translator;
import weka.classifiers.AbstractClassifier;
import weka.classifiers.djl.dataset.InstancesDataset;
import weka.classifiers.djl.idgenerator.FixedID;
import weka.classifiers.djl.idgenerator.IDGenerator;
import weka.classifiers.djl.networkgenerator.NetworkGenerator;
import weka.classifiers.djl.networkgenerator.TabularRegressionGenerator;
import weka.classifiers.djl.outputdirgenerator.FixedDir;
import weka.classifiers.djl.outputdirgenerator.OutputDirGenerator;
import weka.core.Capabilities;
import weka.core.Capabilities.Capability;
import weka.core.Instance;
import weka.core.Instances;
import weka.core.Option;
import weka.core.Utils;

import java.io.File;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.List;
import java.util.Vector;

/**
 * Uses Deep Java Library for building a regression model.
 *
 * @author fracpete (fracpete at waikato dot ac dot nz)
 */
public class DJLRegressor
  extends AbstractClassifier {

  private static final long serialVersionUID = -8361229968357782660L;

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

    getCapabilities().test(data);

    modelID   = m_ID.generate();
    modelDir  = m_OutputDir.generate();
    modelPath = modelDir.toPath();

    // delete any left-over .params files
    for (File f: modelDir.listFiles()) {
      if (f.getName().matches("^" + modelID + "-[0-9]+.params$")) {
	if (getDebug())
	  System.out.println("Removing: " + f);
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

    m_Model = Model.newInstance("tabular");
    m_Model.setBlock(m_Network.generate(m_Dataset));

    trainingConfig = new DefaultTrainingConfig(
      new TabNetRegressionLoss())
	.addTrainingListeners(TrainingListener.Defaults.basic());

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
}
