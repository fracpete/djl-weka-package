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
 * FixedDir.java
 * Copyright (C) 2025 University of Waikato, Hamilton, New Zealand
 */

package weka.classifiers.djl.outputdirgenerator;

import weka.core.Option;
import weka.core.Utils;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.List;
import java.util.Vector;

/**
 * Uses the user-supplied output dir.
 *
 * @author fracpete (fracpete at waikato dot ac dot nz)
 */
public class FixedDir
  extends AbstractOutputDirGenerator {

  private static final long serialVersionUID = -8875224901669801709L;

  /** the output dir to use. */
  protected File m_OutputDir = new File(".");

  /**
   * Default constructor.
   */
  public FixedDir() {
    super();
  }

  /**
   * Instantiates the generator and sets the dir to use.
   *
   * @param dir		the dir to use
   */
  public FixedDir(String dir) {
    this(new File(dir));
  }

  /**
   * Instantiates the generator and sets the dir to use.
   *
   * @param dir		the dir to use
   */
  public FixedDir(File dir) {
    super();
    setOutputDir(dir);
  }

  /**
   * Returns a string describing the object.
   *
   * @return a description suitable for displaying in the gui
   */
  public String globalInfo() {
    return "Uses the user-supplied output dir.";
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
      "\tThe directory to store the network in.\n"
	+ "\t(default: .)",
      "output-dir", 1, "-output-dir <dir>"));

    enm = super.listOptions();
    while (enm.hasMoreElements())
      result.add(enm.nextElement());

    return new Vector<Option>().elements();
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

    tmpStr = Utils.getOption("output-dir", options);
    if (tmpStr.isEmpty())
      setOutputDir(new File("."));
    else
      setOutputDir(new File(tmpStr));

    super.setOptions(options);
  }

  /**
   * Gets the current option settings for the OptionHandler.
   *
   * @return the array of current option settings as an array of strings
   */
  @Override
  public String[] getOptions() {
    List<String> result;

    result = new ArrayList<String>();

    result.add("-output-dir");
    result.add(getOutputDir().toString());

    result.addAll(Arrays.asList(super.getOptions()));

    return result.toArray(new String[0]);
  }

  /**
   * Sets the output dir.
   *
   * @param value 	the dir
   */
  public void setOutputDir(File value) {
    m_OutputDir = value;
  }

  /**
   * Gets the output dir.
   *
   * @return 		the dir
   */
  public File getOutputDir() {
    return m_OutputDir;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return		tip text for this property suitable for
   * 			displaying in the explorer/experimenter gui
   */
  public String outputDirTipText() {
    return "The output directory to use.";
  }

  /**
   * Generates the output directory.
   *
   * @return the directory
   */
  @Override
  public File generate() {
    return m_OutputDir;
  }
}
