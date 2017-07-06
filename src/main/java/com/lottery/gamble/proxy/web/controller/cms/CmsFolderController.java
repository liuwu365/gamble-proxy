package com.lottery.gamble.proxy.web.controller.cms;

import com.google.gson.Gson;
import com.lottery.gamble.entity.CmsFolder;
import com.lottery.gamble.entity.Page;
import com.lottery.gamble.entity.Result;
import com.lottery.gamble.enums.OperLog;
import com.lottery.gamble.proxy.core.util.ChineseToEnglish;
import com.lottery.gamble.proxy.core.util.ErrorWriterUtil;
import com.lottery.gamble.proxy.web.controller.base.BaseController;
import com.lottery.gamble.proxy.web.service.cms.CmsFolderService;
import com.lottery.gamble.proxy.web.service.log.OperationLogService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.Date;
import java.util.List;

/**
 * @Description: CMS资讯模块-->栏目控制器
 * @User: liuwu_eva@163.com
 * @Date: 2016-07-20 下午 5:12
 */
@Controller
@RequestMapping("/news/folder")
public class CmsFolderController extends BaseController {
    private static final Logger logger = LoggerFactory.getLogger(CmsFolderController.class);
    private static final Gson gson = new Gson();

    @Resource
    private CmsFolderService cmsFolderService;
    @Resource
    private OperationLogService operationLogService;

    private final String folder_listPage = "model/news/folder/folder_list";
    private final String folder_addPage = "model/news/folder/folder_add";
    private final String folder_editPage = "model/news/folder/folder_edit";

    /*================================= 栏目 =======================================================*/
    //查询列表页面
    @RequestMapping(value = "/folder_list")
    public String folderList(Model model, Page<CmsFolder> page) {
        logger.info("request /news/folder_list.html page={}", gson.toJson(page));
        page = cmsFolderService.findPage(page);
        model.addAttribute("folder", page);

        logger.info("list result={}", gson.toJson(page));
        return folder_listPage;
    }

    //跳转到新增页面
    @RequestMapping(value = "/folder_add", method = RequestMethod.GET)
    public String folderAdd() {
        return folder_addPage;
    }

    //新增操作
    @RequestMapping(value = "/folder_add", method = RequestMethod.POST)
    @ResponseBody
    public boolean folderAdd(HttpServletRequest request, CmsFolder cmsFolder) {
        int result = 0;
        try {
            result = cmsFolderService.insertSelective(cmsFolder);
            operationLogService.addNormalOperLog(request, OperLog.NEWS, String.format("新增资讯栏目【%s】", cmsFolder.getName()));
        } catch (Exception e) {
            logger.error("add folder error|ex={}", ErrorWriterUtil.WriteError(e).toString());
            return false;
        }
        return result >= 1;
    }

    //删除操作
    @RequestMapping("deleteFolder/{folderid}")
    @ResponseBody
    public Result deleteForder(HttpServletRequest request, @PathVariable("folderid") Long folderid) {
        logger.info("request /news/deleteForder/{}.html folderid={}", folderid, folderid);
        Result meta = new Result(1001, "初始化");
        try {
            int count = cmsFolderService.selectFolderExitContent(folderid);
            if (count > 0) {
                meta.setCode(405);
                meta.setMsg("不能删除,该栏目下存在【" + count + "】条文章记录;要执行删除操作请先删除该栏目下的文章.");
            } else {
                CmsFolder folder = cmsFolderService.selectByPrimaryKey(folderid);
                count = cmsFolderService.deleteByPrimaryKey(folderid);
                if (count > 0) {
                    meta.setCode(200);
                    meta.setMsg("操作成功!");
                    operationLogService.addNormalOperLog(request, OperLog.NEWS, String.format("删除资讯栏目【%s】", folder.getName()));
                }
            }
        } catch (Exception e) {
            logger.error("deleteFolder folderid={}|error={}", folderid, ErrorWriterUtil.WriteError(e).toString());
            meta.setCode(500);
            meta.setMsg("操作失败!");
        }
        return meta;
    }

    //加载要编辑的栏目信息
    @RequestMapping(value = "/loadFolder", method = RequestMethod.GET)
    public String loadFolder(Model model, @RequestParam(value = "folderid", required = false) Long folderid) throws Exception {
        CmsFolder cmsFolder = cmsFolderService.selectByPrimaryKey(folderid);
        model.addAttribute("folder", cmsFolder);
        logger.info("request /news/loadFolder.html folderid={}|result={}", folderid, gson.toJson(cmsFolder));
        return folder_editPage;
    }

    //编辑操作
    @RequestMapping(value = "/eidtFolder", method = RequestMethod.POST)
    @ResponseBody
    public boolean eidtFolder(HttpServletRequest request, CmsFolder cmsFolder) {
        int result = 0;
        try {
            logger.info("edit /news/eidtFolder cmsFolder={}", gson.toJson(cmsFolder));
            cmsFolder.setUpdatetime(new Date());
            result = cmsFolderService.updateByPrimaryKeySelective(cmsFolder);
            operationLogService.addNormalOperLog(request, OperLog.NEWS, String.format("编辑资讯栏目【%s】", cmsFolder.getName()));
        } catch (Exception e) {
            logger.error("edit folder error|ex={}", ErrorWriterUtil.WriteError(e).toString());
            return false;
        }
        return result >= 1;
    }

    //生成标识符操作(通过名称生成首字母的简称)
    @RequestMapping("chineseToEnglish/{name}")
    @ResponseBody
    public String chineseToEnglish(@PathVariable("name") String name) {
        String result = ChineseToEnglish.getPinYinHeadChar(name.toLowerCase());
        logger.info("request /news/chineseToEnglish/{} result={}", name, result);
        return result;
    }

    //加载父级栏目
    @RequestMapping(value = "/loadFatherFolder", method = RequestMethod.GET)
    @ResponseBody
    public String loadFatherFolder() {
        List<CmsFolder> list = cmsFolderService.selectParentFolder();
        logger.info("request /news/loadFatherFolder result={}", gson.toJson(list));
        return gson.toJson(list);
    }

    //加载父级栏目
    @RequestMapping(value = "/findFolderAll", method = RequestMethod.GET)
    @ResponseBody
    public String findFolderAll() {
        List<CmsFolder> list = cmsFolderService.selectAll();
        return gson.toJson(list);
    }

}
