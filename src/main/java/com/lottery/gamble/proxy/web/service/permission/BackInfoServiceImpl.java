package com.lottery.gamble.proxy.web.service.permission;

import com.google.gson.Gson;
import com.lottery.gamble.entity.BackUser;
import com.lottery.gamble.entity.Result;
import com.lottery.gamble.proxy.core.constant.BasicConstant;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;


@Service
public class BackInfoServiceImpl implements BackInfoService {
    private static final Logger LOGGER = LoggerFactory.getLogger(BackInfoServiceImpl.class);

    private static final Gson GSON = new Gson();

    @Resource
    private BackUserService backUserService;

    @Override
    @Transactional
    public Result saveUserInfo(BackUser backUser, String roles) {

        Result result = this.backUserService.addOrUpdateItem(backUser);
        Result result1 = this.backUserService.saveUserRoleRelation(roles, backUser.getId());
        if (result.getCode() == 200 && result1.getCode() == 200)
            return result;
        return Result.failure(BasicConstant.SAVE_FAILED);
    }

}
