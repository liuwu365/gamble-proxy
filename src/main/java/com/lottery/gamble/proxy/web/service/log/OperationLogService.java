package com.lottery.gamble.proxy.web.service.log;

import com.lottery.gamble.entity.operation.OperationLog;
import com.lottery.gamble.enums.OperLog;
import com.lottery.gamble.proxy.web.exception.ServiceException;
import com.lottery.gamble.proxy.web.service.BaseService;

import javax.servlet.http.HttpServletRequest;

/**
 * @author Created by 王亚平 on 2017/6/22.
 */
public interface OperationLogService extends BaseService<OperationLog> {

    /**
     * 添加常规操作日志
     *
     * @param request      获取Ip用
     * @param operTypeEnum 日志类型(枚举值)
     * @param content      日志内容
     */
    void addNormalOperLog(HttpServletRequest request, OperLog operTypeEnum, String content);

    OperationLog selectOperationLog(Long userId, String orderNo) throws ServiceException;
}
