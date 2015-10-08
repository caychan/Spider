package downloader;

import java.io.IOException;

import org.apache.commons.io.IOUtils;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.client.methods.RequestBuilder;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;

import sun.print.resources.serviceui;

import com.sun.xml.internal.ws.Closeable;

import f_process.Search;

public class Test {

	public static void main(String[] args) throws ClientProtocolException, IOException, InterruptedException {
/*		
		HttpClientBuilder hcb = HttpClientBuilder.create();
		CloseableHttpClient httpClient = hcb.build();

		String url = "http://bbs.lnu.edu.cn/forum.php?mod=viewthread&tid=3161&extra=page%3D1";
		HttpUriRequest httpUriRequest = RequestBuilder.get().setUri(url).build();
		CloseableHttpResponse response = httpClient.execute(httpUriRequest);
		
//		HttpGet httpget = new HttpGet(url);
//		CloseableHttpResponse response = httpClient.execute(httpget);
		
		
		
		
		HttpEntity entity = response.getEntity();
		byte[] cont = IOUtils.toByteArray(entity.getContent());
		
		System.out.println(new String(cont, "utf-8"));*/

//		System.out.println(test());
		
		final Search search = new Search();
		Thread thread = new Thread(new Runnable() {
			@Override
			public void run() {
				try {
					search.M();
					Thread.sleep(13000);
					System.out.println("====");
					search.stopSearch();
					Thread.sleep(5000);
					search.M();
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		});
		Thread thread2 = new Thread(new Runnable() {
			
			@Override
			public void run() {
				try {
					Thread.sleep(3000);
					System.out.println("-----");
					search.stopSearch();
					
					Thread.sleep(5000);
					System.out.println("-----");
					search.M();
					
					
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				
			}
		});
		thread.start();
		thread2.start();
		

	}
	
	
	static int test(){
		int i = 1;
		try{
			i = 2;
			return i++;
		}catch(Exception e){
			return 3;
		} finally{
			i ++;
//			return i;
		}
	}
	

}
