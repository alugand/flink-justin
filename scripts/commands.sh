kubectl port-forward svc/flink-rest 8081
kubectl delete flinkdeployment/flink
kubectl apply -f job.yaml
kubectl logs -f deploy/flink
mc admin info myminio
export PATH=$HOME/tools:$PATH

kubectl port-forward -n manager svc/prom-grafana 3000:80kubectl port-forward svc/flink-rest 8081

#credentials : admin prom-operator

#kafka : 
kubectl exec -it kafka-0 -n kafka -- /bin/bash
kafka-topics.sh --bootstrap-server kafka-service.kafka.svc.cluster.local:9092 --delete --topic event-demo
kafka-topics.sh --bootstrap-server kafka-service.kafka.svc.cluster.local:9092 --list
kafka-topics.sh --bootstrap-server kafka-service.kafka.svc.cluster.local:9092 --create --topic event-demo --partitions 4 --replication-factor 1
kafka-topics.sh --bootstrap-server kafka-service.kafka.svc.cluster.local:9092 --describe --topic event-demo
kafka-configs.sh --bootstrap-server localhost:9092 --alter --topic event-demo --add-config retention.ms=1000
kafka-configs.sh --bootstrap-server localhost:9092 --alter --topic event-demo --delete-config retention.ms

kubectl port-forward svc/myminio-console 9090 -n minio-tenant

#minio_cluster_usage_total_bytes{}