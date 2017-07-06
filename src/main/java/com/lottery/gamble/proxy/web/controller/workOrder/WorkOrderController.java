package com.lottery.gamble.proxy.web.controller.workOrder;

import com.google.gson.Gson;
import com.lottery.gamble.common.enums.work.WorkOrderStatusEnum;
import com.lottery.gamble.common.enums.work.WorkOrderTypeEnum;
import com.lottery.gamble.common.util.CheckUtil;
import com.lottery.gamble.entity.BackUser;
import com.lottery.gamble.entity.Page;
import com.lottery.gamble.entity.Result;
import com.lottery.gamble.entity.user.UserInfo;
import com.lottery.gamble.entity.work.WorkOrder;
import com.lottery.gamble.proxy.web.controller.base.BaseController;
import com.lottery.gamble.proxy.web.exception.ServiceException;
import com.lottery.gamble.proxy.web.service.UserInfoService;
import com.lottery.gamble.proxy.web.service.workOrder.WorkOrderService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;


/**
 * @author Created by 王亚平 on 2017/6/15.
 * @deprecated 工单处理器
 */
@Controller
@RequestMapping("work/order")
public class WorkOrderController extends BaseController {

	Logger logger = LoggerFactory.getLogger(WorkOrderController.class);


	Gson gson = new Gson();

	@Resource
	private WorkOrderService workOrderService;

	@Resource
	private UserInfoService userInfoService;

	@Resource
	private TemplateEngine templateEngine;

	@RequestMapping(value = "/user/list", method = RequestMethod.GET)
	public String listViewUser(Model model, Page<WorkOrder> page) {

		model.addAttribute("page", page);
		return "model/work/user_list";
	}

	@RequestMapping("/{orderNo}/getOrder.json")
	@ResponseBody
	public String selectByOrderNo(@PathVariable String orderNo) throws ServiceException {
		WorkOrder result = workOrderService.selectByOrderNo(orderNo);
		Context context = new Context();
		List<WorkOrder> list = new ArrayList<WorkOrder>();
		list.add(result);
		context.setVariable("data", list);
		final String text = templateEngine.process("/vm/work_order_info", context);
		//return workOrderService.selectByOrderNo(orderNo);
		return text;
	}

	@RequestMapping("pending")
	public String toPendingOrders() {


		return "model/work/pending_order";
	}


	/**
	 * 获取待处理工单
	 *
	 * @param userId
	 * @param roleId
	 * @return
	 * @throws ServiceException
	 */
	@RequestMapping("getPendingOrders.json")
	@ResponseBody
	public Result selectPendingOrder(Long userId, Long roleId) throws ServiceException {
		List<WorkOrder> workOrders = null;
		workOrders = workOrderService.selectByStatus(null, userId);

		if (!CheckUtil.isEmpty(roleId)) {

		}
		Context context = new Context();
		context.setVariable("data", workOrders);


		//return workOrders!=null?Result.success((Object)html):Result.failure();
		final String process = templateEngine.process("/vm/my_order", context);
		return Result.success(workOrders);
	}

	@RequestMapping("getPendingOrders0.json")
	@ResponseBody
	public String selectPendingOrder0(@RequestParam(value = "type", defaultValue = "0") String type) throws ServiceException {
		List<WorkOrder> workOrders = null;
		String process = null;
		BackUser currentUser = getCurrentUser();
		Long userId = currentUser.getId();
		WorkOrderStatusEnum status = null;
		//默认查询我的工单
		if (!"0".equals(type)) {
			userId = null;//查询组内工单
			//组内工单
			status = WorkOrderStatusEnum.Pending;//才处理工单
		}
		if (currentUser.getDeptId() == 7) {
			workOrders = workOrderService.selectByCreateUser(Long.valueOf(currentUser.getDeptId()),
					Long.valueOf(currentUser.getGroupId()), userId
			);
		} else {
			workOrders = workOrderService.selectByGroupOrMe(status, Long.valueOf(currentUser.getDeptId()),
					Long.valueOf(currentUser.getGroupId()), userId
			);
		}
		Context context = new Context();
		context.setVariable("data", workOrders);
		if (!CheckUtil.isEmpty(workOrders)) {
			process = templateEngine.process("/vm/my_order", context);
		}
		return process;
	}


	/**
	 * 进入创建工单页面
	 *
	 * @param userId 查询的用户的id 查找该用户的名称
	 * @return
	 */
	@RequestMapping(value = "/add", method = RequestMethod.GET)
	public String add(Model model, Long userId) {

		if (!CheckUtil.isEmpty(userId)) {
			UserInfo userInfo = userInfoService.selectByPrimaryKey(userId);
			if (!CheckUtil.isEmpty(userInfo)) {
				model.addAttribute("user", userInfo);
			}
		}
		//工单类型
		model.addAttribute("createTime", new Date());
		model.addAttribute("createUser", getCurrentUser());
		model.addAttribute("orderType", WorkOrderTypeEnum.values());
		return "model/work/add";
	}


	/**
	 * 创建工单
	 *
	 * @param workOrder 工单对象
	 * @return 处理结果
	 */
	@RequestMapping(value = "/add", method = RequestMethod.POST)
	@ResponseBody
	public String add(WorkOrder workOrder) throws ServiceException {
		final Result orderAndNotify = workOrderService.createOrderAndNotify(workOrder);
		return gson.toJson(orderAndNotify);
	}

	/**
	 * 删除工单
	 *
	 * @param id 工单号
	 * @return
	 */
	@RequestMapping(value = "{id}/delete", method = RequestMethod.GET)
	public String delete(@PathVariable(value = "id") long id) throws ServiceException {
		return gson.toJson(workOrderService.deleteWorkOrder(id));
	}

	/**
	 * 进入编辑页面
	 *
	 * @param orderNo 工单号
	 * @return 页面视图
	 */
	@RequestMapping(value = "{id}/edit", method = RequestMethod.GET)
	public String edit(Model model, long orderNo) throws ServiceException {
		final WorkOrder workOrder = workOrderService.selectByPrimaryKey(orderNo);
		if (CheckUtil.isEmpty(workOrder)) {
			throw new ServiceException("工单号" + orderNo + "不存在！");
		}
		model.addAttribute("order", workOrder);
		return null;
	}

	@RequestMapping(value = "{id}/edit", method = RequestMethod.PUT)
	public String edit(WorkOrder order) throws ServiceException {
		return gson.toJson(workOrderService.updateWorkOrder(order));
	}


	/**
	 * 查看工单详细信息
	 *
	 * @param page 工单号
	 * @return ModelAndView
	 */
	@RequestMapping(value = "list", method = RequestMethod.GET)
	public String list(Model model, Page<WorkOrder> page) {
		page = workOrderService.findPage(page);
		model.addAttribute("page",page);
		return "model/work/work_order_list";
	}


}
