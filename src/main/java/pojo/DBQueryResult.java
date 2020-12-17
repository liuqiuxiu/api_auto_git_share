package pojo;

import java.util.Map;

public class DBQueryResult {
	/**
	 * 脚本编号
	 */
	private String no;
	/**
	 * 脚本执行查到的数据，保存到map中，key保存的是字段名，value保存的是对应字段查到的数据
	 * 
	 */
	private Map<String, Object> columnLabelAndValues;
	public String getNo() {
		return no;
	}
	public void setNo(String no) {
		this.no = no;
	}
	public Map<String, Object> getColumnLabelAndValues() {
		return columnLabelAndValues;
	}
	public void setColumnLabelAndValues(Map<String, Object> columnLabelAndValues) {
		this.columnLabelAndValues = columnLabelAndValues;
	}
	@Override
	public String toString() {
		return "DBQueryResult [no=" + no + ", columnLabelAndValues=" + columnLabelAndValues + "]";
	}
	
}
