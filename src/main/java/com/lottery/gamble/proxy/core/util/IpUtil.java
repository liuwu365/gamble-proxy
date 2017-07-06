package com.lottery.gamble.proxy.core.util;


import com.google.gson.Gson;
import org.apache.log4j.Logger;

import javax.servlet.http.HttpServletRequest;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;

/**
 * IP工具类
 *
 *
 */
public class IpUtil {

    /**
     * Logger for this class
     */
    private static Logger logger = Logger.getLogger(Logger.class);

    public static final String IPADDRESS = "0:0:0:0:0:0:0:1";            //获取资源

    static final  Gson gson = new Gson();

    /**
     * 获取请求主机IP地址,如果通过代理进来，则透过防火墙获取真实IP地址;
     *
     * @param request
     * @return
     * @throws java.io.IOException
     */
    public final static String getIpAddress(HttpServletRequest request) throws IOException {
        // 获取请求主机IP地址,如果通过代理进来，则透过防火墙获取真实IP地址


        String ip = request.getHeader("X-Forwarded-For");
        if (logger.isDebugEnabled()) {
            logger.info("getIpAddress(HttpServletRequest) - X-Forwarded-For - String ip=" + ip);
        }

        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
                ip = request.getHeader("Proxy-Client-IP");
                if (logger.isDebugEnabled()) {
                    logger.info("getIpAddress(HttpServletRequest) - Proxy-Client-IP - String ip=" + ip);
                }
            }
            if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
                ip = request.getHeader("WL-Proxy-Client-IP");
                if (logger.isDebugEnabled()) {
                    logger.info("getIpAddress(HttpServletRequest) - WL-Proxy-Client-IP - String ip=" + ip);
                }
            }
            if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
                ip = request.getHeader("HTTP_CLIENT_IP");
                if (logger.isDebugEnabled()) {
                    logger.info("getIpAddress(HttpServletRequest) - HTTP_CLIENT_IP - String ip=" + ip);
                }
            }
            if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
                ip = request.getHeader("HTTP_X_FORWARDED_FOR");
                if (logger.isDebugEnabled()) {
                    logger.info("getIpAddress(HttpServletRequest) - HTTP_X_FORWARDED_FOR - String ip=" + ip);
                }
            }
            if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
                ip = request.getRemoteAddr();
                if (logger.isDebugEnabled()) {
                    logger.info("getIpAddress(HttpServletRequest) - getRemoteAddr - String ip=" + ip);
                }
            }
        } else if (ip.length() > 15) {
            String[] ips = ip.split(",");
            for (int index = 0; index < ips.length; index++) {
                String strIp = (String) ips[index];
                if (!("unknown".equalsIgnoreCase(strIp))) {
                    ip = strIp;
                    break;
                }
            }
        }
        return ip;
    }

    /**
     * 通过IP获取地址
     *
     * @param ip
     * @return
     */
    public static String getIpInfo(String ip) {
        String info = "";
        try {
            URL url = new URL("http://ip.taobao.com/service/getIpInfo.php?ip=" + ip);
            HttpURLConnection htpcon = (HttpURLConnection) url.openConnection();
            htpcon.setRequestMethod("GET");
            htpcon.setDoOutput(true);
            htpcon.setDoInput(true);
            htpcon.setUseCaches(false);

            InputStream in = htpcon.getInputStream();
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(in));
            StringBuffer temp = new StringBuffer();
            String line = bufferedReader.readLine();
            while (line != null) {
                temp.append(line).append("\r\n");
                line = bufferedReader.readLine();
            }
            bufferedReader.close();
            IpEntity ipEntity = gson.fromJson(temp.toString(), IpEntity.class);
            if (ipEntity.getCode() == 0) {
                IpEntity.Data data = ipEntity.getData();
                info += data.getCountry() + " ";
                info += data.getRegion() + " ";
                info += data.getCity() + " ";
                info += data.getIsp();
            }
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (ProtocolException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return info;
    }

    public static class IpEntity {

        /**
         * code : 0
         * data : {"country":"中国","country_id":"CN","area":"西北","area_id":"600000","region":"宁夏回族自治区","region_id":"640000","city":"银川市","city_id":"640100","county":"","county_id":"-1","isp":"联通","isp_id":"100026","ip":"42.63.14.209"}
         */

        private int code;
        /**
         * country : 中国
         * country_id : CN
         * area : 西北
         * area_id : 600000
         * region : 宁夏回族自治区
         * region_id : 640000
         * city : 银川市
         * city_id : 640100
         * county :
         * county_id : -1
         * isp : 联通
         * isp_id : 100026
         * ip : 42.63.14.209
         */

        private Data data;

        public int getCode() {
            return code;
        }

        public void setCode(int code) {
            this.code = code;
        }

        public Data getData() {
            return data;
        }

        public void setData(Data data) {
            this.data = data;
        }

        public static class Data {
            private String country;
            private String country_id;
            private String area;
            private String area_id;
            private String region;
            private String region_id;
            private String city;
            private String city_id;
            private String county;
            private String county_id;
            private String isp;
            private String isp_id;
            private String ip;

            public String getCountry() {
                return country;
            }

            public void setCountry(String country) {
                this.country = country;
            }

            public String getCountry_id() {
                return country_id;
            }

            public void setCountry_id(String country_id) {
                this.country_id = country_id;
            }

            public String getArea() {
                return area;
            }

            public void setArea(String area) {
                this.area = area;
            }

            public String getArea_id() {
                return area_id;
            }

            public void setArea_id(String area_id) {
                this.area_id = area_id;
            }

            public String getRegion() {
                return region;
            }

            public void setRegion(String region) {
                this.region = region;
            }

            public String getRegion_id() {
                return region_id;
            }

            public void setRegion_id(String region_id) {
                this.region_id = region_id;
            }

            public String getCity() {
                return city;
            }

            public void setCity(String city) {
                this.city = city;
            }

            public String getCity_id() {
                return city_id;
            }

            public void setCity_id(String city_id) {
                this.city_id = city_id;
            }

            public String getCounty() {
                return county;
            }

            public void setCounty(String county) {
                this.county = county;
            }

            public String getCounty_id() {
                return county_id;
            }

            public void setCounty_id(String county_id) {
                this.county_id = county_id;
            }

            public String getIsp() {
                return isp;
            }

            public void setIsp(String isp) {
                this.isp = isp;
            }

            public String getIsp_id() {
                return isp_id;
            }

            public void setIsp_id(String isp_id) {
                this.isp_id = isp_id;
            }

            public String getIp() {
                return ip;
            }

            public void setIp(String ip) {
                this.ip = ip;
            }
        }
    }

}