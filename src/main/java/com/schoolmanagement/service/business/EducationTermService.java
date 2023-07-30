package com.schoolmanagement.service.business;


import com.schoolmanagement.exception.ResourceNotFoundException;
import com.schoolmanagement.payload.messages.ErrorMessages;
import com.schoolmanagement.payload.request.EducationTermRequest;
import com.schoolmanagement.payload.response.EducationTermResponse;
import com.schoolmanagement.payload.response.ResponseMessage;
import com.schoolmanagement.repository.business.EducationTermRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class EducationTermService {

    private  final EducationTermRepository educationTermRepository;

    public ResponseMessage<EducationTermResponse> saveEducationTerm(EducationTermRequest educationTermRequest) {
        //2 seyi kontrol etmem lazim--> bir tarihler arasi uyum ve cakisma olmamasi, 2 Term yani 2 semesterda yilda sadece bir kere olmali
        validateEducationTermDates(educationTermRequest);

    }
    private void validateEducationTermDates(EducationTermRequest educationTermRequest){
        // !!! gunler ile ilgili kontrol
        validateEducationTermDatesForUpdate(educationTermRequest);

        // !!! term ile ilgili kontrol
        if(educationTermRepository.existsByTermAndYear(educationTermRequest.getTerm(), educationTermRequest.getStartDate().getYear())) { //sadeec yili almak icin getYear()
            throw new ResourceNotFoundException(ErrorMessages.EDUCATION_TERM_IS_ALREADY_EXIST_BY_TERM_AND_YEAR);
        }
        //verdigim yil icinde term bilgisi var mi diye bak --> varsa exception
    }

    //gunler ile ilgili kontrol methodu !!
    private void validateEducationTermDatesForUpdate(EducationTermRequest educationTermRequest){

        // registrationDate > startDate  (son kayit gunu semester baslamadan once olmali) degilse exception firlat
        if(educationTermRequest.getLastRegistrationDate().isAfter(educationTermRequest.getStartDate())) {
            throw new ResourceNotFoundException(ErrorMessages.EDUCATION_START_DATE_IS_EARLIER_THAN_LAST_REGISTRATION_DATE);
        }
        // end > start  (bitis tarihi baslama tarihinden sonra olmali )
        if(educationTermRequest.getEndDate().isBefore(educationTermRequest.getStartDate())) {
            throw new ResourceNotFoundException(ErrorMessages.EDUCATION_END_DATE_IS_EARLIER_THAN_START_DATE);
        }
    }
}




