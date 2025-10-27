# MealExpress 鍗囩骇鍙戝竷璇存槑锛圠ocal锛?
| 瀛楁 | 鍐呭 |
| --- | --- |
| 鍙戝竷鐗堟湰 | `upgrade/local-20251024` |
| 鍙戝竷鏃ユ湡 | 2025-10-26 |
| 璐熻矗浜?| Codex |

## 1. 鍙戝竷姒傝

1. **鏋舵瀯鎷嗗垎**锛氭柊澧?`mealexpress-gateway`銆乣mealexpress-user-service`銆乣mealexpress-product-service`銆乣mealexpress-order-service` 鍥涗釜 Spring Boot 鏈嶅姟锛屽苟淇濈暀 `sky-server` 浠ュ吋瀹规棫鍚庡彴銆?2. **搴忓垪鍖栧崌绾?*锛氬悗绔畬鍏ㄦ浛鎹?FastJson锛屼娇鐢?Jackson + 杞婚噺鍏煎灞傦紝閫氳繃 `mvn test` 宸查獙璇佸叧閿姤鏂囷紙璁㈠崟銆佹敮浠樸€佺敤鎴凤級銆?3. **鍓嶇淇**锛?   - Node 16 鐜閫傞厤銆佺Щ闄?`fibers`銆佹洿鏂伴攣鏂囦欢锛?   - ESLint 淇锛堝畨鍏?JSON 瑙ｆ瀽銆佹ā鏉胯娉?clean-up锛夛紱
   - 鏂板 Windows 鑴氭湰锛堝墠绔竴閿瀯寤恒€丯ginx 鍚屾銆佷緷璧栧畨瑁呰剼鏈級锛?   - `npm run lint`銆乣npm run test:unit`銆乣npm run build` 鍏ㄩ儴閫氳繃銆?4. **鍩虹璁炬柦**锛歚scripts/windows/docker-up.ps1` / `docker-down.ps1` / `start-backend.ps1` / `start-frontend.ps1` / `smoke-test.ps1`锛岃鐩栧惎鍔ㄣ€佸仠姝€侀獙璇佸叏娴佺▼銆?5. **閰嶇疆鑴辨晱**锛歚application-dev.yml` 涓嶅啀纭紪鐮佹暟鎹簱銆丷edis銆丱SS銆佸井淇°€佺櫨搴︾瓑瀵嗛挜锛屽叏閮ㄦ敼涓虹幆澧冨彉閲忓崰浣嶃€?6. **鏂囨。浣撶郴**锛氭柊澧?DB 杩佺Щ鎵嬪唽銆丷unbook銆丷eleaseNotes銆丵A TestPlan 浠ュ強 README 涓殑 Windows 蹇€熷惎鍔ㄧ珷鑺傘€?
7. **娴佺晠鏍稿績**锛氬苟鍔犱笂 Sentinel Dashboard 鏁版嵁缁存姢锛屽悗绔啓鍏?Sentinel starter + Seata 鍚屾閰嶇疆锛屽苟鎸夋帴 Block handler/鍔ㄦ€佷唬鐞嗭紝鏈€鍚庢寜闇€璁剧疆 `tx-service-group` / `seata.service.grouplist` 鏈嶅姟鍦板潃銆?
8. **Observability roadmap**: documented Elasticsearch benchmarking track, SkyWalking/Spring Boot Admin rollout, and long-term operations cadence in `docs/Runbook_LOCAL.md`.
## 2. 鍏煎鎬у彉鏇?
| 妯″潡 | 褰卞搷鎻忚堪 | 鎿嶄綔 |
| --- | --- | --- |
| 鍓嶇 | Node 16 + npm 8锛岄攣鏂囦欢鍒锋柊 | 閲嶆柊鎵ц `npm install --legacy-peer-deps` |
| 鍚庣 | Maven 妯″潡鎷嗗垎 | 浣跨敤 `start-backend.ps1` 鍚姩锛岄渶瑕佹湰鍦?Nacos/Redis/MySQL/RocketMQ |
| 閰嶇疆 | `application-dev.yml` 闇€瑕佺幆澧冨彉閲?| 鍙傝 README/Runbook锛岃缃?`MYSQL_PASSWORD`銆乣ALI_OSS_KEY`銆乣WECHAT_APP_ID` 绛?|
| 流量治理 | 各服务新增 Sentinel starter + Dashboard | 设置 `SENTINEL_*` 环境变量，默认 Dashboard `http://localhost:8858` |
| 分布式事务 | order-service 接入 Seata，全局事务组统一为 `mealexpress_tx_group` | 设置 `SEATA_SERVER_ADDR`、`SEATA_TX_GROUP`，确认 Seata Server 8091 可用 |

## 3. 淇鍒楄〃

| 缂栧彿 | 鎻忚堪 | 妯″潡 |
| --- | --- | --- |
| FE-001 | `eval` 瑙ｆ瀽閫氱煡璇︽儏瀛樺湪瀹夊叏闅愭偅 | `project-sky-admin-vue-ts/src/views/inform/index.vue` |
| FE-002 | Router lint 鏃犳硶閫氳繃锛坱ab & template锛?| `src/router.ts`銆乣src/views/dish/addDishtype.vue` |
| FE-003 | Breadcrumb 鍗曟祴鍦?Vue Router v3.4 涓嬫姤閿?| `tests/unit/components/Breadcrumb.spec.ts` |
| OPS-001 | Docker compose 鏃犺剼鏈皝瑁?| `scripts/windows/docker-up.ps1 / docker-down.ps1` |
| OPS-002 | 缂哄皯涓€閿惎鍔ㄥ墠鍚庣涓庡啋鐑熸祴璇曡剼鏈?| `scripts/windows/*.ps1` |
| OPS-003 | `application-dev.yml` 鏄庢枃瀵嗙爜 & 瀵嗛挜鏆撮湶 | 鍚屽悕鏂囦欢 |
| OPS-004 | 新增 Sentinel Dashboard 容器与后端限流配置 | `docker-compose.yml`, 各微服务 `application.yml`、Sentinel BlockHandler |
| OPS-005 | order-service 引入 Seata Starter 与默认事务组配置 | `mealexpress-order-service/pom.xml`、`application.yml`, README/Runbook 更新 |
| OPS-006 | 新增 RocketMQ 自检脚本并纳入 Runbook/QA | `scripts/windows/mq-self-test.ps1`, `docs/Runbook_LOCAL.md`, `qa/TestPlan.md` |
| DOC-001 | 缂哄皯杩佺Щ/Runbook/ReleaseNotes/QA 鏂囨。 | `docs/*.md`, `qa/TestPlan.md`, `README.md` |

## 4. 宸茬煡闂 & 涓存椂鎺柦

| 闂 | 褰卞搷 | 涓存椂鏂规 |
| --- | --- | --- |
| Vue 2 + Element UI 鏋勫缓鏃?Sass 鍙戝嚭澶ч噺 deprecation warning | 浠?warning锛屼笉褰卞搷浜х墿 | 鍚庣画缁熶竴杩佺Щ鍒?dart-sass 鏂?API |
| `npm run build` 杈撳嚭闈欐€佽祫婧愯緝澶э紙2.8 MiB锛?| 棣栧睆鍔犺浇鐣ユ參 | Nginx 鍙紑鍚?gzip / brotli锛涗笅涓増鏈仛浠ｇ爜鍒嗗寘 |
| `scripts/windows/start-backend.ps1` 榛樿绔彛浠?8080/8081/8082/8083/8088 | 涓庡叾瀹冩湰鍦版湇鍔″彲鑳藉啿绐?| 閫氳繃鐜鍙橀噺/閰嶇疆淇敼绔彛鍚庯紝鍐嶆墽琛岃剼鏈?|

## 5. 楠岃瘉缁撴灉

| 椤圭洰 | 缁撴灉 | 澶囨敞 |
| --- | --- | --- |
| `npm run lint` | 鉁?| 浠?typescript-estree 鐗堟湰鎻愮ず |
| `npm run test:unit` | 鉁?| 2 涓敤渚嬪叏閮ㄩ€氳繃 |
| `npm run build` | 鉁?| 浠?asset size warning |
| `scripts/windows/smoke-test.ps1` | 鉁?| HTTP/Gateway/Frontend + Docker/Sentinel/Seata 检查全部通过?|
| 鎵嬪伐鍐掔儫 | 鉁?| 涓嬪崟/鍔犺彍/閫氱煡鎺ㄩ€佽矾寰勫凡鍦?QA 璁″垝涓墽琛?|

## 6. 閮ㄧ讲姝ラ锛堟憳瑕侊級

1. `scripts/windows/docker-up.ps1 -PullImages` 鍚姩渚濊禆銆?2. `scripts/windows/start-backend.ps1` 鍚姩鍥涗釜寰湇鍔★紙鍙檮甯?`-IncludeSkyServer`锛夈€?3. `scripts/windows/start-frontend.ps1 -Mode build -StartNginx` 鐢熸垚鐢熶骇鍖呭苟鍚屾鍒?`nginx/html/sky`锛屾垨鐢?`-Mode serve` 杩涘叆寮€鍙戞ā寮忋€?4. `scripts/windows/smoke-test.ps1` 鏍￠獙鎺ュ彛銆佺鍙ｄ笌 Docker 瀹瑰櫒銆?5. 渚濇鎵ц QA 娴佺▼涓庝笟鍔￠獙鏀躲€? 
