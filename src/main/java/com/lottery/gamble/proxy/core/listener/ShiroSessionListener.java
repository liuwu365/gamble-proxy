package com.lottery.gamble.proxy.core.listener;

import org.apache.shiro.SecurityUtils;
import org.apache.shiro.session.Session;
import org.apache.shiro.session.SessionListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author 王亚平
 * @description 会话监听器
 * Created by Administrator on 2016/7/11.
 */
public class ShiroSessionListener implements SessionListener{

    Logger logger = LoggerFactory.getLogger(ShiroSessionListener.class);

    public ShiroSessionListener() {
        logger.info("ShiroSessionListener init");
    }

    @Override
    public void onStart(Session session) {
        logger.info("session start：{}",session.getId());
    }

    @Override
    public void onStop(Session session) {
        logger.info("session stop：{}",session.getId());
    }

    @Override
    public void onExpiration(Session session) {
        logger.info("session expiration：{}",session.getId());
        SecurityUtils.getSubject().logout();
    }
}
