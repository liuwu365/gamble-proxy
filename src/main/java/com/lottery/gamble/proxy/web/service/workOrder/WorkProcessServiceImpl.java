package com.lottery.gamble.proxy.web.service.workOrder;

import com.lottery.gamble.common.enums.work.WorkOrderStatusEnum;
import com.lottery.gamble.dao.BackUserMapper;
import com.lottery.gamble.dao.OperationLogMapper;
import com.lottery.gamble.dao.WorkOrderMapper;
import com.lottery.gamble.dao.WorkProcessMapper;
import com.lottery.gamble.entity.BackUser;
import com.lottery.gamble.entity.Page;
import com.lottery.gamble.entity.Result;
import com.lottery.gamble.entity.operation.OperationLog;
import com.lottery.gamble.entity.work.WorkOrder;
import com.lottery.gamble.entity.work.WorkProcess;
import com.lottery.gamble.entity.work.WorkProcessVO;
import com.lottery.gamble.proxy.core.enums.WorkProcessResult;
import com.lottery.gamble.proxy.core.util.CheckUtil;
import com.lottery.gamble.proxy.core.util.ErrorWriterUtil;
import com.lottery.gamble.proxy.core.util.SessionUtil;
import com.lottery.gamble.proxy.web.exception.ServiceException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static com.lottery.gamble.common.util.JsonUtil.gson;

/**
 * @author Created by 王亚平 on 2017/6/19.
 */
@Service
public class WorkProcessServiceImpl implements WorkProcessService {


	Logger logger = LoggerFactory.getLogger(WorkProcessServiceImpl.class);

	@Resource
	private WorkProcessMapper workProcessMapper;

	@Resource
	private WorkOrderMapper workOrderMapper;

	@Resource
	private BackUserMapper backUserMapper;


	@Resource
	private OperationLogMapper operationLogMapper;


	/**
	 * 抢单，在这里只更新工单状态
	 *
	 * @param userId  抢单员id
	 * @param orderNo 工单id
	 * @return
	 * @throws ServiceException
	 */
	@Override
	@Transactional
	public Result grabOrder(long userId, String orderNo) throws ServiceException {
		//抢单员信息
		BackUser backUser = backUserMapper.selectByPrimaryKey(userId);
		if (CheckUtil.isEmpty(backUser)) {
			throw new ServiceException("抢单用户数据错误");
		}
		WorkOrder order = workOrderMapper.selectByOrderNo(orderNo);
		if (CheckUtil.isEmpty(order)) {
			throw new ServiceException("工单数据错误");
		}
		final WorkOrderStatusEnum status = order.getStatus();
		if (status == WorkOrderStatusEnum.Pending) { //待处理
			order.setStatus(WorkOrderStatusEnum.InProcess);//处理中
			final boolean res = workOrderMapper.updateByPrimaryKey(order) == 1;
			//添加工单处理流程记录
			WorkProcess process = new WorkProcess();
			process.setOrderNo(order.getOrderNo());
			process.setSequence(2);
			process.setCreateTime(new Date());
			process.setProcessUserId(backUser.getId());
			process.setBeginProcessingTime(new Date());//开始处理时间
			process.setProcessResult(WorkOrderStatusEnum.InProcess.getDesc());
			boolean iRes = insertSelective(process) == 1;
			//状态更新完成
			if (res && iRes) {

				return Result.success();
			} else {
				throw new ServiceException("抢单失败,数据库数据更新错误");
			}
		}
		return Result.failure("工单已被抢走了(；´д｀)ゞ");
	}

	@Override
	@Transactional
	public Result process(WorkProcess workProcess) throws ServiceException {
		WorkOrder order = null;

		int processResultCode = workProcess.getProcessResultCode();
		WorkOrderStatusEnum orderStatus = null;
		if (processResultCode == WorkProcessResult.DESCRIPTION_ERROR.getCode() ) { //客服描述错误或其他
			//驳回
			orderStatus = WorkOrderStatusEnum.Reject;
			workProcess.setProcessResult(WorkProcessResult.DESCRIPTION_ERROR.getDesc());
		} else if (processResultCode == WorkProcessResult.COMPLETE.getCode()) {//处理完成

			if (order.getStatus() != WorkOrderStatusEnum.InProcess) {
				throw new ServiceException("不能处理该订单！");
			}

			orderStatus = WorkOrderStatusEnum.ToBeConfirmed;//待确认
			if (CheckUtil.isEmpty(workProcess.getId())) {
				throw new ServiceException("工单流程id 不能为空");
			}
			workProcess.setRejectUpdate(false);
			workProcess.setProcessResult(WorkProcessResult.COMPLETE.getDesc());
		} else if (processResultCode == WorkProcessResult.RESOLVED.getCode()) {//已解决
			orderStatus = WorkOrderStatusEnum.resolved;
			workProcess.setProcessResult(WorkProcessResult.RESOLVED.getDesc());
		} else if (processResultCode == WorkProcessResult.NOT_CONNECTED_CUSTOMER.getCode()) {//联系不上客户
			orderStatus = WorkOrderStatusEnum.Closed;
			workProcess.setProcessResult(WorkProcessResult.NOT_CONNECTED_CUSTOMER.getDesc());
		} else if (processResultCode == WorkProcessResult.UPDATE_WORK_ORDER.getCode()) {
			workProcess.setRejectUpdate(true);
			orderStatus = WorkOrderStatusEnum.Pending;//待处理
		}
		else {
			//其他
			//驳回
			orderStatus = WorkOrderStatusEnum.Reject;
		}

		order = workOrderMapper.selectByOrderNo(workProcess.getOrderNo());
		if (CheckUtil.isEmpty(order)) {
			throw new ServiceException("订单不存在");
		}



		try {
			//添加流程记录，只存在 处理完成或驳回以及自定义结果
			workProcess.setSequence(2);//订单步骤为2
			workProcess.setEndProcessingTime(new Date());
			boolean res = false;
			if (!CheckUtil.isEmpty(workProcess.getId())) {
				res = updateByPrimaryKeySelective(workProcess) == 1;
			} else {
				workProcess.setSequence(workProcess.getSequence()+1);
				workProcess.setProcessUserId(SessionUtil.getUserId());
				res = insertSelective(workProcess) == 1;
			}
			//更新状态
			order.setStatus(orderStatus);
			boolean updateStatus = workOrderMapper.updateByPrimaryKeySelective(order) == 1;
			//boolean updateStatus = workOrderMapper.updateOrderStatusByOrderNo(workProcess.getOrderNo(),orderStatus) == 1;
			if (res && updateStatus) {
				//后续通知处理


				return Result.success();
			} else {
				return Result.failure("操作失败！稍后再试");
			}
		} catch (Exception e) {
			logger.error("process error|process:{}|error:{}", gson.toJson(workProcess), ErrorWriterUtil.WriteError(e).toString());
			throw new ServiceException("数据处理错误");
		}
	}


	public Result handleRejectWorkOrder(WorkOrder order) throws ServiceException {
		WorkProcess process = null;
		//当工单状态是驳回时，客服修改order数据
		if (order.getStatus() == WorkOrderStatusEnum.Reject) {
			process = new WorkProcess();
			process.setOrderType(order.getOrderType());
			process.setOrderNo(order.getOrderNo());
			process.setDeptId(order.getDeptId());
			process.setRoleId(order.getRoleId());
			process.setEventId(order.getEventId());
			process.setImgUrl(order.getImgUrl());
			process.setProblemDescription(order.getProblemDescription());
			process.setProcessUserId(order.getUserId());
			process.setSequence(3);//订单步骤为3
			process.setCreateTime(new Date());
			final boolean res;
			try {
				res = insert(process) == 1;
			} catch (Exception e) {
				logger.error("客服处理驳回工单数据库操作错误:{}", ErrorWriterUtil.WriteError(e));
				throw new ServiceException("修改工单错误，请稍后再试！");
			}
			if (res) {
				//发送通知 从走抢单流程
			}
			return Result.success();
		} else {
			throw new ServiceException("该订单无法非驳回，无需修改！");
		}
	}

	public Result handleResolvedWorkOrder(WorkOrder order) {
		//1.更新工单表状态 2.添加流程记录

		//1.
		final boolean res = workOrderMapper.updateOrderStatusByOrderNo(order.getOrderNo(), WorkOrderStatusEnum.resolved) == 1;
		if (res) {
			//2.

		} else {
			logger.warn("");
		}
		return null;
	}


	public List<WorkProcessVO> selectProcessDetail(String orderNo) throws ServiceException {
		WorkOrder order = workOrderMapper.selectByOrderNo(orderNo);
		if (CheckUtil.isEmpty(order)) {
			throw new ServiceException("订单[" + order + "]不存在");
		}
		List<WorkProcessVO> workProcessVOS = workProcessMapper.selectProcessDetailByOrderNo(orderNo);
		if (!CheckUtil.isEmpty(workProcessVOS)) {

			//匹配操作记录
			for (int i = 0; i < workProcessVOS.size(); i++) {


				WorkProcessVO workProcessVO = workProcessVOS.get(i);
				OperationLog operationLog = operationLogMapper.selectByPrimaryKey(workProcessVO.getOperationId());
				workProcessVO.setOperationLog(operationLog);
			}

			workProcessVOS.add(0, convert(order));
		} else {
			workProcessVOS = new ArrayList<>();
			workProcessVOS.add(convert(order));
		}
		return workProcessVOS;
	}

	/**
	 * 转换实体数据
	 *
	 * @param order 工单对象
	 * @return 工单流程对象
	 */
	public WorkProcessVO convert(WorkOrder order) {
		String orderJson = gson.toJson(order);
		WorkProcessVO workProcessVO = gson.fromJson(orderJson, WorkProcessVO.class);
		workProcessVO.setId(null);
		workProcessVO.setSequence(1);
		workProcessVO.setRejectUpdate(true);
		return workProcessVO;
	}


	@Override
	public Page<WorkProcess> findPage(Page<WorkProcess> page) {
		return null;
	}

	@Override
	public List<WorkProcess> selectAll() {
		return null;
	}

	@Override
	public int deleteByPrimaryKey(Long id) {
		return 0;
	}

	@Override
	public int insert(WorkProcess obj) {
		return workProcessMapper.insert(obj);
	}

	@Override
	public int insertSelective(WorkProcess obj) {
		return workProcessMapper.insertSelective(obj);
	}

	@Override
	public WorkProcess selectByPrimaryKey(Long id) {
		return null;
	}

	@Override
	public int updateByPrimaryKeySelective(WorkProcess obj) {
		return workProcessMapper.updateByPrimaryKeySelective(obj);
	}

	@Override
	public int updateByPrimaryKey(WorkProcess obj) {
		return 0;
	}

}
