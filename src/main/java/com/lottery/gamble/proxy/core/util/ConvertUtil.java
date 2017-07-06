package com.lottery.gamble.proxy.core.util;

import java.security.MessageDigest;
import java.text.NumberFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;


public class ConvertUtil {
    public static String encryptMD5(String str) throws Exception {
        byte[] data=str.getBytes();
        MessageDigest md5 = MessageDigest.getInstance("MD5");
        md5.update(data);
        byte b[] = md5.digest();
        int x;
        StringBuffer buf = new StringBuffer("");
        for (int i = 0; i < b.length; i++) {
            x = b[i];
            if (x < 0)
                x += 256;
            if (x < 16)
                buf.append("0");
            buf.append(Integer.toHexString(x));
        }
        System.out.println("32位加密后的字符串: " + buf.toString());// 32位的加密
        return  buf.toString();
    }
    public static String format(String formatStr,Date date)
    {
        if(CheckUtil.isEmpty(date))
        {
            return "";
        }

        return new SimpleDateFormat(formatStr).format(date);
    }

    public static String genNumber(int number){
        NumberFormat formatter = NumberFormat.getNumberInstance();
        formatter.setMinimumIntegerDigits(4);
        formatter.setGroupingUsed(false);
        String s = formatter.format(number);
        return s;
    }

    public static String genNextId(String maxId) {
        String prefix=maxId.replaceAll("\\d+","");
        String numStr=maxId.replace(prefix,"");
        NumberFormat formatter = NumberFormat.getNumberInstance();
        int intValue= 0;
        try {
            intValue = formatter.parse(numStr).intValue();
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return prefix+genNumber(intValue+1);
    }

    public static String genPrefixId(String prefix,int id){
        return prefix+genNumber(id);
    }
}
