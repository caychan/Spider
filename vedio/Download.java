package tools;

import org.apache.http.HttpEntity;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.protocol.HttpClientContext;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.protocol.HttpContext;
import us.codecraft.webmagic.Request;

import javax.swing.*;
import java.io.*;

/**
 * Created by yongqiang on 2015/3/6.
 */
public class Download extends Thread {
    private final CloseableHttpClient httpClient;
    private final HttpContext context;
    private final HttpGet httpget;
    private String downloadPath = null;
    private File filePath = null;
    private String videoName = null;
    private String fileType=".";

    public Download(Request request, String directory, String videoName, boolean subsection,String platform) {
        this.httpClient = HttpClients.createDefault();
        this.context = HttpClientContext.create();
        this.httpget = new HttpGet(request.getUrl().toString());
        setHeader(request,platform);
        this.videoName = videoName + getSubsection(request, subsection);
        checkSubsection(directory + "/");
        downloadPath = directory + "/" + this.videoName.replaceAll(":", "") + ".mp4";
    }

    private void loading(InputStream in, OutputStream out, double fileSize) {
        byte[] buffer = new byte[4096];
        int readLength = 0;
        double downloadSize = 0;
        try {
            while ((readLength = in.read(buffer)) > 0) {
                downloadSize += readLength;
                out.write(buffer, 0, readLength);
                out.flush();
                System.out.print('\r');
                System.out.print(videoName + "\t" + String.format("%.2f", downloadSize / fileSize * 100) + "%");
            }
            in.close();
            out.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }


    private String getSubsection(Request request, boolean subsection) {
        String result = " ";
        if (subsection){
            result=  request.getExtra("subsection").toString();
        }
        return result ;
    }
    private void setHeader(Request request,String paltform){
        if(paltform.equals("hunantv")){
        this.httpget.addHeader("Referer",request.getUrl().toString());}
    }
    private double getFileSize(CloseableHttpResponse response) {
        double fileSize = Long.valueOf(response.getHeaders("Content-Length")[0].getValue());
        return fileSize;
    }

    private void checkSubsection(String directory) {
        filePath = new File(directory);
        if (filePath.exists() == false) {
            filePath.mkdirs();
        }
    }

    private boolean checkFilesExists(String path, double fileSize) {
        boolean result = true;
        try {
            if (new File(path).exists()) {
                InputStream in = new FileInputStream(new File(path));
                if (in.available() == fileSize) {
                    in.close();
                    result = false;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return result;
    }

    public void run() {
        try {
            CloseableHttpResponse response = httpClient.execute(httpget, context);
            try {
                System.out.println(response.getLastHeader("Content-Disposition"));
                if (response.getStatusLine().getStatusCode() == 200) {
                    HttpEntity entity1 = response.getEntity();
                    if (entity1 != null) {
                        if (checkFilesExists(downloadPath, getFileSize(response))) {
                            if (entity1.getContentLength() != -1) {
                                loading(entity1.getContent(), new FileOutputStream(new File(downloadPath)), getFileSize(response));
                            } else {
                                System.out.println("get connect length is -1");
                            }
                        } else {
                            System.out.println("jump is" + videoName);
                        }

                    } else {
                        System.out.println("not response deta");
                    }

                } else {
                    System.out.println("page status code  not 200");
                }
            } finally {
                response.close();
            }
        } catch (ClientProtocolException ex) {
            // Handle protocol errors
            System.out.println(ex.toString());
        } catch (IOException ex) {
            // Handle I/O errors
            System.out.println(ex.toString());
        }
    }
}
