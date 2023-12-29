# BlobForge
Easy-to-use JSON blob storage behind a REST API   

## About
Store JSON blob data persistently using REST calls.   
Works with any language that supports HTTP.   
This is by no means a professional database solution, but might be useful for demo or proof-of-concept purposes.   

Supported routes:
```
GET /json/{blobName}
PUT /json/{blobName}
POST /init
```

## How to use 
Using BlobForge takes a few simple steps:
- Run the application using Docker
- Initialize a storage account

### Run the application using Docker
Run the Docker container:
```shell
docker run -p 8080:8080 -v ./blobforge-storage/:/app/blobs/ blobforge:latest 
```

or using Docker Compose: 
```yaml
version: '3.1'
services:
  blobforge:
    image: rwslinkman/blobforge:latest
    environment:
      BLOBFORGE_VOLUME: /app/blobs/
    ports:
      - "8080:8080"
    volumes:
      - ./blobforge-storage:/app/blobs/
```

or using Kubernetes:
```yaml
apiVersion: v1
kind: PersistentVolumeClaim
metadata:
  name: blobforge-data-pvc
  namespace: my-namespace
spec:
  storageClassName: default
  accessModes:
    - ReadWriteOnce
  resources:
    requests:
      storage: 1Gi
---
apiVersion: v1
kind: Deployment
metadata:
  name: blobforge
  namespace: my-namespace
spec:
  selector:
    matchLabels:
      app: blobforge
  replicas: 1
  template:
    metadata:
      labels:
        app: blobforge
    spec:
      volumes:
        - name: blobforge-data-volume
          persistentVolumeClaim:
            claimName: blobforge-data-pvc
      containers:
        - name: blobforge
          image: docker.io/rwslinkman/blobforge:1.0.0
          imagePullPolicy: IfNotPresent
          ports:
            - containerPort: 8080
          env:
            - name: BLOBFORGE_VOLUME
              value: /app/blobs/
          volumeMounts:
            - mountPath: /app/blobs/
              name: blobforge-data-volume
```

### Initialize a storage account 
You can choose an API key to configure a storage account.   

```shell
curl --location 'http://localhost:8080/init' \
--header 'Content-Type: application/json' \
--data '{
    "apiKey": "<any-apikey-you-desire-of-length-32>",
    "cleanInit": false
}'
```

### Store JSON persistently in BlobForge
Send a PUT request to store or update your JSON data

```shell
curl --location --request PUT 'http://localhost:8080/json/my-blob' \
--header 'X-API-Key: <your-api-key>' \
--header 'Content-Type: application/json' \
--data '{
    "hi": "this is valid json",
    "lastTimestamp": 123523535,
    "isTrue": false
}'
```

### Load your JSON data at a later time
Fetch your data when you need it: 

```shell
curl --location --request GET 'http://localhost:8080/json/my-blob' \
--header 'X-API-Key: <your-api-key>'
```