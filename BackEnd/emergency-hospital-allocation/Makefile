.PHONY: db db-logs clean-keycloak

env:
	docker compose -f compose_env.yaml up -d

env-logs:
	docker compose -f compose_env.yaml logs -f

clean-keycloak:
	docker rm -f keycloak || true
	docker volume rm emergency-hospital-allocation_keycloak_data || true
