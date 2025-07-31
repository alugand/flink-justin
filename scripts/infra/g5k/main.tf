module "k8s_cluster" {
    source = "github.com/alugand/terraform-grid5000-k8s-cluster"
    nodes_count = 5 #8
    walltime = 8 #2
    nodes_selector="{cluster='nova'}"
    oar_job_name = "gepiciad_resource-estimator"
    kubernetes_version = "v1.22.4-rancher1-1"
    site = "lyon"
}