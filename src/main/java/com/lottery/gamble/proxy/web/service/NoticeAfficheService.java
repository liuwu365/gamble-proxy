package com.lottery.gamble.proxy.web.service;

import com.lottery.gamble.entity.*;

public interface NoticeAfficheService {

    Result addOrUpdateItem(NoticeAffiche record);

    NoticeAffiche getItem(Long aId);

    Page<NoticeAffiche> getPage(Page page);

    Result delItem(Long aId);
}
