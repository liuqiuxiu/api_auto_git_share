/*
 *  Copyright © 2019 Fatri
 */
package utils;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

import org.apache.log4j.Logger;

import com.alibaba.fastjson.JSONObject;

import pojo.DBChecker;
import pojo.DBQueryResult;

public class DBCheckUtil {
	public static Logger logger=Logger.getLogger(DBCheckUtil.class);
	/**
	 * 根据脚本执行查询并返回查询结果
	 * 
	 * @param validateSql
	 * @return
	 */
	public static void main(String[] args) {
		String sql="[{\"no\":\"1\",\"sql\":\"select * from jarvis_product.thing_specification_language where name like'物模型接口测试%'\"}]";
		String doQuery = doQuery(sql);
		System.out.println(doQuery);
	}

	public static String doQuery(String validateSql) {
		logger.info("=============执行数据库验证============");
		if(validateSql != null&&!"".equals(validateSql.trim())) {
		// TODO Auto-generated method stub
		// 将脚本字符串封装成对象
		List<DBChecker> dbCheckers = new JSONObject().parseArray(validateSql, DBChecker.class);
		List<DBQueryResult> dbQueryResultsList = new ArrayList<DBQueryResult>();
		// 执行几次查询，根据list集合的长度决定
		for (DBChecker dbChecker : dbCheckers) {
			String no = dbChecker.getNo();
			String sql = dbChecker.getSql();
			// 执行查询，获取到结果。
			List<LinkedHashMap<String, Object>> allRecordList = JDBCUtil.query(sql);
			for (LinkedHashMap<String, Object> oneRecord : allRecordList) {
				DBQueryResult dbQueryResult = new DBQueryResult();
				dbQueryResult.setNo(no);
				dbQueryResult.setColumnLabelAndValues(oneRecord);
				dbQueryResultsList.add(dbQueryResult);
			}
		}
		return JSONObject.toJSONString(dbQueryResultsList);
		}else {
			return "";
		}
	}

}
