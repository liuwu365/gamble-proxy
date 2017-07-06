package com.lottery.gamble.proxy.web.controller;

import com.lottery.gamble.entity.BackUser;
import com.lottery.gamble.proxy.core.annotation.MethodLog;
import com.lottery.gamble.proxy.web.service.permission.BackUserService;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.*;
import org.apache.shiro.subject.Subject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

/**
 * Created by Administrator on 2016/6/27.
 */
@Controller
public class LoginController {

    private static final Logger logger = LoggerFactory.getLogger(LoginController.class);

    @Resource
    private BackUserService backUserService;

    @RequestMapping(value = "/login", method = RequestMethod.GET)
    @MethodLog(remark = "toLogin")
    public String loginForm(Model model) {
        logger.info("request login.html ");
        return "login";
    }

    @RequestMapping(value = "/login", method = RequestMethod.POST)
    public String login(BackUser user, BindingResult bindingResult,
                        HttpServletRequest request, RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            return "login";
        }
        String rememberMe = request.getParameter("rememberMe");
        String username = user.getAccountName();
        UsernamePasswordToken token = new UsernamePasswordToken(user.getAccountName(), user.getPassword()); //获取当前的Subject
        if ("on".equals(rememberMe)) {
            token.setRememberMe(true);
        }
        Subject currentUser = SecurityUtils.getSubject();
        try {

            //在调用了login方法后,SecurityManager会收到AuthenticationToken,并将其发送给已配置的Realm执行必须的认证检查
            //每个Realm都能在必要时对提交的AuthenticationTokens作出反应
            //所以这一步在调用login(token)方法时,它会走到MyRealm.doGetAuthenticationInfo()方法中,具体验证方式详见此方法
            logger.info("对用户[" + username + "]进行登录验证..验证开始");
            currentUser.login(token);
            logger.info("对用户[" + username + "]进行登录验证..验证通过");
        } catch (UnknownAccountException uae) {
            logger.info("对用户[" + username + "]进行登录验证..验证未通过,未知账户");
            redirectAttributes.addFlashAttribute("message", "未知账户");
        } catch (IncorrectCredentialsException ice) {
            logger.info("对用户[" + username + "]进行登录验证..验证未通过,错误的凭证");
            redirectAttributes.addFlashAttribute("message", "密码不正确");
        } catch (LockedAccountException lae) {
            logger.info("对用户[" + username + "]进行登录验证..验证未通过,账户已锁定");
            redirectAttributes.addFlashAttribute("message", "账户已锁定");
        } catch (ExcessiveAttemptsException eae) {
            logger.info("对用户[" + username + "]进行登录验证..验证未通过,错误次数过多");
            redirectAttributes.addFlashAttribute("message", "用户名或密码错误次数过多");
        } catch (AuthenticationException ae) {
            //通过处理Shiro的运行时AuthenticationException就可以控制用户登录失败或密码错误时的情景
            logger.info("对用户[" + username + "]进行登录验证..验证未通过,堆栈轨迹如下");
            ae.printStackTrace();
            redirectAttributes.addFlashAttribute("message", "用户名或密码不正确");
        }
        //验证是否登录成功
        if (currentUser.isAuthenticated()) {
            logger.info("用户[" + username + "]登录认证通过(这里可以进行一些认证通过后的一些系统参数初始化操作)");
            this.backUserService.addUserLoginRecord(request);
            return "redirect:/base/resources/getUserRes.html";
        } else {
            token.clear();
            return "redirect:/login.html";
        }
    }

    @RequestMapping("/logout")
    @MethodLog(remark = "logout")
    public String logout(HttpServletRequest request) {
        logger.info("request /logout.html");
        Subject subject = SecurityUtils.getSubject();
        subject.logout();
        return "login";
    }

    @RequestMapping(value = "/unauthorized", method = RequestMethod.GET)
    public String unauthorized(Model model) {
        return "unauthorized";
    }

    @RequestMapping(value = {"/index","/",""}, method = RequestMethod.GET)
    public String index(Model model) {
        return "index";
    }



}
