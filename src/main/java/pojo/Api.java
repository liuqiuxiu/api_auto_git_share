package pojo;

import javax.validation.constraints.NotNull;

import cn.afterturn.easypoi.excel.annotation.Excel;

/**
 * 存放接口信息对象
 * @author 秀秀
 *
 */
public class Api implements Cloneable {
	//接口编号	接口名称	接口提交方式	接口地址	参数类型
	@Excel(name="接口编号")
	private String apiId;
	
	@Excel(name="接口名称")
	private String apiName;
	
	@Excel(name="接口提交方式")
	private String type;
	
	@Excel(name="接口地址")
	@NotNull
	private String url;
	
	@Override
	public String toString() {
		return "Api [apiId=" + apiId + ", apiName=" + apiName + ", type=" + type + ", url=" + url + ", contentType="
				+ contentType + "]";
	}

	public String getApiId() {
		return apiId;
	}

	public void setApiId(String apiId) {
		this.apiId = apiId;
	}

	public String getApiName() {
		return apiName;
	}

	public void setApiName(String apiName) {
		this.apiName = apiName;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public String getContentType() {
		return contentType;
	}

	public void setContentType(String contentType) {
		this.contentType = contentType;
	}

	@Excel(name="参数类型")
	private String contentType;


	// 重写克隆方法子列才可以调用
	@Override
	public Object clone() throws CloneNotSupportedException {
		return super.clone();
	}



}
