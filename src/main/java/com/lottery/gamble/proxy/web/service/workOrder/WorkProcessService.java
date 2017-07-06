package com.lottery.gamble.proxy.web.service.workOrder;

import com.lottery.gamble.entity.Result;
import com.lottery.gamble.entity.work.WorkProcess;
import com.lottery.gamble.entity.work.WorkProcessVO;
import com.lottery.gamble.proxy.web.exception.ServiceException;
import com.lottery.gamble.proxy.web.service.BaseService;

import java.util.List;

/**
 * @author Created by 王亚平 on 2017/6/19.
 * 工单流程处理接口 客服与接单员之间的流程处理
 */
public interface WorkProcessService extends BaseService<WorkProcess> {


    /**
     * 抢单
     * @param userId 抢单员id
     * @param orderNo 工单id
     * @return 抢单结果
     */
    Result grabOrder(long userId, String orderNo) throws ServiceException;

    /**
     * 工单处理流程
     * @param workProcess
     * @return
     */
    Result process(WorkProcess workProcess) throws ServiceException;

    /**
     * 根据工单号查询工单详情
     * @param orderNo
     * @return
     * @throws ServiceException
     */
    List<WorkProcessVO> selectProcessDetail(String orderNo) throws ServiceException;
}
