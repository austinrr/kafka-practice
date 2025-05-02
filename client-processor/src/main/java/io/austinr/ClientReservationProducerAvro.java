package io.austinr;

import java.time.Duration;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.CountDownLatch;

public class ClientReservationProducerAvro {
    public static final String APPLICATION_ID = "client-reservation-processor";
    public static final String INPUT_TOPIC = "client-reservation-request-topic";

    private static final Properties CONFIG = new Properties();

    public ClientReservationProducerAvro(Properties props) {
        CONFIG.putAll(props);
        CONFIG.put(StreamsConfig.APPLICATION_ID_CONFIG, APPLICATION_ID);
    }

    protected SpecificAvroSerde<Reservation> reservationAvroSerde() {
        SpecificAvroSerde<Reservation> reservationSerde = new SpecificAvroSerde<>();

        Map<String, String> serdeConfig = new HashMap<>();
        serdeConfig.put("schema.registry.url", CONFIG.getProperty("schema.registry.url"));
        reservationSerde.configure(serdeConfig, false);

        return reservationSerde;
    }

    protected Topology buildTopology(final SpecificAvroSerde<Reservation> reservationAvroSerde) {

//        props.put(StreamsConfig.STATESTORE_CACHE_MAX_BYTES_CONFIG, 0);

        final StreamsBuilder builder = new StreamsBuilder();

        builder.stream(INPUT_TOPIC, Consumed.with(Serdes.String(), reservationAvroSerde))
                .peek((key, res) -> System.out.println("Key: " + key + " Reservation by: " + res.getRequestorName()));

        return builder.build();
    }

    public void start() {
        Topology topology = buildTopology(reservationAvroSerde());

        try (KafkaStreams kafkaStreams = new KafkaStreams(topology, CONFIG)) {
            CountDownLatch countDownLatch = new CountDownLatch(1);
            Runtime.getRuntime().addShutdownHook(new Thread(() -> {
                kafkaStreams.close(Duration.ofSeconds(5));
                countDownLatch.countDown();
            }));
            // For local running only; don't do this in production as it wipes out all local state
            kafkaStreams.cleanUp();
            kafkaStreams.start();
            countDownLatch.await();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}
