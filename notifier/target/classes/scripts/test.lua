local key_name = KEYS[1]
local max_qps = tonumber(ARGV[1])

redis.call('SET', key_name, max_qps)