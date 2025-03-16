# Requirements Grid5000

We assume the user has access to the Grid5000 grid. If not, please [request an access](https://www.grid5000.fr/w/Grid5000:Get_an_account), or use Justin with a local cluster following the [local deployment instructions](./Requirements.md).

### Notebooks

Grid5000 frontends are equipped with Jupyter notebooks.
To create a new Jupyter instance:

1. Access the[ Grid5000 internal hub ](https://intranet.grid5000.fr/notebooks/hub/home).
2. From the hub, select the `Add New Sever` button.
3. From there, select a Site and click on `Site Frontend` from the second option line. 

### Cluster tools

We need the following tools to create and manipulate a local Kubernetes cluster:

1. Terraform -> [Installation](https://developer.hashicorp.com/terraform/tutorials/aws-get-started/install-cli)
2. Helm -> [Installation](https://helm.sh/docs/helm/helm_install/)
3. Kubectl -> [Installation](https://kubernetes.io/docs/tasks/tools/install-kubectl-linux/#install-kubectl-binary-with-curl-on-linux)

Either copy the binaries to the user lib folder or add them to a folder name `tools` located at the root user's home folder. The scripts will append the directory to the user's $PATH. 

### Compiling Justin

To compile the Flink code base along with the benchmarks, we provide a Dockerfile located at the root folder.
This operation cannot be done on the frontend of g5k. Either build the images from a local machine or from one of the reserved node.
Once built, the images need to be pushed to a public repository (e.g. Docker Hub).

This Dockerfile has 3 stages:
1. The `build` stage uses the official maven Docker image to compile the Flink code base with fine-grain memory allocation enabled through Justin.
The Flink uber jar with its libraries are located in the folder `/app/flink-dist/target/flink-1.18-SNAPSHOT-bin/flink-1.18-SNAPSHOT/` inside the image.
2. The `benchmark` stage compiles the motivation and Nexmark benchmarks.
3. Finally, the `main` stage uses the official Flink Docker image and replaces the default executables with the one compiled from the `build` stage and copies the benchmarks into the new image.

To build the container, run the following command:
```bash
# From the root folder
$ docker build . -t flink-justin:dais
```
:warning: The Flink code base is very large and building this image can take up to 15 minutes :warning:

### Compile the Flink Kubernetes Operator

Head to the `flink-kubernetes-operator` folder and run the following command to build the operator's Docker image: 
```bash
# from the flink-kubernetes-operator folder
$ docker build . -t flink-kubernetes-operator:dais
```
This build should take about 3 minutes.