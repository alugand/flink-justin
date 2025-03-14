# JUSTIN

This repository contains the code of Justin along with instructions on how to reproduce the results presented in the paper.

The experiments were conducted on the [Grid5000]() grid but we also include instructions to run Justin in a local environment using Kind.

## Local environment
The simplest way to test Justin is to deploy a local cluster.
Please read the following instructions to deploy a cluster, build the project, and run the experiments.
1. [Requirements.ms](./Requirements.md) contains the instructions to install the required tools, namely Jupyter Notebook, Kind, Helm, and Kubectl.
At the end of the instructions, you will also be able to build the JARs of Flink and the Flink Kubernetes Operator using Docker.
2. Next, read the [instructions](./Deployment.md) on how to deploy a local cluster and install the required services (Prometheus, Grafana, ...).
3. Finally, [Benchmarks.md](./Benchmarks.md) contains the instructions on how to run the motivation and macro benchmarks