#!/bin/bash

./gradlew spotlessCheck

status=$?

if ["$status" = 0]; then
    exit 0
else
    echo 1>&2 "Issue found"
    exit 1
fi