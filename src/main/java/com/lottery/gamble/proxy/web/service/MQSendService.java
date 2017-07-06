package com.lottery.gamble.proxy.web.service;

import com.lottery.gamble.entity.NoticeRecord;
import com.lottery.gamble.entity.Result;

import java.util.List;

public interface MQSendService {

    Result sendToUsers(NoticeRecord noticeRecord, List userlist);

    Result sendToAllUser(NoticeRecord noticeRecord);

}
