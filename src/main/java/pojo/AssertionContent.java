package pojo;

/**
 * 断言内容实体类
 * @author 秀秀
 *
 */
public class AssertionContent {
	/**
	 * 断言部分
	 */
	private int expectedCode;
	/**
	/**
	 * 断言表达式
	 */
	private String expectedBody;
	public int getExpectedCode() {
		return expectedCode;
	}
	public void setExpectedCode(int expectedCode) {
		this.expectedCode = expectedCode;
	}
	public String getExpectedBody() {
		return expectedBody;
	}
	public void setExpectedBody(String expectedBody) {
		this.expectedBody = expectedBody;
	}
	@Override
	public String toString() {
		return "AssertionContent [expectedCode=" + expectedCode + ", expectedBody=" + expectedBody + "]";
	}



	
	
}
