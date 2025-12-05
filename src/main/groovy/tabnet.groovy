import ai.djl.basicdataset.tabular.TabularDataset
import ai.djl.nn.Block
import ai.djl.zero.Performance
import weka.classifiers.djl.networkgenerator.NetworkGenerator


public class TabNetGenerator implements NetworkGenerator {

    /**
     * Generates the network using the supplied dataset.
     *
     * @param dataset	the dataset to generate the network for
     * @return		the network
     */
    public Block generate(TabularDataset dataset) {
        weka.classifiers.djl.networkgenerator.TabNetGenerator generator = new weka.classifiers.djl.networkgenerator.TabNetGenerator()
        generator.setPerformance(Performance.FAST)
        return generator.generate(dataset)
    }
}