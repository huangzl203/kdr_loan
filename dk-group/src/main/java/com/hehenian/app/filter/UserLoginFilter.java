package com.hehenian.app.filter;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.log4j.Logger;
import org.codehaus.jackson.map.ObjectMapper;
import org.springframework.util.StringUtils;

import com.hehenian.common.constant.Constants;
import com.hehenian.model.account.UserAccount;


/**
 * @Description 
 *  用来跳转到统一的登录认证中心
 *  系统需要配置登录URL
 *  并且将用户访问的url传递给登录中心， 登录成功后跳回用户想访问的url
 *  参数名称： fromUrl
 * @author huangzl QQ: 272950754
 * @date 2015年6月15日 下午2:47:40
 * @Project hehenian-lend-app
 * @Package com.hehenian.app.filter 
 * @File UserLoginFilter.java
*/
public class UserLoginFilter implements Filter {
    protected String loginView;
    protected String mobileLoginView;

    Logger logger = Logger.getLogger(this.getClass());

    public String getLoginView() {
        return loginView;
    }

    public void setLoginView(String loginView) {
        this.loginView = loginView;
    }

    public String getMobileLoginView() {
        return mobileLoginView;
    }

    public void setMobileLoginView(String mobileLoginView) {
        this.mobileLoginView = mobileLoginView;
    }

    public UserLoginFilter() {}

    public void destroy() {}

    /**
     * 
     * 过滤掉不需要登录就可以查看的url
     * 其他url 都先检测是否登录过， 没有重定向到登录中心
     * 
     * 
     */
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        HttpServletRequest req = (HttpServletRequest)request;
        HttpServletResponse resp = (HttpServletResponse) response;
        String uri = req.getRequestURI();
//        String fromUrl =req.getScheme()+"://"+req.getServerName()+":"+req.getServerPort()+uri;
        HttpSession session = req.getSession();
        setParameter(session, new HashMap(req.getParameterMap()));
        if (uri.indexOf("/app/group/getAuth")!=-1 ||uri.indexOf(".ico")!=-1 ||uri.indexOf("/app_res/")!=-1||uri.indexOf("/web_res/")!=-1){//uri.endsWith("/index") ||uri.indexOf("/app/mhk/")!=-1||uri.indexOf("/app/elend/")!=-1||uri.indexOf("/app/group/")!=-1
            chain.doFilter(request, response);
        }else {
        	UserAccount user = (UserAccount)session.getAttribute(Constants.USER_ACCOUNT);
            logger.info("uri:=======>"+uri);
            String loginCenterUrl = loginView;
            if (user == null || user.getId()==null) {
                logger.info("loginView:=======>"+loginCenterUrl);
                StringBuffer urlTemp=req.getRequestURL();
                String urlTemp1=req.getQueryString();
                uri = urlTemp+"?"+urlTemp1 + ";s="+ session.getId();
                uri =  ( java.net.URLEncoder.encode(uri,   "utf-8")); 
                resp.sendRedirect(loginCenterUrl + ";s="+ session.getId()+"?fromUrl=" + uri);
            } else {
                chain.doFilter(request, response);
            }	
        }
    }

//    public   String callUrl(String sUrl) {
//        String s = null;
//        InputStream is = null;
//        URL url = null;
//        HttpURLConnection connect = null;
//        try {
//            url = new URL(sUrl);
//            connect = (HttpURLConnection) url.openConnection();
//            is = connect.getInputStream();
//            int len = 0;
//            byte[] b = new byte[2000];
//            if ((len = is.read(b)) > 0) {
//                s = new String(b, 0, len);
//            }
//            logger.info(sUrl + "randcode"+s+"[success]");
//        } catch (Exception e) {
//            e.printStackTrace();
//            logger.error(sUrl + "[error][" + e.getMessage() + "]");
//        } finally {
//            try {
//                if (is != null) {
//                    is.close();
//                }
//                 if(connect != null){
//                 connect.disconnect();
//                 }
//            } catch (Exception e) {
//                 logger.error(sUrl + "[error][" + e.getMessage() + "]");
//            }
//        }
//        return s;
//    }
    private void setParameter(HttpSession session, Map map){
        if (map!=null && map.size()>0){
            Map temp=(Map)session.getAttribute("parameterMap");
            Map newMap=new HashMap();
            if(temp!=null) {
                newMap.putAll(temp);
            }
            if(map!=null) {
                newMap.putAll(map);
            }
            session.setAttribute("parameterMap", newMap);
        }
    }

    public void init(FilterConfig fConfig) throws ServletException {
       ServletContext sc=fConfig.getServletContext();
        sc.setAttribute("doLoginView", loginView);
        sc.setAttribute("mobileLoginView", mobileLoginView);
    }

}
