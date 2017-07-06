package com.lottery.gamble.proxy.web.service.log;

import com.lottery.gamble.common.util.ErrorWriterUtil;
import com.lottery.gamble.dao.OperationLogMapper;
import com.lottery.gamble.entity.Page;
import com.lottery.gamble.entity.operation.OperationLog;
import com.lottery.gamble.proxy.web.exception.ServiceException;
import com.lottery.gamble.proxy.core.util.CheckUtil;
import com.lottery.gamble.proxy.core.util.SessionUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.lottery.gamble.proxy.core.enums.OperLog;
import com.lottery.gamble.proxy.core.util.IpUtil;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.Date;
import java.util.List;

/**
 * @author Created by 王亚平 on 2017/6/22.
 */
@Service
public class OperationLogServiceImpl implements OperationLogService {


    Logger logger = LoggerFactory.getLogger(OperationLogServiceImpl.class);

    @Resource
    private OperationLogMapper operationLogMapper;


    @Override
    public void addNormalOperLog(HttpServletRequest request, OperLog operTypeEnum, String content) {
        try {
            if (!CheckUtil.isEmpty(SessionUtil.getCurrentUser())) {
                OperationLog operationLog = new OperationLog();
                operationLog.setOperUserId(SessionUtil.getCurrentUser().getId());
                operationLog.setOperUserName(SessionUtil.getCurrentUser().getUserName());
                operationLog.setIpAddress(IpUtil.getIpAddress(request));
                operationLog.setOperType(operTypeEnum.getCode());
                operationLog.setCreateTime(new Date());
                operationLog.setContent(content);
                operationLogMapper.insertSelective(operationLog);
            }
        } catch (Exception e) {
            logger.error("插入常规操作日志时异常:{}", ErrorWriterUtil.WriteError(e));
        }
    }

    @Override
    public OperationLog selectOperationLog(Long userId,String orderNo) throws ServiceException {
        try {
            return operationLogMapper.selectOperationLog(userId,orderNo);
        } catch (Exception e) {
            logger.error("selectOperationLog error|userId|{}|orderNo|{}|error|{}",
                    userId,orderNo, ErrorWriterUtil.WriteError(e));
            throw new ServiceException("数据查询错误,请稍后再试！");
        }
    }

    @Override
    public Page<OperationLog> findPage(Page<OperationLog> page) {
        return null;
    }

    @Override
    public List<OperationLog> selectAll() {
        return null;
    }

    @Override
    public int deleteByPrimaryKey(Long id) {
        return operationLogMapper.deleteByPrimaryKey(id);
    }

    @Override
    public int insert(OperationLog obj) {
        return operationLogMapper.insert(obj);
    }

    @Override
    public int insertSelective(OperationLog obj) {
        return operationLogMapper.insertSelective(obj);
    }

    @Override
    public OperationLog selectByPrimaryKey(Long id) {
        return operationLogMapper.selectByPrimaryKey(id);
    }

    @Override
    public int updateByPrimaryKeySelective(OperationLog obj) {
        return operationLogMapper.updateByPrimaryKeySelective(obj);
    }

    @Override
    public int updateByPrimaryKey(OperationLog obj) {
        return operationLogMapper.updateByPrimaryKey(obj);
    }

}
