<%@ page language="java" contentType="text/html; charset=utf-8" pageEncoding="utf-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<!DOCTYPE html>
<html>
<head>
	<%@ include file="include/top.jsp"%>
	<script src="${basePath}/web_res/js/plugins/address/distrit.js"></script>
    <script src="${basePath}/web_res/js/plugins/address/distritSelector.js"></script>
    <link rel="stylesheet" href="${basePath}/web_res/js/plugins/address/sideSelector.css">
	<title>贷款金额</title>
	<style type="text/css">
		#address{
			position: relative;
			display: flex;
			-webkit-box-align: center;
			-webkit-align-items: center;
			align-items: center;
			font-size: 14px;
			padding: 0 15px;
		}
		#address:after {
		    content: "";
			display: block;
			border-top: 1px solid #999;
			border-right: 1px solid #999;
			width: 8px;
			height: 8px;
			position: absolute;
			-webkit-transform: rotate(45deg);
			transform: rotate(45deg);
			top: 7px;
			right: 15px;
		}
		.input-placeholder-font::-webkit-input-placeholder {
			font-size:12px; /* WebKit browsers */
		}
		.input-placeholder-font:-moz-placeholder {
			font-size:12px; /* Mozilla Firefox 4 to 18 */
		}
		.input-placeholder-font::-moz-placeholder {
			font-size:12px; /* Mozilla Firefox 19+ */
		}
		.input-placeholder-font:-ms-input-placeholder {
			font-size:12px; /*Internet Explorer 10+ */
		}
	</style>
</head>

<body>
	<form id="upd_loan" action="applyfor" method="post" >
		<div class="eLoan">  
			<dl class="loaninfo ">
				<dt>
					<label><span>*</span>借款金额：</label>
					<c:if test="${loanDo.applyAmount != null}">
					<c:set var="applyAmount" value="${loanDo.applyAmountString}"/>
					</c:if>
					<input type="number"  min="1"  name="applyAmount" value="${applyAmount}"  placeholder="输入100整数倍金额"  class="txtinput not_null money"/>
					<span>元</span>
				</dt>
				<dd>
				</dd>
				<p class="line"></p>
				<dt class="radioBox">
					<label><span>*</span>借款期限：</label>
					<!-- <label><input type="radio"  id="0" name="0" value="3" checked="checked" >三个月</label>
				    <label><input type="radio"  id="0" name="0" value="6" >六个月</label> -->
				    <select id="loanPeriod" name="loanPeriod">
						<option value="3">3个月</option>
						<option value="4">4个月</option>
						<option value="5">5个月</option>
						<option value="6">6个月</option>
						<option value="7">7个月</option>
						<option value="8">8个月</option>
						<option value="9">9个月</option>
						<option value="10">10个月</option>
						<option value="11">11个月</option>
						<option value="12">12个月</option>
					</select>
				</dt>
				<dd></dd>
				
				<p class="line"></p>
				<dt>
					<span>&nbsp;</span>根据系统计算您最多可以借贷：<span id="creditAmountTip">0.00</span>元<br>
					<span>&nbsp;</span>到账金额请以最终放款金额为准。
				</dt>				
				<dd></dd>
				<!--地址-->
				<p class="line"></p>
				<dt>				
					<a class="item" id="address" href="javascript:;">
			            <p class="text" style="color:#000; padding-left:5px; float:left"><span style="color:red">*</span>选择所在地：</p>
			            <p class="menu-text" style=" float:left;padding-left:10px; color:#000;" id="addressText"></p>
			            <div style="clear:both;"></div>
			            <input id="addressInput" type="hidden" name ="loanPersonDo.address" value="${loanPersonDo.address }">			            
			        </a>
			    </dt>				
				<dd></dd>
				<dt>
			        <div style="width:96%; margin:12px auto 0;">
			        	<input type="text" class="input-placeholder-font" id ="addressDetail" name ="loanPersonDo.addressDetail" value="${loanPersonDo.addressDetail} " placeholder="居住地址，如高新南路2号碧水龙庭A区1502室" style="height:30px; width:100%;  border: 1px solid #dfdfdf;border-radius: 5px;  -webkit-border-radius: 5px;  -moz-border-radius: 5px; padding-left:5px;"> 
			        </div>
				</dt>				
				<dd></dd>	
				<!--部门领导信息-->
				<p class="line"></p>
				<dt>
					<p style="padding-left:10px;  font-size: 13px;padding-bottom: 5px;">
						紧急联系人一：所在部门负责人（部门领导）
					</p>
					<label><span>*</span>姓　名：</label>					
					<input type="text" id="name1" name="loanRelationDoList[0].ralationName" value=""  placeholder="输入2-5位中文姓名"  class="txtinput not_null"/>		
				</dt>
				<dd></dd>
				<dt>
					<label><span>*</span>电　话：</label>	
					<input type="text" id="mobile1" name="loanRelationDoList[0].mobile" value=""  placeholder="输入手机号码"  class="txtinput not_null"/>		
				</dt>
				<dd>
				</dd>		
				<!--家属信息-->
				<p class="line"></p>
				<dt>
					<p style="padding-left:10px;  font-size: 13px;padding-bottom: 5px;">
						紧急联系人二：直系亲属
					</p>
					<label><span>*</span>姓　名：</label>					
					<input type="text" id="name2" name="loanRelationDoList[1].ralationName" value=""  placeholder="输入2-5位中文姓名"  class="txtinput not_null "/>		
				</dt>
				<dd></dd>
				<dt>
					<label><span>*</span>电　话：</label>	
					<input type="text" id="mobile2" name="loanRelationDoList[1].mobile" value=""  placeholder="输入手机号码"  class="txtinput not_null "/>		
				</dt>
				<dd>
				</dd>			
				<p class="line"></p>
				<dt class="radioBox">
					<input type="checkbox" id="chkAgreement1"  style="width: 52px; height: 15px;" /> 我已阅读并同意
					<a href="${basePath }/app_res/word/concat.html" target="_black">《合和年在线借款协议》</a>
				</dt>
				<dd style="padding-left: 25px;"></dd>
				<p class="line"></p>
				<dt class="radioBox">
					<input type="checkbox" id="chkAgreement2"  style="width: 52px; height: 15px;" /> 我已阅读并同意
					<a href="${basePath }/app_res/word/consult.html" target="_black">《借款咨询服务协议》</a>
				</dt>
			<dd style="padding-left: 25px;"></dd>
			<p class="line"></p>
				<dt class="radioBox">
					<input type="checkbox" id="chkAgreement3"   style="width: 52px; height: 15px;" /> 我已阅读并同意
					<a href="${basePath }/app_res/word/entrustDeal.html" target="_black">《委托划款协议》</a>
				</dt>
			<dd style="padding-left: 25px;"></dd>
			</dl>
		<div class="submit_btn db_f">
			<div class="bf1"><a href="javascript:void(0);" class="apply look">查看每月还款金额</a></div>
			<div class="bf1"><a href="javascript:void(0);" id="apply" class="apply">提交申请</a></div>
		</div>
	</div>

	<div class="opacity" style="display:none">
		<div class="alertcontairn" style="padding:0 0 10px; background:#fff;">
			<h4 class="alerthead">还款计划：</h4>
			<span class="closeOpacity closeCcl"><img src="${basePath }/app_res/img/elend/alertClose.png" class="boximg" /></span>
			<div class="textinfo">
				<div class="loantable"  >
					<table>
						<thead>
							<tr>
								<th>第x个月</th>
								<th>月还款金额</th>
							</tr>
						</thead>
						<tbody></tbody>
					</table>
				</div>
			</div>
		</div>
	</div>
</form>
        <%@ include file="include/foot.jsp"%>
        <script type="text/javascript">
        var jobIncome =${loanDo.jobDo.jobIncome eq null ?0:loanDo.jobDo.jobIncome};
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
        	var money =0;

        	$('.money').focus(function(){
				 $(this).parent().next("dd").text("");
			 }).blur(function() {
        		var that = $(this);
        		money =that.val()*1;
				  if(money.toString().indexOf('') > 0){
				 	$(this).parents().next("dd").text("只能输入整数金额");
				 	return false;
				 } else if(money % 100) {
				 	$(this).parents().next("dd").text("只能输入100的整数倍金额");
				 	return false;
				 }
			  var maxLoanAmount = parseFloat($("#creditAmountTip").text());
    			if (money > maxLoanAmount) {
    				$(".money").parents().next("dd").text("您的最多可以借贷金额为" + maxLoanAmount + "元");
    				return false;
    			}
			})


        	$(".look").click(function(){
	        	money = $('.money').val()*1;
		  		  if(money.toString().indexOf('') > 0){
				 	$(".money").parents().next("dd").text("只能输入整数金额");
				 	return false;
				 } else if(money % 100) {
				 	$(".money").parents().next("dd").text("只能输入100的整数倍金额");
				 	return false;
				 }	else if(money<=0){
					$(".money").parents().next("dd").text("只能输入整数金额");
					return false;
				 }
				getLoanType();
        	})

        	function getLoanType() {
        		if(money.toString().indexOf('') > 0){
					return false;
				} else if(money % 100) {
					return false;
				} else if(money<=0){
					return false;
				}

				var loanDate =  $("#loanPeriod").val();
			 	$.get('<c:url value="calSettDetail.do"/>',{loanAmount: money,loanPeriod: loanDate}, function (result) {
				 	if(!result.success){
				 		$(".money").parents().next("dd").text(result.errorMessage);
				 		return false;
				 	}					
				 	var data =  result.model[0]; 
				 	var str='';
				 	for(var i in data) {
				 		str+='<tr><td>'+(i * 1+1) +'</td><td>'+ data[i] +'</td></tr>';
				 	}
				 	$(".loantable tbody").html(str);
				 	$('.opacity').show();
				 });
        	}

        	$("#apply").click(function(){
        		var a = 0 ;
        		money = $('.money').val()*1;
    	  		  if(money<=0){
    	  			$(".money").parents().next("dd").text("借款金额有误");
    			 	a++;
    	  		  }else if(money.toString().indexOf('') > 0){
    			 	$(".money").parents().next("dd").text("只能输入整数金额");
    			 	a++;
    			 } else if(money % 100) {
    			 	$(".money").parents().next("dd").text("只能输入100的整数倍金额");
    			 	a++;
    			 }else{
    				 $(".money").parents().next("dd").text("");
    				 var maxLoanAmount = parseFloat($("#creditAmountTip").text());
    	    			if (money > maxLoanAmount) {
    	    				$(".money").parents().next("dd").text("您的最多可以借贷金额为" + maxLoanAmount + "元");
    	    				a++;
    	    			}else{
    	    				$(".money").parents().next("dd").text("");
    	    			}
    			 }
    	  		
    			if(!$("#chkAgreement1").attr("checked")){
      	  			$("#chkAgreement1").parents().next("dd").text("请先阅读并同意[合和年在线借款协议]");
      	  			a++;
      			}else{
      				$("#chkAgreement1").parents().next("dd").text("");
      			}
     			if(!$("#chkAgreement2").attr("checked")){
      	  			$("#chkAgreement2").parents().next("dd").text("请先阅读并同意[借款咨询服务协议]");
      	  			a++;
      			}else{
      				$("#chkAgreement2").parents().next("dd").text("");
      			}
     			 if(!$("#chkAgreement3").attr("checked")){
      	  			$("#chkAgreement3").parents().next("dd").text("请先阅读并同意[委托划款协议]");
      	  			a++;
      			}else{
      				$("#chkAgreement3").parents().next("dd").text("");
      			}
     			if($("#addressInput").val().toString().indexOf('') > 0){
      	  			$("#addressInput").parents().next("dd").text("请先选择所在地");
      	  			a++;
      			}else{
      				$("#addressInput").parents().next("dd").text("");
      			}
     			if($("#addressDetail").val().toString().indexOf('') > 0){
      	  			$("#addressDetail").parents().next("dd").text("请先选择居住");
      	  			a++;
      			}else{
      				$("#addressDetail").parents().next("dd").text("");
      			}
     			var name1=$("#name1").val();
     			if(name1=="" || name1==null){
     				$("#name1").parents().next("dd").text("请填领导姓名");
      	  			a++;
    		    }else if (!/^[\u4E00-\u9FA5]{2,5}$/ig.test(name1)) {
    		    	$("#name1").parents().next("dd").text("领导姓名只能为2到5中文字符!");
    				return false;
    			}else{
    				$("#name1").parents().next("dd").text("");
    			}
     			var name2=$("#name2").val();
     			if(name2=="" || name2==null){
     				$("#name2").parents().next("dd").text("请填亲属姓名");
      	  			a++;
    		    }else if (!/^[\u4E00-\u9FA5]{2,5}$/ig.test(name2)) {
    		    	$("#name2").parents().next("dd").text("亲属姓名只能为2到5中文字符!");
    				return false;
    			}else{
    				$("#name2").parents().next("dd").text("");
    			}
     			
     			var mobile1=$("#mobile1").val();
     			if(mobile1=="" || mobile1==null){
     				$("#mobile1").parents().next("dd").text("请填领导手机");
      	  			a++;
    		    }else if (!/^1{1}[3,4,5,7,8]{1}\d{9}$/gi.test(mobile1)) {
    		    	$("#mobile1").parents().next("dd").text("领导手机号码格式有误!");
    				return false;
    			}else{
    				$("#mobile1").parents().next("dd").text("");
    			}
     			var mobile2=$("#mobile2").val();
     			if(mobile2=="" || mobile2==null){
     				$("#mobile2").parents().next("dd").text("请填亲属手机");
      	  			a++;
    		    }else if (!/^1{1}[3,4,5,7,8]{1}\d{9}$/gi.test(mobile2)) {
    		    	$("#mobile2").parents().next("dd").text("亲属手机号码格式有误!");
    				return false;
    			}else{
    				$("#mobile2").parents().next("dd").text("");
    			}
     			if(a>0){
    				return false;
    			}else{
    				$("#upd_loan").submit();
    			}
        	})


		$("select").change(function(){
			$(this).parent().next("dd").text("");
			
		})
		//借款期限项变更事件
		$("select[name='loanPeriod']").bind("change", function() {
			calCreditAmount(jobIncome,$("#loanPeriod").val());
		});

	/* 	 $("input[type='radio']").change(function(){
			var btnVal = $(this).val();
			$("input[name='loanPeriod']").val(btnVal);
			calCreditAmount(jobIncome,btnVal)
		}); */ 
        	
        function calCreditAmount(income,loanPeriod){
   			$.get("calCreditAmountGroup.do", {
   				income : income,
   				loanPeriod : loanPeriod
   			}, function(result) {
   				if (!result.error) {
   					$("#creditAmountTip").text(result.creditAmount);
   				} else {
   					$("#creditAmountTip").text(result.errorMessage);
   				}
   				if (money > result.creditAmount) {
    				$(".money").parents().next("dd").text("您的最多可以借贷金额为" + result.creditAmount + "元");
    				return false;
	    		}else{
	    			$(".money").parents().next("dd").text("");
	    		}
   			});
   		};
   		calCreditAmount(jobIncome, $("#loanPeriod").val());
		  //关闭弹出框
		  $('.closeOpacity').click(function(){
		  	$('.opacity').hide();	
		  })
		var address =$('#addressInput').val();    	
		if(address !=null && address !=""){
			address = address.split(',');
		}else{
			address='';
		}   
		var myDistrit = new DistritSelector({
		    data: district,
		    value: address,
		    text: $('#addressText'),
		    input: $('#addressInput'),
		    callback: function(code, text) {
		        if (!code) return;
		        this.text.text(text.join('-'));
		        this.input.val(code.join(','));
		        $('#addressDetail').val($('#addressDetail').val()+text);
		    }
		});
		$('#address').on('click', function() {
		    myDistrit.show();
		    $('#addressDetail').val("");
		});
		   
		      
		</script>
	</body>

	</html>