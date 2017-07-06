package com.lottery.gamble.proxy.core.config;

import com.lottery.gamble.common.enums.work.WorkOrderTypeEnum;
import com.lottery.gamble.proxy.core.util.LogWriter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.autoconfigure.jdbc.DataSourceTransactionManagerAutoConfiguration;
import org.springframework.boot.autoconfigure.web.DispatcherServletAutoConfiguration;
import org.springframework.boot.autoconfigure.web.EmbeddedServletContainerAutoConfiguration;
import org.springframework.boot.autoconfigure.web.MultipartAutoConfiguration;
import org.springframework.boot.autoconfigure.web.ServerPropertiesAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.ImportResource;
import org.springframework.core.convert.converter.Converter;
import org.springframework.web.multipart.MultipartResolver;
import org.springframework.web.multipart.commons.CommonsMultipartResolver;
import org.springframework.web.servlet.DispatcherServlet;

import javax.servlet.http.HttpServletRequest;

/**
 * @author Created by wangyaping on 2016/9/12.
 */
@ImportResource({"classpath:application-context.xml",
        "classpath:application-dubbo-client.xml",
        "classpath:spring-shiro.xml"
})
@Import({ServerPropertiesAutoConfiguration.class,
        DispatcherServletAutoConfiguration.class,
        EmbeddedServletContainerAutoConfiguration.class})
@EnableAutoConfiguration(exclude = {DataSourceAutoConfiguration.class, MultipartAutoConfiguration.class, DataSourceTransactionManagerAutoConfiguration.class})
public class SpringCommonConfig {

    @Bean(name = DispatcherServlet.MULTIPART_RESOLVER_BEAN_NAME)
    @ConditionalOnMissingBean(MultipartResolver.class)
    public CommonsMultipartResolver multipartResolver() {
        return new CommonsMultipartResolver() {
            public boolean isMultipart(HttpServletRequest request) {
                String uri = request.getRequestURI();
                if (uri.indexOf("ueditor/jsp") > 0 && "post".equalsIgnoreCase(request.getMethod())) {
                    logger.info(uri);
                    return false;
                } else {
                    return super.isMultipart(request);
                }
            }
        };
    }

    @Bean
    public LogWriter logWriter(
            @Value("${log.write_to_file}") boolean writeToFile,
            @Value("${log.path}") String logPath,
            @Value("${log.number}") int logNumber
    ) {
        return new LogWriter(writeToFile, logPath, logNumber);
    }

    @Configuration
    public static class SpringToEnum implements Converter<String,WorkOrderTypeEnum> {

        @Override
        public WorkOrderTypeEnum convert(String source) {
            String value = source.trim();
            if ("".equals(value)) {
                return null;
            }
            return WorkOrderTypeEnum.get(Integer.valueOf(value));
        }
    }

}
