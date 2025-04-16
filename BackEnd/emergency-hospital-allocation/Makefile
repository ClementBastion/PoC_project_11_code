.PHONY: db db-logs clean-keycloak locust jmeter postman
JMETER_PATH := /home/bastion/apache-jmeter-5.6.3/bin
POSTMAN_PATH := /home/bastion/postman-linux-x64/Postman


env:
	docker compose -f compose_env.yaml up -d

env-logs:
	docker compose -f compose_env.yaml logs -f

clean-keycloak:
	docker rm -f keycloak || true
	docker volume rm emergency-hospital-allocation_keycloak_data || true

locust:
	locust -f locustfile.py --host http://localhost:8080

jmeter:
	$(JMETER_PATH)/jmeter

postman:
	$(POSTMAN_PATH)/Postman

test:
	./gradlew test