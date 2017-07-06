package com.lottery.gamble.proxy.controller;

import com.lottery.gamble.dao.OperationLogMapper;
import com.lottery.gamble.dao.WorkOrderMapper;
import com.lottery.gamble.entity.operation.OperationLog;
import com.lottery.gamble.entity.work.WorkOrder;
import com.lottery.gamble.entity.work.WorkProcess;
import com.lottery.gamble.proxy.base.BaseTest;
import com.lottery.gamble.proxy.core.enums.WorkProcessResult;
import com.lottery.gamble.common.util.gson.GsonBuilderUtil;
import org.junit.Test;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import javax.annotation.Resource;
import java.util.Date;

import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * @author Created by 王亚平 on 2017/6/22.
 */
public class WorkProcessControllerTest extends BaseTest {


	String orderNo = "CW20170627001";

	//抢单
	@Test
	public void testGrabOrder() throws Exception {

		mvc.perform(MockMvcRequestBuilders.get("/work/process/"+orderNo+"/grab.json")
				.contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.code").value(200))
				.andDo(print())
		;

	}

	@Resource
	public OperationLogMapper operationLogMapper;

	@Resource
	public WorkOrderMapper workOrderMapper;

	/**
	 * 添加操作日志
	 */
	@Test
	public void addLog() {
		final WorkOrder order = workOrderMapper.selectByOrderNo(orderNo);
		OperationLog log = new OperationLog();
		log.setOrderNo(orderNo);
		log.setIpAddress("127.0.0.1");
		log.setOperUserId(order.getUserId());
		log.setOperUserName("test");
		log.setOperType(3);
		log.setCreateTime(new Date());
		log.setContent("充值 200");
		operationLogMapper.insertSelective(log);
	}

	//抢单人员处理完成
	@Test
	public void process () throws Exception {
		WorkProcess workProcess = new WorkProcess();
		workProcess.setOrderNo(orderNo);
		workProcess.setOperationId(3452L);
		workProcess.setProcessResult(WorkProcessResult.COMPLETE.getDesc());
		workProcess.setProcessUserId(1L);
		workProcess.setRemarks("处理完成");
		mvc.perform(MockMvcRequestBuilders.post("/work/process/process.json")
				.contentType(MediaType.APPLICATION_JSON_UTF8_VALUE).content(GsonBuilderUtil.create().toJson(workProcess)))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.code").value(200))
				.andDo(print())
		;
	}



}
