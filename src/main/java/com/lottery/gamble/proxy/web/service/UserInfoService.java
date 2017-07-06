package com.lottery.gamble.proxy.web.service;


import com.lottery.gamble.dao.UserInfoMapper;
import com.lottery.gamble.entity.Page;
import com.lottery.gamble.entity.user.UserInfo;
import com.lottery.gamble.entity.user.UserResult;
import com.lottery.gamble.proxy.core.util.CheckUtil;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

/**
 * Created by Administrator on 2016/11/17 0017.
 */
@Service
public class UserInfoService implements BaseService<UserInfo> {

    @Resource
    private UserInfoMapper userInfoMapper;



    @Override
    public Page findPage(Page page) {
        long L = userInfoMapper.selectUserInfoCount(page);
        page.setTotal(CheckUtil.isEmpty(L) ? 0 : L);
        List<UserResult> list = userInfoMapper.selectByUserInfo(page);
        page.setResult(list);
        return page;
    }


    @Override
    public List<UserInfo> selectAll() {
        return null;
    }

    @Override
    public int deleteByPrimaryKey(Long id) {
        return userInfoMapper.deleteByPrimaryKey(id);
    }

    @Override
    public int insert(UserInfo obj) {
        return userInfoMapper.insert(obj);
    }

    @Override
    public int insertSelective(UserInfo obj) {
        return userInfoMapper.insertSelective(obj);
    }

    @Override
    public UserInfo selectByPrimaryKey(Long id) {
        return userInfoMapper.selectByPrimaryKey(id);
    }

    @Override
    public int updateByPrimaryKeySelective(UserInfo obj) {
        return userInfoMapper.updateByPrimaryKeySelective(obj);
    }

    @Override
    public int updateByPrimaryKey(UserInfo obj) {
        return userInfoMapper.updateByPrimaryKey(obj);
    }

}
