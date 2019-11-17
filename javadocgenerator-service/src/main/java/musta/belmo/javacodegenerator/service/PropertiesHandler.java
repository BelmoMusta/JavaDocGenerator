package musta.belmo.javacodegenerator.service;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Properties;

/**
 * TODO: Complete the description of this class
 *
 * @author default author
 * @since 0.0.0.SNAPSHOT
 * @version 0.0.0
 */
public class PropertiesHandler extends AbstractJavaDocService {

    /**
     * The {@link #properties} attribute.
     */
    static Properties properties;

    /**
     * The {@link #propertiesPath} attribute.
     */
    static String propertiesPath;

    /**
     * The {@link #properties} attribute.
     *
     * @param propertiesPath_ {@link String}
     */
    public static void loadProperties(String propertiesPath_) {
        URL resource = null;
        if (propertiesPath == null) {
            resource = JavaDocGenerator.class.getClassLoader().getResource(APPLICATION_PROPERTIES);
        } else {
            File file = new File(propertiesPath);
            try {
                resource = file.toURI().toURL();
            } catch (MalformedURLException e) {
            }
            if (resource == null) {
                resource = JavaDocGenerator.class.getClassLoader().getResource(APPLICATION_PROPERTIES);
            }
        }
        if (resource != null) {
            properties = new Properties();
            try {
                InputStream resourceAsStream = resource.openStream();
                properties.load(resourceAsStream);
            } catch (IOException e) {
            }
        }
        propertiesPath = propertiesPath_;
        if (resource != null) {
            propertiesPath = resource.getPath();
        }
    }

    /**
     * @return Attribut {@link #propertiesPath}
     */
    public static String getPropertiesPath() {
        return propertiesPath;
    }

    /**
     * @param propertiesPath Value to be assigned to the {@link #propertiesPath} attribute.
     */
    public static void setPropertiesPath(String propertiesPath) {
        PropertiesHandler.propertiesPath = propertiesPath;
    }

    /**
     * Read from properties
     *
     * @param key {@link String}
     * @return String
     */
    public static String readFromProperties(String key) {
        return properties.getProperty(key);
    }
}
