package cn.wenzhuo4657.dailyWeb.domain.agent.mcp;


/**
 * 封装mcp，用于支持agent
 *
 *  现在不需要使用类型校验了，由于纯内部调用
 * 1，/api/item/today
 * 2，根据文档id查询文档内容的接口，
 * 3，tgBot的通知能力
 * 4，日期功能（后续需要根据用户时区来判断是否提醒）
 *
 *
 * 先不考虑多用户，但用户跑通，用户参数作为恒定
 *
 *
 * //        todo 考量，这里的mdc不知道是否还生效，如果生效，为什么?
 */
