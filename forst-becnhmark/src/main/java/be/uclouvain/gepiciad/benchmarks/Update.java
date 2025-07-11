package be.uclouvain.gepiciad.benchmarks;

import be.uclouvain.gepiciad.sources.Event;
import be.uclouvain.gepiciad.sources.EventDeserializer;
import org.apache.flink.api.common.eventtime.WatermarkStrategy;
import org.apache.flink.api.common.functions.OpenContext;
import org.apache.flink.api.common.functions.RichFlatMapFunction;
import org.apache.flink.api.common.state.v2.ValueState;
import org.apache.flink.api.common.state.v2.ValueStateDescriptor;
import org.apache.flink.api.common.typeutils.base.StringSerializer;
import org.apache.flink.connector.kafka.source.KafkaSource;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.api.functions.sink.v2.DiscardingSink;
import org.apache.flink.util.Collector;
import org.apache.flink.util.ParameterTool;

public class Update {

    public static void main(String[] args) throws Exception{
        final ParameterTool pt = ParameterTool.fromArgs(args);

        final StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();
        String password = pt.get("kafka-pwd");
        String jaasCfg = String.format(
                "org.apache.kafka.common.security.scram.ScramLoginModule required username=\"user1\" password=\"%s\";",
                password
        );
        KafkaSource<Event> source = KafkaSource.<Event>builder()
                .setBootstrapServers("my-release-kafka.kafka.svc.cluster.local:9092")
                .setTopics("event-demo")
                .setGroupId("my-consumer-flink")
                //.setStartingOffsets(OffsetsInitializer.latest())
                .setDeserializer(new EventDeserializer())
                .setProperty("security.protocol", "SASL_PLAINTEXT")
                .setProperty("sasl.mechanism", "SCRAM-SHA-256")
                .setProperty("sasl.jaas.config", jaasCfg)
                .build();

        env.fromSource(source, WatermarkStrategy.noWatermarks(),"KafkaSource")
                .keyBy(Event::getKey)
                .enableAsyncState()
                .flatMap(new Mapper())
                .sinkTo(new DiscardingSink<>());

        env.execute("Simple Benchmark");
    }

    public static class Mapper extends RichFlatMapFunction<Event, String> {

        private static final long serialVersionUID = 1L;

        private transient ValueState<String> valueState;

        @Override
        public void open(OpenContext openContext) throws Exception {
            super.open(openContext);
            int index = getRuntimeContext().getTaskInfo().getIndexOfThisSubtask();
            valueState =
                    getRuntimeContext()
                            .getState(
                                    new ValueStateDescriptor<>(
                                            "valueState" + index, StringSerializer.INSTANCE));
        }

        @Override
        public void flatMap(Event event, Collector<String> out) throws Exception {
            valueState.asyncValue().thenAccept(currentValue -> {
                if (currentValue != null) {
                    out.collect(currentValue);
                }
                valueState.asyncUpdate(event.getPayload());
            });
        }


    }
}