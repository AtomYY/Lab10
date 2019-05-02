package temple.edu.lab7;


import android.content.Context;
import android.util.Log;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;


public class HttpDownloader {
    String internalFileName = "/BookDownload/";
    File file;
    private URL url = null;

    public int downlaodFile(Context main, String urlStr, String fileName) {
        InputStream input = null;
        try {
            File f = new File(main.getFilesDir() + "/BookDownload");
            if (!f.exists()) {
                f.mkdirs();
            }
            File file = new File(main.getFilesDir() + internalFileName + fileName);
            if (file.exists()) {
                Log.d("File exists", fileName + String.valueOf(file.exists()));
                return 1;
            } else {

                input = getInputStearmFormUrl(urlStr);
                Log.d("input", input.toString());

                OutputStream output = null;

                try {
                    output = new FileOutputStream(file);
                    byte [] buffer = new byte[4 * 1024];
                    int len;
                    while((len = input.read(buffer)) > 0){
                        output.write(buffer, 0, len);
                        Log.d("output", output.toString());
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
                }            }
        } catch (IOException e) {
            e.printStackTrace();
            return -1;
        }
        finally {
            try {
                if(input != null) {
                    input.close();
                }
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
