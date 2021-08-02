package com.jmal.clouddisk.controller.rest;

import com.jmal.clouddisk.annotation.LogOperatingFun;
import com.jmal.clouddisk.annotation.Permission;
import com.jmal.clouddisk.model.HeartwingsDO;
import com.jmal.clouddisk.model.LogOperation;
import com.jmal.clouddisk.model.WebsiteSettingDTO;
import com.jmal.clouddisk.model.WebsiteSettingDO;
import com.jmal.clouddisk.service.impl.SettingService;
import com.jmal.clouddisk.util.ResponseResult;
import com.jmal.clouddisk.util.ResultUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @author jmal
 * @Description 网站设置
 * @Date 2020/10/28 5:29 下午
 */
@RestController
@Api(tags = "网站设置")
public class WebsiteSettingController {

    @Autowired
    private SettingService settingService;

    @ApiOperation("获取网站设置")
    @GetMapping("/website/setting")
    @LogOperatingFun(logType = LogOperation.Type.BROWSE)
    public ResponseResult<WebsiteSettingDTO> getWebsiteSetting() {
        return ResultUtil.success(settingService.getWebsiteSetting());
    }

    @ApiOperation("获取网站备案信息")
    @GetMapping("/public/website/record")
    @LogOperatingFun(logType = LogOperation.Type.BROWSE)
    public ResponseResult<WebsiteSettingDTO> getWebsiteRecord() {
        return ResultUtil.success(settingService.getWebsiteRecord());
    }

    @ApiOperation("获取网站心语记录")
    @GetMapping("/website/heartwings")
    @LogOperatingFun(logType = LogOperation.Type.BROWSE)
    public ResponseResult<List<HeartwingsDO>> getWebsiteHeartwings(@RequestParam Integer page, @RequestParam Integer pageSize, @RequestParam String order) {
        return settingService.getWebsiteHeartwings(page, pageSize, order);
    }

    @ApiOperation("更新网站设置")
    @PutMapping("/website/setting/update")
    @Permission("website:set:update")
    @LogOperatingFun
    public ResponseResult<Object> update(@RequestBody WebsiteSettingDO websiteSettingDO) {
        return settingService.websiteUpdate(websiteSettingDO);
    }
}

