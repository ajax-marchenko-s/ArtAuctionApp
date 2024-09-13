eval $(minikube docker-env)

minikube addons enable ingress

echo "Building project..."
./gradlew clean build

echo "Building Docker image..."
docker build -t artauction-app .

echo "Applying Kubernetes configurations..."
kubectl apply -f k8s-configuration/mongo-secret.yml
kubectl apply -f k8s-configuration/mongo-configmap.yml
kubectl apply -f k8s-configuration/mongodb-deployment.yml

kubectl wait --for=condition=ready pod -l app=mongodb --timeout=200s

kubectl apply -f k8s-configuration/artauction-configmap.yml
kubectl apply -f k8s-configuration/artauction-deployment.yml
kubectl apply -f k8s-configuration/mongo-express-deployment.yml
kubectl apply -f k8s-configuration/ingress.yml

kubectl wait --for=condition=ready pod --all --timeout=200s
