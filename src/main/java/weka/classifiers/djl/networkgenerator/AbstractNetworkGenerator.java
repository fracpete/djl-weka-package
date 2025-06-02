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
 * AbstractNetworkGenerator.java
 * Copyright (C) 2025 University of Waikato, Hamilton, New Zealand
 */

package weka.classifiers.djl.networkgenerator;

import ai.djl.basicdataset.tabular.TabularDataset;
import ai.djl.nn.Block;
import weka.core.Option;
import weka.core.OptionHandler;

import java.io.Serializable;
import java.util.Enumeration;
import java.util.Vector;

/**
 * Ancestor for network generators.
 *
 * @author fracpete (fracpete at waikato dot ac dot nz)
 */
public abstract class AbstractNetworkGenerator
  implements NetworkGenerator, OptionHandler, Serializable {

  private static final long serialVersionUID = -9021412063817679485L;

  /**
   * Returns an enumeration of all the available options..
   *
   * @return an enumeration of all available options.
   */
  @Override
  public Enumeration<Option> listOptions() {
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
  }

  /**
   * Gets the current option settings for the OptionHandler.
   *
   * @return the array of current option settings as an array of strings
   */
  @Override
  public String[] getOptions() {
    return new String[0];
  }

  /**
   * Checks the dataset before generating the network.
   *
   * @param dataset	the dataset to generate the network for
   * @return		null if checks passed, otherwise error message
   */
  protected String check(TabularDataset dataset) {
    if (dataset == null)
      return "No dataset provided!";
    return null;
  }

  /**
   * Generates the network using the supplied dataset.
   *
   * @param dataset	the dataset to generate the network for
   * @return		the network
   */
  protected abstract Block doGenerate(TabularDataset dataset);

  /**
   * Generates the network using the supplied dataset.
   *
   * @param dataset	the dataset to generate the network for
   * @return		the network
   */
  public Block generate(TabularDataset dataset) {
    String	msg;

    msg = check(dataset);
    if (msg != null)
      throw new IllegalStateException(msg);
    return doGenerate(dataset);
  }
}
