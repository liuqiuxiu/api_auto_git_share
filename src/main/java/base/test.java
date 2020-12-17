package base;

import java.io.*;
import java.util.*;

/**
 * @Description:
 * @Author: Yoyo
 * @CreateDate: 2020/7/13$  10:43$
 */

public class test {
    public final InputStream inputStream = this.getClass().getClassLoader().getResourceAsStream("bayonetConfig.txt");

    public static List<String> testFile(InputStream inputStream){
        BufferedReader br=null;
        List<String> list=new ArrayList<>();
        try {
            br = new BufferedReader(new InputStreamReader(inputStream));
            String str = null;
            while ((str = br.readLine()) != null) {
                list.add(str);
            }
        }catch (IOException e){
            System.out.println("获取失败...");
        }finally {
            try {
                if(br!=null){
                    br.close();
                }
            }catch (IOException e){
                System.out.println("关闭....");
            }
        }
        return list;
    }

    public static void main(String[] args) {
        File file = new File("../api_auto_fatri/src/test/resources/YOYO.zip");
        try {
            InputStream in = new FileInputStream(file);
            System.out.println("文件流="+in);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

}