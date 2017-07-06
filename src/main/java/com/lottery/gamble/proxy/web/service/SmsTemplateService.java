package com.lottery.gamble.proxy.web.service;

import com.lottery.gamble.dao.SmsTemplateMapper;
import com.lottery.gamble.entity.Page;
import com.lottery.gamble.entity.SmsTemplate;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

/**
 * @Description: SMS消息模版
 * @User: liuwu_eva@163.com
 * @Date: 2016-08-26 下午 4:22
 */
@Service
public class SmsTemplateService implements BaseService<SmsTemplate> {

    @Resource
    private SmsTemplateMapper smsTemplateMapper;

    @Override
    public Page<SmsTemplate> findPage(Page<SmsTemplate> page) {
        return null;
    }

    @Override
    public List<SmsTemplate> selectAll() {
        return null;
    }

    @Override
    public int deleteByPrimaryKey(Long id) {
        return smsTemplateMapper.deleteByPrimaryKey(id);
    }

    @Override
    public int insert(SmsTemplate obj) {
        return smsTemplateMapper.insert(obj);
    }

    @Override
    public int insertSelective(SmsTemplate obj) {
        return smsTemplateMapper.insertSelective(obj);
    }

    @Override
    public SmsTemplate selectByPrimaryKey(Long id) {
        return smsTemplateMapper.selectByPrimaryKey(id);
    }

    @Override
    public int updateByPrimaryKeySelective(SmsTemplate obj) {
        return smsTemplateMapper.updateByPrimaryKeySelective(obj);
    }

    @Override
    public int updateByPrimaryKey(SmsTemplate obj) {
        return smsTemplateMapper.updateByPrimaryKey(obj);
    }

    /**
     * 根据类型查询模版列表
     * @param type 1:短信 2:邮件
     * @return
     */
    public List<SmsTemplate> selectByType(Byte type){
        return smsTemplateMapper.selectByType(type);
    }

}
