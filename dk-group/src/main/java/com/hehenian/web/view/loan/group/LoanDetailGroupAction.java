/**  
 * @Project: hehenian-web
 * @Package com.hehenian.web.view.loan.action
 * @Title: LoanDetailAction.java
 * @Description: TODO
 * @author: liuzgmf
 * @date 2014年12月11日 上午10:00:04
 * @Copyright: HEHENIAN Co.,Ltd. All rights reserved.
 * @version V1.0  
 */
package com.hehenian.web.view.loan.group;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.time.DateFormatUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
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
import com.hehenian.common.sms.INotifyService;
import com.hehenian.common.result.IResult;
import com.hehenian.common.result.ResultSupport;
import com.hehenian.common.util.CalculateUtils;
import com.hehenian.common.util.IdCardUtils;
import com.hehenian.common.util.StringUtil;
import com.hehenian.model.account.LoanRelationDo;
import com.hehenian.model.account.LoanUserPersonDo;
import com.hehenian.model.account.UserAccount;
import com.hehenian.model.job.JobDo;
import com.hehenian.model.job.JobDo.JobType;
import com.hehenian.model.order.LoanOrderDo;
import com.hehenian.model.order.LoanOrderDo.LoanStatus;
import com.hehenian.model.product.LoanProductDo;
import com.hehenian.model.product.LoanProductSchemeDo;
import com.hehenian.model.repay.SettDetailDo;
import com.hehenian.model.sms.NotifyDo;
import com.hehenian.model.sms.SMSNotifyDo;
import com.hehenian.web.common.constant.WebConstants;

/**
 * @Description 集团贷
 * @author huangzl QQ: 272950754
 * @date 2015年4月20日 下午2:32:14
 * @Project hehenian-lend-web
 * @Package com.hehenian.web.view.loan.action
 * @File LoanDetailGroupAction.java
 */
@Controller
@RequestMapping(value="/web/group")
public class LoanDetailGroupAction {
	private final Logger logger = Logger.getLogger(this.getClass());
	private DecimalFormat df = new DecimalFormat("##0.00");


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
	/**
	 * 
	 * @param request
	 * @return
	 */
	protected String getString(HttpServletRequest request, String name) {
		String paraVal = request.getParameter(name);
		if (StringUtils.isNotBlank(paraVal)) {
			paraVal = StringUtil.FilteSqlInAndScript(paraVal);
		}
		return paraVal;
	}
	/**
	 * 新增 申请 跳转界面
	 * 
	 * @author: huangzlmf
	 * @date: 2015年4月20日 21:09:54
	 * @param param
	 * @param sign
	 * @param map
	 * @return
	 */
	@RequestMapping("/group")
	public String group(HttpSession session) {
//		logger.info("----group----");
//		session.removeAttribute("loanId");
//		session.removeAttribute("loanDo");
//		session.setAttribute("fromUrl", "group.do");
//		UserAccount user =  (UserAccount) session.getAttribute(Constants.USER_ACCOUNT);
//		//同步最新的用户信息
//      	 IResult<LoanUserPersonDo> temp =loanUserPersonService.selectLoanUserPersonByUserId(user.getId());
//      	 if(temp.isSuccess()){
//   			LoanUserPersonDo loanPerson = temp.getModel();
//   			if(loanPerson.getRealName()!=null&&loanPerson.getRealName().trim().length()>0){
//   				user.setRealName(loanPerson.getRealName());
//   			}
//   			if(loanPerson.getIdNo()!=null&&loanPerson.getIdNo().trim().length()>0){
//   				user.setIdNo(loanPerson.getIdNo());
//   			}
//   			session.setAttribute(Constants.USER_ACCOUNT, user);
//   		}
//		return "/web/group/group_apply";
		return "redirect:http://www.hehuayidai.com/web/group/group.do";
	}
	/**
	 * @Description 判断身份证是否已被注册使用
	 * @author huangzl QQ: 272950754
	 * @date 2015年12月9日 下午3:44:18
	 * @param idNo
	 * @return
	 */
	@RequestMapping(value = "/selectIdNo", method = RequestMethod.GET)
	@ResponseBody
	public IResult<?> selectIdNo(String idNo) {
		Map<String, Object> parameterMap = new HashMap<String, Object>();
		parameterMap.put("idNo", idNo);
		List<LoanUserPersonDo> temp=loanUserPersonService.selectLoanUserPerson(parameterMap);
		if(!(temp!=null&&temp.size()>0)){
			IResult<Object> result = new ResultSupport("0","没有重复的身份证！");
			result.setModel(idNo);
			return result;
		}else{
			return  ResultSupport.buildResult("1", "该身份证已经被注册使用!");
		}
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
	 * 计算还款明细金额（本金，利息，还款总额）
	 * 
	 * @return
	 * @author:huangzlmf
	 * @date: 2015年4月21日 15:09:25
	 */
	@RequestMapping("/calRepayDetailGroup")
	@ResponseBody
	public List<Map<String, String>> calRepayDetailGroup(Double loanAmount, Integer loanPeriod) {
		logger.info("----Start:calRepayDetailGroup;applyAmount=" + loanAmount + "; loanPeriod=" + loanPeriod + ";");
//		double annualRate = (loanPeriod == 3 ? 8.4 : 9.0);// 借款期限为3个月为8.4%，6个月为9%
		double annualRate = 0.0;
		String settleWay = "";
		Map<String, Object> param = new HashMap<String, Object>();
		param.put("code", "D01");
		List<LoanProductDo> tempProductDo=loanProductService.selectLoanProduct(param);
		if(tempProductDo!=null&&tempProductDo.size()>0){
			param.put("prodId", tempProductDo.get(0).getId());//产品ID
			param.remove("code");
			//设置年利率和schemeId
	    	List<LoanProductSchemeDo> productSchemeList = loanProductSchemeService.selectLoanProductScheme(param);
	    	if(productSchemeList!=null&&productSchemeList.size()>0){
	    		LoanProductSchemeDo tempScheme=productSchemeList.get(0);
	    		annualRate=tempScheme.getDefaultAnnualRate().doubleValue();
	    		settleWay=tempScheme.getRepayWay();
	    	}
		}
//		loanPeriod = (loanPeriod < 6 ? 3 : 6);// 借款期限小于6个月，则按3个月计算，大于等于6个月，则按6个月计算
		List<SettDetailDo> settDetailDoList = settleCalculatorService.calSettDetailForRepayPlanShow(loanAmount, annualRate, loanPeriod, settleWay,new Date());
		List<Map<String, String>> repayDetailList = new ArrayList<Map<String, String>>(settDetailDoList.size());
		double totalAmount = 0.00;
		for (SettDetailDo detailDo : settDetailDoList) {
			Map<String, String> map = new LinkedHashMap<String, String>();
			map.put("times", detailDo.getPeriod() + "");
			map.put("principal", df.format(detailDo.getPrincipal()));
			// double
			// interest1=CalculateUtils.add(CalculateUtils.add(detailDo.getInterest(),detailDo.getConsultFee()),detailDo.getServFee());
			double interest = detailDo.getInterest();
			map.put("interest", df.format(interest));
			double repayAmount = CalculateUtils.add(interest, detailDo.getPrincipal());
			map.put("repayAmount", df.format(repayAmount));
			totalAmount = CalculateUtils.add(totalAmount, repayAmount);
			map.put("totalAmount", df.format(totalAmount));
			repayDetailList.add(map);
		}
		logger.info("----End:calRepayDetailGroup;applyAmount=" + loanAmount + "; loanPeriod=" + loanPeriod + ";");
		return repayDetailList;
	}

	/**
	 * 短信发送人
	 * 
	 * @return
	 * @author: huangzlmf
	 * @date: 2015年4月20日 21:09:54
	 */
	private String getMobileArr(String userMobile) {
		logger.info("----getMobileArr----");
		StringBuffer sb = new StringBuffer();
		sb.append("15019238715");
		return sb.toString();
	}

	/**
	 * 新增借款申请信息
	 * 
	 * @return
	 * @author: huangzlmf
	 * @date: 2015年4月21日 21:09:54
	 */
	@RequestMapping(value = "/addLoanDetailGroup", method = RequestMethod.POST)
	public String addLoanDetailGroup(LoanOrderDo loanDetailDo, String idNo, ModelMap map,HttpSession session,HttpServletRequest request) {
		logger.info("----Start:addLoanDetailGroup;loanDetailDo=" + loanDetailDo + ";");
		UserAccount user =  (UserAccount) session.getAttribute(Constants.USER_ACCOUNT);
		Map<String, Object> param = new HashMap<String, Object>();
    	param.put("userId",user.getId());
    	Map<String, Object> paramProd = new HashMap<String, Object>();
    	paramProd.put("code", "D01");
		List<LoanProductDo> tempProductDo=loanProductService.selectLoanProduct(paramProd);
    	List<LoanOrderDo> loanOrderList = orderService.queryOrderProcessing(param);
		if (loanOrderList != null && loanOrderList.size() > 0) { // 有订单
			LoanOrderDo loanOrderDo = loanOrderList.get(0);
			if(loanOrderDo.getProdId()!=null&&loanOrderDo.getProdId().intValue()==tempProductDo.get(0).getId().intValue()){
				/**
				 * 以下是不允许申请和修改的状态： AUDITED-已审核，TREATY-已签约，SUBJECTED-待放款,REPAYING还款中
				 */
				String[] loanStatus1 = { "AUDITED", "TREATY", "SUBJECTED", "REPAYING", "PROCESSING", "PENDING", "DRAFT" };
				for (String loanStatus : loanStatus1) {
					if (loanOrderDo.getLoanStatus().toString().equals(loanStatus)) {
						session.setAttribute(WebConstants.MESSAGE_KEY,"已有正在申请的订单，不能申请，如有问题，请联系客服 4008-303-737!");
						return "common/notify_message";
					}
				}
			}else{
				session.setAttribute(WebConstants.MESSAGE_KEY,"已有正在申请的订单，不能申请，如有问题，请联系客服 4008-303-737!");
				return "common/notify_message";
			}
		}
		String isSuccess ="F";
		String mobile =loanDetailDo.getMobile();
		String realName=loanDetailDo.getLoanPersonDo().getRealName();
		String remarkTemp=loanDetailDo.getRemark()==null?"":loanDetailDo.getRemark().trim();
	 	String groupName="";
		if (remarkTemp != null && remarkTemp.length() > 0) {
			String[] caddressArray = remarkTemp.split(":");
			if (caddressArray != null && caddressArray.length >= 2) {
				groupName=caddressArray[0].trim();
			}
		}
		LoanOrderDo loanDo = loanDetailDo;
		LoanUserPersonDo loanPersonDo = loanDo.getLoanPersonDo();
		JobDo jobDo = loanDo.getJobDo();
		if (null!=user && null!=user.getId()&&user.getId().intValue()>0) {
			loanDo.setUserId(user.getId());
			loanPersonDo.setUserId(user.getId());
			jobDo.setUserId(user.getId());
		}else{
			session.setAttribute(WebConstants.MESSAGE_KEY,"登录超时异常");
			return "common/notify_message";
		}
		int loanPeriod = loanDetailDo.getLoanPeriod();
		loanDo.setLoanPeriod(loanPeriod);
		loanDo.setApplyAmount(loanDetailDo.getLoanAmount());
		loanDo.setLoanType(1);
		//渠道
		loanDo.setChannelId(1L);//彩生活
		loanDo.setLoanStatus(LoanStatus.PENDING);
		loanDo.setProcessCurrentStep(LoanOrderDo.ProcessStep.TO_EDIT);
		loanDo.setProcessNextStep(LoanOrderDo.ProcessStep.TO_EDIT);
		
		loanDo.setLoanUsage("集团贷");
		loanDo.setChannelId(getGroupNo(groupName));
		loanDo.setAnnualRate(21.0);
		//2015-05-26 集团贷统一基准利率是18/年
		// 初始化 产品code 订单号
		loanDo.setProductCode("D01");
		loanDo.setOrderCode(generatorCodeService.generateOrderCode("D01"));
		param.clear();
		param.put("code", "D01");
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
				
//		loanPersonDo.setRealName(realName);
		loanPersonDo.setIdNo(idNo);
		loanPersonDo.setMobile(mobile);
		loanPersonDo.setAge(IdCardUtils.getAgeByIdCard(idNo));
		loanPersonDo.setSex(IdCardUtils.getGenderByIdCard(idNo));
	
		jobDo.setCompanyName(remarkTemp);
		jobDo.setPosition("3");
		jobDo.setJobYear(0);
		jobDo.setCompanyPhone("未知");
		jobDo.setJobType(JobType.SALARYMAN);
		
		loanDo.setJobDo(jobDo);
		loanDo.setLoanPersonDo(loanPersonDo);
		
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

    
    	    		    		

		Long res = orderService.addLoanInfo(loanDo);
		
		if (res != null && res.longValue() > 0) {
//			String smsTemp = loanDo.getLoanPersonDo().getRealName() + ",手机号码:" + loanDo.getLoanPersonDo().getMobile() + ",申请贷 款金额:" + loanDo.getApplyAmount() + "元,申请时间:" + new SimpleDateFormat("yyyy年MM月dd日").format(new Date()) + "";
//			String smsStr = "集团贷 新的贷 款申请订单：贷 款人:" + smsTemp;
//			NotifyDo nd = new SMSNotifyDo(smsStr, getMobileArr(""), "mail_template_default.ftl");
//			notifyService.send(nd);
//
//			smsStr = "贷 款申请提交成功，正在受理：贷 款人:" + smsTemp;
//			nd = new SMSNotifyDo(smsStr, loanDo.getLoanPersonDo().getMobile(), "mail_template_default.ftl");
//			notifyService.send(nd);
			isSuccess="T";
		} 
		logger.info("----End:addLoanDetailGroup;loanDetailDo=" + loanDetailDo + ";");
		logger.info("----toSuccessGroup;isSuccess="+isSuccess+";----");
		map.put(WebConstants.MESSAGE_KEY,  isSuccess.equals("T")?"恭喜，申请成功!": isSuccess.equals("F")?"系统异常，请稍后再试!":"用户未登录！");
		return "/web/group/group_success";
//		return "redirect:/toSuccessGroup?isSuccess="+isSuccess;
	}

	private Long getGroupNo(String groupName) {
		Long temp =0L;
		if(groupName.equals("地产集团")){
			temp=3L;
		}else if(groupName.equals("文旅集团")){
			temp=2L;
		}else if(groupName.equals("物业国际")){
			temp=4L;
		}else if(groupName.equals("商管公司")){
			temp=10L;
		}else if(groupName.equals("金融集团")){
			temp=8L;
		}else if(groupName.equals("彩生活集团")){
			temp=1L;
		}else if(groupName.equals("福泰年")){
			temp=6L;
		}else if(groupName.equals("花样年教育")){
			temp=7L;
		}else if(groupName.equals("邻里乐")){
			temp=11L;
		}
		return temp;
	}

	/**
	 * 保存成功跳转到成功页面
	 * 
	 * @return
	 * @author:huangzlmf
	 * @date: 2015年4月20日 21:09:25
	 */
	@RequestMapping("/toSuccessGroup")
	public String toSuccessGroup(String isSuccess,ModelMap map) {
		logger.info("----toSuccessGroup;isSuccess="+isSuccess+";----");
		map.put(WebConstants.MESSAGE_KEY,  isSuccess.equals("T")?"恭喜，申请成功!": isSuccess.equals("F")?"系统异常，请稍后再试!":"用户未登录！");
		return "/web/group/group_success";
	}

	/**
	 * 根据身份证号码查询借款申请信息
	 * 
	 * @return
	 * @author: huangzlmf
	 * @date: 2015年4月21日 10:14:18
	 */
	@RequestMapping("/getByIdNoGroup")
	@ResponseBody
	public Map<String, Object> getByIdNoGroup(String idNo) {
		logger.info("----Start:getByIdNoGroup;idNo=" + idNo + ";");
		Map<String, Object> map = new HashMap<String, Object>();
		if (StringUtils.isBlank(idNo)) {
			map.put("error", true);
			map.put("message", "参数有误!");
			logger.info("----End:getByIdNoGroup;idNo=" + idNo + ";message=参数有误!");
			return map;
		}
		List<LoanOrderDo> loanList = orderService.getByUserIdNo(idNo);
		if (!(loanList != null && loanList.size()>0)) {
			map.put("error", true);
			map.put("message", "身份证号码不存在!");
			logger.info("----End:getByIdNoGroup;idNo=" + idNo + ";message=身份证号码不存在!");
			return map;
		}
		LoanOrderDo loanDo = loanList.get(0);
		map.put("error", false);
		map.put("realName", loanDo.getRealName());
		map.put("loanStatus", loanDo.getLoanStatus());
		map.put("applyDate", DateFormatUtils.format(loanDo.getCreateTime(), "yyyy-MM-dd"));
		if (loanDo.getLoanStatus().equals(LoanStatus.SUBJECTED) || loanDo.getLoanStatus().equals(LoanStatus.REPAYING)) {
			map.put("auditDate", DateFormatUtils.format(loanDo.getUpdateTime(), "yyyy-MM-dd"));
		} else {
			map.put("auditDate", "");
		}
		if (loanDo.getLoanStatus().equals(LoanStatus.REPAYING)) {
			map.put("loanDate", DateFormatUtils.format(loanDo.getLoanTime(), "yyyy-MM-dd"));
		} else {
			map.put("loanDate", "");
		}
		logger.info("----End:getByIdNoGroup;idNo=" + idNo + ";");
		return map;
	}
	
	/**
	 * 旧数据表转换到新表
	 * t_loan_detail->t_loan;t_loan_person;t_loan_job
	 * @return
	 * @author: huangzlmf
	 * @date: 22015年4月29日 10:14:18
	 */
	@RequestMapping("/detailC")
	@ResponseBody
	public void save(){
//		Map<String, Object> searchItems = new HashMap<String, Object>();
//		searchItems.put("beginCount", 0);
//        searchItems.put("pageSize", 100000);
//        NPageDo<LoanDetailDo> pageDo=loanDetailService.queryLoanDetails(searchItems);
//        int i =1;
//        String dateOld="";
//        for( LoanDetailDo loanDetailDo:pageDo.getModelList()){
//        	LoanDo loanDo = new LoanDo();
//        	loanDo.setRemark(loanDetailDo.getLoanId().toString());
//        	loanDo.setProductCode("D01");
//        	List<LoanDo> loanList =loanApplyService.selectLoanList(loanDo);
//        	if(null!=loanList && loanList.size()>0) continue;
//    		int loanPeriod = loanDetailDo.getLoanPeriod();
//    		loanDo.setLoanPeriod(loanPeriod);
//    		loanDo.setApplyAmount(loanDetailDo.getLoanAmount());
//    		loanDo.setLoanType(1);
//    		loanDo.setLoanUsage("集团贷");
//    		loanDo.setAnnualRate(loanPeriod == 3 ? 8.4 : 9.0);
//    		//拼装产品订单CODE
//    		String date = new SimpleDateFormat("yyMMdd").format(loanDetailDo.getCreateTime());
//    		if(!dateOld.equals(date)) i=1;
//    		String key = String.valueOf(i++);
//    		while (key.length()<5){
//    			key="0"+key;
//    		}
//            String orderCode = "D01"+date+key;
//    		loanDo.setOrderCode(orderCode);
//    		dateOld=date;
//    		//写状态
//    		if (loanDetailDo.getLoanStatus().equals(LoanStatus.PROCESSING) ) {
//    			loanDo.setLoanStatus(com.hehenian.biz.common.loan.dataobject.LoanDo.LoanStatus.PENDING);
//    		} else if (loanDetailDo.getLoanStatus().equals(LoanStatus.CHECKED)) {
//    			loanDo.setLoanStatus(com.hehenian.biz.common.loan.dataobject.LoanDo.LoanStatus.AUDITED);
//    		} else if (loanDetailDo.getLoanStatus().equals(LoanStatus.UNCHECKED)){
//    			loanDo.setLoanStatus(com.hehenian.biz.common.loan.dataobject.LoanDo.LoanStatus.NOPASS);
//    		}
//    		loanDo.setSchemeId(5l);
//    		loanDo.setCreateTime(loanDetailDo.getCreateTime());
//    		loanDo.setUpdateTime(loanDetailDo.getUpdateTime());
//    		
//    		LoanPersonDo loanPersonDo = new LoanPersonDo();
//    		loanPersonDo.setRealName(loanDetailDo.getRealName());
//    		String idNo = loanDetailDo.getIdNo();
//    		loanPersonDo.setIdNo(idNo);
//    		loanPersonDo.setMobile(loanDetailDo.getMobile());
//    		loanPersonDo.setAge(IdCardUtils.getAgeByIdCard(idNo));
//    		String sexString = IdCardUtils.getGenderByIdCard(idNo);
//    		loanPersonDo.setSex(sexString.equals("M") ? LoanPersonDo.Sex.MALE : LoanPersonDo.Sex.FEMALE);
//
//    		JobDo jobDo = new JobDo();
//    		jobDo.setJobIncome(loanDetailDo.getIncome());
//    		jobDo.setCompanyName("未知");
//    		jobDo.setPosition("3");
//    		jobDo.setJobYear(0);
//    		jobDo.setCompanyPhone("未知");
//    		jobDo.setJobType(JobType.SALARYMAN);
//
//    		loanPersonDo.setJobDo(jobDo);
//    		loanDo.setLoanPersonDo(loanPersonDo);
//
//    		Long res = loanApplyService.saveLoan(loanDo);
//        }
		
		
	}
	

}
