package cn.wenzhuo4657.dailyWeb.domain.notifier;

import cn.wenzhuo4657.dailyWeb.domain.notifier.model.dto.NotifierMessage;

public interface INotifierService {

    public boolean send(NotifierMessage message);
}
