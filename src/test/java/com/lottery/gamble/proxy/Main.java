package com.lottery.gamble.proxy;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import static com.lottery.gamble.common.util.JsonUtil.gson;

/**
 * @author Created by 王亚平 on 2017/6/23.
 */
public class Main {


	public static void main(String[] args) {

		List<orderHistory>  orderHistories = new LinkedList<>();
		List<orderProcess>  orderProcesses = new LinkedList<>();

		String orderNo = "CW20161223001";


		orderHistories.add(new orderHistory(1,0,orderNo,"w1","1"));

		orderHistories.add(new orderHistory(2,0,orderNo,"w2","adf"));

		orderHistories.add(new orderHistory(3,0,orderNo,"ww","gh"));


		orderProcesses.add(new orderProcess(6,1,orderNo,"q1","zvz"));

		orderProcesses.add(new orderProcess(5,1,orderNo,"q2","agh"));

		orderProcesses.add(new orderProcess(4,1,orderNo,"q3","zvav"));

		List<Common> all = new LinkedList();
		all.addAll(orderHistories);
		all.addAll(orderProcesses);

		Collections.sort(all);


		for (Common o : all) {
			if (o instanceof orderHistory) {
				System.out.println(gson.toJson((orderHistory)o));
			} else {
				System.out.println(gson.toJson((orderProcess)o));
			}
		}


	}
}

class Common implements Comparable<Common> {

	private int order;

	private int type;

	public Common(int order) {
		this.order = order;
	}

	public Common(int order, int type) {
		this.order = order;
		this.type = type;
	}

	@Override
	public int compareTo(Common o) {
		final int i = this.order - o.order;
		if (i == 0) { //相等
			return 0;
		} else if (i > 0) { //当前对象order 大
			return 1;
		} else {
			return -1;
		}
	}

	public int getOrder() {
		return order;
	}

	public void setOrder(int order) {
		this.order = order;
	}

	public int getType() {
		return type;
	}

	public void setType(int type) {
		this.type = type;
	}
}

class orderHistory extends Common{
	private String orderNo;
	private String createUser;
	private String remarks;

	public orderHistory(int order,int type,String orderNo, String createUser, String remarks) {
		super(order,type);
		this.orderNo = orderNo;
		this.createUser = createUser;
		this.remarks = remarks;
	}

	public String getOrderNo() {
		return orderNo;
	}

	public orderHistory setOrderNo(String orderNo) {
		this.orderNo = orderNo;
		return this;
	}

	public String getCreateUser() {
		return createUser;
	}

	public orderHistory setCreateUser(String createUser) {
		this.createUser = createUser;
		return this;
	}

	public String getRemarks() {
		return remarks;
	}

	public orderHistory setRemarks(String remarks) {
		this.remarks = remarks;
		return this;
	}
}


class orderProcess extends Common {
	private String orderNo;
	private String processUser;
	private String remarks;

	public orderProcess(int order,int type, String orderNo,String processUser, String remarks) {
		super(order,type);
		this.orderNo = orderNo;
		this.processUser = processUser;
		this.remarks = remarks;
	}

	public String getProcessUser() {
		return processUser;
	}

	public orderProcess setProcessUser(String processUser) {
		this.processUser = processUser;
		return this;
	}

	public String getOrderNo() {
		return orderNo;
	}

	public orderProcess setOrderNo(String orderNo) {
		this.orderNo = orderNo;
		return this;
	}
	public String getRemarks() {
		return remarks;
	}

	public orderProcess setRemarks(String remarks) {
		this.remarks = remarks;
		return this;
	}

	@Override
	public void setOrder(int order) {
		super.setOrder(order);
	}
}
