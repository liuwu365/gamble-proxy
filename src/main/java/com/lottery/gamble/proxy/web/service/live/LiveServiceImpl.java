package com.lottery.gamble.proxy.web.service.live;

import com.google.gson.Gson;
import com.lottery.gamble.proxy.core.vo.LiveResult;
import com.lottery.gamble.common.util.CheckUtil;
import com.lottery.gamble.common.util.HttpClientUtil;
import com.lottery.gamble.dao.SysEnumDataMapper;
import com.lottery.gamble.entity.common.SysEnumData;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by wx on 2017/4/13.
 */
@Service
public class LiveServiceImpl implements LiveService {

    private  static  final Gson  GSON = new Gson();

    private  static  final Logger  LOGGER = LoggerFactory.getLogger(LiveServiceImpl.class);

    private  static final String KEY = "MAX+";


    @Resource
    private SysEnumDataMapper sysEnumDataMapper;


    @Override
    public LiveResult getLiveUrl(String roomId, String live_name, String game_name) {
        LiveResult liveUrl = this.findLiveUrl(roomId, live_name, game_name);
        return liveUrl;
    }




    private  LiveResult findLiveUrl(String roomId, String live_name, String game_name){
        SysEnumData sysEnumData = sysEnumDataMapper.selectEnumValues2(KEY);
        if(CheckUtil.isEmpty(sysEnumData)){
            return  null;
        }

        //String  testUrl  ="http://api.maxjia.com/api/live/detail/";
        String url = sysEnumData.getEnumValue();
        Map<String, String> paramsMap  = new HashMap<>();

        paramsMap.put("live_type",live_name);
        paramsMap.put("live_id",roomId);
        paramsMap.put("actual_game_type",game_name);
        paramsMap.put("lang","zh-cn");
        paramsMap.put("os_type","android");
        paramsMap.put("os_version","8.3.1");
        paramsMap.put("_time","1492053183");
        paramsMap.put("device_id","395C1334-4369-4234-9C4D-89A207987488");
        paramsMap.put("game_type",game_name);
        paramsMap.put("max__id","1");

        String s = HttpClientUtil.get(url, paramsMap);
        LOGGER.info("get live  url  result={}",s);
        LiveResult result = GSON.fromJson(s, LiveResult.class);
        return  result;
    }



}
