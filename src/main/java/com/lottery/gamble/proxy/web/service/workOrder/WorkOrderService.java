package com.lottery.gamble.proxy.web.service.workOrder;

import com.lottery.gamble.common.enums.work.WorkOrderStatusEnum;
import com.lottery.gamble.entity.Result;
import com.lottery.gamble.entity.work.WorkOrder;
import com.lottery.gamble.proxy.web.exception.ServiceException;
import com.lottery.gamble.proxy.web.service.BaseService;

import java.util.List;

/**
 * 工单服务接口类
 *
 * @author Created by 王亚平 on 2017/6/16.
 */
public interface WorkOrderService extends BaseService<WorkOrder> {

	/**
	 * 创建工单
	 *
	 * @param order 工单对象
	 * @return 处理结果
	 */
	Result createOrderAndNotify(WorkOrder order) throws ServiceException;

	/**
	 * 删除工单
	 *
	 * @param id 工单id
	 * @return 处理结果
	 * @throws ServiceException
	 */
	Result deleteWorkOrder(long id) throws ServiceException;

	/**
	 * 更新工单信息
	 *
	 * @param order 工单对象
	 * @return 处理结果
	 */
	Result updateWorkOrder(WorkOrder order) throws ServiceException;

	List<WorkOrder> selectByStatus(WorkOrderStatusEnum status, Long userId) throws ServiceException;

	List<WorkOrder> selectByGroupOrMe(WorkOrderStatusEnum status, Long deptId, Long groupId, Long userId) throws ServiceException;

	List<WorkOrder> selectByCreateUser(Long deptId, Long groupId, Long userId) throws ServiceException;

	WorkOrder selectByOrderNo(String orderNo) throws ServiceException;
}
