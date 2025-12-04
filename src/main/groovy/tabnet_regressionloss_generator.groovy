import ai.djl.training.DefaultTrainingConfig
import ai.djl.training.TrainingConfig
import ai.djl.training.listener.TrainingListener
import ai.djl.training.loss.TabNetRegressionLoss
import weka.classifiers.djl.trainingconfiggenerator.TrainingConfigGenerator

public class TabNetRegressionLossConfigGenerator implements TrainingConfigGenerator {

    /**
     * Generates the training configuration to use.
     *
     * @return		the configuration
     */
    public TrainingConfig generate() {
        return new DefaultTrainingConfig(
                new TabNetRegressionLoss())
                .addTrainingListeners(TrainingListener.Defaults.basic())
    }
}