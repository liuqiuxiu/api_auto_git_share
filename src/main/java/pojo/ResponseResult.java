package pojo;
/**
 * 接口返回结果
 * @author FU0656
 *
 */
public class ResponseResult {
	/**
	 * 返回状态码
	 */
	private int code;
	/**
	 * 返回请求体
	 */
	private String body;
	public int getCode() {
		return code;
	}
	public void setCode(int code) {
		this.code = code;
	}
	public String getBody() {
        return body;
    }
    public void setBody(String body) {
        this.body = body;
    }
	@Override
	public String toString() {
		return "ResponseResult [code=" + code + ", body=" + body + "]";
	}
	
}
