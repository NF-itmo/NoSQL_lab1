BACKEND_DOCKERFILE=./backend/Dockerfile 
BACKEND_IMAGE_NAME=nosql-lab1-backend
BACKEND_PATH=./backend

FRONTEND_DOCKERFILE=./nginx/Dockerfile
FRONTEND_IMAGE_NAME=nosql-lab1-frontend
FRONTEND_PATH=.  # это плохо, очень плохо. как и всё что сделано в этом проекте

COMPOSE=docker-compose.yaml
ETCD_TEST_ENDPOINTS=http://localhost:2379,http://localhost:22379,http://localhost:32379

all: run

build-backend:
	docker buildx build \
		-f $(BACKEND_DOCKERFILE) \
		-t $(BACKEND_IMAGE_NAME) $(BACKEND_PATH)

build-frontend:
	docker buildx build \
		-f $(FRONTEND_DOCKERFILE) \
		-t $(FRONTEND_IMAGE_NAME) $(FRONTEND_PATH)

run: build-backend build-frontend
	docker compose -f ${COMPOSE} up --force-recreate

test-etcd-conflicts:
	docker compose -f $(COMPOSE) up -d --wait etcd1 etcd2 etcd3
	cd $(BACKEND_PATH) && \
		ETCD_ENDPOINTS=$(ETCD_TEST_ENDPOINTS) \
		./mvnw -q -pl backend -Dtest=EtcdConcurrentWritesIT test