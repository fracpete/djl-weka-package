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
 * UniqueIDs.java
 * Copyright (C) 2017-2019 University of Waikato, Hamilton, NZ
 */

package weka.core;

import java.lang.management.ManagementFactory;

/**
 * Class for creating unique IDs.
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 */
public class UniqueIDs {

  /** the counter. */
  protected static long m_Counter;
  static {
    m_Counter = 0;
  }

  /** the named counter. */
  protected static NamedCounter m_NamedCounter;
  static {
    m_NamedCounter = new NamedCounter();
  }

  /**
   * Returns the PID of the virtual machine. Caution: it's a hack and can break
   * anytime. Do NOT rely on it. Based on <a href=
   * "http://blog.igorminar.com/2007/03/how-java-application-can-discover-its.html"
   * target="_blank">this blog entry</a>.
   *
   * @return the PID or -1 in case of an error
   */
  public static long getVirtualMachinePID() {
    long result;
    String name;

    name = ManagementFactory.getRuntimeMXBean().getName();

    try {
      result = Long.parseLong(name.replaceAll("@.*", ""));
    }
    catch (Exception e) {
      result = -1;
    }

    return result;
  }

  /**
   * Creates a new unique ID, using current nanotime, virtual machine PID,
   * counter (within JVM session).
   *
   * @return		the generated ID
   */
  public static synchronized String next() {
    return Long.toHexString(System.nanoTime())
	+ "-" + Long.toHexString(getVirtualMachinePID())
	+ "-" + Long.toHexString(nextLong());
  }

  /**
   * Returns the next ID from the counter.
   *
   * @return		the next ID
   */
  public static synchronized long nextLong() {
    m_Counter++;
    return m_Counter;
  }

  /**
   * Returns the next ID for the given name.
   *
   * @param name	the named ID counter
   * @return		the next ID
   */
  public static synchronized int nextInt(String name) {
    return m_NamedCounter.next(name);
  }
}
