package com.lottery.gamble.proxy.web.service.live;

import com.google.gson.Gson;
import com.lottery.gamble.proxy.core.vo.LiveMsg;
import com.lottery.gamble.proxy.core.vo.LiveResult;
import com.lottery.gamble.proxy.web.service.permission.BackInfoServiceImpl;
import com.lottery.gamble.common.util.CheckUtil;
import com.lottery.gamble.common.util.StringUtil;
import com.lottery.gamble.dao.LiveConfigMapper;
import com.lottery.gamble.dao.LiveStreamMapper;
import com.lottery.gamble.entity.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

/**
 * User: zc
 * Date: 2017/4/12
 */

@Service
public class AppLiveServiceImpl implements AppLiveService {
    private static final Logger LOGGER = LoggerFactory.getLogger(BackInfoServiceImpl.class);

    private static final Gson GSON = new Gson();

    @Resource
    private LiveConfigMapper liveConfigMapper;

    @Resource
    private LiveService liveService;

    @Resource
    private LiveStreamMapper liveStreamMapper;

    @Override
    public Page selectPage(Page page) {
        Integer integer = liveConfigMapper.selectCount(page.getFilter());
        page.setTotal(integer == null ? 0 : integer.intValue());
        List<LiveConfig> list =liveConfigMapper.selectPage(page);
        page.setResult(list);
        return page;
    }

    @Override
    public LiveConfig selectById(Long id) {
        //LiveConfig liveConfig = liveConfigMapper.selectByPrimaryKey(id);
        LiveConfig liveConfig = liveConfigMapper.selectConfigAndUrl(id);
        return liveConfig;
    }

    @Override
    public Result insert(LiveConfig liveConfig) {
        com.lottery.gamble.proxy.core.vo.LiveResult liveUrl = new LiveResult();
        LiveStream liveStream = new LiveStream();
        boolean flag = false;
        int i = 0;
        int i1 = 0;
        int i2 = 0;
        try {
            if (liveConfig.getLiveName() != null && liveConfig.getLiveId() != null
                    && liveConfig.getGameType() != null){
                 liveUrl = liveService.getLiveUrl(liveConfig.getLiveId().toString(), liveConfig.getLiveName(), liveConfig.getGameType());
            }
            if (!liveUrl.status.equals("failed")){
                liveConfig.setLiveNickname(liveUrl.result.live_nickname);
                liveConfig.setLiveImg(liveUrl.result.live_img);
                liveConfig.setLiveOnline(Long.valueOf(liveUrl.result.live_online).intValue());
                liveConfig.setLiveTitle(liveUrl.result.live_title);
                liveConfig.setLiveUserimg(liveUrl.result.live_userimg);
                liveConfig.setLiveName(liveUrl.result.live_name);
                if (!CheckUtil.isEmpty(liveUrl.result.stream_list)){

                    i = liveConfigMapper.insertSelective(liveConfig);
                    liveStream.setlId(liveConfig.getId());
                    List<LiveMsg> streamList = liveUrl.result.stream_list;
                    for (LiveMsg stream : streamList){
                        if (stream.type .equals("超清")){
                            liveStream.setsType(stream.type);
                            liveStream.setsUrl(stream.url);
                            liveStream.setlId(liveConfig.getId());
                            i1 = liveStreamMapper.insertSelective(liveStream);
                        }else {
                            liveStream.setsType(stream.type);
                            liveStream.setsUrl(stream.url);
                            liveStream.setlId(liveConfig.getId());
                            i2 = liveStreamMapper.insertSelective(liveStream);
                        }
                    }
                    if(liveConfig.getRecommend()==1){
                        liveConfigMapper.updateInRecommend(liveConfig.getId(), 0);
                    }
                }
                if (i > 0 && i1 > 0 && i2 > 0){
                    flag = true;
                }
                if (flag){
                    return new Result(200,"添加成功！");
                }else {
                    return new Result(400,"添加失败！");
                }
            }
        }catch (Exception ex){

        }

        return null;
    }

    @Override
    public Result update(LiveConfig liveConfig) {
        LiveStream liveStream = new LiveStream();
        try {
            if (liveConfig.getRecommend()==null && liveConfig.getSort() ==null && liveConfig.getLiveName() == null
                    && liveConfig.getLiveId() == null && liveConfig.getGameType() == null){
                return new Result(400,"bad request！");
            }

            LiveResult liveUrl = liveService.getLiveUrl(liveConfig.getLiveId().toString(), liveConfig.getLiveName(), liveConfig.getGameType());
            if (liveUrl.status.equals("failed")){
                return new Result(400,"当前主播没有开启直播！");
            }

            if (CheckUtil.isEmpty(liveUrl.result.stream_list)){
                return new Result(400,"无法获取当前主播的直播流");
            }

            liveConfig.setLiveNickname(liveUrl.result.live_nickname);
            liveConfig.setLiveImg(liveUrl.result.live_img);
            liveConfig.setLiveOnline(Long.valueOf(liveUrl.result.live_online).intValue());
            liveConfig.setLiveTitle(liveUrl.result.live_title);
            liveConfig.setLiveUserimg(liveUrl.result.live_userimg);
            liveConfig.setLiveName(liveUrl.result.live_name);

            int i = liveConfigMapper.updateByPrimaryKey(liveConfig);

            liveStream.setlId(liveConfig.getId());
            List<LiveMsg> streamList = liveUrl.result.stream_list;
            for (LiveMsg stream : streamList){
                if (stream.type .equals("超清")){
                    liveStreamMapper.updateByLiveId(stream.type,stream.url,liveConfig.getId());
                }else {
                    liveStreamMapper.updateByLiveId(stream.type,stream.url,liveConfig.getId());
                }
            }
            if(liveConfig.getRecommend()==1){
                liveConfigMapper.updateInRecommend(liveConfig.getId(), 0);
            }
            if (i > 0){
                return new Result(200,"修改成功！");
            }else {
                return new Result(400,"修改失败！");
            }
        }catch (Exception ex){
            LOGGER.error("update app live config  error :{}", StringUtil.stackTrace(ex));
            return new Result(500,"服务器异常");
        }
    }

    @Override
    public Result del(long id) {
        int i = liveConfigMapper.deleteByPrimaryKey(id);
        if (i > 0){
            List<LiveStream> liveStreams = liveStreamMapper.selectByLId(id);
            for (LiveStream liveStream : liveStreams){
                liveStreamMapper.deleteByPrimaryKey(liveStream.getsId());
            }
            return new Result(200,"删除成功！");
        }else {
            return new Result(400,"删除失败！");
        }
    }

    @Override
    public Result insertLive(LiveConfig liveConfig) {
        if (liveConfig.getLiveName() == null & liveConfig.getRecommend() == null && liveConfig.getLiveNickname() == null
                && liveConfig.getSort() == null && liveConfig.getsUrl() == null && liveConfig.getLiveImg() == null){
            return new Result(400,"bad request!");
        }
        int count = liveConfigMapper.insertSelective(liveConfig);
        if (count < 1){
            return new Result(400,"添加失败！");
        }
        if(liveConfig.getRecommend()==1){
            liveConfigMapper.updateInRecommend(liveConfig.getId(), 0);
        }
        LiveStream liveStream = new LiveStream();
        liveStream.setlId(liveConfig.getId());
        liveStream.setsUrl(liveConfig.getsUrl());
        liveStream.setsStream(liveConfig.getsStream());
        int count2 = liveStreamMapper.insertSelective(liveStream);
        if (count2 < 1){
            return new Result(400,"添加url失败！");
        }
        return new Result(200,"添加成功！");
    }

    @Override
    public Result updateLive(LiveConfig liveConfig) {
        if (liveConfig.getLiveName() == null & liveConfig.getRecommend() == null && liveConfig.getLiveNickname() == null
                && liveConfig.getSort() == null && liveConfig.getsUrl() == null && liveConfig.getLiveImg() == null){
            return new Result(400,"bad request!");
        }
        int i = liveConfigMapper.updateByPrimaryKey(liveConfig);
        if (i < 1){
            return new Result(400,"修改失败！");
        }
        if(liveConfig.getRecommend()==1){
            liveConfigMapper.updateInRecommend(liveConfig.getId(), 0);
        }
        LiveStream liveStream = new LiveStream();
        liveStream.setsUrl(liveConfig.getsUrl());
        liveStream.setlId(liveConfig.getId());
        liveStream.setsStream(liveConfig.getsStream());
        //int i1 = liveStreamMapper.updateByPrimaryKey(liveStream);
        int i1 = liveStreamMapper.updateByLid(liveStream);
        if (i1 < 1){
            return new Result(400,"修改url失败！");
        }
        return new Result(200,"修改成功！");
    }
}
