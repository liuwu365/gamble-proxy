package com.lottery.gamble.proxy.web.service.cms;

import com.lottery.gamble.proxy.core.util.CheckUtil;
import com.lottery.gamble.dao.CmsContentMapper;
import com.lottery.gamble.entity.CmsContent;
import com.lottery.gamble.entity.Page;
import com.lottery.gamble.proxy.web.service.BaseService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;

/**
 * @ClassName: CmsContentService
 * @Package com.lottery.gamble.api
 * @Description: 文章服务
 * @author: liuwu1189@dingtalk.com
 * @date: 2016-07-20 17:36:17
 */
@Service
public class CmsContentService implements BaseService<CmsContent> {

    @Resource
    private CmsContentMapper cmsContentMapper;

    @Override
    public Page<CmsContent> findPage(Page<CmsContent> page) {
        long L = cmsContentMapper.selectCount(page.getFilter());
        page.setTotal(CheckUtil.isEmpty(L) ? 0 : L);
        List<CmsContent> list = cmsContentMapper.selectPage(page);
        page.setResult(list);
        return page;
    }

    @Override
    public List<CmsContent> selectAll() {
        List<CmsContent> contentList = cmsContentMapper.selectAll();
        return contentList;
    }

    @Override
    public int deleteByPrimaryKey(Long id) {
        return cmsContentMapper.deleteByPrimaryKey(id);
    }

    public int deleteByIds(Map<String, Object> map) {
        return cmsContentMapper.deleteByIds(map);
    }

    @Override
    public int insert(CmsContent obj) {
        return cmsContentMapper.insert(obj);
    }

    @Override
    public int insertSelective(CmsContent obj) {
        return cmsContentMapper.insertSelective(obj);
    }

    @Override
    public CmsContent selectByPrimaryKey(Long id) {
        return cmsContentMapper.selectByPrimaryKey(id);
    }

    @Override
    public int updateByPrimaryKeySelective(CmsContent obj) {
        return cmsContentMapper.updateByPrimaryKeySelective(obj);
    }

    @Override
    public int updateByPrimaryKey(CmsContent obj) {
        return cmsContentMapper.updateByPrimaryKey(obj);
    }

    public int updateStatus(Map<String, Object> map) {
        return cmsContentMapper.updateStatus(map);
    }

    public int updateApp(Map<String, Object> map) {
        return cmsContentMapper.updateApp(map);
    }

}

