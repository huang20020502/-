package com.xin.yygh.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(value = "service-cmn")
public interface DictService {

    @GetMapping("/admin/cmn/{pid}/{value}")
    String getNameByPidAndValue(@PathVariable(value = "value") Long value, @PathVariable(value = "pid") Long pid);

    @GetMapping("/admin/cmn/{value}")
    String getNameByValue(@PathVariable(value = "value") Long value);

    @GetMapping("/admin/cmn/dict/id/{dictCode}")
    Long getIdByDictCode(@PathVariable(value = "dictCode") String dictCode);
}
