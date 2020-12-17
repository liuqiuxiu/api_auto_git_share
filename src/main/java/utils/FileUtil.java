package utils;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;

/**
 * @Author:秀秀
 * @Date:2020/8/14
 * @Content:
 */
public class FileUtil {
    /**
     *
     * @param filename  文件名称
     * @param length  文件大小，单位为KB
     */
    public static String create(String filename, long length) {
        String filePath=null;
        RandomAccessFile r = null;
        try {
            File file = new File("src/test/resources/"+filename);
            r = new RandomAccessFile(file, "rw");
            length=length*1024;
            r.setLength(length);
            filePath=file.getPath().replace("\\", "/");
//            System.out.println(filePath);
        } catch (Exception e ){
            e.printStackTrace();
        } finally {
            if (r != null) {
                try {
                    r.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        return filePath;
    }

}
