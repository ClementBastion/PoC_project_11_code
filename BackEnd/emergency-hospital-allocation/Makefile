.PHONY: db db-logs clean-keycloak locust jmeter postman
JMETER_PATH := /home/bastion/apache-jmeter-5.6.3/bin
POSTMAN_PATH := /home/bastion/postman-linux-x64/Postman
APP_JAR=target/emergency-hospital-allocation.jar

env:
	docker compose -f compose_env.yaml up -d

env-logs:
	docker compose -f compose_env.yaml logs -f

env-build:
	docker compose -f compose_env.yaml up --build

clean-keycloak:
	docker rm -f keycloak || true
	docker volume rm emergency-hospital-allocation_keycloak_data || true

locust:
	locust -f locustfile.py --host http://localhost:8080

open-jmeter:
	$(JMETER_PATH)/jmeter

jmeter:
	jmeter \
      -JAPI_KEY=d3a1f8e2-5c4b-4f9b-ae69-1c2d3e4b5f60 \
      -n \
      -t jmeter/stress-random-test.jmx \
      -l jmeter/results/stress-test-results.jtl \
      -j jmeter/logs/jmeter.log

jmeter-report:
	@echo "Cleaning old report…"
	@rm -rf jmeter/results/report
	@echo "Generating new JMeter HTML report…"
	jmeter \
	  -g jmeter/results/stress-test-results.jtl \
	  -o jmeter/results/report
	@echo "Report generated at jmeter/results/report/index.html"
	@if command -v xdg-open >/dev/null; then \
	  xdg-open jmeter/results/report/index.html & \
	elif command -v open >/dev/null; then \
	  open jmeter/results/report/index.html & \
	else \
	  echo "Please open jmeter/results/report/index.html manually."; \
	fi

postman:
	$(POSTMAN_PATH)/Postman

test:
	./gradlew test

kafka-messages:
	docker exec -it kafka \
	kafka-console-consumer --bootstrap-server kafka:29092 \
	--topic hospital.bed.allocated --from-beginning --property print.key=true --property print.timestamp=true

GRADLEW := ./gradlew

JAR := $(firstword $(wildcard build/libs/*.jar))

.PHONY: clean build stress-mode stress-run

clean:
	$(GRADLEW) clean

build:
	$(GRADLEW) build -x test

stress-mode: build
	@echo "🚀 Démarrage en mode stress (profil=stress) via java -jar..."
	SPRING_PROFILES_ACTIVE=stress java -jar $(JAR)

stress-run:
	@echo "🚀 Démarrage en mode stress (profil=stress) via bootRun..."
	SPRING_PROFILES_ACTIVE=stress $(GRADLEW) bootRun
