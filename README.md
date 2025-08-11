<h1 align="center" style="margin: 5px 0 30px; font-weight: bold;">拼团交易领域驱动平台</h1> <!-- 将顶部边距从 30px 改为 10px -->
<h4 align="center">B2C拼团营销交易平台</h4>
<div align="center">
    <img alt="GitHub top language" src="https://img.shields.io/github/languages/top/ybbzzj/Group-Buying-Transaction-Platform">
    <img src="https://img.shields.io/badge/Spring%20Boot-2.7.12-green?logo=springboot" alt="Spring Boot">

</div>

## 项目介绍
基于DDD领域驱动设计和六边形架构开发的拼团营销交易平台，参考主流电商平台的拼团模式，实现了完整的营销交易流程。系统采用Maven多模块设计，清晰划分交易、活动、人群三大领域边界，通过责任链和工厂模式构建了灵活的规则引擎。

## 核心实现流程图
#### 链式多分支规则树
![](./assets/img1.png)
#### 活动领域ActionNode设计
![](./assets/img2.png)
#### 基于Redis发布/订阅的动态配置中心
![](./assets/img3.png)
#### 责任链
![](./assets/img4.png)

## 数据库建表
[详见数据库建表](./docs/dev-ops/mysql/sql/group_buy_plus.sql)

## 部署方式
[详见 Docker 部署](./docs/dev-ops/docker-deploy.md)


