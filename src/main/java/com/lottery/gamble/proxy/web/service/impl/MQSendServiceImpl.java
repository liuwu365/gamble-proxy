package com.lottery.gamble.proxy.web.service.impl;

import com.google.gson.Gson;
import com.lottery.gamble.proxy.web.service.MQSendService;
import com.lottery.gamble.common.mq.MQSend;
import com.lottery.gamble.common.util.CheckUtil;
import com.lottery.gamble.entity.NoticeMessage;
import com.lottery.gamble.entity.NoticeRecord;
import com.lottery.gamble.entity.Result;
import com.lottery.gamble.proxy.core.util.ErrorWriterUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.io.IOException;
import java.util.List;

@Service
public class MQSendServiceImpl implements MQSendService {
    private static final Logger LOGGER = LoggerFactory.getLogger(MQSendServiceImpl.class);

    private static final Gson GSON = new Gson();

    @Resource
    private MQSend mqSend;

    @Override
    @Transactional
    public Result sendToUsers(NoticeRecord noticeRecord, List userList) {

        NoticeMessage noticeMessage = new NoticeMessage();
        noticeMessage.setnName(noticeRecord.getnName());
        noticeMessage.setnContent(noticeRecord.getnContent());
        noticeMessage.setType(0);   // 0：通知类型
        try {
            for (int i = 0; i < userList.size(); i++) {
                String userId = (String) userList.get(i);
                if (!CheckUtil.isEmpty(userId)) {
                    this.mqSend.sendNotifyMessage(noticeMessage, Long.parseLong(userId));
                }
            }
        } catch (IOException e) {
            LOGGER.error("sendToUsers error|noticeRecord={}|userList={}|ex={}", GSON.toJson(noticeRecord), GSON.toJson(userList), ErrorWriterUtil.WriteError(e).toString());
            throw new RuntimeException();
        }
        return new Result(200, "ok");
    }

    @Override
    public Result sendToAllUser(NoticeRecord noticeRecord) {

        NoticeMessage noticeMessage = new NoticeMessage();
        noticeMessage.setnName(noticeRecord.getnName());
        noticeMessage.setnContent(noticeRecord.getnContent());
        noticeMessage.setType(0);   // 0：通知类型
        try {
            this.mqSend.sendNotifyMessage(noticeMessage, -1);
        } catch (IOException e) {
            LOGGER.error("sendToAllUser error|noticeRecord={}|ex={}", GSON.toJson(noticeRecord), ErrorWriterUtil.WriteError(e).toString());
            throw new RuntimeException();
        }
        return new Result(200, "ok");
    }
}
