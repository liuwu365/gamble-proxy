package com.lottery.gamble.proxy.web.controller.base.permisssion;

import com.google.gson.Gson;
import com.lottery.gamble.entity.BackRole;
import com.lottery.gamble.entity.Page;
import com.lottery.gamble.entity.Result;
import com.lottery.gamble.entity.TreeObject;
import com.lottery.gamble.enums.OperLog;
import com.lottery.gamble.proxy.core.util.CheckUtil;
import com.lottery.gamble.proxy.core.util.ErrorWriterUtil;
import com.lottery.gamble.proxy.core.util.TreeUtil;
import com.lottery.gamble.proxy.web.service.log.OperationLogService;
import com.lottery.gamble.proxy.web.service.permission.BackResourcesService;
import com.lottery.gamble.proxy.web.service.permission.BackRoleService;
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
import java.util.ArrayList;
import java.util.List;

@Controller
@RequestMapping("/base/role")
public class BackRoleController {

    private static final Logger logger = LoggerFactory.getLogger(BackRoleController.class);

    private static final Gson gson = new Gson();

    @Resource
    private BackRoleService backRoleService;
    @Resource
    private BackResourcesService backResourcesService;
    @Resource
    private OperationLogService operationLogService;

    @RequestMapping(value = "/add", method = RequestMethod.GET)
    public String add(Model model) {

        return "base/role/add";
    }

    @RequestMapping(value = "/edit", method = RequestMethod.GET)
    public String edit(Model model, @RequestParam Long id) {

        BackRole item = this.backRoleService.getItem(id);
        model.addAttribute("backRole", item);
        return "base/role/edit";
    }

    @RequestMapping(value = "/save", method = RequestMethod.POST)
    @ResponseBody
    public String save(HttpServletRequest request, BackRole record) {

        logger.info("request /base/role/save record={}", gson.toJson(record));
        Result result = new Result();
        try {
            result = this.backRoleService.addOrUpdateItem(record);
            String tag = CheckUtil.isEmpty(record.getId()) ? "新增" : "修改";
            operationLogService.addNormalOperLog(request, OperLog.RESOURCE, String.format("%s角色【%s】成功", tag, record.getName()));
        } catch (Exception e) {
            logger.error("BackRole/save error|record={}|ex={}", gson.toJson(record), ErrorWriterUtil.WriteError(e).toString());
            result = new Result(500, "服务器异常");
        }
        logger.info("request /base/role/save result={}", gson.toJson(result));
        return gson.toJson(result);
    }

    @RequestMapping(value = "/list")
    public String list(Model model, Page<BackRole> page) {

        try {
            page = this.backRoleService.getPage(page);
            model.addAttribute("page", page);
        } catch (Exception e) {
            logger.error("BackRole/list error|page={}|ex={}", gson.toJson(page), ErrorWriterUtil.WriteError(e).toString());
        }
        return "base/role/list";
    }

    @RequestMapping(value = "/delete")
    @ResponseBody
    public String delete(HttpServletRequest request, @RequestParam Long id, @RequestParam String roleName) {

        logger.info("request /base/role/delete id={}", gson.toJson(id));
        Result result = new Result();
        try {
            result = this.backRoleService.delItem(id);
            operationLogService.addNormalOperLog(request, OperLog.RESOURCE, String.format("删除角色【%s】成功", roleName));
        } catch (Exception e) {
            logger.error("BackRole/delete error|id={}|ex={}", id, ErrorWriterUtil.WriteError(e).toString());
            result = new Result(500, "服务器异常");
        }
        logger.info("request /base/role/delete result={}", gson.toJson(result));
        return gson.toJson(result);
    }

    @RequestMapping(value = "/editPerm")
    public String addPerm(Model model) {

        List<TreeObject> ns = new ArrayList<>();
        List<TreeObject> list = this.backResourcesService.getList();
        TreeUtil treeUtil = new TreeUtil();
        ns = treeUtil.getChildTreeObjects(list, 0);
        model.addAttribute("list", ns);
        return "base/role/perm";
    }

    @RequestMapping(value = "/getRoleRes")
    @ResponseBody
    public String getRoleRes(Long roleId) {
        Result result = new Result();
        try {
            result = this.backRoleService.getRoleResList(roleId);
        } catch (Exception e) {
            logger.error("BackRole/getRoleRes error|roleId={}|ex={}", roleId, ErrorWriterUtil.WriteError(e).toString());
        }
        return gson.toJson(result);
    }


    @RequestMapping(value = "/saveRoleRes")
    @ResponseBody
    public String saveRoleRes(HttpServletRequest request, Long roleId, String res) {
        Result result = new Result();
        try {
            result = this.backRoleService.saveRoleResRelation(res, roleId);
            operationLogService.addNormalOperLog(request, OperLog.RESOURCE, String.format("分配权限【%s】成功", res));
        } catch (Exception e) {
            logger.error("BackRole/saveRoleRes error|roleId={}|ex={}", roleId, ErrorWriterUtil.WriteError(e).toString());
            result = new Result(500, "服务器异常");
        }
        return gson.toJson(result);
    }


}
