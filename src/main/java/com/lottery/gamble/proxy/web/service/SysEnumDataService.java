package com.lottery.gamble.proxy.web.service;

import com.lottery.gamble.dao.SysEnumDataMapper;
import com.lottery.gamble.entity.Page;
import com.lottery.gamble.entity.common.SysEnumData;
import com.lottery.gamble.proxy.core.util.CheckUtil;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;
/**
 * Created by Administrator on 2016/12/5.
 */
@Service
public class SysEnumDataService implements BaseService<SysEnumData>{
    @Resource
    private SysEnumDataMapper sysEnumDataMapper;

    @Override
    public Page findPage(Page page) {
        long L = sysEnumDataMapper.selectSysEnumDataCount(page);
        page.setTotal(CheckUtil.isEmpty(L) ? 0 : L);
        List<SysEnumData> list = sysEnumDataMapper.selectSysEnumDataListPage(page);
        page.setResult(list);
        return page;
    }



    public SysEnumData getItem(Long id) {
        return this.sysEnumDataMapper.selectByPrimaryKey(id);
    }

    @Override
    public int insert(SysEnumData obj) {
        return sysEnumDataMapper.insert(obj);
    }

    @Override
    public int insertSelective(SysEnumData obj) {
        return sysEnumDataMapper.insertSelective(obj);
    }

    @Override
    public SysEnumData selectByPrimaryKey(Long id) {
        return sysEnumDataMapper.selectByPrimaryKey(id);
    }

    @Override
    public int deleteByPrimaryKey(Long id) {
        return sysEnumDataMapper.deleteByPrimaryKey(id);
    }

    @Override
    public int updateByPrimaryKeySelective(SysEnumData obj) {
        return sysEnumDataMapper.updateByPrimaryKeySelective(obj);
    }

    @Override
    public int updateByPrimaryKey(SysEnumData obj) {
        return sysEnumDataMapper.updateByPrimaryKey(obj);
    }

    @Override
    public List<SysEnumData> selectAll() {
        List<SysEnumData> sysEnumDatas = sysEnumDataMapper.selectAll();
        return sysEnumDatas;
    }

}
