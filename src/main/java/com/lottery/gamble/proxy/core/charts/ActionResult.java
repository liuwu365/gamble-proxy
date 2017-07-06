package com.lottery.gamble.proxy.core.charts;

/**
 * 操作结果基类，代表成功／失败，及相关信息
 *
 *
 */
public class ActionResult<T> {
	
	
	private T obj;
	
	/**
	 * 操作是否成功
	 */
	private boolean success = false;

	/**
	 * 相关信息
	 */
	private String msg = "";

	public boolean isSuccess() {
		return success;
	}

	public void setSuccess(boolean success) {
		this.success = success;
	}

	public String getMsg() {
		return msg;
	}

	public void setMsg(String msg) {
		this.msg = msg;
	}

	public T getObj() {
		return obj;
	}

	public void setObj(T obj) {
		this.obj = obj;
	}
}
