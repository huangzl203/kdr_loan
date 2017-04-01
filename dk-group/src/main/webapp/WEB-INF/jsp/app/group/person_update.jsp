<%@ page language="java" contentType="text/html; charset=utf-8" pageEncoding="utf-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<!DOCTYPE html>
<html>
	<head>
		<%@ include file="include/top.jsp"%>
		<title>借款人个人信息</title>
	</head>
	
	<body>
		<form id="form_update_person" action="<c:url value='personalInfo.do'/>" method="post" >
		<input type="hidden" name="loanId" value="${loanId}"/>
		<div class="eLoan">
           <dl class="loaninfo">
             <dt>
                <label><span>*</span>真实姓名：</label>
               <c:set var="realName" value="${userAccount.realName}"/>
               <c:if test="${realName== null && userThird.realName != null}">
               		<c:set var="realName" value="${userThird.realName}"/>
               </c:if>
               <input type="text" name="realName" <c:if test="${userAccount.realName !=null}"> readonly="readonly" </c:if> value="${realName==null?'':realName}" class="txtinput not_null chinese"/>
             </dt>
             <dd></dd>
             <p class="line"></p>
              <dt>
               <label><span>*</span>手机号码：</label>
               <c:set var="mobile" value="${userAccount.mobile}"/>
               <c:if test="${mobile== null && userThird.mobile != null}">
               		<c:set var="mobile" value="${userThird.mobile}"/>
               </c:if>
               <input type="number" name="mobile" <c:if test="${userAccount.mobile !=null}"> readonly="readonly" </c:if> value="${mobile==null?'':mobile}" class="txtinput not_null tel" />
             </dt>
             <dd></dd>
             <p class="line"></p>
             <dt>
               <label><span>*</span>身份证号：</label>
               <c:set var="idNo" value="${userAccount.idNo}"/>
               <c:if test="${idNo== null && userThird.idNo != null}">
               		<c:set var="idNo" value="${userThird.idNo}"/>
               </c:if>
               <input type="text" id ="idNo" name="idNo" <c:if test="${userAccount.idNo !=null}"> readonly="readonly" </c:if> value="${idNo==null?'':idNo}" class="txtinput not_null identity" />
             </dt>
             <dd></dd>
             <p class="line"></p>
             <dt>
               <label><span>*</span>每月收入：</label>
               <input type="number"  placeholder="请真实填写月收入 "  min="1" name="jobIncome" id="jobIncome" value="" class="txtinput not_null" />
             </dt>
             <dd></dd>
             <p class="line"></p>
             <dt>
               	<label><span>*</span>所在公司：</label>
             		<select name="company" id="company">
						<option value="0" >选择公司</option>
						<option value="地产集团">地产集团</option>
						<option value="文旅集团">文旅集团</option>
						<option value="物业国际">物业国际</option>
						<option value="商管公司">商管公司</option>
						<option value="金融集团">金融集团</option>
						<option value="彩生活集团">彩生活集团</option>
						<option value="福泰年">福泰年</option>
						<option value="花样年教育">花样年教育</option>
						<option value="邻里乐">邻里乐</option>
					</select> 
             </dt>
             <dd></dd>
             <p class="line"></p>
             <dt>
             	<label><span>*</span>所在部门：</label>
               	<input type="text" name="dept" id="dept"  value="" placeholder="输入所在部门" class="txtinput not_null " />
               	<input type="hidden" name="remark" id="remark"  value="${loanPersonDo.remark}"  />
             </dt>
             <dd></dd>
	         </dl>
	         <div class="submit_btn">
	            <a href="javascript:void(0);" class="apply">提交借款人信息</a>
	         </div>
           </div>
		</form>
        <%@ include file="include/foot.jsp"%>
		<script type="text/javascript">
		  $('.txtinput').focus(function(){
			 $(this).addClass('write');
			 $(this).parents('dt').next('dd').text('');
		   }).blur(function(){
		     $(this).removeClass('write');
		   })
		  
		  function isNull(){//验证必填是否非空
			  var flag=true;
			  $('.not_null').each(function() {
				  if ($(this).val() == "") {
					  flag=false;
					  return flag; 
				  }
			  });
			  return flag;
		  }
		  
		  function mobileNum(){//验证手机号码
			  var reg = /(1[3,4,5,7,8]\d{9}$)/;
				 var tel=$('.tel');
				 if (!reg.test(tel.val())){
					 tel.parents('dt').next('dd').text('请填写正确的手机号');
					 return false;
				 }
			  return true;
		  }
		  
		  function chineseWord(){//验证名字是否中文
			  var reg =/^[\u4e00-\u9fa5]+$/;
				 var chinese=$('.chinese');
				 if (!reg.test(chinese.val())){
					 chinese.parents('dt').next('dd').text('请填写中文名字');
					 return false;
				 }
			  	return true;
		  }
		  
		 function identityCard(){//验证15位或18位身份证号
			  var reg =/(^\d{15}$)|(^\d{17}(\d|x|X)$)/;
				 var identity= $('.identity');
				 if (!reg.test(identity.val())){
					 identity.parents('dt').next('dd').text('请填写正确的身份证号');
					 return false;
				 }else if (!getSelectIdNo()){
					 return false;
				 }else{
					 return true;
				 }
		  }
		 function checkJobIncome(){
			var jobIncome= $('#jobIncome').val();
			if (jobIncome == "" || jobIncome.length==0) {
				$('#jobIncome').parents().next("dd").text("请输入每月收入");
				return false;
			}
		    if(!/^\+?[1-9][0-9]*$/.test(jobIncome)){
		    	$('#jobIncome').parents().next("dd").text("每月收入只能输入数字");
			 	return false;
			 } 
			 return true;
		  }
		 function checkCompany(){
			var company= $('#company').val();
			if (company == "" || company==0 ) {
				 $('#company').parents('dt').next('dd').text("请选择所在公司");
				 return false;
			 }else{
				 $('#company').parents('dt').next('dd').text("");
			 }
			 return true;
		  }
		 function checkDept(){
			var dept= $('#dept').val();
			if (dept == "" || dept.length==0) {
				 $('#dept').parents('dt').next('dd').text("请输入部门信息");
				 return false;
			 }
			 return true;
		  }
		 
		 $(".tel").blur(mobileNum);
		 $(".identity").blur(identityCard);
		 $(".chinese").blur(chineseWord);
		 
		 $("#jobIncome").blur(checkJobIncome);
		 $("#company").blur(checkCompany);
		 $("#dept").blur(checkDept);
		 
		 function getSelectIdNo() {
			 var idNo = $("#idNo").val();
			 var callRet= false;
			  $.ajax({
		          type:"GET",  
		          url:'<c:url value="selectIdNo.do"/>',  
		          data:{idNo: idNo},
		          dataType:"json",  
		          async:false,
		          success:function(data){
		        	  if(data.success && data.resultCode == '0'){
		        		    callRet = true;
		    	    		return true;	
		    			}else{
		    				$("#idNo").parents('dt').next('dd').text('该身份证已被注册使用');
		    				callRet = false;
		        	        return false;
		    			}
		          }
		        });
			  return callRet;
       	}
		 
		  $('.apply').click(function(){
			var temp= 0;
			 if(!chineseWord() ) {
				 temp++;
			 }
			 if( !mobileNum() ) {
				 temp++;
			 }
			 if( !identityCard() ) {
				 temp++;
			 }
			 if(!checkCompany() ) {
				 temp++;
			 }
			 if( !checkDept()) {
				 temp++;
			 }
			 if( !checkJobIncome()) {
				 temp++;
			 }
			 if( temp>0){
				 return false;
			 }else{
				 $('#remark').val($('#company').val()+":"+$('#dept').val())
				 $("#form_update_person").submit();
			 }
		  })
		</script>	
	</body>
</html>