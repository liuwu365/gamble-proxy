package com.lottery.gamble.proxy.core.util;


import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 *
 * 验证类
 */
public class CommonUtil {
    /**
     * 手机号码验证
     * @param content
     * @return
     */
    public static boolean handleMobile(String content) {
        String str= "^[1][3,4,5,8,9][0-9]{9}$";
        Pattern p = Pattern.compile(str);
        Matcher m = p.matcher(content);
        return m.matches();
    }

    /**
     * 邮箱验证
     * @param email
     * @return
     */
    public static  boolean handleEmail(String email){
        String reg = "[\\w]+@[\\w]+.[\\w]+";
        Pattern p = Pattern.compile(reg);
        Matcher m = p.matcher(email);
        return m.matches();
    }

    /**
     * 对汉字的长度进行验证等yes 的时候满足输入的要求
     * @param kanji  汉字的类容
     * @param count  需要验证的长度
     * @return
     */
    public static String  handleLeng(String kanji ,int count){
        String regEx = "[\u4e00-\u9fa5]";
        Pattern p = Pattern.compile(regEx);
        int num = 0;//汉字长度
        for(int i=0;i<kanji.length();i++){
            if(p.matches(regEx, kanji.substring(i, i + 1))){
                num++;
            }
        }
        if(num>count){
           return "king-size";
        }
        if(num<0){
            return "not-null";
        }
        return "yes";
    }

    /**
     * 性别的验证
     *
     */
    public static  boolean handleSex(String Sex){
        String reg = "^[\\u7537\\u5973]+$";
        Pattern p = Pattern.compile(reg);
        Matcher m = p.matcher(Sex);
        return m.matches();
    }

    /**
     * 实体类转成MAP
     */
    public static Map<String,Object> ConvertObjToMap(Object obj){
        Map<String,Object> reMap = new HashMap<String,Object>();
        if (obj == null){
            return null;
        }

        Field[] fields = obj.getClass().getDeclaredFields();
        try {
            for(int i=0;i<fields.length;i++){
                try {
                    Field f = obj.getClass().getDeclaredField(fields[i].getName());
                    f.setAccessible(true);
                    Object o = f.get(obj);
                    if(o != null && !o.equals("")){
                        reMap.put(fields[i].getName(), o);
                    }
                } catch (NoSuchFieldException e) {
                    e.printStackTrace();
                } catch (IllegalArgumentException e) {
                    e.printStackTrace();
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                }
            }
        } catch (SecurityException e) {
            e.printStackTrace();
        }
        return reMap;
    }

  /*  *//**
     * 状态的验证
     *
     *//*
    public static  boolean handleSex(String Sex){
        String reg = " ^[\\u4E00-\\uFA29\\uE7C7-\\uE7F3]+-[已发布为发布]$";
        Pattern p = Pattern.compile(reg);
        Matcher m = p.matcher(Sex);
        return m.matches();
    }*/

}
