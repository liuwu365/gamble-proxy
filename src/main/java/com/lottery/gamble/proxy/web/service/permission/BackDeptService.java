package com.lottery.gamble.proxy.web.service.permission;

import com.lottery.gamble.entity.BackDept;
import com.lottery.gamble.entity.Page;
import com.lottery.gamble.proxy.web.service.BaseService;

/**
 * @Description: 部门和组服务接口类
 * @User: liuwu_eva@163.com
 * @Date: 2017-06-27 下午 4:48
 */
public interface BackDeptService extends BaseService<BackDept> {

    /**
     * 获取部门列表(假分页)
     *
     * @return
     */
    Page<BackDept> getPage(Page page);

    /**
     * 部门是否存在
     *
     * @param deptName
     * @return 存在：true
     */
    boolean isExisit(String deptName);

}
