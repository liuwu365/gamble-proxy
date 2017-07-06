package com.lottery.gamble.proxy.core.charts;




import java.util.List;
import java.util.Map;

@SuppressWarnings("all")
public class ReportActionResult extends ActionResult {
	
	private Map reportMap;
	
	private Map reportMap1;
	
	private Map reportMap2;
	
	private List reportList;

	public Map getReportMap() {
		return reportMap;
	}

	public void setReportMap(Map reportMap) {
		this.reportMap = reportMap;
	}
	
	public Map getReportMap1() {
		return reportMap1;
	}

	public void setReportMap1(Map reportMap1) {
		this.reportMap1 = reportMap1;
	}

	public Map getReportMap2() {
		return reportMap2;
	}

	public void setReportMap2(Map reportMap2) {
		this.reportMap2 = reportMap2;
	}

	public List getReportList() {
		return reportList;
	}

	public void setReportList(List reportList) {
		this.reportList = reportList;
	}

}
