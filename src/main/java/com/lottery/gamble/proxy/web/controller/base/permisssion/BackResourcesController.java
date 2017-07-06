package com.lottery.gamble.proxy.web.controller.base.permisssion;

import com.google.gson.Gson;
import com.lottery.gamble.entity.BackResources;
import com.lottery.gamble.entity.Page;
import com.lottery.gamble.entity.Result;
import com.lottery.gamble.entity.TreeObject;
import com.lottery.gamble.enums.OperLog;
import com.lottery.gamble.proxy.core.util.CheckUtil;
import com.lottery.gamble.proxy.core.util.ErrorWriterUtil;
import com.lottery.gamble.proxy.core.util.SessionUtil;
import com.lottery.gamble.proxy.core.util.TreeUtil;
import com.lottery.gamble.proxy.web.service.log.OperationLogService;
import com.lottery.gamble.proxy.web.service.permission.BackResourcesService;
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
import javax.servlet.http.HttpSession;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/base/resources")
public class BackResourcesController {
    private static final Logger logger = LoggerFactory.getLogger(BackResourcesController.class);
    private static final Gson gson = new Gson();

    @Resource
    private BackResourcesService backResourcesService;
    @Resource
    private OperationLogService operationLogService;

    @RequestMapping(value = "/add", method = RequestMethod.GET)
    public String add(Model model) {

        return "base/resources/add";
    }

    @RequestMapping(value = "/edit", method = RequestMethod.GET)
    public String edit(Model model, @RequestParam Long id) {

        BackResources item = this.backResourcesService.getItem(id);
        List<TreeObject> list = this.backResourcesService.getList();
        TreeUtil treeUtil = new TreeUtil();
        List<TreeObject> ns = treeUtil.getChildTreeObjects(list, 0, "　    ");
        model.addAttribute("BackResources", item);
        model.addAttribute("ns", ns);
        return "base/resources/edit";
    }

    @RequestMapping(value = "/save", method = RequestMethod.POST)
    @ResponseBody
    public String save(BackResources record, HttpServletRequest request) {

        logger.info("request /base/resources/save record={}", gson.toJson(record));
        Result result = new Result();
        try {
            result = this.backResourcesService.addOrUpdateItem(record);
            String tag = CheckUtil.isEmpty(record.getId()) ? "新增" : "修改";
            operationLogService.addNormalOperLog(request, OperLog.RESOURCE, String.format("%s资源【%s】", tag, record.getName()));
        } catch (Exception e) {
            logger.error("BackResources/save error|record={}|ex={}", gson.toJson(record), ErrorWriterUtil.WriteError(e).toString());
            result = new Result(500, "服务器异常");
        }
        logger.info("request /base/resources/save result={}", gson.toJson(result));
        return gson.toJson(result);
    }

    @RequestMapping(value = "/reslists")
    @ResponseBody
    public String reslists(Map map) {

        List<TreeObject> ns = new ArrayList<>();
        try {
            List<TreeObject> list = this.backResourcesService.getList();
            TreeUtil treeUtil = new TreeUtil();
            ns = treeUtil.getChildTreeObjects(list, 0, "　    ");
        } catch (Exception e) {
            logger.error("BackResources/reslists error|map={}|ex={}", gson.toJson(map), ErrorWriterUtil.WriteError(e).toString());
        }
        return gson.toJson(ns);
    }


    @RequestMapping(value = "/list")
    public String list(Page page, Model model) {

        try {
            page = this.backResourcesService.getPage(page);
            model.addAttribute("page", page);
            model.addAttribute("menuId", page.getFilter().get("parentId"));
        } catch (Exception e) {
            logger.error("BackResources/list error|page={}|ex={}", gson.toJson(page), ErrorWriterUtil.WriteError(e).toString());
        }
        return "base/resources/list";
    }

    @RequestMapping(value = "/delete")
    @ResponseBody
    public String delete(@RequestParam Long id, @RequestParam String resourceName, HttpServletRequest request) {

        logger.info("request /base/resources/delete id={}", id);
        Result result = new Result();
        try {
            result = this.backResourcesService.delItem(id);
            operationLogService.addNormalOperLog(request, OperLog.RESOURCE, String.format("删除资源【%s】", resourceName));
        } catch (Exception e) {
            logger.error("BackResources/delete error|id={}|ex={}", id, ErrorWriterUtil.WriteError(e).toString());
            result = new Result(500, "服务器异常");
        }
        logger.info("request /base/resources/delete result={}", gson.toJson(result));
        return gson.toJson(result);
    }


    @RequestMapping(value = "/getUserRes")
    public String getUserRes(HttpServletRequest request) {

        HttpSession session = request.getSession();
        Long userId = SessionUtil.getUserId();
        try {
            List<TreeObject> list = this.backResourcesService.getUserList(userId);
            session.setAttribute("resList", list);
        } catch (Exception e) {
            logger.error("BackResources/getUserRes error|userId={}|ex={}", userId, ErrorWriterUtil.WriteError(e).toString());
        }
        return "redirect:/index.html";
    }

    @RequestMapping(value = "/findParentMenuAll", method = RequestMethod.GET)
    @ResponseBody
    public String findParentMenuAll(@RequestParam(value = "parentId") Long parentId) {
        Result result = new Result();
        try {
            List<BackResources> list = backResourcesService.selectMenu(parentId);
            if (!CheckUtil.isEmpty(list)) {
                result.setCode(200);
                result.setT(list);
            } else {
                result.setCode(404);
                result.setMsg("暂无数据");
            }
        } catch (Exception e) {
            logger.error("BackResources/findParentMenuAll error|parentId={}|ex={}", parentId, ErrorWriterUtil.WriteError(e).toString());
            result = new Result(500, "服务器异常");
        }
        logger.info("request /base/resources/findParentMenuAll result={}", gson.toJson(result));
        return gson.toJson(result);
    }

    @RequestMapping(value = "/updateLevel")
    @ResponseBody
    public String updateLevel(@RequestParam(value = "resId") Long resId,
                              @RequestParam(value = "level") Integer level) {
        Result result;
        try {
            BackResources resources = new BackResources();
            resources.setId(resId);
            resources.setLevel(level);
            result = backResourcesService.addOrUpdateItem(resources);
        } catch (Exception e) {
            logger.error("backUser/updateLevel error|ex={}", ErrorWriterUtil.WriteError(e).toString());
            result = new Result(500, "服务器异常");
        }
        return gson.toJson(result);
    }


}
