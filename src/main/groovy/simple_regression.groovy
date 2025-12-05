import ai.djl.basicdataset.tabular.TabularDataset
import ai.djl.nn.Activation
import ai.djl.nn.Block
import ai.djl.nn.SequentialBlock
import ai.djl.nn.core.Linear
import weka.classifiers.djl.networkgenerator.NetworkGenerator


public class SimpleRegressionGenerator implements NetworkGenerator {

    /**
     * Generates the network using the supplied dataset.
     *
     * @param dataset	the dataset to generate the network for
     * @return		the network
     */
    public Block generate(TabularDataset dataset) {
        SequentialBlock block = new SequentialBlock()
        // Hidden layer
        block.add(Linear.builder().setUnits(10).build())
        // Activation
        block.add(Activation.reluBlock())
        // Output layer (1 number)
        block.add(Linear.builder().setUnits(1).build())
        return block
    }
}