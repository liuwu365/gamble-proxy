package com.lottery.gamble.proxy.web.service.cms;

import com.lottery.gamble.dao.CmsCarouselMapper;
import com.lottery.gamble.entity.CmsCarousel;
import com.lottery.gamble.entity.Page;
import com.lottery.gamble.proxy.core.util.CheckUtil;
import com.lottery.gamble.proxy.web.service.BaseService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;

/**
 * @Description: 轮播服务
 * @User: liuwu_eva@163.com
 * @Date: 2016-08-15 下午 6:34
 */
@Service
public class CmsCarouselService implements BaseService<CmsCarousel> {

    @Resource
    private CmsCarouselMapper cmsCarouselMapper;

    @Override
    public Page<CmsCarousel> findPage(Page<CmsCarousel> page) {
        long L = cmsCarouselMapper.selectCount(page.getFilter());
        page.setTotal(CheckUtil.isEmpty(L) ? 0 : L);
        List<CmsCarousel> list = cmsCarouselMapper.selectPage(page);
        page.setResult(list);
        return page;
    }

    @Override
    public List<CmsCarousel> selectAll() {
        return null;
    }

    @Override
    public int deleteByPrimaryKey(Long id) {
        return cmsCarouselMapper.deleteByPrimaryKey(id);
    }

    @Override
    public int insert(CmsCarousel obj) {
        return cmsCarouselMapper.insert(obj);
    }

    @Override
    public int insertSelective(CmsCarousel obj) {
        return cmsCarouselMapper.insertSelective(obj);
    }

    @Override
    public CmsCarousel selectByPrimaryKey(Long id) {
        return cmsCarouselMapper.selectByPrimaryKey(id);
    }

    @Override
    public int updateByPrimaryKeySelective(CmsCarousel obj) {
        return cmsCarouselMapper.updateByPrimaryKeySelective(obj);
    }

    @Override
    public int updateByPrimaryKey(CmsCarousel obj) {
        return cmsCarouselMapper.updateByPrimaryKey(obj);
    }

    public int updateStatus(Map<String, Object> map) {
        return cmsCarouselMapper.updateStatus(map);
    }

}
