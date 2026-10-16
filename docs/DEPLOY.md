# Oracle Cloud 배포 순서

Oracle Cloud 무료(Always Free) VM 한 대에 Docker Compose로 DB·앱·Caddy(HTTPS)를 띄웁니다.
주소는 무료 DuckDNS 서브도메인(예: `finns.duckdns.org`)을 씁니다. 처음 한 번만 하면 되고, 30분~1시간 정도 걸립니다.

## 1. DuckDNS 서브도메인 만들기

1. https://www.duckdns.org 에 GitHub 계정 등으로 로그인합니다.
2. **sub domain** 칸에 원하는 이름(예: `finns`)을 넣고 **add domain**을 누릅니다.
3. 화면 위쪽의 **token** 값을 메모해 둡니다. IP는 4단계에서 넣습니다.

## 2. Oracle Cloud 가입

1. https://www.oracle.com/cloud/free/ 에서 가입합니다. 본인 확인용 카드 등록이 필요하지만, Always Free 자원만 쓰면 요금이 나가지 않습니다.
2. 홈 리전은 **South Korea Central (Seoul)** 또는 **South Korea North (Chuncheon)** 을 고릅니다. 가입 후에는 바꿀 수 없습니다.

## 3. VM 만들기

**Compute → Instances → Create instance**

| 항목 | 값 |
| --- | --- |
| Image | Canonical Ubuntu 24.04 |
| Shape | **Ampere → VM.Standard.A1.Flex**, OCPU 2, 메모리 12GB (Always Free 범위) |
| Networking | 새 VCN, **Assign a public IPv4 address** 체크 |
| SSH keys | **Save private key**로 개인키를 내려받아 둡니다 |

- A1 자리가 없다는 오류(Out of capacity)가 나면 몇 시간 뒤 다시 시도하거나, **AMD → VM.Standard.E2.1.Micro**(메모리 1GB)를 고릅니다. 1GB에서도 동작하지만 첫 빌드가 오래 걸립니다(5단계 스크립트가 스왑을 추가합니다).

**포트 열기**: 인스턴스 화면 → **Subnet → Default Security List → Add Ingress Rules**

| Source CIDR | Protocol | Destination Port |
| --- | --- | --- |
| 0.0.0.0/0 | TCP | 80 |
| 0.0.0.0/0 | TCP | 443 |

**공인 IP 고정(권장)**: 인스턴스 → Attached VNICs → IPv4 Addresses → 공인 IP를 **Reserved public IP**로 바꿉니다. 그러면 서버를 껐다 켜도 IP가 바뀌지 않습니다.

## 4. 도메인을 서버 IP로 연결

DuckDNS 화면에서 서브도메인의 **current ip** 칸에 VM의 공인 IP를 넣고 **update ip**를 누릅니다.

## 5. 서버 설정

내 PC에서 접속합니다 (Windows PowerShell 기준).

```bash
ssh -i 내려받은개인키.key ubuntu@서버공인IP
```

서버에서:

```bash
git clone https://github.com/ygh97/finNS.git
cd finNS
sudo sh deploy/setup-server.sh   # Docker 설치, 80/443 방화벽 열기, (메모리 작으면) 스왑 추가
exit                              # 다시 접속해야 docker 권한이 적용됨
```

## 6. 실행

다시 접속해서:

```bash
cd finNS
cp .env.example .env
nano .env
```

`.env`에 값을 채웁니다. 비밀번호와 키는 `openssl rand -base64 32` 결과를 붙여 넣으면 됩니다.

```
DOMAIN=finns.duckdns.org
DB_ROOT_PASSWORD=...
DB_PASSWORD=...
JWT_SECRET=...
```

```bash
docker compose up -d --build     # 첫 빌드 5~10분
docker compose logs -f app       # "Server startup" 이 보이면 Ctrl+C
```

브라우저에서 `https://finns.duckdns.org` 에 접속해 **체험 계정으로 둘러보기**를 눌러 봅니다.
HTTPS 인증서는 Caddy가 첫 접속 때 자동으로 받습니다(1분 정도 걸릴 수 있음).

## 운영

| 할 일 | 명령 |
| --- | --- |
| 최신 코드 반영 | `git pull && docker compose up -d --build` |
| 로그 보기 | `docker compose logs -f app` |
| 예시 데이터 초기화 | `sh deploy/reset-db.sh` (가입 회원·작성 글 삭제) |
| 중지 / 시작 | `docker compose stop` / `docker compose start` |

**예시 데이터 날짜 유지**: 예시 소비 내역은 실행일 기준 3개월 전 ~ 1개월 후로 만들어집니다. 체험 화면에 항상 이번 달 데이터가 보이게 하려면 한 달에 한 번 초기화합니다.

```bash
crontab -e
# 매달 1일 새벽 4시
0 4 1 * * cd ~/finNS && sh deploy/reset-db.sh >> ~/reset-db.log 2>&1
```

## GitHub Actions 자동 배포 (선택)

main에 푸시하면 서버에서 `git pull && docker compose up -d --build`를 실행하도록 `.github/workflows/deploy.yml`이 준비돼 있습니다.
GitHub 저장소 **Settings → Secrets and variables → Actions**에 아래를 넣으면 켜집니다.

| 종류 | 이름 | 값 |
| --- | --- | --- |
| Variables | `DEPLOY_ENABLED` | `true` |
| Secrets | `SSH_HOST` | 서버 공인 IP |
| Secrets | `SSH_USER` | `ubuntu` |
| Secrets | `SSH_KEY` | 3단계에서 받은 개인키 파일 내용 전체 |
