# MealExpress 升级待办清单

> **更新准则**：按优先级分组，完成后立即勾选并补充结果链接或备注。默认负责人对应原子代理角色。

## P0（已完成）
- [x] Coordinator：产出 `ImplementationPlan.md` 并创建分支 `upgrade/local-20251024`（完成者：Codex，2025-10-24）
- [x] Coordinator：完成环境盘点并记录至 `docs/Inventory.md`，包含 Docker、网络、前端工具链等（2025-10-24）
- [x] BE-Agent：初始化 gateway/user/product/order 微服务模块，`mvn clean package -DskipTests` 通过（2025-10-24）
- [x] BE-Agent：完成 FastJson → Jackson 迁移与兼容适配，`mvn test` 通过（2025-10-24）
- [x] FE-Agent：核对 Node/NPM 版本与依赖风险，结果同步到 `docs/Inventory.md`，并对 Gateway 端口做基线确认（2025-10-24）
- [x] FE-Agent：完成前端构建并输出 Nginx 静态站点，编写 `scripts/windows/start-frontend.ps1`
- [x] DevOps-Agent：审查 `D:\projects\docker-compose.yml`，补全 `scripts/windows/docker-up.ps1` / `docker-down.ps1`
- [x] DevOps-Agent：验证 `projects_dev-network` 存在且可用，记录联通性检查方法
- [x] DB-Agent：梳理数据库拆分与迁移策略，落地 `docs/DB_Migration.md` 初版
- [x] MQ-Agent：完成 RocketMQ 连通性检查与验证记录
- [x] QA-Agent：输出冒烟 / 回归测试清单 `qa/TestPlan.md`
- [x] Docs-Agent：整理 `docs/ReleaseNotes_LOCAL.md` 与 `docs/Runbook_LOCAL.md` 初版
- [x] Scripts：归档一键运行脚本至 `scripts/windows/` 并补充基础错误处理
- [x] README：新增 “Windows 本地一键运行与排障” 章节

## P1（高优先级保障项）
- [x] BE-Agent：引入 Seata 并配置分布式事务与回滚策略
- [x] BE-Agent：接入 Sentinel 并为关键接口提供限流处理
- [x] FE-Agent：统一前端到 Gateway 的 `/api` 代理策略（含开发、Nginx 与文档）
- [x] MQ-Agent：准备并验证 RocketMQ 主题/消费组自检脚本（`mq-self-test.ps1`）
- [x] QA-Agent：将关键校验整合进 `smoke-test.ps1`（含 RocketMQ）并更新 QA/Runbook 描述

## P2（后续增强）
- [x] BE-Agent：预研 Elasticsearch 检索能力，输出方案到 `docs/Runbook_LOCAL.md`（完成者：Codex，2025-10-27）
- [x] DevOps-Agent：规划 SkyWalking / Spring Boot Admin 引入路径（完成者：Codex，2025-10-27）
- [x] Docs-Agent：整理中长期运维策略（值班、容量、升级频率）（完成者：Codex，2025-10-27）
