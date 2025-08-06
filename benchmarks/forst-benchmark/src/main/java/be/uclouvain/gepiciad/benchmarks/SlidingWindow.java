package be.uclouvain.gepiciad.benchmarks;

import be.uclouvain.gepiciad.sources.Event;
import be.uclouvain.gepiciad.sources.EventDeserializer;
import org.apache.flink.api.common.eventtime.WatermarkStrategy;
import org.apache.flink.api.common.functions.AggregateFunction;
import org.apache.flink.connector.kafka.source.KafkaSource;
import org.apache.flink.connector.kafka.source.enumerator.initializer.OffsetsInitializer;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.api.windowing.assigners.SlidingProcessingTimeWindows;
import java.time.Duration;

public class SlidingWindow {

    public static class HighestBidAggregator implements AggregateFunction<Event, Long, Long> {
        @Override
        public Long createAccumulator() {
            return -1L;
        }

        @Override
        public Long add(Event event, Long accumulator) {
            return Math.max(accumulator, event.getBid());
        }

        @Override
        public Long getResult(Long accumulator) {
            return accumulator;
        }

        @Override
        public Long merge(Long a, Long b) {
            return Math.max(a, b);
        }
    }

    public static void main(String[] args) throws Exception {
        final StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();

        KafkaSource<Event> kafkaSource = KafkaSource.<Event>builder()
                .setBootstrapServers("kafka-service.kafka.svc.cluster.local:9092")
                .setTopics("event-demo")
                .setGroupId("sliding-window-job-group")
                .setStartingOffsets(OffsetsInitializer.latest())
                .setDeserializer(new EventDeserializer())
                .build();

        env.fromSource(kafkaSource, WatermarkStrategy.noWatermarks(), "Kafka Source")
                .keyBy(Event::getKey)
                .enableAsyncState()
                .window(SlidingProcessingTimeWindows.of(Duration.ofSeconds(30), Duration.ofSeconds(10)))
                .aggregate(new HighestBidAggregator())
                .print();

        env.execute("ighest Bid Job");
    }
}