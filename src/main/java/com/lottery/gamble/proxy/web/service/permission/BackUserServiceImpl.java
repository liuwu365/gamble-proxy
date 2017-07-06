package com.lottery.gamble.proxy.web.service.permission;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.google.gson.Gson;
import com.lottery.gamble.dao.BackUserLoginMapper;
import com.lottery.gamble.dao.BackUserMapper;
import com.lottery.gamble.dao.BackUserRoleMapper;
import com.lottery.gamble.entity.*;
import com.lottery.gamble.proxy.core.constant.BasicConstant;
import com.lottery.gamble.proxy.core.util.*;
import org.apache.shiro.authc.SimpleAuthenticationInfo;
import org.apache.shiro.authc.UsernamePasswordToken;
import org.apache.shiro.authc.credential.HashedCredentialsMatcher;
import org.apache.shiro.util.ByteSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Service
public class BackUserServiceImpl implements BackUserService {
    private static final Logger LOGGER = LoggerFactory.getLogger(BackUserServiceImpl.class);

    private static final Gson GSON = new Gson();

    @Resource
    private BackUserMapper backUserMapper;

    @Resource
    private BackUserRoleMapper backUserRoleMapper;

    @Resource
    private BackUserLoginMapper backUserLoginMapper;

    @Value("${admin.account.original.password}")
    private String originalPassword;
    @Override
    @Transactional
    public Result<Long> addOrUpdateItem(BackUser user) {

        //user.setAccountName(user.getAccountName().toLowerCase());
        boolean res = false;
        String sucMsg = BasicConstant.ADD_SUCCESS;
        String faiMsg = BasicConstant.ADD_FAILED;
        if (CheckUtil.isEmpty(user.getId())) {
            //新增
            try {
                PasswordHelper passwordHelper = new PasswordHelper();
                passwordHelper.encryptPassword(user);
                res = this.backUserMapper.insertSelective(user) == 1;
            } catch (Exception e) {
                LOGGER.error("add BackUser error|user={}|ex={}", GSON.toJson(user), ErrorWriterUtil.WriteError(e).toString());
                throw new RuntimeException();
            }
        } else {
            //修改
            try {
                res = this.backUserMapper.updateByPrimaryKeySelective(user) == 1;
                sucMsg = BasicConstant.UPDATE_SUCCESS;faiMsg = BasicConstant.UPDATE_FAILED;
            } catch (Exception e) {
                LOGGER.error("update BackUser error|user={}|ex={}", GSON.toJson(user), ErrorWriterUtil.WriteError(e).toString());
                throw new RuntimeException();
            }
        }
        return res?Result.success(sucMsg,user.getId()):Result.failure(faiMsg);
    }

    @Override
    public BackUser getItem(Long id) {

        return this.backUserMapper.selectByPrimaryKey(id);
    }

    @Override
    public Page<BackUser> getPage(Page page) {

        PageHelper.startPage((int) page.getPage(), page.getLimit());
        List<BackUser> itemList = this.backUserMapper.selectPage(page);
        PageInfo pageInfo = new PageInfo(itemList);
        page.setResult(pageInfo.getList());
        page.setTotal(pageInfo.getTotal());
        return page;
    }

    @Override
    public Page<BackUserLogin> getLoginPage(Page page) {

        PageHelper.startPage((int) page.getPage(), page.getLimit());
        List<BackUserLogin> itemList = this.backUserLoginMapper.selectPage(page);
        PageInfo pageInfo = new PageInfo(itemList);
        page.setResult(pageInfo.getList());
        page.setTotal(pageInfo.getTotal());
        return page;
    }

    @Override
    @Transactional
    public Result addUserLoginRecord(HttpServletRequest request) {

        BackUserLogin backUserLogin = new BackUserLogin();
        BackUser backUserInfo = SessionUtil.getCurrentUser();
        backUserLogin.setUserId(backUserInfo.getId());
        backUserLogin.setAccountName(backUserInfo.getAccountName());
        try {
            String ipAddress = IpUtil.getIpAddress(request);
            backUserLogin.setLoginIp(ipAddress);
        } catch (IOException e) {
            LOGGER.error("addUserLoginRecord getIP error|backUserInfo={}|ex={}", GSON.toJson(backUserInfo), ErrorWriterUtil.WriteError(e).toString());
        }
        int i = this.backUserLoginMapper.insertSelective(backUserLogin);
        if (i != 1)
            return new Result(400, "failed");
        return new Result(200, "ok");
    }

    @Override
    @Transactional
    public Result delItem(Long id) {

        try {
            Result result = new Result();
            boolean res = this.backUserMapper.deleteByPrimaryKey(id) == 1;
            return res?Result.success(BasicConstant.DELETE_SUCCESS):Result.failure(BasicConstant.DELETE_FAILED);
        } catch (Exception e) {
            LOGGER.error("delItem error|id={}|ex={}", id, ErrorWriterUtil.WriteError(e).toString());
            throw new RuntimeException();
        }
    }

    @Override

    public Result isExisit(String accountName) {

        BackUser backUser = this.backUserMapper.selectByAccountName(accountName);
        return CheckUtil.isEmpty(backUser)?Result.success(false):Result.success(true);
    }

    @Override
    @Transactional
    public Result saveUserRoleRelation(String roles, Long userId) {

        int i1 = this.backUserRoleMapper.delByUserId(userId);
        if (CheckUtil.isEmpty(roles))
            return Result.success();

        List<BackUserRoleKey> list = new ArrayList<>();
        String[] arr = roles.split(",");
        for (int i = 0; i < arr.length; i++) {
            Long roleId = Long.valueOf(arr[i]);
            BackUserRoleKey backUserRoleKey = new BackUserRoleKey();
            backUserRoleKey.setRoleId(roleId);
            backUserRoleKey.setUserId(userId);
            list.add(backUserRoleKey);
        }
        int i = this.backUserRoleMapper.insertBatch(list);
        if (i == -1 || i1 == -1)
            throw new RuntimeException();
        return Result.success();
    }

    @Override
    public Result getUserRoleList(Long userId) {

        List<BackUserRoleKey> backUserRoleKeys = this.backUserRoleMapper.selectByUserId(userId);
        return Result.success(backUserRoleKeys);
    }

    @Override
    @Transactional
    public Result changePassword(BackUser user, String password, String nPassword) {

        try {
            if (CheckUtil.isEmpty(user))
                return Result.failure(BasicConstant.IDENTITY_EXPIRED);
            boolean isOk = this.authen(user, password);
            if (!isOk)
                return Result.failure(BasicConstant.OLD_PASSWORD_ERROR);
            PasswordHelper passwordHelper = new PasswordHelper();
            user.setPassword(nPassword);
            passwordHelper.encryptPassword(user);
            boolean res = this.backUserMapper.updateByPrimaryKeySelective(user) == 1;
            return res?Result.success(BasicConstant.CHANGE_PASSWORD_SUCCESS):Result.failure(BasicConstant.CHANGE_PASSWORD_FAILED);
        } catch (Exception e) {
            LOGGER.error("changePassword error|user={}|password={}|nPassword={}|ex={}", user, password, nPassword, ErrorWriterUtil.WriteError(e).toString());
            throw new RuntimeException();
        }
    }

    @Override
    public Result resetPassword(Long userId) {

        try {
            BackUser backUser = this.getItem(userId);
            backUser.setPassword(CheckUtil.isEmpty(originalPassword)?"123456":originalPassword);
            PasswordHelper passwordHelper = new PasswordHelper();
            passwordHelper.encryptPassword(backUser);
            boolean res = this.backUserMapper.updateByPrimaryKeySelective(backUser) == 1;
            return res?Result.success(BasicConstant.RESET_PASSWORD_SUCCESS):Result.failure(BasicConstant.RESET_PASSWORD_FAILED);
        } catch (Exception e) {
            LOGGER.error("resetPassword error|userId={}|ex={}", userId, ErrorWriterUtil.WriteError(e).toString());
            throw new RuntimeException();
        }
    }

    /**
     * 校验原密码
     *
     * @param user
     * @param password
     * @return
     */
    private boolean authen(BackUser user, String password) {
        HashedCredentialsMatcher matcher = new HashedCredentialsMatcher("md5");
        matcher.setHashIterations(2);
        matcher.setStoredCredentialsHexEncoded(true);
        user.setAccountName(user.getAccountName().toLowerCase());
        UsernamePasswordToken token = new UsernamePasswordToken(user.getAccountName(), password);
        SimpleAuthenticationInfo authenticationInfo = new SimpleAuthenticationInfo(
                user.getAccountName(), // 用户名
                user.getPassword(), // 密码
                ByteSource.Util.bytes(user.getAccountName() + "" + user.getCredentialsSalt()),// salt=username+salt
                "com.lottery.gamble.manage.core.auth.ShiroRealmImpl_0" // realm name
        );
        return matcher.doCredentialsMatch(token, authenticationInfo);
    }


    public List<BackUser> selectUserListByGroupId(Long groupId) {
        return backUserMapper.selectUserListByGroupId(groupId);
    }

}
