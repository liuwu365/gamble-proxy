package com.lottery.gamble.proxy.core.interceptor;

import com.lottery.gamble.proxy.core.constant.BasicConstant;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.http.server.ServletServerHttpRequest;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.server.HandshakeInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.Map;

/**
 * @author 王亚平
 * @description 拦截器
 */
public class webSocketInterceptor implements HandshakeInterceptor{
  
    @Override  
    public boolean beforeHandshake(ServerHttpRequest request, ServerHttpResponse response, WebSocketHandler wsHandler,  
            Map<String, Object> attributes) throws Exception {  
        // TODO Auto-generated method stub  
        HttpServletRequest servletRequest = ((ServletServerHttpRequest) request).getServletRequest();

        HttpSession session = servletRequest.getSession(false);
        Long username = (Long) session.getAttribute(BasicConstant.SESSION_USER_ID);
        // 标记用户
        if(username !=null){
            attributes.put(BasicConstant.SESSION_USER_ID, username);
        }else{
            return false;  
        }
        return true;  
    }  
  
    @Override  
    public void afterHandshake(ServerHttpRequest request, ServerHttpResponse response, WebSocketHandler wsHandler,  
            Exception exception) {  
        // TODO Auto-generated method stub
    }  
  
} 