package com.lottery.gamble.proxy.web.controller.base.permisssion;

import com.google.gson.Gson;
import com.lottery.gamble.entity.*;
import com.lottery.gamble.enums.OperLog;
import com.lottery.gamble.proxy.core.util.CheckUtil;
import com.lottery.gamble.proxy.core.util.ErrorWriterUtil;
import com.lottery.gamble.proxy.web.controller.base.BaseController;
import com.lottery.gamble.proxy.web.service.log.OperationLogService;
import com.lottery.gamble.proxy.web.service.permission.BackInfoService;
import com.lottery.gamble.proxy.web.service.permission.BackRoleService;
import com.lottery.gamble.proxy.web.service.permission.BackUserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.List;

@Controller
@RequestMapping("/base/user")
public class BackUserController extends BaseController {
    private static final Logger logger = LoggerFactory.getLogger(BackUserController.class);
    private static final Gson gson = new Gson();

    @Resource
    private BackInfoService backInfoService;
    @Resource
    private BackUserService backUserService;
    @Resource
    private BackRoleService backRoleService;
    @Resource
    private OperationLogService operationLogService;

    @RequestMapping(value = "/add", method = RequestMethod.GET)
    public String add(Model model) {
        List<BackRole> list = this.backRoleService.getList();
        model.addAttribute("list", list);
        return "base/user/add";
    }

    @RequestMapping(value = "/edit", method = RequestMethod.GET)
    public String edit(Model model, @RequestParam Long id) {
        BackUser item = this.backUserService.getItem(id);
        model.addAttribute("backUser", item);
        List<BackRole> list = this.backRoleService.getList();
        model.addAttribute("list", list);
        return "base/user/edit";
    }

    @RequestMapping(value = "/save", method = RequestMethod.POST)
    @ResponseBody
    public String save(HttpServletRequest request, BackUser backUser, String roles) {
        logger.info("request /base/user/save backUser={}|roles={}|", gson.toJson(backUser), gson.toJson(roles));
        Result result = new Result();
        try {
            result = this.backInfoService.saveUserInfo(backUser, roles);
            String tag = CheckUtil.isEmpty(backUser.getId()) ? "新增" : "修改";
            operationLogService.addNormalOperLog(request, OperLog.BACK_USER, String.format("%s用户【%s】成功", tag, backUser.getUserName()));
        } catch (Exception e) {
            logger.error("backUser/save error|backUser={}|ex={}", gson.toJson(backUser), ErrorWriterUtil.WriteError(e).toString());
            result = new Result(500, "服务器异常");
        }
        logger.info("request /base/role/save result={}", gson.toJson(result));
        return gson.toJson(result);
    }

    @RequestMapping(value = "/list")
    public String list(Model model, Page<BackUser> page) {
        try {
            page = this.backUserService.getPage(page);
            model.addAttribute("page", page);
        } catch (Exception e) {
            logger.error("backUser/list error|page={}|ex={}", gson.toJson(page), ErrorWriterUtil.WriteError(e).toString());
        }
        return "base/user/list";
    }

    @RequestMapping(value = "/delete")
    @ResponseBody
    public String delete(HttpServletRequest request, @RequestParam Long id, @RequestParam String userName) {
        logger.info("request /base/user/delete id={}", gson.toJson(id));
        Result result = new Result();
        try {
            operationLogService.addNormalOperLog(request, OperLog.BACK_USER, String.format("删除用户【%s】成功", userName));
            result = this.backUserService.delItem(id);
        } catch (Exception e) {
            logger.error("backUser/delete error|id={}|ex={}", id, ErrorWriterUtil.WriteError(e).toString());
            result = new Result(500, "服务器异常");
        }
        logger.info("request /base/user/delete result={}", gson.toJson(result));
        return gson.toJson(result);
    }

    @RequestMapping(value = "/isExist")
    @ResponseBody
    public boolean isExist(String accountName, @RequestParam(value = "flag", defaultValue = "false") boolean fg) {
        boolean flag = false;
        try {
            Result result = this.backUserService.isExisit(accountName);
            if (result.getCode() == 200) {
                if (!fg) {
                    flag = !(boolean) result.getT();
                } else {
                    flag = (boolean) result.getT();
                }
            }
        } catch (Exception e) {
            logger.error("backUser/isExist error|accountName={}|ex={}", accountName, ErrorWriterUtil.WriteError(e).toString());
        }
        return flag;
    }

    @RequestMapping(value = "/roleList")
    @ResponseBody
    public String roleList(Long userId) {
        Result result = new Result();
        try {
            result = this.backUserService.getUserRoleList(userId);
        } catch (Exception e) {
            logger.error("backUser/roleList error|userId={}|ex={}", userId, ErrorWriterUtil.WriteError(e).toString());
            result = new Result(500, "服务器异常");
        }
        return gson.toJson(result);
    }

    @RequestMapping(value = "/changePassword")
    @ResponseBody
    public String changePassword(HttpServletRequest request, String password, String nPassword) {
        Result result = new Result();
        BackUser user = new BackUser();
        try {
            user = getCurrentUser();
            logger.info("request /base/user/changePassword user={}", gson.toJson(user));
            result = this.backUserService.changePassword(user, password, nPassword);
            operationLogService.addNormalOperLog(request, OperLog.BACK_USER, String.format("修改用户【%s】密码成功", user.getUserName()));
        } catch (Exception e) {
            logger.error("backUser/changePassword error|user={}|password={}|nPassword={}|ex={}", user, password, nPassword, ErrorWriterUtil.WriteError(e).toString());
            result = new Result(500, "服务器异常");
        }
        logger.info("request /base/user/changePassword result={}", gson.toJson(result));
        return gson.toJson(result);
    }


    @RequestMapping(value = "/login/list")
    public String loginList(Model model, Page<BackUserLogin> page) {
        try {
            page = this.backUserService.getLoginPage(page);
            model.addAttribute("page", page);
        } catch (Exception e) {
            logger.error("backUser/loginList error|page={}|ex={}", gson.toJson(page), ErrorWriterUtil.WriteError(e).toString());
        }
        return "base/user/login_list";
    }

    @RequestMapping(value = "/resetPassword")
    @ResponseBody
    public String resetPassword(HttpServletRequest request, Long userId) {
        Result result = new Result();
        try {
            logger.info("request /base/user/resetPassword userId={}", userId);
            result = this.backUserService.resetPassword(userId);
            BackUser user = backUserService.getItem(userId);
            operationLogService.addNormalOperLog(request, OperLog.BACK_USER, String.format("重置用户【%s】密码成功", user.getUserName()));
        } catch (Exception e) {
            logger.error("backUser/resetPassword error|userId={}|ex={}", userId, ErrorWriterUtil.WriteError(e).toString());
            result = new Result(500, "服务器异常");
        }
        logger.info("request /base/user/resetPassword result={}", gson.toJson(result));
        return gson.toJson(result);
    }

    @RequestMapping(value = "/lockUser")
    @ResponseBody
    public String lockUser(HttpServletRequest request, @RequestParam Long id, @RequestParam Boolean lock, @RequestParam String accountName) {
        Result result;
        try {
            BackUser user = new BackUser();
            user.setId(id);
            user.setLocked(lock);
            result = backUserService.addOrUpdateItem(user);
            operationLogService.addNormalOperLog(request, OperLog.BACK_USER, String.format("锁定用户帐号【%s】", accountName));
        } catch (Exception e) {
            logger.error("backUser/lockUser error|ex={}", ErrorWriterUtil.WriteError(e).toString());
            result = new Result(500, "服务器异常");
        }
        return gson.toJson(result);
    }

    @RequestMapping(value = "/operWorkOrder")
    @ResponseBody
    public String operWorkOrder(@RequestParam Long id, @RequestParam Boolean operWorkOrderVal) {
        Result result;
        try {
            BackUser user = new BackUser();
            user.setId(id);
            user.setOperWorkOrder(operWorkOrderVal);
            result = backUserService.addOrUpdateItem(user);
        } catch (Exception e) {
            logger.error("backUser/operWorkOrder error|ex={}", ErrorWriterUtil.WriteError(e).toString());
            result = new Result(500, "服务器异常");
        }
        return gson.toJson(result);
    }


}
