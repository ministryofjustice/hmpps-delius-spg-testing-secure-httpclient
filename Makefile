default: build
.PHONY: build

build:
	./gradlew clean build

# Http client Pipeline
eng-ci-plan:
	scripts/local-stack-action.sh plan

eng-ci-apply:
	scripts/local-stack-action.sh apply

eng-ci-destroy:
	scripts/local-stack-action.sh destroy