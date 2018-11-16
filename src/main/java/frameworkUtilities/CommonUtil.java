package frameworkUtilities;


import org.apache.commons.configuration2.Configuration;
import org.apache.commons.configuration2.FileBasedConfiguration;
import org.apache.commons.configuration2.PropertiesConfiguration;
import org.apache.commons.configuration2.builder.FileBasedConfigurationBuilder;
import org.apache.commons.configuration2.builder.fluent.Parameters;
import org.apache.commons.configuration2.ex.ConfigurationException;

public class CommonUtil {

    public static Configuration loadPropertiesFile(String filename) {
        Parameters params = new Parameters();
        FileBasedConfigurationBuilder<FileBasedConfiguration> builder =
                new FileBasedConfigurationBuilder<FileBasedConfiguration>(PropertiesConfiguration.class)
                        .configure(params.properties()
                                .setFileName(filename + ".properties"));
        try {
            Configuration config = builder.getConfiguration();
            return config;
        } catch (ConfigurationException cex) {
            // loading of the configuration file failed
            Log.info(filename + " is not loaded" + cex.getMessage());
            return null;
        }

    }
}

