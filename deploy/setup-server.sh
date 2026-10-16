#!/bin/sh
# Oracle Cloud Ubuntu VM 최초 설정: Docker 설치, 방화벽(80/443) 열기, 메모리가 작으면 스왑 추가.
# 사용: sudo sh deploy/setup-server.sh
set -e

# Docker + compose 플러그인
if ! command -v docker >/dev/null 2>&1; then
  curl -fsSL https://get.docker.com | sh
fi
usermod -aG docker "${SUDO_USER:-ubuntu}"

# Oracle Ubuntu 이미지는 iptables 기본 규칙이 80/443을 막는다 (클라우드 보안 목록과 별개)
for port in 80 443; do
  if ! iptables -C INPUT -p tcp --dport "$port" -j ACCEPT 2>/dev/null; then
    iptables -I INPUT 6 -m state --state NEW -p tcp --dport "$port" -j ACCEPT
  fi
done
apt-get install -y iptables-persistent >/dev/null 2>&1 || true
netfilter-persistent save >/dev/null 2>&1 || true

# 메모리 2GB 미만(무료 AMD 인스턴스 1GB)이면 빌드 중 메모리 부족을 막기 위해 스왑 2GB
mem_kb=$(awk '/MemTotal/ {print $2}' /proc/meminfo)
if [ "$mem_kb" -lt 2000000 ] && [ ! -f /swapfile ]; then
  fallocate -l 2G /swapfile
  chmod 600 /swapfile
  mkswap /swapfile
  swapon /swapfile
  echo '/swapfile none swap sw 0 0' >> /etc/fstab
fi

echo "완료. 로그아웃 후 다시 접속하면 sudo 없이 docker 명령을 쓸 수 있습니다."
