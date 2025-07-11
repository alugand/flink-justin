package be.uclouvain.gepiciad.sources;

import org.apache.flink.api.common.eventtime.WatermarkStrategy;
import org.apache.flink.connector.kafka.sink.KafkaRecordSerializationSchema;
import org.apache.flink.connector.kafka.sink.KafkaSink;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.util.ParameterTool;
import org.apache.flink.formats.json.JsonSerializationSchema;
import org.apache.flink.connector.datagen.source.DataGeneratorSource;
import org.apache.flink.connector.datagen.source.GeneratorFunction;
import java.util.Random;
import org.apache.flink.api.common.typeinfo.TypeInformation;
import org.apache.flink.api.connector.source.util.ratelimit.RateLimiterStrategy;
import java.time.Instant;
import org.apache.flink.util.StringUtils;

public class KafkaGenerator {
    public static void main(String[] args) throws Exception{
        final ParameterTool pt = ParameterTool.fromArgs(args);
        Random random = new Random();

        final StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();
        
        int srcRate = Integer.parseInt(pt.get("src-rate","10000"));
        int payloadLength = Integer.parseInt(pt.get("payload-length","1000"));


        GeneratorFunction<Long, Event> generatorFunction = i -> {
            int key = random.nextInt(100000);
            long eventTime = Instant.now().toEpochMilli();
            i++;
            String payload = StringUtils.getRandomString(
                    random, payloadLength, payloadLength, 'A', 'z');
            return new Event(key, eventTime, i, payload);
        };

        DataGeneratorSource<Event> dataGeneratorSource =
                new DataGeneratorSource<>(
                        generatorFunction,
                        Long.MAX_VALUE,
                        RateLimiterStrategy.perSecond(srcRate),
                        TypeInformation.of(Event.class)
                );

        KafkaSink<Event> kafkaSink = KafkaSink.<Event>builder()
                .setProperty("batch.size", "262144")
                .setProperty("linger.ms", "50")
                .setBootstrapServers("kafka-service.kafka.svc.cluster.local:9092")
                .setRecordSerializer(KafkaRecordSerializationSchema.builder()
                        .setValueSerializationSchema(new JsonSerializationSchema<Event>())
                        .setTopic("event-demo")
                        .build())
                .build();

        env.fromSource(dataGeneratorSource, WatermarkStrategy.noWatermarks(),"event-stream")
                .sinkTo(kafkaSink);

        env.execute("Kafka Event Generator Job");
    }
}
