package base;

import pojo.Api;
import pojo.Case;
import pojo.ResponseResult;
import pojo.SQLChecker;
import pojo.WriteBackData;
import utils.AssertUtil;
import utils.AuthorizationUtils;
import utils.ExcelUtil;
import utils.FunctionUtil;
import utils.HttpUtil;
import utils.JDBCUtil;
import utils.ParameterGenerationUtil;
import utils.PropertiesUtil;
import utils.SQLCheckerUtil;
import utils.StringUtil;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.JSONPath;
import com.jayway.jsonpath.JsonPath;
import io.qameta.allure.Step;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.testng.Assert;
import org.testng.annotations.AfterSuite;
import org.testng.annotations.AfterTest;
import org.testng.annotations.BeforeSuite;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

//import bsh.StringUtil;

/**
 * 基类 把所有测试类中用到的方法，都抽取出来放到基类里面，子类继承基类的方法就可以。 简化代码
 *
 * @author 秀秀
 */
public class Base {


	// 自动生成参数，用key-value保存参数名和参数值
	public static String excelPath;
	public static String sheetName;
	public static Logger logger = Logger.getLogger(Base.class);
	//数据库连接地址
	public static String database_ip=PropertiesUtil.jdbcProperties.getProperty("jdbc.ip");
	//数据库连接用户名
	public static String database_name=PropertiesUtil.jdbcProperties.getProperty("jdbc.user");
	//数据库连接密码
	public static String database_pwd=PropertiesUtil.jdbcProperties.getProperty("jdbc.password");
	//数据库名
	public static String[] names={"jarvis_device","jarvis_product","jarvis_application","jarvis_datastore","jarvis_ota","jarvis_rule_engine"
	,"jarvis_tenant","jarvis_user","jarvis_push"};
	/**
	 * 替换符，如果数据中包含“${}”则会被替换成公共参数中存储的数据
	 */
	protected Pattern replaceParamPattern = Pattern.compile("\\$\\{(.*?)\\}");

	/**
	 * 截取自定义方法正则表达式：__xxx(ooo)
	 */
	protected Pattern funPattern = Pattern
			.compile("__(\\w*?)\\((([\\w\\\\\\/:\\.\\$]*,?)*)\\)");// __(\\w*?)\\((((\\w*)|(\\w*,))*)\\)
	// __(\\w*?)\\(((\\w*,?\\w*)*)\\)
	// 模拟环境变量，map随着类的加载而加载。
	public static final Map<String, String> ENV_MAP = new HashMap<String, String>();

	@BeforeSuite
	public void backupDatabase(){

		JDBCUtil.BackupDatabase(database_ip, database_name, database_pwd, names);
	}

	@AfterSuite
	public void  recoveryDatabase(){
		JDBCUtil.recoveryDatabase(database_ip, database_name, database_pwd);
	}

	@BeforeTest
	@Parameters({"excelPath"})
	public void getExcelPath(String excelPath) {
		Base.excelPath = excelPath;
	}

	/**
	 * 测试方法
	 *
	 * @param api   接口数据
	 * @param case1 用例数据
	 */
	@Test(dataProvider = "datas")
	public void exec_test_case(Api api, Case case1) {
		//1、接口请求前替换参数
		replaceAllParams(api, case1);
		// 2、前置sql的查询,并保存结果
		String preValidateResult = SQLCheckerUtil.getSqlCheckerResult(case1.getPreValidateSql());
		// 3、获取响应
		try {//设置等待时间
			Thread.currentThread().sleep(300);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		ResponseResult responseResult = getResponse(api, case1);
		String response  = responseResult.getBody();
		//4、接口请求结果需要保存的数据保存到ENV_MAP中
		saveResult( response,case1.getSave());
		int code = responseResult.getCode();
		// 5、替换后置脚本的参数
		case1.setAfterValidateSql(sqlReplace(case1.getAfterValidateSql()));
		//6、保存后置脚本查询结果中需要保存的数据
		SQLCheckerUtil.saveSqlResult(case1.getAfterValidateSql());
		//7、后置脚本查询，并得到断言结果
		String afterValidateResult = SQLCheckerUtil.getSqlCheckerResult(case1.getAfterValidateSql());
		case1.setExpectedResponseData(replace(case1.getExpectedResponseData()));
		// 8、断言实际响应结果和预期响应结果是否相等pass,fail
		String responseAssert = AssertUtil.codeAndBodyAssertion(case1, responseResult);
		storeWriteBackDatas(api, case1, preValidateResult, response, code, afterValidateResult, responseAssert);
		// 9、响应结果断言
		Assert.assertEquals(responseAssert, "通过", responseAssert + "-----》");
	}

	/**
	 * 回写数据
	 * @param api
	 * @param case1
	 * @param preValidateResult
	 * @param response
	 * @param code
	 * @param afterValidateResult
	 * @param responseAssert
	 */

	private void storeWriteBackDatas(Api api, Case case1, String preValidateResult, String response, int code, String afterValidateResult, String responseAssert) {
		// 状态响应码回写
		storeWriteBackData( case1, ExcelUtil.cellNameCellnumMapping.get("Code"), String.valueOf(code));
		// 6、实际参数回写(请求地址和请求参数)
		storeUrlparamsToWBD(api,case1);
		// 7、实际响应结果回写
		storeWriteBackData( case1, ExcelUtil.cellNameCellnumMapping.get("ResultResponse"), response);
		// 8前置脚本查询结果的回写
		storeWriteBackData( case1, ExcelUtil.cellNameCellnumMapping.get("PreValidateResult"),
				preValidateResult);
		// 9后置脚本查询结果的回写
		storeWriteBackData( case1, ExcelUtil.cellNameCellnumMapping.get("AfterValidateResult"),
				afterValidateResult);
		// 10、实际响应结果回写
		storeWriteBackData( case1, ExcelUtil.cellNameCellnumMapping.get("ResponseAssertion"), responseAssert);
		//用例编号回写
		storeWriteBackData( case1, ExcelUtil.cellNameCellnumMapping.get("CaseId"), case1.getCaseId());
	}

	/**
	 * url和请求参数回写内容存放
	 * @param api
	 * @param case1
	 */
	private void storeUrlparamsToWBD(Api api, Case case1) {
		HashMap<String,String> map=new HashMap<String,String>();
		map.put("url", api.getUrl());
//		map.put("case1",case1.toString());
		if(case1.getParams()!=null&&!"".equals(case1.getParams().trim())){
			map.put("params", case1.getParams());
		}
		if(case1.getPreValidateSql()!=null&&!"".equals(case1.getPreValidateSql().trim())){
			map.put("preSql", case1.getPreValidateSql());
		}
		if(case1.getAfterValidateSql()!=null&&!"".equals(case1.getAfterValidateSql().trim())){
			map.put("afterSql", case1.getAfterValidateSql());
		}
		storeWriteBackData( case1, ExcelUtil.cellNameCellnumMapping.get("ActualParams"), map.toString());

	}


	/**
	 * 提取json串中的值保存至ENV_MAP中
	 *
	 * @param json
	 *            将被提取的json串。
	 * @param allSave
	 *            所有将被保存的数据：xx=$.jsonpath.xx;oo=$.jsonpath.oo，将$.jsonpath.
	 *            xx提取出来的值存放至EVN的key中，将$.jsonpath.oo提取出来的值存放至ENV的value中
	 */
	protected void saveResult(String json, String allSave) {
		if (null == json || "".equals(json) || null == allSave
				|| "".equals(allSave)) {
			return;
		}

//        allSave = getCommonParam(allSave);
		String[] saves = allSave.split(";");
		String key, value;
		for (String save : saves) {
			// key = save.split("=")[0].trim();
			// value = JsonPath.read(json,
			// save.split("=")[1].trim()).toString();
			// ReportUtil.log(String.format("存储公共参数   %s值为：%s.", key, value));
			// saveDatas.put(key, value);

			Pattern pattern = Pattern.compile("([^;=]*)=([^;]*)");
			Matcher m = pattern.matcher(save.trim());
			while (m.find()) {
				key = getBuildValue(json, m.group(1));
				value = getBuildValue(json, m.group(2));
				if(value.contains("$")){
					logger.error("jsonPath解析失败");
				}else{
					logger.info(String.format("存储公共参数   %s值为：%s.", key, value));
					ENV_MAP.put(key, value);
				}
			}
		}
	}
	/**
	 * 获取格式化后的值
	 *
	 * @param sourchJson
	 * @param key
	 * @return
	 */
	private String getBuildValue(String sourchJson, String key) {

			key = key.trim();
			Matcher funMatch = funPattern.matcher(key);
			if (key.startsWith("$.")) {// jsonpath
				try {
					key = JSONPath.read(sourchJson, key).toString();
				}catch (Exception e){
					e.printStackTrace();
				}
			} else if (funMatch.find()) {
				// String args;
				// if (funMatch.group(2).startsWith("$.")) {
				// args = JSONPath.read(sourchJson, funMatch.group(2)).toString();
				// } else {
				// args = funMatch.group(2);
				// }
				String args = funMatch.group(2);
				String[] argArr = args.split(",");
				for (int index = 0; index < argArr.length; index++) {
					String arg = argArr[index];
					if (arg.startsWith("$.")) {
						argArr[index] = JSONPath.read(sourchJson, arg).toString();
					}
				}
				String value = FunctionUtil.getValue(funMatch.group(1), argArr);
				key = StringUtil.replaceFirst(key, funMatch.group(), value);

			}

		return key;
	}
	/**
	 * 取公共参数 并替换参数
	 *
	 * @param param
	 * @return
	 */
	protected String getCommonParam(String param) {
		if (StringUtil.isEmpty(param)) {
			return "";
		}
		Matcher m = replaceParamPattern.matcher(param);// 取公共参数正则
		while (m.find()) {
			String replaceKey = m.group(1);
			String value;
			// 从公共参数池中获取值
			value = ENV_MAP.get(replaceKey);
			param = param.replace(m.group(), value);
		}
		return param;
	}

	/**
	 * 替换所有的变量值
	 *
	 * @param api
	 * @param case1
	 */
	public static void replaceAllParams(Api api, Case case1) {
		logger.info("=========开始执行参数替换=========");
		// 替换前置sql
		case1.setPreValidateSql(sqlReplace(case1.getPreValidateSql()));
		//查询sql并保存变量
		SQLCheckerUtil.saveSqlResult(case1.getPreValidateSql());
		replaceValueByCase(case1);//替换value
		// 替换路径参数
		case1.setParams(replace(case1.getParams()));
		// 替换请求参数
		case1.setPathParams(replace(case1.getPathParams()));
		// 替换后置sql
//		case1.setAfterValidateSql(sqlReplace(case1.getAfterValidateSql()));
		// 替换预期响应结果
		case1.setExpectedResponseData(replace(case1.getExpectedResponseData()));
		//替换query参数的变量
		case1.setQueryParams(replace(case1.getQueryParams()));
		//替换请求地址
		replaceUrl(api, case1);
		logger.info("=========结束执行参数替换=========");
		// 替换变量值
		// 1、替换参数 和sql,并重新给对象赋值
		// 替换uri和路径参数
//		replaceUrl(api, case1);

	}
	@Step("参数替换")
	public  static String replace(String source) {
		//调用ParameterGenerationUtil进行替换参数
		return ParameterGenerationUtil.getCommonStr(source);
	}

	/**
	 * 替换sql参数。除了save成员变量
	 * @param sqlJsonStr
	 * @return
	 */
	private static String sqlReplace(String sqlJsonStr) {
		if(sqlJsonStr==null||sqlJsonStr.equals("")) {
			return "";
		}
		try {
			List<SQLChecker> parseArray = JSONObject.parseArray(sqlJsonStr, SQLChecker.class);
			List<SQLChecker> newList = new ArrayList<SQLChecker>();
			for (SQLChecker sqlChecker : parseArray) {
				if (sqlChecker.getSql() != null) {
					sqlChecker.setSqlByENV();
				}
				if (sqlChecker.getExpectedResult() != null) {

					sqlChecker.setExpectedResultByENV();
				}
				newList.add(sqlChecker);

			}
			 sqlJsonStr  = JSONObject.toJSONString(newList);
			return JSONObject.toJSONString(newList);
		}catch (Exception e ){
			logger.error(e);
			logger.error("sqlJsonStr转换失败");
		}
		return sqlJsonStr;
	}


	/**
	 * 替换含有value变量的值
	 * @param case1
	 */
	public static void replaceValueByCase(Case case1) {
		String replaceValue=case1.getValue();
		case1.setParams(replaceValue(case1.getParams(),replaceValue));//替换参数的value
		case1.setPathParams(replaceValue(case1.getPathParams(),replaceValue));//替换路径参数的value
		case1.setQueryParams(replaceValue(case1.getQueryParams(),replaceValue));//替换query参数的value
		case1.setExpectedResponseData(replaceValue(case1.getExpectedResponseData(),replaceValue));//替换预期结果的value
        case1.setPreValidateSql(replaceValue(case1.getPreValidateSql(),replaceValue));
        case1.setPreValidateResult(replaceValue(case1.getPreValidateResult(),replaceValue));
        case1.setAfterValidateSql(replaceValue(case1.getAfterValidateSql(),replaceValue));
        case1.setAfterValidateResult(replaceValue(case1.getAfterValidateResult(),replaceValue));
	}

	private static String replaceValue(String source, String replaceValue) {
		if(source==null){
			return "";
		}
		while (source!=null&&source.contains("${value}")) {//替换params
			if (replaceValue != null && replaceValue.length() != 0) {//如果value不为空也不为空字符串
				source = source.replace("${value}", replaceValue);
			} else {//如果为空的话，参数值用“”替换
				source = source.replace("${value}", "");
			}
		}
		return source;
	}

	public static String replaceUrl(Api api, Case case1) {
		String uri = api.getUrl();
		String pathParams = case1.getPathParams();
		String queryParams=case1.getQueryParams();
		String replaceUrl = replaceUrl(uri, pathParams,queryParams);
		api.setUrl(replaceUrl);//去掉api中uri的参数，用实际值代替
		return replaceUrl;

	}

	/**
	 * /api/v1/tenants/{tenantId}--替换成/api/v1/tenants/123456789
	 *
	 * @param uri
	 * @param pathParams
	 * @return
	 */

	public static String replaceUrl(String uri, String pathParams,String queryParams) {
		if (!uri.contains("http")) {
			uri = PropertiesUtil.userProperties.getProperty("baseUrl") + uri;
		}
		// 判断地址中是否含有路径参数,該類接口http://172.28.2.107/api/v1/tenants/{tenantId}
		String regex="(\\{.*?\\})";
		Pattern pattern = Pattern.compile(regex);
		Matcher matcher = pattern.matcher(uri);
		try {
			while (matcher.find()) {
				String oldstr = matcher.group(1);
				String paramName = oldstr.substring(oldstr.indexOf("{") + 1, oldstr.indexOf("}"));

				if (StringUtil.isNotEmpty(pathParams) && pathParams.contains(paramName)) {
					try {
						Object paramValue = JsonPath.read(pathParams, "$." + paramName);
						uri = uri.replace(oldstr, java.net.URLEncoder.encode(paramValue.toString(), "utf-8"));//编码
					}catch (Exception e){
						logger.error(e.toString());
					}
				}


			}
		}catch (Exception e){
			logger.error(e.toString());
		}
//		if (uri.contains("{")) {
//			// 提取出参数名
//			String paramName = uri.substring(uri.indexOf("{") + 1, uri.indexOf("}"));
//			Object paramValue = "";
//			try {
//				// 提取出参数值
//				if (pathParams != null && !pathParams.equals("")) {
//					paramValue = JsonPath.read(pathParams, "$." + paramName);
//					uri = uri.replace("{" + paramName + "}", java.net.URLEncoder.encode(paramValue.toString(), "utf-8"));//编码
//				} else {
//					uri = uri.replace("{" + paramName + "}", "");
//				}
//			} catch (Exception e) {
//				e.printStackTrace();
//				logger.error("jsonpath解析失败");
//			}
//
//		}

//		//地址中包含query参数.query参数拼接到url上
		if(!StringUtils.isEmpty(queryParams) ) {
			JSONObject jsonObject = JSONObject.parseObject(queryParams);
			Set<String> strings = jsonObject.keySet();
			for (String key : strings) {
				if (uri.indexOf("?") == -1) {
					uri = uri + "?";
				} else {
					uri = uri + "&";
				}

				String value = jsonObject.getString(key);
				try {
					uri = uri + key + "=" + java.net.URLEncoder.encode(value.toString(), "utf-8");
				} catch (UnsupportedEncodingException e) {
					e.printStackTrace();
				}
			}
		}

		return uri;
	}



	public static String replaceUrl(String uri) {
		if (!uri.contains("http")) {
			uri = PropertiesUtil.userProperties.getProperty("baseUrl") + uri;
		}
		return uri;
	}

	/**
	 * 如果不是登陆和注册接口，则需要接口鉴权
	 *
	 * @param api
	 * @param case1
	 * @return 响应
	 */
	public ResponseResult getResponse(Api api, Case case1) {
		logger.info("=============开始获取接口响应结果=============");
		ResponseResult responseResult;
		if ("注册".equals(api.getApiName()) || "登陆".equals(api.getApiName()) || "登录".equals(api.getApiName())) {
			// 发起http请求，并获取响应
			responseResult = call(api, case1, false);
			;
			// 如果是登陆接口并且响应内容中包含token
			if ("登录".equals(api.getApiName()) && responseResult.getBody().toLowerCase().contains("token".toLowerCase()))
				AuthorizationUtils.storeTokenAndMemberId(responseResult.getBody());
		} else {
			responseResult = call(api, case1, true);
		}
		logger.info("=============结束获取接口响应，结果=【" + responseResult.toString() + "】=============");
		return responseResult;
	}





	/**
	 *      * 把需要回写的数据保存到list中。测试方法执行结束之后统一进行回写
	 * @param sheetName 回写表单名
	 * @param case1  从case1中获取行号
	 * @param cellNum 列号
	 * @param result  回写内容
	 */
	public static void storeWriteBackData(String sheetName, Case case1, int cellNum, String result) {
		// response数据回写，回写到哪里去（excelPath,excelSheet,rowNum），可以把这些数据封装成对象。等用例执行完了，再一次性做write的操作
		// caseid==回写的行号
		// 回写的数据封装成list
		// 获得回写的行号
		int rowNum = Integer.parseInt(case1.getCaseId());
		// 生成一个回写的对象
		logger.info("将需要回写的数据保存到对象中。。。。。");
		WriteBackData wbd = new WriteBackData(sheetName, rowNum, cellNum, result);
		logger.info("回写的行号=" + rowNum + "，列号=" + cellNum + "，结果=" + result + "，表单名=" + sheetName);
		// 回写的对象添加到list集合中
		ExcelUtil.wbdList.add(wbd);
	}

	/**
	 * 回写的数据，默认的表单名为Base.sheetName
	 * @param case1 从case1中获取行号
	 * @param cellNum 回写的列号
	 * @param result  回写的内容
	 */
	public static void storeWriteBackData(Case case1, int cellNum, String result) {
		storeWriteBackData(Base.sheetName,case1,cellNum,result);
	}

	/**
	 * 调用接口请求 ，需要鉴权
	 *
	 * @param api
	 * @param case1
	 * @param b     是否需要鉴权
	 * @return
	 */
	@Step("调用接口请求")
	public static ResponseResult call(Api api, Case case1, boolean b) {
		logger.info("===============接口调用===============");
		// 接口地址
		String uri = api.getUrl();
//        uri = replaceUrl(api, case1);
		// 接口类型
		String type = api.getType();
		// 接口参数
		String params = case1.getParams();
		// 参数请求方式
		String contentType = api.getContentType();

		logger.info("接口地址为:【" + uri + "】接口类型【：" + type + "】接口参数：【" + params + "】接口请求方式：【" + contentType + "】");
		ResponseResult responseResult = HttpUtil.call(uri, type, params, contentType, b);
		logger.info("接口响应结果为：【" + responseResult.toString() + "】");
		return responseResult;
	}

	/**
	 * 测试集执行结束后，批量回写数据
	 */
	@Step("批量数据回写")
	@AfterTest
	public void writeBackData() {
		logger.info("批量回写数据");
		// 调用excelUtil类进行回写
		ExcelUtil.writeBackData(excelPath);

		logger.info("==========套件执行结束==============");

		// 清空回写list中的数据
		ExcelUtil.clearList();

	}

}
