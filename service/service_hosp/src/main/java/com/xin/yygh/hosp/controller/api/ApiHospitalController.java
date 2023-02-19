package com.xin.yygh.hosp.controller.api;

import com.alibaba.fastjson.JSONObject;
import com.xin.yygh.common.Result;
import com.xin.yygh.hosp.model.hosp.Hospital;
import com.xin.yygh.hosp.service.HospitalService;
import com.xin.yygh.hosp.utils.HttpRequestUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;

@RestController
@RequestMapping("/api/hosp")
public class ApiHospitalController {

    @Autowired
    private HospitalService hospitalService;

    @RequestMapping("/hospital/show")
    public Result showHospital(HttpServletRequest request) {
        Map<String, String[]> map = request.getParameterMap();
        Map<String, Object> resultMap = HttpRequestUtils.switchMap(map);

        Hospital hospital = hospitalService.getByHoscode((String) resultMap.get("hoscode"));
        String str = JSONObject.toJSONString(hospital);
        Map parseMap= JSONObject.parseObject(str, Map.class);
        return Result.ok().code(200).data(parseMap);
    }


    @RequestMapping("/saveHospital")
    public Result saveHospital(HttpServletRequest request) {
       boolean isSuccess = hospitalService.saveHospital(request);
       if (isSuccess) {
           return Result.ok().code(200);
       } else {
           return Result.error();
       }
    }
}
