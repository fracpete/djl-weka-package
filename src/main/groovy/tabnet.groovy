import ai.djl.basicdataset.tabular.TabularDataset
import ai.djl.nn.Block
import ai.djl.zero.Performance
import weka.classifiers.djl.networkgenerator.NetworkGenerator
import weka.classifiers.djl.networkgenerator.TabularRegressionGenerator


public class TabNetGenerator implements NetworkGenerator {

    /**
     * Generates the network using the supplied dataset.
     *
     * @param dataset	the dataset to generate the network for
     * @return		the network
     */
    public Block generate(TabularDataset dataset) {
        TabularRegressionGenerator generator = new TabularRegressionGenerator()
        generator.setPerformance(Performance.FAST)

        return generator.generate(dataset)
    }
}