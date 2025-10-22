# MealExpress - 在线外卖微服务平台改造指导文档

> **项目原名**: sky-take-out（苍穹外卖）
> **新项目名**: MealExpress
> **改造目标**: 单体应用 → Spring Cloud Alibaba微服务架构
> **开发环境**: Windows + IDEA + Java 8
> **文档版本**: v1.0
> **最后更新**: 2025-10-10

---

## 一、项目现状分析

### 1.1 当前技术栈

```yaml
基础框架:
  - Spring Boot: 2.7.3
  - Java: 8
  - Maven: 多模块管理

数据访问:
  - MyBatis: 2.2.0
  - MySQL: 数据持久化
  - Druid: 1.2.1 连接池
  - PageHelper: 1.3.0 分页插件

缓存:
  - Redis: 缓存支持
  - (无消息队列)

第三方集成:
  - 阿里云OSS: 3.10.2 文件存储
  - 微信支付: wechatpay-apache-httpclient 0.4.8
  - 百度地图: 地址定位
  - POI: 3.16 Excel报表

安全与文档:
  - JWT (jjwt 0.9.1): 身份认证
  - Knife4j 3.0.2: 接口文档
  - Lombok: 1.18.30 代码简化
  - FastJson: 1.2.76 JSON处理（存在安全漏洞）

项目模块:
  - sky-common: 公共工具类
  - sky-pojo: 实体类/DTO/VO
  - sky-server: 业务核心模块（单体）
```

### 1.2 前端架构

```yaml
管理端:
  路径: project-sky-admin-vue-ts/
  技术栈: Vue 3 + TypeScript + Element UI
  请求配置:
    baseURL: /api (代理到http://localhost:8080)
    实际地址: http://localhost:8080/admin/**
    WebSocket: ws://localhost:8080/ws/

小程序:
  路径: wechatproject/mp-weixin/
  技术栈: uni-app编译的微信小程序
  请求地址: http://localhost:8080/user/**
```

### 1.3 业务域识别

| 业务域 | 核心实体 | 主要功能 | 拆分优先级 |
|--------|---------|---------|-----------|
| **用户域** | User, AddressBook | 用户登录、地址管理 | ⭐⭐⭐⭐⭐ |
| **商品域** | Category, Dish, SetMeal | 分类、菜品、套餐管理 | ⭐⭐⭐⭐⭐ |
| **订单域** | Order, ShoppingCart | 购物车、订单管理 | ⭐⭐⭐⭐⭐ |
| **支付域** | 微信支付集成 | 支付回调、退款 | ⭐⭐⭐⭐ |
| **员工域** | Employee | 员工管理 | ⭐⭐ |

### 1.4 存在的问题

```yaml
架构层面:
  ❌ 单体架构，无法独立扩展
  ❌ 所有业务耦合在一起
  ❌ 简历竞争力不足（缺乏微服务经验）

技术层面:
  ⚠️ FastJson 1.2.76 有安全漏洞（需替换Jackson）
  ⚠️ 无分布式事务处理能力
  ⚠️ 无服务治理能力（限流、熔断）
  ⚠️ 无消息队列（异步解耦不足）

简历呈现:
  ❌ "苍穹外卖"培训班项目特征明显
  ❌ 缺乏微服务实战经验
  ❌ 技术栈基础化
```

---

## 二、微服务改造方案

### 2.1 目标架构图

```
┌──────────────────────────────────────────────────────────┐
│          前端（Vue管理端 + uni-app小程序）                  │
│              http://localhost:8080/...                    │
└───────────────────────┬──────────────────────────────────┘
                        │
┌───────────────────────▼──────────────────────────────────┐
│           Spring Cloud Gateway (网关) :8080               │
│     集成: Sentinel限流 + JWT鉴权 + 路由转发               │
└───────────────────────┬──────────────────────────────────┘
                        │
        ┌───────────────┼──────────┬──────────┬────────┐
        │               │          │          │        │
┌───────▼──────┐ ┌─────▼─────┐ ┌──▼──────┐ ┌▼──────┐ ┌▼───────┐
│user-service  │ │order-     │ │product- │ │payment│ │search- │
│:8081         │ │service    │ │service  │ │service│ │service │
│              │ │:8083      │ │:8082    │ │:8084  │ │:8085   │
│用户/地址     │ │订单/购物车│ │分类/菜品│ │微信   │ │ES搜索  │
└───────┬──────┘ └─────┬─────┘ └──┬──────┘ └┬──────┘ └┬───────┘
        │               │          │         │        │
        └───────────────┼──────────┴─────────┴────────┘
                        │
        ┌───────────────┼────────────────────────────┐
        │               │                            │
┌───────▼───────┐ ┌────▼────────┐ ┌────────────┐ ┌─▼──────────┐
│ Nacos :8848   │ │ Sentinel    │ │ Seata      │ │ RocketMQ   │
│注册+配置中心  │ │流量控制     │ │:8091       │ │:9876,10911 │
│               │ │             │ │分布式事务  │ │消息队列    │
└───────────────┘ └─────────────┘ └────────────┘ └────────────┘
                        │
┌───────────────────────▼──────────────────────────────────┐
│                    基础设施层                              │
│  MySQL(分库) :3306 | Redis :6379 | Elasticsearch :9200  │
└───────────────────────┬──────────────────────────────────┘
                        │
┌───────────────────────▼──────────────────────────────────┐
│                   监控与追踪层                             │
│  SkyWalking(APM) | ELK(日志) | Spring Boot Admin        │
└──────────────────────────────────────────────────────────┘
```

### 2.2 核心技术栈（Java 8版本）

```yaml
微服务基础:
  ✅ Spring Boot: 2.7.3（保持不变，兼容Java 8）
  ✅ Spring Cloud: 2021.0.5
  ✅ Spring Cloud Alibaba: 2021.0.5.0
  ✅ Java: 8

服务治理:
  ✅ Nacos: 2.0.4（注册中心+配置中心）
  ✅ Sentinel: 1.8.6（流量控制、熔断降级）
  ✅ OpenFeign: 3.1.5（声明式HTTP客户端）
  ✅ Gateway: 3.1.5（统一网关）

分布式解决方案:
  ✅ Seata: 1.5.2（分布式事务，AT模式）
  ✅ RocketMQ: 4.9.4（消息队列）
    - 异步解耦
    - 事务消息（订单一致性）
    - 延迟消息（订单超时取消）

数据与搜索:
  ✅ MySQL: 8.0（分库策略）
  ✅ MyBatis-Plus: 3.5.3（ORM框架）
  ✅ Redis: 7.0（缓存 + Redisson分布式锁）
  ✅ Elasticsearch: 7.17.9（全文搜索，最后支持Java 8的版本）
  ✅ Canal: 1.1.6（数据同步 MySQL → ES）

监控与追踪:
  ✅ SkyWalking: 8.9.1（APM链路追踪）
  ✅ Spring Boot Admin: 2.7.10（服务监控）
  ✅ ELK Stack: 日志收集与分析

安全与工具:
  ✅ JWT: 身份认证（保留）
  ✅ Jackson: JSON处理（替换FastJson）
  ✅ Knife4j: 3.0.3 接口文档
```

### 2.3 版本兼容性说明

```yaml
为什么选择这些版本？

Spring Cloud Alibaba 2021.0.5.0:
  - 官方支持 Java 8
  - 兼容 Spring Boot 2.6.x - 2.7.x
  - 官方文档: https://github.com/alibaba/spring-cloud-alibaba

Nacos 2.0.4:
  - 支持 Spring Cloud 2021.x
  - Windows友好，有批处理启动脚本
  - 官网: https://nacos.io/

RocketMQ 4.9.4:
  - 完全支持 Java 8
  - 事务消息、延迟消息完整支持
  - 官网: https://rocketmq.apache.org/

Elasticsearch 7.17.9:
  - 最后一个支持 Java 8 的 7.x 版本
  - IK分词器兼容
  - 官网: https://www.elastic.co/elasticsearch/

关键约束:
  - Elasticsearch 8.x 要求 Java 11+（不可用）
  - Spring Cloud 2022.x 要求 Spring Boot 3+（不可用）
  - RocketMQ 5.x 推荐 Java 11+（不推荐）
```

---

## 三、服务拆分设计

### 3.1 核心服务划分

#### **1. mealexpress-gateway（网关服务）**

```yaml
职责:
  - 统一入口（端口8080，保持不变）
  - 路由转发到各个服务
  - JWT鉴权（保留原有逻辑）
  - Sentinel限流

关键配置:
  server:
    port: 8080  # 必须保持8080，前端零改动

  spring:
    cloud:
      gateway:
        routes:
          # 管理端路由
          - id: admin-employee
            uri: lb://user-service
            predicates:
              - Path=/admin/employee/**

          - id: admin-category
            uri: lb://product-service
            predicates:
              - Path=/admin/category/**

          - id: admin-dish
            uri: lb://product-service
            predicates:
              - Path=/admin/dish/**,/admin/setmeal/**

          - id: admin-order
            uri: lb://order-service
            predicates:
              - Path=/admin/order/**

          # 用户端路由
          - id: user-user
            uri: lb://user-service
            predicates:
              - Path=/user/user/**,/user/addressBook/**

          - id: user-category
            uri: lb://product-service
            predicates:
              - Path=/user/category/**,/user/dish/**,/user/setmeal/**

          - id: user-cart
            uri: lb://order-service
            predicates:
              - Path=/user/shoppingCart/**

          - id: user-order
            uri: lb://order-service
            predicates:
              - Path=/user/order/**

          # WebSocket路由
          - id: websocket
            uri: lb://order-service
            predicates:
              - Path=/ws/**
```

#### **2. mealexpress-user-service（用户服务）:8081**

```yaml
职责:
  - 用户登录（微信授权）
  - 用户信息管理
  - 地址簿管理
  - 员工管理（admin端）

数据库: mealexpress_user

主要表:
  - user（用户）
  - address_book（地址簿）
  - employee（员工）

对外接口（Feign）:
  - GET /user/info/{userId} - 获取用户信息
  - GET /address/default/{userId} - 获取默认地址
```

#### **3. mealexpress-product-service（商品服务）:8082**

```yaml
职责:
  - 分类管理
  - 菜品管理（含口味）
  - 套餐管理
  - 库存管理

数据库: mealexpress_product

主要表:
  - category（分类）
  - dish（菜品）
  - dish_flavor（口味）
  - setmeal（套餐）
  - setmeal_dish（套餐菜品）

对外接口（Feign）:
  - GET /product/dish/{id} - 获取菜品详情
  - POST /product/stock/deduct - 扣减库存（内部调用）
```

#### **4. mealexpress-order-service（订单服务）:8083**

```yaml
职责:
  - 购物车管理
  - 订单创建与管理
  - 订单状态流转
  - WebSocket推送

数据库: mealexpress_order

主要表:
  - shopping_cart（购物车）
  - orders（订单）
  - order_detail（订单明细）

关键特性:
  - Seata分布式事务（下单扣库存）
  - RocketMQ延迟消息（超时取消）
  - WebSocket实时推送
```

#### **5. mealexpress-payment-service（支付服务）:8084**

```yaml
职责:
  - 微信支付集成
  - 支付回调处理
  - 退款处理

数据库: 复用mealexpress_order或独立

关键特性:
  - RocketMQ事务消息（支付状态同步）
```

#### **6. mealexpress-search-service（搜索服务）:8085**

```yaml
职责:
  - 商品全文搜索（Elasticsearch）
  - 数据同步（Canal监听MySQL）

数据源: Elasticsearch

索引: product_index（菜品搜索）
```

### 3.2 数据库拆分方案

```sql
-- 原数据库: sky_takeout

-- 拆分后（Windows MySQL同一实例，不同database）:

-- 用户服务数据库
CREATE DATABASE mealexpress_user DEFAULT CHARSET utf8mb4;

USE mealexpress_user;
-- 迁移表: user, address_book, employee

-- 商品服务数据库
CREATE DATABASE mealexpress_product DEFAULT CHARSET utf8mb4;

USE mealexpress_product;
-- 迁移表: category, dish, dish_flavor, setmeal, setmeal_dish

-- 订单服务数据库
CREATE DATABASE mealexpress_order DEFAULT CHARSET utf8mb4;

USE mealexpress_order;
-- 迁移表: shopping_cart, orders, order_detail

-- Nacos配置数据库
CREATE DATABASE nacos_config DEFAULT CHARSET utf8mb4;
-- Nacos自动初始化
```

---

## 四、技术栈配置（Java 8版本）

### 4.1 父工程pom.xml

```xml
<?xml version="1.0" encoding="UTF-8"?>
<project xmlns="http://maven.apache.org/POM/4.0.0"
         xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
         xsi:schemaLocation="http://maven.apache.org/POM/4.0.0
         http://maven.apache.org/xsd/maven-4.0.0.xsd">
    <modelVersion>4.0.0</modelVersion>

    <parent>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-parent</artifactId>
        <version>2.7.18</version>
        <relativePath/>
    </parent>

    <groupId>com.mealexpress</groupId>
    <artifactId>mealexpress-parent</artifactId>
    <version>1.0-SNAPSHOT</version>
    <packaging>pom</packaging>

    <modules>
        <module>mealexpress-common</module>
        <module>mealexpress-api</module>
        <module>mealexpress-gateway</module>
        <module>mealexpress-user</module>
        <module>mealexpress-product</module>
        <module>mealexpress-order</module>
        <module>mealexpress-payment</module>
        <module>mealexpress-search</module>
    </modules>

    <properties>
        <java.version>1.8</java.version>
        <maven.compiler.source>1.8</maven.compiler.source>
        <maven.compiler.target>1.8</maven.compiler.target>
        <project.build.sourceEncoding>UTF-8</project.build.sourceEncoding>

        <spring-cloud.version>2021.0.5</spring-cloud.version>
        <spring-cloud-alibaba.version>2021.0.5.0</spring-cloud-alibaba.version>

        <mybatis-plus.version>3.5.3</mybatis-plus.version>
        <druid.version>1.2.16</druid.version>
        <lombok.version>1.18.30</lombok.version>
        <knife4j.version>3.0.3</knife4j.version>
        <jjwt.version>0.9.1</jjwt.version>
    </properties>

    <dependencyManagement>
        <dependencies>
            <!-- Spring Cloud -->
            <dependency>
                <groupId>org.springframework.cloud</groupId>
                <artifactId>spring-cloud-dependencies</artifactId>
                <version>${spring-cloud.version}</version>
                <type>pom</type>
                <scope>import</scope>
            </dependency>

            <!-- Spring Cloud Alibaba -->
            <dependency>
                <groupId>com.alibaba.cloud</groupId>
                <artifactId>spring-cloud-alibaba-dependencies</artifactId>
                <version>${spring-cloud-alibaba.version}</version>
                <type>pom</type>
                <scope>import</scope>
            </dependency>

            <!-- MyBatis Plus -->
            <dependency>
                <groupId>com.baomidou</groupId>
                <artifactId>mybatis-plus-boot-starter</artifactId>
                <version>${mybatis-plus.version}</version>
            </dependency>

            <!-- Druid -->
            <dependency>
                <groupId>com.alibaba</groupId>
                <artifactId>druid-spring-boot-starter</artifactId>
                <version>${druid.version}</version>
            </dependency>

            <!-- Knife4j -->
            <dependency>
                <groupId>com.github.xiaoymin</groupId>
                <artifactId>knife4j-spring-boot-starter</artifactId>
                <version>${knife4j.version}</version>
            </dependency>

            <!-- JWT -->
            <dependency>
                <groupId>io.jsonwebtoken</groupId>
                <artifactId>jjwt</artifactId>
                <version>${jjwt.version}</version>
            </dependency>

            <!-- Lombok -->
            <dependency>
                <groupId>org.projectlombok</groupId>
                <artifactId>lombok</artifactId>
                <version>${lombok.version}</version>
            </dependency>
        </dependencies>
    </dependencyManagement>

    <build>
        <plugins>
            <plugin>
                <groupId>org.springframework.boot</groupId>
                <artifactId>spring-boot-maven-plugin</artifactId>
            </plugin>
        </plugins>
    </build>
</project>
```

### 4.2 服务application.yml模板

```yaml
server:
  port: 8081  # 各服务端口不同

spring:
  application:
    name: user-service  # 服务名称

  cloud:
    nacos:
      discovery:
        server-addr: localhost:8848
        namespace: dev
      config:
        server-addr: localhost:8848
        namespace: dev
        file-extension: yml

    sentinel:
      transport:
        dashboard: localhost:8080
        port: 8719

  datasource:
    druid:
      driver-class-name: com.mysql.cj.jdbc.Driver
      url: jdbc:mysql://localhost:3306/mealexpress_user?serverTimezone=Asia/Shanghai&useUnicode=true&characterEncoding=utf-8
      username: root
      password: your_password

  redis:
    host: localhost
    port: 6379
    database: 0

mybatis-plus:
  mapper-locations: classpath:mapper/*.xml
  type-aliases-package: com.mealexpress.user.entity
  configuration:
    map-underscore-to-camel-case: true
    log-impl: org.apache.ibatis.logging.stdout.StdOutImpl

# Seata配置
seata:
  enabled: true
  application-id: ${spring.application.name}
  tx-service-group: mealexpress-group
  registry:
    type: nacos
    nacos:
      application: seata-server
      server-addr: localhost:8848
      namespace: dev
      group: SEATA_GROUP
  config:
    type: nacos
    nacos:
      server-addr: localhost:8848
      namespace: dev
      group: SEATA_GROUP
```

---

## 五、Windows本地环境搭建

### 5.1 使用Windows版docker

- 本地环境搭建使用windows版的docker：docker-desktop。

- 所有服务均已通过docker容器启动，其中mysql数据库中表都已经创建好了、数据也都有已经准备好

### 5.2 数据库初始化

```sql
-- 1. 创建Nacos配置数据库
CREATE DATABASE nacos_config DEFAULT CHARSET utf8mb4;

-- 2. 导入Nacos SQL脚本
-- 脚本位置: nacos/conf/nacos-mysql.sql

-- 3. 创建业务数据库
CREATE DATABASE mealexpress_user DEFAULT CHARSET utf8mb4;
CREATE DATABASE mealexpress_product DEFAULT CHARSET utf8mb4;
CREATE DATABASE mealexpress_order DEFAULT CHARSET utf8mb4;

-- 4. 从原sky_takeout迁移表结构和数据
-- （后续通过脚本完成）
```

### 5.3 IDEA多服务配置

```yaml
Run/Debug Configurations配置:

1. Gateway（必须先启动）
   Name: mealexpress-gateway
   Main class: com.mealexpress.gateway.GatewayApplication
   VM options: -Xms256m -Xmx512m
   Port: 8080

2. User Service
   Name: mealexpress-user
   Main class: com.mealexpress.user.UserApplication
   VM options: -Xms256m -Xmx512m
   Port: 8081

3. Product Service
   Name: mealexpress-product
   Main class: com.mealexpress.product.ProductApplication
   VM options: -Xms256m -Xmx512m
   Port: 8082

4. Order Service
   Name: mealexpress-order
   Main class: com.mealexpress.order.OrderApplication
   VM options: -Xms256m -Xmx512m
   Port: 8083

5. Payment Service
   Name: mealexpress-payment
   Main class: com.mealexpress.payment.PaymentApplication
   VM options: -Xms256m -Xmx512m
   Port: 8084

6. Search Service（可选）
   Name: mealexpress-search
   Main class: com.mealexpress.search.SearchApplication
   VM options: -Xms256m -Xmx512m
   Port: 8085

启动顺序:
  1. 中间件（Docker Compose）
  2. Gateway
  3. 各业务服务（并行启动）

内存需求:
  - 服务: 6 x 512MB = 3GB
  - Docker: 8GB
  - 系统: 4GB
  - 总计: 至少16GB内存
```

---

## 六、实施路线图

### 6.1 Phase 1: 准备阶段

```yaml
1: 环境准备
  ✅ 验证中间件可访问:
     - Nacos: http://localhost:8848/nacos (nacos/nacos)
     - MySQL: localhost:3306
     - Redis: localhost:6379
     - RocketMQ Console: 需要单独部署（可选）

  ✅ Git分支管理
     cd D:\projects\sky-take-out
     git checkout -b microservice-refactor
     git add .
     git commit -m "Backup: 单体应用备份"

  ✅ 创建新Maven父工程
     mkdir mealexpress-parent
     # 创建pom.xml（参考4.1节）
```

### 6.2 Phase 2: 公共模块提取

```yaml
1: 创建公共模块
  ✅ mealexpress-common
     - 从sky-common迁移代码
     - 添加Spring Cloud依赖
     - 公共配置类、工具类、异常类

  ✅ mealexpress-api
     - 定义Feign接口
     - DTO/VO对象
     - 接口契约
```

### 6.3 Phase 3: 服务拆分

```yaml
一: 基础服务拆分

1: Gateway + User Service
  ✅ 创建gateway模块（端口8080）
  ✅ 创建user-service模块（端口8081）
  ✅ 数据库拆分: mealexpress_user
  ✅ Nacos注册验证
  ✅ Gateway路由配置
  ✅ 测试用户登录功能

2: Product Service
  ✅ 创建product-service模块（端口8082）
  ✅ 数据库拆分: mealexpress_product
  ✅ 菜品、套餐API迁移
  ✅ 测试分类、菜品查询

3: Order Service
  ✅ 创建order-service模块（端口8083）
  ✅ 数据库拆分: mealexpress_order
  ✅ 购物车、订单API迁移
  ✅ Feign调用user、product服务
  ✅ 测试下单流程

二: 高级特性集成

1: Payment Service
  ✅ 创建payment-service模块（端口8084）
  ✅ 微信支付集成
  ✅ 支付回调处理

2: Seata分布式事务
  ✅ Seata Server启动配置
  ✅ AT模式配置
  ✅ 订单-库存事务验证

3: RocketMQ消息队列
  ✅ 订单延迟消息（15分钟超时取消）
  ✅ 事务消息（支付状态同步）
  ✅ 异步解耦（订单创建通知）
```

### 6.4 Phase 4: 搜索与监控

```yaml
1: Elasticsearch搜索
  ✅ search-service模块（端口8085）
  ✅ ES索引设计
  ✅ Canal数据同步
  ✅ 搜索API实现

2: SkyWalking链路追踪
  ✅ SkyWalking OAP + UI部署
  ✅ Java Agent配置
  ✅ 链路追踪验证

3: Spring Boot Admin监控
  ✅ Admin Server部署
  ✅ 各服务注册到Admin
  ✅ 监控面板配置
```

---

## 七、前端适配说明

### 7.1 前端是否需要改动？

**答案：完全不需要改动！**

**原因分析**:

```yaml
管理端前端配置:
  baseURL: /api
  实际请求: http://localhost:8080/admin/**

  单体架构流程:
    前端 /api/admin/employee/login
      → 代理 http://localhost:8080/admin/employee/login
      → sky-server处理

  微服务架构流程:
    前端 /api/admin/employee/login
      → 代理 http://localhost:8080/admin/employee/login
      → Gateway路由到user-service
      → user-service处理

  关键点:
    ✅ Gateway端口保持8080
    ✅ URL路径完全一致
    ✅ 前端感知不到后端变化
```

### 7.2 Gateway路由规则设计

```yaml
# Gateway配置关键点

spring:
  cloud:
    gateway:
      routes:
        # 完全匹配原有URL结构
        - id: admin-employee
          uri: lb://user-service
          predicates:
            - Path=/admin/employee/**
          # 不需要StripPrefix，保持原路径

        - id: admin-dish
          uri: lb://product-service
          predicates:
            - Path=/admin/dish/**,/admin/category/**,/admin/setmeal/**

        - id: user-order
          uri: lb://order-service
          predicates:
            - Path=/user/order/**,/user/shoppingCart/**

设计原则:
  1. URL路径不变（/admin/**, /user/**）
  2. 根据路径路由到不同服务
  3. 不修改路径（不使用StripPrefix）
  4. 保持JWT鉴权逻辑
```

### 7.3 WebSocket适配

```yaml
问题: WebSocket连接如何处理？

原配置:
  ws://localhost:8080/ws/orderId

Gateway配置:
  spring:
    cloud:
      gateway:
        routes:
          - id: websocket
            uri: lb:ws://order-service  # 注意是ws协议
            predicates:
              - Path=/ws/**

小程序配置:
  无需修改，保持 ws://localhost:8080/ws/**
```

---

## 八、关键技术实现

### 8.1 Seata分布式事务

```java
// order-service: OrderServiceImpl.java

@Service
public class OrderServiceImpl implements OrderService {

    @Autowired
    private OrderMapper orderMapper;

    @Autowired
    private ProductClient productClient; // Feign接口

    @Autowired
    private UserClient userClient;

    /**
     * 提交订单 - Seata全局事务
     */
    @GlobalTransactional(name = "create-order", rollbackFor = Exception.class)
    @Override
    public Long submitOrder(OrderDTO orderDTO) {
        // 1. 创建订单（本地事务）
        Orders order = new Orders();
        BeanUtils.copyProperties(orderDTO, order);
        order.setStatus(Orders.PENDING_PAYMENT);
        order.setOrderTime(LocalDateTime.now());
        orderMapper.insert(order);

        // 2. 扣减库存（远程调用product-service）
        StockDTO stockDTO = new StockDTO();
        stockDTO.setDishId(orderDTO.getDishId());
        stockDTO.setQuantity(orderDTO.getNumber());

        Boolean stockResult = productClient.deductStock(stockDTO);
        if (!stockResult) {
            throw new BusinessException("库存不足");
        }

        // 3. 扣减余额（远程调用user-service）
        BalanceDTO balanceDTO = new BalanceDTO();
        balanceDTO.setUserId(orderDTO.getUserId());
        balanceDTO.setAmount(order.getAmount());

        Boolean balanceResult = userClient.deductBalance(balanceDTO);
        if (!balanceResult) {
            throw new BusinessException("余额不足");
        }

        // 任何一步失败，Seata自动回滚所有操作
        return order.getId();
    }
}
```

### 8.2 RocketMQ延迟消息

```java
// 订单超时自动取消

@Service
public class OrderServiceImpl implements OrderService {

    @Autowired
    private RocketMQTemplate rocketMQTemplate;

    @Override
    public Long submitOrder(OrderDTO orderDTO) {
        // ... 创建订单

        // 发送延迟消息（15分钟）
        Message<Long> message = MessageBuilder
            .withPayload(order.getId())
            .build();

        // Level 4 = 15分钟
        rocketMQTemplate.syncSend(
            "order-cancel-topic",
            message,
            3000,
            4  // 延迟级别
        );

        return order.getId();
    }
}

// 消费者
@Component
@RocketMQMessageListener(
    topic = "order-cancel-topic",
    consumerGroup = "order-cancel-consumer"
)
public class OrderCancelListener implements RocketMQListener<Long> {

    @Autowired
    private OrderMapper orderMapper;

    @Override
    public void onMessage(Long orderId) {
        Orders order = orderMapper.selectById(orderId);

        // 如果仍是待支付，则取消
        if (order.getStatus() == Orders.PENDING_PAYMENT) {
            order.setStatus(Orders.CANCELLED);
            orderMapper.updateById(order);
            log.info("订单{}超时取消", orderId);
        }
    }
}
```

### 8.3 OpenFeign远程调用

```java
// mealexpress-api: ProductClient.java

@FeignClient(
    name = "product-service",
    fallback = ProductClientFallback.class
)
public interface ProductClient {

    @PostMapping("/product/stock/deduct")
    Boolean deductStock(@RequestBody StockDTO stockDTO);

    @GetMapping("/product/dish/{id}")
    DishVO getDish(@PathVariable Long id);
}

// 降级处理
@Component
public class ProductClientFallback implements ProductClient {

    @Override
    public Boolean deductStock(StockDTO stockDTO) {
        log.error("商品服务不可用，扣库存失败");
        return false;
    }

    @Override
    public DishVO getDish(Long id) {
        return new DishVO(); // 返回默认值
    }
}

// 配置
feign:
  sentinel:
    enabled: true
  httpclient:
    enabled: true
    max-connections: 200
```

### 8.4 Sentinel流量控制

```java
@RestController
@RequestMapping("/order")
public class OrderController {

    @PostMapping("/submit")
    @SentinelResource(
        value = "submitOrder",
        blockHandler = "submitOrderBlockHandler"
    )
    public Result submitOrder(@RequestBody OrderDTO orderDTO) {
        // 业务逻辑
        return Result.success();
    }

    // 限流降级处理
    public Result submitOrderBlockHandler(OrderDTO orderDTO, BlockException ex) {
        return Result.error("系统繁忙，请稍后重试");
    }
}
```

---

## 📌 总结

### 关键决策回顾

```yaml
技术栈:
  ✅ Java 8（不升级）
  ✅ Spring Boot 2.7.3
  ✅ Spring Cloud Alibaba 2021.0.5.0
  ✅ 版本完全兼容，官方支持

环境:
  ✅ Windows本地开发
  ✅ Docker Desktop管理中间件
  ✅ IDEA多服务启动
  ✅ 单机运行（分库不分机）

前端:
  ✅ 完全零改动
  ✅ Gateway端口8080
  ✅ URL路径保持一致

改名:
  ✅ 简单改名方案
  ✅ 仅改外部名称
  ✅ 保留内部包名
  ✅ 改完立即可运行
```

### 实施优先级

```yaml
Priority 1:
  - 环境搭建（Docker Compose）
  - 项目改名
  - Gateway + User + Product + Order拆分
  - Seata分布式事务
  - 前端功能验证

Priority 2 :
  - RocketMQ消息队列
  - Sentinel流量控制
  - Payment Service拆分

Priority 3:
  - Elasticsearch搜索
  - SkyWalking链路追踪
  - Spring Boot Admin监控
```

---

## 🔗 官方资源链接

```yaml
Spring Cloud Alibaba:
  官网: https://spring.io/projects/spring-cloud-alibaba
  GitHub: https://github.com/alibaba/spring-cloud-alibaba
  文档: https://github.com/alibaba/spring-cloud-alibaba/wiki

Nacos:
  官网: https://nacos.io/
  下载: https://github.com/alibaba/nacos/releases
  文档: https://nacos.io/zh-cn/docs/quick-start.html

Sentinel:
  官网: https://sentinelguard.io/zh-cn/
  GitHub: https://github.com/alibaba/Sentinel
  文档: https://sentinelguard.io/zh-cn/docs/introduction.html

Seata:
  官网: https://seata.io/
  GitHub: https://github.com/seata/seata
  文档: https://seata.io/zh-cn/docs/overview/what-is-seata.html

RocketMQ:
  官网: https://rocketmq.apache.org/
  下载: https://rocketmq.apache.org/download/
  文档: https://rocketmq.apache.org/docs/quick-start/

Elasticsearch:
  官网: https://www.elastic.co/elasticsearch/
  下载: https://www.elastic.co/downloads/elasticsearch
  文档: https://www.elastic.co/guide/en/elasticsearch/reference/7.17/index.html
```

