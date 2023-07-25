package com.schoolmanagement.service.validator;

import com.schoolmanagement.exception.ConflictException;
import com.schoolmanagement.payload.messages.ErrorMessages;
import com.schoolmanagement.repository.user.*;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

@Component                   //  Bean olusturuyoruz kii service katmaninda kullanabilelim  !!
@RequiredArgsConstructor
public class UniquePropertyValidator {

    //repository katmaninda db de var mi diye kontrol edecegiz
    private final AdminRepository adminRepository;
    private final DeanRepository deanRepository;
    private final StudentRepository studentRepository;
    private final TeacherRepository teacherRepository;
    private final ViceDeanRepository viceDeanRepository;

    public void checkDuplicate(String... values) {

        String username = values[0];  //varrags'in ilk parametresi username olsun
        String ssn = values[1];
        String phone = values[2];
        String email = "";

        if(values.length==4){        //varrags lenght'i 4 elemanli is 4. ye email ata
            email= values[3];
        }

        //herhangi bir tanesinin dublicate olmasi exception almak icin yeterli !
        //else if ile sirayla kontrol ediyoruz

        if(adminRepository.existsByUsername(username) || deanRepository.existsByUsername(username)
                || studentRepository.existsByUsername(username) || teacherRepository.existsByUsername(username)
                || viceDeanRepository.existsByUsername(username))
        {
            throw new ConflictException(String.format(ErrorMessages.ALREADY_REGISTER_MESSAGE_USERNAME, username));
       
        } else if (adminRepository.existsBySsn(ssn) || deanRepository.existsBySsn(ssn) || studentRepository.existsBySsn(ssn)
                || teacherRepository.existsBySsn(ssn) || viceDeanRepository.existsBySsn(ssn)) {
            throw new ConflictException(String.format(ErrorMessages.ALREADY_REGISTER_MESSAGE_SSN, ssn));
        
        } else if (adminRepository.existsByPhoneNumber(phone) || deanRepository.existsByPhoneNumber(phone)
                || studentRepository.existsByPhoneNumber(phone)
                || teacherRepository.existsByPhoneNumber(phone) || viceDeanRepository.existsByPhoneNumber(phone)) {
            throw new ConflictException(String.format(ErrorMessages.ALREADY_REGISTER_MESSAGE_PHONE_NUMBER,phone));
       
        } else if (studentRepository.existsByEmail(email) || teacherRepository.existsByEmail(email)) {
            throw new ConflictException(String.format(ErrorMessages.ALREADY_REGISTER_MESSAGE_EMAIL,email));
        }
            



    }
}





















/*
package com.schoolmanagement.service.validator;

import com.schoolmanagement.exception.ConflictException;
import com.schoolmanagement.payload.messages.ErrorMessages;
import com.schoolmanagement.repository.user.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component      //bu classdan bir bean kullanmak icin @Component ile anote ettik
@RequiredArgsConstructor
public class UniquePropertyValidator {

    private final AdminRepository adminRepository;
    private final DeanRepository deanRepository;
    private final StudentRepository studentRepository;
    private final TeacherRepository teacherRepository;
    private final ViceDeanRepository viceDeanRepository;

    //username, ssn, phoneNumber ve email kontrol edecegiz ama bazi classlar email icermiyor
    public void checkDuplicate(String... values ){  //array gibi calisir istedgimiz sayida parametre gonderebilirz

        String username = values[0];
        String ssn = values[1];
        String phone = values[2];
        String email = "";   //email yoksa hiclik olarak kalir
        if(values.length==4){ //bu array'in uzunlugu 4 ise email de gelmis demek
            email=values[3];
        }

        if(adminRepository.existsByUsername(username) || deanRepository.existsByUsername(username)
                || studentRepository.existsByUsername(username) || teacherRepository.existsByUsername(username)
                || viceDeanRepository.existsByUsername(username))
        {
          throw new ConflictException(String.format(ErrorMessages.ALREADY_REGISTER_MESSAGE_USERNAME,username));
        }else if(adminRepository.existBySsn(ssn) || deanRepository.existsBySsn(ssn) || studentRepository.existsBySsn(ssn)
        || teacherRepository.existsBySsn(ssn) || viceDeanRepository.existsBySsn(ssn))
        {
            throw new ConflictException(String.format(ErrorMessages.ALREADY_REGISTER_MESSAGE_SSN));
        }


    }
}
*/
