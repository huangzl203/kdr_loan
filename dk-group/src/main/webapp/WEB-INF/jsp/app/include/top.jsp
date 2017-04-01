<%@ page language="java" contentType="text/html; charset=utf-8" pageEncoding="utf-8"%>
<%
	response.setHeader("Pragma","No-cache");
	response.setHeader("Cache-Control","no-cache");
	response.setDateHeader("Expires", 0);
	String path = request.getContextPath();
	String temp=request.getServerPort()==80?"":":"+request.getServerPort();
	String basePath = request.getScheme() + "://" + request.getServerName() + temp + path + "/";
	if(application.getAttribute(basePath)==null){
		application.setAttribute("basePath",basePath);
	}
	
%>
<meta http-equiv="Cache-Control" content="no-cache" />
<meta name="viewport" content="width=device-width initial-scale=1.0 maximum-scale=1.0 user-scalable=0" />
<meta name="format-detection" content="telephone=no" />
<meta name="MobileOptimized" content="320" />
<link href="${basePath }/app_res/css/elend/eLoan.css?v=${cssversion}" rel="stylesheet" type="text/css">
<link rel="stylesheet" href="${basePath }/app_res/css/common.css?v=${cssversion}">
<link rel="stylesheet" href="${basePath }/app_res/css/mhk/style.css?v=${cssversion}">
<script type="text/javascript" src="http://tajs.qq.com/stats?sId=48752908" charset="UTF-8"></script>
