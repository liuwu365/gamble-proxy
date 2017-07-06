package com.lottery.gamble.proxy.web.controller.base;

import com.lottery.gamble.proxy.ueditor.ActionEnter;
import com.lottery.gamble.common.util.JsonUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.PrintWriter;

/**
 * 百度编辑器图片处理
 * Created by Administrator on 2016/7/15.
 */
@Controller
@RequestMapping("/ueditor/jsp")
public class JspController {

    Logger logger = LoggerFactory.getLogger(JspController.class);

    @RequestMapping(value = "controller.jsp",produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public void upload(HttpServletRequest request,HttpServletResponse response) throws Exception {
        logger.info("ueditor param:{}",JsonUtil.gson.toJson(request.getParameterMap()));
        request.setCharacterEncoding( "utf-8" );
        response.setHeader("Content-Type" , "text/html");

        PrintWriter out = response.getWriter();
        String rootPath = "/data/source";
//                this.getClass().getClassLoader().getResource("").getPath().concat("/static/");

        logger.info("rootpath:{}",rootPath);
        logger.info("uri:{}",ActionEnter.class.getClassLoader().getResource("").getFile().toString());
        ActionEnter actionEnter = new ActionEnter(request, rootPath);
        String exec = actionEnter.exec();
        String arg = new String(exec.getBytes(), "UTF-8");
        logger.info("exec result:{}", arg);
        out.write(exec);
    }
}
