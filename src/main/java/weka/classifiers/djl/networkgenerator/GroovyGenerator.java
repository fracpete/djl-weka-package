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
 * GroovyGenerator.java
 * Copyright (C) 2025 University of Waikato, Hamilton, New Zealand
 */

package weka.classifiers.djl.networkgenerator;

import ai.djl.basicdataset.tabular.TabularDataset;
import ai.djl.nn.Block;
import weka.core.Option;
import weka.core.OptionHandler;
import weka.core.Utils;
import weka.core.scripting.GroovyMod;

import java.io.File;
import java.util.Collections;
import java.util.Enumeration;
import java.util.Vector;

/**
 * Uses the specified Groovy script to generate the network.
 *
 * @author fracpete (fracpete at waikato dot ac dot nz)
 */
public class GroovyGenerator
  extends AbstractNetworkGenerator {

  /** the Groovy module. */
  protected File m_GroovyModule = new File(System.getProperty("user.dir"));

  /** the options for the Groovy module. */
  protected String[] m_GroovyOptions = new String[0];

  /** the loaded Groovy object. */
  protected transient NetworkGenerator m_GroovyObject = null;

  /**
   * Returns a string describing the object.
   *
   * @return a description suitable for displaying in the gui
   */
  public String globalInfo() {
    return "Loads the specified Groovy script (which must implement " + NetworkGenerator.class.getName()
	     + ") and uses it to generate the network structure.";
  }

  /**
   * Returns an enumeration describing the available options.
   *
   * @return an enumeration of all the available options.
   */
  @Override
  public Enumeration<Option> listOptions() {
    Vector<Option> result = new Vector<Option>();

    result.addElement(
      new Option(
	"\tThe Groovy module to load (full path)\n"
	  + "\tOptions after '--' will be passed on to the Groovy module.",
	"G", 1, "-G <filename>"));

    result.addElement(
      new Option(
	"\tThe options to pass on to the Groovy module.",
	"O", 1, "-O <options>"));

    result.addAll(Collections.list(super.listOptions()));

    return result.elements();
  }

  /**
   * Parses a given list of options.
   *
   * @param options the list of options as an array of strings
   * @throws Exception if an option is not supported
   */
  @Override
  public void setOptions(String[] options) throws Exception {
    String tmpStr;

    m_GroovyOptions = new String[0];

    tmpStr = Utils.getOption('G', options);
    if (!tmpStr.isEmpty())
      setGroovyModule(new File(tmpStr));
    else
      setGroovyModule(new File(System.getProperty("user.dir")));

    setGroovyOptions(Utils.getOption('O', options));

    super.setOptions(options);

    Utils.checkForRemainingOptions(options);
  }

  /**
   * Gets the current settings of the Classifier.
   *
   * @return an array of strings suitable for passing to setOptions
   */
  @Override
  public String[] getOptions() {
    Vector<String> result = new Vector<String>();

    result.add("-G");
    result.add(getGroovyModule().getAbsolutePath());

    if (!getGroovyOptions().isEmpty()) {
      result.add("-O");
      result.add(getGroovyOptions());
    }

    Collections.addAll(result, super.getOptions());

    return result.toArray(new String[0]);
  }

  /**
   * Returns the tip text for this property.
   *
   * @return tip text for this property suitable for displaying in the
   *         explorer/experimenter gui
   */
  public String GroovyModuleTipText() {
    return "The Groovy module to load and execute.";
  }

  /**
   * Sets the Groovy module.
   *
   * @param value the Groovy module
   */
  public void setGroovyModule(File value) {
    m_GroovyModule = value;
    initGroovyObject();
  }

  /**
   * Gets the Groovy module.
   *
   * @return the Groovy module
   */
  public File getGroovyModule() {
    return m_GroovyModule;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return tip text for this property suitable for displaying in the
   *         explorer/experimenter gui
   */
  public String GroovyOptionsTipText() {
    return "The options for the Groovy module.";
  }

  /**
   * Sets the Groovy module options.
   *
   * @param value the options
   */
  public void setGroovyOptions(String value) {
    try {
      m_GroovyOptions = Utils.splitOptions(value).clone();
      initGroovyObject();
    }
    catch (Exception e) {
      m_GroovyOptions = new String[0];
      e.printStackTrace();
    }
  }

  /**
   * Gets the Groovy module options.
   *
   * @return the options
   */
  public String getGroovyOptions() {
    return Utils.joinOptions(m_GroovyOptions);
  }

  /**
   * tries to initialize the groovy object and set its options.
   */
  protected void initGroovyObject() {
    try {
      if (m_GroovyModule.isFile())
	m_GroovyObject = (NetworkGenerator) GroovyMod.newInstance(m_GroovyModule, NetworkGenerator.class, getClass().getClassLoader());
      else
	m_GroovyObject = null;

      if ((m_GroovyObject != null) && (m_GroovyObject instanceof OptionHandler))
	((OptionHandler) m_GroovyObject).setOptions(m_GroovyOptions.clone());
      else if (m_GroovyOptions.length > 0)
	System.err.println("ERROR: Groovy script does not implement " + OptionHandler.class.getName() + ", cannot set options!");
    }
    catch (Exception e) {
      m_GroovyObject = null;
      e.printStackTrace();
    }
  }


  /**
   * Generates the network using the supplied dataset.
   *
   * @param dataset the dataset to generate the network for
   * @return the network
   */
  @Override
  protected Block doGenerate(TabularDataset dataset) {
    initGroovyObject();
    return m_GroovyObject.generate(dataset);
  }
}
