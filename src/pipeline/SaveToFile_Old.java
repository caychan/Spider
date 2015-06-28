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


public class SaveToFile_Old extends FilePersistentBase {
	
    private Logger logger = LoggerFactory.getLogger(getClass());
	private static final int LENGTH = 128;
	DBUtils dbUtils = new DBUtils();
    
    public SaveToFile_Old() {
        setPath("F:\\Clawer");
    }

    public SaveToFile_Old(String path) {
        setPath(path);
    }
    
/*    public String getSignature(HttpEntity entity, int taskId, String url){
        byte[] signature = new byte[LENGTH];
        boolean first = true;
		BufferedInputStream bis = null;
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
	            if (first == true) {// 第一次不做xor处理，zhishi
					signature = b;
					first = false;
				}else {
					signature = NumberUtils.xor(LENGTH, signature, b);
				}
            }
            //将signature插入到数据库中
            int[] sig = new int[LENGTH];

            for (int i = 0; i < signature.length; i++) {
				sig[i] = signature[i];
			}
            String sign = "";
            for (int i : sig) {
            	sign += String.valueOf(i);
			}

    	return sign;
    }*/
    
	public void save(HttpEntity entity, int taskId, String url) {
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
        byte[] signature = new byte[LENGTH];
        boolean first = true;
		BufferedInputStream bis = null;
		BufferedOutputStream bos = null;
        try {
//	        	InputStream is = entity.getContent();
            bis = new BufferedInputStream(entity.getContent());
            bos = new BufferedOutputStream(new FileOutputStream(getFile(fileFullName)));

            byte[] b = new byte[LENGTH];

            int len, readBytes;
            boolean flag = true;
            while(flag){
            	readBytes = 0;
	            while (readBytes < LENGTH) {  //保证每次都能将b读满
	            	len = bis.read(b, readBytes, LENGTH - readBytes);
	            	if (len == -1) {   //判断是不是读到了数据流的末尾 ，防止出现死循环。
	            		System.out.println("readbyte is "+readBytes);
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
	            
//		            System.out.println("------> " +readBytes);
	            bos.write(b, 0, readBytes);
	            if (first == true) {
					signature = b;
					first = false;
				}else {
					signature = NumberUtils.xor(LENGTH, signature, b);
				}
            }
            bos.flush();
            //将signature插入到数据库中
            int[] sig = new int[LENGTH];

            for (int i = 0; i < signature.length; i++) {
				sig[i] = signature[i];
			}
            String sign = "";
            for (int i : sig) {
            	sign += String.valueOf(i);
			}
//	            String sign = new String(signature);
//            dbUtils.insertOrUpdateResource(taskId, type, url, fileFullName, sign);

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
    }
}
