./tools/kubectl patch flinkdeployment.flink.apache.org flink -p '{"metadata":{"finalizers":null}}' --type=merge
./tools/kubectl delete flinkdeployment.flink.apache.org flink
./tools/helm uninstall flink-kubernetes-operator
./tools/kubectl delete crd/flinkdeployments.flink.apache.org
./tools/kubectl delete crd/flinksessionjobs.flink.apache.org
./tools/kubectl delete crd/flinkclusters.flinkoperator.k8s.io
