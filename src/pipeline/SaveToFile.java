package pipeline;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.apache.http.HttpEntity;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import utils.DBUtils;
import utils.FilePersistentBase;
import utils.NumberUtils;


public class SaveToFile extends FilePersistentBase {
	
    private Logger logger = LoggerFactory.getLogger(getClass());
	private static final int LENGTH = 128;
	DBUtils dbUtils = new DBUtils();
    
    public SaveToFile() {
        setPath("F:\\Clawer");
    }

    public SaveToFile(String path) {
        setPath(path);
    }
    
    public String getSignature(HttpEntity entity){
        byte[] sign = new byte[LENGTH];
        boolean first = true;
		BufferedInputStream bis = null;
        String signature = "";
        try {
            bis = new BufferedInputStream(entity.getContent());
            byte[] b = new byte[LENGTH];

            int len, readBytes;
            boolean flag = true;
            while(flag){
            	readBytes = 0;
	            while (readBytes < LENGTH) {  //保证每次都能将b读满
	            	len = bis.read(b, readBytes, LENGTH - readBytes);
	            	if (len == -1) {   //判断是不是读到了数据流的末尾 ，防止出现死循环。
	            		if (readBytes < LENGTH) {
							for (int i = readBytes; i < LENGTH; i++) {
								b[i] = 0;
							}
						}
	            		flag = false;
	            		break;
	            	}
	            	readBytes += len;
	            }
	            if (first == true) {// 第一次signat位空，不做xor处理，只是让signature中存储128位。
	            	sign = b;
					first = false;
				}else {
					sign = NumberUtils.xor(LENGTH, sign, b);
				}
            }

            int[] sig = new int[LENGTH];

            for (int i = 0; i < sign.length; i++) {
				sig[i] = sign[i];
			}
            for (int i : sig) {
            	signature += String.valueOf(i);
			}

	    }catch (IOException e) {
	        logger.warn("write file error", e);
	    } finally{
/*	        try {
	        	bis.close();
			} catch (IOException e) {
				e.printStackTrace();
			}*/
	    }
        System.out.println(signature);
        return signature;
    }
    
	public String saveToLocal(HttpEntity entity, int taskId) {
	 	String type = entity.getContentType().getValue();
        int begin = type.indexOf('/') + 1;
        
        String fileFormat;
        if (type.contains(" ")) {
        	fileFormat = type.substring(begin, type.indexOf(' '));
		} else {
			fileFormat = type.substring(begin);
		}
        System.out.println( "file format is  " + fileFormat);
        
 		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd_HHmmss_SSS");
		String dateString = sdf.format(new Date());
		
		String path = this.path;
		String fileFullName = path + taskId + "_" + dateString + "." + fileFormat;
		BufferedInputStream bis = null;
		BufferedOutputStream bos = null;
        try {
        	
            bis = new BufferedInputStream(entity.getContent());
            bos = new BufferedOutputStream(new FileOutputStream(getFile(fileFullName)));
            byte[] b = new byte[1024];
            int len;
            while((len = bis.read(b, 0, 1024)) != -1){
	            bos.write(b, 0, len);
            }  
            bos.flush();

        } catch (IOException e) {
            logger.warn("write file error", e);
        } finally{
            try {
				EntityUtils.consume(entity);
            	bis.close();
				bos.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
        }
        System.out.println(fileFullName);
        return fileFullName;
    }
}
