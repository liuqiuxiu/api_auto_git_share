package utils;

import base.Base;
import org.apache.http.HttpRequest;
import org.apache.log4j.Logger;

import com.jayway.jsonpath.JsonPath;

/**
 * 授权类
 * 
 * @author 秀秀
 *
 */
public class AuthorizationUtils {
	public static Logger logger=Logger.getLogger(AuthorizationUtils.class);
	public static void main(String[] args) {
		String json = "{\"code\":0,\"msg\":\"OK\",\"data\":{\"id\":914033,\"leave_amount\":500.0,\"mobile_phone\":\"17620395933\",\"reg_name\":\"liuqiuxiu\",\"reg_time\":\"2020-02-05 15:14:11.0\",\"type\":1,\"token_info\":{\"token_type\":\"Bearer\",\"expires_in\":\"2020-02-16 22:59:58\",\"token\":\"eyJhbGciOiJIUzUxMiJ9.eyJtZW1iZXJfaWQiOjkxNDAzMywiZXhwIjoxNTgxODY1MTk4fQ.c9eImoAfsoVBfAHsKUU05fxJFtrsYyKUM89NMVpYQZajyFws-p2WzVVK93e1d9QKlToPbHw0Om8EKqxGW4BuVg\"}},\"copyright\":\"Copyright 柠檬班 © 2017-2019 湖南省零檬信息技术有限公司 All Rights Reserved\"}";

		Object read = JsonPath.read(json, "$.data.token_info.token");
		Object read2 = JsonPath.read(json, "$.data.id");
		System.out.println(read);
		System.out.println(read2);
	}

	/**
	 * 存储token
	 * 
	 * @param response
	 */
	public static void storeTokenAndMemberId(String response) {
		try {
			Object token = JsonPath.read(response, "$.accessToken");
			if (token != null) {
				Base.ENV_MAP.put("token", token.toString());
			}
		} catch (Exception e) {
			logger.error(e);
		}	
			
			// 如果token不为空，则存储到map中
//		try {
////			Object hierarchyId=JsonPath.read(response, "$.user.hierarchyId");
////			if(hierarchyId!=null) {
////				Base.ENV_MAP.put("${hierarchyId}", hierarchyId.toString());
//			}
//		}catch (Exception e) {
//			logger.error(e);
//		}
//			


	}

	/**
	 * 设置请求头的token
	 * 
	 * @param request
	 */
	public static void setTokenInRequest(HttpRequest request) {
		try {
			// 从环境变量中取到token的值
			String value = Base.ENV_MAP.get("token");
			if (value != null) {
				// 在头里添加token
				request.addHeader("Authorization", value);

			}
		} catch (Exception e) {
			// 捕获异常
			e.printStackTrace();
		}
	}

}
