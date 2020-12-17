package utils;

import constants.Constants;
import functions.Function;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.log4j.Logger;

import java.io.File;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class FunctionUtil {
    public static Logger logger = Logger.getLogger(FunctionUtil.class);

    private static final Map<String, Class<? extends Function>> functionsMap = new HashMap<String, Class<? extends Function>>();
    static {
        //bodyfile 特殊处理
        functionsMap.put("bodyfile", null);
        List<Class<?>> clazzes = ClassFinder.getAllAssignedClass(Function.class);
        clazzes.forEach((clazz) -> {
            try {
                // function
                Function tempFunc = (Function) clazz.newInstance();
                String referenceKey = tempFunc.getReferenceKey();
                if (referenceKey.length() > 0) { // ignore self
                    functionsMap.put(referenceKey, tempFunc.getClass());
                }
            } catch (Exception ex) {
                ex.printStackTrace();
                //TODO
            }
        });
    }

    public static boolean isFunction(String functionName){
        return functionsMap.containsKey(functionName);
    }

    public static String getValue(String functionName,String[] args){
        try {
            return functionsMap.get(functionName).newInstance().execute(args);
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            return "";
        }
    }

    /**
     * 生成指定长度的随机字符串
     * @param s 多少位的字符串${__RandomString(20)}
     * @return
     */
    public static String RandomString(String s) {
        return RandomStringUtils.randomAlphanumeric(Integer.parseInt(s));

    }
    public static String randomString(String s){
        return  RandomString(s);
    }

    /**
     * 生成指定长度的数值
     * @param s 多少位的数字
     * @return
     */
    public static String RandomInt(String s) {
        Random rand = new Random();
        StringBuffer sb = new StringBuffer();
        int n=Integer.parseInt(s);
        for (int i = 1; i <= n; i++) {
            int randNum = rand.nextInt(9) + 1;
            String num = randNum + "";
            sb = sb.append(num);
        }
        String random = String.valueOf(sb);
        return random;

    }
    public static String randomInt(String s){
        return RandomInt(s);
    }

    /**
     *  String str="{\"value\":\"${__Random(a,b,c,d)}\"}";
     * @param args
     * @return 随机返回指定参数中的其中一个值
     */
    public static String Random(String ...args){
        int length=args.length;//可变参数的长度
        if(length==0){
            return "a";
        }
        int index = (int) (Math.random() * length);//生成随机数
        String arg = args[index];//随机生成可变参数中的其中一个值
    System.out.println(arg);
        return arg;
    }

    public static void main(String[] args) throws NoSuchMethodException, ClassNotFoundException, IllegalAccessException, InstantiationException, InvocationTargetException {
        Class<FunctionUtil> c = FunctionUtil.class;
//        Method m = c.getMethod("Random",int[].class);
//        m.invoke(null,new int[]{1,2,3});
        Method  m = c.getMethod("Random",String[].class);
        //m.invoke(null,new String[]{"A","B","C"});//ERROR
//        m.invoke(null,(Object)new String[]{"A","B","C"});//YES,强转为Object类型
        m.invoke(null,new Object[]{new String[]{"A","B","C"}});//推荐写法


    }

    public static String getFile(String str,String size){
       long l= Long.parseLong(size);
        return  FileUtil.create(str, l);
    }
    public static String getModelFile(String source) {
        File file = new File("src/test/resources/tsl01.json");
        String filePath=file.getPath().replace("\\", "/");
        source =source.replace("${import_model_file}", filePath);
        return source;
    }

    /**
     * 获取手机头3位
     *
     * @param type
     * @return
     */
    public static String getHeadMobile(Integer type) {
        switch (type) {
            case 1:
                return "130";
            case 2:
                return "131";
            case 3:
                return "132";
            case 4:
                return "133";
            case 5:
                return "134";
            case 6:
                return "135";
            case 7:
                return "136";
            case 8:
                return "137";
            case 9:
                return "138";
            case 10:
                return "139";
            case 11:
                return "150";
            case 12:
                return "151";
            case 13:
                return "152";
            case 14:
                return "153";
            case 15:
                return "155";
            case 16:
                return "156";
            case 17:
                return "157";
            case 18:
                return "158";
            case 19:
                return "159";
            case 20:
                return "177";
            case 21:
                return "186";
            case 22:
                return "183";
            case 23:
                return "187";
            case 24:
                return "188";
            case 25:
                return "189";
            default:
                return "173";
        }
    }




    /**
     * @return
     * @author Yoyo
     * 随机生成角色name
     */
    public static String getTestUserName() {
        // roleName + 4位随机数
        String testUserName = "testUserName" + (int) ((Math.random() * 9 + 1) * 1000);
        return testUserName;
    }

    /**
     * @return
     * @author Yoyo
     * 随机生成角色name
     */
    public static String getRoleName() {
        // roleName + 4位随机数
        String roleName = "roleName" + (int) ((Math.random() * 9 + 1) * 1000);
        return roleName;
    }

    /**
     * @return
     * @author Yoyo
     * 获取file文件流
     * 获取resource路径下的zip文件
     */
/*    public  static  File getFile(){
        File file = new File("../api_auto_fatri/src/test/resources/YOYO.zip");
        InputStream in=null;
        try {
             in = new FileInputStream(file);
            System.out.println("文件流="+in);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }finally{//关流操作
            if(in!=null){
                try {
                    in.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return file;
    }*/
    public static String getFile() {
        return Constants.FILE_PATH;
    }


    /**
     * 获取尾号4位getMirrorImageVersion
     *
     * @return
     */
    public static String getEndMobile() {
        String ychar = "0,1,2,3,4,5,6,7,8,9";
        int wei = 8;
        String[] ychars = ychar.split(",");
        String endMobile = "";
        Random rdm = new Random();
        for (int i = 0; i < wei; i++) {
            int j = (rdm.nextInt() >>> 1) % 10;
            if (j > 10)
                j = 0;
            endMobile = endMobile + ychars[j];
        }
        return endMobile;

    }

    /**
     * 生成手机号码
     *
     * @return
     */
    public static String getPhoneNumber() {
        Integer headRandom = new Random().nextInt(25);
        String mobile = getHeadMobile(headRandom) + getEndMobile();
        return mobile;
    }

    /**
     * 生成注册名称
     *
     * @return
     */
    public static String getUserName() {
        // liuqiuxiu+4位随机数
        String userName = "liuqiuxiu" + (int) ((Math.random() * 9 + 1) * 1000);
//		System.out.println((int) ((Math.random() * 9 + 1) * 1000));
        return userName;

    }

    /**
     * 生成邮箱
     *
     * @return
     */
    public static String getEmail() {
        String email = (int) ((Math.random() * 9 + 1) * 10000000) + "@126.com";
        return email;

    }

    public static String getDeviceName() {
        long timeNew = System.currentTimeMillis(); // 10位数的时间戳
        String deviceName = "接口自动化测试" + timeNew;
        return deviceName;
    }

    public static String getProductName() {
        long timeNew = System.currentTimeMillis(); // 10位数的时间戳
        String deviceName = "产品名称" + timeNew;
        return deviceName;
    }

    /**
     * 随机生成name
     *
     * @return
     */

    public static String getName() {
        long timeNew = System.currentTimeMillis(); // 10位数的时间戳
        String name = "name" + timeNew;
        return name;
    }

    /**
     * 随机生成设备分组名称
     *
     * @return
     * @Author Yoyo
     */
    public static String getGroupName() {
        long timeNew = System.currentTimeMillis(); // 10位数的时间戳
        String name = "OTAGroupName" + timeNew;
        return name;
    }

    /**
     * 随机生成OTA任务名称
     *
     * @return
     * @Author Yoyo
     */
    public static String getOtaTaskName() {
        long timeNew = System.currentTimeMillis(); // 10位数的时间戳
        String name = "OtaTaskName" + timeNew;
        return name;
    }

    /**
     * 随机生成OTA任务名称
     *
     * @return
     * @Author Yoyo
     */
    public static String getOtaDeviceGroupName() {
        long timeNew = System.currentTimeMillis(); // 10位数的时间戳
        String otaDeviceGroupName = "OtaTaskName" + timeNew;
        return otaDeviceGroupName;
    }

    /**
     * 随机生成镜像名称
     *
     * @return
     * @Author Yoyo
     */
    public static String getMirrorImageName() {
        long timeNew = System.currentTimeMillis(); // 10位数的时间戳
        String imageName = "镜像" + timeNew;
        return imageName;
    }

    public static String getImageName() {
        return getMirrorImageName();
    }


    /**
     * 创建镜像的Type类型随机取
     *
     * @return
     * @Author Yoyo
     */
    public static String getImageType() {
        String[] strs = {"FIRMWARE", "SOFTWARE"};
        int index = (int) (Math.random() * strs.length);
        String ImageType = strs[index];
        return ImageType;
    }

    /**
     * 镜像中的Versiong
     *
     * @return
     * @Author Yoyo
     */
    public static String getMirrorImageVersion() {
        int num1 = (int) (Math.random() * 10);
        int num2 = (int) (Math.random() * 10);
        String mirrorImageVersion = "1." + num1 + "." + num2 + ".20200715";
        return mirrorImageVersion;
    }


    /**
     * 随机生成6位数字且不重复
     *
     * @return
     * @Author Yoyo
     */
    public static String getVerificationCode() {
        int code = (int) (Math.random() * 1000000);
        String verificationCode = String.valueOf(code);
        return verificationCode;
    }


    /**
     * 镜像中的target
     *
     * @return
     * @Author Yoyo
     */
    public static String getMirrorImageTarget() {
        int n1 = (int) (Math.random() * 10);
        int n2 = (int) (Math.random() * 10);
        String mirrorImageTarget = "2." + n1 + "." + n2 + ".20200715";
        return mirrorImageTarget;
    }

    /**
     * 从26个英文字母中随机组合3位
     *
     * @return
     * @Author: Yoyo
     */
    public static String getEnglishName() {
        String s[] = new String[]{"A", "B", "C", "D", "E", "F", "G", "H", "I", "J", "K", "M", "L", "N", "O", "P", "Q", "R", "S", "T", "V", "U", "W", "X", "Y", "Z"};
        Random random = new Random();
        String stringName = s[random.nextInt(s.length)] + s[random.nextInt(s.length)] + s[random.nextInt(s.length)];
        return stringName;
    }

    public static String getStringName() {
        return getEnglishName();
    }

    /**
     * 随机生成数字
     *
     * @return
     * @Author: Yoyo
     */
    public static String getNumberName() {
        long currentTime = System.currentTimeMillis(); // 10位数的时间戳
        String nuberName = String.valueOf(currentTime);
        return nuberName;
    }

    /**
     * 中英结合
     *
     * @return
     * @Author: Yoyo
     */
    public static String getChineseEnglishName() {
        String s[] = new String[]{"A", "B", "C", "D", "E", "F", "G", "H", "I", "J", "K", "M", "L", "N", "O", "P", "Q", "R", "S", "T", "V", "U", "W", "X", "Y", "Z"};
        Random random = new Random();
        String ChineseEnglishName = "中文" + s[random.nextInt(s.length)] + s[random.nextInt(s.length)] + s[random.nextInt(s.length)];
        return ChineseEnglishName;
    }



    /**
     * 生成标识符
     *
     * @return
     */
    public static String getIdentifier() {
        // TODO Auto-generated method stub
        return FunctionUtil.randomString("10");
    }


    /**
     * 生成文件名，指定文件大小
     *
     * @param source
     * @return
     */
    public static String replaceFileParams(String source) {
        //{"file":"${__file("filename",10)}"}
//        第一步：提取 ${__file("filename",10)}
//        第二步：提取filename 和10
//        第三步：调用生成文件函数生成文件
//        第四步：用生成的文件路径替换参数值
        String pattern = "\\$\\{__file\\(\\\".*\\\",[0-9]+\\)\\}";
        Pattern r = Pattern.compile(pattern);
        Matcher m = r.matcher(source);
        while (m.find()) {
            try {
                String old = m.group();
                String regEx = "\\\"\\,([0-9]+)";
                Pattern p = Pattern.compile(regEx);
                Matcher m1 = p.matcher(old);
                Integer lentth = 0;
                while (m1.find()) {
                    String str = m1.group(1);
                    lentth = Integer.valueOf(str);
                }

                regEx = "\"(.*)\"";
                p = Pattern.compile(regEx);
                m1 = p.matcher(old);
                String filename = "";
                while (m1.find()) {
                    filename = m1.group(1);
                }
                if (!"".equals(filename)) {

                    String filePath = FileUtil.create(filename, lentth);
                    source = source.replace(old, filePath);
                }
            } catch (Exception e) {
                logger.error(e);
            }
        }
        return source;
    }


}
