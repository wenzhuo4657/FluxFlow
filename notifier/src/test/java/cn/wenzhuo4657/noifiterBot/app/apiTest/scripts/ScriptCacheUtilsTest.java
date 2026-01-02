package cn.wenzhuo4657.noifiterBot.app.apiTest.scripts;

import cn.wenzhuo4657.noifiterBot.app.infrastructure.cache.GlobalCache;
import cn.wenzhuo4657.noifiterBot.app.types.cache.ScriptCacheUtils;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class ScriptCacheUtilsTest {

    @Autowired
    private GlobalCache globalCache;
    @Test
    public void testLoadScript() throws Exception {
        ScriptCacheUtils.loadScript(ScriptCacheUtils.Script.qps,globalCache);
    }
}
