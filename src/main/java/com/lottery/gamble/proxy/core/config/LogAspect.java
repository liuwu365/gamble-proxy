package com.lottery.gamble.proxy.core.config;

import com.google.gson.Gson;
import com.lottery.gamble.proxy.core.util.LogWriter;
import com.lottery.gamble.common.util.JsonUtil;
import com.lottery.gamble.proxy.core.annotation.MethodLog;
import com.lottery.gamble.proxy.core.util.CheckUtil;
import com.lottery.gamble.proxy.core.util.ErrorWriterUtil;
import com.lottery.gamble.proxy.core.util.IpUtil;
import org.apache.shiro.SecurityUtils;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.lang.reflect.Method;
import java.text.SimpleDateFormat;
import java.util.Calendar;

/**
 * @author 王亚平
 *         Created by Administrator on 2016/6/30.
 */
@Component
@Aspect
@EnableAspectJAutoProxy(proxyTargetClass = true)
public class LogAspect {

    final Logger logger = LoggerFactory.getLogger(LogAspect.class);

    final String springCglib = "$$EnhancerBySpringCGLIB$$";

    Gson gson = new Gson();

    private String PACKAGES_ENTITY_PREFIX = "com.lottery.gamble";

    LogAspect() {
        //logger.info("Aop log ...");
    }


    @Resource
    private LogWriter logWriter;

    @Pointcut("execution(* com.lottery.gamble.manage.web.*.*.*(..))")
    private void pcMethod() {
    }

    ;

    @Pointcut("@annotation(com.lottery.gamble.proxy.core.annotation.MethodLog)")
    public void methodCachePointcut() {
    }

    @Before(value = "methodCachePointcut()")
    public void before() {
        //logger.info("before");
    }

    @AfterReturning(value = "methodCachePointcut()")
    public void after() {
        //logger.info("after");
    }


    @Around("methodCachePointcut()")
    public Object around(ProceedingJoinPoint point) throws Throwable {
        //logger.info("around");
        HttpServletRequest request = ((ServletRequestAttributes)
                RequestContextHolder.getRequestAttributes()).getRequest();

        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss E");
        Calendar calendar = Calendar.getInstance();
        String operDate = df.format(calendar.getTime());

        String ipAddress = IpUtil.getIpAddress(request);

        String principal = (String) SecurityUtils.getSubject().getPrincipal();

        String loginName, name;
        if (principal != null) {
            loginName = principal;
        } else {
            loginName = "匿名用户";
        }

        String methodRemark = getMethodRemark(point);
        String methodName = point.getSignature().getName();
        String packages = point.getThis().getClass().getName();
        int i = packages.indexOf(springCglib);
        if (i > -1) {
            //com.lottery.gamble.manage.web.controller.IndexController$$EnhancerBySpringCGLIB$$f1bebdaa
            packages = packages.substring(0, packages.indexOf("$$"));
        }

        String operatingContent = "";
        Object[] method_param = null;


        Object object = null;

        try {
            method_param = point.getArgs();

            object = point.proceed();
        } catch (Exception e) {
            throw e;
        }


        SysLog sysLog = new SysLog();
        sysLog.setIpAddress(ipAddress);
        sysLog.setLoginName(loginName);
        sysLog.setMethodName(packages.concat(".").concat(methodName));
        sysLog.setMethodRemark(methodRemark);
        String operContent = "";
        sysLog.setOperatingContent(methodRemark.concat(":") + "" + method_param[0]);

        logger.info("sysLog={}", JsonUtil.gsonFormat.toJson(sysLog));

        return object;
    }


    @Before(value = "pcMethod()")
    public void controllerBefore() {
        //logger.info("controllerBefore ...");
    }

    @AfterThrowing(pointcut = "pcMethod()", throwing = "ex")
    public void afterThrowing(JoinPoint joinPoint, Exception ex) throws Throwable {
        logger.info("afterThrowing");
        Object[] args = joinPoint.getArgs();
//        HttpServletRequest request = ((ServletRequestAttributes)
//                RequestContextHolder.getRequestAttributes()).getRequest();
//        try {
//            String ipAddress = IpUtil.getIpAddress(request);
//        } catch (IOException e) {
//            logger.info("IpUtil.getIpAddress(request) error");
//        }
        String params = "";
        if (!CheckUtil.isEmpty(args)) {
            for (int i = 0; i < args.length; i++) {
                String name = args[i].getClass().getName();
                if (!CheckUtil.isEmpty(name) && name.indexOf(PACKAGES_ENTITY_PREFIX) > -1) {
                    params += gson.toJson(args[i]) + ";";
                }
            }
        }
        String exceptionCode = ex.getClass().getName();//异常类型代码
        String exceptionDetail = ex.getMessage();//异常详细信息
        String className = joinPoint.getTarget().getClass().getName();
        String methodName = className + "." + joinPoint.getSignature().getName();
        ExceptionLog log = new ExceptionLog();
        log.setExceptionCode(exceptionCode);
        log.setExceptionDetail(exceptionDetail);
        log.setClassName(className);
        log.setMethodName(methodName);
        log.setParams(params);
        log.setStackTraceStr(ErrorWriterUtil.WriteError(ex).toString());
        String s = gson.toJson(log);
        logger.info(s);
        logWriter.write(s);
    }


    public static String getMethodRemark(ProceedingJoinPoint point) throws Exception {

        String targetName = point.getTarget().getClass().getName();
        String methodName = point.getSignature().getName();
        Object[] arguments = point.getArgs();


        Class<?> aClass = Class.forName(targetName);

        Method[] methods = aClass.getMethods();
        String remark = "";
        for (Method m : methods) {
            if (m.getName().equals(methodName)) {
                Class<?>[] parameterTypes = m.getParameterTypes();
                if (parameterTypes.length == arguments.length) {
                    MethodLog annotation = m.getAnnotation(MethodLog.class);
                    if (annotation != null) {
                        remark = annotation.remark();
                    }
                    break;
                }
            }
        }

        return remark;
    }


    public static class SysLog {

        private String loginName;
        private String ipAddress;
        private String methodName;
        private String methodRemark;
        private String operatingContent;

        public String getLoginName() {
            return loginName;
        }

        public void setLoginName(String loginName) {
            this.loginName = loginName;
        }

        public String getIpAddress() {
            return ipAddress;
        }

        public void setIpAddress(String ipAddress) {
            this.ipAddress = ipAddress;
        }

        public String getMethodName() {
            return methodName;
        }

        public void setMethodName(String methodName) {
            this.methodName = methodName;
        }

        public String getMethodRemark() {
            return methodRemark;
        }

        public void setMethodRemark(String methodRemark) {
            this.methodRemark = methodRemark;
        }

        public String getOperatingContent() {
            return operatingContent;
        }

        public void setOperatingContent(String operatingContent) {
            this.operatingContent = operatingContent;
        }
    }

    public static class ExceptionLog {
        private String className;
        private String methodName;
        private String exceptionCode;
        private String exceptionDetail;
        private String params;
        private String stackTraceStr;


        public String getClassName() {
            return className;
        }

        public void setClassName(String className) {
            this.className = className;
        }

        public String getMethodName() {
            return methodName;
        }

        public String getExceptionCode() {
            return exceptionCode;
        }

        public void setExceptionCode(String exceptionCode) {
            this.exceptionCode = exceptionCode;
        }

        public String getExceptionDetail() {
            return exceptionDetail;
        }

        public void setExceptionDetail(String exceptionDetail) {
            this.exceptionDetail = exceptionDetail;
        }

        public void setMethodName(String methodName) {
            this.methodName = methodName;
        }

        public String getParams() {
            return params;
        }

        public void setParams(String params) {
            this.params = params;
        }

        public String getStackTraceStr() {
            return stackTraceStr;
        }

        public void setStackTraceStr(String stackTraceStr) {
            this.stackTraceStr = stackTraceStr;
        }


    }


}
