package com.lottery.gamble.proxy.core.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.ImportResource;

/**
 * Created by liuyuanzhou on 8/31/16.
 */
@ImportResource({
        "classpath:release/application-dao.xml"
})
@Import(SpringCommonConfig.class)
public class SpringReleaseConfig {

    private static final Logger logger = LoggerFactory.getLogger(SpringReleaseConfig.class);

    public SpringReleaseConfig() {
        logger.info("load release env");
    }
}
