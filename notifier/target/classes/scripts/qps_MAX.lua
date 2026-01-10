--[[
    QPS 限制脚本 (适配JsonJacksonCodec)
    功能：通过 Redis Lua 脚本实现 QPS 限制功能
    参数：
    - KEYS[1]: 键名
    - ARGV[1]: 最大 QPS 限制值

    执行逻辑：
    - 如果键不存在，创建键并赋值为 1
    - 如果键存在，检查当前值是否小于等于最大值
    - 如果当前值小于最大值，则增加 1
    - 如果当前值已达到最大值，则返回错误状态

    返回值：JSON格式字符串，适配JsonJacksonCodec
    {
        "status": 1,           -- 1:成功, 0:失败
        "current_value": 1,     -- 当前值
        "current_qps": 1,       -- 当前 QPS
        "message": "new_created" -- 状态描述
    }
]]

-- 获取传入的参数
local key_name = KEYS[1]
local json_qps = ARGV[1]
local success, max_qps_string = pcall(cjson.decode, json_qps)
local max_qps=tonumber(max_qps_string)



if not key_name or not max_qps or max_qps <= 0 then
    return redis.error_reply('Invalid parameters: key_name=' .. key_name .. ', max_qps=' .. max_qps)
end

-- 获取当前计数
local current_value = redis.call('GET', key_name)

-- 如果键不存在，创建新键并设置初始值为 1，过期时间为 3 秒
if current_value == false then
    redis.call('SET', key_name, 1, 'EX', 3)
    local result = {
        ["status"] = 1,
        ["current_value"] = 1,
        ["current_qps"] = 1,
        ["message"] = "new_created"
    }
    return cjson.encode(result)
end

-- 转换为数字
current_value = tonumber(current_value)

-- 检查当前值是否小于最大值
if current_value < max_qps then
    -- 增加计数
    local new_value = redis.call('INCR', key_name)
    -- 如果是第一次设置，确保有过期时间
    if new_value == 1 then
        redis.call('EXPIRE', key_name, 3)
    end
    local result = {
        ["status"] = 1,
        ["current_value"] = new_value,
        ["current_qps"] = new_value,
        ["message"] = "increased"
    }
    return cjson.encode(result)
else
    -- 已达到最大值，不允许继续增加
    local result = {
        ["status"] = 0,
        ["current_value"] = current_value,
        ["current_qps"] = current_value,
        ["message"] = "limit_reached"
    }
    return cjson.encode(result)
end