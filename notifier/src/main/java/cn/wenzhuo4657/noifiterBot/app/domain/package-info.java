package cn.wenzhuo4657.noifiterBot.app.domain;


/**
 * 领域模块编写
 * domain： 例如auth领域，
 *  - model: 数据实体
 *       -  entity: pojo对象
 *       -  vo: 装饰类、描述类，用于描述pojo对象的属性
 *       -  aggregate： 聚合实体，内部封装entity和vo对象，用于和infrastructure进行交互，规定数据边界
 *   - repository: 数据仓库接口
 *   - service: 业务逻辑层
 *
 */