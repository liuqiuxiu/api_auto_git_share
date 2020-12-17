package utils;

import org.apache.commons.dbutils.QueryRunner;
import org.apache.commons.dbutils.handlers.ScalarHandler;

import java.io.File;
import java.lang.reflect.Method;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

public class JDBCUtil {

	public static List<String> databaseNameList=new ArrayList<String>();


	public static Connection getConnection(String sql) {
		// 创建一个连接
		Connection conn = null;
		try {
			// 创建一个连接

			conn = DriverManager.getConnection(PropertiesUtil.jdbcProperties.getProperty("jdbc.url"),
					PropertiesUtil.jdbcProperties.getProperty("jdbc.user"),
					PropertiesUtil.jdbcProperties.getProperty("jdbc.password"));
		} catch (Exception e) {
			e.printStackTrace();
		}
		return conn;
	}

	/**
	 * 获取单条查询结果
	 *
	 * @param sql 查询sql
	 * @return
	 */
	public static Object queryone(String sql) {
		if (sql == null)
			return null;
		Object result = null;
		// 建立连接
		Connection conn = getConnection(sql);
		try {
			// 建立一个runner对象
			QueryRunner runner = new QueryRunner();
			// 调用方法查询，并取的结果
			result = runner.query(conn, sql, new ScalarHandler<>());
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			close(conn);
		}
		return result;

	}

	/*
	 * public static List<Map<String, Object>> query(String sql) {
	 * List<Map<String,Object>> list=new ArrayList<Map<String,Object>>(); if (sql ==
	 * null) return null; Object result = null; // 建立连接 Connection conn =
	 * getConnection(); try { // 建立一个runner对象 QueryRunner runner = new
	 * QueryRunner(); // 调用方法查询，并取的结果 result = runner.query(conn, sql, new
	 * ScalarHandler<>()); } catch (SQLException e) { e.printStackTrace(); } finally
	 * { close(conn); } return list;
	 *
	 * }
	 */

	/**
	 * 查询sql，将结果以键值对的形式保存到list中
	 *
	 * @param sql
	 * @param values
	 * @return
	 */
	public static List<LinkedHashMap<String, Object>> query(String sql, Object... values) {
		Connection connection = null;
		PreparedStatement preparedStatement = null;
		// 结果处理集
		ResultSet resultSet = null;
		ResultSetMetaData metaData = null;
		// 定义一个list保存结果
		List<LinkedHashMap<String, Object>> list = new ArrayList<LinkedHashMap<String, Object>>();
		// 表的列名
		String[] colNames = null;
		if (sql == null) {

			return null;
		}
		// 建立连接
		try {
			connection = getConnection(sql);
			// 创建PreparedStatement对象，用于将参数化SQL语句发送到数据库。
			PreparedStatement prepareStatement = connection.prepareStatement(sql);
			// 装载参数
			for (int i = 0; i < values.length; i++) {
				prepareStatement.setObject(i + 1, values[i]);
			}
			// 执行查询语句，并获得结果集
			resultSet = prepareStatement.executeQuery(sql);

			// 检索此ResultSet对象的列的数目、类型和属性。
			metaData = resultSet.getMetaData();
			// 获取查询的列数
			int count = metaData.getColumnCount();
			colNames = new String[count];
			for (int i = 1; i <= count; i++) {
				// 获取列名,放到数组里面
				colNames[i - 1] = metaData.getColumnLabel(i);
			}
			// 如果结果集中有数据，循环取出数据
//			while(!resultSet.isAfterLast()) {
			while (resultSet.next()) {
				LinkedHashMap<String, Object> map = new LinkedHashMap<String, Object>();
				// 遍历列
				for (int i = 0; i < colNames.length; i++) {
					String colName = colNames[i];
					if (resultSet.getObject(colName) != null) {
						map.put(colName, resultSet.getObject(colName).toString());
					} else {
						map.put(colName, "");
					}

				}
				list.add(map);
			}
//			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			close(connection, preparedStatement, resultSet);
		}
		return list;

	}

	/**
	 * 获取多条查询结果
	 *
	 * @param sql 查询sql
	 * @return
	 */
	public static <T> List<T> query(String sql, Class<T> clazz, Object... values) {
		Connection connection = null;
		PreparedStatement preparedStatement = null;
		// 结果处理集
		ResultSet resultSet = null;
		ResultSetMetaData metaData = null;
		List<T> list = new ArrayList<T>();
		// 表的列名
		String[] colNames = null;
		if (sql == null) {

			return null;
		}
		// 建立连接
		try {
			connection = getConnection(sql);
			// 创建PreparedStatement对象，用于将参数化SQL语句发送到数据库。
			PreparedStatement prepareStatement = connection.prepareStatement(sql);
			// 装载参数
			for (int i = 0; i < values.length; i++) {
				prepareStatement.setObject(i + 1, values[i]);
			}
			// 执行查询语句，并获得结果集
			resultSet = prepareStatement.executeQuery(sql);

			// 检索此ResultSet对象的列的数目、类型和属性。
			metaData = resultSet.getMetaData();
			// 获取查询的列数
			int count = metaData.getColumnCount();
			colNames = new String[count];
			for (int i = 1; i <= count; i++) {
				// 获取列名,放到数组里面
				colNames[i - 1] = metaData.getColumnLabel(i);
			}
			// 获取对象的所有方法名
			Method[] methods = clazz.getMethods();
			// 如果结果集中有数据，循环取出数据
//			while(!resultSet.isAfterLast()) {
			while (resultSet.next()) {
				// 实例化对象
				T object = clazz.newInstance();
				// 遍历列
				for (int i = 0; i < colNames.length; i++) {
					String colName = colNames[i];
					// 获得方法名
					String methodName = "set" + colName.substring(0, 1).toUpperCase() + colName.substring(1);
					// 遍历所有的方法名
					for (Method md : methods) {
						if (methodName.equals(md.getName())) {
							md.invoke(object, resultSet.getObject(colName));
						}
					}

				}
				list.add(object);
			}
//			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			close(connection, preparedStatement, resultSet);
		}
		return list;

	}

	/**
	 * 查询结果封装到数组中
	 *
	 * @param sql
	 * @param values
	 * @return
	 */
	public static List<String> queryToArray(String sql, Object... values) {
		Connection connection = null;
		PreparedStatement preparedStatement = null;
		// 结果处理集
		ResultSet resultSet = null;
		ResultSetMetaData metaData = null;
		List<String> list = new ArrayList<String>();
		String stringArray = "";
		if (sql == null) {
			return null;
		}
		// 建立连接
		try {
			connection = getConnection(sql);
			// 创建PreparedStatement对象，用于将参数化SQL语句发送到数据库。
			PreparedStatement prepareStatement = connection.prepareStatement(sql);
			// 装载参数
			for (int i = 0; i < values.length; i++) {
				prepareStatement.setObject(i + 1, values[i]);
			}
			// 执行查询语句，并获得结果集
			resultSet = prepareStatement.executeQuery(sql);

			// 检索此ResultSet对象的列的数目、类型和属性。
			metaData = resultSet.getMetaData();
			int columnCount = metaData.getColumnCount();
			String value = null;
			// 如果结果集中有数据，循环取出数据
			while (resultSet.next()) {
				// 实例化对象
				// 遍历列
				for (int i = 1; i <= columnCount; i++) {
					value = resultSet.getString(i);
					if (value == null) {
						value = "";
					}
					list.add(value);
				}
			}


//			String[] strings = new String[list.size()];
//
//			list.toArray(strings);
//			stringArray=Arrays.toString(strings);
//			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			close(connection, preparedStatement, resultSet);
		}
		return list;

	}


	/**
	 * 获取多条查询结果
	 *
	 * @param sql 查询sql
	 * @return
	 */
	public static void delete(String sql, Object... values) {
		Connection connection = null;
		PreparedStatement preparedStatement = null;
		if (sql == null) {
			return;
		}
		// 建立连接
		try {
			connection = getConnection(sql);
			// 创建PreparedStatement对象，用于将参数化SQL语句发送到数据库。
			PreparedStatement prepareStatement = connection.prepareStatement(sql);
			// 装载参数
			for (int i = 0; i < values.length; i++) {
				prepareStatement.setObject(i + 1, values[i]);
			}
			prepareStatement.executeUpdate(sql);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			close(connection, preparedStatement);
		}

	}

	/**
	 * 关闭资源
	 *
	 * @param conn
	 */
	public static void close(Connection conn, PreparedStatement preparedStatement, ResultSet resultSet) {
		close(conn);
		close(preparedStatement);
		close(resultSet);
	}

	private static void close(ResultSet resultSet) {
		if (resultSet != null) {
			try {
				resultSet.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
	}

	private static void close(PreparedStatement preparedStatement) {
		if (preparedStatement != null) {
			try {
				preparedStatement.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
	}

	private static void close(Connection conn) {
		if (conn != null) {
			try {
				conn.close();
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	private static void close(Connection connection, PreparedStatement prepareStatement) {
		close(connection);
		close(prepareStatement);
	}

	/**
	 * 批量执行updata 语句
	 *
	 * @param list
	 */

	public static void main(String[] args) throws Exception {
		String[] names={"jarvis_device","jarvis_product"};
		BackupDatabase("172.28.2.107", "root", "root", names);
		recoveryDatabase("172.28.2.107", "root", "root");
	}

	/**
	 * 数据库备份
	 *
	 * @param ip           数据库连接地址
	 * @param username     数据库连接名
	 * @param password     数据库连接密码
	 * @param databasename 数据库名称
	 */

	public static void BackupDatabase(String ip, String username, String password, String[] databasenamesArray) {
		try {
			File file = new File("src/test/resources/database");
			if (!file.exists()) {
				file.mkdir();
			}
			//遍历数据库名
			for(int i=0;i<databasenamesArray.length;i++){
				String sqlname =  databasenamesArray[i];
				File datafile = new File(file + File.separator + sqlname + ".sql");

				/*if (datafile.exists()) {
					System.out.println(sqlname + "文件名已存在，请更换");
					return;
				}*/
				//拼接cmd命令
				Process exec = Runtime.getRuntime().exec("cmd /c mysqldump -h" + ip + " -u " + username + " -p" + password + " " + databasenamesArray[i] + " > " + datafile);
				if (exec.waitFor() == 0) {
					System.out.println("数据库备份成功,备份路径为：" + datafile);
					databaseNameList.add(datafile.getPath());
				}

			}


		} catch (Exception e) {
			e.printStackTrace();
		}

	}
	//还原
	//mysql -h端口号 -u用户 -p密码 数据库 < d:/test.sql 恢复到数据库中

	/**
	 * 数据库还原
	 * @param ip  数据库连接地址
	 * @param username 数据库连接名
	 * @param password 数据库密码
	 * @param databasename 数据库名
	 */
	public static void  recoveryDatabase(String ip, String username, String password ) {

		try {
			int size=databaseNameList.size();
			for(int i=0;i<size;i++){

				File datafile = new File(databaseNameList.get(i));
				if (!datafile.exists()) {
					System.out.println(databaseNameList.get(i)+ "文件不已存在，请检查");
					return;
				}
				String filename=datafile.getName();
				int i1 = filename.indexOf(".");
				filename=filename.substring(0, i1);
				//拼接cmd命令
				Process exec = Runtime.getRuntime().exec("cmd /c mysql -h" + ip + " -u " + username + " -p" + password + " " + filename + " < " + datafile);
				if (exec.waitFor() == 0) {
					System.out.println("数据库还原成功，还原的文件为：" + datafile);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

	}
}