package com.sky.controller.admin;

import com.sky.constant.MessageConstant;
import com.sky.result.Result;
import com.sky.utils.AliOssUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Objects;

@RestController
@RequestMapping("/admin/common")
@Api(tags = "通用接口")
@Slf4j
public class CommonController {

    @Autowired//注入阿里云文件上传工具类Bean
    private AliOssUtil aliOssUtil;

    @PostMapping("/upload")
    @ApiOperation("文件上传接口")
    public Result<String> upload(MultipartFile file){
        log.info("申请上传文件：{}",file);
        //直接调用工具类对象的方法
        try {
            return Result.success(aliOssUtil.upload(file.getBytes(), Objects.requireNonNull(file.getOriginalFilename())));
        } catch (IOException e) {
            log.error("文件上传失败:",e);
        }
        return Result.error(MessageConstant.UPLOAD_FAILED);
    }
}
