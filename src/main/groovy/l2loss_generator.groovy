import ai.djl.training.DefaultTrainingConfig
import ai.djl.training.TrainingConfig
import ai.djl.training.listener.TrainingListener
import ai.djl.training.loss.Loss
import ai.djl.training.optimizer.Optimizer
import ai.djl.training.tracker.Tracker
import weka.classifiers.djl.trainingconfiggenerator.TrainingConfigGenerator

public class TabNetRegressionLossConfigGenerator implements TrainingConfigGenerator {

    /**
     * Generates the training configuration to use.
     *
     * @return		the configuration
     */
    public TrainingConfig generate() {
        return new DefaultTrainingConfig(Loss.l2Loss()) // L2 Loss (MSE) for regression
                .optOptimizer(Optimizer.sgd().setLearningRateTracker(Tracker.fixed(0.01f)).build())
                .addTrainingListeners(TrainingListener.Defaults.logging())
    }
}