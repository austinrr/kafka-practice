package io.austinr;

import io.austinr.records.ReservationRecord;
import org.apache.kafka.clients.Metadata;
import org.apache.kafka.clients.admin.AdminClient;
import org.apache.kafka.clients.producer.*;
import org.apache.kafka.streams.StreamsConfig;

import java.util.List;
import java.util.Properties;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

public class ClientReservationProducer {

    // public
    public static final String APPLICATION_ID = "client-reservation-producer";
    public static final String OUTPUT_TOPIC = "client-reservation-topic";

    // member
    private static final Properties CONFIG = new Properties();
    private final Producer<String, ReservationRecord> producer;
    private volatile boolean producing = true;

    public ClientReservationProducer(Properties props) {
        CONFIG.putAll(props);
        CONFIG.put(StreamsConfig.APPLICATION_ID_CONFIG, APPLICATION_ID);
        this.producer = new KafkaProducer<>(CONFIG);
    }

    public void runProducer() {

        try (AdminClient adminClient = AdminClient.create(CONFIG)) {
            adminClient.createTopics(List.of(Utils.createTopic(OUTPUT_TOPIC)));
        } catch (Exception e) {
            System.out.println("Error while creating topic " + OUTPUT_TOPIC + "\n" + e);
        }

        while (producing) {
            ReservationRecord reservation = MockClient.makeFakeRecord();
            ProducerRecord<String, ReservationRecord> record = createProducerRecord(reservation);
            sendEvent(record, printMetadata());
        }

        Runtime.getRuntime().addShutdownHook(new Thread(this::shutdown));
    }

    private void shutdown() {
        System.out.println("Shutting down...");
        producing = false;
        producer.close();
    }

    private ProducerRecord<String, ReservationRecord> createProducerRecord(ReservationRecord reservationRecord) {
        return new ProducerRecord<>(OUTPUT_TOPIC, reservationRecord.lot(), reservationRecord);
    }

    @SuppressWarnings("UnusedReturnValue")
    private Future<RecordMetadata> sendEvent(ProducerRecord<String, ReservationRecord> record, Callback callback) {
        return producer.send(record, callback);
    }

    private Callback printMetadata() {
        return (RecordMetadata metadata, Exception exception) -> {
            if (exception == null) {
                System.out.printf(
                        "Record written to topic: '%s'%n\tPartition: %s%n\tOffset: %s%n\tTimestamp: %s%n",
                        metadata.topic(), metadata.partition(), metadata.offset(), metadata.timestamp());
            } else {
                System.err.printf("Error reading RecordMetadata. %s%n", exception);
            }
        };
    }
}