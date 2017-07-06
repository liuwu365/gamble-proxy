package com.lottery.gamble.proxy.web.controller.base;


import com.google.gson.Gson;
import com.lottery.gamble.common.util.CommonUtil;
import com.lottery.gamble.entity.BackUser;
import com.lottery.gamble.proxy.core.util.ExcelOperateUtil;
import com.lottery.gamble.proxy.core.util.SessionUtil;
import org.apache.log4j.Logger;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authz.AuthorizationException;
import org.apache.shiro.subject.Subject;
import org.springframework.beans.propertyeditors.CustomDateEditor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.ServletRequestDataBinder;
import org.springframework.web.bind.annotation.InitBinder;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;


@Controller
public class BaseController {
    public static Logger LOG = Logger.getLogger(BaseController.class);

    public static final Gson GSON = new Gson();

    @InitBinder
    protected void init(HttpServletRequest request, ServletRequestDataBinder binder) throws Exception {
        CustomDateEditor dateEditor = new CustomDateEditor(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss"), true);
        binder.registerCustomEditor(Date.class, dateEditor);
    }

    /**
     * 获取当前登录用户
     *
     * @return
     */
    public static BackUser getCurrentUser() {
        return SessionUtil.getCurrentUser();
    }

    public static String getCurrentAccountName() {
        return SecurityUtils.getSubject().getPrincipal().toString();
    }

    /**
     * java判断是否拥有该权限
     */
    public static void isAuthoriza(String authorizaCode) {
        //权限校验。判断是否包含权限。
        Subject subject = SecurityUtils.getSubject();
        //具体响应ShiroDbRealm。doGetAuthorizationInfo，判断是否包含此url的响应权限
        boolean isPermitted = subject.isPermitted(authorizaCode);
        if (isPermitted) {
            System.out.println("拥有该权限！");
        } else {
            System.out.println("没有该权限！");
            throw new AuthorizationException();
        }
    }

    /**
     * 导出到指定路径下
     * list:数据集合
     * path：导出的路径
     * strExcTitle:导出Excel列头名称
     */
    public static void exportMethod2(List<?> list, String path, String[] strExcTitle, HttpServletResponse response) throws Exception {
        try {
            if (list.size() > 0) {
                //当前时间
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy年MM月dd日HH点mm分ss秒");
                String time = sdf.format(new Date());
                //生成当天文件夹
                String nowFileName = CommonUtil.createFile(path);
                String fileName = path + nowFileName + "/" + getCurrentUser().getAccountName() + "_" + time + ".xls"; //格式：登录者帐号_年月日时分秒
                OutputStream out = new FileOutputStream(fileName);

                //标题参数
                String title = "sheet1";
                //时间格式参数
                String fomatStr = "yyyy-MM-dd hh:mm:ss";
                //调用
                ExcelOperateUtil.exportExcel(title, strExcTitle, list, out, fomatStr);
                out.flush();
                out.close();
            }
        } catch (Exception e) {
            throw new Exception(e);
        }
    }

    /**
     * 以弹框形式，用户可指定自己的路径
     * 导出到指定路径下
     * list:数据集合
     * path：导出的路径
     * strExcTitle:导出Excel列头名称
     */
    public static void exportMethod(List<?> list, String[] strExcTitle, HttpServletResponse response) throws Exception {
        try {
            //当前时间
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy年MM月dd日HH点mm分ss秒");
            String time = sdf.format(new Date());
            String fileName = time + ".xls"; //格式：登录者帐号_年月日时分秒
            response.setContentType("application/x-msdownload");
            response.addHeader("Content-Disposition", "attachment;filename=" + new String(fileName.getBytes("GBK"), "iso8859-1"));
            OutputStream out = response.getOutputStream();
            //标题参数
            String title = "sheet1";
            //时间格式参数
            String fomatStr = "yyyy-MM-dd HH:mm:ss";
            //调用
            ExcelOperateUtil.exportExcel(title, strExcTitle, list, out, fomatStr);
            out.flush();
            out.close();
        } catch (Exception e) {
            throw new Exception(e);
        }
    }

}
