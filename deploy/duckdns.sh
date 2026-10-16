#!/bin/sh
# DuckDNS 서브도메인이 이 서버의 공인 IP를 가리키도록 갱신한다.
# 사용: DUCKDNS_DOMAIN=finns DUCKDNS_TOKEN=토큰 sh deploy/duckdns.sh
# Oracle 공인 IP는 고정(예약 IP)으로 두면 한 번만 실행해도 된다.
set -e
: "${DUCKDNS_DOMAIN:?DUCKDNS_DOMAIN 이 필요합니다 (예: finns)}"
: "${DUCKDNS_TOKEN:?DUCKDNS_TOKEN 이 필요합니다 (duckdns.org 로그인 후 화면 상단)}"
result=$(curl -fsS "https://www.duckdns.org/update?domains=${DUCKDNS_DOMAIN}&token=${DUCKDNS_TOKEN}&ip=")
echo "DuckDNS 갱신 결과: $result"
