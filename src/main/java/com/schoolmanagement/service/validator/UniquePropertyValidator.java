package com.schoolmanagement.service.validator;

import com.schoolmanagement.entity.abstracts.User;
import com.schoolmanagement.entity.concretes.user.Dean;
import com.schoolmanagement.entity.concretes.user.Student;
import com.schoolmanagement.entity.concretes.user.Teacher;
import com.schoolmanagement.exception.ConflictException;
import com.schoolmanagement.payload.messages.ErrorMessages;
import com.schoolmanagement.payload.request.DeanRequest;
import com.schoolmanagement.payload.request.StudentRequest;
import com.schoolmanagement.payload.request.TeacherRequest;
import com.schoolmanagement.payload.request.abstracts.BaseUserRequest;
import com.schoolmanagement.repository.user.*;
import lombok.RequiredArgsConstructor;
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

    //iki deger birbirine esit degilse yani requesten gelen ile db deki o zaman setle yoksa degismesin
    // bu method tum app icin evrensel hale geldi(Abstract classlari paarametre olarak girdigimiz icin )
    //update den gelen ile db deki deger ayni degilse requesten gelen degerle setle dedik
    //teacher ve student da extradan email oldugu icin teacher ya da student degilse yalnizca 3 paramtreyi al
    // yoksa 4 paramtreyi al
    public void checkUniqueProperties(User user, BaseUserRequest baseUserRequest){

        String updatedUsername = "";
        String updatedSsn = "";
        String updatedPhone = "";
        String updatedEmail = "";
        boolean isChanged =false;

        if(!user.getUsername().equalsIgnoreCase(baseUserRequest.getUsername())) {
            updatedUsername = baseUserRequest.getUsername();
            isChanged=true;
        }

        if(!user.getSsn().equalsIgnoreCase(baseUserRequest.getSsn())){
            updatedSsn = baseUserRequest.getSsn();
            isChanged=true;
        }

        if(!user.getPhoneNumber().equalsIgnoreCase(baseUserRequest.getPhoneNumber())){
            updatedPhone = baseUserRequest.getPhoneNumber();
            isChanged=true;
        }

        boolean teacherOrStudent = false ;

        if(user instanceof Teacher && baseUserRequest instanceof TeacherRequest){  //gelen paramtrelerin teachera ait  olup olmadigini kontrol ettik
            Teacher teacher = (Teacher) user;                                      //true ise yukardaki paarmtreleri teacher icin olan formatlara donusturduk(cast islemi)
            TeacherRequest teacherRequest = (TeacherRequest) baseUserRequest;
            teacherOrStudent = true;
            if(!teacher.getEmail().equalsIgnoreCase(teacherRequest.getEmail())){
                updatedEmail = teacherRequest.getEmail();
                isChanged=true;
            }
        }

        if(user instanceof Student && baseUserRequest instanceof StudentRequest){   //bu methodu tetikleye user'in rolünü tespit ediyor
            Student student = (Student) user;                                       //parent dan child a giderken java otomatik yapmiyor bu yuzden casti biz yaptik
            StudentRequest studentRequest = (StudentRequest) baseUserRequest;
            teacherOrStudent = true;
            if(!student.getEmail().equalsIgnoreCase(studentRequest.getEmail())){
                updatedEmail = studentRequest.getEmail();
                isChanged=true;
            }

        }
        if(isChanged) {
            if (teacherOrStudent) {
                checkDuplicate(updatedUsername, updatedSsn, updatedPhone, updatedEmail);
            } else {
                checkDuplicate(updatedUsername, updatedSsn, updatedPhone);
            }
        }
    }
        //degerler setlenmek isteniyorsa setlensin yoksa hiclik olarak kalsin degismesin
        //Dean icin ilk 3 field yeterli ama teacher veya student gelirse email de oldugu icin onlari da kontrol etmem lazim
        //bu methodu tetikleyen user teacher mi diye kontrol ettik oyleyse o zaman gelen degerle var olan deger farkli ise
        //o zaman setleme islemi yap

       //soru ?? ---> yine ayni methodu kullanmis olmuyor muyuz neden extra bir method yazdik, dolayli olarak yine db ye gitmedi mi

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
