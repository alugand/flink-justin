package be.uclouvain.gepiciad.sources;

import com.fasterxml.jackson.databind.ObjectMapper;

import org.apache.flink.api.common.typeinfo.TypeInformation;
import org.apache.flink.connector.kafka.source.reader.deserializer.KafkaRecordDeserializationSchema;
import org.apache.flink.util.Collector;
import org.apache.kafka.clients.consumer.ConsumerRecord;

import java.io.IOException;

public class EventDeserializer implements KafkaRecordDeserializationSchema<Event> {
    private static final long serialVersionUID = 1L;
    private transient ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public void deserialize(ConsumerRecord<byte[], byte[]> record, Collector<Event> out) throws IOException {
        if (objectMapper == null) {
            objectMapper = new ObjectMapper();
        }
        if (record.value() != null) {
            Event event = objectMapper.readValue(record.value(), Event.class);
            out.collect(event);
        }
    }

    @Override
    public TypeInformation<Event> getProducedType() {
        return TypeInformation.of(Event.class);
    }
}