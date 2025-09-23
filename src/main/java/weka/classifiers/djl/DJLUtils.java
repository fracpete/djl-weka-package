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
 * DJLUtils.java
 * Copyright (C) 2025 University of Waikato, Hamilton, New Zealand
 */

package weka.classifiers.djl;

import ai.djl.engine.Engine;
import ai.djl.pytorch.engine.PtEngineProvider;
import ai.djl.util.ClassLoaderUtils;
import weka.core.WekaPackageClassLoaderManager;

import java.lang.reflect.Method;

/**
 * Utility functions for DJL.
 *
 * @author fracpete (fracpete at waikato dot ac dot nz)
 */
public class DJLUtils {

  private static boolean ClassLoaderUtilsExceptionShown = false;

  private static boolean EngineExceptionShown = false;

  /**
   * Initialize the classloader using the current context.
   *
   * @param context	the object trying to use DJL, like a classifier
   */
  public static void initClassLoader(Object context) {
    ClassLoader wekaClassLoader;
    Method	method;

    // get classloader from context
    wekaClassLoader = WekaPackageClassLoaderManager.getWekaPackageClassLoaderManager().getLoaderForClass(context.getClass().getName());

    // ClassLoaderUtils
    try {
      // ClassLoaderUtils.setContextClassLoader(wekaClassLoader);
      method = ClassLoaderUtils.class.getMethod("setContextClassLoader", ClassLoader.class);
      method.invoke(null, wekaClassLoader);
    }
    catch (NoSuchMethodException ne) {
      if (!ClassLoaderUtilsExceptionShown) {
	ClassLoaderUtilsExceptionShown = true;
	System.err.println("If you are using DJL via Maven, you can ignore the following exception:");
	ne.printStackTrace();
      }
    }
    catch (Exception e) {
      System.err.println("Something unexpected went wrong:");
      e.printStackTrace();
    }

    // Engine
    try {
      // Engine.reInitEngine(wekaClassLoader);
      method = Engine.class.getMethod("reInitEngine", ClassLoader.class);
      method.invoke(null, wekaClassLoader);
    }
    catch (NoSuchMethodException ne) {
      if (!EngineExceptionShown) {
	EngineExceptionShown = true;
	System.err.println("If you are using DJL via Maven, you can ignore the following exception:");
	ne.printStackTrace();
      }
    }
    catch (Exception e) {
      System.err.println("Something unexpected went wrong:");
      e.printStackTrace();
    }
  }

  /**
   * Registers the Pytorch engine.
   */
  public static void registerPytorch() {
    Engine.registerEngine(new PtEngineProvider());
  }
}
