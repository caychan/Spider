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

import com.sun.xml.internal.ws.Closeable;

public class Test {

	public static void main(String[] args) throws ClientProtocolException, IOException {
		
		HttpClientBuilder hcb = HttpClientBuilder.create();
		CloseableHttpClient httpClient = hcb.build();

		String url = "http://bbs.lnu.edu.cn/forum.php?mod=viewthread&tid=3161&extra=page%3D1";
		HttpUriRequest httpUriRequest = RequestBuilder.get().setUri(url).build();
		CloseableHttpResponse response = httpClient.execute(httpUriRequest);
		
//		HttpGet httpget = new HttpGet(url);
//		CloseableHttpResponse response = httpClient.execute(httpget);
		
		
		
		
		HttpEntity entity = response.getEntity();
		byte[] cont = IOUtils.toByteArray(entity.getContent());
		System.out.println(new String(cont, "utf-8"));
		
	}

}
