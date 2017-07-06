package com.lottery.gamble.proxy.web.controller.cms;

import com.google.gson.Gson;
import com.lottery.gamble.common.util.CacheUtil;
import com.lottery.gamble.common.util.CheckUtil;
import com.lottery.gamble.entity.BackUser;
import com.lottery.gamble.entity.CmsContent;
import com.lottery.gamble.entity.Page;
import com.lottery.gamble.entity.Result;
import com.lottery.gamble.enums.OperLog;
import com.lottery.gamble.proxy.core.util.DateUtils;
import com.lottery.gamble.proxy.core.util.ErrorWriterUtil;
import com.lottery.gamble.proxy.web.controller.base.BaseController;
import com.lottery.gamble.proxy.web.service.cms.CmsContentService;
import com.lottery.gamble.proxy.web.service.cms.CmsNewsValidation;
import com.lottery.gamble.proxy.web.service.log.OperationLogService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
 * @Description: CMS资讯模块-->文章控制器
 * @User: liuwu_eva@163.com
 * @Date: 2016-07-22 下午 1:49
 */
@Controller
@RequestMapping("/news/content")
public class CmsContentController extends BaseController {
    private static final Logger logger = LoggerFactory.getLogger(CmsContentController.class);
    private static final Gson gson = new Gson();
    private static CacheUtil cacheUtil = CacheUtil.getInstance();
    CmsNewsValidation validation = CmsNewsValidation.getInstance();
    @Value("${base.url}")
    private String news_prefix;
    @Resource
    private CmsContentService cmsContentService;
    @Resource
    private OperationLogService operationLogService;

    private final String content_listPage = "model/news/content/content_list";
    private final String content_addPage = "model/news/content/content_add";
    private final String content_editPage = "model/news/content/content_edit";
    private final String content_showDetail = "model/news/content/showDetail";
    private final String trash_listPage = "model/news/content/trash_list";

    /*================================= 文章 =======================================================*/
    //测试新闻详情分页
    @RequestMapping(value = "/showDetail")
    public String showDetail() {
        return content_showDetail;
    }


    //查询列表页面
    @RequestMapping(value = "/content_list")
    public String contentList(Model model, Page<CmsContent> page) {
        page.getFilter().put("statusIdS", new int[]{0, 1}); //0.草稿 1.已审核 3.已删除(回收站)
        logger.info("request /news/content_list.html page={}", gson.toJson(page));
        page = cmsContentService.findPage(page);
        model.addAttribute("content", page);
        model.addAttribute("news_prefix", news_prefix);

        logger.info("list result={}", gson.toJson(page));
        return content_listPage;
    }

    //跳转到新增页面
    @RequestMapping(value = "/content_add", method = RequestMethod.GET)
    public String contentAdd(Model model) {
        model.addAttribute("news_prefix", news_prefix);
        return content_addPage;
    }

    //新增操作
    @RequestMapping(value = "/content_add", method = RequestMethod.POST)
    @ResponseBody
    public Result contentAdd(HttpServletRequest request, Long folderid, String title, String shortTitleStyle,
                             String shortTitle, String secondTitle, String keyworld,
                             String author, String source, String linkerurl,
                             String indexImage, Byte level, String createtime,
                             Long clickcount, Byte status, String content, Integer app) {
        Result meta = new Result();
        try {
            BackUser currentUser = getCurrentUser(); //获取当前登录人信息
            CmsContent cmsContent = new CmsContent();

            //1.参数封装
            Date date = null; //发布时间
            if (CheckUtil.isEmpty(cmsContent.getCreatetime())) {
                cmsContent.setCreatetime(new Date());
                cmsContent.setUpdatetime(new Date());
            } else {
                date = DateUtils.parseFullDateTime(createtime);
                cmsContent.setCreatetime(date);
                cmsContent.setUpdatetime(date);
            }
            cmsContent.setFolderid(folderid);
            cmsContent.setTitle(title);
            cmsContent.setShortTitleStyle(shortTitleStyle);
            cmsContent.setShortTitle(shortTitle);
            cmsContent.setSecondTitle(secondTitle);
            cmsContent.setKeyworld(keyworld);
            cmsContent.setAuthor(author);
            cmsContent.setSysUserid(currentUser.getId());
            cmsContent.setSource(source);
            cmsContent.setLinkerurl(linkerurl);
            cmsContent.setIndexImage(indexImage);
            cmsContent.setLevel(CheckUtil.isEmpty(level) ? 0 : level);
            cmsContent.setClickcount(CheckUtil.isEmpty(clickcount) ? 0 : clickcount);
            cmsContent.setStatus(status);
            cmsContent.setContent(content);
            cmsContent.setApp(app);
            //2.提交DB之前必填参数验证
            boolean b = validation.validContentParam(meta, cmsContent);
            if (b == false) {
                return meta;
            }
            //3.入库DB
            int result = cmsContentService.insertSelective(cmsContent);
            if (result > 0) {
                meta.setCode(200);
                meta.setMsg("操作成功");
                operationLogService.addNormalOperLog(request, OperLog.NEWS, String.format("新增新闻资讯【%s】", shortTitle));
            } else {
                meta.setCode(500);
                meta.setMsg("操作失败");
            }
        } catch (Exception e) {
            logger.error("add content error|ex={}", ErrorWriterUtil.WriteError(e).toString());
            meta.setCode(500);
            meta.setMsg("服务器异常");
            return meta;
        }
        return meta;
    }

    //删除操作(彻底删除)
    @RequestMapping("deleteContent/{id}")
    @ResponseBody
    public boolean deleteContent(HttpServletRequest request, @PathVariable("id") Long id) throws Exception {
        logger.info("request /news/deleteContent/{}.html id={}", id, id);
        boolean falg = false;
        try {
            //删除对应的缓存数据
            cacheUtil.del(("news_" + id).getBytes());
            CmsContent content = cmsContentService.selectByPrimaryKey(id);
            operationLogService.addNormalOperLog(request, OperLog.NEWS, String.format("删除新闻资讯【%s】", content.getShortTitle()));
            falg = cmsContentService.deleteByPrimaryKey(id) >= 1;
        } catch (Exception e) {
            logger.error("deleteContent id={}|error={}", id, ErrorWriterUtil.WriteError(e).toString());
            return false;
        }
        return falg;
    }

    //批量删除操作(彻底删除)
    @RequestMapping("deleteContentBath/{ids}")
    @ResponseBody
    public boolean deleteContentBath(HttpServletRequest request, @PathVariable("ids") String ids) throws Exception {
        logger.info("request /news/deleteContentBath/{}.html ids={}", ids, ids);
        boolean falg = false;
        try {
            //删除对应的缓存数据
            String[] idsStr = ids.split(",");
            for (String id : idsStr) {
                cacheUtil.del(("news_" + Integer.parseInt(id)).getBytes());
                CmsContent content = cmsContentService.selectByPrimaryKey(Long.parseLong(id));
                operationLogService.addNormalOperLog(request, OperLog.NEWS, String.format("删除新闻资讯【%s】", content.getShortTitle()));
            }
            Map<String, Object> map = new HashMap<>();
            map.put("idS", ids);
            falg = cmsContentService.deleteByIds(map) >= 1;
        } catch (Exception e) {
            logger.error("deleteContent id={}|error={}", ids, ErrorWriterUtil.WriteError(e).toString());
            return false;
        }
        return falg;
    }

    //加载要编辑的文章信息
    @RequestMapping(value = "/loadContent", method = RequestMethod.GET)
    public String loadContent(Model model, @RequestParam(value = "id", required = false) Long id) throws Exception {
        CmsContent cmsContent = cmsContentService.selectByPrimaryKey(id);
        model.addAttribute("news_prefix", news_prefix);
        model.addAttribute("content", cmsContent);
        logger.info("request /news/loadContent.html id={}|result={}", id, gson.toJson(cmsContent));
        return content_editPage;
    }

    //编辑操作
    @RequestMapping(value = "/eidtContent", method = RequestMethod.POST)
    @ResponseBody
    public Result eidtContent(HttpServletRequest request, CmsContent cmsContent) {
        Result meta = new Result();
        try {
            logger.info("edit /news/eidtContent cmsContent={}", gson.toJson(cmsContent));
            //提交DB之前必填参数验证
            boolean b = validation.validContentParam(meta, cmsContent);
            if (b == false) {
                return meta;
            }
            if (cmsContent.getApp() == null) {
                cmsContent.setApp(0);
            }
            cmsContent.setIndexImage(cmsContent.getIndexImage());
            cmsContent.setLevel(CheckUtil.isEmpty(cmsContent.getLevel()) ? 0 : cmsContent.getLevel());
            cmsContent.setClickcount(CheckUtil.isEmpty(cmsContent.getClickcount()) ? 0 : cmsContent.getClickcount());
            cmsContent.setUpdatetime(new Date());
            int result = cmsContentService.updateByPrimaryKeySelective(cmsContent);
            if (result > 0) {
                meta.setCode(200);
                meta.setMsg("操作成功");
                cacheUtil.del(("news_" + cmsContent.getId()).getBytes());
                operationLogService.addNormalOperLog(request, OperLog.NEWS, String.format("编辑新闻资讯【%s】", cmsContent.getShortTitle()));
            } else {
                meta.setCode(500);
                meta.setMsg("操作失败");
            }
        } catch (Exception e) {
            logger.error("edit content error|ex={}", ErrorWriterUtil.WriteError(e).toString());
            meta.setCode(500);
            meta.setMsg("服务器异常");
            return meta;
        }
        return meta;
    }

    //设置状态  0.草稿 1.已审核 3.已删除(回收站)
    @RequestMapping(value = "/updateStatus/{status}/{ids}")
    @ResponseBody
    public boolean updateStatus(@PathVariable("status") Byte status, @PathVariable("ids") String ids) throws Exception {
        boolean flag = false;
        try {
            Map<String, Object> map = new HashMap<>();
            map.put("status", status);
            map.put("idS", ids);
            flag = cmsContentService.updateStatus(map) >= 1;
        } catch (Exception e) {
            logger.error("updateStatus id={}|error={}", ids, ErrorWriterUtil.WriteError(e).toString());
            return false;
        }
        return flag;
    }

    //批量同步App
    @RequestMapping(value = "/updateSyncApp/{app}/{ids}")
    @ResponseBody
    public boolean updateSyncApp(@PathVariable("app") Integer app, @PathVariable("ids") String ids) throws Exception {
        boolean flag = false;
        try {
            Map<String, Object> map = new HashMap<>();
            map.put("app", app);
            map.put("idS", ids);
            flag = cmsContentService.updateApp(map) >= 1;
        } catch (Exception e) {
            logger.error("updateStatus id={}|error={}", ids, ErrorWriterUtil.WriteError(e).toString());
            return false;
        }
        return flag;
    }

    /*================================= 回收站 =======================================================*/
    //查询回收站列表信息 0.草稿 1.已审核 3.已删除(回收站)
    @RequestMapping(value = "/trash_list")
    public String trashList(Model model, Page<CmsContent> page) {
        page.getFilter().put("status", 3);
        logger.info("request /news/content_list.html page={}", gson.toJson(page));
        page = cmsContentService.findPage(page);
        model.addAttribute("content", page);
        logger.info("list result={}", gson.toJson(page));
        return trash_listPage;
    }


}
