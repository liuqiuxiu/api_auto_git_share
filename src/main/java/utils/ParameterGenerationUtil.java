package utils;

import base.Base;
import com.jayway.jsonpath.JsonPath;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ParameterGenerationUtil {
    public static Logger logger = Logger.getLogger(ParameterGenerationUtil.class);


    /**
     * 参数替换
     *
     * @param source
     * @return
     */

    public static String getCommonStr(String source) {
        if (StringUtils.isEmpty(source)) {
            return "";

        }
        //替换已经存放到ENV_MAP的普通变量 如${name} 等，
        String regex = "(\\$\\{(\\w*?)\\})";
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(source);
        while (matcher.find()) {
            String oldstr = matcher.group(0);///
            String newstr = Base.ENV_MAP.get(oldstr);
            String functionName=matcher.group(2);
//            System.out.println(functionName);
            if (StringUtils.isNotEmpty(newstr)) {//如果ENV_MAP有这个参数值，从ENV_MAP中获取
                source = source.replace(oldstr, newstr);
                logger.info("参数替换，olestr为：【" + oldstr + "】,newStr为：【" + newstr + "】");
            } else {
                //ENV_MAP有这个参数值,调用函数读取
//                case "${username}": return ParameterGenerationUtil.getUserName();
                try {
                     functionName = "get"+functionName.substring(0,1).toUpperCase()+functionName.substring(1);
                    Class clazz = FunctionUtil.class;
                    Method method = clazz.getMethod(functionName);
                    String result = (String) method.invoke(null);
                    source = source.replace(oldstr, result);
                } catch (Exception e) {
//                    e.printStackTrace();//不要打印异常了，这个异常太多了。
                }

            }
        }

        // 如果包含函数${__RandomString(33)}等
        source = getFunctionOptStr(source);

        return source;
    }

    public static void main(String[] args) {
        String str="{\"value\":\"${__Random(a,b,c,d)}\"}";
        String functionOptStr = getFunctionOptStr(str);
        System.out.println(functionOptStr);
    }
    /**
     * 从函数中获取参数值
     *
     * @param str
     * @return
     */
    public static String getFunctionOptStr(String str)  {


//        String regex="\\$\\{__(\\w*?)\\(((\\w*,?)*)\\)\\}";
        String regex = "\\$\\{__(\\w*?)\\(((.*?,?)*)\\)\\}";
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(str);
        Class clazz = FunctionUtil.class;
        while (matcher.find()) {
            String totalStr = matcher.group(0);//需要替换的参数名
            String functionName = matcher.group(1);//函数名称
            String params = matcher.group(2);//函数参数
            String[] paramsArray = params.split(",");//参数数组
            int paramsLength = paramsArray.length;//参数个数
//            System.out.println(paramsLength);
            String result = null;


            if (StringUtils.isEmpty(params)) {//如果参数个数为空，反射调用方法时，无需传入参数
                try {
                    Method method = clazz.getMethod(functionName);
                    result = (String) method.invoke(null);
                    str = str.replace(totalStr, result);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } else {//如果参数数量不为空，需要传入参数
                try {
//                    String str="{\"value\":\"${__Random(a,b,c,d)}\"}";
                    if("Random".equals(functionName)){//functionUtil中的Random方法是可变参数，所以需要特殊处理
                        try {
                            //得到方法对象
                            Method method= clazz.getMethod(functionName, String[].class);
//                            //在反射中，执行具有可变数量的参数的方法时，需要将入口参数定义成二维数组
                            result =(String) method.invoke(null,new Object[]{new String[]{Arrays.toString(paramsArray)}});
                            str = str.replace(totalStr, result);//参数值替换掉参数名
                        } catch (IllegalAccessException e) {
                            e.printStackTrace();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }else{//方法中存在多个参数
                    Class[] typesArray = new Class[paramsLength];
                    for (int i = 0; i < paramsLength; i++) {
                        typesArray[i] = String.class;
                    }
                    Method method = clazz.getMethod(functionName, typesArray);//通过反射获取方法名
                    result = (String) method.invoke(params, paramsArray);//获得方法值
                    str = str.replace(totalStr, result);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            //参数值把参数名替换掉
        }

        // 如果包含系统变量${__RandomString(33)}
//        str=replaceRandom(str);
        //如果包含文件变量 ${__file("filename",5)}
//        str=replaceFileParams(str);
//        ${import_model_file}，上传文件模型的文件路径
        if (str.contains("${import_model_file}")) {
            str = FunctionUtil.getModelFile(str);
        }
        return str;
    }



    /*	// length用户要求产生字符串的长度
        public static String __RandomString(int length) {
            String str = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
            Random random = new Random();
            StringBuffer sb = new StringBuffer();
            for (int i = 0; i < length; i++) {
                int number = random.nextInt(62);
                sb.append(str.charAt(number));
            }
            return sb.toString();
        }*/
    /**
     * 用例变量替换
     *不需要这么写了，太长
     * @param params
     * @return
     */
/*    public static String generation(String params) throws IOException {
        switch (params) {
            case "${username}":
                return ParameterGenerationUtil.getUserName();
            case "${email}":
                return ParameterGenerationUtil.getEmail();
            case "${phoneNumber}":
                return ParameterGenerationUtil.getPhoneNumber();
            case "${deviceName}":
                return ParameterGenerationUtil.getDeviceName();
            case "${productName}":
                return ParameterGenerationUtil.getProductName();
            case "${name}":
                return ParameterGenerationUtil.getName();
            case "${stringName}":
                return ParameterGenerationUtil.getStringName();
            case "${nuberName}":
                return ParameterGenerationUtil.getNumberName();
            case "${groupName}":
                return ParameterGenerationUtil.getGroupName();
            case "${imageName}":
                return ParameterGenerationUtil.getImageName();
            case "${OtaTaskName}":
                return ParameterGenerationUtil.getOtaTaskName();
            case "${otaDeviceGroupName}":
                return ParameterGenerationUtil.getOtaDeviceGroupName();
            case "${ChineseEnglishName}":
                return ParameterGenerationUtil.getChineseEnglishName();
            case "${mirrorImageVersion}":
                return ParameterGenerationUtil.getMirrorImageVersion();
            case "${mirrorImageTarget}":
                return ParameterGenerationUtil.getMirrorImageTarget();
            case "${ImageType}":
                return ParameterGenerationUtil.getImageType();
            case "${file}":
//                return Constants.FILE_PATH;
                return ParameterGenerationUtil.getFile();
            case "${verificationCode}":
                return ParameterGenerationUtil.getVerificationCode();
            case "${roleName}":
                return ParameterGenerationUtil.getRoleName();
            case "${testUserName}":
                return ParameterGenerationUtil.getTestUserName();
        }
        return null;
        // 自动生成注册用户otaDeviceGroupName
    }*/

    /**
     * 从相应body中提取某个字段和值，保存到环境变量中
     *
     * @param body    返回数据
     * @param key     要提取的
     * @param express 表达式
     * @return
     */
    public static String storeKeyAndValueToEVN(String body, String key, String express) {
        String result = "";
        try {
            Object read = JsonPath.read(body, express);
            result = read.toString();
        } catch (Exception e) {
            e.printStackTrace();
        }
        //保存到环境变量中
        Base.ENV_MAP.put(key, result);
        return result;
    }
}
