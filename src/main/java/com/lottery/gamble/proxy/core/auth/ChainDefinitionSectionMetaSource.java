package com.lottery.gamble.proxy.core.auth;

import com.lottery.gamble.dao.BackResourcesMapper;
import com.lottery.gamble.entity.TreeObject;
import org.apache.commons.lang.StringUtils;
import org.apache.shiro.config.Ini;
import org.springframework.beans.factory.FactoryBean;

import javax.annotation.Resource;
import java.util.List;

/**
 * 产生责任链，确定每个url的访问权限
 * 
 */
public class ChainDefinitionSectionMetaSource implements FactoryBean<Ini.Section> {

	@Resource
	private BackResourcesMapper backResourcesMapper;

	// 静态资源访问权限
	private String filterChainDefinitions = null;

	public Ini.Section getObject() throws Exception {
//		new ConfigUtils().initTableField(resourcesMapper);
		Ini ini = new Ini();
		// 加载默认的url
		ini.load(filterChainDefinitions);
		Ini.Section section = ini.getSection(Ini.DEFAULT_SECTION_NAME);
		// 循环Resource的url,逐个添加到section中。section就是filterChainDefinitionMap,
		// 里面的键就是链接URL,值就是存在什么条件才能访问该链接
		List<TreeObject> lists = this.backResourcesMapper.selectList();
		for (TreeObject resources : lists) {
			// 构成permission字符串
			if (StringUtils.isNotEmpty(resources.getResUrl() + "") && StringUtils.isNotEmpty(resources.getResKey() + "")) {
				String permission = "perms[" + resources.getResKey() + "]";
				//System.out.println(permission);
				// 不对角色进行权限验证
				// 如需要则 permission = "roles[" + resources.getResKey() + "]";
				section.put(resources.getResUrl() + "", permission);
			}
		}
		// 所有资源的访问权限，必须放在最后
		section.put("/**", "authc");
		/** 如果需要一个用户只能登录一处地方,,修改为 section.put("/**", "authc,kickout,sysUser,user"); **/
		return section;
	}

	/**
	 * 通过filterChainDefinitions对默认的url过滤定义
	 * 
	 * @param filterChainDefinitions
	 *            默认的url过滤定义
	 */
	public void setFilterChainDefinitions(String filterChainDefinitions) {
		this.filterChainDefinitions = filterChainDefinitions;
	}

	public Class<?> getObjectType() {
		return this.getClass();
	}

	public boolean isSingleton() {
		return false;
	}
}
