package com.lottery.gamble.proxy.web.service.workOrder;

import com.lottery.gamble.common.async.AsyncUtil;
import com.lottery.gamble.common.entity.Message;
import com.lottery.gamble.common.enums.work.WorkOrderStatusEnum;
import com.lottery.gamble.dao.BackUserMapper;
import com.lottery.gamble.dao.UserInfoMapper;
import com.lottery.gamble.dao.WorkOrderMapper;
import com.lottery.gamble.dao.WorkProcessMapper;
import com.lottery.gamble.entity.BackUser;
import com.lottery.gamble.entity.Page;
import com.lottery.gamble.entity.Result;
import com.lottery.gamble.entity.user.UserInfo;
import com.lottery.gamble.entity.work.WorkOrder;
import com.lottery.gamble.helper.GenerateOrderHelper;
import com.lottery.gamble.helper.PageHelper;
import com.lottery.gamble.proxy.core.constant.BasicConstant;
import com.lottery.gamble.proxy.core.util.CheckUtil;
import com.lottery.gamble.proxy.core.util.EmailUtil;
import com.lottery.gamble.proxy.core.util.ErrorWriterUtil;
import com.lottery.gamble.proxy.core.util.SessionUtil;
import com.lottery.gamble.proxy.web.exception.ServiceException;
import org.apache.shiro.session.Session;
import org.apache.shiro.session.mgt.eis.EnterpriseCacheSessionDAO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Collection;
import java.util.List;

/**
 * @author Created by 王亚平 on 2017/6/16.
 */
@Service
public class WorkOrderServiceImpl implements WorkOrderService {


	private Logger logger = LoggerFactory.getLogger(WorkOrderServiceImpl.class);

	final Object object = new Object();

	@Value("${work.order.notify.admin.context}")
	private String notifyAdminContext;

	@Resource
	private WorkOrderMapper workOrderMapper;

	@Resource
	private WorkProcessMapper workProcessMapper;

	@Resource
	private UserInfoMapper userInfoMapper;


	@Resource
	private EmailUtil emailUtil;

	@Resource
	private GenerateOrderHelper generateOrderHelper;

	@Resource
	private BackUserMapper backUserMapper;

	@Resource(name = "sessionDAO")
	private EnterpriseCacheSessionDAO enterpriseCacheSessionDAO;

	/**
	 * 创建工单
	 *
	 * @param order 工单对象
	 * @return 处理结果
	 * @throws ServiceException
	 */
	@Override
	public Result createOrderAndNotify(WorkOrder order) throws ServiceException {
		Result result = null;
		/*
		 * 验证用户数据
         * 1.创建入口如果是客服查询结果页面，则账号默认显示当前查询用户
         * 2.该用户是客服直接创建，否则
         */
		BackUser currentUser = SessionUtil.getCurrentUser();
		if (CheckUtil.isEmpty(order.getUserId())) {
			String userName = order.getUserName();
			UserInfo userInfo = userInfoMapper.selectUserInfoByUserName(userName);
			if (CheckUtil.isEmpty(userInfo) || CheckUtil.isEmpty(userInfo.getId())) {
				throw new ServiceException("用户 [" + userName + "]不存在！");
			}
			//设置用户id
			order.setUserId(userInfo.getId());
		}
		try {
			order.setStatus(WorkOrderStatusEnum.Pending);
			order.setDelete(false);
			order.setFromDeptId(Long.valueOf(currentUser.getDeptId()));
			order.setFromRoleId(Long.valueOf(currentUser.getGroupId()));
			synchronized (object) {
				order.setOrderNo(generateOrderHelper.genOrderNum(order.getOrderType()));
				insertSelective(order);
			}
		} catch (Exception e) {
			throw new ServiceException("创建工单错误！");
		}
		if (!CheckUtil.isEmpty(order.getId())) {
			//发送通知 ,通知客服选的用户组 抢单 ....后续再处理
			final Long sendUserGroup = order.getRoleId();
			final List<BackUser> backUsers = backUserMapper.selectUserListByGroupId(sendUserGroup);
			if (!CheckUtil.isEmpty(backUsers)) {
				for (int i = 0; i < backUsers.size(); i++) {
					final BackUser backUser = backUsers.get(i);
					if (isOnline(backUser.getId())) {
						Message<String> msg = new Message<String>();
						msg.setFrom(Math.toIntExact(currentUser.getId()));
						msg.setTo(Math.toIntExact(backUser.getId()));//模拟一个账号
						msg.setTs(System.currentTimeMillis());
						msg.setMsgType(2);
						msg.setMsgContent(notifyAdminContext);
						AsyncUtil.addMsg(msg);
					}
					if (!CheckUtil.isEmpty(backUser.getEmail())) {
						emailUtil.sendEmail(backUser.getEmail());
					}
				}
			}
			result = Result.success();
		} else {
			result = Result.failure("创建工单失败！");
		}
		return result;
	}


	public boolean isOnline(Long userId) {
		if (!CheckUtil.isEmpty(userId)) {
			final Collection<Session> activeSessions = enterpriseCacheSessionDAO.getActiveSessions();
			for (Session activeSession : activeSessions) {
				BackUser backUser = (BackUser) activeSession.getAttribute(BasicConstant.BACK_USER_INFO);
				if (!CheckUtil.isEmpty(backUser)) {
					if (backUser.getId().equals(userId)) {
						return true;
					}
				}
			}
		}
		return false;
	}


	/**
	 * 删除工单
	 *
	 * @param id 工单id
	 * @return
	 * @throws ServiceException
	 */
	public Result deleteWorkOrder(long id) throws ServiceException {

		final WorkOrder workOrder = selectByPrimaryKey(id);
		if (CheckUtil.isEmpty(workOrder)) {
			throw new ServiceException("工单不存在");
		}
		try {
			workOrder.setDelete(true);//设置删除标志
			final int i = updateByPrimaryKey(workOrder);
			return i == 1 ? Result.success() : Result.failure();
		} catch (Exception e) {
			logger.error("delete work order error|{}", ErrorWriterUtil.WriteError(e).toString());
			throw new ServiceException("删除工单失败！");
		}
	}

	/**
	 * 更新工单信息
	 *
	 * @param order 工单对象
	 * @return
	 * @throws ServiceException
	 */
	@Override
	public Result updateWorkOrder(WorkOrder order) throws ServiceException {
		boolean b = false;
		try {
			b = updateByPrimaryKey(order) == 1;
		} catch (Exception e) {
			throw new ServiceException("更新工单错误");
		}
		if (!b) {
			throw new ServiceException("更新工单失败");
		}
		return Result.success();
	}

	@Override
	public WorkOrder selectByOrderNo(String orderNo) throws ServiceException {
		try {
			return workOrderMapper.selectByOrderNo(orderNo);
		} catch (Exception e) {
			throw new ServiceException("订单查询错误");
		}
	}


	public List<WorkOrder> selectByStatus(WorkOrderStatusEnum status, Long userId) throws ServiceException {
		try {
			return workOrderMapper.selectByStatus(status, userId);
		} catch (Exception e) {
			logger.error("selectByStatus error|userId{}|status{}|{}", userId, status.getDesc(), ErrorWriterUtil.WriteError(e));
			throw new ServiceException("订单信息查询错误");
		}
	}

	public List<WorkOrder> selectByGroupOrMe(WorkOrderStatusEnum status, Long deptId, Long groupId, Long userId) throws ServiceException {
		try {
			return workOrderMapper.selectByDeptAndGroup(status, deptId, groupId, userId);
		} catch (Exception e) {
			logger.error("selectByGroupOrMe error|deptId|{}|groupId|{}|userId|{}|error|{}", deptId, groupId, userId, ErrorWriterUtil.WriteError(e));
			throw new ServiceException("订单信息查询错误");
		}
	}

	public List<WorkOrder> selectByCreateUser(Long deptId, Long groupId, Long userId) throws ServiceException {
		try {
			return workOrderMapper.selectByCreateUser(deptId, groupId, userId);
		} catch (Exception e) {
			logger.error("selectByCreateUser error|deptId|{}|groupId|{}|userId|{}|error|{}", deptId, groupId, userId, ErrorWriterUtil.WriteError(e));
			throw new ServiceException("订单信息查询错误");
		}
	}


	@Override
	public Page<WorkOrder> findPage(Page<WorkOrder> page) {
		return new PageHelper<WorkOrder>(workOrderMapper).invoke(page);
	}

	@Override
	public List<WorkOrder> selectAll() {
		return null;
	}

	@Override
	public int deleteByPrimaryKey(Long id) {
		return 0;
	}

	@Override
	public int insert(WorkOrder obj) {
		return workOrderMapper.insert(obj);
	}

	@Override
	public int insertSelective(WorkOrder obj) {
		return workOrderMapper.insertSelective(obj);
	}

	@Override
	public WorkOrder selectByPrimaryKey(Long id) {
		return null;
	}

	@Override
	public int updateByPrimaryKeySelective(WorkOrder obj) {
		return 0;
	}

	@Override
	public int updateByPrimaryKey(WorkOrder obj) {
		return workOrderMapper.updateByPrimaryKey(obj);
	}

}
