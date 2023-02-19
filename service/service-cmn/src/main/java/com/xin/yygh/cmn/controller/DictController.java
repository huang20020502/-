package com.xin.yygh.cmn.controller;


import com.netflix.client.http.HttpResponse;
import com.xin.yygh.cmn.service.DictService;
import com.xin.yygh.common.Result;
import com.xin.yygh.hosp.model.cmn.Dict;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

@Api(tags = "字典api")
@RestController
@RequestMapping("/admin/cmn")
public class DictController {

    @Autowired
    private DictService dictService;

    @GetMapping("/findByDictCode/{dictCode}")
    public Result findByDictCode(@PathVariable String dictCode) {
        List<Dict> list = dictService.findByDictCode(dictCode);
        return Result.ok().data("list", list);
    }

    @GetMapping("/{pid}/{value}")
    public String getNameByPidAndValue(@PathVariable(value = "value") Long value,
                                       @PathVariable(value = "pid") Long pid) {
        return dictService.getNameByPidAndValue(pid,value);
    }

    @GetMapping("/{value}")
    public String getNameByValue(@PathVariable(value = "value") Long value) {
        return dictService.getNameByValue(value);
    }

    @GetMapping("/dict/id/{dictCode}")
    public Long getIdByDictCode(@PathVariable(value = "dictCode") String dictCode) {
        return dictService.getIdByDictCode(dictCode);
    }


    @CacheEvict(value = "dict", allEntries = true)
    @PostMapping("/upload")
    public Result uploadFile(MultipartFile file) throws IOException {
        dictService.upload(file);
        return Result.ok();
    }

    @GetMapping("/download")
    public void download(HttpServletResponse response) throws Exception {
        dictService.downloadDict(response);
    }

    @Cacheable(value = "dict" , key = "'selectIndexList:'+#pid")
    @ApiOperation(value = "根据父id来查询对应的子元素")
    @GetMapping("/findChildrenData/{pid}")
    public Result getChildrenListByPid(@ApiParam(name = "pid", value = "父id") @PathVariable Long pid) {
        List<Dict> childrenList = dictService.getChildrenList(pid);
        return Result.ok().data("items",childrenList);
    }
}
