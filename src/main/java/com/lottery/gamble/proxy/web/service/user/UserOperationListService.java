package com.lottery.gamble.proxy.web.service.user;

import com.lottery.gamble.dao.OperationLogMapper;
import com.lottery.gamble.entity.Page;
import com.lottery.gamble.entity.operation.OperationLog;
import com.lottery.gamble.proxy.core.util.CheckUtil;
import com.lottery.gamble.proxy.web.service.BaseService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

/**
 * Created by Administrator on 2017/6/30.
 */
@Service
public class UserOperationListService implements BaseService<OperationLog> {

    @Resource
    private OperationLogMapper operationLogMapper;

    @Override
    public Page findPage(Page page) {
        long L = operationLogMapper.selectOperationLogCount(page);
        page.setTotal(CheckUtil.isEmpty(L) ? 0 : L);
        List<OperationLog> list = operationLogMapper.selectByOperationLogInfo(page);
        page.setResult(list);
        return page;
    }


    @Override
    public List<OperationLog> selectAll() {
        return null;
    }

    @Override
    public int deleteByPrimaryKey(Long id) {
        return 0;
    }

    @Override
    public int insert(OperationLog obj) {
        return 0;
    }

    @Override
    public int insertSelective(OperationLog obj) {
        return 0;
    }

    @Override
    public OperationLog selectByPrimaryKey(Long id) {
        return operationLogMapper.selectByPrimaryKey(id);
    }

    @Override
    public int updateByPrimaryKeySelective(OperationLog obj) {
        return operationLogMapper.updateByPrimaryKey(obj);
    }

    @Override
    public int updateByPrimaryKey(OperationLog obj) {
        return 0;
    }
}
