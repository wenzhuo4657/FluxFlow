package cn.wenzhuo4657.noifiterBot.app.domain.notifier.service.decorator.qps;

import cn.wenzhuo4657.noifiterBot.app.domain.notifier.service.strategy.NotifierResult;

public class QpsResult extends NotifierResult {
    QpsResponse qpsResponse;

    public QpsResponse getQpsResponse() {
        return qpsResponse;
    }

    public void setQpsResponse(QpsResponse qpsResponse) {
        this.qpsResponse = qpsResponse;
    }


}
