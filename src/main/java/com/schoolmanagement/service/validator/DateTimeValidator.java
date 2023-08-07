package com.schoolmanagement.service.validator;
import com.schoolmanagement.entity.concretes.business.LessonProgram;
import com.schoolmanagement.exception.BadRequestException;
import com.schoolmanagement.payload.messages.ErrorMessages;
import org.springframework.stereotype.Component;

import java.time.LocalTime;
import java.util.HashSet;
import java.util.Set;


@Component
public class DateTimeValidator {

    private boolean checkTime(LocalTime start, LocalTime stop) {
        return start.isAfter(stop) || start.equals(stop);
    }

    public void checkTimeWithException(LocalTime start, LocalTime stop) {
        if(checkTime(start, stop)) {
            throw new BadRequestException(ErrorMessages.TIME_NOT_VALID_MESSAGE);
        }
    }

    //not: TeacherService de kullanmak icin yaziyoruz
    //bir problem varsa exception yoksa bir sey dondurmaseine gerek yok
    //mevcutdaki lessonProgramlar ile requesten gelen lessonProgramlari karsilastir
    public void checkLessonPrograms(Set<LessonProgram> existLessonProgram, Set<LessonProgram> lessonProgramRequest){

        if(existLessonProgram.isEmpty() && lessonProgramRequest.size()>1){  //bu ogretmenin mevcutta bir ders Programi yoksa ayni zamanda requesten gelen lessonProgram 1 den fazlaysa o zaman 1 den fazla lessonProogramin akisip cakismadigini kontrol ediyorum
            checkDuplicateLessonPrograms(lessonProgramRequest);             //saatler cakisiyor mu kontrolü  //lessonProgramRequest time kontrolü
        } else { //bu kisim eski ogretmenler icin kontrol
            checkDuplicateLessonPrograms(lessonProgramRequest);                    //lessonProgramRequest time kontrolü
            checkDuplicateLessonPrograms(existLessonProgram,lessonProgramRequest); //mevcutta varsa eski ve yeni cakisma kontülü
        }

    }

    private void checkDuplicateLessonPrograms(Set<LessonProgram> lessonPrograms) {  //requesti kontrol etmek icin yazdik
        Set<String> uniqueLessonProgramKeys = new HashSet<>();

        for (LessonProgram lessonProgram : lessonPrograms) {
            String lessonProgramKey =  lessonProgram.getDay().name() + lessonProgram.getStartTime();
            if(uniqueLessonProgramKeys.contains(lessonProgramKey)){
                throw new BadRequestException(ErrorMessages.LESSON_PROGRAM_ALREADY_EXIST);
            }
            uniqueLessonProgramKeys.add(lessonProgramKey);

            //String Manupilation ile gün ve baslama saatlerini yan yana yazip ayni cumle olusuyor mu diye kontrol ederim
            //set yapida unique 'lik soz konusudur.
            //cakisan lessonProgramin hangisi oldugunu bilemeyiz.
            //lessonProgram.getDay().name() --> enum yapidaki ismi almak iicn kullandik
        }
    }
    private void checkDuplicateLessonPrograms(Set<LessonProgram> existLessonProgram, Set<LessonProgram> lessonProgramRequest) { //eski ile yeni kontrolü

        for (LessonProgram requestLessonprogram : lessonProgramRequest) {
            if(existLessonProgram.stream().
                    anyMatch(lessonProgram -> lessonProgram.getStartTime().equals(requestLessonprogram.getStartTime())  //anyMatch herhangi biri demek
                            && lessonProgram.getDay().name().equals(requestLessonprogram.getDay().name()))){
                throw new BadRequestException(ErrorMessages.LESSON_PROGRAM_ALREADY_EXIST);
            }
        }
    }  //burda iki yapidan  birinin iicnde dolasip
       //lessonProgramRequest in icindeki herhangi bir programin existLessonProgram in icindeki herhangi bir lessonProgram ile cakisma(gunler ve saatler) durumunda exception firlattik



}


/*
 GENEL NOT :
 CheckLessonProgram methodumd gelen parametreler --> mevcuttaki dersProgramini ve talep edilen ders programini aliyor
 if icinde --> mevduttaki leessonProgramin ici bossa (ilk paramtre) ve talep edilen ders programlarinin bulundugu 2. paramtre 1 den fazla ders programi iceriyorsa
 (1 tane varsa zaten dublictae kontrolü yapmaya gerek yok )
 o zaman yeni ogretmen anlamina gelir ve bunun icin tek paramtre alan ve lessonProgramalrin gun ve saatini kontrol eden methodumu calistirirm
 (ayni methodu eski ogretmenler icin de kullanacagiz cunku ogretmenin tum lessonProgrami silinmis olabilir )
 gün ve saat kontrol ederken String Manupilation yapiyoruz --> requestden gelen tum lessonProgram isteklerini String cumle haline getirip
 bir set icine atiyoruz foreach ile --> ayni cumle ikinnci kez gelirse o zaman exception firlatilyoruz !
 Eger eski ogretmen ise; talep edilenler icinde yukarda yazdigimiz gun ve saat kontrolü yapip cakisma var mi diye bakyioruz, hepsi unique olmali,
 sonra ayni islemi var olan lessonProgramlar icinde yapiroyum --> yani var olan lessonProgramlar ile requestden gelen lessonProgramalr birbileriyle cakisyor mu diye!!
 (ucuncu yazdigimiz 2 paramtreli  method bu islemi yapiyor)





  */