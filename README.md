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

* `weka.classifiers.djl.networkgenerator.GroovyGenerator`
* `weka.classifiers.djl.networkgenerator.TabNetGenerator`

### GroovyGenerator

The following example generators using Groovy are available:

* [simple_regression.groovy](src/main/groovy/simple_regression.groovy)
* [tabnet.groovy](src/main/groovy/tabnet.groovy)
* [tabnet_lowlevel.groovy](src/main/groovy/tabnet_lowlevel.groovy) (Groovyfied [TabNet.java](https://github.com/deepjavalibrary/djl/blob/76f0f2646a722f8f337b02a021fe32a95a8b6a7f/model-zoo/src/main/java/ai/djl/basicmodelzoo/tabular/TabNet.java#L317))


## ID generators

The ID generators are used to generate a prefix for the `.params` files
that the training process generates.

* `weka.classifiers.djl.idgenerator.FixedID`
* `weka.classifiers.djl.idgenerator.GroovyGenerator`

### GroovyGenerator

The following example generators using Groovy are available:

* [fixed_id.groovy](src/main/groovy/fixed_id.groovy)


## Output generators

Since the underlying network cannot be serialized within the Weka model itself,
the PyTorch parameters need to get stored in a directory. The output generators
are used for generating that output directory:

* `weka.classifiers.djl.outputdirgenerator.FixedDir`
* `weka.classifiers.djl.outputdirgenerator.GroovyGenerator`

### GroovyGenerator

The following example generators using Groovy are available:

* [fixed_dir.groovy](src/main/groovy/fixed_dir.groovy)


## Training config generators

The training config generators determine how the training works.

* `weka.classifiers.djl.trainingconfiggenerator.GroovyGenerator`
* `weka.classifiers.djl.trainingconfiggenerator.TabNetRegressionLossGenerator`

### GroovyGenerator

The following example generators using Groovy are available:

* [l2loss_generator.groovy](src/main/groovy/l2loss_generator.groovy)
* [tabnet_regressionloss_generator.groovy](src/main/groovy/tabnet_regressionloss_generator.groovy)


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
