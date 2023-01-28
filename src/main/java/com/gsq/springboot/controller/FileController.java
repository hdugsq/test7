package com.gsq.springboot.controller;


import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.IdUtil;
import cn.hutool.crypto.SecureUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.gsq.springboot.common.Result;
import com.gsq.springboot.entity.Files;
import com.gsq.springboot.entity.User;
import com.gsq.springboot.mapper.FileMapper;
import com.gsq.springboot.utils.TokenUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.IOException;
import java.net.URLEncoder;
import java.util.List;

/**
 * 文件上传接口
 *
 * */
@RestController
@RequestMapping("/file")
public class FileController {

    @Value("${files.upload.path}")
    private String fileUploadPath;

    @Resource
    private FileMapper fileMapper;

    @PostMapping("/upload")
    public String upload(@RequestParam MultipartFile file) throws IOException {
        String originalFilename=file.getOriginalFilename();
        String type= FileUtil.extName(originalFilename);
        long size=file.getSize();

        //判断文件目录是否存在，不存在就创建
        File upLoadFile= new File(fileUploadPath);
        if(!upLoadFile.exists()){
            upLoadFile.mkdir();
        }

        //定义唯一标识码
        String uuid= IdUtil.fastSimpleUUID();
        String fileUuid=uuid+'.'+type;
        String url="http://localhost:8081/file/"+fileUuid;
        File finalFile=new File(fileUploadPath+fileUuid);

        //存入磁盘；
        file.transferTo(finalFile);
        //获取文件md5
        String md5= SecureUtil.md5(finalFile);
        QueryWrapper<Files> queryWrapper=new QueryWrapper<>();
        queryWrapper.eq("md5",md5);
        List<Files> dbFiles=fileMapper.selectList(queryWrapper);
        if(dbFiles.size()!=0){
            url= dbFiles.get(0).getUrl();
            finalFile.delete();
        }
        //存入数据库
        Files saveFile=new Files();
        saveFile.setName(originalFilename);
        saveFile.setType(type);
        saveFile.setSize(size);
        saveFile.setUrl(url);//文件下载链接
        saveFile.setMd5(md5);
        fileMapper.insert(saveFile);
        return url;
    }

    @GetMapping("/{uuid}")
    public void download(@PathVariable String uuid, HttpServletResponse response) throws IOException {
        // 根据文件的唯一标识码获取文件
        File uploadFile = new File( fileUploadPath + uuid);
        // 设置输出流的格式
        ServletOutputStream os = response.getOutputStream();
        response.addHeader( "Content-Disposition", "attachment;filename=" + URLEncoder.encode(uuid,"UTF-8"));
        response.setContentType("application/octet-stream");
        // 读取文件的字节流
        os.write(FileUtil.readBytes(uploadFile));
        os.flush();
        os.close();
    }

    @GetMapping("/page")
    public Result findPage(@RequestParam Integer pageNum,
                           @RequestParam Integer pageSize,
                           @RequestParam(defaultValue = "") String name) {
        QueryWrapper<Files> queryWrapper=new QueryWrapper<>();
        queryWrapper.eq("is_delete",false);
        if(!"".equals(name)) {
            queryWrapper.like("name", name);//模糊查询
        }
        return Result.success(fileMapper.selectPage(new Page<>(pageNum, pageSize),queryWrapper));
    }

    @DeleteMapping("/{id}")
    public Result delete(@PathVariable Integer id) {
        Files file=fileMapper.selectById(id);
        file.setIsDelete(true);
        return Result.success(fileMapper.updateById(file));
    }

}
