package com.lottery.gamble.proxy.web.service.permission;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.google.gson.Gson;
import com.lottery.gamble.dao.BackResourcesMapper;
import com.lottery.gamble.dao.OperationLogMapper;
import com.lottery.gamble.entity.BackResources;
import com.lottery.gamble.entity.Page;
import com.lottery.gamble.entity.Result;
import com.lottery.gamble.entity.TreeObject;
import com.lottery.gamble.proxy.core.constant.BasicConstant;
import com.lottery.gamble.proxy.core.enums.OperLog;
import com.lottery.gamble.proxy.core.util.CheckUtil;
import com.lottery.gamble.proxy.core.util.ErrorWriterUtil;
import com.lottery.gamble.proxy.core.util.TreeUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.List;

@Service
public class BackResourcesServiceImpl implements BackResourcesService {
    private static final Logger LOGGER = LoggerFactory.getLogger(BackResourcesServiceImpl.class);

    private static final Gson GSON = new Gson();

    @Resource
    private BackResourcesMapper backResourcesMapper;
    @Resource
    private OperationLogMapper logMapper;

    @Override
    @Transactional
    public Result<Long> addOrUpdateItem(BackResources backResources) {

        boolean res = false;
        Result result = new Result<>();
        String sucMsg = BasicConstant.ADD_SUCCESS;
        String faiMsg = BasicConstant.ADD_FAILED;
        if (CheckUtil.isEmpty(backResources.getId())) {
            //新增
            try {
                res = this.backResourcesMapper.insertSelective(backResources) == 1;
            } catch (Exception e) {
                LOGGER.error("add BackResources error|backResources={}|ex={}", GSON.toJson(backResources), ErrorWriterUtil.WriteError(e).toString());
                throw new RuntimeException();
            }
        } else {
            //修改
            try {
                res = this.backResourcesMapper.updateByPrimaryKeySelective(backResources) == 1;
                sucMsg = BasicConstant.UPDATE_SUCCESS;
                faiMsg = BasicConstant.UPDATE_FAILED;
            } catch (Exception e) {
                LOGGER.error("update BackResources error|backResources={}|ex={}", GSON.toJson(backResources), ErrorWriterUtil.WriteError(e).toString());
                throw new RuntimeException();
            }
        }


        return res ? Result.success(sucMsg, backResources.getId()) : Result.failure(faiMsg);
    }

    @Override
    public BackResources getItem(Long id) {

        return this.backResourcesMapper.selectByPrimaryKey(id);
    }

    @Override
    public List<TreeObject> getList() {

        List<TreeObject> backResourceses = this.backResourcesMapper.selectList();
        return backResourceses;
    }

    @Override
    public Page<BackResources> getPage(Page page) {

        PageHelper.startPage((int) page.getPage(), page.getLimit());
        List<BackResources> list = this.backResourcesMapper.selectPage(page);
        PageInfo pageInfo = new PageInfo(list);
        page.setResult(pageInfo.getList());
        page.setTotal(pageInfo.getTotal());
        return page;

    }


    @Override
    public Result delItem(Long id) {

        try {
            boolean res = this.backResourcesMapper.deleteByPrimaryKey(id) == 1;
            return res ? Result.success(BasicConstant.DELETE_SUCCESS) : Result.failure(BasicConstant.DELETE_FAILED);
        } catch (Exception e) {
            LOGGER.error("delItem error|id={}|ex={}", id, ErrorWriterUtil.WriteError(e).toString());
            throw new RuntimeException();
        }
    }

    @Override
    public List<TreeObject> getUserList(Long userId) {

        List<TreeObject> list = this.backResourcesMapper.selectUserList(userId);
        TreeUtil treeUtil = new TreeUtil();
        list = treeUtil.getChildTreeObjects(list, 0);
        return list;
    }

    @Override
    public List<BackResources> selectMenu(Long parentId) {
        return backResourcesMapper.selectMenu(parentId);
    }


}
