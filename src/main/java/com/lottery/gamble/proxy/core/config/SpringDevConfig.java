package com.lottery.gamble.proxy.core.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.ImportResource;

/**
 * Created by liuyuanzhou on 4/19/16.
 */
@ImportResource({
        "classpath:dev/application-dao.xml"
})
@Import(SpringCommonConfig.class)
public class SpringDevConfig {

    private static final Logger logger = LoggerFactory.getLogger(SpringDevConfig.class);

    public SpringDevConfig() {
        logger.info("load dev env");
    }
}
