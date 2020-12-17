package pojo;

/**
 * 用例信息
 * @author 秀秀
 *
 */
public class Case {
//CaseId(用例编号)	ApiId(接口编号)	Desc(用例描述)	Params(参数)	ExpectedResponseData(预期结果)	
	//ResultResponse(实际结果)	ResponseAssertion(是否验证通过)	Sql(数据库查询脚本)	DatabaseAssertion(数据库断言)	IsPass(最终断言结果)

	/**
	 * 用例编号
	 */
	private String caseId;
	private String save;

	public String getSave() {
		return save;
	}

	@java.lang.Override
	public java.lang.String toString() {
		return "Case{" +
				"caseId='" + caseId + '\'' +
				", save='" + save + '\'' +
				", desc='" + desc + '\'' +
				", PathParams='" + PathParams + '\'' +
				", QueryParams='" + QueryParams + '\'' +
				", params='" + params + '\'' +
				", value='" + value + '\'' +
				", actualParams='" + actualParams + '\'' +
				", apiId='" + apiId + '\'' +
				", expectedResponseData='" + expectedResponseData + '\'' +
				", code='" + code + '\'' +
				", resultResponse='" + resultResponse + '\'' +
				", responseAssertion='" + responseAssertion + '\'' +
				", preValidateSql='" + preValidateSql + '\'' +
				", preValidateResult='" + preValidateResult + '\'' +
				", afterValidateSql='" + afterValidateSql + '\'' +
				", afterValidateResult='" + afterValidateResult + '\'' +
				", databaseAssertion='" + databaseAssertion + '\'' +
				", isPass='" + isPass + '\'' +
				'}';
	}

	public void setSave(String save) {
		this.save = save;
	}

	/**
	 * 用例描述
	 */
	private String desc;
	private String PathParams;
	private String QueryParams;

	public String getQueryParams() {
		return QueryParams;
	}

	public void setQueryParams(String queryParams) {
		QueryParams = queryParams;
	}

	public String getPathParams() {
		return PathParams;
	}
	public void setPathParams(String PathParams) {
		this.PathParams = PathParams;
	}
	/**
	 * 用例参数
	 */

	private String params;
	/**
	 * 参数值
	 */
	private String value;
	private String actualParams;
	
	public String getValue() {
		return value;
	}
	public void setValue(String value) {
		this.value = value;
	}

	public String getActualParams() {
		return actualParams;
	}
	public void setActualParams(String actualParams) {
		this.actualParams = actualParams;
	}
	private String apiId;
	/**
	 * 期望响应数据
	 */
	private String  expectedResponseData;
	private String code;
	public String getCode() {
		return code;
	}
	public void setCode(String code) {
		this.code = code;
	}
	/**
	 * 
	 * 实际响应数据
	 */
	private String resultResponse;
	/**
	 * 响应断言
	 */
	private String responseAssertion;
	/**
	 * 接口执行前的脚本验证本
	 */
	private String preValidateSql;
	/**
	 * 接口执行前数据库验证结果
	 */
	private String preValidateResult;
	/**
	 * 接口执行后的脚本验证
	 */
	private String afterValidateSql;
	/**
	 * 接口执行后数据库验证结果
	 */
	private String afterValidateResult;
	public String getPreValidateSql() {
		return preValidateSql;
	}
	public void setPreValidateSql(String preValidateSql) {
		this.preValidateSql = preValidateSql;
	}
	public String getPreValidateResult() {
		return preValidateResult;
	}
	public void setPreValidateResult(String preValidateResult) {
		this.preValidateResult = preValidateResult;
	}
	public String getAfterValidateSql() {
		return afterValidateSql;
	}
	public void setAfterValidateSql(String afterValidateSql) {
		this.afterValidateSql = afterValidateSql;
	}
	public String getAfterValidateResult() {
		return afterValidateResult;
	}
	public void setAfterValidateResult(String afterValidateResult) {
		this.afterValidateResult = afterValidateResult;
	}
	/**
	 * 数据库断言是否通过
	 */
	
	private String databaseAssertion;
	/**
	 * 最终断言结果
	 */
	private String isPass;
	public Case() {
		super();
	}
	public String getCaseId() {
		return caseId;
	}
	public void setCaseId(String caseId) {
		this.caseId = caseId;
	}
	public String getDesc() {
		return desc;
	}
	public void setDesc(String desc) {
		this.desc = desc;
	}
	public String getParams() {
		return params;
	}
	public void setParams(String params) {
		this.params = params;
	}
	public String getApiId() {
		return apiId;
	}
	public void setApiId(String apiId) {
		this.apiId = apiId;
	}
	public String getExpectedResponseData() {
		return expectedResponseData;
	}
	public void setExpectedResponseData(String expectedResponseData) {
		this.expectedResponseData = expectedResponseData;
	}
	public String getResultResponse() {
		return resultResponse;
	}
	public void setResultResponse(String resultResponse) {
		this.resultResponse = resultResponse;
	}
	public String getResponseAssertion() {
		return responseAssertion;
	}
	public void setResponseAssertion(String responseAssertion) {
		this.responseAssertion = responseAssertion;
	}
	public String getDatabaseAssertion() {
		return databaseAssertion;
	}
	public void setDatabaseAssertion(String databaseAssertion) {
		this.databaseAssertion = databaseAssertion;
	}
	public String getIsPass() {
		return isPass;
	}
	public void setIsPass(String isPass) {
		this.isPass = isPass;
	}

}
