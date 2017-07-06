package com.lottery.gamble.proxy.web.controller.financing;

import com.google.gson.Gson;
import com.lottery.gamble.entity.Result;
import com.lottery.gamble.entity.quiz.PayChannels;
import com.lottery.gamble.enums.financing.*;
import com.lottery.gamble.proxy.core.util.ErrorWriterUtil;
import com.lottery.gamble.proxy.web.controller.base.BaseController;
import com.lottery.gamble.proxy.web.service.financing.FinancingService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;

/**
 * User: zc
 * Date: 2017/6/23
 */
@Controller
@RequestMapping("/financial")
public class FinancingController extends BaseController {

    Logger logger = LoggerFactory.getLogger(FinancingController.class);

    static final Gson gson = new Gson();

    private static final String payChannels = "/model/financial/channel_list";

    private static final String payChannelsEditView = "/model/financial/channel_edit_view";

    private static final String payChannelsPlaceApply = "/model/financial/place_apply_edit";

    @Value("${img.url.prefix}")
    private String img_url_prefix;
    @Resource
    private FinancingService financingService;

    @RequestMapping("/pay/channel/list")
    public String getPayChannel(Model model) {
        List<PayChannels> payChannelsList = financingService.getPayChannel();
        List<PayChannelLimitEnum> limitEnums = new ArrayList<>();
        limitEnums.add(PayChannelLimitEnum.TIME_TYPE);
        limitEnums.add(PayChannelLimitEnum.ALL_MONEY_TYPE);
        limitEnums.add(PayChannelLimitEnum.ONCE_MONET_TYPE);
        model.addAttribute("img_url_prefix", img_url_prefix);
        model.addAttribute("payChannelsList", payChannelsList);
        model.addAttribute("limitEnums", limitEnums);
        return payChannels;
    }

    @RequestMapping("/pay/channel/editView")
    public String payChannelEditView(Long id, Model model) {
        PayChannels payChannels = financingService.editChannelView(id);
        List<PayTypeEnum> payType = new ArrayList<>();
        payType.add(PayTypeEnum.ALIPAY);
        payType.add(PayTypeEnum.BANK_CARD);
        payType.add(PayTypeEnum.WECHAT);
        model.addAttribute("payChannels", payChannels);
        model.addAttribute("payType", payType);
        model.addAttribute("img_url_prefix", img_url_prefix);
        return payChannelsEditView;
    }

    @RequestMapping("/pay/channel/editSave")
    @ResponseBody
    public String payChannelEditSave(PayChannels payChannels) {
        try {
            logger.info("edit /financial/pay/channel/editSave", gson.toJson(payChannels));
            Result result = financingService.editChannel(payChannels);
            return gson.toJson(result);
        } catch (Exception ex) {
            logger.error("edit /financial/pay/channel/editSave error|ex={}", ErrorWriterUtil.WriteError(ex).toString());
            return gson.toJson(new Result<>(500, "修改失败！"));
        }
    }

    @RequestMapping("/pay/channelApply")
    public String payChannelApply(Long id, Model model) {
        List<WebPayChannelEnum> webList = new ArrayList<>();
        List<AppPayChannelEnum> appList = new ArrayList<>();
        webList.add(WebPayChannelEnum.PAY_CHANNEL1);
        webList.add(WebPayChannelEnum.PAY_CHANNEL2);
        webList.add(WebPayChannelEnum.PAY_CHANNEL3);
        webList.add(WebPayChannelEnum.PAY_CHANNEL4);
        appList.add(AppPayChannelEnum.PAY_CHANNEL1);
        appList.add(AppPayChannelEnum.PAY_CHANNEL2);
        appList.add(AppPayChannelEnum.PAY_CHANNEL3);
        PayChannels payChannels = financingService.editChannelView(id);
        model.addAttribute("payChannels", payChannels);
        model.addAttribute("webList", webList);
        model.addAttribute("appList", appList);
        return payChannelsPlaceApply;
    }

    @RequestMapping("/pay/channel/applySave")
    @ResponseBody
    public String applySave(Integer webCheckboxes, Integer appCheckboxes, Long id) {
        Result result = financingService.updateApplyPlace(webCheckboxes, appCheckboxes, id);
        return gson.toJson(result);
    }
}

