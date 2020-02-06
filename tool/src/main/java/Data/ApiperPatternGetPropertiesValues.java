package Data;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Properties;

public class ApiperPatternGetPropertiesValues{

    InputStream inputStream;

    private Path allowedProjectsFile;
    private Path saveFilePath;
    private Path apiSource;
    private Path jarFiles;

    public ApiperPatternGetPropertiesValues() throws IOException{

        try {
            Properties prop = new Properties();
            String propFileName = "config.properties";

            inputStream = new FileInputStream(propFileName);

            if (inputStream != null) {
                prop.load(inputStream);
            } else {
                throw new FileNotFoundException("property file '" + propFileName + "' not found in the classpath");
            }

            // get the property values
            allowedProjectsFile = Paths.get(prop.getProperty("allowedProjectsFile"));
            saveFilePath = Paths.get(prop.getProperty("saveFilePath"));
            apiSource = Paths.get(prop.getProperty("apiSource"));
            jarFiles = Paths.get(prop.getProperty("jarFiles"));

        } catch (Exception e) {
            System.out.println("Exception: " + e);
        } finally {
            inputStream.close();
        }
    }

    public Path getAllowedProjectsFile() {
        return allowedProjectsFile;
    }

    public Path getSaveFilePath() {
        return saveFilePath;
    }

    public Path getApiSource() {
        return apiSource;
    }

    public Path getJarFiles() {
        return jarFiles;
    }
}
