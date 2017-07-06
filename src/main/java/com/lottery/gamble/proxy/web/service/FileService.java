package com.lottery.gamble.proxy.web.service;


import com.lottery.gamble.common.fastdfs.FastDFSFile;
import com.lottery.gamble.common.fastdfs.FileManager;
import com.lottery.gamble.common.util.CheckUtil;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;
import org.springframework.web.multipart.support.DefaultMultipartHttpServletRequest;

import javax.imageio.ImageIO;
import javax.servlet.http.HttpServletRequest;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.Set;

/**
 * @author 王亚平
 * @description 文件上传服务类
 */
@Service
public class FileService {


    String format[] = {"image/bmp", "image/jpg", "image/jpeg", "image/png", "image/gif"};

    long maxSize = 2 * 1024;

    public static void main(String[] args) {
        String fileFormat = "png";
        String format[] = {"BMP", "JPG", "JPEG", "PNG", "GIF"};

        if (!Arrays.asList(format).contains(fileFormat.toLowerCase())) {
            System.out.println("不在");
        } else {
            System.out.println("不");
        }
    }

    org.slf4j.Logger logger = LoggerFactory.getLogger(FileService.class);

    public String upLoad(HttpServletRequest request, String type) throws IOException {
        MultipartHttpServletRequest multipartRequest = (MultipartHttpServletRequest) request;
        MultipartFile multipartFile = multipartRequest.getFile("Filedata");
        if (com.lottery.gamble.proxy.core.util.CheckUtil.isEmpty(multipartFile)) {
            Set<String> strings = ((DefaultMultipartHttpServletRequest) multipartRequest).getMultiFileMap().keySet();
            for (String fileId : strings) {
                multipartFile = multipartRequest.getFile(fileId);
                if (multipartFile != null) {
                    break;
                }
            }
            if (multipartFile == null) {
                return null;
            }
        }

        if (multipartFile.isEmpty()) {
            return "2";//文件是空的
        }
        InputStream fis = multipartFile.getInputStream();
        InputStream fisClone = null;
        if (!CheckUtil.isEmpty(type)) {
            fisClone = multipartFile.getInputStream();
            BufferedImage read = ImageIO.read(fisClone);
            int width = read.getWidth();
            int height = read.getHeight();
            if (type.equals("photo")) {
                if (width != 80 && height != 80) {
                    return "4";
                }
            } else if (type.equals("cover")) {
                if (width != 218 && height != 280) {
                    return "5";
                }
            }else if(type.equals("contest")){
                if (width != 550 && height != 360) {
                    return "6";
                }
            }
        }
        long size = multipartFile.getSize();
        logger.info("图片大小:{}kB", size / 1024);
        if (size / 1024 > maxSize) {
            return "3";//文件大于2MB
        }
        String contentType = multipartFile.getContentType();
        if (!Arrays.asList(format).contains(contentType.toLowerCase())) {
            return "1";//文件格式不对
        }
        String[] split = multipartFile.getOriginalFilename().split("\\.");
        if (!CheckUtil.isEmpty(split) && split.length == 2) {

            if (fis != null) {
                int len = fis.available();
                logger.info("available:{}",len);
                byte[] file_buff = new byte[len];
                int read = fis.read(file_buff);
                logger.info("read:{}",read);

                FastDFSFile dfsFile = new FastDFSFile(split[0], file_buff, split[1]);

                logger.info("upLoad dfsFile ={}",dfsFile);

                String fileAbsolutePath = FileManager.upload(dfsFile);
                System.out.println(fileAbsolutePath);
                if (!CheckUtil.isEmpty(fis)) {
                    fis.close();
                }
                if (!CheckUtil.isEmpty(fisClone)) {
                    fisClone.close();
                }
                return fileAbsolutePath;
            } else {
                return null;
            }
        } else {
            return null;
        }
    }


}
