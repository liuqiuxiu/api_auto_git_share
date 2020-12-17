package pojo;
/**
 * 预期结果jsonpath判断内容实体类
 * @author 秀秀
 *
 */
public class JsonPathValidata {
	/**
	 * 表达式
	 */
	private String expression;
	/**
	 * 值
	 */
	private String value;
	/**
	 * 断言方式
	 */
	private String assertType;
	
	public String getAssertType() {
		return assertType;
	}
	public void setAssertType(String assertType) {
		this.assertType = assertType;
	}
	public String getExpression() {
		return expression;
	}
	public void setExpression(String expression) {
		this.expression = expression;
	}
	public String getValue() {
		return value;
	}
	public void setValue(String value) {
		this.value = value;
	}
	public JsonPathValidata() {
		super();
	}
	public JsonPathValidata(String expression, String value) {
		super();
		this.expression = expression;
		this.value = value;
	}
	@Override
	public String toString() {
		return "JsonPathValidata [expression=" + expression + ", value=" + value + ", assertType=" + assertType + "]";
	}
	
}
