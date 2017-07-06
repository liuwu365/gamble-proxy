package com.lottery.gamble.proxy.web.controller;

import com.lottery.gamble.common.async.AsyncUtil;
import com.lottery.gamble.common.entity.Message;
import com.lottery.gamble.entity.BackUser;
import com.lottery.gamble.proxy.core.util.SessionUtil;
import com.lottery.gamble.proxy.web.controller.base.BaseController;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.AsyncContext;
import javax.servlet.AsyncEvent;
import javax.servlet.AsyncListener;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * @author Created by liuyuanzhou on 4/15/16.
 */
@Controller
@RequestMapping("/async")
public class AsyncController extends BaseController {


    Logger logger = LoggerFactory.getLogger(AsyncController.class);


    @RequestMapping(value = "/comet.json", method = RequestMethod.GET)
    public void comet(
            //@RequestParam final int uid,
            HttpServletRequest request, HttpServletResponse response) {
        AsyncContext context = request.startAsync(request, response);
        context.setTimeout(30000);
        final BackUser currentUser = SessionUtil.getCurrentUser();
        if (currentUser == null)
            return;

        int uid = Math.toIntExact(currentUser.getId());
        context.addListener(new AsyncListener() {
            @Override
            public void onComplete(AsyncEvent asyncEvent) throws IOException {
                //final AsyncContext asyncContext = asyncEvent.getAsyncContext();
                //logger.info("onComplete {}",uid);
            }

            @Override
            public void onTimeout(AsyncEvent asyncEvent) throws IOException {
                AsyncContext context1 = asyncEvent.getAsyncContext();
                AsyncUtil.forceResponse(uid, context1);
                //logger.info("onTimeout {}",uid);
            }

            @Override
            public void onError(AsyncEvent asyncEvent) throws IOException {
                //logger.error("onError {}",uid);
            }

            @Override
            public void onStartAsync(AsyncEvent asyncEvent) throws IOException {
                //logger.error("onStartAsync {}",uid);
            }
        });
        AsyncUtil.tryResponse(uid, context);
    }


    @RequestMapping(value = "addMsg.json", method = RequestMethod.GET)
    public void addMsg(@RequestParam int from, @RequestParam int to, @RequestParam String msg,
                       HttpServletRequest request, HttpServletResponse response) {
        Message<String> ms = new Message<String>();
        ms.setFrom(from);
        ms.setTo(to);
        ms.setMsgContent(msg);
        ms.setTs(System.currentTimeMillis());
        AsyncUtil.addMsg(ms);
    }


}
