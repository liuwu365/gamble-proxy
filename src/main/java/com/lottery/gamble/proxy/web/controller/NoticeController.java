package com.lottery.gamble.proxy.web.controller;


import com.google.gson.Gson;
import com.lottery.gamble.proxy.core.vo.ListVo;
import com.lottery.gamble.proxy.web.service.NoticeRecordService;
import com.lottery.gamble.proxy.web.service.NoticeService;
import com.lottery.gamble.common.util.ErrorWriterUtil;
import com.lottery.gamble.entity.NoticeRecord;
import com.lottery.gamble.entity.Page;
import com.lottery.gamble.entity.Result;
import com.lottery.gamble.enums.ClientType;
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


@RequestMapping("/notice")
@Controller
public class NoticeController {

    private static final Logger logger = LoggerFactory.getLogger(NoticeController.class);

    private final Gson gson = new Gson();

    @Resource
    private NoticeService noticeService;

    @Resource
    private NoticeRecordService noticeRecordService;

    @InitBinder
    protected void initBinder(WebDataBinder binder) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm");
        binder.registerCustomEditor(Date.class, new CustomDateEditor(dateFormat, true));
    }

    @RequestMapping(value = "/client/send", method = RequestMethod.GET)
    public String clientSend() {
        return "model/notice/client/send";
    }

    @RequestMapping(value = "/client/send", method = RequestMethod.POST)
    @ResponseBody
    public String clientSend(NoticeRecord noticeRecord, ListVo listVo) {

        Result result = new Result();
        logger.info("request /notice/client/send noticeRecord={}|listVo={}", gson.toJson(noticeRecord), gson.toJson(listVo));
        try {
            result = this.noticeService.sendClientMsg(noticeRecord, listVo.getList());
        } catch (Exception e) {
            result = new Result(500, "服务器异常");
        }
        logger.info("request /notice/client/send result={}", gson.toJson(result));
        return gson.toJson(result);
    }

    @RequestMapping(value = "/client/list")
    public String list(Model model, Page page) {

        try {
            page = this.noticeRecordService.getPage(page);
            model.addAttribute("page", page);
        } catch (Exception e) {
            logger.error("/notice/client/list error|page={}|ex={}", gson.toJson(page), ErrorWriterUtil.WriteError(e).toString());
        }
        return "model/notice/client/list";
    }

    @RequestMapping(value = "/app/list")
    public String appList(Model model, Page page) {

        try {
            page = this.noticeRecordService.getAppPage(page);
            model.addAttribute("page", page);
        } catch (Exception e) {
            logger.error("/notice/client/list error|page={}|ex={}", gson.toJson(page), ErrorWriterUtil.WriteError(e).toString());
        }
        return "model/notice/app/list";
    }

    @RequestMapping(value = "/app/send", method = RequestMethod.GET)
    public String appSend() {
        return "model/notice/app/send";
    }

//    @RequestMapping(value = "/app/send", method = RequestMethod.POST)
//    @ResponseBody
//    public String appSend(NoticeRecord noticeRecord) {
//
//        Result result = new Result();
//        logger.info("request /notice/app/send noticeRecord={}", gson.toJson(noticeRecord));
//        try {
//            result = this.noticeService.sendAppMsg(noticeRecord);
//        } catch (Exception e) {
//            result = new Result(500, "服务器异常");
//            logger.error("request /notice/app/send result={}|error={}", gson.toJson(result), ErrorWriterUtil.WriteError(e));
//        }
//        logger.info("request /notice/app/send result={}", gson.toJson(result));
//        return gson.toJson(result);
//    }

    @RequestMapping(value = "/client/view")
    public String view(Model model, @RequestParam Long id) {
        String page = null;
        try {
            NoticeRecord noticeRecord = this.noticeRecordService.getItem(id);
            model.addAttribute("noticeRecord", noticeRecord);
            if (noticeRecord.getnType() == ClientType.ANDROID_CLIENT.getType() || noticeRecord.getnType() == ClientType.IPHONE_CLIENT.getType()){
                page = "model/notice/app/view";
            }else {
                page = "model/notice/client/view";
            }
        } catch (Exception e) {
            logger.error("/notice/client/view error|id={}|ex={}", id, ErrorWriterUtil.WriteError(e).toString());
        }
        return page;
    }

    @RequestMapping(value = "/client/delete")
    @ResponseBody
    public String delete(@RequestParam Long id) {

        logger.info("request /notice/client/delete id={}", gson.toJson(id));
        Result result = new Result();
        try {
            result = this.noticeRecordService.delItem(id);
        } catch (Exception e) {
            logger.error("noticeRecordService/delete error|id={}|ex={}", id, ErrorWriterUtil.WriteError(e).toString());
            result = new Result(500, "服务器异常");
        }
        logger.info("request /notice/client/delete result={}", gson.toJson(result));
        return gson.toJson(result);
    }

}
