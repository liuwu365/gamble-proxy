package com.lottery.gamble.proxy.web.controller.base;

import com.lottery.gamble.proxy.core.util.ErrorWriterUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import javax.servlet.http.HttpServletRequest;
import java.io.*;
import java.util.Arrays;
import java.util.List;

/**
 * @author 王亚平
 * @description 多文件上传控制器
 */
@Controller
public class BatchFileUploadController {
    Logger logger = LoggerFactory.getLogger(BatchFileUploadController.class);
    String format[] = {"image/bmp", "image/jpg", "image/jpeg", "image/png", "image/gif"};
    long maxSize = 2 * 1024;

    @RequestMapping(value = "/batch/upload", method = RequestMethod.POST)
    public
    @ResponseBody
    String handleFileUpload(HttpServletRequest request) {
        List<MultipartFile> files = ((MultipartHttpServletRequest) request).getFiles("file");
        for (int i = 0; i < files.size(); ++i) {
            MultipartFile file = files.get(i);
            String name = file.getName();
            if (!file.isEmpty()) {
                try {
                    byte[] bytes = file.getBytes();
                    BufferedOutputStream stream =
                            new BufferedOutputStream(new FileOutputStream(new File(name + i)));
                    stream.write(bytes);
                    stream.close();
                } catch (Exception e) {
                    return "You failed to upload " + name + " => " + e.getMessage();
                }
            } else {
                return "You failed to upload " + name + " because the file was empty.";
            }
        }
        return "upload successful";
    }

    /**
     * 批量上传图片到磁盘
     *
     * @param request
     * @return
     */
    @RequestMapping(value = "/batch/uploadImg", method = RequestMethod.POST)
    @ResponseBody
    public String uploadImg2(HttpServletRequest request) {
        String fileName = null;
        BufferedOutputStream stream = null;

        try {
            MultipartHttpServletRequest multipartRequest = (MultipartHttpServletRequest) request;
            List<MultipartFile> multipartFile = multipartRequest.getFiles("upload_org_code");

            for (int i = 0; i < multipartFile.size(); i++) {
                if (multipartFile.get(i) == null) {
                    continue;
                } else {
                    long size = multipartFile.get(i).getSize();
                    String contentType = multipartFile.get(i).getContentType();
                    logger.info("图片大小:{}KB-{}字节", size / 1024, size);
                    logger.info("contentType={}", contentType);
                    if (multipartFile.isEmpty()) {
                        return "3";
                    } else if (size / 1024 > maxSize) {
                        return "2";
                    } else if (!Arrays.asList(format).contains(contentType.toLowerCase())) {
                        return "1";
                    } else {
                        String ml = "/data/source/emailTemplateTheme/";
                        InputStream inputStream = multipartFile.get(i).getInputStream();
                        int len = inputStream.available();
                        byte[] file_buff = new byte[len];
                        inputStream.read(file_buff);
                        File fileDir = new File(ml);
                        if (!fileDir.exists()) {
                            fileDir.mkdir();
                            logger.info("目录：{}，创建成功！", ml);
                        }
                        fileName = multipartFile.get(i).getOriginalFilename();
                        stream = new BufferedOutputStream(new FileOutputStream(ml + fileName));
                        stream.write(file_buff);
                    }
                }
            }
        } catch (Exception e) {
            logger.info("上传图片异常：ex={}", ErrorWriterUtil.WriteError(e).toString());
        } finally {
            if (stream != null) {
                try {
                    stream.close();
                } catch (IOException e) {
                    logger.info("输出IO 异常");
                }
            }
        }
        return fileName;
    }

}