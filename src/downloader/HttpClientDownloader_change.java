package downloader;

import clawer.Page;
import clawer.Request;
import clawer.Site;
import clawer.Task;

import com.google.common.collect.Sets;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpHost;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.annotation.ThreadSafe;
import org.apache.http.client.config.CookieSpecs;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.client.methods.RequestBuilder;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.util.EntityUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import selector.PlainText;
import utils.HttpConstant;
import utils.UrlUtils;

import java.io.IOException;
import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;


/**
 * The http downloader based on HttpClient.
 */
@ThreadSafe
public class HttpClientDownloader_change extends AbstractDownloader {

    private Logger logger = LoggerFactory.getLogger(getClass());
    private final Map<String, CloseableHttpClient> httpClients = new HashMap<String, CloseableHttpClient>();
    private HttpClientGenerator httpClientGenerator = new HttpClientGenerator();

    public CloseableHttpClient getHttpClient(Site site) {
        if (site == null) {
            return httpClientGenerator.getClient(null);
        }
        String domain = site.getDomain();
        CloseableHttpClient httpClient = httpClients.get(domain);
        if (httpClient == null) {
            synchronized (this) {
                httpClient = httpClients.get(domain);
                if (httpClient == null) {
                    httpClient = httpClientGenerator.getClient(site);
                    httpClients.put(domain, httpClient);
                }
            }
        }
        return httpClient;
    }


    public HttpEntity downloadEntity(Request request, Task task) {
    	Site site = null;
    	if (task != null) {
    		site = task.getSite();
    	}
//    	Set<Integer> acceptStatCode;
//    	String charset = null;
    	Map<String, String> headers = null;
    	
    	if (site != null) {
    		headers = site.getHeaders();
//    		acceptStatCode = site.getAcceptStatCode();
//    		charset = site.getCharset();
    	} /*else {
    		acceptStatCode = Sets.newHashSet(200);
    	}*/
    	
    	logger.info("downloading page {}", request.getUrl());
    	
    	CloseableHttpResponse httpResponse = null;
    	int statusCode=0;

    	try {
    		HttpUriRequest httpUriRequest = getHttpUriRequest(request, site, headers);		//得到request
    		httpResponse = getHttpClient(site).execute(httpUriRequest);   //得到httpClient，执行request，得到httpResponse
    		if (httpResponse != null) {
					return httpResponse.getEntity();
			} else {
				return null;
			}

//    		statusCode = httpResponse.getStatusLine().getStatusCode();
    		
/*    		if (statusAccept(acceptStatCode, statusCode)) {
    			onSuccess(request);
    			return httpResponse.getEntity();
    		} else {
    			logger.warn("code error " + statusCode + "\t" + request.getUrl());
    			return null;
    		}*/
    	} catch (IOException e) {
    		logger.warn("download page " + request.getUrl() + " error", e);
    		onError(request);
    		return null;
    	} finally {
    		request.putExtra(Request.STATUS_CODE, statusCode);
/*    		try {
    			if (httpResponse != null) {
    				//ensure the connection is released back to pool
    				EntityUtils.consume(httpResponse.getEntity()); //这句导致了后面的写入文件错误.entity所得到的流是不可重复读取的也就是说所得的到实体只能一次消耗完,不能多次读取
    															//所以在执行EntityUtils.toString(entity)后,流就关闭了,就导致后面的读和写显示错误.
    			}
    		} catch (IOException e) {
    			logger.warn("close response fail", e);
    		}*/
    	}
    }
    @Override
    public Page download(Request request, Task task) {
    	Site site = null;
        if (task != null) {
            site = task.getSite();
        }
        Set<Integer> acceptStatCode;
        String charset = null;
        Map<String, String> headers = null;
        
        if (site != null) {
            acceptStatCode = site.getAcceptStatCode();
            charset = site.getCharset();
            headers = site.getHeaders();
        } else {
            acceptStatCode = Sets.newHashSet(200);
        }
        
        logger.info("downloading page {}", request.getUrl());
        
        CloseableHttpResponse httpResponse = null;
        int statusCode=0;
        String contentType = null;
        try {
            HttpUriRequest httpUriRequest = getHttpUriRequest(request, site, headers);		//得到request
            httpResponse = getHttpClient(site).execute(httpUriRequest);   //得到httpClient，执行request，得到httpResponse
            
            Header header = httpResponse.getEntity().getContentType();
            contentType = header.toString();
            
            statusCode = httpResponse.getStatusLine().getStatusCode();
            request.putExtra(Request.STATUS_CODE, statusCode);
            
            if (statusAccept(acceptStatCode, statusCode)) {
//************************** 得到page *******************************
            	Page page = handleResponse(request, charset, httpResponse, contentType);
//              Page page = handleResponse(request, charset, httpResponse, task, contentType);
                onSuccess(request);
                return page;
            } else {
                logger.warn("code error " + statusCode + "\t" + request.getUrl());
                return null;
            }
        } catch (IOException e) {
            logger.warn("download page " + request.getUrl() + " error", e);
            if (site.getCycleRetryTimes() > 0) {
                return addToCycleRetry(request, site);
            }
            onError(request);
            return null;
        } finally {
        	request.putExtra(Request.STATUS_CODE, statusCode);
            try {
                if (httpResponse != null) {
                    //ensure the connection is released back to pool
                    EntityUtils.consume(httpResponse.getEntity());
                }
            } catch (IOException e) {
                logger.warn("close response fail", e);
            }
        }
    }

    @Override
    public void setThread(int thread) {
        httpClientGenerator.setPoolSize(thread);
    }

    protected boolean statusAccept(Set<Integer> acceptStatCode, int statusCode) {
        return acceptStatCode.contains(statusCode);
    }

    protected HttpUriRequest getHttpUriRequest(Request request, Site site, Map<String, String> headers) {
        RequestBuilder requestBuilder = selectRequestMethod(request).setUri(request.getUrl());
        if (headers != null) {
            for (Map.Entry<String, String> headerEntry : headers.entrySet()) {
                requestBuilder.addHeader(headerEntry.getKey(), headerEntry.getValue());
            }
        }
        RequestConfig.Builder requestConfigBuilder = RequestConfig.custom()
                .setConnectionRequestTimeout(site.getTimeOut())
                .setSocketTimeout(site.getTimeOut())
                .setConnectTimeout(site.getTimeOut())
                .setCookieSpec(CookieSpecs.BEST_MATCH);
        if (site.getHttpProxyPool() != null && site.getHttpProxyPool().isEnable()) {
            HttpHost host = site.getHttpProxyFromPool();
			requestConfigBuilder.setProxy(host);
			request.putExtra(Request.PROXY, host);
		}
        requestBuilder.setConfig(requestConfigBuilder.build());
        	return requestBuilder.build();
    }

    //得到request的方法，get，post等
    protected RequestBuilder selectRequestMethod(Request request) {
        String method = request.getMethod();
        if (method == null || method.equalsIgnoreCase(HttpConstant.Method.GET)) {
            //default get
            return RequestBuilder.get();
        } else if (method.equalsIgnoreCase(HttpConstant.Method.POST)) {
            RequestBuilder requestBuilder = RequestBuilder.post();
            NameValuePair[] nameValuePair = (NameValuePair[]) request.getExtra("nameValuePair");
            if (nameValuePair.length > 0) {
                requestBuilder.addParameters(nameValuePair);
            }
            return requestBuilder;
        } else if (method.equalsIgnoreCase(HttpConstant.Method.HEAD)) {
            return RequestBuilder.head();
        } else if (method.equalsIgnoreCase(HttpConstant.Method.PUT)) {
            return RequestBuilder.put();
        } else if (method.equalsIgnoreCase(HttpConstant.Method.DELETE)) {
            return RequestBuilder.delete();
        } else if (method.equalsIgnoreCase(HttpConstant.Method.TRACE)) {
            return RequestBuilder.trace();
        }
        throw new IllegalArgumentException("Illegal HTTP Method " + method);
    }

    
    //得到page的各种信息，url，statusCode，contentType等
    //protected Page handleResponse(Request request, String charset, HttpResponse httpResponse, Task task, String contentType) throws IOException {
    protected Page handleResponse(Request request, String charset, HttpResponse httpResponse, String contentType) throws IOException {
        String content = getContent(charset, httpResponse);
        Page page = new Page();
        page.setRawText(content);
        page.setUrl(new PlainText(request.getUrl()));
        page.setRequest(request);
        page.setStatusCode(httpResponse.getStatusLine().getStatusCode());
        
        page.setContentType(contentType);
        return page;
    }

    //得到response的content
    protected String getContent(String charset, HttpResponse httpResponse) throws IOException {
        if (charset == null) {
            byte[] contentBytes = IOUtils.toByteArray(httpResponse.getEntity().getContent());
            String htmlCharset = getHtmlCharset(httpResponse, contentBytes);
            if (htmlCharset != null) {
                return new String(contentBytes, htmlCharset);
            } else {
                logger.warn("Charset autodetect failed, use {} as charset. Please specify charset in Site.setCharset()", Charset.defaultCharset());
                return new String(contentBytes);
            }
        } else {
            return IOUtils.toString(httpResponse.getEntity().getContent(), charset);
        }
    }

    //得到html的charset
    protected String getHtmlCharset(HttpResponse httpResponse, byte[] contentBytes) throws IOException {
        String charset;
        // charset
        // 1、encoding in http header Content-Type
        String value = httpResponse.getEntity().getContentType().getValue();
        charset = UrlUtils.getCharset(value);
        if (StringUtils.isNotBlank(charset)) {
            logger.debug("Auto get charset: {}", charset);
            return charset;
        }
        // use default charset to decode first time
        Charset defaultCharset = Charset.defaultCharset();
        String content = new String(contentBytes, defaultCharset.name());
        // 2、charset in meta
        if (StringUtils.isNotEmpty(content)) {
            Document document = Jsoup.parse(content);
            Elements links = document.select("meta");
            for (Element link : links) {
                // 2.1、html4.01 <meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
                String metaContent = link.attr("content");
                String metaCharset = link.attr("charset");
                if (metaContent.indexOf("charset") != -1) {
                    metaContent = metaContent.substring(metaContent.indexOf("charset"), metaContent.length());
                    charset = metaContent.split("=")[1];
                    break;
                }
                // 2.2、html5 <meta charset="UTF-8" />
                else if (StringUtils.isNotEmpty(metaCharset)) {
                    charset = metaCharset;
                    break;
                }
            }
        }
        logger.debug("Auto get charset: {}", charset);
        // 3、todo use tools as cpdetector for content decode
        return charset;
    }
}
