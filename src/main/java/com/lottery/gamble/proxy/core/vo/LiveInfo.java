package com.lottery.gamble.proxy.core.vo;

import java.util.List;

/**
 * Created by wx on 2017/4/13.
 */
public class LiveInfo {
/*

    "enable": 1,
            "game_type": "dota2",
            "is_followed": 0,
            "live_id": "10006",
            "live_img": "http://i8.pdim.gs/90/8d58c6b14bf46ed810b1b7422847aa02/w338/h190.jpg",
            "live_name": "panda",
            "live_nickname": "错觉老中医",
            "live_online": 12995,
            "live_title": "错觉  最近我有点强啊",
            "live_type": "panda",
            "live_userimg": "http://i8.pdim.gs/577fa9006d6b3fc4d984536ededf75ec.jpeg",
            "offline_time": "1492006214.0809",
            "online_time": "1492050082.0203",
            "push_time": "1491879096.6912",
            "sort_num": 2599,
            "sort_weight": -1,
            "stream_list": [
    {
        "type": "超清",
            "url": "http://pl8.live.panda.tv/live_panda/01c2c68ccbe6566f4ee177609af090cd.flv?sign=79b8756c6ed4cbcd795ea3f94503fba8&ts=58ef14e5&rid=-6037129"
    }
    ]
*/


    public int enable;

    public String game_type;

    public int is_followed;

    public String live_id;

    public String live_img;

    public String live_name;

    public String live_nickname;

    public String live_online;

    public String live_title;

    public String live_type;

    public String live_userimg;

    public List<LiveMsg> stream_list;

}
