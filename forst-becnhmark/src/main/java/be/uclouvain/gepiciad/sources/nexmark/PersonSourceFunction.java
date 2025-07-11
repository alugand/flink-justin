package be.uclouvain.gepiciad.sources.nexmark;


import org.apache.beam.sdk.nexmark.NexmarkConfiguration;;
import org.apache.beam.sdk.nexmark.model.Person;
import org.apache.beam.sdk.nexmark.sources.generator.GeneratorConfig;
import org.apache.beam.sdk.nexmark.sources.generator.model.PersonGenerator;
import org.apache.flink.streaming.api.functions.source.legacy.RichParallelSourceFunction;
import org.joda.time.DateTime;

import java.util.Random;

/**
 * A ParallelSourceFunction that generates Nexmark Person data
 */
public class PersonSourceFunction extends RichParallelSourceFunction<Person> {

    private volatile boolean running = true;
    private final GeneratorConfig config = new GeneratorConfig(NexmarkConfiguration.DEFAULT, 1, 1000L, 0, 1);
    private long eventsCountSoFar = 0;
    private final int rate;

    public PersonSourceFunction(int srcRate) {
        this.rate = srcRate;
    }

    @Override
    public void run(SourceContext<Person> ctx) throws Exception {
        while (running) {
            long emitStartTime = System.currentTimeMillis();

            for (int i = 0; i < rate; i++) {
                long nextId = nextId();
                Random rnd = new Random(nextId);

                // When, in event time, we should generate the event. Monotonic.
                long eventTimestamp =
                        config.timestampAndInterEventDelayUsForEvent(
                                config.nextEventNumber(eventsCountSoFar)).getKey();

                ctx.collect(PersonGenerator.nextPerson(nextId, rnd, (eventTimestamp), config));
                eventsCountSoFar++;
            }

            // Sleep for the rest of timeslice if needed
            long emitTime = System.currentTimeMillis() - emitStartTime;
            if (emitTime < 1000) {
                Thread.sleep(1000 - emitTime);
            }

        }
    }

    @Override
    public void cancel() {
        running = false;
    }

    private long nextId() {
        return config.firstEventId + config.nextAdjustedEventNumber(eventsCountSoFar);
    }

}