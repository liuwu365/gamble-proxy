package com.lottery.gamble.proxy.web.controller;

import com.google.gson.Gson;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * Created by Administrator on 2017/6/26.
 */
@Controller
@RequestMapping("/lottery/user")
public class LotteryUserListController  {
    private static final Logger logger = LoggerFactory.getLogger(LotteryUserListController.class);

    private final Gson gson = new Gson();

    /*private final String lotteryUser="model/user/gaming_lottery_user_list";

    private final String agencyList="model/user/gaming_agency_list";

    private final String auditList="model/user/gaming_agency_audit_list";

    @RequestMapping(value = "/list", method = RequestMethod.GET)
    public String userList() {
        return lotteryUser;
    }

    @RequestMapping(value = "/agencyList", method = RequestMethod.GET)
      public String agencyList() {
        return agencyList;
    }

    @RequestMapping(value = "/auditList", method = RequestMethod.GET)
    public String auditList() {
        return auditList;
    }*/
}
