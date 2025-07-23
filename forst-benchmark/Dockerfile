FROM flink:2.0.0

RUN mkdir /opt/flink/plugins/flink-s3-fs-hadoop; \
  cp /opt/flink/opt/flink-s3-fs-hadoop-2.0.0.jar /opt/flink/plugins/flink-s3-fs-hadoop/

COPY target/flink-forst-benchmark-1.0-SNAPSHOT.jar /opt/flink/usrlib/flink-forst.jar
COPY target/Query1.jar /opt/flink/usrlib/Query1.jar
COPY target/Query3.jar /opt/flink/usrlib/Query3.jar
COPY target/Read.jar /opt/flink/usrlib/Read.jar
COPY target/Update.jar /opt/flink/usrlib/Update.jar
COPY target/WriteOnly.jar /opt/flink/usrlib/WriteOnly.jar
COPY target/Generator.jar /opt/flink/usrlib/Generator.jar
