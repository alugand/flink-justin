cd ../flink; mvn  package -T 8 -DskipTests -Dspotless.check.skip=true -Drat.skip=true -Dcheckstyle.skip -pl flink-runtime,flink-dist,flink-core,flink-state-backends/flink-statebackend-rocksdb

cd ..; docker build . -t $DOCKER_ID/flink-justin:dais
docker push $DOCKER_ID/flink-justin:dais