import weka.classifiers.djl.idgenerator.IDGenerator
import weka.core.Option
import weka.core.OptionHandler
import weka.core.Utils

public class FixedIDGenerator implements IDGenerator, OptionHandler {

    /** the ID to use. */
    protected String m_ID = "djl"

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
                "\tThe ID to use.\n"
                        + "\t(default: djl)",
                "id", 1, "-id <ID>"))

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

        tmpStr = Utils.getOption("id", options)
        if (tmpStr.isEmpty())
            setID("djl")
        else
            setID(tmpStr)
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

        result.add("-id")
        result.add(getID())

        return result.toArray(new String[0])
    }

    /**
     * Sets the ID/prefix for saving the model.
     *
     * @param value 	the ID/prefix
     */
    public void setID(String value) {
        m_ID = value
    }

    /**
     * Gets the ID/prefix for saving the model.
     *
     * @return 		the ID/prefix
     */
    public String getID() {
        return m_ID
    }

    /**
     * Returns the tip text for this property.
     *
     * @return		tip text for this property suitable for
     * 			displaying in the explorer/experimenter gui
     */
    public String IDTipText() {
        return "The ID to use."
    }

    /**
     * Generates the ID.
     *
     * @return		the ID
     */
    public String generate() {
        return m_ID
    }
}
