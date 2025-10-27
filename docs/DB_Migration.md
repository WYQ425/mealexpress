# MealExpress 鏁版嵁搴撹縼绉绘墜鍐?
鏈墜鍐岄潰鍚戝湪鍗曟満锛圵indows锛夌幆澧冧腑鍗囩骇 MealExpress 骞冲彴鐨勬暟鎹簱缁存姢浜哄憳锛岀洰鏍囨槸鍦ㄤ繚鐣欐棦鏈変笟鍔℃暟鎹殑鍓嶆彁涓嬶紝灏嗗崟浣撳簱骞虫粦杩佺Щ鍒板井鏈嶅姟鎷嗗垎鍚庣殑鍩虹璁炬柦锛圡ySQL + Redis + RocketMQ + Nacos + Seata锛夈€俙sky.sql` 涓庢牴鐩綍鐨勩€婃暟鎹簱璁捐鏂囨。.md銆嬩粛鐒舵槸缁撴瀯鐨勨€滃崟涓€浜嬪疄鏉ユ簮鈥濓紝鏈墜鍐屽垯鑱氱劍鈥滃浣曞畨鍏ㄥ湴钀藉湴鈥濄€?
## 1. 杩佺Щ鑼冨洿涓庡璞?
| 鏈嶅姟 | 闇€瑕佺殑鏍稿績琛?| 澶囨敞 |
| --- | --- | --- |
| user-service | `user`, `address_book`, `shopping_cart` | 璐熻矗 C 绔敤鎴峰強鍦板潃绨裤€佽喘鐗╄溅 |
| product-service | `category`, `dish`, `dish_flavor`, `setmeal`, `setmeal_dish`, `setmeal_category` | 娑电洊鑿滃搧銆佸椁愬強鍒嗙被 |
| order-service | `orders`, `order_detail`, `order_status_log`, `payment_record` | 璐熻矗涓嬪崟銆侀厤閫併€佹敮浠樼姸鎬?|
| sky-server锛堝悗鍙帮級 | `employee`, `role`, `permission`, `operation_log` | 鑻ヤ粛淇濈暀鍚庡彴 Monolith锛屽彲缁х画鍏卞簱 |

Redis 缁х画缂撳瓨锛氶獙璇佺爜銆佺櫥褰曟€併€佸簵閾鸿惀涓氱姸鎬併€佺儹鐐硅彍鍝併€俁ocketMQ 鎵挎媴璁㈠崟閫氱煡/钀ラ攢鎺ㄩ€併€侼acos/Seata 涓庝笟鍔℃暟鎹棤鑰﹀悎锛屼絾闇€瑕佸湪杩佺Щ鍓嶇‘璁よ处鍙蜂俊鎭€?
## 2. 杩佺Щ鍓嶆鏌ユ竻鍗?
1. **鐜鍋ュ悍**锛歚scripts/windows/docker-up.ps1 -SkipHealthChecks:$false` 宸叉垚鍔熸墽琛岋紝`docker ps` 涓?mysql/redis/nacos/rocketmq/seata 鍧囦负 running/healthy銆?2. **澶囦唤绛栫暐**锛氬畬鎴愪互涓嬫搷浣滃苟璁板綍璺緞锛? 
   - `mysqldump -uroot -p --databases sky_take_out > backup/sky_take_out_$(Get-Date -Format yyyyMMddHHmm).sql`  
   - Redis锛歚redis-cli --rdb backup/redis-$(Get-Date -Format yyyyMMddHHmm).rdb`
3. **鏉冮檺閰嶇疆**锛氬皢 `application-dev.yml` 涓殑鏁版嵁搴撱€丱SS銆乄echat銆丅aidu 绛夊瘑閽ラ€氳繃鐜鍙橀噺浼犲叆锛岄伩鍏嶆槑鏂囦繚瀛樺湪閰嶇疆鏂囦欢涓紙鍙傝鏈鎻愪氦锛夈€?4. **鍋滄満绐楀彛**锛氬仠姝㈡棫鐗堝簲鐢紝纭繚鏃犳柊浜嬪姟鍐欏叆锛堝彲閫氳繃 `scripts/windows/start-backend.ps1` 鐨勫悇绐楀彛鍏抽棴锛夈€?
## 3. 杩佺Щ姝ラ

### Step 1. 寤哄簱寤鸿〃

```powershell
cd D:\projects\mealexpress
mysql -uroot -p < sky.sql
```

濡傞渶澧為噺鍗囩骇锛岃瀵规瘮 `sky.sql` 涓庣嚎涓?schema锛岃ˉ榻愮己澶卞瓧娈碉紙绀轰緥锛歚orders.pay_method`銆乣dish.category_id` 绛夋柊澧炲瓧娈碉級銆傚缓璁娇鐢?`mysqldiff` 鎴?Navicat 鐨勭粨鏋勫姣斿姛鑳界敓鎴?ddl銆?
### Step 2. 鎸夋湇鍔℃媶鍒嗗垵濮嬪寲鏁版嵁

1. **鐢ㄦ埛鍩?*锛?   ```sql
   CREATE DATABASE IF NOT EXISTS meal_user DEFAULT CHARACTER SET utf8mb4;
   INSERT INTO meal_user.user SELECT * FROM sky_take_out.user;
   INSERT INTO meal_user.address_book SELECT * FROM sky_take_out.address_book;
   ```
   閰嶇疆 `mealexpress-user-service/src/main/resources/application.yml` 鎸囧悜 `meal_user`銆?
2. **鍟嗗搧鍩?*锛氬悓鐞嗘嫹璐濊彍鍝併€佸椁愩€佸彛鍛崇瓑琛ㄥ埌 `meal_product`锛堜繚鎸佷笌 `product-service` 鏁版嵁婧愪竴鑷达級銆?
3. **璁㈠崟鍩?*锛氬皢璁㈠崟涓庤鍗曟槑缁嗚縼绉诲埌 `meal_order`銆傚鍚敤 Seata锛屽彲鍦?`seat_server.undo_log` 棰勫缓 undo 琛ㄣ€?
> 鑻ユ殏涓嶆媶搴擄紝鍙部鐢ㄥ崟搴?`sky_take_out`锛屼粎纭繚鎵€鏈夎〃/瀛楁榻愬叏銆傛枃妗ｄ粛寤鸿鍒嗗簱鏄负浜嗗悗缁笂浜戝仛鍑嗗銆?
### Step 3. Redis/MQ 鏁版嵁鍑嗗

1. Redis锛氭墽琛?`redis-cli FLUSHALL` 浠ユ竻鐞嗘棫缂撳瓨锛屽啀閫氳繃搴旂敤鑷姩鍒濆鍖栥€傞渶瑕佸瘑鐮佺殑鍦烘櫙锛屽湪 `application-dev.yml` 涓敼涓?`REDIS_PASSWORD` 鐜鍙橀噺銆?2. RocketMQ锛氱‘璁?`rocketmq-namesrv`銆乣rocketmq-broker` 杩愯鍚庯紝鎵ц绀轰緥锛堣瑙?`scripts/windows/smoke-test.ps1`锛夛細
   ```powershell
   docker exec -it rocketmq-broker sh -c "mqadmin updateTopic -n rocketmq-namesrv:9876 -t mealexpress_order -c DefaultCluster"
   ```

### Step 4. 鍥炲～涓庨獙璇?
1. 杩愯 `scripts/windows/start-backend.ps1 -Services gateway,user-service,product-service,order-service`銆?
2. 瀹夎骞朵紶鍏?`SEATA_SERVER_ADDR` / `SEATA_TX_GROUP`锛堝煎鍐欎负 `mealexpress_tx_group` -> `127.0.0.1:8091`锛夛紝鍙€夋祴鍊煎湪 Seata Server 鏄剧ず鍚堟垚鍔?
3. 鎵ц `scripts/windows/smoke-test.ps1`锛岀‘璁?`/actuator/health`銆乣/api` 浠ｇ悊銆丯ginx 鍓嶇鍙敤锛佹牴鎹?Sentinel Dashboard 鍖呭惈鐨勮鑹插噯浜哄畬鎴愬崟娴佹竻鏂?
4. 鎶芥牱楠岃瘉鏍稿績涓氬姟锛氬垱寤虹敤鎴?-> 鍦板潃 -> 涓嬪崟 -> 鏀粯妯℃嫙锛堝彲浣跨敤 `qa/TestPlan.md` 鐨勫満鏅級銆?
## 4. 鍥炴粴鏂规

鑻ヨ縼绉诲け璐ワ細

1. 鍋滄鏂扮増鏈湇鍔★紱`scripts/windows/docker-down.ps1` 鍙敤浜庡揩閫熷叧闂緷璧栥€?2. 閫氳繃澶囦唤鏂囦欢鎭㈠锛?   ```powershell
   mysql -uroot -p < backup/sky_take_out_xxx.sql
   redis-cli --pipe < backup/redis-xxx.rdb
   ```
3. 鎭㈠鏃х増搴旂敤锛堝彲閲嶆柊鍚姩 `sky-server` 骞舵寚鍚戞棫搴擄級銆?
## 5. 璁板綍涓庝氦鎺?
- 瀹屾垚杩佺Щ鍚庡湪 `docs/Inventory.md` 鏇存柊鏁版嵁搴撱€丷edis銆丷ocketMQ銆丯acos 鐨勭増鏈笌璁块棶鏂瑰紡銆?- 灏?`DB_Migration.md` 鐨勬墽琛屾壒娆°€佹搷浣滀汉銆佸浠戒綅缃€侀獙鏀惰瘉鎹檮鍦?`docs/Runbook_LOCAL.md` 鐨勨€滃彉鏇磋褰曗€濊〃涓紝鏂逛究瀹¤銆? 
