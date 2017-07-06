package com.lottery.gamble.proxy.web.service.permission;

import com.lottery.gamble.common.util.CheckUtil;
import com.lottery.gamble.dao.BackGroupMapper;
import com.lottery.gamble.entity.BackGroup;
import com.lottery.gamble.entity.Page;
import com.lottery.gamble.proxy.web.service.BaseService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

/**
 * @Description: 部门下的分组接口实现类
 * @User: liuwu_eva@163.com
 * @Date: 2017-06-27 下午 6:21
 */
@Service
public class BackGroupServiceImpl implements BaseService<BackGroup>, BackGroupService {
    @Resource
    private BackGroupMapper backGroupMapper;

    @Override
    public Page<BackGroup> findPage(Page<BackGroup> page) {
        return null;
    }

    @Override
    public List<BackGroup> selectAll() {
        return null;
    }

    @Override
    public int deleteByPrimaryKey(Long id) {
        return backGroupMapper.deleteByPrimaryKey(id);
    }

    @Override
    public int insert(BackGroup obj) {
        return backGroupMapper.insert(obj);
    }

    @Override
    public int insertSelective(BackGroup obj) {
        return backGroupMapper.insertSelective(obj);
    }

    @Override
    public BackGroup selectByPrimaryKey(Long id) {
        return backGroupMapper.selectByPrimaryKey(id);
    }

    @Override
    public int updateByPrimaryKeySelective(BackGroup obj) {
        return backGroupMapper.updateByPrimaryKeySelective(obj);
    }

    @Override
    public int updateByPrimaryKey(BackGroup obj) {
        return backGroupMapper.updateByPrimaryKey(obj);
    }

    @Override
    public int selectByDeptId(Long deptId) {
        return backGroupMapper.selectByDeptId(deptId);
    }

    @Override
    public List<BackGroup> selectListByDeptId(Long deptId) {
        return backGroupMapper.selectListByDeptId(deptId);
    }

    @Override
    public boolean isExisitGroup(String groupName) {
        BackGroup backGroup = backGroupMapper.selectByGroupName(groupName);
        return CheckUtil.isEmpty(backGroup) ? false : true;
    }

    @Override
    public int insertBatch(List<BackGroup> groupList) {
        return backGroupMapper.insertBatch(groupList);
    }
}
