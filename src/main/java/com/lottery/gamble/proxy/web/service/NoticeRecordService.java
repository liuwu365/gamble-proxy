package com.lottery.gamble.proxy.web.service;

import com.lottery.gamble.entity.*;

import java.util.List;

public interface NoticeRecordService {

    Result addItem(NoticeRecord record);

    Page<NoticeRecord> getPage(Page page);

    Page<NoticeRecord> getAppPage(Page page);

    List getUserList(NoticeRecord noticeRecord);

    NoticeRecord getItem(Long id);

    Result delItem(Long id);
}
