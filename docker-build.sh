#!/usr/bin/env bash
rm -rf build/libs/
./gradlew assemble
docker buildx build --platform linux/arm64 -t robachmann/netatmo-exporter:1.2.1-arm64 --push .
