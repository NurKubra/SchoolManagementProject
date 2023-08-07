package com.schoolmanagement.service.business;

import com.schoolmanagement.entity.concretes.user.AdvisoryTeacher;
import com.schoolmanagement.exception.ResourceNotFoundException;
import com.schoolmanagement.payload.mappers.AdvisoryTeacherMapper;
import com.schoolmanagement.payload.messages.ErrorMessages;
import com.schoolmanagement.payload.messages.SuccessMessages;
import com.schoolmanagement.payload.response.AdvisoryTeacherResponse;
import com.schoolmanagement.payload.response.ResponseMessage;
import com.schoolmanagement.repository.business.AdvisoryTeacherRepository;
import com.schoolmanagement.service.helper.PageableHelper;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AdvisoryTeacherService {

    private final AdvisoryTeacherRepository advisoryTeacherRepository;
    private final AdvisoryTeacherMapper advisoryTeacherMapper;
    private final PageableHelper pageableHelper;



    // Not : getAll() ****************************************************************
    public List<AdvisoryTeacherResponse> getAll() {

        return  advisoryTeacherRepository.findAll()
                .stream()
                .map(advisoryTeacherMapper::mapAdvisorTeacherToAdvisorTeacherResponse)
                .collect(Collectors.toList());
    }

    //advisoryTaecherRequeste ihtiyacim yok --> zaten var olan bir teachera bu advisory olma ozelligi ekliyoruz
    //ayri bir entity yapma nedenimiz ise tum advisory Teacher'lari tek bir tabloda gostermek
    //suanda db de advisoryTaecher db de yok --> advisoryTaecherin db ye kayit edilme islmeini TeacherService de save() in icinde yapacagiz !





    // Not: getAllWithPage ***********************************************************
    public Page<AdvisoryTeacherResponse> getAllAdvisorTeacherByPage(int page, int size, String sort, String type) {
        Pageable pageable = pageableHelper.getPageableWithProperties(page, size, sort, type);
        return advisoryTeacherRepository.findAll(pageable)
                .map(advisoryTeacherMapper::mapAdvisorTeacherToAdvisorTeacherResponse);
    }




    // Not : Delete() ****************************************************************
    public ResponseMessage deleteAdvisorTeacherById(Long id) {
        AdvisoryTeacher advisoryTeacher = getAdvisoryTeacherById(id);
        advisoryTeacherRepository.deleteById(id);
        return ResponseMessage.builder()
                .message(SuccessMessages.ADVISOR_TEACHER_DELETE)
                .httpStatus(HttpStatus.OK)
                .build();

    }

    public AdvisoryTeacher getAdvisoryTeacherById(Long advisoryTeacherId){  //varsa getir yoksa exception
        return advisoryTeacherRepository.findById(advisoryTeacherId)
                .orElseThrow(()->
                        new ResourceNotFoundException(String.format(ErrorMessages.NOT_FOUND_ADVISOR_MESSAGE, advisoryTeacherId)));
    }
}