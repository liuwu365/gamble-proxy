package com.lottery.gamble.proxy.web.service.impl;

import com.google.gson.Gson;
import com.lottery.gamble.proxy.web.service.TokenInfoService;
import com.lottery.gamble.common.util.CacheUtil;
import com.lottery.gamble.dao.TokenInfoMapper;
import com.lottery.gamble.entity.Result;
import com.lottery.gamble.proxy.core.util.CheckUtil;
import com.lottery.gamble.proxy.core.util.ErrorWriterUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.List;

@Service
public class TokenInfoServiceImpl implements TokenInfoService {

    private static final Logger logger = LoggerFactory.getLogger(TokenInfoServiceImpl.class);

    private static final Gson GSON = new Gson();

    @Resource
    private TokenInfoMapper tokenInfoMapper;

    private static final CacheUtil cacheUtil = CacheUtil.getInstance();

    @Override
    @Transactional
    public Result clearToken(Long userId) {

        logger.info("start clearToken userId={}",userId);
        Result result = new Result();
        try {
            List<String> tokenList = this.tokenInfoMapper.selectTokenByUserId(userId);
            if (CheckUtil.isEmpty(tokenList)) {
                result = new Result(200, "ok");
            }else{
                for (int i = 0; i < tokenList.size(); i++) {
                    String token = tokenList.get(i);
                    cacheUtil.del("token:" + token);
                }
                int i = this.tokenInfoMapper.delByUserId(userId);
                if (i == -1)
                    result = new Result(400, "清除token失败");
                result =  new Result(200, "ok");
            }
            logger.info("clearToken result={}",GSON.toJson(result));
            return result;
        } catch (Exception e) {
            logger.error("clearToken error|userId={}|ex={}", userId,ErrorWriterUtil.WriteError(e).toString());
            throw new RuntimeException();
        }
    }
}
