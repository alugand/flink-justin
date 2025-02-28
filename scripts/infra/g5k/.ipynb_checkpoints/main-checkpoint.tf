module "k8s_cluster" {
    source = "github.com/guillaumerosinosky/terraform-grid5000-k8s-cluster"
    nodes_count = 5 #8
    walltime = 1 #2
    reservation = var.reservation
    nodes_selector="{cluster='gros'}"
    oar_job_name = "gepiciad_resource-estimator"
    kubernetes_version = "v1.22.4-rancher1-1"
    site = "nancy"
}