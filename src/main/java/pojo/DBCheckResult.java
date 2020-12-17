package pojo;

import java.util.LinkedHashMap;
import java.util.List;

public class DBCheckResult {
	/**
	 * 验证sql的编号
	 */
	private String no;
	/**
	 * 
	 */
	private List<LinkedHashMap<String, Object>>  actualResultList;
	/**
	 * 验证成功|失败
	 */
	private String result;
	public String getNo() {
		return no;
	}
	public void setNo(String no) {
		this.no = no;
	}
	
	public DBCheckResult() {
		super();
	}
	public DBCheckResult(String no, List<LinkedHashMap<String, Object>> actualResultList, String result) {
		super();
		this.no = no;
		this.actualResultList = actualResultList;
		this.result = result;
	}
	@Override
	public String toString() {
		return "DBCheckResult [no=" + no + ", actualResultList=" + actualResultList + ", result=" + result + "]";
	}
	public List<LinkedHashMap<String, Object>> getActualResultList() {
		return actualResultList;
	}
	public void setActualResultList(List<LinkedHashMap<String, Object>> actualResultList) {
		this.actualResultList = actualResultList;
	}
	public String getResult() {
		return result;
	}
	public void setResult(String result) {
		this.result = result;
	}
	
}
