package com.lottery.gamble.proxy.web.service.permission;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.lottery.gamble.common.util.CheckUtil;
import com.lottery.gamble.dao.BackDeptMapper;
import com.lottery.gamble.entity.BackDept;
import com.lottery.gamble.entity.Page;
import com.lottery.gamble.proxy.web.service.BaseService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

/**
 * @Description: 部门和组接口实现类
 * @User: liuwu_eva@163.com
 * @Date: 2017-06-27 下午 4:49
 */
@Service
public class BackDeptServiceImpl implements BaseService<BackDept>, BackDeptService {
    @Resource
    private BackDeptMapper backDeptMapper;

    //假分页
    @Override
    public Page<BackDept> getPage(Page page) {
        PageHelper.startPage((int) page.getPage(), page.getLimit());
        List<BackDept> itemList = backDeptMapper.selectPage(page);
        PageInfo pageInfo = new PageInfo(itemList);
        page.setResult(pageInfo.getList());
        page.setTotal(pageInfo.getTotal());
        return page;
    }

    @Override
    public boolean isExisit(String deptName) {
        BackDept backDept = backDeptMapper.selectByDeptName(deptName);
        return CheckUtil.isEmpty(backDept) ? false : true;
    }

    @Override
    public Page<BackDept> findPage(Page<BackDept> page) {
        return null;
    }

    @Override
    public List<BackDept> selectAll() {
        return backDeptMapper.selectAll();
    }

    @Override
    public int deleteByPrimaryKey(Long id) {
        return backDeptMapper.deleteByPrimaryKey(id);
    }

    @Override
    public int insert(BackDept obj) {
        return backDeptMapper.insert(obj);
    }

    @Override
    public int insertSelective(BackDept obj) {
        return backDeptMapper.insertSelective(obj);
    }

    @Override
    public BackDept selectByPrimaryKey(Long id) {
        return backDeptMapper.selectByPrimaryKey(id);
    }

    @Override
    public int updateByPrimaryKeySelective(BackDept obj) {
        return backDeptMapper.updateByPrimaryKeySelective(obj);
    }

    @Override
    public int updateByPrimaryKey(BackDept obj) {
        return backDeptMapper.updateByPrimaryKey(obj);
    }

}
