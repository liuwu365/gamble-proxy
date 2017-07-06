package com.lottery.gamble.proxy.controller;

import com.lottery.gamble.common.enums.work.WorkOrderStatusEnum;
import com.lottery.gamble.common.enums.work.WorkOrderTypeEnum;
import com.lottery.gamble.common.util.DateUtil;
import com.lottery.gamble.common.util.gson.GsonBuilderUtil;
import com.lottery.gamble.entity.work.WorkOrder;
import com.lottery.gamble.proxy.base.BaseTest;
import com.lottery.gamble.proxy.web.exception.ServiceException;
import com.lottery.gamble.proxy.web.service.workOrder.WorkOrderService;
import net.sourceforge.groboutils.junit.v1.MultiThreadedTestRunner;
import net.sourceforge.groboutils.junit.v1.TestRunnable;
import org.junit.Test;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Random;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * @author Created by 王亚平 on 2017/6/15.
 */
public class WorkOrderControllerTest extends BaseTest {


    @Resource
    private WorkOrderService workOrderService;


    @Resource
    TemplateEngine templateEngine;

    @Test
    public void testThymeleafEngine() throws ServiceException {
        final WorkOrder result = workOrderService.selectByOrderNo("CW20170627001");
        Context context = new Context();
        List<WorkOrder> list = new ArrayList<WorkOrder>();
        list.add(result);
        context.setVariable("data",list);
        final String process = templateEngine.process("/vm/workOrder", context);
        System.out.println(process);

    }

    @Test
    public void getPendingOrders() throws Exception {

        final ResultActions resultActions = mvc.perform(post("/work/order/getPendingOrders.json"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andDo(print());

        System.out.println(resultActions);
    }


    @Test
    public void createOrder() throws Exception {
        WorkOrder order = new WorkOrder();
        order.setCreateUser(1L);
        order.setCreateTime(new Date());
        order.setEventId("12312312");
        order.setOrderType(WorkOrderTypeEnum.FINANCE);
        order.setStatus(WorkOrderStatusEnum.Pending);
        order.setImgUrl("/user/1.jpg");
        order.setUserId(2L);
        order.setDeptId(1L);
        order.setRoleId(1L);
        order.setProblemDescription("问题描述11111111111111111111");
        order.setDelete(false);
        String json = GsonBuilderUtil.create().toJson(order);
        final String currTime = DateUtil.getCurrTime();
        System.out.println(currTime);
        for (int i=0;i<1000;i++) {
            mvc.perform(post("/work/order/add")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(json))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.code").value(200));
                    //.andDo(print());


        }
        final String currTime1 = DateUtil.getCurrTime();
        final long x = Long.parseLong(currTime1) - Long.parseLong(currTime);
        System.out.println(x/1000);

    }

    /**
     * 多线程测试用例
     */
    @Test
    public void MultiRequestsTest() {
        final int[] count = {0};
        WorkOrder order = new WorkOrder();
        order.setCreateUser(1L);
        order.setCreateTime(new Date());
        order.setEventId("12312312");
        order.setOrderType(WorkOrderTypeEnum.FINANCE);
        order.setStatus(WorkOrderStatusEnum.Pending);
        order.setImgUrl("/user/1.jpg");
        order.setUserId(2L);
        order.setDeptId(1L);
        order.setRoleId(1L);
        order.setProblemDescription("问题描述11111111111111111111");
        order.setDelete(false);
        String json = GsonBuilderUtil.create().toJson(order);
        order.setEventId("org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;");
        String json1 = GsonBuilderUtil.create().toJson(order);
        String [] js = new String[10];
        for (int i = 0; i < js.length; i++) {
            if (i==5 || i==3 || i==7) {
                js[i] = json1;
            }
            js[i] = json;

        }
        // 构造一个Runner
        TestRunnable runner = new TestRunnable() {

            private String json;

            public String getJson() {
                return json;
            }

            public void setJson(String json) {
                this.json = json;
            }

            @Override
            public void runTest() throws Throwable {
                // 测试内容


                try {
                    mvc.perform(post("/work/order/add")
							.contentType(MediaType.APPLICATION_JSON)
							.content(js[new Random().nextInt(10)]));
							//.andExpect(status().isOk())
							//.andExpect(jsonPath("$.code").value(200));
                    //.andDo(print());
                } catch (Exception e) {
                    if (e instanceof ServiceException) {
                        count[0]++;
                    }
                }

            }
        };
        int runnerCount = 1000;
        //Rnner数组，想当于并发多少个。
        TestRunnable[] trs = new TestRunnable[runnerCount];
        for (int i = 0; i < runnerCount; i++) {
            trs[i] = runner;
        }
        // 用于执行多线程测试用例的Runner，将前面定义的单个Runner组成的数组传入
        MultiThreadedTestRunner mttr = new MultiThreadedTestRunner(trs);
        try {
            // 开发并发执行数组里定义的内容
            mttr.runTestRunnables();
        } catch (Throwable e) {
            e.printStackTrace();
        }

        System.out.println("error count:"+count[0]);
    }



    @Test
    public void delete_work() throws Exception {
        final MvcResult mvcResult = mvc.perform(MockMvcRequestBuilders.get("/work_order/5/delete"))
                //.andExpect(status().isOk())
                //.andExpect(jsonPath("$",hasSize()))
                .andExpect(jsonPath("$.code").value(400))
                .andExpect(jsonPath("$.msg").value("业务逻辑异常：工单不存在"))
                .andDo(print()).andReturn();
    }
}
