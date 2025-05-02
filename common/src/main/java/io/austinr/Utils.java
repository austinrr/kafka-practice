package io.austinr;

import org.apache.kafka.clients.admin.NewTopic;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Properties;

public class Utils {

    // overwrite default constructor
    private Utils() {}

    public static final String DEFAULT_PROPERTIES_LOCATION = "src/main/resources/confluent.properties";

    public static Properties loadProperties(final String path) {
        Properties properties = new Properties();
        try (InputStreamReader inputStreamReader = new InputStreamReader(new FileInputStream(path))) {
            properties.load(inputStreamReader);
            return properties;
        } catch (IOException e) {
            throw new RuntimeException("Problem loading properties file for example", e);
        }
    }

    public static Properties loadProperties() {
        return loadProperties(DEFAULT_PROPERTIES_LOCATION);
    }

    public static NewTopic createTopic(final String topicName, final int partitions, final short replicas) {
        return new NewTopic(topicName, partitions, replicas);
    }

    public static NewTopic createTopic(final String topicName){
        return new NewTopic(topicName, 6, (short) 3);
    }
}
