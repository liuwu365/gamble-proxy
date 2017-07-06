package com.lottery.gamble.proxy.web.exception;

/**
 * 自定义异常类
 * @author 王亚平
 */
public class ServiceException extends Exception {
  public ServiceException(String msg) {
    super(msg);
  }
}