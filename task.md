# 角色与目标
你是本项目的“升级实施负责人”。这是一个已完成可运行的 Java 单体项目，所有环境均在同一台 Windows 机器上本地运行：
- 后端：Windows + IntelliJ IDEA 开发/运行
- 前端：Windows + VS Code 开发/运行，打包后通过 Nginx 本地部署
- 依赖：全部本机容器化（Windows 的 Docker Desktop）；docker-compose 位于：
  D:\projects\docker-compose.yml
- RocketMQ：namesrv 在 compose 网络中；broker 独立运行，命令如下（已部署，无需改动，若需验证仅做连通性与配置检查）：
  docker run -d --name rocketmq-broker --restart always --network projects_dev-network -p 10909:10909 -p 10911:10911 -e "NAMESRV_ADDR=rocketmq-namesrv:9876" -e "JAVA_OPT_EXT=-server -Xms256m -Xmx256m" -e "TZ=Asia/Shanghai" -v rocketmq_broker_data:/home/rocketmq/logs -v rocketmq_broker_store:/home/rocketmq/store apache/rocketmq:4.9.4 sh mqbroker -n rocketmq-namesrv:9876

约束：不涉及 Linux 服务器上线；只在本机（Windows）完成升级、构建、验证与本地部署。除非必要，不修改现有容器端口映射与网络名（projects_dev-network）。

# 任务要求（先读再做）
1) 打开并通读根目录的 plan.md，基于其中的升级目标与范围，生成一个“可执行实施方案”并落地到仓库。
2) 使用子代理并行推进：为不同职责创建子代理并由你统筹调度与合并成果。
3) 在仓库根目录创建/维护 todo.md（待办清单），按优先级分组，**实时更新**完成进度（勾选、追加备注、链接到变更/脚本/配置）。
4) 全流程围绕“本地可跑通”为准，完成后可一键本地启动并通过 Nginx 提供前端静态资源访问。

# 子代理规划（并行执行，彼此用 todo.md 同步）
- Coordinator（总协调，主代理自己承担）：
  - 读取 plan.md → 产出 ImplementationPlan.md（实施方案）并在 todo.md 建立首版清单与优先级。
  - 负责子代理工单拆分、依赖管理、合并 PR/变更、里程碑达成与风险关闭。
- BE-Agent（后端升级）：
  - 自动识别构建系统（Maven/Gradle）、JDK/框架版本、依赖树与风险。
  - 按 plan.md 的目标做版本升级（含兼容性迁移、代码调整、配置变更），保留可回滚提交。
  - 增补或修复单元/集成测试；提供本地一键运行脚本（Windows 优先，PowerShell .ps1）。
- FE-Agent（前端与 Nginx）：
  - 检测 Node/Yarn/NPM 版本与前端构建脚本；做必要的依赖升级与构建修复。
  - 产出本地构建产物，生成/校验 Nginx 配置（Windows 路径与权限），提供一键本地启动脚本。
- DevOps-Agent（Docker Desktop & Compose）：
  - 校验 D:\projects\docker-compose.yml 的服务健康与互联（含数据库、缓存、namesrv 等）。
  - 只在需要时做“向后兼容”的安全升级（镜像 tag、环境变量、卷映射）、补充 .env 样例。
  - 验证 projects_dev-network 存在且连通；提供本地一键启动/停止脚本（.ps1）。
- DB-Agent（数据库迁移）：
  - 基于 plan.md 的 schema/数据变更，生成迁移脚本（如 Flyway/Liquibase/SQL 脚本），先在本地容器 DB 演练。
  - 给出备份/回滚策略与校验 SQL；把步骤写入 docs/DB_Migration.md。
- MQ-Agent（RocketMQ 校验）：
  - 仅做连通性与主题/消费组自检（基于 NAMESRV_ADDR=rocketmq-namesrv:9876）。
  - 提供本地自测小程序或脚本验证发送/消费链路（不更改现有 broker 端口与部署方式）。
- QA-Agent（验证与回归）：
  - 输出测试清单（功能/接口/回归/冒烟），整理可复现指令和预期结果。
  - 将关键校验加入一键脚本；在 todo.md 中以复选框跟踪通过率。
- Docs-Agent（文档与输出物）：
  - 汇总升级差异、运行说明、已知问题与规避方案；生成 ReleaseNotes_LOCAL.md 与 Runbook_LOCAL.md。
  - 在 README 中追加“Windows 本地一键运行”段落与排障指引。

# 执行顺序（先后次序）
1) 基线梳理：
   - 读取 plan.md → 生成 ImplementationPlan.md（含范围、风险、里程碑、回滚策略）。
   - 新建分支：upgrade/local-{日期}；初始化 todo.md（优先级 P0/P1/P2，按模块分组）。
2) 环境探测与快速健康检查（只读或安全命令）：
   - 构建系统类型、JDK/Node 版本、依赖树、Docker Desktop/Compose 版本与容器状态。
   - 记录探测结果到 docs/Inventory.md，并把风险项加进 todo.md。
3) 子代理并行落地（BE/FE/DevOps/DB/MQ/QA/Docs）：
   - 每个子代理产出对应文件夹或文档（例如 scripts、docs、nginx、db/migration、qa/…），提交小步快跑。
   - 每完成一个子任务：更新 todo.md 的复选框、补充“如何复现/验证”的链接。
4) 本地一键化：
   - 生成 scripts/windows/*.ps1：compose 启停、后端启动、前端构建与 Nginx 启动、端到端冒烟。
   - 在 README 追加“一键命令与访问地址”。
5) 端到端验证与收尾：
   - QA-Agent 执行清单；Coordinator 汇总问题并推动修复直至通过。
   - Docs-Agent 生成 ReleaseNotes_LOCAL.md、Runbook_LOCAL.md；在 PR 描述中引用。

# 交付物与验收
- ImplementationPlan.md：对齐 plan.md 的可执行方案（含影响面/回滚）。
- todo.md：实时更新的待办清单（带勾选与链接），最终应全部勾选。
- scripts/windows/*.ps1：本地一键启停/构建/验证脚本（包含注释与错误处理）。
- README 更新：新增“Windows 本地一键运行与排障”。
- docs/Inventory.md、ReleaseNotes_LOCAL.md、Runbook_LOCAL.md、（若有）db 迁移脚本与验证记录。
- 可在本机完成：Docker 依赖就绪 → 后端启动 → 前端经 Nginx 提供服务 → MQ/DB 正常 → 冒烟通过。

# 操作准则
- 默认在当前仓库工作区内读写文件；优先使用小提交并含语义化信息（feat:, fix:, chore:, docs:, build:, refactor:）。
- 除非会破坏数据或越权，不需要逐步确认；遇到不可逆或高风险操作才请求我确认。
- 所有脚本与配置要兼容 Windows 路径与 PowerShell（必要时提供 .bat 备选）。
- 不引入 Linux 专用路径或 systemd 等与本环境无关的内容。

开始执行：先读取 plan.md → 输出 ImplementationPlan.md（首版）→ 建立/填充 todo.md（首版）→ 开始子代理并行工作，并在 todo.md 实时更新。