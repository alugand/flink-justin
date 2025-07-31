package be.uclouvain.gepiciad.benchmarks;

import be.uclouvain.gepiciad.sources.Event;
import be.uclouvain.gepiciad.sources.EventDeserializer;
import org.apache.flink.api.common.eventtime.WatermarkStrategy;
import org.apache.flink.api.common.functions.OpenContext;
import org.apache.flink.api.common.functions.RichFlatMapFunction;
import org.apache.flink.api.common.state.v2.MapStateDescriptor;
import org.apache.flink.api.common.state.v2.MapState;
import org.apache.flink.api.common.typeinfo.Types;
import org.apache.flink.connector.kafka.source.KafkaSource;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.api.functions.sink.v2.DiscardingSink;
import org.apache.flink.util.Collector;
import org.apache.flink.util.ParameterTool;

public class LargeState {

    public static void main(String[] args) throws Exception{
        final ParameterTool pt = ParameterTool.fromArgs(args);

        final StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();

        KafkaSource<Event> source = KafkaSource.<Event>builder()
                .setBootstrapServers("kafka-service.kafka.svc.cluster.local:9092")
                .setTopics("event-demo")
                .setGroupId("my-consumer-flink")
                //.setStartingOffsets(OffsetsInitializer.latest())
                .setDeserializer(new EventDeserializer())
                .build();

        env.fromSource(source, WatermarkStrategy.noWatermarks(),"KafkaSource")
                .keyBy(Event::getKey)
                .enableAsyncState()
                .flatMap(new StateAccumulator())
                .sinkTo(new DiscardingSink<>());

        env.execute("Simple Benchmark");
    }

    public static class StateAccumulator extends RichFlatMapFunction<Event, String> {

        private static final long serialVersionUID = 1L;

        private transient MapState<Long, String> stateMap;

        @Override
        public void open(OpenContext openContext) throws Exception {
            super.open(openContext);
            int index = getRuntimeContext().getTaskInfo().getIndexOfThisSubtask();
            MapStateDescriptor<Long, String> descriptor =
                    new MapStateDescriptor<>("accumulated-state", Types.LONG, Types.STRING);
            stateMap = getRuntimeContext().getMapState(descriptor);
        }

        @Override
        public void flatMap(Event event, Collector<String> out) throws Exception {
            stateMap.asyncPut(event.getEventTime(), event.getPayload());
        }


    }
}