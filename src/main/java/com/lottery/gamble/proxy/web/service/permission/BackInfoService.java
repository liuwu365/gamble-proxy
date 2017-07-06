package com.lottery.gamble.proxy.web.service.permission;

import com.lottery.gamble.entity.BackUser;
import com.lottery.gamble.entity.Page;
import com.lottery.gamble.entity.Result;

public interface BackInfoService {

    Result saveUserInfo(BackUser backUser, String roles);


}
