#!/bin/bash

kubectl delete -f mysql-service.yaml
kubectl delete -f mysql-deployment.yaml
kubectl delete -f mysql-secret.yaml
kubectl delete -f mysql-storage.yaml
kubectl delete -f mysql-namespace.yaml

