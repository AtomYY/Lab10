package temple.edu.lab7;


import android.util.Log;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;


public class HttpDownloader {
    private URL url = null;

    public int downlaodFile(String urlStr, String path, String fileName) {
        InputStream input = null;

        try {
            FileUtil fileUtil = new FileUtil();
            if (fileUtil.isFileExist(path + fileName)) {
                return 1;
            } else {
                input = getInputStearmFormUrl(urlStr);
                File resultFile = fileUtil.write2SDFromInput(path,fileName,input);
                if (resultFile == null)
                    return -1;
            }
        } catch (IOException e) {
            e.printStackTrace();
            return -1;
        }
        finally {
            try {
                input.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return  0;
    }


    public InputStream getInputStearmFormUrl(String urlStr) throws IOException {
        url = new URL(urlStr);
        HttpURLConnection urlConn = (HttpURLConnection) url.openConnection();
        InputStream input = urlConn.getInputStream();
        return input;
    }
}
