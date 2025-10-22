# CLAUDE.md

该文件为 Claude Code (claude.ai/code) 在此仓库中工作时提供指导。

## 项目概述

MealExpress（原名苍穹外卖）是一个在线订餐平台，包含以下组件：
- **后端**：Spring Boot 2.7.3 单体应用，采用多模块 Maven 结构
- **管理端前端**：Vue 3 + TypeScript（位于 `project-sky-admin-vue-ts/`）
- **微信小程序**：基于 uni-app（位于 `wechatproject/`）
- **数据库**：MySQL，包含 11 个核心数据表
- **缓存**：Redis 用于缓存和会话管理

## 架构设计

### 模块结构

项目采用多模块 Maven 架构：

1. **sky-common**：共享工具类、常量、异常和公共组件
   - JWT 认证工具
   - JSON 处理工具
   - 自定义异常和结果包装器
   - OSS（对象存储服务）集成

2. **sky-pojo**：领域模型和 DTO
   - 实体类（Employee、User、Dish、Order 等）
   - 层间数据传输的 DTO
   - API 响应的 VO（视图对象）

3. **sky-server**：主应用模块
   - 按用户类型划分的控制器：
     - `/admin/*` - 管理系统接口
     - `/user/*` - 客户端接口
     - `/notify/*` - 支付回调接口
   - 包含业务逻辑的服务层
   - MyBatis 数据库操作的 Mapper 层
   - 实时订单通知的 WebSocket 支持
   - 订单管理的定时任务

### 请求流程架构

```
前端请求 → 网关 (端口 8080) → JWT 拦截器 → 控制器 → 服务层 → Mapper层 → 数据库
                                       ↓           ↓
                                   WebSocket    Redis缓存
```

### 业务领域

应用程序围绕以下核心业务领域组织：

1. **用户管理**：员工和客户用户系统，分别认证
2. **商品管理**：分类、菜品和套餐定价管理
3. **订单管理**：购物车、订单创建和订单生命周期
4. **支付集成**：微信支付集成及回调处理
5. **报表功能**：业务分析和 Excel 导出功能

### 关键集成点

- **阿里云 OSS**：菜品图片和用户头像的文件/图片存储
- **微信服务**：登录认证和支付处理
- **百度地图 API**：地址地理编码和配送距离计算
- **Redis**：会话管理、缓存和店铺状态
- **WebSocket**：向管理端仪表板实时推送订单状态更新

## 数据库架构

核心数据表及其关系：
- `employee` - 管理系统的员工账户
- `user` - 客户账户（微信用户）
- `category` - 菜品和套餐的商品分类
- `dish` & `dish_flavor` - 带自定义选项的菜单项
- `setmeal` & `setmeal_dish` - 套餐组合
- `shopping_cart` - 临时购物车项
- `orders` & `order_detail` - 带明细的订单记录
- `address_book` - 客户配送地址

## 配置管理

应用程序使用 Spring profiles 进行环境特定配置：
- `application.yml` - 基础配置
- `application-dev.yml` - 开发环境
- `application-prod.yml` - 生产环境

关键配置区域：
- 数据库连接（`sky.datasource.*`）
- Redis 连接（`sky.redis.*`）
- JWT 密钥（`sky.jwt.*`）
- 微信集成（`sky.wechat.*`）
- 阿里云 OSS（`sky.alioss.*`）

## 未来微服务迁移

`plan.md` 中有一份详细计划，将此单体应用重构为使用 Spring Cloud Alibaba 的微服务架构：
- 网关服务 (8080) - API 路由和认证
- 用户服务 (8081) - 用户和员工管理
- 商品服务 (8082) - 菜单和分类管理
- 订单服务 (8083) - 购物车和订单处理
- 支付服务 (8084) - 支付集成
- 搜索服务 (8085) - Elasticsearch 集成

迁移保留所有前端 URL，确保前端无需任何更改。

## 开发注意事项

- 项目使用 MyBatis 作为 ORM，XML 映射文件位于 `src/main/resources/mapper/`
- AutoFill AOP 切面自动填充创建/更新时间戳和用户 ID
- JWT 认证通过拦截器处理管理端和用户端接口
- 应用支持立即配送和预约配送
- WebSocket 连接提供订单状态变更的实时更新
- 所有金额使用 DECIMAL(10,2) 存储以确保精度