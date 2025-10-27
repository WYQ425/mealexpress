# MealExpress QA Test Plan (Local)

## 1. 鑳屾櫙

閽堝 `upgrade/local-20251024` 鎷嗗垎鐗堬紝瀹炵幇绔埌绔啋鐑?+ 鍏抽敭涓氬姟鍥炲綊锛岃鐩栧悗绔洓涓湇鍔°€佸墠绔帶鍒跺彴銆佸熀纭€璁炬柦锛圖ocker/Nacos/RocketMQ/Redis锛夈€?
## 2. 鐩爣涓庤寖鍥?
| 鐩爣 | 璇存槑 |
| --- | --- |
| 鍔熻兘楠岃瘉 | 鐢ㄦ埛涓嬪崟銆佽彍鍝?濂楅绠＄悊銆侀€氱煡涓績銆佹敮浠樺洖璋冿紙妯℃嫙锛夈€佽繍钀ュ悗鍙?|
| 闆嗘垚楠岃瘉 | Gateway 璺敱銆丯acos 鏈嶅姟娉ㄥ唽銆丷edis 缂撳瓨銆丷ocketMQ 閫氱煡 |
| 鏁版嵁楠岃瘉 | `sky.sql` 鍒濆鍖?+ `docs/DB_Migration.md` 姝ラ瀹屾垚鍚庯紝鏍稿績琛ㄦ暟鎹纭?|
| 闈炲姛鑳?| 鍚仠娴佺▼銆佽剼鏈彲鐢ㄦ€с€佹棩蹇?鍋ュ悍妫€鏌ュ彲杩借釜 |

涓嶅湪鑼冨洿锛氭€ц兘鍘嬫祴銆佺Щ鍔ㄧ UI銆佺敓浜х骇澶氳妭鐐归儴缃层€?
## 3. 娴嬭瘯鐜

| 缁勪欢 | 鐗堟湰 | 澶囨敞 |
| --- | --- | --- |
| OS | Windows 11 22H2 | PowerShell 5.1 |
| Docker Desktop | 4.33 + Compose v2 | `docker-up.ps1` 鍚姩 |
| JDK | 1.8.0_361 | `java -version` |
| Maven | 3.9.6 | `mvn -v` |
| Node.js | 16.20.2 + npm 8.19 | `node -v` |
| 娴忚鍣?| Edge/Chrome 鏈€鏂?| 鍓嶇楠岃瘉 |
| Sentinel Dashboard | 1.8.6 (Docker) | `http://localhost:8858`，默认账号/密码 `sentinel/sentinel` |
| Seata Server | 1.5.2 (Docker) | 监听 8091，事务组默认 `mealexpress_tx_group` |

鑴氭湰锛歚scripts/windows/docker-up.ps1`, `start-backend.ps1`, `start-frontend.ps1`, `smoke-test.ps1`銆?
## 4. 娴嬭瘯椤?& 鐢ㄤ緥

### 4.1 鍩虹璁炬柦

| 缂栧彿 | 鍦烘櫙 | 姝ラ | 鏈熸湜 |
| --- | --- | --- | --- |
| INF-001 | Docker 鍚姩 | 杩愯 `docker-up.ps1` | 鎵€鏈夊鍣?running/healthy |
| INF-002 | Docker 鍋滄 | `docker-down.ps1` | 瀹瑰櫒娓呯悊銆佺鍙ｉ噴鏀?|
| INF-003 | 鍐掔儫鑴氭湰 | `smoke-test.ps1` | HTTP + Docker + RocketMQ 鍏ㄩ儴閫氳繃 |
| INF-004 | Sentinel Dashboard | 浏览器访问 `http://localhost:8858/` | 登录成功，能查看服务流控数据 |
| INF-005 | RocketMQ 自检 | `smoke-test.ps1`（自动调用 `mq-self-test.ps1`） | 输出含 `SEND_OK`、`Consume ok` 以及期望的 `Body` |

### 4.2 鍚庣鏈嶅姟

| 缂栧彿 | 鍦烘櫙 | 姝ラ | 鏈熸湜 |
| --- | --- | --- | --- |
| BE-001 | Gateway 娉ㄥ唽 | `start-backend.ps1` 鍚庢煡鐪?Nacos | 鍥涗釜鏈嶅姟鍧囨敞鍐岋紝鐘舵€?UP |
| BE-002 | 鐢ㄦ埛涓嬪崟 API | 璋冪敤 `/api/order/submit`锛圥ostman/鍓嶇锛?| 杩斿洖鎴愬姛锛岃鍗曞叆搴?|
| BE-003 | 鑿滃搧 CRUD | 璋冪敤 `/api/dish` 绯诲垪鎺ュ彛 | 鏂板/淇敼/鍒犻櫎鎴愬姛锛孌B 鏇存柊 |
| BE-004 | 閫氱煡涓績 | `order-service` 瑙﹀彂 RocketMQ -> WebSocket | 鍓嶇閫氱煡涓績鏀跺埌鎺ㄩ€?|
| BE-005 | Redis 缂撳瓨 | 鐧诲綍鍚庢鏌?redis key | 浼氳瘽銆侀獙璇佺爜绛?key 姝ｅ父鍐欏叆 |

### 4.3 鍓嶇

| 缂栧彿 | 鍦烘櫙 | 姝ラ | 鏈熸湜 |
| --- | --- | --- | --- |
| FE-001 | 鐧诲綍/閫€鍑?| 娴忚鍣ㄨ闂?`http://localhost/#/login` | 鐧诲綍鎴愬姛銆侀€€鍑哄洖鍒扮櫥褰曢〉 |
| FE-002 | 浠〃鐩?| 鏌ョ湅棣栭〉銆佺粺璁″浘 | 鏁版嵁灞曠ず姝ｅ父锛屾棤 JS error |
| FE-003 | 鑿滃搧绠＄悊 | 鏂板缓鑿滃搧銆佷笂浼犲浘鐗?| 鍒楄〃鍒锋柊銆佸浘鐗囪惤 OSS (鍙ā鎷? |
| FE-004 | 璁㈠崟绠＄悊 | 鏌ョ湅/绛涢€夎鍗曘€佹寚娲鹃厤閫?| 鐘舵€佸垏鎹㈡纭?|
| FE-005 | 閫氱煡涓績 | 鏌ョ湅寰呮帴鍗曘€佸凡鎺ュ崟 | 鍒楄〃/璺宠浆姝ｅ父 |
| FE-006 | 鍗曟祴 | `npm run test:unit` | 9 涓祴璇曞叏閮?PASS |
| FE-007 | 鏋勫缓 | `npm run build` | 浜х墿鐢熸垚骞跺彲鐢?Nginx 閮ㄧ讲 |

## 5. 娴嬭瘯娴佺▼

1. 鍑嗗鐜锛歚docker-up.ps1` 鈫?`start-backend.ps1` 鈫?`start-frontend.ps1 -Mode build -StartNginx`銆?2. 鎵ц `smoke-test.ps1`锛岀‘淇濅緷璧栧氨缁€?3. 鎸夌珷鑺?4 閫愰」鎵ц锛堝缓璁敤琛ㄦ牸璁板綍缁撴灉/鎴浘锛夈€?4. 缂洪櫡绠＄悊锛氬湪 `todo.md` P0/P1 鍖哄煙鐧昏锛屽苟鍏宠仈澶嶇幇姝ラ銆?5. 閫€鍑猴細`docker-down.ps1`锛屽叧闂悗绔?鍓嶇绐楀彛銆?
## 6. 閫氳繃鏍囧噯

| 椤?| 鏍囧噯 |
| --- | --- |
| 鍏抽敭鐢ㄤ緥 | INF/BE/FE 鐢ㄤ緥鍏ㄩ儴 PASS锛屾棤闃诲缂洪櫡 |
| 鑷姩鍖?| lint/test/build/smoke 鍛戒护鍧囨垚鍔?|
| 鏁版嵁 | 涓嬪崟娴佹按銆佽彍鍝併€侀€氱煡鏁版嵁姝ｇ‘鍏ュ簱涓?Redis/RocketMQ 姝ｅ父宸ヤ綔 |
| 鏂囨。 | QA 缁撴灉鍙嶉鍒?`ReleaseNotes_LOCAL.md` / `Runbook_LOCAL.md` |

## 7. 闄勫綍

- Postman 闆嗗悎鍙湪 `qa/postman/mealexpress-2025-local.json`锛堝悗缁ˉ鍏咃級銆?- 鑻ラ渶鍘嬫祴鎴栧鑺傜偣楠岃瘉锛屽皢鍦?P2 杩唬涓彟寮€璁″垝銆? 
