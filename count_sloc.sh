#!/usr/bin/env bash

for f in src/main/kotlin/*.kt; do
  result=$(sloc "$f" -f csv | tail -n 1 | cut -d',' -f3)
  echo "$f: $result"
done
