package com.lottery.gamble.proxy.web.service.cms;


import com.lottery.gamble.dao.CmsCarouselVersionMapper;
import com.lottery.gamble.entity.CmsCarouselVersion;
import com.lottery.gamble.entity.Page;
import com.lottery.gamble.proxy.web.service.BaseService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

/**
 * @Author 马明伟
 * @Date 2017/1/18
 * @Description
 */
@Service
public class CmsCarouselVersionService implements BaseService<CmsCarouselVersion> {

    @Resource
    private CmsCarouselVersionMapper cmsCarouselVersionMapper;

    @Override
    public Page<CmsCarouselVersion> findPage(Page<CmsCarouselVersion> page) {
        return null;
    }

    @Override
    public List<CmsCarouselVersion> selectAll() {
        return null;
    }

    @Override
    public int deleteByPrimaryKey(Long id) {
        return 0;
    }

    @Override
    public int insert(CmsCarouselVersion obj) {
        return cmsCarouselVersionMapper.insert(obj);
    }

    @Override
    public int insertSelective(CmsCarouselVersion obj) {
        return cmsCarouselVersionMapper.insertSelective(obj);
    }

    @Override
    public CmsCarouselVersion selectByPrimaryKey(Long id) {
        return cmsCarouselVersionMapper.selectByPrimaryKey(id);
    }

    @Override
    public int updateByPrimaryKeySelective(CmsCarouselVersion obj) {
        return cmsCarouselVersionMapper.updateByPrimaryKeySelective(obj);
    }

    @Override
    public int updateByPrimaryKey(CmsCarouselVersion obj) {
        return cmsCarouselVersionMapper.updateByPrimaryKey(obj);
    }

    public CmsCarouselVersion selectByCarouselType(String carouselType){return cmsCarouselVersionMapper.selectByCarouselType(carouselType);}
}
