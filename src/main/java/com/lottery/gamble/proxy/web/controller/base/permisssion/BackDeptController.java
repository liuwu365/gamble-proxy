package com.lottery.gamble.proxy.web.controller.base.permisssion;

import com.google.gson.Gson;
import com.lottery.gamble.entity.BackDept;
import com.lottery.gamble.entity.BackGroup;
import com.lottery.gamble.entity.Page;
import com.lottery.gamble.entity.Result;
import com.lottery.gamble.enums.OperLog;
import com.lottery.gamble.proxy.core.util.CheckUtil;
import com.lottery.gamble.proxy.core.util.ErrorWriterUtil;
import com.lottery.gamble.proxy.web.controller.base.BaseController;
import com.lottery.gamble.proxy.web.service.log.OperationLogService;
import com.lottery.gamble.proxy.web.service.permission.BackDeptService;
import com.lottery.gamble.proxy.web.service.permission.BackGroupService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;

/**
 * @Description: 部门和分组控制器
 * @User: liuwu_eva@163.com
 * @Date: 2017-06-27 下午 4:45
 */
@Controller
@RequestMapping("/base/dept")
public class BackDeptController extends BaseController {
    private static final Logger logger = LoggerFactory.getLogger(BackDeptController.class);
    private static final Gson gson = new Gson();

    @Resource
    private BackDeptService backDeptService;
    @Resource
    private BackGroupService backGroupService;
    @Resource
    private OperationLogService operationLogService;

    @RequestMapping(value = "/list")
    public String list(Model model, Page<BackDept> page) {
        try {
            page = backDeptService.getPage(page);
            model.addAttribute("page", page);
        } catch (Exception e) {
            logger.error("backDept/list error|page={}|ex={}", gson.toJson(page), ErrorWriterUtil.WriteError(e));
        }
        return "base/dept/list";
    }

    //跳转到新增页面
    @RequestMapping(value = "/add", method = RequestMethod.GET)
    public String deptAdd() {
        return "base/dept/add";
    }

    //新增操作
    @RequestMapping(value = "/save", method = RequestMethod.POST)
    @ResponseBody
    public Result addDeptGroup(HttpServletRequest request, BackDept backDept, String groupName) {
        Result result;
        try {
            if (CheckUtil.isEmpty(backDept.getDeptName())) {
                return Result.failure("部门名称不能为空");
            }
            if (backDeptService.isExisit(backDept.getDeptName())) {
                return Result.failure("该部门名称已存在");
            }
            //添加部门
            int deptId = backDeptService.insertSelective(backDept);
            //添加分组
            if (!CheckUtil.isEmpty(deptId) && !CheckUtil.isEmpty(groupName)) {
                String[] groupNameArray = groupName.split(",");
                List<BackGroup> groupList = new ArrayList<>();
                StringBuffer sb = new StringBuffer();
                for (String str : groupNameArray) {
                    if (backGroupService.isExisitGroup(str)) {
                        sb.append("[" + str + "]");
                    } else {
                        BackGroup backGroup = new BackGroup();
                        backGroup.setDeptId((long) deptId);
                        backGroup.setGroupName(str);
                        groupList.add(backGroup);
                    }
                }
                if (sb.length() > 0) {
                    return Result.failure(String.format("分组%s名称已存在", sb));
                } else {
                    backGroupService.insertBatch(groupList);
                }
            }
            operationLogService.addNormalOperLog(request, OperLog.RESOURCE, String.format("新增部门【%s】和分组【%s】", backDept.getDeptName(), groupName));
            result = Result.success();
        } catch (Exception e) {
            logger.error("add dept error|ex={}", ErrorWriterUtil.WriteError(e));
            result = Result.failure("服务器异常");
        }
        return result;
    }

    @RequestMapping(value = "/isExistDept")
    @ResponseBody
    public boolean isExistDept(String deptName) {
        boolean result;
        try {
            result = backDeptService.isExisit(deptName);
        } catch (Exception e) {
            logger.error("backDept/isExist error|deptName={}|ex={}", deptName, ErrorWriterUtil.WriteError(e));
            result = false;
        }
        return result;
    }

    @RequestMapping(value = "/isExistGroup")
    @ResponseBody
    public boolean isExistGroup(String groupName) {
        boolean result;
        try {
            result = backGroupService.isExisitGroup(groupName);
        } catch (Exception e) {
            logger.error("backDept/isExistGroup error|deptName={}|ex={}", groupName, ErrorWriterUtil.WriteError(e));
            result = false;
        }
        return result;
    }

    @RequestMapping(value = "/editDeptName")
    @ResponseBody
    public String editDeptName(HttpServletRequest request, @RequestParam Long id, @RequestParam String deptName) {
        Result result;
        try {
            if (backDeptService.isExisit(deptName)) {
                result = Result.failure("该部门名称已存在");
                return gson.toJson(result);
            }
            BackDept dept = new BackDept();
            dept.setId(id);
            dept.setDeptName(deptName);
            result = backDeptService.updateByPrimaryKeySelective(dept) == 1 ? Result.success() : Result.failure();
            operationLogService.addNormalOperLog(request, OperLog.RESOURCE, String.format("编辑部门【%s】信息成功", deptName));
        } catch (Exception e) {
            logger.error("backDept/editDeptName error|ex={}", ErrorWriterUtil.WriteError(e));
            result = Result.failure("服务器异常");
        }
        return gson.toJson(result);
    }

    @RequestMapping(value = "/addGroup")
    @ResponseBody
    public String addGroup(HttpServletRequest request, @RequestParam Long deptId, @RequestParam String groupName) {
        Result result;
        try {
            if (backGroupService.isExisitGroup(groupName)) {
                result = Result.failure("该分组名称已存在");
                return gson.toJson(result);
            }
            BackGroup group = new BackGroup();
            group.setDeptId(deptId);
            group.setGroupName(groupName);
            result = backGroupService.insertSelective(group) == 1 ? Result.success() : Result.failure();
            operationLogService.addNormalOperLog(request, OperLog.RESOURCE, String.format("添加分组【%s】成功", groupName));
        } catch (Exception e) {
            logger.error("backDept/addDeptGroup error|ex={}", ErrorWriterUtil.WriteError(e));
            result = Result.failure("服务器异常");
        }
        return gson.toJson(result);
    }

    @RequestMapping(value = "/deleteDept")
    @ResponseBody
    public String deleteDept(HttpServletRequest request, @RequestParam Long deptId, @RequestParam String deptName) {
        Result result;
        try {
            int count = backGroupService.selectByDeptId(deptId);
            if (count > 0) {
                result = Result.failure("不能删除,该部门下存在【" + count + "】条分组;要执行删除操作请先删除该部门下的分组.");
            } else {
                result = backDeptService.deleteByPrimaryKey(deptId) == 1 ? Result.success() : Result.failure();
                operationLogService.addNormalOperLog(request, OperLog.RESOURCE, String.format("删除部门【%s】成功", deptName));
            }
        } catch (Exception e) {
            logger.error("backDept/addDeptGroup error|ex={}", ErrorWriterUtil.WriteError(e));
            result = Result.failure("服务器异常");
        }
        return gson.toJson(result);
    }

    @RequestMapping(value = "/deleteGroup")
    @ResponseBody
    public String deleteGroup(HttpServletRequest request, @RequestParam Long groupId, @RequestParam String groupName) {
        Result result;
        try {
            result = backGroupService.deleteByPrimaryKey(groupId) == 1 ? Result.success() : Result.failure();
            operationLogService.addNormalOperLog(request, OperLog.RESOURCE, String.format("删除分组【%s】成功", groupName));
        } catch (Exception e) {
            logger.error("backDept/addDeptGroup error|ex={}", ErrorWriterUtil.WriteError(e));
            result = Result.failure("服务器异常");
        }
        return gson.toJson(result);
    }

    @RequestMapping(value = "/findDept")
    @ResponseBody
    public String findDept() {
        Result result;
        try {
            List<BackDept> list = backDeptService.selectAll();
            result = Result.success(list);
            logger.info(gson.toJson(result));
        } catch (Exception e) {
            logger.error("backDept/findDept error|ex={}", ErrorWriterUtil.WriteError(e));
            result = Result.failure("服务器异常");
        }
        return gson.toJson(result.getT());
    }

    @RequestMapping(value = "/findGroupByDeptId")
    @ResponseBody
    public String findGroupByDeptId(@RequestParam Long deptId) {
        Result result;
        try {
            List<BackGroup> list = backGroupService.selectListByDeptId(deptId);
            result = Result.success(list);
            logger.info(gson.toJson(result));
        } catch (Exception e) {
            logger.error("backDept/findGroupByDeptId error|ex={}", ErrorWriterUtil.WriteError(e));
            result = Result.failure("服务器异常");
        }
        return gson.toJson(result);
    }

}
