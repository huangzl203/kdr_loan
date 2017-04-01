<%@page import="com.sun.org.apache.xml.internal.serialize.Printer"%>
<%@ page language="java" import="java.util.*" pageEncoding="utf-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<meta http-equiv="pragma" content="no-cache" />
<meta http-equiv="cache-control" content="no-cache" />
<meta http-equiv="expires" content="0" />
<link href="<c:url value='${fileServerUrl }/web_res/css/hhncss.css?t=${jsversion}'/>" rel="stylesheet" type="text/css" />
<link href="<c:url value='${fileServerUrl }/web_res/css/Site2.css?t=${jsversion}'/>" rel="stylesheet" type="text/css" />
<link href="<c:url value='${fileServerUrl }/web_res/css/lytebox.css?t=${jsversion}'/>'" rel="stylesheet" type="text/css" />
<!--IE6透明判断-->
<!--[if IE 6]>
<script src="../css/DD_belatedPNG_0.0.8a-min.js"></script>
<script>
DD_belatedPNG.fix('.flash_bar,#tit_fc1,#tit_fc2,#tit_fc3,#tit_fc4,#flashLine,#right_tel,#right_qq,#right_tip,.login_all,.wytz_center_onez,.wytz_center_one,img');
</script>
<![endif]-->

<script src="<c:url value='${fileServerUrl }/web_res/js/add_all.js?t=${jsversion}'/>" type="text/javascript"></script>
<script src="<c:url value='${fileServerUrl }/web_res/js/MSClass.js?t=${jsversion}'/>" type="text/javascript"></script>
<script type="text/javascript" src="<c:url value='${fileServerUrl }/web_res/js/jquery-1.7.1.min.js'/>"></script>
<script type="text/javascript" src="<c:url value='${fileServerUrl }/web_res/js/jquery.shove-1.0.js?t=${jsversion}'/>"></script>
<script type="text/javascript" src="<c:url value='${fileServerUrl }/web_res/js/lytebox.js?t=${jsversion}'/>"></script>