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
        // 1.??????????????????????????????
        // ??????????????????
        Criteria criteria = new Criteria("hoscode").is(hoscode);
        if (!StringUtils.isEmpty(depcode) && StringUtils.hasLength(depcode)) {
            criteria.and("depcode").is(depcode);
        }
        Aggregation aggregation = Aggregation.newAggregation(
                // ????????????
                Aggregation.match(criteria),
                // ??????????????????
                Aggregation.group("workDate")
                           .first("workDate").as("workDate")
                           .sum("reservedNumber").as("reservedNumber")
                           .sum("availableNumber").as("availableNumber")
                           .count().as("docCount"),
                // ??????
                Aggregation.sort(Sort.Direction.ASC,"workDate"),
                // ??????
                Aggregation.skip((pageNum - 1) * pageSize),
                Aggregation.limit(pageSize)
        );
        AggregationResults<BookingScheduleRuleVo> aggregate = mongoTemplate.aggregate(aggregation, Schedule.class, BookingScheduleRuleVo.class);

        // ????????????????????????
        Aggregation aggregation2 = Aggregation.newAggregation(
                // ????????????
                Aggregation.match(criteria),
                // ??????????????????
                Aggregation.group("workDate")
        );
        AggregationResults<BookingScheduleRuleVo> aggregate2 = mongoTemplate.aggregate(aggregation2, Schedule.class, BookingScheduleRuleVo.class);

        // ??????????????????
        Hospital hospital = hospitalRepository.findByHoscode(hoscode);

        // ????????????
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
        // ?????????????????????
        Hospital hospital = hospitalRepository.findByHoscode(hoscode);
        if (hospital == null) {
            throw new YyghException(20001, "?????????????????????");
        }

        // ?????????????????????????????????
        BookingRule bookingRule = hospital.getBookingRule();
        Map<String, Object> map = this.getDatePageList(pageNum, pageSize, bookingRule);

        // ??????????????????
        ArrayList<Date> currentPage = (ArrayList<Date>)map.get("currentPage");

        for (Date date : currentPage) {
            Date date1 = new DateTime(date).toDate();

        }

        //??????????????????????????????????????????
        Criteria criteria = Criteria.where("hoscode").is(hoscode).and("depcode").is(depcode).and("workDate").in(currentPage);
        Aggregation agg = Aggregation.newAggregation(
                Aggregation.match(criteria),
                Aggregation.group("workDate")//????????????
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
            // ???????????????????????????
            if (bookingScheduleRuleVo == null) {
                // ????????????
                bookingScheduleRuleVo = new BookingScheduleRuleVo();
                //??????????????????
                bookingScheduleRuleVo.setDocCount(0);
                //?????????????????????  -1????????????
                bookingScheduleRuleVo.setAvailableNumber(-1);

            }
            bookingScheduleRuleVo.setWorkDate(date);
            bookingScheduleRuleVo.setWorkDateMd(date);
            //?????????????????????????????????
            String dayOfWeek = this.getDayOfWeek(new DateTime(date));
            bookingScheduleRuleVo.setDayOfWeek(dayOfWeek);

            // ?????????????????????????????????
            int pages = (int)map.get("pages");
            if (pageNum == pages && i == size - 1) {
                // ?????????????????????????????????
                bookingScheduleRuleVo.setStatus(1);
            } else {
                bookingScheduleRuleVo.setStatus(0);
            }

            // ??????????????????????????????????????????????????????????????????????????????
            if (pageNum == 1 && i == 0) {
                // ????????????????????????
                DateTime hospitalBookStopTime = this.getDateTime(new Date(), bookingRule.getStopTime());
                if (hospitalBookStopTime.isBeforeNow()) {
                    // ?????????????????????
                    bookingScheduleRuleVo.setStatus(-1);
                }
            }

            // ?????????list???
            bookingScheduleRuleVoList.add(bookingScheduleRuleVo);
        }

        Map<String, Object> result = new HashMap<String, Object>();
        int total = (int)map.get("total");
        result.put("total", total);
        result.put("bookingScheduleList", bookingScheduleRuleVoList);
        //??????????????????
        Map<String, String> baseMap = new HashMap<>();
        //????????????
        baseMap.put("hosname", hospital.getHosname());
        //??????
        Department department = departmentRepository.findByHoscodeAndDepcode(hoscode, depcode);
        //???????????????
        baseMap.put("bigname", department.getBigname());
        //????????????
        baseMap.put("depname", department.getDepname());
        //???
        baseMap.put("workDateString", new DateTime().toString("yyyy???MM???"));
        //????????????
        baseMap.put("releaseTime", bookingRule.getReleaseTime());
        //????????????
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
        // 1.??????scheduleId?????????????????????
        Schedule schedule = scheduleRepository.findById(scheduleId).get();
        // 2.?????????ScheduleOrderVo??????
        ScheduleOrderVo scheduleOrderVo = new ScheduleOrderVo();
        BeanUtils.copyProperties(schedule, scheduleOrderVo);
        scheduleOrderVo.setReserveDate(schedule.getWorkDate());
        scheduleOrderVo.setReserveTime(schedule.getWorkTime());
        // 3.??????????????????
        Hospital hospital = hospitalRepository.findByHoscode(schedule.getHoscode());
        scheduleOrderVo.setHosname(hospital.getHosname());
        // 4.??????????????????
        Department department = departmentRepository.findByHoscodeAndDepcode(hospital.getHoscode(), schedule.getDepcode());
        scheduleOrderVo.setDepname(department.getDepname());
        // 5. ??????????????????
        Integer quitDay = hospital.getBookingRule().getQuitDay();
        String quitTime = hospital.getBookingRule().getQuitTime();
        Date date = new DateTime(schedule.getWorkDate()).plusDays(quitDay).toDate();
        Date bookQuitTime = this.getDateTime(date, quitTime).toDate();
        scheduleOrderVo.setQuitTime(bookQuitTime);
        // ??????????????????????????????
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

        // 1.??????????????????????????????????????????????????????????????????,?????????????????????+1
        Integer cycle = bookingRule.getCycle();
        String releaseTime = bookingRule.getReleaseTime();

        // ????????????????????????????????? 2023-2-15 10:55
        DateTime dateTime = this.getDateTime(new Date(), releaseTime);
        // ????????????????????????????????????????????????
        if (dateTime.isBeforeNow()) {
            // ?????????????????????
            cycle++;
        }

        ArrayList<Date> dateList = new ArrayList<>();
        // 2.?????????????????????
        // DateTime ?????????Date??????????????? toDate(),????????????????????????toDate()
        for (int i = 0; i < cycle; i++) {
            //????????????????????????
            DateTime curDateTime = new DateTime().plusDays(i);
            String dateString = curDateTime.toString("yyyy-MM-dd");
            dateList.add(new DateTime(dateString).toDate());
        }

        // 3.????????????
        ArrayList<Date> currentDatePageList = dateList.stream().skip((pageNum - 1) * pageSize)
                                                           .limit(pageSize)
                                                           .collect(Collectors.toCollection(ArrayList<Date>::new));
        // ?????????
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
                dayOfWeek = "??????";
                break;
            case DateTimeConstants.MONDAY:
                dayOfWeek = "??????";
                break;
            case DateTimeConstants.TUESDAY:
                dayOfWeek = "??????";
                break;
            case DateTimeConstants.WEDNESDAY:
                dayOfWeek = "??????";
                break;
            case DateTimeConstants.THURSDAY:
                dayOfWeek = "??????";
                break;
            case DateTimeConstants.FRIDAY:
                dayOfWeek = "??????";
                break;
            case DateTimeConstants.SATURDAY:
                dayOfWeek = "??????";
            default:
                break;
        }
        return dayOfWeek;
    }


}
