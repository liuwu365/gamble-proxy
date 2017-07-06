//package com.lottery.gamble.manage.web.controller;
//
//import com.google.gson.Gson;
//import CommonUtil;
//import com.lottery.gamble.manage.web.service.*;
//import com.lottery.gamble.api.EmailMessageService;
//import com.lottery.gamble.api.SmsMessageService;
//import com.lottery.gamble.common.util.CacheUtil;
//import com.lottery.gamble.common.util.CheckUtil;
//import com.lottery.gamble.entity.*;
//import com.lottery.gamble.enums.EmailType;
//import com.lottery.gamble.enums.VerificationCodeType;
//import UserInfoVo;
//import ErrorWriterUtil;
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//import org.springframework.beans.factory.annotation.Value;
//import org.springframework.stereotype.Controller;
//import org.springframework.ui.Model;
//import org.springframework.web.bind.annotation.*;
//
//import javax.annotation.Resource;
//import javax.servlet.http.HttpServletRequest;
//import javax.servlet.http.HttpServletResponse;
//import java.util.*;
//
///**
// * @Description: 用户个人中心控制器
// * @User: liuwu_eva@163.com
// * @Date: 2016-08-04 下午 3:35
// */
//@Controller
//@RequestMapping("/user/center")
//public class UserCenterController extends BaseController {
//    private static final Logger logger = LoggerFactory.getLogger(UserCenterController.class);
//
//    private static final Gson gson = new Gson();
//
//    private static final CacheUtil cacheUtil = CacheUtil.getInstance();
//
//    private static final String PASSWORD_ERROR_ACCOUNT_LOCK = "PASSWORD_ERROR_ACCOUNT_LOCK:";
//
//
//    @Value("${email.template.image.prefix}")
//    private String email_templateImg_prefix;
//
//    @Resource
//    private UserCenterService userCenterService;
//    @Resource
//    private UserSendMsgInfoService userSendMsgInfoService;
//    @Resource
//    private EmailMessageService emailMessageService;
//    @Resource
//    private SmsMessageService smsMessageService;
//    @Resource
//    private SmsTemplateService smsTemplateService;
//    @Resource
//    private UserMsgInfoService userMsgInfoService;
//
//    @Resource
//    private UserInfoService userInfoService;
//
//    @Resource
//    private TokenInfoService tokenInfoService;
//
//    private final String user_center_listPage = "model/user/center_list";
//    private final String center_msg_listPage = "model/user/center_msg_list";
//    private final String center_send_msg = "model/user/center_send_msg";
//    private final String center_edit_msg = "model/user/center_edit_msg";
//
//    //查询用户个人中心信息列表页面
//    @RequestMapping(value = "/center_list")
//    public String userCenterList(Model model,Page<UserCenterInfo> page) throws Exception {
//        logger.info("request /model/user/center_list.html page={}", gson.toJson(page));
//        page = userCenterService.findPage(page);
//        for (int i = 0; i < page.getResult().size(); i++) {
//                String id=page.getResult().get(i).getId().toString();
//                String islock30Minute = cacheUtil.get(PASSWORD_ERROR_ACCOUNT_LOCK + id);
//                if(CheckUtil.isEmpty(islock30Minute)){
//                    page.getResult().get(i).setLocked(false);
//                } else {
//                    page.getResult().get(i).setLocked(true);
//                }
//        }
//        model.addAttribute("user", page);
//        logger.info("list result={}", gson.toJson(page));
//
//        return user_center_listPage;
//    }
//
//    //导出Execle
//    @RequestMapping(value = "/userCenterInfoExcel")
//    public void userCenterInfoExcel(String beginDate,String mobile, String endDate,HttpServletRequest request,HttpServletResponse response) throws Exception {
//        try {
//            Map<String, Object> map = new HashMap<>();
//            map.put("beginDate", beginDate);
//            map.put("endDate", endDate);
//            map.put("mobile",mobile);
//            logger.info("request  /model/user/userCenterInfoExcel map={}", gson.toJson(map));
//            List<UserCenterInfo> userCenterInfos = userCenterService.selectUserCenterInfoToExecle(map);
//            List<UserInfoVo> userInfoVos = new ArrayList<UserInfoVo>();
//            for (UserCenterInfo userCenterInfo : userCenterInfos) {
//                UserInfoVo userInfoVo = new UserInfoVo();
//                userInfoVo.setUserName(userCenterInfo.getUserName());
//                userInfoVo.setId(userCenterInfo.getId());
//                userInfoVo.setMobile(userCenterInfo.getMobile());
//                userInfoVo.setEmail(userCenterInfo.getEmail());
//                userInfoVo.setLastLoginTime(userCenterInfo.getLastLoginTime());
//                userInfoVo.setLevel(userCenterInfo.getLevel());
//                userInfoVos.add(userInfoVo);
//            }
//            logger.info("userCenterInfoExcel list result={}", gson.toJson(userInfoVos));
//            logger.info("userCenterInfoExcel list beginDate={}", gson.toJson(beginDate));
//            logger.info("userCenterInfoExcel list endDate={}", gson.toJson(endDate));
//            logger.info("userCenterInfoExcel list mobile={}", gson.toJson(mobile));
//            String[] strExcTitle = {"用户ID" ,"用户名","最后登录时间","手机号","邮箱","等级"};
//            exportMethod(userInfoVos, strExcTitle, response); //Excel导出
//        } catch (Exception e) {
//            logger.error("userCenterInfoExcel error|ex={}", ErrorWriterUtil.WriteError(e).toString());
//        }
//    }
//
//
//
//    //批量解冻金币
//    @RequestMapping(value = "/unfreezeBath/{userIds}")
//    @ResponseBody
//    public Result unfreezeBath(@PathVariable(value = "userIds") String userIds) throws Exception {
//        Result meta = new Result();
//        try {
//            //查询用户财富==>解冻==>将冻结的金币返还==>记录操作日志
//            SysUser sysUser = getCurrentUser();
//            boolean flag = userCenterService.updateUserCenterInfoBath(sysUser, userIds);
//            if (flag) {
//                meta.setCode(200);
//                meta.setMsg("操作成功");
//            } else {
//                meta.setCode(500);
//                meta.setMsg("操作失败");
//            }
//        } catch (Exception e) {
//            logger.error("unfreezeBath error|ex={}", ErrorWriterUtil.WriteError(e).toString());
//            meta.setCode(500);
//            meta.setMsg("服务器异常");
//            return meta;
//        }
//        return meta;
//    }
//
//    //批量加减金币
//    @RequestMapping(value = "/wealthNumberBatch", method = RequestMethod.POST)
//    @ResponseBody
//    public String wealthNumberBatch(@RequestParam(value = "userIds") String userIds,
//                                    @RequestParam(value = "wealthNumber") Double wealthNumber,
//                                    @RequestParam(value = "operation") String operation) throws Exception {
//        Result meta = new Result();
//        try {
//            SysUser sysUser = getCurrentUser();
//            boolean flag = userCenterService.updateUserWealthNumberBatch(sysUser, userIds, wealthNumber, operation);
//            if (flag) {
//                meta.setCode(200);
//                meta.setMsg("操作成功");
//            } else {
//                meta.setCode(500);
//                meta.setMsg("操作失败");
//            }
//        } catch (Exception e) {
//            logger.error("wealthNumberBatch error|ex={}", ErrorWriterUtil.WriteError(e).toString());
//            meta.setCode(500);
//            meta.setMsg("服务器异常");
//            return gson.toJson(meta);
//        }
//        return gson.toJson(meta);
//    }
//
//    //批量加减经验
//    @RequestMapping(value = "/experienceBatch", method = RequestMethod.POST)
//    @ResponseBody
//    public String experienceBatch(@RequestParam(value = "userIds") String userIds,
//                                  @RequestParam(value = "experience") Integer experience,
//                                  @RequestParam(value = "operation") String operation) throws Exception {
//        Result meta = new Result();
//        try {
//            SysUser sysUser = getCurrentUser();
//            boolean flag = userCenterService.updateUserExperienceBatch(sysUser, userIds, experience, operation);
//            if (flag) {
//                meta.setCode(200);
//                meta.setMsg("操作成功");
//            } else {
//                meta.setCode(500);
//                meta.setMsg("操作失败");
//            }
//        } catch (Exception e) {
//            logger.error("experienceBatch error|ex={}", ErrorWriterUtil.WriteError(e).toString());
//            meta.setCode(500);
//            meta.setMsg("服务器异常");
//            return gson.toJson(meta);
//        }
//        return gson.toJson(meta);
//    }
//
//    //修改经验
//    @RequestMapping(value = "/updateExperience")
//    @ResponseBody
//    public String updateExperience(@RequestParam(value = "userId") Long userId, @RequestParam(value = "experience") Integer experience) throws Exception {
//        Result meta = new Result();
//        try {
//            SysUser sysUser = getCurrentUser();
//            boolean flag = userCenterService.updateUserExperience(sysUser, userId, experience);
//            if (flag) {
//                meta.setCode(200);
//                meta.setMsg("操作成功");
//            } else {
//                meta.setCode(500);
//                meta.setMsg("操作失败");
//            }
//        } catch (Exception e) {
//            logger.error("updateExperience error|ex={}", ErrorWriterUtil.WriteError(e).toString());
//            meta.setCode(500);
//            meta.setMsg("服务器异常");
//            return gson.toJson(meta);
//        }
//        return gson.toJson(meta);
//    }
//
//    //修改金币
//    @RequestMapping(value = "/updateWealthNumber")
//    @ResponseBody
//    public String updateWealthNumber(@RequestParam(value = "userId") Long userId, @RequestParam(value = "wealthNumber") Double wealthNumber) throws Exception {
//        Result meta = new Result();
//        try {
//            SysUser sysUser = getCurrentUser();
//            boolean flag = userCenterService.updateUserWealthNumber(sysUser, userId, wealthNumber);
//            if (flag) {
//                meta.setCode(200);
//                meta.setMsg("操作成功");
//            } else {
//                meta.setCode(500);
//                meta.setMsg("操作失败");
//            }
//        } catch (Exception e) {
//            logger.error("updateWealthNumber error|ex={}", ErrorWriterUtil.WriteError(e).toString());
//            meta.setCode(500);
//            meta.setMsg("服务器异常");
//            return gson.toJson(meta);
//        }
//        return gson.toJson(meta);
//    }
//
//    //***************************************** 发送消息 *******************************************************************
//    //查询发送消息列表(短信消息，邮件消息)
//    @RequestMapping(value = "/center_msg_list")
//    public String userCenterMsgList(Model model, Page<UserMsgInfo> pageX) throws Exception {
//        logger.info("request /model/user/center_msg_list.html pageX={}", gson.toJson(pageX));
//
//        pageX = userSendMsgInfoService.findPage(pageX);
//        model.addAttribute("msg", pageX);
//
//        logger.info("list result={}", gson.toJson(pageX));
//        return center_msg_listPage;
//    }
//
//    //跳转到发送消息页面
//    @RequestMapping(value = "/center_send_msg", method = RequestMethod.GET)
//    public String contentAdd() {
//        return center_send_msg;
//    }
//
//    //查询邮件模版主题(1:短信 2:邮件)
//    @RequestMapping(value = "/selectEmailTemplateTheme/{type}", method = RequestMethod.GET)
//    @ResponseBody
//    public String selectEmailTemplateTheme(@PathVariable(value = "type") Byte type) {
//        List<SmsTemplate> smsTemplateList = smsTemplateService.selectByType(type.byteValue());
//        return gson.toJson(smsTemplateList);
//    }
//
//    //查询短信模版主题(1:短信 2:邮件),从运营商直接获取
//    @RequestMapping(value = "/selectMobileTemplateTheme/{type}", method = RequestMethod.GET)
//    @ResponseBody
//    public String selectMobileTemplateTheme(@PathVariable(value = "type") Byte type) {
//        MessageResult result = new MessageResult();
//        if (CheckUtil.isEmpty(type)) {
//            result.setCode(404);
//            result.setMsg("非法操作");
//        } else {
//            result = smsMessageService.getTplList(null);
//        }
//        return gson.toJson(result);
//    }
//
//    //发送消息
//    @RequestMapping(value = "/send_msg", method = RequestMethod.POST)
//    @ResponseBody
//    public String sendMsg(Integer msgSendType, String mobile, String email, String emailType, String message) {
//        Result meta = new Result();
//        try {
//            //第一步：各种验证  1.短信发送 2.邮件发送
//            boolean flag = false;
//            boolean target = false;
//            if (msgSendType == 1) {
//                if (!CommonUtil.handleMobile(mobile)) {
//                    meta.setCode(502);
//                    meta.setMsg("手机号码【" + mobile + "】不合法");
//                    return gson.toJson(meta);
//                }
//                flag = userSendMsgInfoService.checkSendMsg(mobile, null); //检查是否存在
//                if (flag == false) {
//                    meta.setCode(404);
//                    meta.setMsg("手机号码【" + mobile + "】不存在，请核实");
//                    return gson.toJson(meta);
//                } else {
//                    target = true;
//                }
//            } else if (msgSendType == 2) {
//                if (!CommonUtil.handleEmail(email)) {
//                    meta.setCode(502);
//                    meta.setMsg("邮箱不合法");
//                    return gson.toJson(meta);
//                }
//                flag = userSendMsgInfoService.checkSendMsg(null, email); //检查是否存在
//                if (flag == false) {
//                    meta.setCode(404);
//                    meta.setMsg("邮箱地址不存在，请核实");
//                    return gson.toJson(meta);
//                } else {
//                    target = true;
//                }
//            } else {
//                meta.setCode(403);
//                meta.setMsg("非法操作");
//                return gson.toJson(meta);
//            }
//            if (CheckUtil.isEmpty(message)) {
//                meta.setCode(400);
//                meta.setMsg("发送消息不能为空");
//                return gson.toJson(meta);
//            }
//
//            //第2步：发送消息
//            MessageResult messageInfo = null;
//            SysUser sysUser = getCurrentUser();
//            if (target && msgSendType == 1) {
//                //mobileTemplateTitle 短信模版内容（使用的是智能匹配发送短信）
//                messageInfo = smsMessageService.sendMobileMessage(sysUser, VerificationCodeType.sendMsgByMobile, mobile, message);
//                meta.setCode(messageInfo.getCode());
//                meta.setMsg(messageInfo.getMsg());
//
//            } else if (target && msgSendType == 2) {
//                String subject = "WCA赛事平台-消息通知";
//                if (emailType.equals("normal")) {
//                    messageInfo = emailMessageService.sendEmailMsg(sysUser, email, subject, message, VerificationCodeType.sendMsgByEmail, EmailType.normalEmail, null);
//                } else if (emailType.equals("template")) {
//                    EmailTemplateInfo entity = new EmailTemplateInfo();
//                    entity.setSubject(subject);
//                    entity.setMailTo(new String[]{email});
//                    entity.setVmfile(message); //mail_1.vm
//                    entity.setFiles(new String[]{}); //附件文件
//                    //替换模版中的字段
//                    entity.setModel(new HashMap<String, Object>() {
//                        {
//                            put("userName", email.substring(0, email.indexOf("@")));
//                            put("email_templateImg_prefix", email_templateImg_prefix);
//                        }
//                    });
//                    messageInfo = emailMessageService.sendEmailMsg(sysUser, email, subject, message, VerificationCodeType.sendMsgByEmail, EmailType.templateEmail, entity);
//                }
//                meta.setCode(messageInfo.getCode());
//                meta.setMsg(messageInfo.getMsg());
//            }
//            //第3步：插入消息记录到db中
//            if (!CheckUtil.isEmpty(messageInfo)) {
//                UserMsgInfo userMsgInfo = new UserMsgInfo();
//                userMsgInfo.setMsgSendType(msgSendType == 1 ? 1 : 2); //消息发送类型: 1.短信发送 2.邮件发送
//                userMsgInfo.setMobile(CheckUtil.isEmpty(mobile) ? "" : mobile);
//                userMsgInfo.setEmail(CheckUtil.isEmpty(email) ? "" : email);
//                userMsgInfo.setMessage(message);
//                if (messageInfo.getCode() == 200) {
//                    userMsgInfo.setStatus(1);  //发送状态: 1.成功 2.失败
//                } else {
//                    userMsgInfo.setStatus(2);
//                    userMsgInfo.setFailReason(messageInfo.getMsg());
//                }
//                userMsgInfoService.insertSelective(userMsgInfo);
//            }
//        } catch (Exception e) {
//            meta.setCode(500);
//            meta.setMsg("系统内部错误:" + e.getMessage());
//        }
//        return gson.toJson(meta);
//    }
//
//    //修改后重新发送
//    @RequestMapping(value = "/update_return_send", method = RequestMethod.GET)
//    public String updateReturnSend(Model model, @RequestParam(value = "id") Long id) {
//        UserMsgInfo userMsgInfo = userSendMsgInfoService.selectByPrimaryKey(id);
//        model.addAttribute("msg", userMsgInfo);
//        return center_edit_msg;
//    }
//
//    //预览邮件模版
//    @RequestMapping(value = "/scanEmailTemplate", method = RequestMethod.POST)
//    @ResponseBody
//    public String scanEmailTemplate(){
//        return gson.toJson("");
//    }
//
//    /**
//     * 批量删除用户
//     * @param userIds
//     * @return
//     */
//    @RequestMapping(value = "/delUserBatch", method = RequestMethod.POST)
//    @ResponseBody
//    public String delUserBatch(String userIds){
//
//        Result result = new Result();
//        SysUser sysUser = new SysUser();
//        try {
//            sysUser = getCurrentUser();
//            logger.info("request /base/user/delUserBatch sysUser={}", gson.toJson(sysUser));
//            result = this.userCenterService.delUserBatch(userIds, sysUser);
//        } catch (Exception e) {
//            logger.error("delUserBatch error|userIds={}|sysUser={}|ex={}", gson.toJson(userIds), gson.toJson(sysUser), ErrorWriterUtil.WriteError(e).toString());
//            result = new Result(500,"服务器异常");
//        }
//        logger.info("request /base/user/resetPassword result={}", gson.toJson(result));
//        return gson.toJson(result);
//    }
//
//    /**
//     * 批量改变用户锁定状态
//     * @param userIds
//     * @return
//     */
//    @RequestMapping(value = "/lockedUserBatch", method = RequestMethod.POST)
//    @ResponseBody
//    public String lockedUserBatch(String userIds,boolean locked){
//        Result result = new Result();
//        SysUser sysUser = new SysUser();
//        try {
//                sysUser = getCurrentUser();
//                logger.info("request /base/user/lockedUserBatch sysUser={}|locked={}", gson.toJson(sysUser), locked);
//                result = this.userCenterService.lockedUserBatch(userIds, locked, sysUser);
//                String[] ids = userIds.split(",");
//                List<String> list = Arrays.asList(ids);
//                    for (int i = 0; i < list.size(); i++) {
//                        String id = list.get(i);
//                        if (locked) {
//                            cacheUtil.set(PASSWORD_ERROR_ACCOUNT_LOCK + id, id);
//
//                        } else {
//                            cacheUtil.del(PASSWORD_ERROR_ACCOUNT_LOCK + id);
//                        }
//                    }
//        } catch (Exception e) {
//            logger.error("lockedUserBatch error|userIds={}|locked={}|sysUser={}|ex={}", gson.toJson(userIds), locked,gson.toJson(sysUser), ErrorWriterUtil.WriteError(e).toString());
//            result = new Result(500,"服务器异常");
//        }
//        logger.info("request /base/user/lockedUserBatch result={}", gson.toJson(result));
//        return gson.toJson(result);
//    }
//
//
//
//    //设置用户是否是裁判
//    @RequestMapping(value = "/updateJudge",method = RequestMethod.POST)
//    @ResponseBody
//    public String updateJudge(@RequestParam(value = "userId") Long userId) throws Exception {
//        Result result=new Result();
//        try {
//            UserInfo userInfo=userInfoService.selectByPrimaryKey(userId);
//            if(userInfo.getType()==0){
//                userInfo.setType(1);
//                userInfoService.updateByPrimaryKeySelective(userInfo);
//            }else if(userInfo.getType()==1){
//                userInfo.setType(0);
//                userInfoService.updateByPrimaryKeySelective(userInfo);
//            }
//            updateUserCache(userInfo);
//        } catch (Exception e) {
//            logger.error("updateJudge error|ex={}", ErrorWriterUtil.WriteError(e).toString());
//            return gson.toJson(result);
//        }
//        return gson.toJson(result);
//    }
//
//    private void updateUserCache(UserInfo userInfo) {
//        String key = "user:" + userInfo.getId();
//        cacheUtil.onRedisSync(jedis -> {
//            if(jedis.exists(key)) {
//                jedis.hset(key, "type", String.valueOf(userInfo.getType()));
//            }
//            return true;
//        });
//    }
//
//
//    //修改金币
//    @RequestMapping(value = "/updateDiamond")
//    @ResponseBody
//    public String updateDiamond(@RequestParam(value = "userId") Long userId, @RequestParam(value = "diamond") Integer diamond) throws Exception {
//        Result meta = new Result();
//        try {
//            SysUser sysUser = getCurrentUser();
//            boolean flag = userCenterService.updateUserDiamond(sysUser,userId,diamond);
//            if (flag) {
//                meta.setCode(200);
//                meta.setMsg("操作成功");
//            } else {
//                meta.setCode(500);
//                meta.setMsg("操作失败");
//            }
//        } catch (Exception e) {
//            logger.error("updateDiamond error|ex={}", ErrorWriterUtil.WriteError(e).toString());
//            meta.setCode(500);
//            meta.setMsg("服务器异常");
//            return gson.toJson(meta);
//        }
//        return gson.toJson(meta);
//    }
//}
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
