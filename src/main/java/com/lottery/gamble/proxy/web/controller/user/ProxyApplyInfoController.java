package com.lottery.gamble.proxy.web.controller.user;

import com.google.gson.Gson;
import com.lottery.gamble.entity.Page;
import com.lottery.gamble.proxy.web.controller.base.BaseController;
import com.lottery.gamble.proxy.web.service.user.ProxyApplyInfoService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * Created by Administrator on 2017/7/6.
 */
@Controller
@RequestMapping("/lottery/user")
public class ProxyApplyInfoController extends BaseController {
    private static final Logger logger = LoggerFactory.getLogger(UserInfoController.class);

    private static final Gson gson = new Gson();

    @Autowired
    private ProxyApplyInfoService proxyApplyInfoService;

    private final String auditList="model/user/gaming_agency_audit_list";


   /* @RequestMapping(value = "/agencyList", method = RequestMethod.GET)
    public String agencyList() {
        return agencyList;
    }
*/

    @RequestMapping(value = "/auditList")
    public String auditList(Model model,Page page) throws Exception {
        logger.info("request /model/user/gaming_lottery_user_list.html page={}",new Gson().toJson(page));
        page = proxyApplyInfoService.findPage(page);
        model.addAttribute("pageList", page);
        logger.info("list result={}", gson.toJson(page));
        return auditList;
    }
}
