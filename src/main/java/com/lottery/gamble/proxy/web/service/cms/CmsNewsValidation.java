package com.lottery.gamble.proxy.web.service.cms;

import com.google.gson.Gson;
import com.lottery.gamble.common.util.CheckUtil;
import com.lottery.gamble.entity.CmsContent;

import com.lottery.gamble.entity.Result;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @Description: 验证
 * @User: liuwu_eva@163.com
 * @Date: 2016-07-26 下午 4:52
 */
public class CmsNewsValidation {
    private final Logger logger = LoggerFactory.getLogger(CmsNewsValidation.class);

    private final Gson gson = new Gson();

    private static volatile CmsNewsValidation instance;
    private static Object object = new Object();

    public static CmsNewsValidation getInstance() {
        if (instance != null) {
            return instance;
        } else {
            synchronized (object) {
                if (instance == null) {
                    instance = new CmsNewsValidation();
                }
            }
            return instance;
        }
    }

    //验证文章提交的参数(在新增和编辑时用到)
    public boolean validContentParam(Result meta,CmsContent content){
        boolean flag = true;
        if(CheckUtil.isEmpty(content.getTitle())){
            meta.setCode(410);
            meta.setMsg("文章标题不能为空");
            flag = false;
        }else if (!CheckUtil.isEmpty(content.getLevel())) {
            if (content.getLevel()<0&&content.getLevel()>99){
                meta.setCode(411);
                meta.setMsg("推荐级别只能设置在0~99之间");
                flag = false;
            }
        }else if (CheckUtil.isEmpty(content.getFolderid())){
            meta.setCode(412);
            meta.setMsg("所属栏目不能为空");
            flag = false;
        }else if (CheckUtil.isEmpty(content.getStatus())){
            meta.setCode(413);
            meta.setMsg("发布状态不能为空");
            flag = false;
        }else if (!CheckUtil.isEmpty(content.getLinkerurl())&& !CheckUtil.isDomain(content.getLinkerurl())){
            meta.setCode(414);
            meta.setMsg("转向链接不是有效网址");
            flag = false;
        }
        return flag;
    }

}
