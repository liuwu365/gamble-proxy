package com.lottery.gamble.proxy.core.handler;

import com.google.gson.GsonBuilder;
import com.lottery.gamble.common.util.ErrorWriterUtil;
import com.lottery.gamble.proxy.core.constant.BasicConstant;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.socket.*;

import java.io.IOException;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;


/**
 * @author 王亚平
 * websocket 处理器
 */
public class SpringWebSocketHandler implements WebSocketHandler {

    Logger logger = LoggerFactory.getLogger(SpringWebSocketHandler.class);

  
    private static final Map<Long, WebSocketSession> userSocketSessionMap;
  
    static {  
        userSocketSessionMap = new ConcurrentHashMap<Long, WebSocketSession>();
    }  


    private Long getClientCode(WebSocketSession session) {
        final Long key = (Long) session.getAttributes().get(BasicConstant.SESSION_USER_ID);
        userSocketSessionMap.put(key,session);
        if (userSocketSessionMap.get(key)!=null) {
            return key;
        }
        return null;
    }
      
    @Override  
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {  
        // TODO Auto-generated method stub
        Long clientCode = getClientCode(session);
        if (clientCode!= null) {
            //broadcast(new TextMessage(new GsonBuilder().create().toJson("\"number\":\""+i+"\"")));
            String message = "服务器已连接上.....";
            session.sendMessage(new TextMessage(new GsonBuilder().create().toJson(message)));
        }
    }  
  
    @Override  
    public void handleMessage(WebSocketSession session, WebSocketMessage<?> message) throws Exception {  
        // TODO Auto-generated method stub  

        session.sendMessage(message);  
    }  
  
    @Override  
    public void handleTransportError(WebSocketSession session, Throwable exception) throws Exception {  
        // TODO Auto-generated method stub  
        if (session.isOpen()) {
            session.close();  
        }
        // 移除Socket会话
        for (Entry<Long, WebSocketSession> entry : userSocketSessionMap
                .entrySet()) {
            if (entry.getValue().getId().equals(session.getId())) {
                userSocketSessionMap.remove(entry.getKey());
                System.out.println("出错啦,Socket会话已经移除:用户ID:" + entry.getKey());
                break;
            }
        }
    }  
  
    @Override  
    public void afterConnectionClosed(WebSocketSession session, CloseStatus closeStatus) throws Exception {  
        // TODO Auto-generated method stub  
        System.out.println("Websocket:" + session.getId() + "已经关闭");
        // 移除Socket会话
        for (Entry<Long, WebSocketSession> entry : userSocketSessionMap
                .entrySet()) {
            if (entry.getValue().getId().equals(session.getId())) {
                userSocketSessionMap.remove(entry.getKey());
                System.out.println("Socket会话已经关闭:用户ID:" + entry.getKey());
                break;
            }
        }
    }  
  
    @Override  
    public boolean supportsPartialMessages() {  
        // TODO Auto-generated method stub  
        return false;  
    }  
    /** 
     * 群发
     */
    public void broadcast(final TextMessage message) throws IOException {

        // 多线程群发  
        for (Entry<Long, WebSocketSession> entry : userSocketSessionMap
                .entrySet()) {

            if (entry.getValue().isOpen()) {
                new Thread(() -> {
                    try {
                        if (entry.getValue().isOpen()) {
                            entry.getValue().sendMessage(message);
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }).start();

            }

        }  
    }  
      
    /** 
     * 给所有在线用户的实时工程检测页面发送消息 
     *  
     * @param message 
     * @throws IOException 
     */
    public void sendMessageToJsp(final TextMessage message,String type) throws IOException {

        // 多线程群发  
        for (Entry<Long, WebSocketSession> entry : userSocketSessionMap
                .entrySet()) {

            if (entry.getValue().isOpen() && entry.getKey().toString().contains(type)) {
                new Thread(new Runnable() {

                    public void run() {
                        try {
                            if (entry.getValue().isOpen()) {
                                entry.getValue().sendMessage(message);
                            }
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }

                }).start();
            }

        }  
    }

    /**
     * 发送信息给指定用户
     * @param clientId
     * @param message
     * @return
     */
    public boolean sendMessageToUser(long clientId, TextMessage message) {
        WebSocketSession session = userSocketSessionMap.get(clientId);
        if (session == null)
            return false;
        System.out.println("sendMessage:" + session);
        if (!session.isOpen())
            return false;
        try {
            session.sendMessage(message);
        } catch (IOException e) {
            logger.error("send web socket message to user failed |userId:{}|error:{}",clientId, ErrorWriterUtil.WriteError(e).toString());
            return false;
        }
        return true;
    }

    /**
     * 广播信息
     * @param message
     * @return
     */
    public boolean sendMessageToAllUsers(TextMessage message) {
        boolean allSendSuccess = true;
        Set<Long> clientIds = userSocketSessionMap.keySet();
        WebSocketSession session = null;
        for (Long clientId : clientIds) {
            try {
                session = userSocketSessionMap.get(clientId);
                if (session.isOpen()) {
                    session.sendMessage(message);
                }
            } catch (IOException e) {
                e.printStackTrace();
                allSendSuccess = false;
            }
        }

        return  allSendSuccess;
    }
}  