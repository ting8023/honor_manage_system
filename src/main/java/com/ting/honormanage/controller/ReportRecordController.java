package com.ting.honormanage.controller;

import com.ting.honormanage.entity.*;
import com.ting.honormanage.model.HonorInfoModel;
import com.ting.honormanage.model.ReportRecordModel;
import com.ting.honormanage.repository.*;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author nitmali
 */
@RestController
public class ReportRecordController {
    @Resource
    private ReportRecordRepository reportRecordRepository;

    @Resource
    private StudentInfoRepository studentInfoRepository;

    @Resource
    private HonorInfoRepository honorInfoRepository;

    @Resource
    private CheckerInfoRepository checkerInfoRepository;

    @Resource
    private CheckRecordRepository checkRecordRepository;

    @GetMapping("/api/checker_manager/get_reportRecord_all")
    public List<ReportRecordModel> getReportRecordList() {
        List<ReportRecordModel> reportRecordModelArrayList = new ArrayList<>();
        List<ReportRecord> reportRecordList = (List<ReportRecord>) reportRecordRepository.findAll();
        for (ReportRecord aReportRecordList : reportRecordList) {
            ReportRecordModel reportRecordModel = new ReportRecordModel(aReportRecordList);
            reportRecordModelArrayList.add(reportRecordModel);
        }
        return reportRecordModelArrayList;
    }

    @GetMapping("/api/checker/get_reportRecord_all_of_check")
    public List<ReportRecordModel> getReportRecordOfcheckList(HttpServletRequest request) {
        HttpSession session = request.getSession();
        CheckerInfo checkerInfo = checkerInfoRepository
                .findCheckerInfoByUsername((String) session.getAttribute("userName"));
        List<ReportRecord> reportRecordList;
        if (checkerInfo.getAuthority() == CheckerInfo.Authority.FIRST_LEVEL) {
            reportRecordList = reportRecordRepository
                    .findReportRecordByStatus(ReportRecord.Status.WAITING_REVIEW);
        } else {
            reportRecordList = reportRecordRepository
                    .findReportRecordByStatus(ReportRecord.Status.FIRST_REVIEW);
        }
        List<ReportRecordModel> reportRecordModelArrayList = new ArrayList<>();

        for (ReportRecord aReportRecordList : reportRecordList) {
            ReportRecordModel reportRecordModel = new ReportRecordModel(aReportRecordList);

            reportRecordModelArrayList.add(reportRecordModel);

        }
        return reportRecordModelArrayList;
    }

    @GetMapping("/api/manager_checker/get_reportRecord_id")
    public ReportRecordModel getReportRecordFromId(Long id) {
        return new ReportRecordModel(reportRecordRepository.findReportRecordById(id));
    }

    @GetMapping("/api/manager_checker/get_reportRecord_status")
    public List<ReportRecordModel> getReportRecordListFromStatus(ReportRecord.Status status) {
        List<ReportRecordModel> reportRecordModelArrayList = new ArrayList<>();
        List<ReportRecord> reportRecordList = reportRecordRepository.findReportRecordByStatus(status);
        for (ReportRecord aReportRecordList : reportRecordList) {
            ReportRecordModel reportRecordModel = new ReportRecordModel(aReportRecordList);
            reportRecordModelArrayList.add(reportRecordModel);
        }
        return reportRecordModelArrayList;
    }

    @GetMapping("/api/manager_checker/get_reportRecord_studentInfo")
    public List<ReportRecordModel> getReportRecordListFromStudentInfo(Long studentId) {
        StudentInfo studentInfo = studentInfoRepository.findStudentInfoById(studentId);
        List<ReportRecordModel> reportRecordModelArrayList = new ArrayList<>();
        List<ReportRecord> reportRecordList = reportRecordRepository.findReportRecordByStudentInfo(studentInfo);
        for (ReportRecord aReportRecordList : reportRecordList) {
            ReportRecordModel reportRecordModel = new ReportRecordModel(aReportRecordList);
            reportRecordModelArrayList.add(reportRecordModel);
        }
        return reportRecordModelArrayList;
    }


    @GetMapping("/api/manager_checker/get_reportRecord_honorInfo")
    public List<ReportRecordModel> getReportRecordListFromHonorInfo(Long honorId) {
        HonorInfo honorInfo = honorInfoRepository.findHonorInfoById(honorId);
        List<ReportRecordModel> reportRecordModelArrayList = new ArrayList<>();
        List<ReportRecord> reportRecordList = reportRecordRepository.findReportRecordByHonorInfo(honorInfo);
        for (ReportRecord aReportRecordList : reportRecordList) {
            ReportRecordModel reportRecordModel = new ReportRecordModel(aReportRecordList);
            reportRecordModelArrayList.add(reportRecordModel);
        }
        return reportRecordModelArrayList;
    }

    @GetMapping("/api/manager_checker/get_reportRecord_studentInfo_honorInfo")
    public List<ReportRecordModel> getReportRecordListFromStudentInfoAndHonorInfo(Long studentId, Long honorId) {
        StudentInfo studentInfo = studentInfoRepository.findStudentInfoById(studentId);
        HonorInfo honorInfo = honorInfoRepository.findHonorInfoById(honorId);
        List<ReportRecordModel> reportRecordModelArrayList = new ArrayList<>();
        List<ReportRecord> reportRecordList = reportRecordRepository
                .findReportRecordByStudentInfoAndHonorInfo(studentInfo, honorInfo);
        for (ReportRecord aReportRecordList : reportRecordList) {
            ReportRecordModel reportRecordModel = new ReportRecordModel(aReportRecordList);
            reportRecordModelArrayList.add(reportRecordModel);
        }
        return reportRecordModelArrayList;
    }

    @PostMapping("/api/student/add_reportRecord")
    public String addReportRecord(@RequestBody HonorInfoModel honorInfoModel, HttpServletRequest request) {
        HttpSession session = request.getSession();
        StudentInfo studentInfo = studentInfoRepository
                .findStudentInfoByNumber((String) session.getAttribute("userName"));
        HonorInfo honorInfo = new HonorInfo(honorInfoModel);
        ReportRecord reportRecord = new ReportRecord(honorInfo, studentInfo);
        reportRecordRepository.save(reportRecord);
        return "{\"message\":\"add reportRecord success\"}";
    }

    @PostMapping("/api/manager_checker/update_reportRecord")
    public String updateReportRecord(@RequestBody ReportRecordModel reportRecordModel) {
        ReportRecord reportRecord1 = reportRecordRepository.findReportRecordById(reportRecordModel.getId());
        if (reportRecord1 == null) {
            return "{\"message\":\"reportRecord not find\"}";
        } else {
            reportRecord1.setStatus(reportRecordModel.getStatus());
            reportRecordRepository.save(reportRecord1);
        }
        return "{\"message\":\"update reportRecord success\"}";
    }

    @GetMapping("/api/manager/count_reportRecord")
    @ResponseBody
    public Map<String, Long> countReportRecord() {
        Map<String, Long> map = new HashMap<>();
        map.put("WAITING_REVIEW", reportRecordRepository
                .countReportRecordByStatus(ReportRecord.Status.WAITING_REVIEW));
        map.put("FIRST_REVIEW", reportRecordRepository
                .countReportRecordByStatus(ReportRecord.Status.FIRST_REVIEW));
        map.put("ALREADY_REVIEW", reportRecordRepository
                .countReportRecordByStatus(ReportRecord.Status.ALREADY_REVIEW));
        return map;
    }

}
