package com.lottery.gamble.proxy.web.service.permission;

import com.lottery.gamble.entity.BackResources;
import com.lottery.gamble.entity.Page;
import com.lottery.gamble.entity.Result;
import com.lottery.gamble.entity.TreeObject;

import java.util.List;

public interface BackResourcesService {

    /**
     * 添加或修改资源
     * @param backResources
     * @return
     */
    Result addOrUpdateItem(BackResources backResources);

    /**
     * 获取资源信息
     * @param id
     * @return
     */
    BackResources getItem(Long id);

    /**
     * 获取资源列表
     * @return
     */
    List<TreeObject> getList();

    /**
     *
     * @param page
     * @return
     */
    Page<BackResources> getPage(Page page);

    /**
     * 删除一项资源
     * @param id
     * @return
     */
    Result delItem(Long id);

    /**
     * 获取用户资源
     * @param userId
     * @return
     */
    List<TreeObject> getUserList(Long userId);

    /**
     * 获取某个父栏目下的资源列表
     * @param parentId
     * @return
     */
    List<BackResources> selectMenu(Long parentId);
}
