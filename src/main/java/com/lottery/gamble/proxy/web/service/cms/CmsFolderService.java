package com.lottery.gamble.proxy.web.service.cms;

import com.lottery.gamble.dao.CmsFolderMapper;
import com.lottery.gamble.entity.CmsFolder;
import com.lottery.gamble.entity.Page;
import com.lottery.gamble.proxy.core.util.CheckUtil;
import com.lottery.gamble.proxy.web.service.BaseService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

/**
 * @Description: 栏目服务
 * @User: liuwu_eva@163.com
 * @Date: 2016-07-20 下午 5:34
 */
@Service
public class CmsFolderService implements BaseService<CmsFolder> {

    @Resource
    private CmsFolderMapper cmsFolderMapper;

    @Override
    public Page<CmsFolder> findPage(Page<CmsFolder> page) {
        long L = cmsFolderMapper.selectCount(page.getFilter());
        page.setTotal(CheckUtil.isEmpty(L) ? 0 : L);
        List<CmsFolder> list = cmsFolderMapper.selectPage(page);
        for (CmsFolder cmsFolder : list) {
            long id = cmsFolder.getParentid();
            //根据pid查询父目录名称
            if (id == 0) {
                cmsFolder.setPidName("根目录");
            } else {
                CmsFolder cmsFolder1 = cmsFolderMapper.selectByPrimaryKey(id);
                cmsFolder.setPidName(cmsFolder1.getName());
            }

        }
        page.setResult(list);
        return page;
    }

    @Override
    public List<CmsFolder> selectAll() {
        List<CmsFolder> folderList = cmsFolderMapper.selectAll();
        return folderList;
    }

    @Override
    public int deleteByPrimaryKey(Long id) {
        return cmsFolderMapper.deleteByPrimaryKey(id);
    }

    @Override
    public int insert(CmsFolder obj) {
        return cmsFolderMapper.insert(obj);
    }

    @Override
    public int insertSelective(CmsFolder obj) {
        return cmsFolderMapper.insertSelective(obj);
    }

    @Override
    public CmsFolder selectByPrimaryKey(Long id) {
        return cmsFolderMapper.selectByPrimaryKey(id);
    }

    @Override
    public int updateByPrimaryKeySelective(CmsFolder obj) {
        return cmsFolderMapper.updateByPrimaryKeySelective(obj);
    }

    @Override
    public int updateByPrimaryKey(CmsFolder obj) {
        return cmsFolderMapper.updateByPrimaryKey(obj);
    }

    //查询所有父级栏目
    public List<CmsFolder> selectParentFolder() {
        return cmsFolderMapper.selectParentFolder();
    }

    //查询当前栏目下是否有文章
    public int selectFolderExitContent(Long folderId) {
        return cmsFolderMapper.selectFolderExitContent(folderId);
    }


}
