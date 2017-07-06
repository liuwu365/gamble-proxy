package com.lottery.gamble.proxy.web.service.user;

import com.lottery.gamble.dao.ProxyApplyInfoMapper;
import com.lottery.gamble.entity.Page;
import com.lottery.gamble.entity.user.ProxyApplyInfo;
import com.lottery.gamble.proxy.core.util.CheckUtil;
import com.lottery.gamble.proxy.web.service.BaseService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

/**
 * Created by Administrator on 2017/7/6.
 */
@Service
public class ProxyApplyInfoService implements BaseService<ProxyApplyInfo> {
    @Resource
    private ProxyApplyInfoMapper proxyApplyInfoMapper;

    @Override
    public Page findPage(Page page) {
        long L = proxyApplyInfoMapper.selectByAgencyAuditInfoCount(page);
        page.setTotal(CheckUtil.isEmpty(L) ? 0 : L);
        List<ProxyApplyInfo> list = proxyApplyInfoMapper.selectByAgencyAuditInfo(page);
        page.setResult(list);
        return page;
    }


    @Override
    public List<ProxyApplyInfo> selectAll() {
        return null;
    }

    @Override
    public int deleteByPrimaryKey(Long id) {
        return proxyApplyInfoMapper.deleteByPrimaryKey(id);
    }

    @Override
    public int insert(ProxyApplyInfo obj) {
        return proxyApplyInfoMapper.insert(obj);
    }

    @Override
    public int insertSelective(ProxyApplyInfo obj) {
        return proxyApplyInfoMapper.insertSelective(obj);
    }

    @Override
    public ProxyApplyInfo selectByPrimaryKey(Long id) {
        return proxyApplyInfoMapper.selectByPrimaryKey(id);
    }

    @Override
    public int updateByPrimaryKeySelective(ProxyApplyInfo obj) {
        return proxyApplyInfoMapper.updateByPrimaryKeySelective(obj);
    }

    @Override
    public int updateByPrimaryKey(ProxyApplyInfo obj) {
        return proxyApplyInfoMapper.updateByPrimaryKey(obj);
    }

}
