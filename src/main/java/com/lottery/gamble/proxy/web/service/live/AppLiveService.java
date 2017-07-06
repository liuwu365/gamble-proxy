package com.lottery.gamble.proxy.web.service.live;

import com.lottery.gamble.entity.LiveConfig;
import com.lottery.gamble.entity.Page;
import com.lottery.gamble.entity.Result;

import java.util.List;

/**
 * User: zc
 * Date: 2017/4/12
 */

public interface AppLiveService {

    Page selectPage(Page page);

    LiveConfig selectById(Long id);

    Result insert(LiveConfig liveConfig);

    Result update(LiveConfig liveConfig);

    Result del(long id);

    Result insertLive(LiveConfig liveConfig);

    Result updateLive(LiveConfig liveConfig);
}
