package com.lottery.gamble.proxy.core.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.ImportResource;

/**
 * Created by liuyuanzhou on 8/31/16.
 */
@ImportResource({
        "classpath:prod/application-dao.xml"
})
@Import(SpringCommonConfig.class)
public class SpringProdConfig {

    private static final Logger logger = LoggerFactory.getLogger(SpringDevConfig.class);

    public SpringProdConfig() {
        logger.info("load prod env");
    }
}
