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
 * FixedID.java
 * Copyright (C) 2025 University of Waikato, Hamilton, New Zealand
 */

package weka.classifiers.djl.idgenerator;

import weka.core.Option;
import weka.core.Utils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.List;
import java.util.Vector;

/**
 * Just uses the supplied ID.
 *
 * @author fracpete (fracpete at waikato dot ac dot nz)
 */
public class FixedID
  extends AbstractIDGenerator {

  private static final long serialVersionUID = 6971300631570600112L;

  /** the ID to use. */
  protected String m_ID = "djl";

  /**
   * Default constructor.
   */
  public FixedID() {
    super();
  }

  /**
   * Instantiates the generator and sets the ID to use.
   *
   * @param id		the ID to use
   */
  public FixedID(String id) {
    super();
    setID(id);
  }

  /**
   * Returns a string describing the object.
   *
   * @return a description suitable for displaying in the gui
   */
  public String globalInfo() {
    return "Just uses the supplied ID.";
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
      "\tThe ID to use.\n"
	+ "\t(default: djl)",
      "id", 1, "-id <ID>"));

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

    tmpStr = Utils.getOption("id", options);
    if (tmpStr.isEmpty())
      setID("djl");
    else
      setID(tmpStr);

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

    result.add("-id");
    result.add(getID());

    result.addAll(Arrays.asList(super.getOptions()));

    return result.toArray(new String[0]);
  }

  /**
   * Sets the ID/prefix for saving the model.
   *
   * @param value 	the ID/prefix
   */
  public void setID(String value) {
    m_ID = value;
  }

  /**
   * Gets the ID/prefix for saving the model.
   *
   * @return 		the ID/prefix
   */
  public String getID() {
    return m_ID;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return		tip text for this property suitable for
   * 			displaying in the explorer/experimenter gui
   */
  public String IDTipText() {
    return "The ID to use.";
  }

  /**
   * Generates the ID.
   *
   * @return the ID
   */
  @Override
  public String generate() {
    return m_ID;
  }
}
