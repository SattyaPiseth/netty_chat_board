package config;

import java.io.IOException;
import java.io.InputStream;
import java.util.Optional;
import java.util.Properties;

/**
 * @author Sattya
 * create at 2/13/2025 10:45 PM
 */
public class PropertiesConfig {
    private static final Properties properties = new Properties();
    private PropertiesConfig(){
        throw new IllegalStateException();
    }
    static {
        try(InputStream inputStream = Optional.ofNullable(PropertiesConfig.class.getClassLoader().getResourceAsStream("application.properties"))
                .orElseThrow(()->new RuntimeException("Unable to find application.properties"))) {
            properties.load(inputStream);
        } catch (IOException exception){
            throw new IllegalArgumentException("Error loading properties",exception);
        }
    }

    public static String get(String key){
        return Optional.ofNullable(properties.getProperty(key)).orElseThrow(()->new IllegalArgumentException("key not found: "+key));
    }

    public static int getInt(String key){
        return Optional.ofNullable(properties.getProperty(key))
                .map(Integer::parseInt)
                .orElseThrow(()->new IllegalArgumentException("Invalid or missing integer key: "+key));
    }
}
