package temple.edu.lab7;


import android.os.Environment;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;


public class FileUtil {
    private final String SDPATH = Environment.getExternalStorageDirectory() + "/BookDownload/";

    public FileUtil() {

    }

    public String getSDPATH() {
        return SDPATH;
    }

    public FileUtil(String SDPATH){
        SDPATH = Environment.getExternalStorageDirectory() + "/BookDownload/" ;
    }

    public File createSDFile(String fileName) throws IOException {
        String folder_main = "/BookDownload";
        File f = new File(Environment.getExternalStorageDirectory(), folder_main);
        if (!f.exists()) {
            f.mkdirs();
        }

        File file = new File(SDPATH + fileName);
        file.createNewFile();
        return file;
    }

    public File createDir(String dirName){
        File dir = new File(SDPATH + dirName);
        dir.mkdir();
        return dir;
    }

    public boolean isFileExist(String fileName){
        File file = new File(SDPATH + fileName);
        return file.exists();
    }

    public File write2SDFromInput(String fileName, InputStream input){
        File file = null;
        OutputStream output = null;

        try {

            file =createSDFile(fileName);
            output = new FileOutputStream(file);
            byte [] buffer = new byte[4 * 1024];
            while(input.read(buffer) != -1){
                output.write(buffer);
                output.flush();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        finally {
            try {
                output.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return file;
    }

}
