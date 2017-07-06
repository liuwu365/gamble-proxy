package com.lottery.gamble.proxy.core.auth;

import org.apache.shiro.config.Ini;
import org.apache.shiro.spring.web.ShiroFilterFactoryBean;
import org.apache.shiro.util.CollectionUtils;
import org.apache.shiro.web.filter.mgt.DefaultFilterChainManager;
import org.apache.shiro.web.filter.mgt.PathMatchingFilterChainResolver;
import org.apache.shiro.web.servlet.AbstractShiroFilter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.BeanFactoryAware;

import javax.annotation.PostConstruct;
import java.util.Map;

/**
 * @author Created by 王亚平 on 2017/6/27.
 */
public abstract class AbstractFilterChainDefinitionsService implements FilterChainDefinitionsService, BeanFactoryAware {


	private final static Logger log = LoggerFactory.getLogger(AbstractFilterChainDefinitionsService.class);

	private String filterChainDefinitions = "";

	private AbstractShiroFilter shiroFilter;

	protected BeanFactory beanFactory;


	@PostConstruct
	public void initPermission() {
		//this.shiroFilterFactoryBean.setFilterChainDefinitionMap(obtainPermission());
		//log.debug("initialize shiro permission success...");
	}



	@Override
	public void updatePermission() {
		AbstractShiroFilter shiroFilter = (AbstractShiroFilter) beanFactory.getBean("shiroFilter");
		ShiroFilterFactoryBean shiroFilterFactoryBean = beanFactory.getBean(ShiroFilterFactoryBean.class);
		// 获取过滤管理器
		PathMatchingFilterChainResolver filterChainResolver = (PathMatchingFilterChainResolver)shiroFilter.getFilterChainResolver();
		DefaultFilterChainManager filterChainManager = (DefaultFilterChainManager)filterChainResolver.getFilterChainManager();
		//清空初始权限配置
		filterChainManager.getFilters().clear();
		shiroFilterFactoryBean.getFilterChainDefinitionMap().clear();

		//重新构建权限配置
		shiroFilterFactoryBean.setFilterChainDefinitionMap(initOtherPermission());
		log.debug("update shiro permission success...");


	}


	/** 读取配置资源 */
	private Ini.Section obtainPermission() {
		Ini ini = new Ini();
		ini.load(filterChainDefinitions); // 加载资源文件节点串
		Ini.Section section = ini.getSection("urls"); // 使用默认节点
		if (CollectionUtils.isEmpty(section)) {
			section = ini.getSection(Ini.DEFAULT_SECTION_NAME); // 如不存在默认节点切割,则使用空字符转换
		}
		Map<String, String> permissionMap = initOtherPermission();
		if (permissionMap != null && !permissionMap.isEmpty()) {
			section.putAll(permissionMap);
		}
		return section;
	}

	public String getFilterChainDefinitions() {
		return filterChainDefinitions;
	}

	public void setFilterChainDefinitions(String filterChainDefinitions) {
		this.filterChainDefinitions = filterChainDefinitions;
	}

	@Override
	public void setBeanFactory(BeanFactory beanFactory) throws BeansException {
		this.beanFactory = beanFactory;
	}


}
