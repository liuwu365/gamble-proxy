package com.lottery.gamble.proxy.web.controller;


import com.google.gson.Gson;
import com.lottery.gamble.entity.NoticeAffiche;
import com.lottery.gamble.entity.Page;
import com.lottery.gamble.entity.Result;
import com.lottery.gamble.proxy.core.util.ErrorWriterUtil;
import com.lottery.gamble.proxy.web.service.NoticeAfficheService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.propertyeditors.CustomDateEditor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.text.SimpleDateFormat;
import java.util.Date;


@RequestMapping("/notice/affiche")
@Controller
public class NoticeAfficheController {

    private static final Logger logger = LoggerFactory.getLogger(NoticeAfficheController.class);

    private final Gson gson = new Gson();

    @Resource
    private NoticeAfficheService noticeAfficheService;

    @InitBinder
    protected void initBinder(WebDataBinder binder) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm");
        binder.registerCustomEditor(Date.class, new CustomDateEditor(dateFormat, true));
    }

    @RequestMapping(value = "/add", method = RequestMethod.GET)
    public String clientSend() {
        return "model/notice/affiche/add";
    }

    @RequestMapping(value = "/save", method = RequestMethod.POST)
    @ResponseBody
    public String clientSend(NoticeAffiche noticeAffiche) {

        Result result = new Result();
        logger.info("request /notice/affiche/save NoticeAffiche={}", gson.toJson(noticeAffiche));
        try {
            result = this.noticeAfficheService.addOrUpdateItem(noticeAffiche);
        } catch (Exception e) {
            result = new Result(500, "服务器异常");
        }
        logger.info("request /notice/affiche/save result={}", gson.toJson(result));
        return gson.toJson(result);
    }

    @RequestMapping(value = "/edit", method = RequestMethod.GET)
    public String edit(Model model, @RequestParam Long id) {

        NoticeAffiche item = this.noticeAfficheService.getItem(id);
        model.addAttribute("noticeAffiche", item);
        return "model/notice/affiche/edit";
    }

    @RequestMapping(value = "/list")
    public String list(Model model, Page<NoticeAffiche> page) {

        try {
            page = this.noticeAfficheService.getPage(page);
            model.addAttribute("page", page);
        } catch (Exception e) {
            logger.error("/notice/affiche/list error|page={}|ex={}", gson.toJson(page), ErrorWriterUtil.WriteError(e).toString());
        }
        return "model/notice/affiche/list";
    }

    @RequestMapping(value = "/delete")
    @ResponseBody
    public String delete(@RequestParam Long id) {

        logger.info("request /notice/affiche/delete id={}", gson.toJson(id));
        Result result = new Result();
        try {
            result = this.noticeAfficheService.delItem(id);
        } catch (Exception e) {
            logger.error("/notice/affiche/delete error|id={}|ex={}", id, ErrorWriterUtil.WriteError(e).toString());
            result = new Result(500, "服务器异常");
        }
        logger.info("request /notice/affiche/delete result={}", gson.toJson(result));
        return gson.toJson(result);
    }
}
