# djl-weka-package
Weka package for applying [Deep Learning using Deep Java Library (DJL)](https://djl.ai/).

Uses the [PyTorch engine](https://docs.djl.ai/master/engines/pytorch/pytorch-engine/index.html) 
for the models.


## Classifiers

The following classifiers are available:

* `weka.classifiers.djl.DJLRegressor` - for regression problems


## Network generators

Network generators construct the network that is being learned. Also used 
when deserializing the model, as the `.params` files do not contain the
structure.

* `weka.classifiers.djl.network.GroovyGenerator`
* `weka.classifiers.djl.network.TabularRegressionGenerator`


### GroovyGenerator

The following example generators using Groovy are available:

* [tabnet.groovy](src/main/groovy/tabnet.groovy)
* [tabnet_lowlevel.groovy](src/main/groovy/tabnet.groovy) (Groovyfied [TabNet.java](https://github.com/deepjavalibrary/djl/blob/76f0f2646a722f8f337b02a021fe32a95a8b6a7f/model-zoo/src/main/java/ai/djl/basicmodelzoo/tabular/TabNet.java#L317))


## ID generators

The ID generators are used to generate a prefix for the `.params` files
that the training process generates.

* `weka.classifiers.djl.idgenerator.FixedID`


## Output generators

Since the underlying network cannot be serialized within the Weka model itself,
the PyTorch parameters need to get stored in a directory. The output generators
are used for generating that output directory:

* `weka.classifiers.djl.outputdirgenerator.FixedDir`


## Launch via Maven

You can launch Weka and the DJL package directly via Maven as follows:

```bash
mvn exec:java
```

The amount of heap size is controlled via the `MAVEN_OPTS` environment variable.

On Linux/Mac:

```bash
export MAVEN_OPTS=-Xmx2g
```

Or on Windows:

```bash
set MAVEN_OPTS=-Xmx2g
```


## How to use packages

For more information on how to install the package, see:

https://waikato.github.io/weka-wiki/packages/manager/


## Maven

Add the following dependency in your `pom.xml` to include the package:

```xml
    <dependency>
      <groupId>com.github.fracpete</groupId>
      <artifactId>djl-weka-package</artifactId>
      <version>2025.6.30-SNAPSHOT</version>
      <type>jar</type>
      <exclusions>
        <exclusion>
          <groupId>nz.ac.waikato.cms.weka</groupId>
          <artifactId>weka-dev</artifactId>
        </exclusion>
      </exclusions>
    </dependency>
```
