package com.lottery.gamble.proxy.web.service.impl;

import com.google.gson.Gson;
import com.lottery.gamble.common.util.CheckUtil;
import com.lottery.gamble.entity.BackUser;
import com.lottery.gamble.entity.NoticeRecord;
import com.lottery.gamble.entity.Result;
import com.lottery.gamble.proxy.core.util.SessionUtil;
import com.lottery.gamble.proxy.web.service.MQSendService;
import com.lottery.gamble.proxy.web.service.NoticeRecordService;
import com.lottery.gamble.proxy.web.service.NoticeService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

@Service
public class NoticeServiceImpl implements NoticeService {
    private static final Logger LOGGER = LoggerFactory.getLogger(NoticeServiceImpl.class);

    private static final Gson GSON = new Gson();

    @Resource
    private MQSendService mqSendService;

    @Resource
    private NoticeRecordService noticeRecordService;

//    @Resource
//    private MiPushService miPushService;


    private ScheduledExecutorService sendExecutor = Executors.newSingleThreadScheduledExecutor(r -> {
        Thread t = new Thread(r);
        t.setName("sendExecutor");
        return t;
    });

    @Override
    @Transactional
    public Result sendClientMsg(NoticeRecord noticeRecord, List userList) {

        Result result = this.saveRecord(noticeRecord, userList);
        if (result.getCode() != 200) {
            return result;
        }
        long seconds = this.setSendTime(noticeRecord);
        // 条件筛选
        final List list = new ArrayList<>();
        if (noticeRecord.getnTarget() == 3) {
            List list2 = this.noticeRecordService.getUserList(noticeRecord);
            list.addAll(list2);
        }
        if (!CheckUtil.isEmpty(userList))
            list.addAll(userList);
        // 消息发送
        this.sendExecutor.schedule(() -> {
            try {
                if (noticeRecord.getnTarget() == 1) {   // 给所有在线用户发消息
                    this.mqSendService.sendToAllUser(noticeRecord);
                } else { // 给指定用户发消息
                    this.mqSendService.sendToUsers(noticeRecord, list);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }, seconds, TimeUnit.SECONDS);
        return result;
    }

//    @Override
//    public Result sendAppMsg(NoticeRecord noticeRecord) {
//        Result result = noticeRecordService.addItem(noticeRecord);
//        if (result.getCode() != 200) {
//            return result;
//        }
//        result = miPushService.pushNotice(noticeRecord);
//        return result;
//    }


    private Result saveRecord(NoticeRecord noticeRecord, List userList) {

        BackUser backUser = SessionUtil.getCurrentUser();
        noticeRecord.setBackUserName(backUser.getUserName());
        if (!CheckUtil.isEmpty(userList)) {
            StringBuffer buf = new StringBuffer();
            for (int i = 0; i < userList.size(); i++) {
                Object o = userList.get(i);
                if (!CheckUtil.isEmpty(o))
                    buf.append(o).append(",");
            }
            String str = buf.substring(0, buf.length() - 1);
            noticeRecord.setnUsers(str);
        }
        return this.noticeRecordService.addItem(noticeRecord);
    }

    private long setSendTime(NoticeRecord noticeRecord) {
        long seconds = 0;
        switch (noticeRecord.getnWay()) {
            case 0:
                break;
            case 1:
                seconds = 60 * 60 * 1;  // 一小时
                break;
            case 2:
                seconds = 60 * 60 * 2;  // 两小时
                break;
            default:
                break;
        }
        return seconds;
    }

}
