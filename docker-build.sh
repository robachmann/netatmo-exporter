#!/usr/bin/env bash
./gradlew assemble
docker buildx build --platform linux/arm64 -t robachmann/netatmo-exporter:1.2.0-arm64 --push .
