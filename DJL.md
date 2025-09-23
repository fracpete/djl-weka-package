# DJL

The following instructions are for creating custom DJL API jar (`v0.34.0`)
for the Weka DJL package (version `2025.6.30`), allowing Weka to set a custom 
class loader context.

## Prepare DJL fork

* fork the djl repository (all, not just master branch):

  https://github.com/deepjavalibrary/djl

* clone the forked repo

  ```bash
  git clone git@github.com:fracpete/djl.git
  ```

* turn the release tag into a branch for development

  ```bash
  git checkout -b v0.34.0 tags/v0.34.0
  ```

## Update DJL code base
  
* edit `api/build.gradle.kts`

    * add plugin
  
      ```
      `maven-publish`
      ```
      
    * append

      ```
      publishing {
        repositories {
          mavenLocal()
        }
      }
      ```

* update the following classes to allow using a custom classloader

  ```
  api/src/main/java/ai/djl/engine/Engine.java
  api/src/main/java/ai/djl/util/ClassLoaderUtils.java
  ```

## Build DJL and Weka package

* build DJL fork (top-level dir)

  ```bash
  ./gradlew clean
  ./gradlew build -x test
  ```

* gather all Maven dependencies in `jars` directory of package:

  ```bash
  mvn clean generate-resources
  ```

* remove old Maven artifacts

  ```bash
  rm -R ~/.m2/repository/ai/djl/api/0.34.0-SNAPSHOT
  ```

* publish DJL API artifacts locally (top-level dir)

  ```bash
  ./gradlew publish
  ```

* go to Maven repository

  ```
  ~/.m2/repository/ai/djl/api/0.34.0-SNAPSHOT/
  ```

* copy `api-0.34.0-XYZ.jar` into `jars` directory

* build Weka package
  
  ```bash
  ant -f build_package.xml -Dpackage=djl-2025.6.30 clean make_package
  ```

## Use Weka package

* install Weka package

  ```bash
  java -cp ./lib/weka.jar weka.core.WekaPackageManager -uninstall-package djl \
    && java -cp ./lib/weka.jar weka.core.WekaPackageManager -install-package ./dist/djl-2025.6.30.zip 
  ```

* launch Explorer with a dataset

  ```bash
  java -Dorg.slf4j.simpleLogger.defaultLogLevel=DEBUG \
    -cp ./lib/weka.jar \
    weka.gui.explorer.Explorer ./data/bolts.arff
  ```
