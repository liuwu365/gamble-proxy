package com.lottery.gamble.proxy.web.service.impl;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.google.gson.Gson;
import com.lottery.gamble.dao.NoticeRecordMapper;
import com.lottery.gamble.entity.NoticeRecord;
import com.lottery.gamble.entity.Page;
import com.lottery.gamble.entity.Result;
import com.lottery.gamble.proxy.core.util.ErrorWriterUtil;
import com.lottery.gamble.proxy.web.service.NoticeRecordService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.List;

@Service
public class NoticeRecordServiceImpl implements NoticeRecordService {
    private static final Logger LOGGER = LoggerFactory.getLogger(NoticeRecordServiceImpl.class);

    private static final Gson GSON = new Gson();

    @Resource
    private NoticeRecordMapper noticeRecordMapper;

    @Override
    @Transactional
    public Result addItem(NoticeRecord record) {

        boolean b = false;
        Result result = new Result<>();
        try {
            b = this.noticeRecordMapper.insertSelective(record) == 1;
            if (b) {
                result = new Result(200, "ok");
            } else {
                result = new Result(400, "操作失败");
            }
            return result;
        } catch (Exception e) {
            LOGGER.error("add NoticeRecord error|NoticeRecord={}|ex={}", GSON.toJson(record), ErrorWriterUtil.WriteError(e).toString());
            throw new RuntimeException();
        }
    }


    @Override
    public Page<NoticeRecord> getPage(Page page) {

        PageHelper.startPage((int) page.getPage(), page.getLimit());
        List<NoticeRecord> noticeRecords = this.noticeRecordMapper.selectPage(page);
        PageInfo pageInfo = new PageInfo(noticeRecords);
        page.setResult(pageInfo.getList());
        page.setTotal(pageInfo.getTotal());
        return page;
    }

    @Override
    public Page<NoticeRecord> getAppPage(Page page) {
        PageHelper.startPage((int) page.getPage(), page.getLimit());
        List<NoticeRecord> noticeRecords = this.noticeRecordMapper.selectAppPage(page);
        PageInfo pageInfo = new PageInfo(noticeRecords);
        page.setResult(pageInfo.getList());
        page.setTotal(pageInfo.getTotal());
        return page;
    }

    @Override
    public List<String> getUserList(NoticeRecord noticeRecord) {

        return this.noticeRecordMapper.selectUserList(noticeRecord);
    }

    @Override
    public NoticeRecord getItem(Long id) {

        return this.noticeRecordMapper.selectByPrimaryKey(id);
    }

    @Override
    public Result delItem(Long id) {

        int i = this.noticeRecordMapper.deleteByPrimaryKey(id);
        if (i != 1)
            return new Result(400, "删除失败");
        return new Result(200, "删除成功");
    }

}
