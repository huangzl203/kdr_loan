<%@ page language="java" contentType="text/html; charset=utf-8" pageEncoding="utf-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%--
<nav>
  <ul class="bs db_f footer_nav">
    <li class="bf1"><a href="${hehuayidaiwelcome}"><span>首页</span></a></li>
    <li class="bf1"><a href="${hehuayidaismallloan}"><span>三人微贷</span></a></li>
    <li class="bf1"><a href="${hehuayidaipersoncenter}"><span>个人中心</span></a></li>
  </ul>
</nav>
 --%>
<c:if test="${platform ne 'android' && platform ne 'ios' && platform ne 'pad'}">
<div class="fix-nav">
    	<!-- active-n 代表第n个高亮 -->
        <div class="active-0">
            <div class="fix-nav-wrap">
                <a href="${hehuayidaiwelcome}">
                    <i class="icon icon-loan"></i>
                    <p>首页</p>
                </a>
                <!--  
                <a href="${basePath}/product/toPeriodApply.do">
                    <i class="icon icon-pro"></i>
                    <p>去分期</p>
                </a>
              
                <a href="${hehuayidaismallloan}">
                    <i class="icon icon-three"></i>
                    <p>三人微贷</p>
                </a>
                  -->
                <a href="${hehuayidaipersoncenter}">
                    <i class="icon icon-setting"></i>
                    <p>我</p>
                </a>
            </div>
        </div>
</div>
</c:if>
<script src="${basePath }/app_res/js/libs/zepto.js?v=${jsversion}"></script>
<script src="${basePath }/app_res/js/app.js?v=${jsversion}"></script>

