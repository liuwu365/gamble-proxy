package com.lottery.gamble.proxy.base;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.lottery.gamble.proxy.core.config.SpringDevConfig;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.UsernamePasswordToken;
import org.apache.shiro.mgt.SecurityManager;
import org.apache.shiro.subject.Subject;
import org.apache.shiro.util.ThreadContext;
import org.junit.After;
import org.junit.Before;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import javax.annotation.Resource;

/**
 * @author Created by liuyuanzhou on 4/15/16.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = SpringDevConfig.class)
@TestPropertySource(locations = "classpath:application.properties")
@WebAppConfiguration
public class BaseTest {

    Gson gsonBuilder = new GsonBuilder().serializeNulls().create();

    Gson gson = new Gson();


    public String objToStr(Object object) throws JsonProcessingException {

        //设置值
        ObjectMapper mapper = new ObjectMapper();
        ObjectWriter ow = mapper.writer().withDefaultPrettyPrinter();
        return ow.writeValueAsString(object);
    }

    @Autowired
    public WebApplicationContext wac;

    public MockMvc mvc;

    @Resource
    SecurityManager securityManager;

    @Before
    public void setUp() {
        //this.mvc = MockMvcBuilders.standaloneSetup(workOrderController).build();
        this.mvc = MockMvcBuilders.webAppContextSetup(wac).build();
        login ("wyp", "123456");

    }

    public void login (String username,String password) {
        ThreadContext.bind(securityManager);
        UsernamePasswordToken token = new UsernamePasswordToken(username,password); //获取当前的Subject
        Subject currentUser = SecurityUtils.getSubject();
        currentUser.login(token);
     }

     public void logout () {
         SecurityUtils.getSubject().logout();
     }


    @After
    public void stop () {
        logout ();
    }

}