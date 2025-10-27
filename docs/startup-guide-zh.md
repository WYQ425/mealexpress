# MealExpress 本地启动指南

## 1. 前置条件检查
- **Docker Desktop**：切换到 Linux 容器模式并已启动，建议在设置中分配 ≥10 GB 内存。
- **JDK 8**：`java -version` 应输出 `1.8.x`；若使用多版本管理器，请在当前 PowerShell 会话里确认。
- **Maven**：系统 PATH 中可以运行 `mvn -v`；脚本也会自动 fallback 到各模块内的 `mvnw.cmd`。
- **Node.js 16 + npm 8**：`node -v` ≈ 16.x，`npm -v` ≈ 8.x；若是初次安装，务必重开终端。
- **PowerShell 执行策略**：执行 `Set-ExecutionPolicy -Scope Process RemoteSigned`，确保 `.ps1` 可运行。
- **环境变量**：根据实际账号准备，示例命令（执行后需重开终端）：
  ```powershell
  setx MYSQL_USERNAME root
  setx MYSQL_PASSWORD 1010
  setx SENTINEL_USERNAME sentinel
  setx SENTINEL_PASSWORD sentinel
  ```

## 2. 启动基础设施（MySQL / Redis / Nacos / RocketMQ / Seata / Sentinel）
在仓库根目录 `D:\projects\mealexpress`：
```powershell
./scripts/windows/docker-up.ps1           # 首次建议追加 -PullImages
```
脚本会基于 `D:\projects\docker-compose.yml` 拉起容器，并逐一等待健康检查通过；全部就绪后提示 “Infrastructure stack is up.”。

## 3. 启动后端 Spring Boot 服务
默认会启动四个微服务（端口：gateway=8088、user=8081、product=8082、order=8083）：
```powershell
./scripts/windows/start-backend.ps1 -Services gateway,user-service,product-service,order-service
```
- 每个服务会在独立的 PowerShell 窗口执行 `mvn spring-boot:run -DskipTests`。
- 若需要旧版 `sky-server` 一并运行，添加 `-IncludeSkyServer`（使用 8080 端口）。
- 如端口被占用，可先停止冲突服务或在对应模块的 `application-dev.yml` 内修改端口，并以 `-SkipPortCheck` 跳过校验。

## 4. 启动前端与 Nginx
生产模式构建并同步到内置 Nginx（仅首次运行或依赖更新时加 `-InstallDependencies`）：
```powershell
./scripts/windows/start-frontend.ps1 -Mode build -StartNginx -InstallDependencies
```
- 构建完成后会将 `project-sky-admin-vue-ts/dist` 拷贝到 `nginx-1.20.2/html/sky`，并启动 `nginx.exe`（默认 80 端口）。
- 若进入开发模式则改用 `-Mode serve`，会启动 Vue CLI Dev Server（默认 9528），无需 Nginx。

## 5. 冒烟验证
基础服务启动后，可运行一键体检脚本：
```powershell
./scripts/windows/smoke-test.ps1
```
脚本会检查：
- Gateway / User / Product / Order `/actuator/health`
- Nginx 静态站点（http://localhost/）
- MySQL、Redis、Nacos、RocketMQ、Seata、Sentinel 容器状态
- RocketMQ 自检脚本输出（发送/消费消息）

全部通过后即可开始业务联调；如有失败，按脚本日志提示排查对应容器或服务日志。

## 6. 日常访问入口
- 管控后台：`http://localhost/`（Nginx 80 端口）
- Gateway API：`http://localhost:8088/`
- Sentinel Dashboard：`http://localhost:8858/`（默认账号密码 `sentinel/sentinel`）
- Nacos 控制台：`http://localhost:8848/nacos/`
- RocketMQ Console（如已启用）：`http://localhost:8080/`

## 7. 停止服务与清理
1. 前端（Nginx）：执行 `nginx-1.20.2\nginx.exe -s stop`，或直接结束进程。
2. 后端：关闭各个 `start-backend.ps1` 打开的 PowerShell 窗口，或在窗口中 `Ctrl+C`。
3. 基础设施：在仓库根目录执行
   ```powershell
   ./scripts/windows/docker-down.ps1           # 如需删除数据卷，追加 -RemoveVolumes
   ```
4. 如需释放磁盘，可定期运行 `docker system prune --volumes`，不过请先确认已备份数据库。

按照以上顺序操作即可在本地完整启动、验证并关闭 MealExpress 全栈环境。若遇到问题，优先检查对应脚本输出及容器日志（`docker logs <container>`）。祝开发顺利！
