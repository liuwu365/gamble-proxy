package com.lottery.gamble.proxy.web.controller.webSocket;

import com.google.gson.GsonBuilder;
import com.lottery.gamble.proxy.core.handler.SpringWebSocketHandler;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.socket.TextMessage;

import javax.annotation.Resource;
import java.io.IOException;
@Controller
@RequestMapping("send")
public class WebSocketController {
    @Resource
    SpringWebSocketHandler webSocketHandler;

    @RequestMapping(value = "GarlicPriceController/testWebSocket", method ={RequestMethod.POST,RequestMethod.GET}, produces = "application/json; charset=utf-8")
    @ResponseBody
    public String testWebSocket() throws IOException {
        webSocketHandler.sendMessageToJsp(new TextMessage(new GsonBuilder().create().toJson("\"number\":\""+"GarlicPriceController/testWebSocket"+"\"")), "AAA");
        return "1";  
    }

    @RequestMapping(value = "test")
    @ResponseBody
    public void sendMsg () {
        final boolean asfasdfadfasdgadgfadsfas = webSocketHandler.sendMessageToUser(51L, new TextMessage("asfasdfadfasdgadgfadsfas"));
        System.out.println(asfasdfadfasdgadgfadsfas);
    }
      
} 