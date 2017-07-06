package com.lottery.gamble.proxy.core.auth;

import java.util.Map;

/**
 * @author Created by 王亚平 on 2017/6/27.
 */
public interface FilterChainDefinitionsService {

	/**
	 * 初始化权限资源配置
	 */
	void initPermission();

	/**
	 * 更新权限资源配置
	 */
	void updatePermission();


	Map<String,String> initOtherPermission();

}
