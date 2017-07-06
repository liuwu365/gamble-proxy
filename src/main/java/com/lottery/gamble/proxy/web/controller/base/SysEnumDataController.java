package com.lottery.gamble.proxy.web.controller.base;

import com.google.gson.Gson;
import com.lottery.gamble.common.constant.SymbolConstant;
import com.lottery.gamble.common.util.CacheUtil;
import com.lottery.gamble.entity.Page;
import com.lottery.gamble.entity.Result;
import com.lottery.gamble.entity.common.SysEnumData;
import com.lottery.gamble.proxy.core.util.ErrorWriterUtil;
import com.lottery.gamble.proxy.web.service.SysEnumDataService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;
import java.sql.SQLException;

/**
 * Created by Administrator on 2016/12/5.
 */
@Controller
@RequestMapping("/configManage/enumData")
public class SysEnumDataController  extends  BaseController{

    private static final Logger logger = LoggerFactory.getLogger(SysEnumDataController.class);

    private static final CacheUtil cache = CacheUtil.getInstance();

    private static final String SYSENUMDATA = "sys:";

    private final String sysEnumDataPage="base/config/list";
    private final String addSysEnumData="base/config/add";
    private final String editSysEnumData="base/config/edit";

    @Resource
    private SysEnumDataService sysEnumDataService;


    @RequestMapping(value = "/sysEnumDataList")
    public String sysEnumDataList(Model model,Page page) throws Exception {
        logger.info("request /model/config.manage/list.html page={}",new Gson().toJson(page));
        page = sysEnumDataService.findPage(page);
        model.addAttribute("page", page);
        logger.info("list result={}", GSON.toJson(page));
        return sysEnumDataPage;
    }


    /**
     * 添加
     * @param
     * @return
     */

    @RequestMapping(value = "/add", method = RequestMethod.GET)
    public String addSysEnumData(Model model) {
        return addSysEnumData;
    }


    @RequestMapping(value = "/addSysEnumData", method = RequestMethod.POST)
    @ResponseBody
    public Result addSysEnumData(SysEnumData sysEnumData) {
        Result meta = new Result();
        try {
            int count = sysEnumDataService.insertSelective(sysEnumData);
            if(count>0){
                meta.setCode(200);
                meta.setMsg("添加成功！");
            } else{
                meta.setCode(500);
                meta.setMsg("添加失败！");
            }
        } catch (Exception e) {
            logger.error("add content error|ex={}", ErrorWriterUtil.WriteError(e).toString());
            meta.setCode(500);
            meta.setMsg("服务器异常");
            return meta;
        }
        return meta;
    }



    @RequestMapping(value = "/editView", method = RequestMethod.GET)
    public String editGet(Model model, @RequestParam(value = "id", required = false) Long id) {
        logger.info("request /model/config.manage/edit_sys_enum_data.html id={}", id);
        model.addAttribute("sysEnumData", sysEnumDataService.getItem(id));
        return editSysEnumData;
    }


    @RequestMapping(value = "/edit", method = RequestMethod.POST)
    @ResponseBody
    public boolean editPost(SysEnumData sysEnumData) {
        logger.info("request /model/config.manage/edit.json sysEnumData={}", GSON.toJson(sysEnumData));
        int result = 0;
        try {
            SysEnumData sysEnumDataOld = sysEnumDataService.selectByPrimaryKey(sysEnumData.getId());
            cache.del(SYSENUMDATA + sysEnumDataOld.getDataType() + SymbolConstant.MINUS_SIGN + sysEnumDataOld.getEnumKey());
            cache.del(sysEnumDataOld.getDataType());
            result = sysEnumDataService.updateByPrimaryKeySelective(sysEnumData);
        } catch (Exception e) {
            if (e instanceof SQLException) {
                logger.error("sql error");
            }
            logger.error("editPost sysEnumData error|sysEnumData={}|ex={}", GSON.toJson(sysEnumData), ErrorWriterUtil.WriteError(e).toString());
        }
        return result == 1;
    }

    /**
     * 删除
     * @param
     * @return
     */
    @RequestMapping(value = "/deleteSysEnumData")
    @ResponseBody
    public String deleteSysEnumData(@RequestParam(value="id") Long id) {
        Result meta = new Result();
        try {
            SysEnumData sysEnumData = sysEnumDataService.selectByPrimaryKey(id);
            cache.del(SYSENUMDATA + sysEnumData.getDataType() + SymbolConstant.MINUS_SIGN + sysEnumData.getEnumKey());
            cache.del(sysEnumData.getDataType());
            int count = sysEnumDataService.deleteByPrimaryKey(id);
            if (count > 0) {
                meta.setCode(200);
                meta.setMsg("操作成功");
            } else {
                meta.setCode(500);
                meta.setMsg("操作失败");
            }
        } catch (Exception e) {
            logger.error("deleteSysEnumData error|ex={}", ErrorWriterUtil.WriteError(e).toString());
            meta.setCode(500);
            meta.setMsg("服务器异常");
            return GSON.toJson(meta);
        }
        return GSON.toJson(meta);
    }


    @RequestMapping(value = "/updateJudge")
    @ResponseBody
    public String updateJudge(@RequestParam(value = "id") Long id,@RequestParam(value = "enumValue") String enumValue)throws Exception {
        Result meta = new Result();
        try {
            SysEnumData sysEnumData = sysEnumDataService.selectByPrimaryKey(id);
            cache.del(SYSENUMDATA + sysEnumData.getDataType() + SymbolConstant.MINUS_SIGN + sysEnumData.getEnumKey());
            SysEnumData c=new SysEnumData();
            c.setEnumValue(enumValue);
            c.setId(id);
            boolean flag =  sysEnumDataService.updateByPrimaryKeySelective(c)==1;
            if(flag){
                meta.setCode(200);
                meta.setMsg("操作成功");
            }else {
                meta.setCode(500);
                meta.setMsg("操作失败");
            }
        }catch (Exception e){
            logger.error("updateJudge error|ex={}", ErrorWriterUtil.WriteError(e).toString());
            meta.setCode(500);
            meta.setMsg("服务器异常");
            return GSON.toJson(meta);
        }
        return GSON.toJson(meta);
    }
}
