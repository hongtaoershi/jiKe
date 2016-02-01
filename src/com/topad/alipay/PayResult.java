package com.topad.alipay;import android.text.TextUtils;public class PayResult {	public String resultStatus;	public String result;	public String memo;	public String out_trade_no;	public String seller_id;	public String subject;	public String body;	public String total_fee;	public PayResult(String rawResult) {		if (TextUtils.isEmpty(rawResult))			return;		String[] resultParams = rawResult.split(";");		for (String resultParam : resultParams) {			if (resultParam.startsWith("resultStatus")) {				resultStatus = gatValue(resultParam, "resultStatus");			}			if (resultParam.startsWith("result")) {				result = gatValue(resultParam, "result");			}			if (resultParam.startsWith("memo")) {				memo = gatValue(resultParam, "memo");			}		}	}	@Override	public String toString() {		return "resultStatus={" + resultStatus + "};memo={" + memo				+ "};result={" + result + "}";	}	private String gatValue(String content, String key) {		String prefix = key + "={";		return content.substring(content.indexOf(prefix) + prefix.length(),				content.lastIndexOf("}"));	}	/**	 * @return the resultStatus	 */	public String getResultStatus() {		return resultStatus;	}	/**	 * @return the memo	 */	public String getMemo() {		return memo;	}	/**	 * @return the memo	 */	public String getout_trade_no() {		return out_trade_no;	}	/**	 * @return the memo	 */	public String getseller_id() {		return seller_id;	}	public String getsubject() {		return subject;	}	public String getbody() {		return body;	}	public String gettotal_fee() {		return total_fee;	}	/**	 * @return the result	 */	public String getResult() {		return result;	}}