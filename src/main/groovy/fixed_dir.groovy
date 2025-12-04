import weka.classifiers.djl.outputdirgenerator.OutputDirGenerator
import weka.core.Option
import weka.core.OptionHandler
import weka.core.Utils

public class FixedDirGenerator implements OutputDirGenerator, OptionHandler {

    /** the output dir to use. */
    protected File m_OutputDir = new File(".")

    /**
     * Returns an enumeration of all the available options..
     *
     * @return an enumeration of all available options.
     */
    @Override
    Enumeration<Option> listOptions() {
        Vector<Option> 	result

        result = new Vector<>()
        result.add(new Option(
                "\tThe directory to store the network in.\n"
                        + "\t(default: .)",
                "output-dir", 1, "-output-dir <dir>"))

        return new Vector<Option>().elements()
    }

    /**
     * Sets the OptionHandler's options using the given list. All options
     * will be set (or reset) during this call (i.e. incremental setting
     * of options is not possible).
     *
     * @param options the list of options as an array of strings
     * @throws Exception if an option is not supported
     */
    @Override
    void setOptions(String[] options) throws Exception {
        String	tmpStr

        tmpStr = Utils.getOption("output-dir", options)
        if (tmpStr.isEmpty())
            setOutputDir(new File("."))
        else
            setOutputDir(new File(tmpStr))
    }

    /**
     * Gets the current option settings for the OptionHandler.
     *
     * @return the array of current option settings as an array of strings
     */
    @Override
    public String[] getOptions() {
        List<String> result

        result = new ArrayList<String>()

        result.add("-output-dir")
        result.add(getOutputDir().toString())

        return result.toArray(new String[0])
    }

    /**
     * Sets the output dir.
     *
     * @param value 	the dir
     */
    public void setOutputDir(File value) {
        m_OutputDir = value
    }

    /**
     * Gets the output dir.
     *
     * @return 		the dir
     */
    public File getOutputDir() {
        return m_OutputDir
    }

    /**
     * Returns the tip text for this property.
     *
     * @return		tip text for this property suitable for
     * 			displaying in the explorer/experimenter gui
     */
    public String outputDirTipText() {
        return "The output directory to use."
    }

    /**
     * Generates the output directory.
     *
     * @return the directory
     */
    @Override
    public File generate() {
        return m_OutputDir
    }
}
