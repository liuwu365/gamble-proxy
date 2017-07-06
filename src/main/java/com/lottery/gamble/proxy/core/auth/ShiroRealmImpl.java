package com.lottery.gamble.proxy.core.auth;

import com.lottery.gamble.dao.BackResourcesMapper;
import com.lottery.gamble.dao.BackUserMapper;
import com.lottery.gamble.entity.BackUser;
import com.lottery.gamble.entity.TreeObject;
import com.lottery.gamble.proxy.core.constant.BasicConstant;
import com.lottery.gamble.proxy.core.util.CheckUtil;
import com.lottery.gamble.proxy.core.util.SessionUtil;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.*;
import org.apache.shiro.authz.AuthorizationInfo;
import org.apache.shiro.authz.SimpleAuthorizationInfo;
import org.apache.shiro.realm.AuthorizingRealm;
import org.apache.shiro.session.Session;
import org.apache.shiro.subject.PrincipalCollection;
import org.apache.shiro.util.ByteSource;

import javax.annotation.Resource;
import java.util.List;

/**
 * Created by Administrator on 2016/6/27.
 */
public class ShiroRealmImpl extends AuthorizingRealm {

    @Resource
    private BackUserMapper backUserMapper;

    @Resource
    private BackResourcesMapper backResourcesMapper;

    /**
     * 只有需要验证权限时才会调用, 授权查询回调函数, 进行鉴权但缓存中无用户的授权信息时调用.在配有缓存的情况下，只加载一次.
     */
    @Override
    protected AuthorizationInfo doGetAuthorizationInfo(PrincipalCollection principalCollection) {
        String loginName = SecurityUtils.getSubject().getPrincipal().toString();
        if (loginName != null) {
            Long userId = SessionUtil.getUserId();
            List<TreeObject> list = this.backResourcesMapper.selectUserList(userId);
            // 权限信息对象info,用来存放查出的用户的所有的角色（role）及权限（permission）
            SimpleAuthorizationInfo info = new SimpleAuthorizationInfo();
            // 用户的角色对应的所有权限，如果只使用角色定义访问权限
            for (int i = 0; i < list.size(); i++) {
                TreeObject treeObject = list.get(i);
                info.addStringPermission(treeObject.getResKey());
            }
            return info;
        }
        return null;
    }

    @Override
    protected AuthenticationInfo doGetAuthenticationInfo(AuthenticationToken authenticationToken) throws AuthenticationException {
        String accountName = authenticationToken.getPrincipal().toString().toLowerCase();
        BackUser backUser = backUserMapper.selectByAccountName(accountName);
        if (!CheckUtil.isEmpty(backUser)) {
            if (backUser.getLocked()) {
                throw new LockedAccountException(); // 帐号锁定
            }
            SimpleAuthenticationInfo authenticationInfo = new SimpleAuthenticationInfo(
                    accountName, // 用户名
                    backUser.getPassword(), // 密码
                    ByteSource.Util.bytes(accountName + "" + backUser.getCredentialsSalt()),// salt=username+salt
                    getName() // realm name
            );
            // 当验证都通过后，把用户信息放在session里
            Session session = SecurityUtils.getSubject().getSession();
            session.setAttribute(BasicConstant.BACK_USER_INFO, backUser);
            return authenticationInfo;
        } else {
            throw new UnknownAccountException();// 没找到帐号
        }
    }
}
