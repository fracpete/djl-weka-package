# DJL

The following instructions are for creating custom DJL API jar (`v0.34.0`)
for the Weka DJL package (version `2025.12.5`), allowing Weka to set a custom 
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

* current repo with these changes:

  https://github.com/fracpete/djl


## Build DJL and Weka package

* enable Java 11

* switch to **djl-weka-package** project

* gather all Maven dependencies in `jars` directory of package:

  ```bash
  mvn clean generate-resources
  ```

* remove old Maven DJL artifacts from Maven repository:

  ```bash
  rm -R ~/.m2/repository/ai/djl/api/0.34.0-SNAPSHOT
  ```

* switch to **DJL** project (top-level dir)

* build DJL fork:

  ```bash
  ./gradlew clean
  ./gradlew build -x test
  ```
  
* publish DJL API artifacts locally:

  ```bash
  ./gradlew publish
  ```

* switch back to **djl-weka-package** project

* copy `api-0.34.0-XYZ.jar` into `jars` directory, replacing `api-0.34.0.jar`:

  ```bash
  rm jars/api-0.34.0.jar
  cp ~/.m2/repository/ai/djl/api/0.34.0-SNAPSHOT/api-0.34.0-*[0-9].jar jars
  ```

* build Weka package:
  
  ```bash
  ant -f build_package.xml -Dpackage=djl-2025.12.5 clean make_package
  ```


## Use Weka package

The following commands are to be issued from within the **djl-weka-package** project:

* install Weka package:

  ```bash
  java -cp ./lib/weka.jar weka.core.WekaPackageManager -uninstall-package djl \
    && java -cp ./lib/weka.jar weka.core.WekaPackageManager -install-package ./dist/djl-2025.12.5.zip 
  ```
  
* launch Explorer with a dataset:

  ```bash
  java -Dorg.slf4j.simpleLogger.defaultLogLevel=DEBUG \
    -cp ./lib/weka.jar \
    weka.gui.explorer.Explorer ./data/bolts.arff
  ```
