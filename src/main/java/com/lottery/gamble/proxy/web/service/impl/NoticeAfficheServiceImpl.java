package com.lottery.gamble.proxy.web.service.impl;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.google.gson.Gson;
import com.lottery.gamble.proxy.core.util.CheckUtil;
import com.lottery.gamble.proxy.web.service.NoticeAfficheService;
import com.lottery.gamble.dao.NoticeAfficheMapper;
import com.lottery.gamble.entity.NoticeAffiche;
import com.lottery.gamble.entity.Page;
import com.lottery.gamble.entity.Result;
import com.lottery.gamble.proxy.core.util.ErrorWriterUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.List;

@Service
public class NoticeAfficheServiceImpl implements NoticeAfficheService {
    private static final Logger LOGGER = LoggerFactory.getLogger(NoticeAfficheServiceImpl.class);

    private static final Gson GSON = new Gson();

    @Resource
    private NoticeAfficheMapper noticeAfficheMapper;

    @Override
    @Transactional
    public Result<Long> addOrUpdateItem(NoticeAffiche noticeAffiche) {

        boolean b = false;
        Result result = new Result<>();
        if (CheckUtil.isEmpty(noticeAffiche.getaId())) {
            //新增
            try {
                b = this.noticeAfficheMapper.insertSelective(noticeAffiche) == 1;
                if (b) {
                    result = new Result(200, "添加成功", noticeAffiche.getaId());
                } else {
                    result = new Result(400, "添加失败");
                }
            } catch (Exception e) {
                if (e instanceof DuplicateKeyException)
                    return new Result<>(401, "已有该类型公告，请重新添加");
                LOGGER.error("add NoticeAffiche error|noticeAffiche={}|ex={}", GSON.toJson(noticeAffiche), ErrorWriterUtil.WriteError(e).toString());
                throw new RuntimeException();
            }
        } else {
            //修改
            try {
                b = this.noticeAfficheMapper.updateByPrimaryKeySelective(noticeAffiche) == 1;
                if (b) {
                    result = new Result(200, "修改成功", noticeAffiche.getaId());
                } else {
                    result = new Result(400, "修改失败");
                }
            } catch (Exception e) {
                if (e instanceof DuplicateKeyException)
                    return new Result<>(401, "已有该类型公告，请重新添加");
                LOGGER.error("update NoticeAffiche error|noticeAffiche={}|ex={}", GSON.toJson(noticeAffiche), ErrorWriterUtil.WriteError(e).toString());
                throw new RuntimeException();
            }
        }
        return result;
    }

    @Override
    public NoticeAffiche getItem(Long id) {

        return this.noticeAfficheMapper.selectByPrimaryKey(id);
    }

    @Override
    public Page<NoticeAffiche> getPage(Page page) {

        PageHelper.startPage((int) page.getPage(), page.getLimit());
        List<NoticeAffiche> itemList = this.noticeAfficheMapper.selectPage(page);
        PageInfo pageInfo = new PageInfo(itemList);
        page.setResult(pageInfo.getList());
        page.setTotal(pageInfo.getTotal());
        return page;
    }

    @Override
    @Transactional
    public Result delItem(Long id) {

        try {
            Result result = new Result();
            int i = this.noticeAfficheMapper.deleteByPrimaryKey(id);
            if (i == 1) {
                result = new Result(200, "删除成功");
            } else {
                result = new Result(400, "删除失败");
            }
            return result;
        } catch (Exception e) {
            LOGGER.error("delItem error|id={}|ex={}", id, ErrorWriterUtil.WriteError(e).toString());
            throw new RuntimeException();
        }
    }


}
