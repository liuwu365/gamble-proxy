package com.lottery.gamble.proxy.web.controller.user;

import com.google.gson.Gson;
import com.lottery.gamble.api.EmailMessageService;
import com.lottery.gamble.entity.Page;
import com.lottery.gamble.entity.quiz.LtWithdrawBinding;
import com.lottery.gamble.entity.user.UserInfo;
import com.lottery.gamble.enums.user.*;
import com.lottery.gamble.proxy.web.controller.base.BaseController;
import com.lottery.gamble.proxy.web.service.UserInfoService;
import com.lottery.gamble.proxy.web.service.user.LtWithdrawBindingService;
import com.lottery.gamble.proxy.web.service.user.UserOperationListService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import com.lottery.gamble.entity.Result;
import java.util.Date;

/**
 * Created by Administrator on 2017/6/29.
 */
@Controller
@RequestMapping("/lottery/user")
public class UserInfoController extends BaseController{
    private static final Logger logger = LoggerFactory.getLogger(UserInfoController.class);

    private static final Gson gson = new Gson();

    @Autowired
    private UserInfoService userInfoService;

    @Autowired
    private UserOperationListService userOperationListService;

    @Autowired
    private LtWithdrawBindingService ltWithdrawBindingService;

    @Autowired
    private EmailMessageService emailMessageService;

    private final String lotteryUser="model/user/gaming_lottery_user_list";

    private final String operationList="model/user/user_operation_list";

    private final String addLotteryUser="model/user/add_lottery_user_list";

   private final String agencyList="model/user/gaming_agency_list";

    private final String auditList="model/user/gaming_agency_audit_list";


    @RequestMapping(value = "/agencyList", method = RequestMethod.GET)
    public String agencyList() {
        return agencyList;
    }

 /*   @RequestMapping(value = "/auditList", method = RequestMethod.GET)
        public String auditList(Model model,Page page) throws Exception {
           *//* logger.info("request /model/user/gaming_agency_audit_list.html page={}",new Gson().toJson(page));
            page = userInfoService.findPageByAgencyAudit(page);
            model.addAttribute("page", page);
            logger.info("list result={}", gson.toJson(page));*//*
            return auditList;
    }*/


    /**
     * 用户列表
     * @param model
     * @param page
     * @return
     * @throws Exception
     */
    @RequestMapping(value = "/list")
    public String userList(Model model,Page page) throws Exception {
        logger.info("request /model/user/gaming_lottery_user_list.html page={}",new Gson().toJson(page));
        UserStatus[] values = UserStatus.values();
        PayStatus[] payType = PayStatus.values();
        ProfitStatus[] profitStatuses= ProfitStatus.values();
        PayChannel[] payChannels= PayChannel.values();
        RegisterStatus[] registerStatuses=RegisterStatus.values();
        page = userInfoService.findPage(page);
        model.addAttribute("pageList", page);
        model.addAttribute("userList", values);
        model.addAttribute("payType", payType);
        model.addAttribute("payChannels", payChannels);
        model.addAttribute("profitStatuses", profitStatuses);
        model.addAttribute("registerStatuses", registerStatuses);
        logger.info("list result={}", gson.toJson(page));
        return lotteryUser;
    }

    @RequestMapping(value = "/operationList")
    public String operationList(Model model,Page page) throws Exception {
        logger.info("request /model/user/user_operation_list.html page={}",new Gson().toJson(page));
        page = userOperationListService.findPage(page);
        model.addAttribute("page", page);
        logger.info("list result={}", gson.toJson(page));
        return operationList;
    }

    /**
     * 修改用户状态为冻结状态
     * @param id
     * @return
     */
    @RequestMapping(value = "/updateUserStatsByFROZEN")
      public int updateUserStatsByFROZEN(Long id){
        UserInfo userInfo=new UserInfo();
        userInfo.setUpdateTime(new Date());
        userInfo.setUpdateTime(new Date());
        userInfo.setStatus(UserStatus.FROZEN.getType());
        userInfo.setId(id);
        return  userInfoService.updateByPrimaryKeySelective(userInfo);
    }

    /**
     * 修改用户状态为问题用户状态
     * @param id
     * @return
     */
    @RequestMapping(value = "/updateUserStatsBySUSPECTIVE")
    public int updateUserStatsBySUSPECTIVE(long id){
        UserInfo userInfo=new UserInfo();
        userInfo.setUpdateTime(new Date());
        userInfo.setStatus(UserStatus.SUSPECTIVE.getType());
        userInfo.setId(id);
        return  userInfoService.updateByPrimaryKeySelective(userInfo);
    }


    /**
     * 修改用户状态为代理状态
     * @param id
     * @return
     */
    @RequestMapping(value = "/updateUserStatsByAGENCY")
    public int updateUserStatsByAGENCY(long id){
        UserInfo userInfo=new UserInfo();
        /*UserInfo userInfo1=userInfoService.selectByPrimaryKey(id);*/
        userInfo.setUpdateTime(new Date());
        userInfo.setUpdateTime(new Date());
        userInfo.setStatus(UserStatus.AGENCY.getType());
        userInfo.setId(id);
        /*userInfo.setEmail(userInfo1.getEmail());*/

        /*emailMessageService.sendEmailMsg()*/

        return userInfoService.updateByPrimaryKeySelective(userInfo);


    }

    /**
     * 解绑用户银行卡
     * @param id
     * @return
     */
    @RequestMapping(value = "/deleteUserManage")
    public int deleteUserManage(@RequestParam(value="id") Long id) {
          return ltWithdrawBindingService.deleteByPrimaryKey(id);
    }

    /**
     * 绑定用户银行卡
     * @param id
     * @return
     */
    @RequestMapping(value = "/bangDingUserManage")
    public int bangDingUserManage(@RequestParam(value="id") Long id) {
        Result result = new Result<>();
        LtWithdrawBinding ltWithdrawBinding=new LtWithdrawBinding();
         int count=ltWithdrawBindingService.insertSelective(ltWithdrawBinding);
        if(count<0){
            result.setCode(200);
            result.setMsg("绑定成功！");
        }else if(count>0){
            result.setCode(400);
            result.setMsg("已经绑定过了！");
        }
        return count;
    }

    /**
     * 添加用户账号
     * @param model
     * @return
     */
    @RequestMapping(value = "/add", method = RequestMethod.GET)
    public String add(Model model) {
        return addLotteryUser;
    }

   /* @RequestMapping(value = "/addSysEnumData", method = RequestMethod.POST)
    @ResponseBody
    public Meta addSysEnumData(UserInfo userInfo) {
        Meta meta = new Meta();
        try {
            int count = userInfoService.insertSelective(userInfo);
            if(count>0){
                meta.setCode(200);
                meta.setMessage("添加成功！");
            } else{
                meta.setCode(500);
                meta.setMessage("添加失败！");
            }
        } catch (Exception e) {
            logger.error("add content error|ex={}", ErrorWriterUtil.WriteError(e).toString());
            meta.setCode(500);
            meta.setMessage("服务器异常");
            return meta;
        }
        return meta;
    }*/



}
