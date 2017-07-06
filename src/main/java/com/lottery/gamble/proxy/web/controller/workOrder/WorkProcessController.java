package com.lottery.gamble.proxy.web.controller.workOrder;

import com.google.gson.Gson;
import com.lottery.gamble.common.enums.work.WorkOrderTypeEnum;
import com.lottery.gamble.common.util.CheckUtil;
import com.lottery.gamble.entity.BackUser;
import com.lottery.gamble.entity.Result;
import com.lottery.gamble.entity.operation.OperationLog;
import com.lottery.gamble.entity.work.WorkProcess;
import com.lottery.gamble.entity.work.WorkProcessVO;
import com.lottery.gamble.proxy.core.enums.WorkProcessResult;
import com.lottery.gamble.proxy.core.util.SessionUtil;
import com.lottery.gamble.proxy.web.exception.ServiceException;
import com.lottery.gamble.proxy.web.service.log.OperationLogService;
import com.lottery.gamble.proxy.web.service.workOrder.WorkProcessService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;
import java.util.Date;
import java.util.List;

/**
 * @author Created by 王亚平 on 2017/6/19.
 *         <p>
 *         工单流程控制器
 */

@Controller
@RequestMapping("/work/process")
public class WorkProcessController {


	Logger logger = LoggerFactory.getLogger(WorkProcessController.class);

	static final Gson gson = new Gson();

	@Resource
	private WorkProcessService workProcessService;

	@Resource
	private OperationLogService operationLogService;


	/**
	 * 抢单 只变更订单状态
	 *
	 * @param orderNo
	 * @return
	 * @throws ServiceException
	 */
	@RequestMapping(value = "/{orderNo}/grab.json", method = RequestMethod.GET)
	@ResponseBody
	public String grabOrder(@PathVariable("orderNo") String orderNo) throws ServiceException {

		return gson.toJson(workProcessService.grabOrder(SessionUtil.getUserId(), orderNo));
	}


	@RequestMapping("/{orderNo}/checkOpRecord.json")
	@ResponseBody
	public Result checkOpRecord(@PathVariable String orderNo) throws ServiceException {
		OperationLog operationLog = operationLogService.selectOperationLog(SessionUtil.getUserId(), orderNo);
		return !CheckUtil.isEmpty(operationLog) ? Result.success() : Result.failure("系统没有检测到你对该订单的操作记录，请先去处理。");
	}


	/**
	 * 操作员完成工单或驳回工单
	 * op =0 是完成操作
	 * 否则 是驳回操作
	 *
	 * @param model
	 * @param orderNo
	 * @param op      用户操作类型 0:完成工单，1:确认工单 ，2:驳回工单
	 * @return
	 * @throws ServiceException
	 */
	@RequestMapping(value = "/{orderNo}/{op}/handler", method = RequestMethod.GET)
	public String process(Model model, @PathVariable("orderNo") String orderNo, @PathVariable("op") String op) throws ServiceException {
		//WorkOrder workOrder = workOrderService.selectByOrderNo(orderNo);//
		model.addAttribute("op", op);//操作类型
		List<WorkProcessVO> workProcessVOS = workProcessService.selectProcessDetail(orderNo);
		//查询操作记录
		BackUser currentUser = SessionUtil.getCurrentUser();
		WorkProcessResult[] wpResult = WorkProcessResult.getRejectView();
		if ("0".equals(op)) { //完成工单
			////处理完成记录
			//OperationLog operationLog = operationLogService.selectOperationLog(currentUser.getId(), orderNo);
			////当查询到抢单人员的操作记录时,说明抢单人员处理完成该工单  页面显示处理完成
			//model.addAttribute("ol",operationLog);
			WorkProcessVO workProcessVO = workProcessVOS.get(workProcessVOS.size() - 1);
			model.addAttribute("process", workProcessVO);
			workProcessVOS.remove(workProcessVO);
			wpResult = WorkProcessResult.getCompleteView();
		} else if ("1".equals(op)) { //确认工单
			wpResult = WorkProcessResult.getConfirmView();
		} else if ("2".equals(op)) { //2 驳回工单
			WorkProcessVO workProcessVO = workProcessVOS.get(workProcessVOS.size() - 1);
			workProcessVOS.remove(workProcessVO);
			model.addAttribute("process", workProcessVO);
		} else {
			//客服修改订单
			final WorkProcessVO workProcessVO = workProcessVOS.get(0);
			WorkProcessVO workProcessVO1 = workProcessVOS.get(workProcessVOS.size() - 1);
			model.addAttribute("od1", workProcessVO1);
			model.addAttribute("od", workProcessVO);//订单创建信息
			model.addAttribute("createTime", new Date());
			model.addAttribute("orderType", WorkOrderTypeEnum.values());
		}
		//model.addAttribute("order",workOrder);
		model.addAttribute("orderNo", orderNo);
		model.addAttribute("orders", workProcessVOS);
		model.addAttribute("currentUser", currentUser);
		model.addAttribute("wpResult", wpResult);
		return "model/work/work_order_detail";
	}


	@RequestMapping(value = "/process", method = RequestMethod.POST)
	@ResponseBody
	public String process(WorkProcess workProcess) throws ServiceException {

		return gson.toJson(workProcessService.process(workProcess));
	}


}
