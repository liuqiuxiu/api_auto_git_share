package utils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import base.Base;
import com.alibaba.fastjson.JSONObject;

import pojo.DBCheckResult;
import pojo.SQLChecker;

public class SQLCheckerUtil {


/*	public static void getSqlCheckerResultAndSava(Case case1){
		getSqlCheckerResult(case1.getPreValidateSql());
		SQLCheckerUtil.savePresqlResult(case1.getPreValidateSql());
	}*/

    /**
     * 获取数据库验证的结果
     *
     * @param sqlJsonStr
     * @return
     */
    public static String getSqlCheckerResult(String sqlJsonStr) {
        //如果sql为空，则返回空字符串
        String resultStr = "";
        if (sqlJsonStr == null || sqlJsonStr.equals("")) {
            return "";
        }
        try {
            //验证sql
            //将字符串转换成list
            List<SQLChecker> parseArray = JSONObject.parseArray(sqlJsonStr, SQLChecker.class);
            List<DBCheckResult> resultList = new ArrayList<DBCheckResult>();
            //对每一条数据验证进行验证
            for (SQLChecker sqlChecker : parseArray) {
                DBCheckResult dbCheckResult;
                String no = sqlChecker.getNo();
                String sql = sqlChecker.getSql();
                String save = sqlChecker.getSave();
                List<LinkedHashMap<String, Object>> expectedResult = sqlChecker.getExpectedResult();
//			System.out.println(expectedResult);
                //查询数据库，获取查询结果
                List<LinkedHashMap<String, Object>> actualResultList = JDBCUtil.query(sql);
                if (expectedResult != null) {//预期结果不为空，则进行断言
                    if (expectedResult.equals(actualResultList)) {
                        dbCheckResult = new DBCheckResult(no, actualResultList, "通过");
                    } else {
                        dbCheckResult = new DBCheckResult(no, actualResultList, "失败");
                    }
                    resultList.add(dbCheckResult);
                    resultStr = JSONObject.toJSONString(resultList);
                }


            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return resultStr;
    }

    /**
     * 保存sql查询结果
     *
     * @param sqlJsonStr sqlstr
     */
    public static void saveSqlResult(String sqlJsonStr) {
        //如果sql为空，则返回空字符串
        if (sqlJsonStr == null || sqlJsonStr.equals("")) {
            return;
        }
        try {
          /*  //验证sql
            //将字符串转换成list
            List<SQLChecker> parseArray = JSONObject.parseArray(sqlJsonStr, SQLChecker.class);
            //对每一条数据验证进行验证
			Map<String,List<String>> saveMap=new HashMap<String,List<String>>();
            for (SQLChecker sqlChecker : parseArray) {
                String sql=sqlChecker.getSql();
                 String save=sqlChecker.getSave();
                //查询数据库，获取查询结果
                if(save!=null&&save.toLowerCase().contains("arra")){//数组,查询结果添加到list中
					List<String> strList = JDBCUtil.queryToArray(sql);
					if(saveMap.isEmpty()){//如果savemap为空，则直接put
						saveMap.put(save,strList);
					}else{
						for(Map.Entry<String, List<String>> entry:saveMap.entrySet()){
							List<String> value = entry.getValue();
							value.addAll(strList);
							saveMap.put(entry.getKey(),value);
						}
					}
				}else{
					Object queryone = JDBCUtil.queryone(sql);//查询结果只有一行，直接保存key--value
					Base.ENV_MAP.put(save, queryone.toString());
				}
			}
			for(Map.Entry<String, List<String>> entry:saveMap.entrySet()){
				Base.ENV_MAP.put(entry.getKey(),entry.getValue().toString());
			}*/

            //将字符串转换成list
            List<SQLChecker> parseArray = JSONObject.parseArray(sqlJsonStr, SQLChecker.class);
            List<String> saveList = new ArrayList<String>();
            //对每一条数据验证进行验证
            Map<String, List<Object>> saveMap = new HashMap<String, List<Object>>();
            for (SQLChecker sqlChecker : parseArray) {
                String sql = sqlChecker.getSql();
                String save = sqlChecker.getSave();
                if (save != null && sql != null) {
                    Base.replace(sql);
                    String[] saves = save.split(";");
                    saveList.addAll(Arrays.asList(saves));//sava分隔之后保存到list中
                    List<LinkedHashMap<String, Object>> query = JDBCUtil.query(sql);//获取查询结果集
                    if(query.isEmpty()){
                        return;
                    }
                    for (LinkedHashMap<String, Object> map : query) {//遍历查询结果集
                        for (Map.Entry<String, Object> entry : map.entrySet()) {//遍历查询结果的map
                            List<Object> newList = new ArrayList<Object>();//查询结果的值保存到list中
                            String key = entry.getKey();//key
                            Object value = entry.getValue();//value
                            newList.add(value);
                            if(!saveMap.containsKey(key)){
                                saveMap.put(key,newList);
                            }else{
                                List<Object> oldList = saveMap.get(key);
                                oldList.addAll(newList);
                                saveMap.put(key,oldList);
                            }
                        }
                    }
                }

            }
            //saveList去重
            saveList = saveList.stream().distinct().collect(Collectors.toList());

            //将newMap存放到hashMap里
            for (String key : saveList) {
                for (Map.Entry<String, List<Object>> entry : saveMap.entrySet()) {
                    String mapKey="${"+entry.getKey()+"}";
                    if (!"".equals(key) && mapKey.toLowerCase().equals(key.toLowerCase())) {
                        List<Object> valueList = entry.getValue();
                        if(key.toLowerCase().contains("arra")){//如果key包含arra，value保存为数组
                            Base.ENV_MAP.put(key, valueList.toString());
                        }else{//不是数组的话，保存第一个值
                            Base.ENV_MAP.put(key, valueList.get(0).toString());
                        }
                    }
                }
            }


        } catch (Exception e) {
            e.printStackTrace();
        }

    }


    public static void main(String[] args) {
        List<String> list1 = new ArrayList<String>();
        List<String> list2 = new ArrayList<String>();
        List<String> list3 = new ArrayList<String>();
        list1.add("a");
        list1.add("b");
        list2.add("b");
        list2.add("c");
        list3.addAll(list1);
        list3.addAll(list2);
        System.out.println(list3.toString());

    }
}