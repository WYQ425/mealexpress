# MealExpress (Local) 蹇€熶笂鎵嬫寚鍗?
鏈粨搴撳寘鍚媶鍒嗗悗鐨?MealExpress 鍚庣锛坓ateway + 涓変釜涓氬姟鏈嶅姟 + 鍏煎鐨?`sky-server`锛夈€佸墠绔?Vue 鎺у埗鍙颁互鍙?Windows 杩愮淮鑴氭湰銆傛湰鎸囧崡鑱氱劍銆屽湪 Windows 缁堢蹇€熸媺璧?鍏抽棴/楠岃瘉鏁翠釜鏍堛€嶃€?
## 1. 鐩綍鎬昏

| 璺緞 | 璇存槑 |
| --- | --- |
| `mealexpress-gateway` | Spring Cloud Gateway锛堢鍙?8088锛?|
| `mealexpress-user-service` | 鐢ㄦ埛鍩熸湇鍔★紙绔彛 8081锛?|
| `mealexpress-product-service` | 鍟嗗搧鍩熸湇鍔★紙绔彛 8082锛?|
| `mealexpress-order-service` | 璁㈠崟鍩熸湇鍔★紙绔彛 8083锛?|
| `sky-server` | 鏃х増鍚庡彴锛堜繚鐣欏吋瀹癸級 |
| `project-sky-admin-vue-ts` | Vue2 + TS 鎺у埗鍙?|
| `nginx-1.20.2` | Windows 闈欐€佽祫婧愭壙杞?|
| `scripts/windows` | 鏂板鐨?PowerShell 宸ュ叿闆?|
| Sentinel Dashboard (Docker) | 流量治理看板（默认端口 8858，登录账号“sentinel/sentinel”）|
| Seata Server (Docker) | 分布式事务协调器（端口 8091，默认事务组 `mealexpress_tx_group`）|
| `docs/*.md` | Inventory銆丏B 杩佺Щ銆丷unbook銆丷eleaseNotes |
| `qa/TestPlan.md` | QA 鍐掔儫娓呭崟 |

## 2. 杩愯鍓嶅噯澶?
1. 瀹夎 JDK 8銆丮aven銆丯ode 16锛堝缓璁娇鐢?`nvm-windows` 鍒囨崲锛夈€?2. 瀹夎 Docker Desktop锛屽苟鍚敤 WSL2 + Linux 瀹瑰櫒銆?3. 灏嗘晱鎰熼厤缃啓鍏ョ幆澧冨彉閲忥紝渚嬪锛?   ```powershell
   $env:MYSQL_USERNAME='root'
   $env:MYSQL_PASSWORD='***'
   $env:ALI_OSS_ENDPOINT='oss-cn-beijing.aliyuncs.com'
   $env:ALI_OSS_KEY='your-ak'
   $env:ALI_OSS_SECRET='your-sk'
   $env:WECHAT_APP_ID='wx...'
   ```
4. 鍦?PowerShell 涓厑璁歌剼鏈墽琛岋紙绠＄悊鍛樿韩浠斤級锛歚Set-ExecutionPolicy RemoteSigned`銆?
## 3. 涓€閿惎鍔?/ 鍋滄

```powershell
cd D:\projects\mealexpress

# 1. 鍩虹璁炬柦
./scripts/windows/docker-up.ps1 -PullImages

# 2. 鍚庣寰湇鍔★紙浼氭墦寮€澶氫釜 PowerShell 绐楀彛锛?./scripts/windows/start-backend.ps1 -Services gateway,user-service,product-service,order-service

# 3. 鍓嶇锛坆uild + 鍚屾鍒?nginx + 鍚姩 nginx锛?./scripts/windows/start-frontend.ps1 -Mode build -StartNginx

# 4. 鍐掔儫楠岃瘉
./scripts/windows/smoke-test.ps1
```

鍋滄锛?
```powershell
./scripts/windows/docker-down.ps1 -RemoveVolumes
# 鍏抽棴 start-backend / start-frontend 鎵撳紑鐨勭獥鍙ｏ紝鎴栧湪绐楀彛涓?Ctrl+C銆?```

## 4. 甯哥敤鑴氭湰璇存槑

| 鑴氭湰 | 鍙傛暟 | 浣滅敤 |
| --- | --- | --- |
| `docker-up.ps1` | `-ComposeFile` 鎸囧畾 compose 璺緞锛沗-PullImages`; `-ForceRecreate`; `-SkipHealthChecks` | 灏佽 `docker compose up` 骞剁瓑寰?mysql/redis/nacos/rocketmq/seata/sentinel 绛夊仴搴?|
| `docker-down.ps1` | `-RemoveVolumes`; `-RemoveImages` | 灏佽 `docker compose down` |
| `start-backend.ps1` | `-Services gateway,user-service,...`; `-IncludeSkyServer`; `-SkipPortCheck` | 姣忎釜鏈嶅姟浠?`mvn spring-boot:run` 鏂瑰紡鍦ㄧ嫭绔嬬獥鍙ｅ惎鍔?|
| `start-frontend.ps1` | `-Mode serve|build`; `-InstallDependencies`; `-StartNginx`; `-FrontendDir`; `-NginxRoot` | 鍚姩 Vue dev server 鎴栫敓鎴愮敓浜у寘骞跺悓姝ュ埌 Nginx |
| `smoke-test.ps1` | `-GatewayBaseUrl`; `-FrontendUrl`; `-SkipDocker`; `-SkipRocketMQ` | 检查 Gateway/各服务 `/actuator/health`、Nginx、Sentinel Dashboard、Docker 容器端口，并内置 RocketMQ 自检 |
| `mq-self-test.ps1` | `-NameServer`; `-Topic`; `-ConsumerGroup`; `-DockerNetwork` | 发送/消费一条 RocketMQ 消息并校验输出中的 `Consume ok` 与消息体 |

## 5. 鍓嶇浠诲姟

```powershell
cd project-sky-admin-vue-ts
npm install --legacy-peer-deps  # 棣栨闇€瑕?npm run lint
npm run test:unit
npm run build
```

鏋勫缓浜х墿浣嶄簬 `dist/`锛屼篃浼氳 `start-frontend.ps1` 鍚屾鍒?`nginx-1.20.2/html/sky`銆?
璇存槑锛歴VNode` 鍩虹璁″垝浠娿€滃悗绔崌绾у悕闊瑰壇鎵€鍦版満搴庯紝鍔ㄧ敾娉ㄩ噴鍚?`gateway-service` 绔彛 8088 鎴?`/api` 浠ｇ悊锛?`.env.development` 鐨?`VUE_APP_URL` 浼氬搴?`http://localhost:8088`銆?
## 6. 鏂囨。 & QA

- `docs/Runbook_LOCAL.md`锛氬畬鏁磋繍缁存墜鍐岋紙鍚仠銆佹帓闅溿€佸彉鏇磋褰曪級銆?- `docs/DB_Migration.md`锛氬崟搴?鈫?澶氭湇鍔¤縼绉绘楠ゅ強鍥炴粴鏂规銆?- `docs/ReleaseNotes_LOCAL.md`锛氬姛鑳?淇/宸茬煡闂鍒楄〃銆?- `qa/TestPlan.md`锛氬啋鐑熺敤渚嬬煩闃碉紝鍙洿鎺ュ嬀閫夋墽琛屻€?
## 7. 闂鍙嶉

閬囧埌鑴氭湰鎶ラ敊鎴栫鍙ｅ啿绐侊細
1. 鏌ョ湅鑴氭湰杈撳嚭锛堝凡鍖呭惈鎻愮ず锛夛紱
2. 鍙傝€?`docs/Runbook_LOCAL.md` 鐨勨€滃父瑙佹晠闅溾€濊〃锛?3. 鍦?`todo.md` 涓寜鐓?P0/P1/P2 鏇存柊浠诲姟鐘舵€佸苟闄勪笂澶嶇幇姝ラ銆? 
