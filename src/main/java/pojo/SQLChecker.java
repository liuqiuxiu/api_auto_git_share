package pojo;

import base.Base;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Set;

public class SQLChecker {
	private String no;
	private String sql;
	private String save;
	private List<LinkedHashMap<String, Object>> expectedResult;

	public String getNo() {
		return no;
	}
	public void setNo(String no) {
		this.no = no;
	}
	public String getSql() {
		return sql;
	}
	public void setSql(String sql) {
		this.sql = sql;
	}
	public void setSqlByENV( ){
		sql= Base.replace(sql);
	}
	public void setExpectedResultByENV(){
		List<LinkedHashMap<String, Object>> newexpectedResult = new ArrayList<LinkedHashMap<String, Object>>() ;
		for (LinkedHashMap<String, Object> map:
		expectedResult) {
			LinkedHashMap<String, Object> newMap=new LinkedHashMap<String,Object>();
			Set<String> strings = map.keySet();
			for (String key:
				strings ) {
				String o = (String) map.get(key);
				String replace = Base.replace(o);
				newMap.put(key, replace);
			}
			newexpectedResult.add(newMap);
		}
		expectedResult=newexpectedResult;

	}
	public List<LinkedHashMap<String, Object>> getExpectedResult() {
		return expectedResult;
	}
	public void setExpectedResult(List<LinkedHashMap<String, Object>> expectedResult) {
		this.expectedResult = expectedResult;
	}

	public String getSave() {
		return save;
	}

	public void setSave(String save) {
		this.save = save;
	}

	@Override
	public String toString() {
		return "SQLChecker{" +
				"no='" + no + '\'' +
				", sql='" + sql + '\'' +
				", save='" + save + '\'' +
				", expectedResult=" + expectedResult +
				'}';
	}

}
