package kqxt.wechat.util;

import java.io.BufferedReader;  
import java.io.InputStream;  
import java.io.InputStreamReader;  
import java.io.OutputStream;  
import java.io.UnsupportedEncodingException;  
import java.net.ConnectException;  
import java.net.URL;  
import java.security.MessageDigest;  
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.Formatter;  
import java.util.HashMap;  
import java.util.Map;  
import java.util.UUID;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;  
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.SSLSocketFactory;  
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import javax.servlet.http.HttpServletRequest;

import net.sf.json.JSONObject;  
  
public class WeixinUtil {  
	private static class SecurityTrustManager implements X509TrustManager {

		public void checkClientTrusted(X509Certificate[] chain, String authType) throws CertificateException {
		}

		public void checkServerTrusted(X509Certificate[] chain, String authType) throws CertificateException {
		}

		public X509Certificate[] getAcceptedIssuers() {
			return new X509Certificate[] {};
		}
	}

	private static class TrustyHostnameVerifier implements HostnameVerifier {
		public boolean verify(String hostname, SSLSession session) {
			return true;
		}
	}
    /** 
    * ��������httpRequest</br> 
    * ����������http����</br> 
    * ������Ա��souvc </br> 
    * ����ʱ�䣺2016-1-5  </br> 
    * @param requestUrl 
    * @param requestMethod 
    * @param outputStr 
    * @return ˵������ֵ���� 
    * @throws ˵���������쳣������ 
     */  
	public static String sendHttpsByGet(String url) {
		String result = "";
		try {
			SSLContext sslContext = SSLContext.getInstance("SSL", "SunJSSE");  
			TrustManager[] trustManager = { new SecurityTrustManager() };
			sslContext.init(null, trustManager, new java.security.SecureRandom());
			HttpsURLConnection httpsConn = (HttpsURLConnection) new URL(url).openConnection();
			httpsConn.setSSLSocketFactory(sslContext.getSocketFactory());
			httpsConn.setHostnameVerifier(new TrustyHostnameVerifier());
			httpsConn.setConnectTimeout(60000);
			httpsConn.setReadTimeout(60000);
			httpsConn.setRequestMethod("GET");
			httpsConn.connect();

			BufferedReader in = new BufferedReader(new InputStreamReader(httpsConn.getInputStream(), "UTF-8"));
			String line = null;
			while ((line = in.readLine()) != null) {
				result += line;
			}
			in.close();
			httpsConn.disconnect();
		} catch (Exception e) {
			System.out.println("Exception at SendHttpsRequest.sendHttpsByGet() : " + e.getMessage());
			e.printStackTrace();
		}
		return result;
	}

    public static JSONObject httpRequest(String requestUrl,String requestMethod, String outputStr) {  
        JSONObject jsonObject = null;  
        StringBuffer buffer = new StringBuffer();  
        try {  
        	TrustManager[] trustManager = { new SecurityTrustManager() };
            SSLContext sslContext = SSLContext.getInstance("SSL", "SunJSSE");  
            sslContext.init(null, trustManager, new java.security.SecureRandom());  
            SSLSocketFactory ssf = sslContext.getSocketFactory();  
            URL url = new URL(requestUrl);  
            HttpsURLConnection httpUrlConn = (HttpsURLConnection) url.openConnection();  
            httpUrlConn.setSSLSocketFactory(ssf);  
            httpUrlConn.setDoOutput(true);  
            httpUrlConn.setDoInput(true);  
            httpUrlConn.setUseCaches(false);  
            httpUrlConn.setRequestMethod(requestMethod);  
            if ("GET".equalsIgnoreCase(requestMethod))  
                httpUrlConn.connect();  
            if (null != outputStr) {  
                OutputStream outputStream = httpUrlConn.getOutputStream();  
                outputStream.write(outputStr.getBytes("UTF-8"));  
                outputStream.close();  
            }  
            InputStream inputStream = httpUrlConn.getInputStream();  
            InputStreamReader inputStreamReader = new InputStreamReader(inputStream, "utf-8");  
            BufferedReader bufferedReader = new BufferedReader(inputStreamReader);  
            String str = null;  
            while ((str = bufferedReader.readLine()) != null) {  
                buffer.append(str);  
            }  
            bufferedReader.close();  
            inputStreamReader.close();  
            inputStream.close();  
            inputStream = null;  
            httpUrlConn.disconnect();  
            jsonObject = JSONObject.fromObject(buffer.toString());  
        } catch (ConnectException ce) {  
            ce.printStackTrace();  
        } catch (Exception e) {  
            e.printStackTrace();  
        }  
        return jsonObject;  
    }  
  
      
    /** 
    * ��������getWxConfig</br> 
    * ��������ȡ΢�ŵ�������Ϣ </br> 
    * ������Ա��souvc  </br> 
    * ����ʱ�䣺2016-1-5  </br> 
    * @param request 
    * @return ˵������ֵ���� 
    * @throws ˵���������쳣������ 
     */  
    public static Map<String, Object> getWxConfig(HttpServletRequest request) {  
        Map<String, Object> ret = new HashMap<String, Object>();  
        
        String appId = "wx8427a4d6efeef5e5"; // ������ںŵ�Ψһ��ʶ  
        String secret = "4b0d70b7713e8fbb3f2ec9fc79881927";  
  
        String requestUrl = request.getRequestURL().toString();  
        String access_token = "";  
        String jsapi_ticket = "";  
        String timestamp = Long.toString(System.currentTimeMillis() / 1000); // �������ǩ����ʱ���  
        String nonceStr = UUID.randomUUID().toString(); // �������ǩ���������  
        String url = "https://api.weixin.qq.com/cgi-bin/token?grant_type=client_credential&appid="+ appId + "&secret=" + secret;  
          
        JSONObject json = WeixinUtil.httpRequest(url, "GET", null);  
          
        if (json != null) {  
            //Ҫע�⣬access_token��Ҫ����  
            access_token = json.getString("access_token");  
              
            url = "https://api.weixin.qq.com/cgi-bin/ticket/getticket?access_token="+access_token+"&type=jsapi";  
            json = WeixinUtil.httpRequest(url, "GET", null);  
            if (json != null) {  
                jsapi_ticket = json.getString("ticket");  
            }  
        }  
        String signature = "";  
        // ע���������������ȫ��Сд���ұ�������  
        String sign = "jsapi_ticket=" + jsapi_ticket + "&noncestr=" + nonceStr+ "&timestamp=" + timestamp + "&url=" + requestUrl;  
        try {  
            MessageDigest crypt = MessageDigest.getInstance("SHA-1");  
            crypt.reset();  
            crypt.update(sign.getBytes("UTF-8"));  
            signature = byteToHex(crypt.digest());  
        } catch (NoSuchAlgorithmException e) {  
            e.printStackTrace();  
        } catch (UnsupportedEncodingException e) {  
            e.printStackTrace();  
        }  
        ret.put("appId", appId);  
        ret.put("timestamp", timestamp);  
        ret.put("nonceStr", nonceStr);  
        ret.put("signature", signature);  
        return ret;  
    }  
  
      
    /** 
    * ��������byteToHex</br> 
    * �������ַ������ܸ������� </br> 
    * ������Ա��souvc  </br> 
    * ����ʱ�䣺2016-1-5  </br> 
    * @param hash 
    * @return ˵������ֵ���� 
    * @throws ˵���������쳣������ 
     */  
    private static String byteToHex(final byte[] hash) {  
        Formatter formatter = new Formatter();  
        for (byte b : hash) {  
            formatter.format("%02x", b);  
        }  
        String result = formatter.toString();  
        formatter.close();  
        return result;  
  
    }  
}  