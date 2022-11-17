package com.reggie.controller;

import com.reggie.common.R;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.UUID;

/**
 * 文件上传和下载
 */

@RestController
@RequestMapping("/common")
@Slf4j
public class CommonController {

    @Value("${reggie.imgPath}")
    private String imgPath;

    @PostMapping("/upload")
    public R<String> upload(MultipartFile file){
//        file 是一个临时文件，需要保存到指定位置 否则本次请求结束之后就会被删除

//        原始文件名
        String fileOriginalFilename = file.getOriginalFilename();
        String suffix = fileOriginalFilename.substring(fileOriginalFilename.lastIndexOf("."));
//        使用uuid重新生成文件名 防止文件名相同出现覆盖
        String fileName = UUID.randomUUID().toString()+suffix;


        File dir = new File(imgPath);
        if (!dir.exists()) {
            dir.mkdirs();
        }
        try {
            file.transferTo(new File(imgPath+fileName));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
//        返回文件名称
        return R.success(fileName);
    }

    /**
     * 文件下载
     * @param name
     * @param response
     */
    @GetMapping("/download")
    public void download(String name, HttpServletResponse response){
        try {
//            输入流 获取图片
            FileInputStream fileInputStream = new FileInputStream(new File(imgPath + name));
//            输出流 回显到浏览器
            ServletOutputStream outputStream = response.getOutputStream();

            response.setContentType("image/jpeg");

            int len = 0;
            byte[] bytes = new byte[1024];
            while( (len = fileInputStream.read(bytes))!=-1){
                outputStream.write(bytes,0,len);
                outputStream.flush();
            }

            outputStream.close();
            fileInputStream.close();
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }



    }
}
