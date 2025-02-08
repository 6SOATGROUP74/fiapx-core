#!/bin/bash
kubectl apply -f ./service-account.yaml
kubectl apply -f ./app/service-rds.yaml
sleep 3
kubectl apply -f ./app/app-nlb.yaml
kubectl apply -f ./app/app-svc.yaml
kubectl apply -f ./app/app-configmap.yaml
kubectl apply -f ./app/app-deployment.yaml
kubectl apply -f ./app/app-hpa.yaml
kubectl apply -f ./app/app-opaque.yaml

