package cn.wenzhuo4657.noifiterBot.app.domain.notifier.service.decorator.qps;

import cn.wenzhuo4657.noifiterBot.app.domain.notifier.service.decorator.NotifierDecorator;
import cn.wenzhuo4657.noifiterBot.app.domain.notifier.service.strategy.INotifier;
import cn.wenzhuo4657.noifiterBot.app.domain.notifier.service.strategy.NotifierConfig;
import cn.wenzhuo4657.noifiterBot.app.domain.notifier.service.strategy.NotifierMessage;
import cn.wenzhuo4657.noifiterBot.app.domain.notifier.service.strategy.NotifierResult;
import cn.wenzhuo4657.noifiterBot.app.infrastructure.cache.GlobalCache;
import cn.wenzhuo4657.noifiterBot.app.types.cache.ScriptCacheUtils;
import org.springframework.core.io.ClassPathResource;
import org.springframework.util.FileCopyUtils;

import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;

public class QpsMaxDecorator extends NotifierDecorator<QpsResult> {


    GlobalCache globalCache;
    public QpsMaxDecorator(INotifier notifier, GlobalCache globalCache) {
        super(notifier);
        this.globalCache=globalCache;
    }


    @Override
    public QpsResult send(NotifierMessage message) {
        QpsResponse result;
        NotifierResult result1;
        try {

            String scriptSha1 = ScriptCacheUtils.loadScript(ScriptCacheUtils.Script.qps, globalCache);
//        2，调用脚本
            // 使用SHA1执行脚本

            String qpsKey = ScriptCacheUtils.Script.qps.getScriptType().getKey()+":"+ScriptCacheUtils.Script.qps.getFileName()+":data:"+notifier.getName();
            int maxQps = notifier.getQps();

            // 使用SHA1执行测试
            result = globalCache.executeLuaScriptSha1(scriptSha1,
                    Arrays.asList(qpsKey), Arrays.asList(maxQps), QpsResponse.class);
            if (result.getStatus()==1){
                result1 = notifier.send(message);
            }else {
                result1=NotifierResult.fail();
            }

            QpsResult qpsResult = new QpsResult();
            qpsResult.setSuccess(result1.isSuccess());
            qpsResult.setQpsResponse(result);
            return qpsResult;
        } catch (IOException e) {
            e.printStackTrace();
        }


        QpsResult qpsResult = new QpsResult();
        qpsResult.setSuccess(false);
        return qpsResult;






    }

    @Override
    public NotifierConfig getConfig() {
        return super.getConfig();
    }


}
