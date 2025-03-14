# Requirements


### Notebooks
We assume that the user has `python3` installed.

1. Head to the root directory of the project and create a new virtual environment: 
```bash
$ python3 -m venv venv
$ source venv/bin/activate
```
2. Install and launch a Jupyter notebook server
```bash
$ pip install notebook
$ python3 -m notebook
```
The last command will prompt a url to access the notebook which we will use to reproduce our experiments.

### Cluster tools

:warning: Kind and Helm both require Docker to be installed:
```bash
$ curl -fsSL https://get.docker.com -o get-docker.sh
$ sudo sh get-docker.sh
# You should also add your user to the 'docker' group
$ sudo groupadd docker
$ sudo usermod -aG docker $USER
$ newgrp docker
```


We need the following tools to create and manipulate a local Kubernetes cluster:

1. Kind -> [Installation](https://kind.sigs.k8s.io/docs/user/quick-start/#installation)
2. Helm -> [Installation](https://helm.sh/docs/helm/helm_install/)
3. Kubectl -> [Installation](https://kubernetes.io/docs/tasks/tools/install-kubectl-linux/#install-kubectl-binary-with-curl-on-linux)

### Compiling Justin

To compile the Flink code base along with the benchmarks, we provide a Dockerfile located at the root folder.
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