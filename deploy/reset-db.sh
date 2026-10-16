#!/bin/sh
# 예시 데이터를 처음 상태로 되돌린다 (가입한 회원·작성한 내용은 모두 지워진다).
# 예시 데이터의 날짜는 실행일 기준으로 다시 만들어지므로, 체험 사이트를 계속 "이번 달" 데이터로 유지하려면
# 한 달에 한 번 정도 실행한다. 예) crontab: 0 4 1 * * cd ~/finNS && sh deploy/reset-db.sh
set -e
cd "$(dirname "$0")/.."

docker compose exec -T db sh -c '
  mariadb -uroot -p"$MARIADB_ROOT_PASSWORD" --default-character-set=utf8mb4 < /docker-entrypoint-initdb.d/01-schema.sql &&
  mariadb -uroot -p"$MARIADB_ROOT_PASSWORD" --default-character-set=utf8mb4 < /docker-entrypoint-initdb.d/02-seed.sql
'
echo "예시 데이터를 다시 만들었습니다."
