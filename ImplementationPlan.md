# MealExpress 升级实施方案

## 1. 背景与目标
- **目标**：依据 `plan.md` 将单体 `sky-server` 改造为 Spring Cloud Alibaba 微服务体系，同时保持 Windows 本地环境可一键启动。
- **范围**：后端服务拆分与升级、前端与 Nginx 调整、Docker Desktop 依赖栈校准、数据库与 RocketMQ 验证、测试与文档完善。
- **约束**：全部在本机（Windows）执行；沿用现有 Docker 网络 `projects_dev-network`；保留端口布局与数据安全。

## 2. 角色与交付物
| 角色 | 负责人 | 核心交付物 |
| --- | --- | --- |
| Coordinator | 主代理 | `todo.md`、里程碑跟踪、风险关闭 |
| BE-Agent | 主代理协调下的子任务 | 拆分后的微服务代码、升级后的 Maven 依赖、测试用例、`scripts/windows/start-backend.ps1` |
| FE-Agent | 同上 | 前端依赖升级、构建产物、`scripts/windows/start-frontend.ps1`、Nginx 配置 |
| DevOps-Agent | 同上 | Compose 校准、`scripts/windows/docker-up.ps1`/`docker-down.ps1` |
| DB-Agent | 同上 | `docs/db/` 下的迁移脚本、`docs/DB_Migration.md` |
| MQ-Agent | 同上 | RocketMQ 连通性脚本、验证报告 |
| QA-Agent | 同上 | `qa/TestPlan.md`、冒烟脚本 |
| Docs-Agent | 同上 | `docs/Inventory.md`、`docs/ReleaseNotes_LOCAL.md`、`docs/Runbook_LOCAL.md`、README 更新 |

> 所有角色通过根目录 `todo.md` 同步状态，完成后勾选并附链接。

## 3. 范围拆解
- **后端微服务化 (P0)**：新增 `gateway`, `user-service`, `product-service`, `order-service` 等 Maven 模块；接入 Nacos、OpenFeign、Sentinel、Seata；替换 FastJson 为 Jackson。
- **前端&Nginx (P0)**：核对 Node 版本，更新依赖；本地构建并生成 Nginx 静态部署；维护 Windows 兼容的 `nginx/conf.d/mealexpress.conf`。
- **DevOps 环境 (P0)**：复核 `D:\projects\docker-compose.yml` 中的 MySQL/Redis/Nacos/Sentinel/RocketMQ 等服务；提供启动/停止脚本。
- **数据库迁移 (P0)**：梳理拆分后所需的新库/表/字段；形成迁移 SQL、回滚方案；更新 `sky.sql` 与 `数据库设计文档.md`。
- **消息队列 (P1)**：验证 RocketMQ namesrv/broker 连通性，准备主题与消费组自检脚本。
- **测试验证 (P0)**：补齐单元测试、集成测试与冒烟脚本；QA 清单覆盖关键接口。
- **文档交付 (P0)**：库存清单、发布说明、运行手册、README 补充“一键运行”。
- **扩展能力 (P2)**：Elasticsearch、SkyWalking、Spring Boot Admin 暂列未来任务，仅保留规划占位。

## 4. 里程碑计划
| 里程碑 | 目标完成时间 | 验收标准 |
| --- | --- | --- |
| M1 基线梳理 | Day 1 | ImplementationPlan.md & todo.md 建立；git 分支 `upgrade/local-20251024` 创建 |
| M2 环境盘点 | Day 1 | `docs/Inventory.md` 首版，列出版本与健康状态 |
| M3 微服务框架落地 | Day 4 | 后端模块拆分完成，基础功能可在本地启动，单元测试通过 |
| M4 前端与 Nginx 流程 | Day 5 | 本地前端构建 + Nginx 静态服务可访问 |
| M5 基础设施脚本 | Day 5 | `scripts/windows/*.ps1` 覆盖 Docker、后端、前端、冒烟测试 |
| M6 综合验证 | Day 6 | QA 清单全部打勾；端到端冒烟通过 |
| M7 文档收尾 | Day 6 | ReleaseNotes_LOCAL、Runbook_LOCAL、README 更新提交 |

（如进度受阻，Coordinator 在 `todo.md` 调整优先级并记录原因。）

## 5. 工作流阶段
1. **基线阶段**：读取现状、补足 ImplementationPlan、todo；锁定风险与资源。
2. **环境探测**：识别 JDK/Node/Maven/NPM/Docker 版本、可用端口；更新 Inventory 与待办。
3. **服务拆分**：并行创建网关与各业务服务模块；完成依赖升级、配置拆分、跨服务调用；替换 FastJson。
4. **Infrastructure Ready**：Compose、Nacos、Sentinel、Seata、RocketMQ、本地数据库准备就绪。
5. **前端联调**：修订代理地址、环境变量；构建并通过 Nginx 配置静态站点；联通后端网关。
6. **测试与验收**：BE/FE 回归 → QA 汇总结果；修复缺陷循环直至全绿。
7. **文档与发布**：Docs-Agent 汇总差异、运行步骤、排障、回滚策略；更新 README 与 release notes。

## 6. 关键依赖与假设
- 使用现有 Docker Desktop 与 `projects_dev-network`，无需重新创建网络。
- MySQL/Redis 等数据可在容器中持久化，升级前先导出现有业务数据。
- 本地 JDK 维持 8；若检测到更高版本，需验证兼容性或设置项目 JDK。
- RocketMQ broker 已部署，子任务仅需验证连通性与主题。
- 前端构建需 Node >= 16；若当前版本不符，需在 `todo.md` 标记并计划升级。

## 7. 风险与应对
| 风险 | 影响 | 等级 | 应对措施 |
| --- | --- | --- | --- |
| Maven 多模块拆分导致启动失败 | 阻塞后端 | 高 | 先在分支创建最小骨架服务，逐步迁移业务模块并保持单元测试通过 |
| 依赖升级冲突（如 Sentinel、Seata） | 构建失败 | 高 | 使用 BOM 管理版本，记录冲突解决方案于 `docs/Inventory.md` |
| FastJson 替换破坏序列化 | 业务回归失败 | 高 | 增加序列化单测，保留备选转换工具 |
| Docker 资源不足 | 服务无法全部启动 | 中 | 文档中列出资源需求，提供关停脚本 |
| Nginx Windows 权限问题 | 前端无法提供服务 | 中 | 预先验证路径映射，脚本中添加权限检测 |
| RocketMQ 主题未创建 | 消息链路失败 | 中 | MQ-Agent 提供自动化创建/校验脚本 |
| 数据迁移误操作 | 数据丢失 | 高 | DB-Agent 编写备份脚本，演练恢复流程 |

## 8. 回滚策略
1. **代码回滚**：所有改动集中在 `upgrade/local-20251024` 分支，必要时通过 Git 重置到 `main`。
2. **数据库回滚**：迁移前执行全量备份；每个 SQL 附 `ROLLBACK` 脚本，记录在 `docs/DB_Migration.md`。
3. **配置回滚**：对 `application*.yml`、Nginx 配置进行备份副本，脚本中支持恢复。
4. **Docker 服务**：保留原 compose 镜像标签，升级时先拉取新标签，验证通过再替换。

## 9. 沟通与跟踪
- `todo.md`：按 P0/P1/P2 分类；每次任务进展勾选并附链接（文档/脚本/提交）。
- `docs/Inventory.md`：记录探测结果、组件版本、异常。
- 每个子代理的输出路径固定：`backend/`, `frontend/`, `scripts/`, `docs/`, `qa/`, `db/`, `mq/` 等，便于归档。

## 10. 下一步
1. 初始化 `todo.md`（首版待办与优先级）。
2. 开展环境探测（JDK、Node、Docker、Compose、现有容器状态）。
3. 依据探测结果细化子代理任务，启动并行实施。

