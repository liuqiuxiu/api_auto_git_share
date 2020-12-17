package utils;

import java.util.List;

import org.apache.log4j.Logger;

import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.JSONPath;
import com.jayway.jsonpath.Configuration;
import com.jayway.jsonpath.JsonPath;

import pojo.AssertionContent;
import pojo.Case;
import pojo.JsonPathValidata;
import pojo.ResponseResult;

public class AssertUtil {
	public static Logger logger = Logger.getLogger(AssertUtil.class);
//	public static void main(String[] logger) {
//		String excpectedResult = "[{\"expression\":\"$.code\",\"value\":\"0\"},{\"expression\":\"$.data.id\",\"value\":\"914033\"}]";
//		String response = "{\"code\":0,\"msg\":\"OK\",\"data\":{\"id\":914033,\"leave_amount\":7000.0,\"mobile_phone\":\"17620395933\",\"reg_name\":\"liuqiuxiu\",\"reg_time\":\"2020-02-05 15:14:11.0\",\"type\":1,\"token_info\":{\"token_type\":\"Bearer\",\"expires_in\":\"2020-02-18 14:18:17\",\"token\":\"eyJhbGciOiJIUzUxMiJ9.eyJtZW1iZXJfaWQiOjkxNDAzMywiZXhwIjoxNTgyMDA2Njk3fQ.5ZozGDUQtavC4aa4C4lHTVKkAbQdyMCPSFHnncp7GiRhIHel37sttMk5mcwXg27Txb-X7uHyuA7o1XKeA5-wqw\"}},\"copyright\":\"Copyright 柠檬班 © 2017-2019 湖南省零檬信息技术有限公司 All Rights Reserved\"}";
//		verification(excpectedResult, response);
//	}

	public static void verification(String excpectedResult, String response) {
		// 将预期结果封装成Jsonpathvalidate对象
		List<JsonPathValidata> list = JSONObject.parseArray(excpectedResult, JsonPathValidata.class);
		// 遍历结合
		for (JsonPathValidata jsonPathValidata : list) {
			String path = jsonPathValidata.getExpression();
			String value = jsonPathValidata.getValue();
			// 用jsonpath提取出想要的内容，并判断
			// JSONPath.read(response, path) == null 如果为null的话，是不能转成字符串的，所以先判断是否为null
			String obj = JSONPath.read(response, path) == null ? "" : JSONPath.read(response, path).toString();
			System.out.println("预期结果=【" + value + "】，实际结果=【" + obj + "】，单次断言结果：" + value.equals(obj));
		}

	}

	public static void main(String[] args) {
		String string = "{\"code\": 200,\"body\": [{\"expression\": \"$.name\",\"value\": \"name1591683259678\",\"assertType\": \"等于\"}]}";
	}

	public static String codeAndBodyAssertion(Case case1, ResponseResult responseResult) {
		String content = case1.getExpectedResponseData();

		if (content != null && content.length() != 0 && !"".equals(content)) {
			try {
			AssertionContent assertionContent = JSONObject.parseObject(content, AssertionContent.class);
			return codeAndBodyAssertion(assertionContent, responseResult);
			}catch (Exception e) {
				return "预期结果解析异常";
			}
		} else {
			return "通过";
		}

	}

	/**
	 * code和body的断言
	 * 
	 * @param assertionContent 断言内容
	 * @param responseResult
	 * @return
	 */
	public static String codeAndBodyAssertion(AssertionContent assertionContent, ResponseResult responseResult) {
		String result = "通过";
		try {
			int code = assertionContent.getExpectedCode();
			String expectedbody = assertionContent.getExpectedBody();
			List<JsonPathValidata> validataList = JSONObject.parseArray(expectedbody, JsonPathValidata.class);
			if (code != 0) {
				result = codeAssertion(responseResult.getCode(), assertionContent.getExpectedCode());
				if ("通过".equals(result)&&expectedbody!=null) {
					result = bodyAssertion(responseResult.getBody(), validataList);
				}
			} else if(expectedbody!=null){
				result = bodyAssertion(responseResult.getBody(), validataList);
			}
		} catch (Exception e) {
			logger.error(e);
			return "json查找异常";
		}
		return result;

	}

	/**
	 * 判断状态响应码
	 * 
	 * @param actualResult
	 * @param expectedCode
	 * @return
	 */
	public static String codeAssertion(int actualResult, int expectedCode) {
		String result = "通过";
		if (actualResult == expectedCode) {
			return result;
		} else {
			return "实际结果【" + actualResult + "】不等于预期结果【" + expectedCode + "】";
		}
	}

	/**
	 * 响应报文的断言
	 * 
	 * @param case1    用例对象
	 * @param response 实际响应结果
	 */
	public static String bodyAssertion(String actualResult, List<JsonPathValidata> validataList) {
		String result = "通过";
		Object document = Configuration.defaultConfiguration().jsonProvider().parse(actualResult);
		String actualValue = "";
		for (JsonPathValidata jsonPathValidata : validataList) {
			// 表达式
			String path = jsonPathValidata.getExpression();
			// 表达式的值
			String expectedValue = jsonPathValidata.getValue();
			String assertionType = jsonPathValidata.getAssertType();
			// 先把response加载出来，提高性能
			Object obj = JsonPath.read(document, path);
			// 用jsonpath提取出想要的内容，并判断
			// JSONPath.read(response, path) == null 如果为null的话，是不能转成字符串的，所以先判断是否为null
			actualValue = obj == null ? "" : obj.toString();
			if ("等于".equals(assertionType)) {
				if (!actualValue.equals(expectedValue)) {
					result = "实际结果【" + actualValue + "】不等于预期结果【" + expectedValue + "】";
				}
			} else if ("包含".equals(assertionType)) {
				if (!actualResult.contains(expectedValue)) {
					result = "实际结果【" + actualResult + "】不包含【" + expectedValue + "】";
				}
			}
		}
		return result;

	}

	public static String afterSqlValit(String actualResult, String expectedResult) {
		// todo
		return "还没有完成";
	}

}
