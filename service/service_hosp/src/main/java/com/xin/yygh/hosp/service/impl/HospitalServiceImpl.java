package com.xin.yygh.hosp.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.xin.yygh.client.DictService;
import com.xin.yygh.common.utils.MD5;
import com.xin.yygh.hosp.mapper.HospitalSetMapper;
import com.xin.yygh.hosp.model.hosp.Hospital;
import com.xin.yygh.hosp.entities.HospitalSet;
import com.xin.yygh.hosp.repository.HospitalRepository;
import com.xin.yygh.hosp.service.HospitalService;
import com.xin.yygh.hosp.utils.HttpRequestUtils;
import com.xin.yygh.hosp.vo.hosp.HospitalQueryVo;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.ExampleMatcher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import javax.servlet.http.HttpServletRequest;
import java.util.Date;
import java.util.List;
import java.util.Map;

@Service
public class HospitalServiceImpl implements HospitalService {

    @Autowired
    private HospitalSetMapper hospitalSetMapper;

    @Autowired
    private HospitalRepository hospitalRepository;

    @Autowired
    private DictService dictService;

    @Override
    public boolean saveHospital(HttpServletRequest request) {
        // 1. 获取参数
        Map<String, Object> map = HttpRequestUtils.switchMap(request.getParameterMap());
        String signKey = (String)map.get("sign");
        String hoscode = (String) map.get("hoscode");

        if (verifySignKey(hoscode,signKey)) {
            String logoData = (String)map.get("logoData").toString().replace(" ","+");
            // 2. 转换成hospital对象
            String str = JSONObject.toJSONString(map);
            Hospital hospital = JSONObject.parseObject(str, Hospital.class);

            hospital.setLogoData(logoData);
            // 3. 保存到mongodb中
            // 判断mongodb是否有
            Hospital result = hospitalRepository.findByHoscode(hospital.getHoscode());
            if (result == null) {
                // 执行添加操作
                hospital.setCreateTime(new Date());
                hospital.setUpdateTime(new Date());
                hospital.setStatus(0);
                hospital.setIsDeleted(0);

            } else {
                hospital.setCreateTime(result.getCreateTime());
                hospital.setStatus(result.getStatus());
                hospital.setIsDeleted(result.getIsDeleted());
                // 执行修改操作
                hospital.setId(result.getId());
                hospital.setUpdateTime(new Date());
            }

            hospitalRepository.save(hospital);
        }

        return true;
    }

    @Override
    public Hospital getByHoscode(String hoscode) {
        return hospitalRepository.findByHoscode(hoscode);
    }

    // 带条件的分页查询
    @Override
    public Page<Hospital> getPageList(Integer pageNum, Integer pageSize, String dictCode, HospitalQueryVo hospitalQueryVo) {

        // 1. 构建查询条件
        Hospital hospital = new Hospital();
        BeanUtils.copyProperties(hospitalQueryVo,hospital);
        PageRequest pageRequest = PageRequest.of(pageNum - 1, pageSize);


        ExampleMatcher matcher = ExampleMatcher.matching() //构建对象
                .withMatcher("hosname", ExampleMatcher.GenericPropertyMatcher.of(ExampleMatcher.StringMatcher.CONTAINING)) //改变默认字符串匹配方式：包含
                .withIgnoreCase(true); //改变默认大小写忽略方式：忽略大小写
        Example<Hospital> hospitalExample = Example.of(hospital, matcher);

        Page<Hospital> page = hospitalRepository.findAll(hospitalExample, pageRequest);


        // 将hospital中的数字(医院等级，省 市 区)变成对应的值
        page.stream().forEach(hospital1 -> {
           packingHospital(hospital1,dictCode);
        });

        return page;
    }

    public Hospital packingHospital(Hospital hospital,String dictCode) {
        // 调用dictservice
        // 省名称
        String provinceName = dictService.getNameByValue(Long.parseLong(hospital.getProvinceCode()));
        // 市名称
        String cityName = dictService.getNameByValue(Long.parseLong(hospital.getCityCode()));
        // 区名称
        String districtName = dictService.getNameByValue(Long.parseLong(hospital.getDistrictCode()));

        // 医院等级
        Long pid = dictService.getIdByDictCode(dictCode);
        String hosLeveName = dictService.getNameByPidAndValue(Long.parseLong(hospital.getHostype()),pid);

        hospital.setProvinceCode(provinceName);
        hospital.setCityCode(cityName);
        hospital.setDistrictCode(districtName);
        hospital.setHostype(hosLeveName);
        String address = provinceName + cityName + districtName + hospital.getAddress();
        hospital.setAddress(address);
        return hospital;
    }

    @Override
    public void updateStatusById(String id, Integer status) {
        // 1. 根据id查询出来
        Hospital hospital = hospitalRepository.findById(id).get();

        // 2. 修改状态
        hospital.setStatus(status);

        // 3. 修改
        hospitalRepository.save(hospital);
    }

    @Override
    public Hospital getHospitalById(String id) {
        // 1. 根据id查询出来
        Hospital hospital = hospitalRepository.findById(id).get();

        // 2. 封装hospital
        packingHospital(hospital,"Hostype");

        return hospital;
    }

    @Override
    public List<Hospital> getListByHosname(String hosname) {
        List<Hospital> hospitalList = hospitalRepository.findByHosnameLike(hosname);
        return hospitalList;
    }

    @Override
    public List<Hospital> findAll() {
        List<Hospital> list = hospitalRepository.findAll();
        for (Hospital hospital : list) {
            String hosLeveName = dictService.getNameByPidAndValue(Long.parseLong(hospital.getHostype()),10000L);
            hospital.setHostype(hosLeveName);
        }
        return list;
    }

    @Override
    public List<Hospital> getListByAddress(HospitalQueryVo hospitalQueryVo) {
        // 将VO 转换成 DO
        // VO传递过来的是省、市、区的编号信息
        Hospital hospital = new Hospital();
        BeanUtils.copyProperties(hospitalQueryVo,hospital);

        // 根据省、市、区的编号信息来进行查询
        Example<Hospital> example = Example.of(hospital);
        List<Hospital> hospitals = hospitalRepository.findAll(example);
        return hospitals;
    }

    @Override
    public List<Hospital> getListByHosTypeAndDistrictCode(String hosType, String districtCode) {
        Hospital hospital = new Hospital();
        if (StringUtils.hasLength(hosType)) {
            hospital.setHostype(hosType);
        }
        if (StringUtils.hasLength(districtCode)) {
            hospital.setDistrictCode(districtCode);
        }
        // 根据省、市、区的编号信息来进行查询
        Example<Hospital> example = Example.of(hospital);
        List<Hospital> hospitals = hospitalRepository.findAll(example);
        return hospitals;
    }

    @Override
    public Hospital getDetailByHoscode(String hoscode) {
        Hospital byHoscode = hospitalRepository.findByHoscode(hoscode);
        packingHospital(byHoscode,"Hostype");
        return byHoscode;
    }

    public boolean verifySignKey(String hoscoed, String signKey) {
        // 3. 判断系统中是否有传过来的对象
        // 根据hospcode来查询出对象
        QueryWrapper<HospitalSet> query = new QueryWrapper<HospitalSet>().eq("hoscode", hoscoed);
        HospitalSet hospitalSet = hospitalSetMapper.selectOne(query);

        String platformSignKey = MD5.encrypt(hospitalSet.getSignKey());
        String requestSignKey  = signKey;

        // 表示当前医院设置中不存在第三方传送过来的信息
        if (platformSignKey == null && requestSignKey == null && !platformSignKey.equals(requestSignKey)) {
            return false;
        }
        return true;
    }


}
