#!/bin/bash
for i in {1..10000}; do
  curl ae70c0cec2f44480094ef0e85c6e4337-1292753061.us-east-1.elb.amazonaws.com/v1/pedidos
  sleep $1
done