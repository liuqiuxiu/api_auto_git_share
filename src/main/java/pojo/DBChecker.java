package pojo;

import javax.management.loading.PrivateClassLoader;

import org.omg.CORBA.PRIVATE_MEMBER;
/**
 * 
 * 数据库验证实体类
 *
 */
public class DBChecker {
	/**
	 * 编号
	 */
	private String no;
	/**
	 * sql语句
	 */
	private String sql;
	
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


	@Override
	public String toString() {
		return "DBChecker [no=" + no + ", sql=" + sql + "]";
	}
}
