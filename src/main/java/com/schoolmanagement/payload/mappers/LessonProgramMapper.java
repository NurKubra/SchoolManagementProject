package com.schoolmanagement.payload.mappers;

import com.schoolmanagement.entity.concretes.business.EducationTerm;
import com.schoolmanagement.entity.concretes.business.Lesson;
import com.schoolmanagement.entity.concretes.business.LessonProgram;
import com.schoolmanagement.payload.request.LessonProgramRequest;
import com.schoolmanagement.payload.response.LessonProgramResponse;
import lombok.Data;
import org.springframework.stereotype.Component;

import java.util.Set;

@Data
@Component
public class LessonProgramMapper {

    // !!! DTO --> POJO
    public LessonProgram mapLessonProgramRequestToLessonProgram(LessonProgramRequest lessonProgramRequest, Set<Lesson> lessonSet,
                                                                EducationTerm educationTerm){

        return LessonProgram.builder()
                .startTime(lessonProgramRequest.getStartTime())
                .stopTime(lessonProgramRequest.getStopTime())
                .day(lessonProgramRequest.getDay())
                .lessons(lessonSet)
                .educationTerm(educationTerm)
                .build();

    }


    // !!! POJO --> DTO
    public LessonProgramResponse mapLessonProgramToLessonProgramResponse(LessonProgram lessonProgram){

        return LessonProgramResponse.builder()
                .day(lessonProgram.getDay())
                .startTime(lessonProgram.getStartTime())
                .stopTime(lessonProgram.getStopTime())
                .lessonProgramId(lessonProgram.getId())
                .lessonName(lessonProgram.getLessons())
                .build();
    }
}

//Lesson'larin id si list seklinde gelmesin sadece lessonlarin ismi gelsin , ve EducationTerm'ler donsun
//Request de id olmaisni istemedigimiz icin
//normalde dto pojo ya ceviriken tek paramtre aldik, yani list yapida olmayan
//ama burda List yapilarla baglantimiz var
//2 turlu cekebiirz verileri --> bizim id'leri listtelemiz lazim ama biz zaten service clasinda idleri ile cagrilmasi gerekn
//lesson ve ecucationTerm 'in idlaeri uzerinden zaten listeledik burda bu defa id kullanmadik direk
