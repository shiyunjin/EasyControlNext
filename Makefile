DOCKER ?= docker
IMAGE ?= easycontrolnext:local
SRC ?= $(CURDIR)/easycontrolnext

.PHONY: build image clean clean-image

build: image
	$(DOCKER) run --rm \
		-u "$(shell id -u):$(shell id -g)" \
		-e HOME=/tmp \
		-e GRADLE_USER_HOME=/tmp/.gradle \
		-v "$(SRC):/src" \
		-w /src \
		$(IMAGE) \
		sh -c '\
			sh gradlew --no-daemon assembleDebug -p server && \
			sh gradlew --no-daemon copyDebug -p server && \
			sh gradlew --no-daemon assembleDebug && \
			sh gradlew --no-daemon assembleRelease -p server && \
			sh gradlew --no-daemon copyRelease -p server && \
			sh gradlew --no-daemon assembleRelease'

image:
	$(DOCKER) build -t $(IMAGE) .

clean:
	rm -rf "$(SRC)/app/build" "$(SRC)/server/build" "$(SRC)/build" "$(SRC)/.gradle"
	rm -f "$(SRC)/app/src/main/res/raw/easycontrolnext_server.jar"

clean-image:
	-$(DOCKER) rmi $(IMAGE)
