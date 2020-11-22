import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.Properties;

public class Configuration {
    private static final Logger LOGGER = LogManager.getLogger(Configuration.class);
    private static Properties properties;

    public void setConfiguration(String pathToFileWithProperties)  {
        File file = new File(pathToFileWithProperties);
            try  {
                Properties localProperties = new Properties();
                localProperties.load(getClass().getClassLoader().getResourceAsStream(pathToFileWithProperties));
                properties = localProperties;
            } catch (Exception ex) {
                LOGGER.error(ex);
            }
        }


    public String getValue(String key) {
        return properties.getProperty(key);
    }
}
