//SortList.java
package com.lottery.gamble.proxy.core.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
* 通用排序
*/
public class SortUtil<E> {


    private static final String DESC = "desc";

    private final Logger logger = LoggerFactory.getLogger(SortUtil.class);

    /**
     * 对List<E></b>
     * 泛型集合排序
     *
     * @param list   排序的List集合对象
     * @param method 排序列的get方法
     * @param sort   升序、降序
     */
    public void sort(List<E> list, final String method, final String sort) {
        Collections.sort(list, new Comparator() {
            public int compare(Object a, Object b) {
                int ret = 0;
                try {
                    Method m1 = ((E) a).getClass().getMethod(method, null);
                    Method m2 = ((E) b).getClass().getMethod(method, null);
                    if (sort != null && SortUtil.DESC.equals(sort))//倒序
                        ret = m2.invoke(((E) b), null).toString().compareTo(m1.invoke(((E) a), null).toString());
                    else//正序
                        ret = m1.invoke(((E) a), null).toString().compareTo(m2.invoke(((E) b), null).toString());
                } catch (NoSuchMethodException ne) {
                    logger.error(ErrorWriterUtil.WriteError(ne).toString());
                } catch (IllegalAccessException ie) {
                    logger.error(ErrorWriterUtil.WriteError(ie).toString());
                } catch (InvocationTargetException it) {
                    logger.error(ErrorWriterUtil.WriteError(it).toString());
                }
                return ret;
            }
        });
    }

    public void sort(List<E> list, final String method) {
        sort(list,method,DESC);
    }
}