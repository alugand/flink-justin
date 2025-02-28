# JUSTIN

## Compiling Flink

### Requirements
1. Java 11
2. Maven

```bash
cd flink
mvn  package -T 8 -DskipTests -Dspotless.check.skip=true -Drat.skip=true -Dcheckstyle.skip
```

The generated binaries are located in the dir `flink/flink-dist/target/...`.

### Building the image

```bash
docker build . -t ${DOCKER_ID}/flink:justin
docker push ${DOCKER_ID}/flink:justin
```

## Building the Flink Kubernetes Operator

```bash
docker build . -t ${DOCKER_ID}/flink-operator:justin
docker push ${DOCKER_ID}/flink-operator:justin
```

## Deploying the Flink Kubernetes Operator

```bash
helm install flink-kubernetes-operator flink-operator-repo/flink-kubernetes-operator --set image.repository= ${DOCKER_ID}/flink-operator --set image.tag=justin -f values.yaml
```