package com.lottery.gamble.proxy;

import com.lottery.gamble.proxy.core.config.SpringDevConfig;
import com.lottery.gamble.proxy.core.config.SpringProdConfig;
import com.lottery.gamble.proxy.core.config.SpringReleaseConfig;
import org.springframework.boot.SpringApplication;

/**
 * @author Created by liuyuanzhou on 4/15/16.
 * 后台程序启动类
 */
public class StartApplication {

    public static void main(String[] args) {
        String env = System.getProperty("yt.env", "dev");
        if(env.equalsIgnoreCase("dev")) {
            SpringApplication.run(SpringDevConfig.class);
        } else if (env.equalsIgnoreCase("release")){
            SpringApplication.run(SpringReleaseConfig.class);
        } else {
            SpringApplication.run(SpringProdConfig.class);
        }
        System.out.println("gamble-proxy start!");
    }
}
