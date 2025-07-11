#!/bin/bash

export PATH=$HOME/tools:$PATH # for grid5k

# give full rights to pod running in default (bad practice, but ok in our experimentation case)
kubectl apply -f ./cluster-role-binding-default.yaml

# ingress (works in 1.21 max)
#kubectl apply -f ../common/nginx-controller.yaml

kubectl apply -f https://github.com/jetstack/cert-manager/releases/download/v1.5.3/cert-manager.yaml
kubectl --namespace cert-manager rollout status deployment/cert-manager-webhook
kubectl apply -f https://github.com/spotify/flink-on-k8s-operator/releases/download/v0.4.0-beta.8/flink-operator.yaml

# storage
kubectl create namespace manager
kubectl apply -f https://raw.githubusercontent.com/rancher/local-path-provisioner/v0.0.21/deploy/local-path-storage.yaml
kubectl apply -f ./cm-local-path.yaml # override default directory to /tmp (caution on reboot !)

sleep 30 # sleep for 30 seconds for application of configuration in local-path

helm repo add prometheus-community https://prometheus-community.github.io/helm-charts
helm repo update
helm install --namespace manager --version 30.0.2 prom prometheus-community/kube-prometheus-stack -f ./values-prom.yaml

kubectl apply -f pod-monitor.yaml

helm repo add grafana https://grafana.github.io/helm-charts
helm repo update
helm upgrade --install loki grafana/loki-stack --namespace manager --version 2.6.0 -f ./values-loki.yaml

sleep 10 # sometimes timeout

# repo for kowl
helm repo add cloudhut https://raw.githubusercontent.com/cloudhut/charts/master/archives
helm repo update

# ingress for prom, grafana
kubectl apply -f ../common/ingress-localhost.yaml

kubectl create namespace kafka

#kafka with kind cluster
#helm install my-release oci://registry-1.docker.io/bitnamicharts/kafka --namespace kafka -f ./values-kafka.yaml
#helm repo add bitnami https://charts.bitnami.com/bitnami
#helm repo update
#helm install my-release bitnami/kafka --namespace kafka -f ./values-kafka.yaml

#kafka when using g5k
kubectl apply -f ./kafka-deployment.yaml

sleep 10

helm repo add flink-operator-repo https://downloads.apache.org/flink/flink-kubernetes-operator-1.12.1/
helm repo update
kubectl apply -f https://raw.githubusercontent.com/kubernetes/ingress-nginx/main/deploy/static/provider/kind/deploy.yaml
helm install flink-kubernetes-operator flink-operator-repo/flink-kubernetes-operator

sleep 10

helm repo add minio-operator https://operator.min.io
helm repo update
helm install --namespace minio-operator --create-namespace operator minio-operator/operator
kubectl apply -f ./tenant-base.yaml

sleep 10