When developing locally, e.g. with a handy Minikube cluster, you can use the following configuration to define a
scrape-target deployed outside the cluster:

```bash
helm install prometheus prometheus-community/kube-prometheus-stack -f additional-scrape-config.yml
# or
helm upgrade prometheus prometheus-community/kube-prometheus-stack -f additional-scrape-config.yml
```

Make sure to specify your machine's IP address in [additional-scrape-config.yml](additional-scrape-config.yml).