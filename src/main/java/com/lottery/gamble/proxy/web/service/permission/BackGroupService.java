package com.lottery.gamble.proxy.web.service.permission;

import com.lottery.gamble.entity.BackGroup;
import com.lottery.gamble.proxy.web.service.BaseService;

import java.util.List;

/**
 * Created by liuwu on 2017/6/27 0027.
 */
public interface BackGroupService extends BaseService<BackGroup> {
    int selectByDeptId(Long deptId);

    List<BackGroup> selectListByDeptId(Long deptId);

    /**
     * 分组是否存在
     *
     * @param groupName
     * @return 存在：true
     */
    boolean isExisitGroup(String groupName);

    /**
     * 批量插入分组
     * @param groupList
     * @return
     */
    int insertBatch(List<BackGroup> groupList);
}
