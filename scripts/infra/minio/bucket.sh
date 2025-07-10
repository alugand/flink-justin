cd "$(dirname "$0")"
kubectl apply -f tenant-base.yaml
sleep 50
kubectl port-forward svc/myminio-hl 9000 -n minio-tenant &
sleep 10
mc alias set myminio http://localhost:9000 minio minio123 --insecure
mc mb myminio/mybucket --insecure