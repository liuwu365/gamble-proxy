package com.lottery.gamble.proxy.web.controller.cms;

import com.google.gson.Gson;
import com.lottery.gamble.common.util.CheckUtil;
import com.lottery.gamble.entity.CmsCarousel;
import com.lottery.gamble.entity.CmsCarouselVersion;
import com.lottery.gamble.entity.Page;
import com.lottery.gamble.entity.Result;
import com.lottery.gamble.enums.OperLog;
import com.lottery.gamble.proxy.core.util.ErrorWriterUtil;
import com.lottery.gamble.proxy.web.controller.base.BaseController;
import com.lottery.gamble.proxy.web.service.cms.CmsCarouselService;
import com.lottery.gamble.proxy.web.service.cms.CmsCarouselVersionService;
import com.lottery.gamble.proxy.web.service.log.OperationLogService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * @Description: CMS资讯模块-->轮播控制器
 * @User: liuwu_eva@163.com
 * @Date: 2016-08-15 下午 6:12
 */
@Controller
@RequestMapping("/news/carousel")
public class CmsCarouselController extends BaseController {
    private static final Logger logger = LoggerFactory.getLogger(CmsCarouselController.class);
    private static final Gson gson = new Gson();
    @Value("${base.url}")
    private String news_prefix;
    @Autowired
    private CmsCarouselService cmsCarouselService;
    @Resource
    private CmsCarouselVersionService cmsCarouselVersionService;
    @Resource
    private OperationLogService operationLogService;

    private final String carousel_listPage = "model/news/carousel/carousel_list";
    private final String carousel_addPage = "model/news/carousel/carousel_add";
    private final String carousel_editPage = "model/news/carousel/carousel_edit";

    //查询列表页面
    @RequestMapping(value = "/carousel_list")
    public String carouselList(Model model, Page<CmsCarousel> page) {
        logger.info("request /news/carousel_list.html page={}", gson.toJson(page));

        page = cmsCarouselService.findPage(page);
        model.addAttribute("carousel", page);
        model.addAttribute("news_prefix", news_prefix);

        logger.info("list result={}", gson.toJson(page));
        return carousel_listPage;
    }

    //跳转到新增页面
    @RequestMapping(value = "/carousel_add", method = RequestMethod.GET)
    public String carouselAdd(Model model) {
        model.addAttribute("news_prefix", news_prefix);
        return carousel_addPage;
    }

    //新增操作
    @RequestMapping(value = "/carousel_add", method = RequestMethod.POST)
    @ResponseBody
    public Result carouselAdd(HttpServletRequest request, CmsCarousel cmsCarousel) {
        Result meta = new Result();
        if (!CheckUtil.isEmpty(cmsCarousel.getNeedVersion())) {
            CmsCarouselVersion cmsCarouselVersion = cmsCarouselVersionService.selectByCarouselType(cmsCarousel.getType());
            if (CheckUtil.isEmpty(cmsCarouselVersion)) {
                CmsCarouselVersion cmsCarouselVersion1 = new CmsCarouselVersion();
                cmsCarouselVersion1.setCarouselType(cmsCarousel.getType());
                cmsCarouselVersion1.setCarouselVersion(1);
                cmsCarouselVersionService.insertSelective(cmsCarouselVersion1);
            } else {
                cmsCarouselVersion.setCarouselVersion(cmsCarouselVersion.getCarouselVersion() + 1);
                cmsCarouselVersionService.updateByPrimaryKeySelective(cmsCarouselVersion);
            }
        }
        try {
            int result = cmsCarouselService.insertSelective(cmsCarousel);
            if (result > 0) {
                meta.setCode(200);
                meta.setMsg("操作成功");
                operationLogService.addNormalOperLog(request, OperLog.NEWS, String.format("新增轮播图id:%s", result));
            } else {
                meta.setCode(500);
                meta.setMsg("操作失败");
            }
        } catch (Exception e) {
            logger.error("add carousel error|ex={}", ErrorWriterUtil.WriteError(e).toString());
            meta.setCode(500);
            meta.setMsg("服务器异常");
            return meta;
        }
        return meta;
    }

    //删除操作
    @RequestMapping("deleteCarousel/{type}/{id}")
    @ResponseBody
    public Result deleteCarousel(HttpServletRequest request, @PathVariable("type") String type, @PathVariable("id") Long id) throws Exception {
        logger.info("request /news/deleteCarousel/{}.html id={}", id, id);
        Result meta = new Result(1001, "初始化");
        try {
            int count = cmsCarouselService.deleteByPrimaryKey(id);
            if (count > 0) {
                meta.setCode(200);
                meta.setMsg("操作成功!");
                operationLogService.addNormalOperLog(request, OperLog.NEWS, String.format("删除轮播图type:%s", type));
            }
        } catch (Exception e) {
            logger.error("deleteCarousel id={}|error={}", id, ErrorWriterUtil.WriteError(e).toString());
            meta.setCode(500);
            meta.setMsg("操作失败!");
        }
        return meta;
    }

    //加载要编辑的轮播信息
    @RequestMapping(value = "/loadCarousel", method = RequestMethod.GET)
    public String loadCarousel(Model model, @RequestParam(value = "id", required = false) Long id) throws Exception {
        CmsCarousel cmsCarousel = cmsCarouselService.selectByPrimaryKey(id);
        model.addAttribute("news_prefix", news_prefix);
        model.addAttribute("carousel", cmsCarousel);
        logger.info("request /news/loadCarousel.html id={}|result={}", id, gson.toJson(cmsCarousel));
        return carousel_editPage;
    }

    //编辑操作
    @RequestMapping(value = "/eidtCarousel", method = RequestMethod.POST)
    @ResponseBody
    public Result eidtCarousel(HttpServletRequest request, CmsCarousel cmsCarousel) {
        Result meta = new Result<>();
        try {
            logger.info("edit /news/eidtCarousel cmsCarousel={}", gson.toJson(cmsCarousel));
            if (!CheckUtil.isEmpty(cmsCarousel.getNeedVersion())) {
                CmsCarouselVersion cmsCarouselVersion = cmsCarouselVersionService.selectByCarouselType(cmsCarousel.getType());
                if (CheckUtil.isEmpty(cmsCarouselVersion)) {
                    CmsCarouselVersion cmsCarouselVersion1 = new CmsCarouselVersion();
                    cmsCarouselVersion1.setCarouselType(cmsCarousel.getType());
                    cmsCarouselVersion1.setCarouselVersion(1);
                    cmsCarouselVersionService.insertSelective(cmsCarouselVersion1);
                } else {
                    cmsCarouselVersion.setCarouselVersion(cmsCarouselVersion.getCarouselVersion() + 1);
                    cmsCarouselVersionService.updateByPrimaryKeySelective(cmsCarouselVersion);
                }
            } else {
                cmsCarousel.setNeedVersion(0);
            }
            cmsCarousel.setSort(CheckUtil.isEmpty(cmsCarousel.getSort()) ? 0 : cmsCarousel.getSort());
            cmsCarousel.setUpdateTime(new Date());
            int result = cmsCarouselService.updateByPrimaryKeySelective(cmsCarousel);
            if (result > 0) {
                meta.setCode(200);
                meta.setMsg("操作成功");
                operationLogService.addNormalOperLog(request, OperLog.NEWS, String.format("编辑轮播图id:%s", cmsCarousel.getId()));
            } else {
                meta.setCode(500);
                meta.setMsg("操作失败");
            }
        } catch (Exception e) {
            logger.error("edit carousel error|ex={}", ErrorWriterUtil.WriteError(e).toString());
            meta.setCode(500);
            meta.setMsg("服务器异常");
            return meta;
        }
        return meta;
    }

    //设置状态  0.草稿 1.已审核
    @RequestMapping(value = "/updateStatus/{status}/{ids}")
    @ResponseBody
    public Result updateStatus(@PathVariable("status") Byte status, @PathVariable("ids") String ids) throws Exception {
        Result meta = new Result<>();
        try {
            Map<String, Object> map = new HashMap<>();
            map.put("status", status);
            map.put("idS", ids);
            int result = cmsCarouselService.updateStatus(map);
            if (result > 0) {
                meta.setCode(200);
                meta.setMsg("操作成功");
            } else {
                meta.setCode(500);
                meta.setMsg("操作失败");
            }
        } catch (Exception e) {
            logger.error("updateStatus id={}|error={}", ids, ErrorWriterUtil.WriteError(e).toString());
            meta.setCode(500);
            meta.setMsg("服务器异常");
            return meta;
        }
        return meta;
    }
}
