package com.xin.yygh.hosp.controller.admin;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.xin.yygh.common.Result;
import com.xin.yygh.common.utils.MD5;
import com.xin.yygh.hosp.entities.HospitalSet;
import com.xin.yygh.hosp.service.HospitalSetService;
import com.xin.yygh.hosp.vo.hosp.HospitalSetQueryVo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@Api(tags = "医院信息的操作")
@RestController
@RequestMapping("/admin/hosp/hospitalSet")
public class HospSetController {


    @Autowired
    private HospitalSetService hospitalSetService;

    /**
     * 修改医院的状态
     * @param id 医院id
     * @param status 医院状态  0 : 锁定状态  ，  1 : 可用状态
     * @return result
     */
    @ApiOperation(value = "修改医院的状态")
    @PutMapping("/changeStatus/{id}/{status}")
    public Result changeStatusById(@ApiParam(name = "id", value = "医院id") @PathVariable Long id,
                                   @ApiParam(name = "status", value = "医院状态") @PathVariable Integer status) {
        // 1. 找到对应id的医院信息
        HospitalSet hospitalSet = hospitalSetService.getById(id);

        // 2. 修改医院状态
        hospitalSet.setStatus(status);
        hospitalSetService.updateById(hospitalSet);

        return Result.ok();
    }

    /**
     * 修改医院信息
     * @param hospitalSet 医院信息
     * @return result
     */
    @ApiOperation(value = "修改医院信息")
    @PutMapping("/update")
    public Result update(@ApiParam(name = "hospitalSet", value = "医院信息") @RequestBody HospitalSet hospitalSet) {
       hospitalSetService.updateById(hospitalSet);
       return Result.ok();
    }
    /**
     * 根据医院id查询医院信息并回显
     * @param id 医院id
     * @return result
     */
    @ApiOperation(value = "回显指定id的医院数据")
    @GetMapping("/detail/{id}")
    public Result toDetailPage(@ApiParam(name = "id",value = "医院id") @PathVariable Long id) {
        HospitalSet hospitalSet = hospitalSetService.getById(id);
        return Result.ok().data("item",hospitalSet);
    }

    /**
     * 添加医院信息
     * @param hospitalSet 医院信息
     * @return result
     */
    @ApiOperation(value = "添加医院")
    @PostMapping("/save")
    public Result saveHospitalSet(@ApiParam(name = "hospitalSet", value = "医院信息") @RequestBody HospitalSet hospitalSet) {
        // 添加sign_key UUID + 时间戳 + MD5加密
        String str = UUID.randomUUID().toString().replace("-","") + System.currentTimeMillis();
        String sign_key = MD5.encrypt(str);

        // 完善信息
        hospitalSet.setSignKey(sign_key);
        hospitalSet.setStatus(1);

        hospitalSetService.save(hospitalSet);
        return Result.ok();
    }

    /**
     * 批量删除医院信息
     * @param ids 选中的医院id
     * @return result
     */
    @ApiOperation(value = "批量删除医院信息")
    @DeleteMapping("/delete")
    public Result batchDelete(@ApiParam(name = "ids", value = "选中的医院id") @RequestBody List<Integer> ids) {
        hospitalSetService.removeByIds(ids);
        return Result.ok();
    }

    /**
     * 根据医院id删除
     * @param id 医院id
     * @return Result对象
     */
    @ApiOperation(value = "根据医院id来删除")
    @DeleteMapping("/delete/{id}")
    public Result deleteById(@ApiParam(name = "id", value = "医院id") @PathVariable Long id) {
        hospitalSetService.removeById(id);
        return Result.ok();
    }

    /**
     * 根据查询用户输入的条件来进行分页查询，如果没有条件默认查询全部
     * @param pageNum 当前页码
     * @param pageSize 当前页显示的条数
     * @param hospitalSetQueryVo 查询信息
     * @return 同一返回对象Result
     */
    @ApiOperation(value = "根据条件进行分页查询，如果没有条件默认查询全部")
    @PostMapping("/page/{pageNum}/{pageSize}")
    public Result getPageInfo(@ApiParam(name = "pageNum", value = "当前页码") @PathVariable Integer pageNum,
                              @ApiParam(name = "pageSize", value = "当前页显示个数") @PathVariable Integer pageSize,
                              @RequestBody HospitalSetQueryVo hospitalSetQueryVo) {
        // 1. 创建分页条件r
        Page<HospitalSet> page = new Page<>(pageNum, pageSize);

        // 2. 通过判断是否传入值来判断是条件查询 还是 查询全部
        // 判断医院名是否传入
        QueryWrapper<HospitalSet> wrapper = new QueryWrapper<>();
        if ( !StringUtils.isEmpty(hospitalSetQueryVo.getHosname()) && StringUtils.hasLength(hospitalSetQueryVo.getHosname())) {
            // 构造查询条件
            wrapper.like("hosname",hospitalSetQueryVo.getHosname());
        }

        // 判断是否传入医院的编号
        if (!StringUtils.isEmpty(hospitalSetQueryVo.getHoscode()) && StringUtils.hasLength(hospitalSetQueryVo.getHoscode())) {
            // 构造查询条件
            wrapper.eq("hoscode",hospitalSetQueryVo.getHoscode());
        }
        wrapper.orderByDesc("id");

        // 3. 进行分页查询
        hospitalSetService.page(page,wrapper);

        // 4. 封装结果对象
        Result result = Result.ok().data("total", page.getTotal())
                                   .data("items", page.getRecords());
        return result;
    }

    @ApiOperation(value = "查询所有医院的信息")
    @GetMapping("/findAll")
    public Result findAll() {
        // 1. 查询出所有医院信息
        List<HospitalSet> list = hospitalSetService.list();
        // 2. 创建返回同一结果对象
        return Result.ok().data("items",list);
    }


}
