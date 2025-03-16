# Deployment

:warning: We expect the reader followed the [instructions to install the required tools.](./Requirements_g5k.md)

### Reserving nodes

From the front end Jupyter notebook web page open the [init-cluster.ipynb](./scripts/infra/g5k/init-cluster.ipynb) file.

Modify the [main.tf](./scripts/infra/g5k/main.tf) file to reflect your reservation:
1. nodes_count (Note that you need at least 4 nodes to deploy Flink)
2. walltime
3. nodes_selector: the name of the cluster
4. oar_job_name
5. site: the site should match the cluster


By default, the scripts we use create a local cluster of 4 virtual nodes:
1. 1 control-plane
1. 1 manager (Prometheus, Grafana, ...)
1. 1 node hosting the JobManager
1. `nodes_count - 3` nodes hosting the TaskManagers

Execute the notebook.
 This will install the required python libraries, create the cluster, and install the services using Helm.

 :warning: If the command complains about a missing Python library in the first cell, restart the kernel and execute it again. 

 The deployment can take several minutes.

### Deploying the Flink Kubernetes Operator
The Flink Kubernetes Operator is responsible of creating JobManagers, TaskManagers, and deploying Flink jobs on newly spawned workers. It also holds the logic of the autoscaler.

To deploy it using Helm from the project root directory:
```bash
# From the root directory
$ helm install flink-kubernetes-operator ./flink-kubernetes-operator/helm/flink-kubernetes-operator --set image.repository=flink-kubernetes-operator --set image.tag=dais -f ./flink-kubernetes-operator/examples/autoscaling/values.yaml
```
:warning: If you changed the name or the tag of the image when building it, please reflect the changes in the command.

To ensure that the operator is running:
```bash
$ kubectl get pods
NAME                                         READY   STATUS    RESTARTS   AGE
flink-kubernetes-operator-6569cb9b96-q4dbc   2/2     Running   0          3m25s
```

We are now all set to deploy a Flink job!