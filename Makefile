BACKEND_DOCKERFILE=./backend/Dockerfile 
BACKEND_IMAGE_NAME=nosql-lab1-backend
BACKEND_PATH=./backend

FRONTEND_DOCKERFILE=./nginx/Dockerfile
FRONTEND_IMAGE_NAME=nosql-lab1-frontend
FRONTEND_PATH=.  # это плохо, очень плохо. как и всё что сделано в этом проекте

COMPOSE=docker-compose.yaml

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