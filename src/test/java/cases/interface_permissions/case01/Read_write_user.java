
package cases.interface_permissions.case01;

import base.Base;
import utils.ExcelUtil;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.DataProvider;


public class Read_write_user extends Base {
	public static String sheetName="读写用户";

	@BeforeClass
	public void DeleteProduct() {
		Base.sheetName=this.sheetName;
		ExcelUtil.loadCellNameCellnumMapping(Base.excelPath, sheetName);
		ExcelUtil.delete(sheetName);
	}

	@DataProvider
	public Object[][] datas() {
		Object[][] obj = ExcelUtil.getDatas(sheetName);
		return obj;
	}
}
