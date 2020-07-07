package com.jmal.clouddisk.controller;

import com.jmal.clouddisk.exception.ExceptionType;
import com.jmal.clouddisk.interceptor.AuthInterceptor;
import com.jmal.clouddisk.exception.CommonException;
import com.jmal.clouddisk.model.FileDocument;
import com.jmal.clouddisk.model.UploadApiParam;
import com.jmal.clouddisk.service.IFileService;
import com.jmal.clouddisk.service.IUserService;
import com.jmal.clouddisk.util.ResponseResult;
import com.jmal.clouddisk.util.ResultUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;


/**
 * @Description 云文件管理控制器
 * @Author jmal
 * @Date 2020-01-27 12:59
 * @blame jmal
 */
@Api(tags = "文件管理")
@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
public class FileController {

    @Autowired
    private IFileService fileService;

    @Autowired
    IUserService service;

    @ApiOperation("文件列表")
    @GetMapping("/list")
    public ResponseResult<Object> list(UploadApiParam upload) throws CommonException {
        return fileService.listFiles(upload);
    }

    @ApiOperation("查找下级目录")
    @GetMapping("/query-file-tree")
    public ResponseResult<Object> queryFileTree(UploadApiParam upload, String fileId) throws CommonException {
        return fileService.queryFileTree(upload,fileId);
    }

    @ApiOperation("搜索文件")
    @GetMapping("/search-file")
    public ResponseResult<Object> searchFile(UploadApiParam upload, String keyword) throws CommonException {
        return fileService.searchFile(upload, keyword);
    }

    @ApiOperation("搜索文件并打开文件夹")
    @GetMapping("/search-file-open")
    public ResponseResult<Object> searchFileAndOpenDir(UploadApiParam upload, String id) throws CommonException {
        return fileService.searchFileAndOpenDir(upload, id);
    }

    @ApiOperation("文件上传")
    @PostMapping("upload")
    @ResponseBody
    public ResponseResult<Object> uploadPost(UploadApiParam upload) throws IOException {
        return fileService.upload(upload);
    }

    @ApiOperation("文件夹上传")
    @PostMapping("upload-folder")
    @ResponseBody
    public ResponseResult<Object> uploadFolder(UploadApiParam upload) throws CommonException {
        return fileService.uploadFolder(upload);
    }

    @ApiOperation("新建文件夹")
    @PostMapping("new_folder")
    @ResponseBody
    public ResponseResult<Object> newFolder(UploadApiParam upload) throws CommonException {
        return fileService.newFolder(upload);
    }

    @ApiOperation("检查文件/分片是否存在")
    @GetMapping("upload")
    @ResponseBody
    public ResponseResult<Object> checkUpload(UploadApiParam upload) throws IOException {
        return fileService.checkChunkUploaded(upload);
    }

    @ApiOperation("合并文件")
    @PostMapping("merge")
    @ResponseBody
    public ResponseResult<Object> merge(UploadApiParam upload) throws IOException {
        return fileService.merge(upload);
    }

    @ApiOperation("读取simText文件")
    @GetMapping("/preview/text")
    public ResponseResult<Object> previewText(@RequestParam String id, @RequestParam String username) throws CommonException {
        ResultUtil.checkParamIsNull(id,username);
        return ResultUtil.success(fileService.getById(id, username));
    }

    @ApiOperation("根据path读取simText文件")
    @GetMapping("/preview/path/text")
    public ResponseResult<Object> previewTextByPath(@RequestParam String path,@RequestParam String username) throws CommonException {
        return fileService.previewTextByPath(path, username);
    }

    @ApiOperation("预览文件")
    @GetMapping("/preview/{filename}")
    public void preview(HttpServletRequest request, HttpServletResponse response, String[] fileIds,@PathVariable String filename) throws CommonException {
        if (fileIds != null && fileIds.length > 0) {
            List<String> list = Arrays.asList(fileIds);
            fileService.nginx(request, response, list, false);
        } else {
            throw new CommonException(ExceptionType.MISSING_PARAMETERS.getCode(), ExceptionType.MISSING_PARAMETERS.getMsg());
        }
    }

    @ApiOperation("下载文件 转到 Nginx 下载")
    @GetMapping("/download")
    public void downLoad(HttpServletRequest request, HttpServletResponse response, String[] fileIds) throws CommonException {
        if (fileIds != null && fileIds.length > 0) {
            List<String> list = Arrays.asList(fileIds);
            fileService.nginx(request, response, list, true);
        } else {
            throw new CommonException(ExceptionType.MISSING_PARAMETERS.getCode(), ExceptionType.MISSING_PARAMETERS.getMsg());
        }
    }

    @ApiOperation("显示缩略图")
    @GetMapping("/view/thumbnail")
    public ResponseEntity<Object> thumbnail(HttpServletRequest request, String id) throws IOException {
        ResultUtil.checkParamIsNull(id);
        Optional<FileDocument> file = fileService.thumbnail(id, service.getUserName(request.getParameter(AuthInterceptor.JMAL_TOKEN)));
        return file.<ResponseEntity<Object>>map(fileDocument ->
                ResponseEntity.ok()
                        .header(HttpHeaders.CONTENT_DISPOSITION, "fileName=" + fileDocument.getName())
                        .header(HttpHeaders.CONTENT_TYPE, fileDocument.getContentType())
                        .header(HttpHeaders.CONTENT_LENGTH, fileDocument.getContent().length + "")
                        .header("Connection", "close")
                        .header(HttpHeaders.CONTENT_ENCODING, "utf-8")
                        .body(fileDocument.getContent())).orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND).body("找不到该文件"));
    }

    @ApiOperation("显示缩略图(mp3封面)")
    @GetMapping("/view/cover")
    public ResponseEntity<Object> coverOfMp3(HttpServletRequest request, String id) throws IOException {
        ResultUtil.checkParamIsNull(id);
        Optional<FileDocument> file = fileService.coverOfMp3(id, service.getUserName(request.getParameter(AuthInterceptor.JMAL_TOKEN)));
        return file.<ResponseEntity<Object>>map(fileDocument ->
                ResponseEntity.ok()
                        .header(HttpHeaders.CONTENT_DISPOSITION, "fileName=" + fileDocument.getName())
                        .header(HttpHeaders.CONTENT_TYPE, fileDocument.getContentType())
                        .header(HttpHeaders.CONTENT_LENGTH, fileDocument.getContent().length + "")
                        .header("Connection", "close")
                        .header(HttpHeaders.CONTENT_ENCODING, "utf-8")
                        .body(fileDocument.getContent())).orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND).body("找不到该文件"));
    }

    @ApiOperation("收藏文件或文件夹")
    @PostMapping("/favorite")
    public ResponseResult<Object> favorite(String[] fileIds) throws CommonException {
        if (fileIds != null && fileIds.length > 0) {
            List<String> list = Arrays.asList(fileIds);
            return fileService.favorite(list);
        } else {
            throw new CommonException(ExceptionType.MISSING_PARAMETERS.getCode(), ExceptionType.MISSING_PARAMETERS.getMsg());
        }
    }

    @ApiOperation("取消收藏")
    @PostMapping("/unFavorite")
    public ResponseResult<Object> unFavorite(String[] fileIds) throws CommonException {
        if (fileIds != null && fileIds.length > 0) {
            List<String> list = Arrays.asList(fileIds);
            return fileService.unFavorite(list);
        } else {
            throw new CommonException(ExceptionType.MISSING_PARAMETERS.getCode(), ExceptionType.MISSING_PARAMETERS.getMsg());
        }
    }

    @ApiOperation("删除文件")
    @DeleteMapping("/delete")
    public ResponseResult<Object> delete(String username, String[] fileIds) throws CommonException {
        if (fileIds != null && fileIds.length > 0) {
            List<String> list = Arrays.asList(fileIds);
            return fileService.delete(username, list);
        } else {
            throw new CommonException(ExceptionType.MISSING_PARAMETERS.getCode(), ExceptionType.MISSING_PARAMETERS.getMsg());
        }
    }

    @ApiOperation("重命名")
    @GetMapping("/rename")
    public ResponseResult<Object> rename(String newFileName, String username, String id) throws CommonException {
        return fileService.rename(newFileName, username, id);
    }

    @ApiOperation("移动文件/文件夹")
    @GetMapping("/move")
    public ResponseResult move(UploadApiParam upload, String[] froms, String to) throws CommonException {
        if (froms != null && froms.length > 0) {
            List<String> list = Arrays.asList(froms);
            return fileService.move(upload, list, to);
        } else {
            throw new CommonException(ExceptionType.MISSING_PARAMETERS.getCode(), ExceptionType.MISSING_PARAMETERS.getMsg());
        }
    }

    @ApiOperation("复制文件/文件夹")
    @GetMapping("/copy")
    public ResponseResult copy(UploadApiParam upload, String[] froms, String to) throws CommonException {
        if (froms != null && froms.length > 0) {
            List<String> list = Arrays.asList(froms);
            return fileService.copy(upload, list, to);
        } else {
            throw new CommonException(ExceptionType.MISSING_PARAMETERS.getCode(), ExceptionType.MISSING_PARAMETERS.getMsg());
        }
    }

    @ApiOperation("解压zip文件")
    @GetMapping("/unzip")
    public ResponseResult unzip(@RequestParam String fileId, String destFileId) throws CommonException {
        return fileService.unzip(fileId, destFileId);
    }

    @ApiOperation("获取目录下的文件")
    @GetMapping("/listfiles")
    public ResponseResult listFiles(@RequestParam String path, @RequestParam String username, Boolean tempDir) throws CommonException {
        if(tempDir == null){
            tempDir = false;
        }
        return fileService.listfiles(path, username, tempDir);
    }

    @ApiOperation("获取上级文件列表")
    @GetMapping("/upper-level-list")
    public ResponseResult upperLevelList(@RequestParam String path, @RequestParam String username) throws CommonException {
        return fileService.upperLevelList(path, username);
    }

    @ApiOperation("根据path删除文件/文件夹")
    @DeleteMapping("/delFile")
    public ResponseResult delFile(@RequestParam String path, @RequestParam String username) throws CommonException {
        return fileService.delFile(path, username);
    }
}
