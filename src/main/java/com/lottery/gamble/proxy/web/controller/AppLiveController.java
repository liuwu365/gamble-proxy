package com.lottery.gamble.proxy.web.controller;

import com.google.gson.Gson;

import com.lottery.gamble.proxy.web.service.live.AppLiveService;
import com.lottery.gamble.entity.LiveConfig;
import com.lottery.gamble.entity.Page;
import com.lottery.gamble.entity.Result;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;

/**
 * User: zc
 * Date: 2017/4/12
 */
@Controller
@RequestMapping("/applive")
public class AppLiveController {
    private static final Logger logger = LoggerFactory.getLogger(AppLiveController.class);

    private static final Gson GSON = new Gson();

    private static final String appList = "model/applive/live_list";

    private static final String edit = "model/applive/live_edit";

    private static final String add = "model/applive/live_add";
    @Value("${img.url.prefix}")
    private String img_url_prefix;

    @Resource
    private AppLiveService appLiveService;

    @RequestMapping("/live_list")
    public String getLiveList(Model model,Page page){
        page = appLiveService.selectPage(page);
        model.addAttribute("live",page);
        model.addAttribute("img_url_prefix",img_url_prefix);
        return appList;
    }

    @RequestMapping("/edit")
    public String edit(Model model,@RequestParam("id")Long id){
        LiveConfig liveConfig = appLiveService.selectById(id);
        model.addAttribute("live",liveConfig);
        model.addAttribute("img_url_prefix",img_url_prefix);
        return edit;
    }

    @RequestMapping(value = "add", method = RequestMethod.GET)
    public String add(Model model){
        model.addAttribute("img_url_prefix",img_url_prefix);
        return add;
    }

    /********************************暂时屏蔽*****************************************/
    /*@RequestMapping("addSave()")
    @ResponseBody
    public String addSave(LiveConfig liveConfig){
        Result result = appLiveService.insert(liveConfig);
        return GSON.toJson(result);
    }

    @RequestMapping("editSave()")
    @ResponseBody
    public String editSave(LiveConfig liveConfig){
        Result result = appLiveService.update(liveConfig);
        return GSON.toJson(result);
    }*/
    /********************************暂时屏蔽*****************************************/
    @RequestMapping("del")
    @ResponseBody
    public String del(@RequestParam("id")long id){
        Result result = appLiveService.del(id);
        return GSON.toJson(result);
    }
    @RequestMapping("addSave")
    @ResponseBody
    public String addSave(LiveConfig liveConfig){
        Result result = appLiveService.insertLive(liveConfig);
        return GSON.toJson(result);
    }

    @RequestMapping("editSave")
    @ResponseBody
    public String editSave(LiveConfig liveConfig){
        Result result = appLiveService.updateLive(liveConfig);
        return GSON.toJson(result);
    }
}
