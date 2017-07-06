package com.lottery.gamble.proxy.core.util;

import com.lottery.gamble.entity.BackUser;
import com.lottery.gamble.proxy.core.constant.BasicConstant;
import org.apache.shiro.SecurityUtils;

/**
 * @author Created by 王亚平 on 2017/6/26.
 */
public class SessionUtil {

	public static BackUser getCurrentUser() {
		BackUser backUser = (BackUser) SecurityUtils.getSubject().getSession().getAttribute(BasicConstant.BACK_USER_INFO);
		return CheckUtil.isEmpty(backUser)?new BackUser():backUser;
	}

	public static Long getUserId() {
		return getCurrentUser().getId();
	}





}
