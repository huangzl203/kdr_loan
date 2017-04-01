package com.hehenian.app.view.loan.group;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.type.TypeReference;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.hehenian.common.account.ILoanUserPersonService;
import com.hehenian.common.account.ILoanUserService;
import com.hehenian.common.constant.Constants;
import com.hehenian.common.file.IFileServerService;
import com.hehenian.common.job.IJobService;
import com.hehenian.common.loan.IGeneratorCodeService;
import com.hehenian.common.order.IOrderService;
import com.hehenian.common.product.ILoanProductSchemeService;
import com.hehenian.common.product.ILoanProductService;
import com.hehenian.common.repay.ISettleCalculatorService;
import com.hehenian.common.result.IResult;
import com.hehenian.common.result.ResultSupport;
import com.hehenian.common.sms.INotifyService;
import com.hehenian.common.util.CalculateUtils;
import com.hehenian.common.util.HttpClientUtils;
import com.hehenian.common.util.IdCardUtils;
import com.hehenian.common.util.Md5Utils;
import com.hehenian.model.account.LoanUserChannelReDo;
import com.hehenian.model.account.LoanUserDo;
import com.hehenian.model.account.LoanUserPersonDo;
import com.hehenian.model.account.UserAccount;
import com.hehenian.model.job.JobDo;
import com.hehenian.model.job.JobDo.JobType;
import com.hehenian.model.order.LoanChannelUserDo;
import com.hehenian.model.order.LoanOrderDo;
import com.hehenian.model.order.LoanOrderDo.LoanStatus;
import com.hehenian.model.product.LoanProductDo;
import com.hehenian.model.product.LoanProductSchemeDo;
import com.hehenian.model.repay.SettDetailDo;
import com.hehenian.model.sms.NotifyDo;
import com.hehenian.model.sms.SMSNotifyDo;
import com.hehenian.web.common.constant.WebConstants;

/**
 * @Description 集团贷款申请控制器
 * @author huangzl QQ: 272950754
 * @date 2015年6月8日 上午10:05:42
 * @Project hehenian-lend-app
 * @Package com.hehenian.app.view.loan.group
 * @File LoanGroupController.java
 */
@Controller
@RequestMapping(value = "/app/group")
public class LoanGroupController {

	private final Logger logger = Logger.getLogger(this.getClass());

    @Autowired
    private IOrderService orderService;
    @Autowired
	private IGeneratorCodeService generatorCodeService;
	@Autowired
	private ILoanUserService loanUserService;
	@Autowired
    private ILoanUserPersonService loanUserPersonService;
	@Autowired
	private ILoanProductService loanProductService;
    @Autowired
    private ILoanProductSchemeService loanProductSchemeService;
	@Autowired
	private ISettleCalculatorService settleCalculatorService;
	@Autowired
	private IJobService jobService;
	@Autowired
	private IFileServerService fileServerService;
	
//	@Autowired
//	private INotifyService notifyService;
	
	@Value("#{sysconfig['color.life.AG.URL']}")
	private String colorLifeAG_URL;
	@Value("#{sysconfig['color.life.AG.COLOR.APP.ID']}")
    private String COLOR_APP_ID;
	@Value("#{sysconfig['color.life.AG.COLOR.TOKEN']}")
    private String COLOR_TOKEN;
	@Value("#{sysconfig['hehuayidai.person.center']}")
	private String personalCenter;
	@Value("#{sysconfig['hhnMobile.loginUrl']}")
	private String loginUrl;
	
	
	private String getIndexUrl(HttpServletRequest request){
		String path = request.getContextPath();
		String basePath = request.getScheme()+"://"+request.getServerName()+":"+request.getServerPort()+path;
		System.out.println("basePath:"+basePath);
		String indexUrl=basePath+"/app/group/welcome.do";
		try {
			indexUrl =  ( java.net.URLEncoder.encode(indexUrl,   "utf-8"));
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		} 
		return indexUrl;
	}
	/**
	 * 获取彩生活登录认证
	 * @param session
	 * @param request
	 * @return
	 */
	@RequestMapping("/getAuth")
	public String getAuth( HttpSession session, HttpServletRequest request) {
		long currentTime  = System.currentTimeMillis()/1000;
		String ts = String.valueOf(currentTime);
		//sign=MD5($appID+$ts+$token+false)
		String sign = Md5Utils.MD5(COLOR_APP_ID+ts+COLOR_TOKEN+"false");
		String openId = request.getParameter("openID");
		String token =request.getParameter("accessToken");
		System.out.println("ts="+ts+";sign="+sign+";");
		Map<String,String> params = new HashMap<String,String>(10);
		params.put("openID", openId);
		params.put("accessToken", token);
		
		try {
			StringBuffer url = new StringBuffer();
			url.append(colorLifeAG_URL).append("?sign=").append(sign).append("&ts=").append(ts).append("&appID=").append(COLOR_APP_ID);
			logger.info("发送给彩生活的URL:"+url.toString());
			String result = HttpClientUtils.post(url.toString(), params);
			logger.info("彩生活认证结果:"+result);
			ObjectMapper mapper = new ObjectMapper();
			Map<Object, Object> params1 = mapper.readValue(result.toString(), new TypeReference<HashMap<Object, Object>>() {});
			int code =Integer.valueOf(params1.get("code").toString());
			if(code==0){
				if(params1.get("content").toString().length()>0){
					Map<Object, Object> contentTemp=(Map<Object, Object>) params1.get("content");
//				UserAccount user = new UserAccount();
//				user.setUsername(contentTemp.get("username")==null?"":contentTemp.get("username").toString());
//				
//				//放入session
//				session.setAttribute("user", user);
				//存放第三方用户信息
				LoanChannelUserDo userThird = new LoanChannelUserDo();
				userThird.setChannelId(1L);//用户渠道
				userThird.setUsername(contentTemp.get("username")==null?"":contentTemp.get("username").toString());//用户名
				userThird.setSourceUserId(contentTemp.get("uid")==null?"":contentTemp.get("uid").toString());//用户ID
				userThird.setMobile(contentTemp.get("mobile")==null?"":contentTemp.get("mobile").toString());//手机号
				try {
					if (StringUtils.isNotBlank(contentTemp.get("realName")+"")) {//真实姓名
						userThird.setRealName(URLDecoder.decode(contentTemp.get("realName")+"", "utf-8"));
					}
				} catch (Exception e) {
					e.printStackTrace();
					return "redirect:"+loginUrl+"?fromUrl="+getIndexUrl(request);
				}
				logger.info("----Start:index---------sessionID:" + session.getId() + "---------------");
				session.setAttribute(Constants.USER_THIRD, userThird);
				session.setAttribute("contentTemp", contentTemp);
				//获取用户ID，通过彩生活ID绑定表
				Map<String, Object> param = new HashMap<String, Object>();
				param.put("channelType", userThird.getChannelId());
				param.put("sourceUserId", userThird.getSourceUserId());
				List<LoanUserChannelReDo> loanUserChannelReList=loanUserService.selectLoanUserChannelRe(param);
				Long userId=0L;
				if(loanUserChannelReList!=null&&loanUserChannelReList.size()>0){
					userId=loanUserChannelReList.get(0).getLoanUserId();
				}else{
					/*========查询用户帐号是否已经注册========*/
					param.clear();
					param.put("mobile", userThird.getMobile());
			        List<LoanUserDo> loanUserList = loanUserService.selectLoanUser(param);
			        if(loanUserList!=null&&loanUserList.size() > 0){
			        	LoanUserChannelReDo newLoanUserChannelReDo=new LoanUserChannelReDo();
			        	newLoanUserChannelReDo.setChannelType(userThird.getChannelId());
			        	newLoanUserChannelReDo.setSourceUserId(userThird.getSourceUserId());
			        	newLoanUserChannelReDo.setLoanUserId(loanUserList.get(0).getId());
			        	loanUserService.saveLoanUserChannelRe(newLoanUserChannelReDo);
			        	userId=loanUserList.get(0).getId();
			        	logger.info("用户帐号"+userThird.getMobile()+"...维护渠道信息成功...");
			        }else{
			        	/*========自动注册========*/
			        	LoanUserDo newLoanUserDo=new LoanUserDo();
						newLoanUserDo.setMobile(userThird.getMobile());
			            
			            String pwdMd5 = DigestUtils.md5Hex(userThird.getMobile() + Constants.PASS_KEY);//加密
						newLoanUserDo.setPassword(pwdMd5);
						newLoanUserDo.setStatus("T");//状态(T有效，F无效，D删除)
			            
			            Date date = new Date();
			            newLoanUserDo.setCreateTime(date);//创建时间别忘记加
			            newLoanUserDo.setUpdateTime(date);//修改时间建议默认是创建时间，方便日后比较
			            IResult<?> userDo =loanUserService.addLoanUser(newLoanUserDo, null);
			            logger.info("用户帐号"+userThird.getMobile()+"注册成功...");
			            userId=(Long)userDo.getModel();
			            if(userId.longValue()>0){
			            	LoanUserChannelReDo newLoanUserChannelReDo=new LoanUserChannelReDo();
			            	newLoanUserChannelReDo.setChannelType(userThird.getChannelId());
				        	newLoanUserChannelReDo.setSourceUserId(userThird.getSourceUserId());
				        	newLoanUserChannelReDo.setLoanUserId(userId);
				        	loanUserService.saveLoanUserChannelRe(newLoanUserChannelReDo);
				        	logger.info("用户帐号"+userThird.getMobile()+"...维护渠道信息成功...");
			            }
			        }
			        
				}
				//组装查询参数
		    	LoanUserDo loanUser = loanUserService.getById(userId);
		    	if(loanUser != null && loanUser.getId().intValue() > 0){
					LoanUserPersonDo loanPersonDo = null; 
					IResult<LoanUserPersonDo> personResult = loanUserPersonService.selectLoanUserPersonByUserId(loanUser.getId());
					if(personResult.isSuccess()){
						loanPersonDo = personResult.getModel();
					}
		    		//登录成功存放用户信息
		    		UserAccount userAccount = new UserAccount();
		    		userAccount.setId(loanUser.getId());
		    		String mobile =loanUser.getMobile()!=null?loanUser.getMobile():"";
		    		userAccount.setMobile(mobile);
		    		userAccount.setUsername(loanUser.getUsername()!=null?loanUser.getUsername():"");
		    		userAccount.setEmail(loanUser.getEmail()!=null?loanUser.getEmail():"");
		    		userAccount.setUserVip(loanUser.getUserLevel());
		        	userAccount.setChannel(userThird.getChannelId()!=null?userThird.getChannelId()+"":"");
		        	if(loanPersonDo!=null){
		        		if(loanPersonDo.getIdNo()!=null){
		        			userAccount.setIdNo(loanPersonDo.getIdNo());
		        		}
		        		if(loanPersonDo.getRealName()!=null){
		        			userAccount.setRealName(loanPersonDo.getRealName());
		        		}
		        	}
		        	if(userAccount.getUsername()!=null&&userAccount.getUsername().trim().length()>0){
		        		userAccount.setUserShow(userAccount.getUsername());
		        	}else{
		        		userAccount.setUserShow(mobile.substring(0, 4) + "****" + mobile.substring(mobile.length() - 4));
		        	}
		    		session.setAttribute(Constants.USER_ACCOUNT, userAccount);
		    		logger.info("sessionId:======================"+session.getId());
		    		logger.info(session.getAttribute(Constants.USER_ACCOUNT));
		    	}}else{
		    		return "redirect:"+loginUrl+"?fromUrl="+getIndexUrl(request);
//					session.setAttribute(WebConstants.MESSAGE_KEY,"财管家数据异常");
//					return "common/notify_message";
				}
			}else{
				return "redirect:"+loginUrl+"?fromUrl="+getIndexUrl(request);
//				String message =params1.get("message").toString();
//				session.setAttribute(WebConstants.MESSAGE_KEY,message);
//				return "common/notify_message";
			}
		} catch (Exception e) {
			e.printStackTrace();
			return "redirect:"+loginUrl+"?fromUrl="+getIndexUrl(request);
		}
		return "app/group/index";
	}
	
	/**
	 * 获取微信登录认证
	 * @param session
	 * @param request
	 * @return
	 */
	@RequestMapping("/getWeixin")
	public String getWeixin(String userId, String productCode ,String mobile,  String sign,HttpSession session, HttpServletRequest request) {
		return "redirect:"+loginUrl+"?fromUrl="+getIndexUrl(request);
//		logger.info("-------------------getWeixin----------------------");
//		String today=new SimpleDateFormat("yyyyMMdd").format(new Date());
//		String token =request.getParameter("realName");
//		if(!(userId!=null && userId.length()>0)){
//			session.setAttribute(WebConstants.MESSAGE_KEY,"微信登录数据异常");
//			return "common/notify_message";
//		}
//		String localSign = Md5Utils.MD5(userId+today+mobile+"#$2$0#@1D@5#@0(4%%0^&1!@");
//		System.out.println("localSigns="+localSign+";sign="+sign+";");
//		try {
//			if(!localSign.equals(sign)){
////				if(productCode.equals("D03")){
////					return "redirect:/app/elend/welcomeWeixin?userId="+Long.valueOf(userId);
////				}
//				//组装查询参数
//		    	LoanUserDo loanUser = loanUserService.getById(Long.valueOf(userId));
//		    	if(loanUser != null && loanUser.getId().intValue() > 0){
//		    		LoanUserPersonDo loanPersonDo = null; 
//					IResult<LoanUserPersonDo> personResult = loanUserPersonService.selectLoanUserPersonByUserId(loanUser.getId());
//					if(personResult.isSuccess()){
//						loanPersonDo = personResult.getModel();
//					}
//		    		//登录成功存放用户信息
//		    		UserAccount userAccount = new UserAccount();
//		    		userAccount.setId(loanUser.getId());
//		    		userAccount.setMobile(loanUser.getMobile());
//		    		userAccount.setUsername(loanUser.getUsername());
//		    		userAccount.setEmail(loanUser.getEmail());
//		    		userAccount.setUserVip(loanUser.getUserLevel());
//		        	userAccount.setChannel(80+"");
//		        	if(loanPersonDo!=null){
//		        		if(loanPersonDo.getIdNo()!=null){
//		        			userAccount.setIdNo(loanPersonDo.getIdNo());
//		        		}
//		        		if(loanPersonDo.getRealName()!=null){
//		        			userAccount.setRealName(loanPersonDo.getRealName());
//		        		}
//		        	}
//		        	if(userAccount.getUsername()!=null&&userAccount.getUsername().trim().length()>0){
//		        		userAccount.setUserShow(userAccount.getUsername());
//		        	}else{
//		        		userAccount.setUserShow(mobile.substring(0, 4) + "****" + mobile.substring(mobile.length() - 4));
//		        	}
//		    		session.setAttribute(Constants.USER_ACCOUNT, userAccount);
//		    		logger.info("sessionId:======================"+session.getId());
//		    		logger.info(session.getAttribute(Constants.USER_ACCOUNT));
//		    		if(productCode.equals("D01")){
//						return "app/group/index";
//					}else{
//						session.setAttribute(WebConstants.MESSAGE_KEY,"请求参数错误，没有对应产品，请联系客服 400 303 737");
//						return "common/notify_message";
//					}
//				}else{
//					session.setAttribute(WebConstants.MESSAGE_KEY,"微信登录超时异常");
//					return "common/notify_message";
//				}
//			}
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
//		session.setAttribute(WebConstants.MESSAGE_KEY,"微信登录超时异常");
//		return "common/notify_message";
	}
	

	@RequestMapping(value = "/welcome", method = RequestMethod.GET)
	public String goElendWelcome() {
		return "app/group/index";
	}
	/**
	 * @Description 判断身份证是否已被注册使用
	 * @author huangzl QQ: 272950754
	 * @date 2015年12月9日 下午5:28:41
	 * @param idNo
	 * @param session
	 * @return
	 */
	@RequestMapping(value = "/selectIdNo", method = RequestMethod.GET)
	@ResponseBody
	public IResult<?> selectIdNo(String idNo,HttpSession session) {
		Map<String, Object> parameterMap = new HashMap<String, Object>();
		parameterMap.put("idNo", idNo);
		UserAccount userAccoun = (UserAccount) session.getAttribute(Constants.USER_ACCOUNT);
		List<LoanUserPersonDo> temp=loanUserPersonService.selectLoanUserPerson(parameterMap);
		if(temp!=null&&temp.size()>0){
			LoanUserPersonDo loanPerson=temp.get(0);
			if(loanPerson.getUserId().intValue()!=userAccoun.getId().intValue()){
					return  ResultSupport.buildResult("1", "该身份证已经被注册使用!");
			}
		}
		IResult<Object> result = new ResultSupport("0","没有重复的身份证！");
		result.setModel(idNo);
		return result;
	}
	/**
	 * 个人信息填写页面
	 * 
	 * @param userId
	 * @param loanId
	 * @param model
	 * @param session
	 * @return
	 * @author: huangzl
	 * @date: 2015年6月8日 19:13:42
	 */
	@RequestMapping(value = "/personalInfo", method = RequestMethod.GET)
	public String saveLoanPerson(Model model, HttpSession session, HttpServletRequest request) {
		session.removeAttribute("loanId");
		session.removeAttribute("loanDo");
		UserAccount user =  (UserAccount) session.getAttribute(Constants.USER_ACCOUNT);
		if (user != null) {
			Map<String, Object> param = new HashMap<String, Object>();
	    	param.put("userId",user.getId());
	    	List<LoanOrderDo> loanOrderList = orderService.queryOrderProcessing(param);
			if (loanOrderList != null && loanOrderList.size() > 0) { // 有订单
				LoanOrderDo loanOrderDo = loanOrderList.get(0);
				Map<String, Object> paramProd = new HashMap<String, Object>();
		    	paramProd.put("code", "D01");
				List<LoanProductDo> tempProductDo=loanProductService.selectLoanProduct(paramProd);
				if(loanOrderDo.getProdId()!=null&&loanOrderDo.getProdId().intValue()==tempProductDo.get(0).getId().intValue()){
					/**
					 * 以下是不允许申请和修改的状态： AUDITED-已审核，TREATY-已签约，SUBJECTED-待放款,REPAYING还款中
					 */
					String[] loanStatus1 = { "AUDITED", "TREATY", "SUBJECTED", "REPAYING", "PROCESSING", "PENDING", "DRAFT" };
					for (String loanStatus : loanStatus1) {
						if (loanOrderDo.getLoanStatus().toString().equals(loanStatus)) {
							return "redirect:"+personalCenter;
//							model.addAttribute(WebConstants.MESSAGE_KEY,"已有正在申请的订单，不能申请，如有问题，请联系客服  4008-303-737!");
//							return "common/notify_message";
						}
					}
				}else{
					return "redirect:"+personalCenter;
				}
			}
			//同步最新的用户信息
	       	 IResult<LoanUserPersonDo> temp =loanUserPersonService.selectLoanUserPersonByUserId(user.getId());
	       	 if(temp.isSuccess()){
	    			LoanUserPersonDo loanPerson = temp.getModel();
	    			if(loanPerson.getRealName()!=null&&loanPerson.getRealName().trim().length()>0){
	    				user.setRealName(loanPerson.getRealName());
	    			}
	    			if(loanPerson.getIdNo()!=null&&loanPerson.getIdNo().trim().length()>0){
	    				user.setIdNo(loanPerson.getIdNo());
	    			}
	    			session.setAttribute(Constants.USER_ACCOUNT, user);
	    		}
		}else{
			return "redirect:"+loginUrl+"?fromUrl="+getIndexUrl(request);
		}
		return "app/group/person_update";
	}

	/**
	 * 个人信息填写页面
	 * 
	 * @param userId
	 * @param loanPersonDo
	 * @param model
	 * @return
	 * @author: huangzl
	 * @date: 2015年6月8日 19:13:34
	 */
	@RequestMapping(value = "/personalInfo", method = RequestMethod.POST)
	public String saveLoanPerson(@ModelAttribute LoanUserPersonDo loanPersonDo, @RequestParam String company,@RequestParam String dept,Model model, HttpServletRequest request,HttpSession session) {
		UserAccount user =  (UserAccount) session.getAttribute(Constants.USER_ACCOUNT);
		loanPersonDo.setAge(IdCardUtils.getAgeByIdCard(loanPersonDo.getIdNo()));
		JobDo jobDo =new JobDo();
		jobDo.setCompanyName(company+dept);
		jobDo.setPosition("3");
		jobDo.setJobIncome(Double.valueOf(request.getParameter("jobIncome")));
		jobDo.setJobYear(0);
		jobDo.setCompanyPhone("未知");
		jobDo.setJobType(JobType.SALARYMAN);
		loanPersonDo.setUserType(JobType.SALARYMAN.name());
		LoanOrderDo loanDo=new LoanOrderDo();
		loanDo.setLoanPersonDo(loanPersonDo);
		loanDo.setJobDo(jobDo);
		session.setAttribute("loanDo", loanDo);
		Map<String, Object> param = new HashMap<String, Object>();
    	param.put("userId",user.getId());
		List<LoanUserPersonDo> loanPersonList=loanUserPersonService.selectLoanUserPerson(param);
		if(loanPersonList!=null&&loanPersonList.size()>0){
			model.addAttribute("loanPersonDo", loanPersonList.get(0));
		}
		return "app/group/loan_update";
	}

	/**
	 * 计算结算明细
	 * 
	 * @param loanAmount
	 * @param annualRateArr
	 *            月利率 逗号隔开
	 * @param loanPeriod
	 * @param schemeIdArr
	 *            还款方式 逗号隔开
	 * @return
	 * @author: huangzl
	 * @date: 2015年6月8日 19:13:23
	 */
	@RequestMapping(value = "/calSettDetail", method = RequestMethod.GET)
	@ResponseBody
	public IResult<?> calSettDetail(Double loanAmount, Integer loanPeriod) {
		if (loanAmount == null || CalculateUtils.le(loanAmount, 0d)) {
			return ResultSupport.buildResult("1", "借款金额有误!");
		}
		String loanAmoutString = new DecimalFormat("###0.#").format(loanAmount);
		if (Long.valueOf(loanAmoutString) % 100 != 0) {
			return ResultSupport.buildResult("1", "请确认金额为100的整数倍！");
		}
		if (loanAmoutString.length() > 11) {
			return ResultSupport.buildResult("1", "输入金额不能超过999亿！");
		}
		if (loanPeriod == null || loanPeriod.intValue() <= 0) {
			return ResultSupport.buildResult("1", "借款期限有误!");
		}

		DecimalFormat df = new DecimalFormat("##0.00");
		List<List<String>> repayAmountAllList = new ArrayList<List<String>>();
		String settleWay = "";
		Double annualRate = 0.0;
		Map<String, Object> param = new HashMap<String, Object>();
		param.put("code", "D01");
		List<LoanProductDo> tempProductDo = loanProductService.selectLoanProduct(param);
		if (tempProductDo != null && tempProductDo.size() > 0) {
			param.put("prodId", tempProductDo.get(0).getId());// 产品ID
			param.remove("code");
			// 设置年利率和schemeId
			List<LoanProductSchemeDo> productSchemeList = loanProductSchemeService.selectLoanProductScheme(param);
			if (productSchemeList != null && productSchemeList.size() > 0) {
				LoanProductSchemeDo tempScheme = productSchemeList.get(0);
				annualRate = tempScheme.getDefaultAnnualRate().doubleValue();
				settleWay = tempScheme.getRepayWay();
			} else {
				return ResultSupport.buildResult("1", "产品方案正在建设中!");
			}
		} else {
			return ResultSupport.buildResult("1", "产品正在建设中!");
		}

		List<SettDetailDo> settDetailDoList = settleCalculatorService.calSettDetailForRepayPlanShow(loanAmount, annualRate, loanPeriod, settleWay, new Date());
		List<String> repayAmountList = new ArrayList<String>();
		System.out.println("===================================start repay scheme ============================================");
		for (int k = 0; k < settDetailDoList.size(); k++) {
			SettDetailDo settDetailDo = settDetailDoList.get(k);
			System.out.println(settDetailDo);
			Double repayAmount = CalculateUtils.add(
					CalculateUtils.add(CalculateUtils.add(settDetailDo.getPrincipal(), settDetailDo.getInterest()), settDetailDo.getServFee() == null ? 0 : settDetailDo.getServFee()),
					settDetailDo.getConsultFee() == null ? 0 : settDetailDo.getConsultFee());
			repayAmountList.add(df.format(repayAmount));
		}
		System.out.println("===================================end repay scheme ============================================");
		repayAmountAllList.add(repayAmountList);

		IResult<Object> result = new ResultSupport("0", "计算成功！");
		result.setModel(repayAmountAllList);
		return result;
	}
	/**
	 * 计算授信金额
	 * 
	 * @return
	 * @author: huangzlmf
	 * @date: 2015年4月21日 12:49:05
	 */
	@RequestMapping("/calCreditAmountGroup")
	@ResponseBody
	public Map<String, Object> calCreditAmountGroup(String income, String loanPeriod) {
		logger.info("----Start:calCreditAmountGroup;income=" + income + ";loanPeriod=" + loanPeriod + ";");
		Map<String, Object> map = new HashMap<String, Object>();
		// 如果月收入或借款期限为空，则返回错误信息
		if (StringUtils.isBlank(income) || StringUtils.isBlank(loanPeriod)) {
			map.put("error", true);
			map.put("message", "请求非法!");
			logger.info("----End:calCreditAmountGroup;income=" + income + ";loanPeriod=" + loanPeriod + ";message=请求非法!");
			return map;
		}
		double incomeD = Double.parseDouble(income);
		int loanPeriodI = Integer.parseInt(loanPeriod);
		loanPeriodI = (loanPeriodI < 6 ? 3 : 6);//
		// 借款期限小于6个月，则按3个月计算，大于等于6个月，则按6个月计算
		int index = 1;
		if (CalculateUtils.le(incomeD, 3000)) {
			index = 1;
		} else if (CalculateUtils.le(incomeD, 5000)) {
			index = 2;
		} else if (CalculateUtils.le(incomeD, 8000)) {
			index = 3;
		} else if (CalculateUtils.le(incomeD, 10000)) {
			index = 4;
		} else if (CalculateUtils.gt(incomeD, 10000)) {
			index = 5;
		}
		index = (loanPeriodI == 6 ? (index + 5) : index);
		double creditAmount = CalculateUtils.round(CalculateUtils.mul(incomeD, creditRateMap.get(index)), 0);
		// 借款期限为6个月，最高可借贷100000元
		if (loanPeriodI == 6 && CalculateUtils.gt(creditAmount, 100000)) {
			creditAmount = 100000;
		}
		// 借款期限为3个月，最高可借贷70000元
		if (loanPeriodI == 3 && CalculateUtils.gt(creditAmount, 70000)) {
			creditAmount = 70000;
		}
		map.put("error", false);
		map.put("creditAmount", creditAmount);
		logger.info("----End:calCreditAmountGroup;income=" + income + ";loanPeriod=" + loanPeriod + ";");
		return map;
	}
	
	
	/**
	 * 借款申请页面
	 * 
	 * @param loanDo
	 * @param model
	 * @return
	 * @author: huangzl
	 * @date: 2015年6月8日 19:13:12
	 */
	@RequestMapping(value = "/applyfor", method = RequestMethod.POST)
	public String saveLoanDetail(@ModelAttribute LoanOrderDo loanDo, Model model, HttpServletRequest request, HttpSession session) {
//		public String saveLoanDetail(@ModelAttribute LoanOrderDo loanDo, @RequestParam String hasHouse, Model model, HttpServletRequest request, HttpSession session) {
		if (loanDo.getApplyAmount() == null || CalculateUtils.le(loanDo.getApplyAmount(), 0d)) {
			model.addAttribute("resultCode", "money");
			model.addAttribute(WebConstants.MESSAGE_KEY, "借款金额有误！");
			return "app/group/loan_update";
		}
		String loanAmoutString = new DecimalFormat("###0.#").format(loanDo.getApplyAmount());
		if (Long.valueOf(loanAmoutString) % 100 != 0) {
			model.addAttribute(WebConstants.MESSAGE_KEY, "请确认金额为100的整数倍！");
			model.addAttribute("resultCode", "money");
			return "app/group/loan_update";
		}
		if (loanAmoutString.length() > 11) {
			model.addAttribute(WebConstants.MESSAGE_KEY, "输入金额不能超过999亿！");
			model.addAttribute("resultCode", "money");
			return "app/group/loan_update";
		}
		// 借款期限为小于6个月，最高可借贷70000元
		if (loanDo.getLoanPeriod()  <6 && CalculateUtils.gt(loanDo.getApplyAmount(), 70000)) {
			model.addAttribute(WebConstants.MESSAGE_KEY, "，最高可借贷70000元");
			model.addAttribute("resultCode", "money");
			return "app/group/loan_update";
		}
		// 借款期限为大于等于6个月，最高可借贷100000元
		if (loanDo.getLoanPeriod() >= 6 && CalculateUtils.gt(loanDo.getApplyAmount(), 100000)) {
			model.addAttribute(WebConstants.MESSAGE_KEY, "最高可借贷100000元");
			model.addAttribute("resultCode", "money");
			return "app/group/loan_update";
		}
		//保存个人信息
		LoanOrderDo tempDo = (LoanOrderDo) session.getAttribute("loanDo");
		LoanUserPersonDo loanPersonDo = tempDo.getLoanPersonDo();
		JobDo jobDo = tempDo.getJobDo();
//		loanPersonDo.setHasHourse(hasHouse);
		String idNo=loanPersonDo.getIdNo();
		loanPersonDo.setAddress(loanDo.getLoanPersonDo().getAddress());
		loanPersonDo.setAddressDetail(loanDo.getLoanPersonDo().getAddressDetail());
		loanPersonDo.setAge(IdCardUtils.getAgeByIdCard(idNo));
		loanPersonDo.setSex(IdCardUtils.getGenderByIdCard(idNo));
		UserAccount user =  (UserAccount) session.getAttribute(Constants.USER_ACCOUNT);
		if (null!=user && null!=user.getId()&&user.getId().intValue()>0) {
			loanDo.setUserId(user.getId());
			loanPersonDo.setUserId(user.getId());
			jobDo.setUserId(user.getId());
		}else{
			session.setAttribute(WebConstants.MESSAGE_KEY,"登录超时异常");
			return "common/notify_message";
		}
		loanDo.setLoanType(1);
		loanDo.setLoanUsage("集团贷");
		
		// 初始化 产品code 订单号
		loanDo.setProductCode("D01");
		loanDo.setOrderCode(generatorCodeService.generateOrderCode("D01"));
		Map<String, Object> param = new HashMap<String, Object>();
		param.put("code", "D01");
		List<LoanProductDo> tempProductDo=loanProductService.selectLoanProduct(param);
		if(tempProductDo!=null&&tempProductDo.size()>0){
			loanDo.setProdId(tempProductDo.get(0).getId());
			param.put("prodId", tempProductDo.get(0).getId());//产品ID
			param.remove("code");
			//设置年利率和schemeId
	    	List<LoanProductSchemeDo> productSchemeList = loanProductSchemeService.selectLoanProductScheme(param);
	    	if(productSchemeList!=null&&productSchemeList.size()>0){
	    		LoanProductSchemeDo tempScheme=productSchemeList.get(0);
	    		loanDo.setAnnualRate(tempScheme.getDefaultAnnualRate().doubleValue());
		    	loanDo.setSchemeId(tempScheme.getId());
	    	}
		}
		LoanChannelUserDo userThird = (LoanChannelUserDo) session.getAttribute(Constants.USER_THIRD);
		//渠道
		if (userThird!=null&&userThird.getChannelId()!=null&&userThird.getChannelId().intValue()>0) {
			loanDo.setChannelId(userThird.getChannelId());//彩生活
		}
		loanDo.setLoanStatus(LoanStatus.PENDING);
		loanDo.setProcessCurrentStep(LoanOrderDo.ProcessStep.TO_EDIT);
		loanDo.setProcessNextStep(LoanOrderDo.ProcessStep.TO_EDIT);
		
		loanDo.setLoanPersonDo(loanPersonDo);
		loanDo.setJobDo(jobDo);
//		List<LoanRelationDo> temp =new ArrayList<LoanRelationDo>();
		
		loanDo.getLoanRelationDoList().get(0).setUserId(user.getId());
		loanDo.getLoanRelationDoList().get(0).setRelationship("2");//同事关系
		loanDo.getLoanRelationDoList().get(0).setRelationType(2);//关系类型，朋友
		loanDo.getLoanRelationDoList().get(0).setCreateTime(new Date());
		loanDo.getLoanRelationDoList().get(0).setUpdateTime(new Date());
		loanDo.getLoanRelationDoList().get(1).setUserId(user.getId());
		loanDo.getLoanRelationDoList().get(1).setRelationship("1");//直系亲属
		loanDo.getLoanRelationDoList().get(1).setRelationType(1);//关系类型，亲戚
		loanDo.getLoanRelationDoList().get(1).setCreateTime(new Date());
		loanDo.getLoanRelationDoList().get(1).setUpdateTime(new Date());

		
		Long loanId = orderService.addLoanInfo(loanDo);
		
	
		if (loanId != null && loanId.longValue() > 0) {
			session.setAttribute("loanDo", loanDo);
			model.addAttribute("loanPersonDo", loanPersonDo);
			// 发送短信通知
			sendSMS(model, session);
			return "redirect:/app/group/showDocumentList";  

		} else {
			model.addAttribute(WebConstants.MESSAGE_KEY, "系统异常，请稍后再试!");
			return "app/group/loan_update";
		}
	}

	/**
	 * 发送短信
	 */
	private void sendSMS(Model model, HttpSession session){
    	
//		LoanOrderDo tempDo = (LoanOrderDo) session.getAttribute("loanDo");
//		LoanUserPersonDo  loanPersonDo = tempDo.getLoanPersonDo();
    	//给贷 款人的短信内容
//    	String smsStr="尊敬的集团贷 用户，您申请的贷  款信息已经提交，集团贷 将尽快与您取得联系，谢谢您的配合。如有需要，请联系客服：4008303737";
//    	NotifyDo nd = new SMSNotifyDo(smsStr,loanPersonDo.getMobile(),"mail_template_default.ftl");
//    	notifyService.send(nd);
    	
    	
    	//hehenian
//    	smsStr="新的集团贷 ：贷  款人:"+loanPersonDo.getRealName()+",手机号码:"+loanPersonDo.getMobile()+",申请贷  款金额:"+tempDo.getApplyAmountString()+"元";
//    	nd = new SMSNotifyDo(smsStr,"15019238715","mail_template_default.ftl");
//    	notifyService.send(nd);
    	
    }	


	/**
	 * 借款申请成功页面
	 * 
	 * @param loanId
	 * @param model
	 * @return
	 * @author: huangzl
	 * @date: 2015年6月8日 19:12:58
	 */
	@RequestMapping(value = "/showDocumentList", method = RequestMethod.GET)
	public String showDocumentList(HttpServletRequest request) {
		LoanUserPersonDo loanPersonDo = (LoanUserPersonDo) request.getSession().getAttribute("loanPersonDo");
		request.setAttribute("loanPersonDo", loanPersonDo);
		request.setAttribute("personalCenter", personalCenter);
		return "app/group/success";
	}


	private static Map<Integer, Double> creditRateMap = new HashMap<Integer, Double>();
	static {
		// 借款期限3个月授信比例
		creditRateMap.put(1, 1.5);
		creditRateMap.put(2, 2.0);
		creditRateMap.put(3, 2.5);
		creditRateMap.put(4, 3.0);
		creditRateMap.put(5, 3.5);
		// 借款期限6个月授信比例
		creditRateMap.put(6, 3.0);
		creditRateMap.put(7, 3.5);
		creditRateMap.put(8, 4.0);
		creditRateMap.put(9, 4.5);
		creditRateMap.put(10, 5.0);
	}
	
}
