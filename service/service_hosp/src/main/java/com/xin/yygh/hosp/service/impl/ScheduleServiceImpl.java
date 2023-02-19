package com.xin.yygh.hosp.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.xin.yygh.common.exception.YyghException;
import com.xin.yygh.hosp.model.hosp.BookingRule;
import com.xin.yygh.hosp.model.hosp.Department;
import com.xin.yygh.hosp.model.hosp.Hospital;
import com.xin.yygh.hosp.model.hosp.Schedule;
import com.xin.yygh.hosp.repository.DepartmentRepository;
import com.xin.yygh.hosp.repository.HospitalRepository;
import com.xin.yygh.hosp.repository.ScheduleRepository;
import com.xin.yygh.hosp.service.DepartmentService;
import com.xin.yygh.hosp.service.ScheduleService;
import com.xin.yygh.hosp.vo.hosp.BookingScheduleRuleVo;
import com.xin.yygh.hosp.vo.hosp.ScheduleOrderVo;
import org.joda.time.DateTime;
import org.joda.time.DateTimeConstants;
import org.joda.time.format.DateTimeFormat;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.ldap.AutoConfigureDataLdap;
import org.springframework.data.domain.*;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.AggregationResults;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import javax.xml.crypto.Data;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class ScheduleServiceImpl implements ScheduleService {

    @Autowired
    private ScheduleRepository scheduleRepository;

    @Autowired
    private MongoTemplate mongoTemplate;

    @Autowired
    private HospitalRepository hospitalRepository;

    @Autowired
    private DepartmentRepository departmentRepository;

    @Override
    public void save(Map<String, Object> stringObjectMap) {

        String str = JSONObject.toJSONString(stringObjectMap);
        Schedule schedule = JSONObject.parseObject(str, Schedule.class);

        String hoscode = schedule.getHoscode();
        String depcode = schedule.getDepcode();
        String hosScheduleId = schedule.getHosScheduleId();

        Schedule result = scheduleRepository.findByHoscodeAndDepcodeAndHosScheduleId(hoscode,depcode,hosScheduleId);

        if (result == null) {
            schedule.setCreateTime(new Date());
            schedule.setUpdateTime(new Date());
            schedule.setIsDeleted(0);
        } else {
            schedule.setUpdateTime(new Date());
            schedule.setId(result.getId());
            schedule.setCreateTime(result.getCreateTime());
            schedule.setIsDeleted(result.getIsDeleted());
        }

        scheduleRepository.save(schedule);
    }

    @Override
    public Page<Schedule> findPage(Map<String, Object> stringObjectMap) {
        String hoscode = (String) stringObjectMap.get("hoscode");
        Integer pageNum = Integer.parseInt((String) stringObjectMap.get("page"));
        Integer pageSize = Integer.parseInt((String) stringObjectMap.get("limit"));

        Schedule querySchedule = new Schedule();
        querySchedule.setHoscode(hoscode);
        Example example = Example.of(querySchedule);

        Pageable pageable = PageRequest.of(pageNum-1,pageSize);

        return scheduleRepository.findAll(example,pageable);
    }

    @Override
    public void delete(Map<String, Object> stringObjectMap) {
        String hoscode = (String) stringObjectMap.get("hoscode");
        String hosScheduleId = (String) stringObjectMap.get("hosScheduleId");
        Schedule schedule = scheduleRepository.findByHoscodeAndHosScheduleId(hoscode, hosScheduleId);

        scheduleRepository.delete(schedule);
    }

    @Override
    public Map<String, Object> page(Integer pageNum, Integer pageSize, String hoscode, String depcode) {
        Map<String, Object> map = new HashMap<>();
        // 1.根据工作日来进行分组
        // 构建查询条件
        Criteria criteria = new Criteria("hoscode").is(hoscode);
        if (!StringUtils.isEmpty(depcode) && StringUtils.hasLength(depcode)) {
            criteria.and("depcode").is(depcode);
        }
        Aggregation aggregation = Aggregation.newAggregation(
                // 查询条件
                Aggregation.match(criteria),
                // 进行聚合操作
                Aggregation.group("workDate")
                           .first("workDate").as("workDate")
                           .sum("reservedNumber").as("reservedNumber")
                           .sum("availableNumber").as("availableNumber")
                           .count().as("docCount"),
                // 排序
                Aggregation.sort(Sort.Direction.ASC,"workDate"),
                // 分组
                Aggregation.skip((pageNum - 1) * pageSize),
                Aggregation.limit(pageSize)
        );
        AggregationResults<BookingScheduleRuleVo> aggregate = mongoTemplate.aggregate(aggregation, Schedule.class, BookingScheduleRuleVo.class);

        // 查询工作日的总数
        Aggregation aggregation2 = Aggregation.newAggregation(
                // 查询条件
                Aggregation.match(criteria),
                // 进行聚合操作
                Aggregation.group("workDate")
        );
        AggregationResults<BookingScheduleRuleVo> aggregate2 = mongoTemplate.aggregate(aggregation2, Schedule.class, BookingScheduleRuleVo.class);

        // 查询医院名称
        Hospital hospital = hospitalRepository.findByHoscode(hoscode);

        // 封装日期
        List<BookingScheduleRuleVo> bookingScheduleRuleVoList = aggregate.getMappedResults();
        for (BookingScheduleRuleVo bookingScheduleRuleVo : bookingScheduleRuleVoList) {
            Date workDate = bookingScheduleRuleVo.getWorkDate();
            DateTime dateTime = new DateTime(workDate);
            String dayOfWeek = this.getDayOfWeek(dateTime);
            bookingScheduleRuleVo.setDayOfWeek(dayOfWeek);

            bookingScheduleRuleVo.setWorkDateMd(bookingScheduleRuleVo.getWorkDate());

        }

        map.put("list", bookingScheduleRuleVoList);
        map.put("total", aggregate2.getMappedResults().size());
        map.put("hosname", hospital.getHosname());

        return map;
    }

    @Override
    public List<Schedule> getScheduleByHoscodeAndDepcodeAndWorkDate(String hoscode, String depcode, String workDate) {
        Date date = DateTime.parse(workDate).toDate();
        List<Schedule> schedules = scheduleRepository.findByHoscodeAndDepcodeAndWorkDate(hoscode,depcode,date);
        return schedules;
    }

    @Override
    public Map<String, Object> getPageList(Integer pageNum, Integer pageSize, String hoscode, String depcode) {
        // 查询出医院信息
        Hospital hospital = hospitalRepository.findByHoscode(hoscode);
        if (hospital == null) {
            throw new YyghException(20001, "没有该医院信息");
        }

        // 获取该医院的预约的规则
        BookingRule bookingRule = hospital.getBookingRule();
        Map<String, Object> map = this.getDatePageList(pageNum, pageSize, bookingRule);

        // 查询排班信息
        ArrayList<Date> currentPage = (ArrayList<Date>)map.get("currentPage");

        for (Date date : currentPage) {
            Date date1 = new DateTime(date).toDate();

        }

        //获取可预约日期科室剩余预约数
        Criteria criteria = Criteria.where("hoscode").is(hoscode).and("depcode").is(depcode).and("workDate").in(currentPage);
        Aggregation agg = Aggregation.newAggregation(
                Aggregation.match(criteria),
                Aggregation.group("workDate")//分组字段
                        .first("workDate").as("workDate")
                        .count().as("docCount")
                        .sum("availableNumber").as("availableNumber")
                        .sum("reservedNumber").as("reservedNumber")
        );
        AggregationResults<BookingScheduleRuleVo> aggregate = mongoTemplate.aggregate(agg, Schedule.class, BookingScheduleRuleVo.class);
        //List<BookingScheduleRuleVo> mappedResults = aggregate.getMappedResults();

        Map<Date, BookingScheduleRuleVo> collect = aggregate.getMappedResults().stream().collect(Collectors.toMap(BookingScheduleRuleVo::getWorkDate, BookingScheduleRuleVo -> BookingScheduleRuleVo));

        List<BookingScheduleRuleVo> bookingScheduleRuleVoList = new ArrayList<BookingScheduleRuleVo>();
        int size = currentPage.size();
        for (int i = 0; i < size; i++) {
            Date date = currentPage.get(i);
            BookingScheduleRuleVo bookingScheduleRuleVo = collect.get(date);
            // 表示这一天没有排班
            if (bookingScheduleRuleVo == null) {
                // 设置信息
                bookingScheduleRuleVo = new BookingScheduleRuleVo();
                //就诊医生人数
                bookingScheduleRuleVo.setDocCount(0);
                //科室剩余预约数  -1表示无号
                bookingScheduleRuleVo.setAvailableNumber(-1);

            }
            bookingScheduleRuleVo.setWorkDate(date);
            bookingScheduleRuleVo.setWorkDateMd(date);
            //计算当前预约日期为周几
            String dayOfWeek = this.getDayOfWeek(new DateTime(date));
            bookingScheduleRuleVo.setDayOfWeek(dayOfWeek);

            // 设置最后一天为即将挂号
            int pages = (int)map.get("pages");
            if (pageNum == pages && i == size - 1) {
                // 设置最后一天为即将预约
                bookingScheduleRuleVo.setStatus(1);
            } else {
                bookingScheduleRuleVo.setStatus(0);
            }

            // 判断如果当前是时间超过了预约的结束时间，显示停止预约
            if (pageNum == 1 && i == 0) {
                // 获取预约结束时间
                DateTime hospitalBookStopTime = this.getDateTime(new Date(), bookingRule.getStopTime());
                if (hospitalBookStopTime.isBeforeNow()) {
                    // 当天的预约结束
                    bookingScheduleRuleVo.setStatus(-1);
                }
            }

            // 添加到list中
            bookingScheduleRuleVoList.add(bookingScheduleRuleVo);
        }

        Map<String, Object> result = new HashMap<String, Object>();
        int total = (int)map.get("total");
        result.put("total", total);
        result.put("bookingScheduleList", bookingScheduleRuleVoList);
        //其他基础数据
        Map<String, String> baseMap = new HashMap<>();
        //医院名称
        baseMap.put("hosname", hospital.getHosname());
        //科室
        Department department = departmentRepository.findByHoscodeAndDepcode(hoscode, depcode);
        //大科室名称
        baseMap.put("bigname", department.getBigname());
        //科室名称
        baseMap.put("depname", department.getDepname());
        //月
        baseMap.put("workDateString", new DateTime().toString("yyyy年MM月"));
        //放号时间
        baseMap.put("releaseTime", bookingRule.getReleaseTime());
        //停号时间
        baseMap.put("stopTime", bookingRule.getStopTime());
        result.put("baseMap", baseMap);
        return result;
    }

    @Override
    public Schedule getScheduleDetailById(String scheduleId) {
        Schedule schedule = scheduleRepository.findById(scheduleId).get();

        String hoscode = schedule.getHoscode();
        String hosName= hospitalRepository.findByHoscode(hoscode).getHosname();

        String depcode = schedule.getDepcode();
        String depName = departmentRepository.findByHoscodeAndDepcode(hoscode, depcode).getDepname();

        Map<String, Object> map = new HashMap<String, Object>();
        map.put("hosname", hosName);
        map.put("depname", depName);
        schedule.setParam(map);
        return schedule;
    }

    @Override
    public ScheduleOrderVo getById(String scheduleId) {
        // 1.根据scheduleId查询出排班信息
        Schedule schedule = scheduleRepository.findById(scheduleId).get();
        // 2.转换成ScheduleOrderVo对象
        ScheduleOrderVo scheduleOrderVo = new ScheduleOrderVo();
        BeanUtils.copyProperties(schedule, scheduleOrderVo);
        scheduleOrderVo.setReserveDate(schedule.getWorkDate());
        scheduleOrderVo.setReserveTime(schedule.getWorkTime());
        // 3.获取医院名称
        Hospital hospital = hospitalRepository.findByHoscode(schedule.getHoscode());
        scheduleOrderVo.setHosname(hospital.getHosname());
        // 4.获取科室名称
        Department department = departmentRepository.findByHoscodeAndDepcode(hospital.getHoscode(), schedule.getDepcode());
        scheduleOrderVo.setDepname(department.getDepname());
        // 5. 设置退号时间
        Integer quitDay = hospital.getBookingRule().getQuitDay();
        String quitTime = hospital.getBookingRule().getQuitTime();
        Date date = new DateTime(schedule.getWorkDate()).plusDays(quitDay).toDate();
        Date bookQuitTime = this.getDateTime(date, quitTime).toDate();
        scheduleOrderVo.setQuitTime(bookQuitTime);
        // 设置当天预约停止时间
        String stopTime = hospital.getBookingRule().getStopTime();
        Date workDate = schedule.getWorkDate();
        Date bookStopTime = this.getDateTime(workDate, stopTime).toDate();
        scheduleOrderVo.setStopTime(bookStopTime);
        return scheduleOrderVo;
    }

    @Override
    public boolean updateAvailableNumber(String scheduleId, Integer availableNumber) {
        Schedule schedule = scheduleRepository.findById(scheduleId).get();
        schedule.setAvailableNumber(availableNumber);
        schedule.setUpdateTime(new Date());
        scheduleRepository.save(schedule);
        return true;
    }

    private Map<String, Object> getDatePageList(Integer pageNum, Integer pageSize, BookingRule bookingRule) {

        // 1.判断当前时间是否超过了当前医院设置的预约时间,超过了预约天数+1
        Integer cycle = bookingRule.getCycle();
        String releaseTime = bookingRule.getReleaseTime();

        // 当前医院的预约开始时间 2023-2-15 10:55
        DateTime dateTime = this.getDateTime(new Date(), releaseTime);
        // 判断当前时间是否超过医院规定时间
        if (dateTime.isBeforeNow()) {
            // 超过了规定时间
            cycle++;
        }

        ArrayList<Date> dateList = new ArrayList<>();
        // 2.拿到预约的日期
        // DateTime 转换成Date时不能之间 toDate(),而是要先格式化再toDate()
        for (int i = 0; i < cycle; i++) {
            //计算当前预约日期
            DateTime curDateTime = new DateTime().plusDays(i);
            String dateString = curDateTime.toString("yyyy-MM-dd");
            dateList.add(new DateTime(dateString).toDate());
        }

        // 3.进行分页
        ArrayList<Date> currentDatePageList = dateList.stream().skip((pageNum - 1) * pageSize)
                                                           .limit(pageSize)
                                                           .collect(Collectors.toCollection(ArrayList<Date>::new));
        // 总页数
        int pages = cycle / pageSize;
        if (cycle % pageSize > 0) {
            pages++;
        }
        Map<String, Object> map = new HashMap<>();
        map.put("total", dateList.size());
        map.put("currentPage", currentDatePageList);
        map.put("pages", pages);
        return map;
    }

    private DateTime getDateTime(Date date, String timeString) {
        String dateTimeString = new DateTime(date).toString("yyyy-MM-dd") + " "+ timeString;
        DateTime dateTime = DateTimeFormat.forPattern("yyyy-MM-dd HH:mm").parseDateTime(dateTimeString);
        return dateTime;
    }

    private String getDayOfWeek(DateTime dateTime) {
        String dayOfWeek = "";
        switch (dateTime.getDayOfWeek()) {
            case DateTimeConstants.SUNDAY:
                dayOfWeek = "周日";
                break;
            case DateTimeConstants.MONDAY:
                dayOfWeek = "周一";
                break;
            case DateTimeConstants.TUESDAY:
                dayOfWeek = "周二";
                break;
            case DateTimeConstants.WEDNESDAY:
                dayOfWeek = "周三";
                break;
            case DateTimeConstants.THURSDAY:
                dayOfWeek = "周四";
                break;
            case DateTimeConstants.FRIDAY:
                dayOfWeek = "周五";
                break;
            case DateTimeConstants.SATURDAY:
                dayOfWeek = "周六";
            default:
                break;
        }
        return dayOfWeek;
    }


}
