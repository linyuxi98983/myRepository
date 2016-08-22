<%@ page language="java" import="java.util.*" contentType="text/html; charset=utf-8"  pageEncoding="utf-8"%>  
<%@page language="java" import="kqxt.wechat.util.WeixinUtil" %>  
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">  
<html>  
<head>  
<meta http-equiv="Content-Type" content="text/html; charset=utf-8">  
<title>  </title>  
<%  
        Map<String,Object>  ret = new HashMap<String,Object> ();  
        ret=WeixinUtil.getWxConfig(request);  
        request.setAttribute("appId", ret.get("appId"));  
        request.setAttribute("timestamp", ret.get("timestamp"));  
        request.setAttribute("nonceStr", ret.get("nonceStr"));  
        request.setAttribute("signature", ret.get("signature"));  
        %>  
</head>  
<body>  
 appId :${appId }<br>
 timestamp:${timestamp}<br>
 nonceStr: ${nonceStr}<br>
 signature:${signature}<br>
  
 result:<span id="result"></span><br>
  <button id="scanQRCode">扫一扫</button>
</body>  
  
  
<script type="text/javascript" src="http://res.wx.qq.com/open/js/jweixin-1.0.0.js"></script>  
<script type="text/javascript">  
  
// 微信信息的以及调用的配置  
        wx.config({  
        debug: false,  
        appId: '${appId}',  
        timestamp: '${timestamp}',  
        nonceStr: '${nonceStr}',  
        signature: '${signature}',  
        jsApiList: ['scanQRCode']  
        });  
  
        document.querySelector('#scanQRCode').onclick = function () {
        	wx.scanQRCode({
        		needResult: 1,
        	    desc: 'scanQRCode desc', // 默认为0，扫描结果由微信处理，1则直接返回扫描结果，        	    
        	    success: function (res) {
        	    	alert(JSON.stringify(res));
        		}
        	}); 
        }; 
</script>  
  
  
</html> 