package com.lottery.gamble.proxy.web.service.permission;

import com.lottery.gamble.entity.BackRole;
import com.lottery.gamble.entity.Page;
import com.lottery.gamble.entity.Result;

import java.util.List;

public interface BackRoleService {

    /**
     * 添加或修改角色
     * @param role
     * @return
     */
    Result addOrUpdateItem(BackRole role);

    /**
     * 获取角色信息
     * @param sId
     * @return
     */
    BackRole getItem(Long sId);

    /**
     * 获取角色列表
     * @return
     */
    Page<BackRole> getPage(Page page);


    /**
     * 获取角色列表
     * @return
     */
    List<BackRole> getList();

    /**
     * 删除一项角色
     * @param id
     * @return
     */
    Result delItem(Long id);


    /**
     * 角色资源关联
     *
     * @param res
     * @param roleId
     * @return
     */
    Result saveRoleResRelation(String res, Long roleId);

    /**
     * 获取角色资源关联
     *
     * @param roleId
     * @return
     */
    Result getRoleResList(Long roleId);
}
