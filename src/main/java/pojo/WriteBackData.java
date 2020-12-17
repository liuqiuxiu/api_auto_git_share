package pojo;

public class WriteBackData {
	private String sheetName;
	/**
	 * 回写的行号
	 */
	private int rowNum;

	/**
	 * 回写的列号
	 */
	private int cellNum;
	/**
	 * 回写的内容
	 */
	private String content;
	public int getRowNum() {
		return rowNum;
	}
	public void setRowNum(int rowNum) {
		this.rowNum = rowNum;
	}
	public String getSheetName() {
		return sheetName;
	}
	public void setSheetName(String sheetName) {
		this.sheetName = sheetName;
	}
	public int getCellNum() {
		return cellNum;
	}
	public void setCellNum(int cellNum) {
		this.cellNum = cellNum;
	}
	public String getContent() {
		return content;
	}
	public void setContent(String content) {
		this.content = content;
	}
	/**
	 * 
	 * @param sheetName 表单名称
	 * @param rowNum 行号
	 * @param cellNum 列号
	 * @param content 回写内容
	 */
	public WriteBackData(String sheetName, int rowNum, int cellNum, String content) {
		super();
		this.sheetName = sheetName;
		this.rowNum = rowNum;
		this.cellNum = cellNum;
		this.content = content;
	}
	public WriteBackData( int rowNum, int cellNum, String content) {
		super();
		this.rowNum = rowNum;
		this.cellNum = cellNum;
		this.content = content;
	}
	@Override
	public String toString() {
		return "WriteBackData [sheetName=" + sheetName + ", rowNum=" + rowNum + ", cellNum=" + cellNum + ", content="
				+ content + "]";
	}

	
}
