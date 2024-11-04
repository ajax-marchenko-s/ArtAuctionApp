eval $(minikube docker-env)

minikube addons enable ingress

echo "Building project..."
./gradlew clean assemble

echo "Building Docker images..."
docker build -f ./domainservice/Dockerfile-domainservice -t domainservice ./domainservice
docker build -f ./gateway/Dockerfile-gateway -t gateway ./gateway

echo "Applying Kubernetes configurations..."
kubectl apply -f k8s-configuration/mongo-secret.yml
kubectl apply -f k8s-configuration/mongo-configmap.yml
kubectl apply -f k8s-configuration/mongo-volume.yml
kubectl apply -f k8s-configuration/mongo-volume-claims.yml
kubectl apply -f k8s-configuration/mongodb-stateful.yml
kubectl apply -f k8s-configuration/nats-deployment.yml

kubectl wait --for=condition=ready pod -l app=mongodb --timeout=200s
kubectl wait --for=condition=ready pod -l app=nats --timeout=200s

kubectl apply -f k8s-configuration/artauction-configmap.yml
kubectl apply -f k8s-configuration/artauction-deployment.yml

kubectl apply -f k8s-configuration/mongo-express-deployment.yml

kubectl apply -f k8s-configuration/gateway-configmap.yml
kubectl apply -f k8s-configuration/gateway-deployment.yml

kubectl apply -f k8s-configuration/ingress.yml

kubectl wait --for=condition=ready pod --all --timeout=200s
