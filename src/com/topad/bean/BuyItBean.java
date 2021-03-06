package com.topad.bean;

import java.io.Serializable;

/**
 * ${todo}<购买产品实体>
 *
 * @author lht
 * @data: on 15/10/26 11:06
 */
public class BuyItBean implements Serializable {
	/**
	 * serialVersionUID:TODO（用一句话描述这个变量表示什么）
	 */
	private static final long serialVersionUID = 4768927122317982665L;
	/** needid **/
	public String needid;
	/** 状态码 **/
	protected int status;
	/** error信息 **/
	protected String msg;

	public String getNeedid() {
		return needid;
	}

	public void setNeedid(String needid) {
		this.needid = needid;
	}

	public int getStatus() {
		return status;
	}

	public void setStatus(int status) {
		this.status = status;
	}

	public String getMsg() {
		return msg;
	}

	public void setMsg(String msg) {
		this.msg = msg;
	}
}