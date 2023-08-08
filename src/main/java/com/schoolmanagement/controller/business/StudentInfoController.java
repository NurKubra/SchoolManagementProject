package com.schoolmanagement.controller.business;

import com.schoolmanagement.payload.request.StudentInfoRequest;
import com.schoolmanagement.payload.response.ResponseMessage;
import com.schoolmanagement.payload.response.StudentInfoResponse;
import com.schoolmanagement.service.business.StudentInfoService;
import lombok.RequiredArgsConstructor;
import org.apache.coyote.http11.upgrade.UpgradeServletInputStream;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

@RestController
@RequestMapping("/studentInfo")
@RequiredArgsConstructor
public class StudentInfoController {

    private final StudentInfoService studentInfoService;

    // Not :  Save() **********************************************************
    @PreAuthorize("hasAnyAuthority('TEACHER')")
    @PostMapping("/save")  // http://localhost:8080/studentInfo/save  + POST
    public ResponseMessage<StudentInfoResponse> saveStudentInfo(HttpServletRequest httpServletRequest,
                                                                @RequestBody @Valid StudentInfoRequest studentInfoRequest ){
        return studentInfoService.saveStudentInfo(httpServletRequest,studentInfoRequest);
    }
    //birinde teachera ulasmak icin digerinde giirlen verilere ulasmak icin




}
