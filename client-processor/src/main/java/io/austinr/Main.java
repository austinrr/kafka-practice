package io.austinr;

import java.util.Properties;

public class Main {

    static ClientReservationProducer producer;

    public static void main(String[] args) {

        Properties props = Utils.loadProperties();
        producer = new ClientReservationProducer(props);
        producer.runProducer();
    }
}