# MealExpress 杩愮淮 Runbook锛圠ocal锛?
## 1. 閫傜敤鑼冨洿

鏈?Runbook 闈㈠悜鏈湴锛圵indows 10/11锛夊紑鍙?楠屾敹鐜锛岃鐩?`upgrade/local-20251024` 鍒嗘敮瀵瑰簲鐨勬媶鍒嗗紡 MealExpress 闆嗙兢銆傛牳蹇冪粍浠讹細Docker Desktop锛圡ySQL/Redis/ElasticSearch/Nacos/RocketMQ/Seata锛夈€丼pring Boot 寰湇鍔★紙gateway/user/product/order/sky-server锛夈€乂ue 鍓嶇锛坧roject-sky-admin-vue-ts + Nginx锛夈€?
## 2. 鍏堝喅鏉′欢

| 椤圭洰 | 瑕佹眰 | 澶囨敞 |
| --- | --- | --- |
| 鎿嶄綔绯荤粺 | Windows 10/11锛孭owerShell 5+ | 鎵€鏈夎剼鏈潎涓?`.ps1` |
| JDK | 1.8u202+锛堥渶瑕?`java -version` 鍙敤锛?| Maven 鏋勫缓渚濊禆 |
| Maven | 3.6+ 鎴栦娇鐢ㄧ郴缁?`mvn` | `start-backend.ps1` 浼氳皟鐢?|
| Node.js / npm | Node 16.x + npm 8.x | 闇€鏀寔 `npm run lint/build` |
| Docker Desktop | 4.x锛屽惎鐢?WSL2 backend & Linux container | Compose v2 |
| 缃戠粶绔彛 | 8080/8081/8082/8083/8088/8848/6379/3306/9876/10911/8091 鍙敤 | `start-backend.ps1` 浼氬仛绔彛妫€娴?|

### 2.1 鐜鍙橀噺

灏嗘晱鎰熼厤缃啓鍏ョ敤鎴风幆澧冩垨 PowerShell Profile锛岀ず渚嬶細

```powershell
$env:MYSQL_USERNAME = 'root'
$env:MYSQL_PASSWORD = '***'
$env:REDIS_PASSWORD = ''
$env:ALI_OSS_ENDPOINT = 'oss-cn-beijing.aliyuncs.com'
$env:ALI_OSS_KEY = '<AK>'
$env:ALI_OSS_SECRET = '<SK>'
$env:ALI_OSS_BUCKET = 'mealexpress-demo'
$env:WECHAT_APP_ID = '<appid>'
# Sentinel dashboard 鐧诲綍锛堥粯璁?sentinel/sentinel锛屽彲鑷畾涔夛級
$env:SENTINEL_USERNAME = 'sentinel'
$env:SENTINEL_PASSWORD = 'sentinel'
# 鍙寜闇€鎸囧畾鍚勬湇鍔＄洃鍚鍙ｏ紝閬垮厤鍐茬獊
$env:SENTINEL_GATEWAY_PORT = 8719
$env:SENTINEL_USER_PORT = 8720
$env:SENTINEL_PRODUCT_PORT = 8721
$env:SENTINEL_ORDER_PORT = 8722
 $env:SEATA_SERVER_ADDR = '127.0.0.1:8091'
 $env:SEATA_TX_GROUP = 'mealexpress_tx_group'
# 鍏朵綑 WECHAT_* / BAIDU_AK / SHOP_ADDRESS 鍙寜闇€璁剧疆
```

## 3. 鏈嶅姟鍚仠娴佺▼

### 3.1 鍚姩

1. **鍩虹璁炬柦**
   ```powershell
   ./scripts/windows/docker-up.ps1 -PullImages
   ```
   - 榛樿璇诲彇 `..\docker-compose.yml`锛涜嫢鑷畾涔夎矾寰勶紝鍙敤 `-ComposeFile`.
   - 鑴氭湰浼氱瓑寰?mysql/redis/elasticsearch/kibana/nacos/rocketmq/seata/sentinel 绛夊仴搴枫€?
2. **鍚庣鏈嶅姟**
   ```powershell
   ./scripts/windows/start-backend.ps1 -Services gateway,user-service,product-service,order-service
   ```
   - 姣忎釜鏈嶅姟鍦ㄧ嫭绔?PowerShell 绐楀彛涓繍琛?`mvn spring-boot:run -DskipTests`銆?   - 濡傞渶鏃х増 `sky-server`锛岃拷鍔?`-IncludeSkyServer`銆?
3. **鍓嶇 + Nginx**
   ```powershell
   ./scripts/windows/start-frontend.ps1 -Mode build -StartNginx
   ```
   - `-InstallDependencies` 鍙湪棣栨杩愯鏃跺畨瑁?node_modules銆?   - build 瀹屾垚鍚庤嚜鍔ㄥ悓姝?`dist` 鍒?`nginx-1.20.2/html/sky` 骞跺惎鍔?`nginx.exe`銆?   - 鑻ラ渶瑕佺儹鏇存柊锛屽彲鏀圭敤 `-Mode serve`銆?
4. **鍐掔儫楠岃瘉**
   ```powershell
   ./scripts/windows/smoke-test.ps1
   ```
   - 鍐呯疆妫€鏌ワ細Gateway/User/Product/Order `/actuator/health`銆丯ginx 鍓嶇 URL銆丏ocker 涓?mysql/redis/nacos/rocketmq/seata 杩愯鐘舵€佸強绔彛銆佹坊鍔犲叾 RocketMQ 鏍￠獙锛堜笉闇€琚嚜鎵ю級銆?
### 3.2 鍋滄

1. `./scripts/windows/docker-down.ps1 -RemoveVolumes`锛堣嫢涓嶆兂娓呯悊鏁版嵁锛屽彲鐪佺暐 `-RemoveVolumes`锛夈€?2. 鎵嬪姩鍏抽棴 `start-backend.ps1` 鎵撳紑鐨?PowerShell 绐楀彛鎴栧湪绐楀彛鍐?`Ctrl+C`銆?3. 鍓嶇 dev server/Nginx锛氬湪 `start-frontend.ps1` 鎵撳紑鐨勭獥鍙ｆ墽琛?`Ctrl+C`锛屾垨杩愯 `nginx.exe -s stop` 鍏抽棴銆?
## 4. 鏃ュ父杩愮淮鎿嶄綔

| 鎿嶄綔 | 姝ラ |
| --- | --- |
| 鏌ョ湅瀹瑰櫒鐘舵€?| `docker ps --format "table {{.Names}}\t{{.Status}}"` |
| 鏌ョ湅 MySQL 鏃ュ織 | `docker logs mysql --tail 200` |
| 鏌ョ湅寰湇鍔℃棩蹇?| 鍦ㄥ搴?PowerShell 绐楀彛鎴?`target` 鐩綍 `tail -f` |
| 閲嶅惎鍗曚釜鏈嶅姟 | 鍏抽棴绐楀彛鍚庨噸鏂拌繍琛?`start-backend.ps1 -Services <name>` |
| 娓呯悊鍓嶇浜х墿 | `Remove-Item project-sky-admin-vue-ts\dist -Recurse -Force` |
| 杩愯鍓嶇 lint/test | `cd project-sky-admin-vue-ts; npm run lint && npm run test:unit` |
| 妫€鏌ヨ嚜瀹氫箟缃戠粶 | `docker network inspect projects_dev-network`锛堣嫢涓嶅瓨鍦ㄥ彲 `docker network create projects_dev-network --subnet 172.21.0.0/16`锛?| 
| 璁块棶 Sentinel Dashboard | 娴忚鍣ㄦ墦寮€ `http://localhost:8858/`锛堥粯璁よ处鍙?瀵嗙爜锛歚sentinel/sentinel`锛屽彲閫氳繃鐜鍙橀噺瑕嗙洊锛?| 
| 校验 RocketMQ 收发链路 | 执行 `./scripts/windows/mq-self-test.ps1`，确认输出包含 `Consume ok` 以及发送时的 `Body` |

## 5. 甯歌鏁呴殰涓庢帓鏌?
| 鐥囩姸 | 鍙兘鍘熷洜 | 鎺掓煡姝ラ | 澶勭悊寤鸿 |
| --- | --- | --- | --- |
| `start-backend.ps1` 鎶ョ鍙ｅ崰鐢?| 绔彛宸叉湁鍏朵粬绋嬪簭 | `Get-NetTCPConnection -LocalPort <port>` | 淇敼鏈嶅姟绔彛鎴栭噴鏀剧鍙?|
| Docker 瀹瑰櫒 unhealthy | 闀滃儚鎷夊彇澶辫触/閰嶇疆閿欒 | `docker logs <container>` | 閲嶆柊鎵ц `docker-up.ps1 -PullImages` |
| `npm run build` 澶辫触锛坒ibers锛?| 浣跨敤 Node 18+ | 纭繚 Node 16锛岄噸鏂?`npm install --legacy-peer-deps` | 
| 鍓嶇绌虹櫧椤?| Nginx 闈欐€佽祫婧愭湭鍚屾 | 閲嶆柊鎵ц `start-frontend.ps1 -Mode build -StartNginx` |
| 鏀粯/OSS 璋冪敤澶辫触 | 鐜鍙橀噺缂哄け | `Get-ChildItem Env: | ? Name -match 'MYSQL|ALI|WECHAT|BAIDU'` | 琛ラ綈鍙橀噺锛岄噸鍚湇鍔?|
| Sentinel Dashboard 鏃犳硶璁块棶 | 绔彛鍗犵敤鎴栧鍣ㄦ湭鍚姩 | `docker logs sentinel-dashboard`锛涚‘璁?8858 绔彛鍙敤 | 淇敼 `SENTINEL_*` 绔彛鎴栭噸鏂版墽琛?`docker-up.ps1` |

## 6. 鍙樻洿璁板綍

| 鏃ユ湡 | 鐗堟湰 | 鍙樻洿鍐呭 | 鎿嶄綔浜?|
| --- | --- | --- | --- |
| 2025-10-26 | upgrade/local-20251024 | 鍒濈増 Runbook锛屾柊澧炶剼鏈笌鏂囨。锛堣惀鍏讳簡 RocketMQ 自检脚本说明锛? | Codex |

> 鑻ユ墽琛?DB 杩佺Щ/鎭㈠銆佷緷璧栧崌绾х瓑鎿嶄綔锛岃鍦ㄦ琛ㄨˉ鍏呰褰曞苟鍚屾鍒?`docs/Inventory.md`銆? 

## 7. Medium-Term Enhancements

### 7.1 Elasticsearch validation track
- **Objective.** Capture baseline read/write throughput for order search and audit flows so we can size future clusters with data.
- **Staging data.** Clone production-like index mappings into `es-preview` (one shard, zero replicas). Seed with anonymised order snapshots (≥ 5 million docs) via `scripts/data/es-seed.ps1` (to be created alongside the test run).
- **Workload design.** Use JMeter or Rally to replay (a) order list paging, (b) fuzzy dish search, (c) management dashboards. Start at 200 req/s and scale by ×2 until 95th percentile latency exceeds 250 ms or heap pressure > 75 %.
- **Observability.** Enable slowlog (50 ms) and capture `/_nodes/stats` snapshots every step; record GC metrics with `docker stats` to correlate.
- **Exit & rollback.** If node saturation occurs, capture index settings, slowlog, and heap dump, then drop `es-preview` index and reset container via `docker compose restart elasticsearch`.

### 7.2 SkyWalking / Spring Boot Admin rollout path
- **Topology.** Add `skywalking-oap` + `skywalking-ui` services into `docker-compose.override.yml` (ports 12800/11800/8080). Mount `config/oap` for cluster settings.
- **Agent wiring.** Package `apache-skywalking-java-agent` under `scripts/agents/` and let `start-backend.ps1` set `-javaagent` plus `SW_AGENT_NAME=<service>` / `SW_AGENT_COLLECTOR_BACKEND_SERVICES=localhost:11800`.
- **Spring Boot Admin.** Host a lightweight admin server inside `mealexpress-gateway` profile `admin`, exposing 8090. Other services register via `spring.boot.admin.client.url=http://localhost:8090`.
- **Security.** Reuse existing Sentinel credentials; store `SW_AGENT_AUTHENTICATION` / SBA basic auth in `.env.local.example` only. Document opt-in toggles `ENABLE_SKYWALKING`, `ENABLE_SBA` so local contributors can disable instrumentation when debugging.
- **Verification.** Extend `smoke-test.ps1` with optional `-Observability` flag to assert OAP `/actuator/health` and SBA `/instances` respond with HTTP 200.

### 7.3 Operations roadmap
- **Capacity.** Keep Docker Desktop memory at ≥ 10 GB so Elasticsearch + SkyWalking can coexist. Each microservice remains under 512 MB heap; adjust via `JAVA_OPTS` in PowerShell profiles.
- **Cost & hygiene.** Nightly `docker system prune --volumes` after exporting MySQL dumps with `scripts/windows/backup-db.ps1` (to avoid local disk pressure). Rotate RocketMQ test topics monthly.
- **Upgrade cadence.** Review dependency stack quarterly: (Q1) spring-cloud-alibaba & Sentinel rules; (Q2) Elasticsearch and Seata; (Q3) Vue build toolchain; (Q4) Docker base images. Record outcomes in `docs/ReleaseNotes_LOCAL.md`.
- **On-call rehearsal.** Run `smoke-test.ps1 -Full` before every milestone tag; capture screenshots/logs in `qa/artifacts/<date>` for traceability.
