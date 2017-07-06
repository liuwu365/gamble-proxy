package com.lottery.gamble.proxy.web.service;

import com.lottery.gamble.entity.Result;

/**
 * Created by zongguang on 2016/9/30 0030.
 */
public interface TokenInfoService {

    Result clearToken(Long userId);
}
