package utils;

import cn.afterturn.easypoi.excel.ExcelImportUtil;
import cn.afterturn.easypoi.excel.entity.ImportParams;
import base.Base;
import pojo.Api;
import pojo.Case;
import pojo.WriteBackData;
import com.alibaba.fastjson.JSONObject;
import org.apache.log4j.Logger;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Row.MissingCellPolicy;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;

import java.io.Closeable;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ExcelUtil {
    public static Logger looger = Logger.getLogger(ExcelUtil.class);

    public static void main(String[] args) {
    }

    /**
     * 接口信息表的路径信息
     */
    public static String apiExcelPath = PropertiesUtil.getPath("apiExcelPath");
    //	public static String writeExcelPath=PropertiesUtil.getPath("excelPath");
    // 读取接口信息的数据保存到list中
    public static List<Api> apiList = new ArrayList<Api>();
    // 回写的数据
    public static List<WriteBackData> wbdList = new ArrayList<WriteBackData>();
    //列名列号的映射关系，存放了列的名称和列的列号（第几列）
    public static Map<String, Integer> cellNameCellnumMapping = new HashMap<String, Integer>();
    public static Object[][] sheetNames;//保存一个excel中所有的sheetname

    static {
        //1、首先读取接口信息表，获取接口的数据
        apiList = readExcel(apiExcelPath, "接口信息", Api.class);
//        loadCellNameCellnumMapping(apiExcelPath, "用例模板");
    }

    /**
     * 第一行的所有字段 和列号的关系
     *
     * @param excelPath 路径
     * @param sheetName 表单名称
     */
    public static void loadCellNameCellnumMapping(String excelPath, String sheetName) {
        cellNameCellnumMapping.clear();
        InputStream inputStream = null;
        Workbook workbook = null;
        try {
            inputStream = new FileInputStream(new File(excelPath));
            workbook = WorkbookFactory.create(inputStream);
            Sheet sheet = workbook.getSheet(sheetName);
            // 获取标题行
            Row titleRow = sheet.getRow(0);
            if (titleRow != null && !isEmptyRow(titleRow)) {
                int lastCellnum = titleRow.getLastCellNum();
                // 循环处理标题行的每一列。
                for (int i = 0; i < lastCellnum; i++) {
                    Cell cell = titleRow.getCell(i, MissingCellPolicy.CREATE_NULL_AS_BLANK);
                    cell.setCellType(CellType.STRING);
                    String title = cell.getStringCellValue();
                    title = title.substring(0, title.indexOf("("));
                    int cellnum = cell.getAddress().getColumn();
                    cellNameCellnumMapping.put(title, cellnum);
//					System.out.println("标题:" + title + ",列号:" + cellnum);

                }
//				// 从第二行开始，获取所有的数据行
//				int lastRownum = sheet.getLastRowNum();
//				for (int i = 1; i <= lastRownum; i++) {
//					Row dataRow = sheet.getRow(i);
//					// 获取每行的第一列
//					Cell fistCellOfRow = dataRow.getCell(0, MissingCellPolicy.CREATE_NULL_AS_BLANK);
//					fistCellOfRow.setCellType(CellType.STRING);
//					String caseId = fistCellOfRow.getStringCellValue();
//					int rownum = dataRow.getRowNum();
//					caseIdRownumMapping.put(caseId, rownum);
//
//					System.out.print("----" + "caseId:" + caseId + ",列:" + rownum);
//				}
            }

        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } finally {
            closeInputStream(inputStream);
            closeWorkbook(workbook);
        }

    }

    private static void closeInputStream(InputStream inputStream) {
        if (inputStream == null) {
            try {
                inputStream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 判断是否为空行
     *
     * @param dataRow
     * @return
     */
    private static boolean isEmptyRow(Row dataRow) {
        // TODO Auto-generated method stub
        int lastCellNum = dataRow.getLastCellNum();
        for (int i = 0; i < lastCellNum; i++) {
            Cell cell = dataRow.getCell(i, MissingCellPolicy.CREATE_NULL_AS_BLANK);
            cell.setCellType(CellType.STRING);
            String value = cell.getStringCellValue();
            if (value != null && value.trim().length() > 0) {
                return false;
            }
        }
        return true;
    }

    /**
     * 根据表单名称，获得二维数据，作为测试的数据提供者
     *
     * @param sheetName 表单名
     * @return
     */
    public static Object[][] getDatas(String sheetName) {
        String excelPath = Base.excelPath;
        List<Case> list = readExcel(excelPath, sheetName, Case.class);
        // 定义一个二维数组，把list数据转换成二维数组，给数据提供者使用
        Object[][] obj = new Object[list.size()][2];
        // 数组长度为用例行的长度
        for (int i = 0; i < list.size(); i++) {
            try {
                Case case1 = list.get(i);//第i行的数据
                Class clazz = case1.getClass();
                Method method = clazz.getMethod("getApiId");
                String apiId = (String) method.invoke(case1);//反射获取apiId
                for (Api api : apiList) {//根据apiid获取对应的api对象，
//                    Api api1=new Api();
                    if (apiId.equals(api.getApiId())) {
//                       api1= (Api) api.clone();//第一种方法：对象克隆，不引用地址
//                        JSONObject.parseObject(JSONObject.toJSONString(api));//第二种方法：实现实现克隆接口更快的方法
                        String s = JSONObject.toJSONString(api);
                        Api api1 = JSONObject.parseObject(s, Api.class);
                        obj[i][0] = api1;
                        break;
                    }
                }

                obj[i][1] = list.get(i);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return obj;
    }

    /**
     * easyPoi读取数据保存到list中
     *
     * @param startSheetIndex 读取的sheet
     * @param clazz
     * @return
     */
    public static <T> List<T> easyReadExcel(String excelPath, int startSheetIndex, Class<T> clazz) {
        FileInputStream is = null;
        // 定义一个list集合，泛型为T
        List<T> list = null;
        try {
            // 创建一个输入流
            is = new FileInputStream(new File(excelPath));
            // 创建importparams对象
            ImportParams params = new ImportParams();
            // 设置sheetIndex的值
            params.setStartSheetIndex(startSheetIndex);
            list = ExcelImportUtil.importExcel(is, clazz, params);
            // 返回list集合
            return list;
        } catch (Exception e) {
            e.printStackTrace();
        }
        close(is);
        return null;
    }

    public static void close(Closeable close) {
        // 关闭流
        if (close != null) {
            try {
                close.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 加载用例表的信息
     *
     * @param excelPath 文件路径
     * @param sheetName 表单名称
     * @param clazz     字节码对象
     * @return
     */
    public static <T> List<T> readExcel(String excelPath, String sheetName, Class<T> clazz) {
//		FileInputStream is = null;
        Sheet sheet;
        Workbook workbook = null;
        try {
            // 创建一个文件对象
            workbook = getWorkBook(excelPath);
            // 获取sheet表单
            sheet = workbook.getSheet(sheetName);

            // 获取表头
            Row startRow = sheet.getRow(0);
            // 定义一个数组保存第一行的数据
            int length = startRow.getLastCellNum();
            String startRowArra[] = new String[length];//第一行的字段名，存放到一维数组中
            // 取出第一行所有列的数据
            for (int i = 0; i < startRow.getLastCellNum(); i++) {
                Cell cell = startRow.getCell(i, MissingCellPolicy.CREATE_NULL_AS_BLANK);
                cell.setCellType(CellType.STRING);
                String value = cell.getStringCellValue();
                // 通过截取获得英文名，保存第一行的数据至数组中,
                startRowArra[i] = value.substring(0, value.indexOf("("));
            }
            // 定义二维数组保存
            T obj = null;
            List<T> list = new ArrayList<T>();
            // 从第二行开始，遍历所有的行
            for (int i = 1; i <= sheet.getLastRowNum(); i++) {
                // 创建对象
                obj = clazz.newInstance();
                // 获取行对象
                Row row = sheet.getRow(i);
                // 获取的列数保存在数组中
                // 定义以为数组保存列内容
                // 遍历所有的列
                for (int j = 0; j < row.getLastCellNum(); j++) {
                    // 获取列对象
                    Cell cell = row.getCell(j, MissingCellPolicy.CREATE_NULL_AS_BLANK);
                    // 设置单元格格式
                    cell.setCellType(CellType.STRING);
                    // 获取单元格的内容
                    String value = cell.getStringCellValue();
                    // 通过第一行的数组，加工生成method方法
                    try {
                        String methodName = "set" + startRowArra[j];
                        if ("setCaseId".equalsIgnoreCase(methodName)) {
                            // 通过反射获取方法名
                            Method method = clazz.getDeclaredMethod(methodName, String.class);
                            // 反射调用方法
                            method.invoke(obj, String.valueOf(i));
                        } else {

                            // 通过反射获取方法名
                            Method method = clazz.getDeclaredMethod(methodName, String.class);
                            // 反射调用方法
                            method.invoke(obj, value);
                        }
                    } catch (Exception e) {
                        looger.error(e);
                    }
                    // 通过反射，设值给对象的属性
                }
                // 对象保存到集合中
                list.add(obj);
            }
            // 返回集合
            return list;
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } finally {
            closeWorkbook(workbook);
        }
        return null;
    }

    private static void closeWorkbook(Workbook workbook) {
        if (workbook != null) {
            try {
                workbook.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public static Workbook getWorkBook(String excelPath) {
        FileInputStream is = null;
        Workbook workbook = null;
        ;
        try {
            File file = new File(excelPath);
            // 创建一个输入流
            is = new FileInputStream(file);
            // 创建一个workbook对象
            workbook = WorkbookFactory.create(is);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            close(is);
        }
        return workbook;
    }

    /**
     * 批量回写数据
     *
     * @param excelPath     excel路径
     * @param writeBackList 回写的数据列表
     */
    public static void writeBackData(String excelPath, List<WriteBackData> writeBackList) {
        FileInputStream is = null;
        FileOutputStream os = null;
        try {
            is = new FileInputStream(excelPath);
            // 获取工作簿
            Workbook workbook = WorkbookFactory.create(is);
            // 获取sheet
            for (WriteBackData wbd : writeBackList) {
                Sheet sheet = workbook.getSheet(wbd.getSheetName());
                // 获取row
                Row row = sheet.getRow(wbd.getRowNum());
                Cell cell = row.getCell(wbd.getCellNum(), MissingCellPolicy.CREATE_NULL_AS_BLANK);
                // 获得cell
                // 设置格式
                cell.setCellType(CellType.STRING);
//				HSSFCellStyle cellStyle = (HSSFCellStyle) workbook.createCellStyle();
//				//设置单元格的背景色
//				if("断言成功".equals(wbd.getContent())) {
//					cellStyle.setFillForegroundColor(IndexedColors.GREEN.getIndex());    //填红色
//					cell.setCellStyle(cellStyle);
//				}else if("断言失败".equals(wbd.getContent())) {
//					cellStyle.setFillForegroundColor(IndexedColors.RED.getIndex());    //填红色
//					cell.setCellStyle(cellStyle);
//				}
                // 设置单元格的 内容
                try {
                    //可能会遇到内容过长，无法写入，捕获异常；后边有空看下过长的内容截取获取扩容；
                    cell.setCellValue(wbd.getContent());
                } catch (Exception e) {
                    e.printStackTrace();
                }

            }
            os = new FileOutputStream(excelPath);
            workbook.write(os);
        } catch (Exception e) {
            e.printStackTrace();
            looger.error("回写失败)");
        } finally {
            // 关流
            close(is);
            close(os);
        }

    }

    /**
     * 回写的路径，数据 默认为本类的成员变量excelPath，
     */

    public static void writeBackData(String excelPath) {
        writeBackData(excelPath, wbdList);
    }

    /**
     * 删除excel表里的字段，需要回写的字段需删除
     * ：ActualParams(实际参数)、
     * ResultResponse(实际结果)、
     * ResponseAssertion(是否验证通过)
     * DatabaseAssertion(数据库断言)
     * 、IsPass(最终断言结果)
     *
     * @param sheetName
     */
    public static void delete(String sheetName) {
        try {
            String excelPath = Base.excelPath;
            FileOutputStream os = null;
            Workbook workbook = getWorkBook(excelPath);
            Sheet sheet = workbook.getSheet(sheetName);
            int a[] = new int[6];
            a[0] = cellNameCellnumMapping.get("ActualParams");
            a[1] = cellNameCellnumMapping.get("ResultResponse");
            a[2] = cellNameCellnumMapping.get("ResponseAssertion");
            a[3] = cellNameCellnumMapping.get("DatabaseAssertion");
            a[4] = cellNameCellnumMapping.get("IsPass");
            a[5] = cellNameCellnumMapping.get("Code");
            //获取所有的行号
            int lastRowNum = sheet.getLastRowNum();
            for (int i = 1; i <= lastRowNum; i++) {
                Row row = sheet.getRow(i);
                for (int j = 0; j < 6; j++) {
                    int cellNum = a[j];
                    Cell cell = row.getCell(cellNum, MissingCellPolicy.CREATE_NULL_AS_BLANK);
                    cell.setCellType(CellType.STRING);
                    cell.setCellValue("");
                }
            }
            os = new FileOutputStream(excelPath);
            workbook.write(os);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public static void clearList() {
        wbdList.clear();

    }

    /**
     * 获取excel表中所有的表单名称
     *
     * @param excelPath
     * @return
     */
    public static Object[][] getSheets(String excelPath) {
        Workbook workbook = getWorkBook(excelPath);
        Sheet sheet;
        for (int i = 0; i < workbook.getNumberOfSheets(); i++) {
            sheet = workbook.getSheetAt(i);
            String sheetName1 = sheet.getSheetName();
            sheetNames[i][0] = sheetName1;
        }
        return sheetNames;
    }

}
