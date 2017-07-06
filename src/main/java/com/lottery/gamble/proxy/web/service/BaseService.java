package com.lottery.gamble.proxy.web.service;

import com.lottery.gamble.entity.Page;

import java.util.List;

/**
 * 服务基类
 */
public interface BaseService<T> {


    //    支持分页查找
    Page<T> findPage(Page<T> page);

    //查询所有不分页
    List<T> selectAll();

    /**
     * 根据id删除
     */
    int deleteByPrimaryKey(Long id);

    int insert(T obj);

    int insertSelective(T obj);

    T selectByPrimaryKey(Long id);

    int updateByPrimaryKeySelective(T obj);

    int updateByPrimaryKey(T obj);

}
