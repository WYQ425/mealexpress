# MealExpress 环境盘点（进行中）

更新时间：2025-10-24
负责角色：Coordinator

## 1. 开发工具链
| 组件 | 检测结果 | 备注 |
| --- | --- | --- |
| 操作系统 | Windows 10 x64 | 本地单机环境 |
| JDK | 1.8.0_451 | 系统环境已切换到 Java 8 |
| Maven | 3.9.10 | 使用 D:\tool\apache-maven-3.9.10 |
| Node.js | 16.20.2 | `nvm use 16.20.2`，与 Vue 2/Element UI 兼容 |
| npm | 8.19.4 | 与 Node 16 搭配使用 |
| Yarn | Corepack 提供 (`corepack enable && yarn import`) | 仅用于生成 `yarn.lock`，不强制安装全局 Yarn |
| JSON 库 | Jackson (通过自定义封装替换 FastJson) | `sky-common/src/main/java/com/alibaba/fastjson/*` 提供兼容包装 |

## 2. 容器与中间件
| 项目 | 检测结果 | 备注 |
| --- | --- | --- |
| Docker Engine | 28.5.1 | `docker --version` |
| Docker Compose | v2.40.2-desktop.1 | `docker compose version` |
| 自定义网络 | 存在 `projects_dev-network`，子网 172.21.0.0/16 | `docker network inspect projects_dev-network` |
| docker-compose.yml | 位于 D:\projects\docker-compose.yml | 已新增 sentinel-dashboard 服务 |

**运行中容器概览（docker ps）**
- mysql (MySQL 8.0) – 3306/tcp，healthy
- redis (Redis 7-alpine) – 6379/tcp，healthy
- elasticsearch (7.17.9) – 9200/9300，healthy
- kibana (7.17.9) – 5601/tcp，healthy
- nacos (v2.0.4) – 8848/9848，healthy
- seata-server (1.5.2) – 8091/tcp，running
- rocketmq-namesrv (4.9.4) – 9876/tcp，running
- rocketmq-broker (4.9.4) – 10909/10911/tcp，running
- sentinel-dashboard (1.8.6) – 8858/tcp，running

## 3. 微服务端口规划
| 服务 | 应用名 | 端口 |
| --- | --- | --- |
| Gateway | gateway-service | 8088 |
| 用户服务 | user-service | 8081 |
| 商品服务 | product-service | 8082 |
| 订单服务 | order-service | 8083 |

## 4. 待补充项
- IDEA / VS Code 版本信息
- 前端依赖：确认是否需安装 Yarn 或 PNPM
- MySQL / Redis 数据卷详情与备份策略
- RocketMQ namesrv / broker 主题、消费组配置
- Docker Desktop 资源限制（CPU / 内存）

## 5. 端口调整记录
- 现有本地后端占用 8080，Gateway 已改用 8088；前端本地代理与 Nginx 已同步指向新端口（2025-10-26）。

> 发现的风险与缺口将同步至 `todo.md` 对应 P0 事项。
