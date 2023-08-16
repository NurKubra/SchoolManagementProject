package com.schoolmanagement.payload.mappers;

import com.schoolmanagement.entity.concretes.business.EducationTerm;
import com.schoolmanagement.entity.concretes.business.Lesson;
import com.schoolmanagement.entity.concretes.business.StudentInfo;
import com.schoolmanagement.entity.enums.Note;
import com.schoolmanagement.payload.request.StudentInfoRequest;
import com.schoolmanagement.payload.request.UpdateStudentInfoRequest;
import com.schoolmanagement.payload.response.StudentInfoResponse;
import lombok.Data;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Data
@Component
public class StudentInfoDto {

    @Autowired
    private StudentMapper studentMapper;

    // !!! DTO --> POJO
    public StudentInfo mapStudentInfoRequestToStudentInfo(StudentInfoRequest studentInfoRequest, Note note , Double average){

        return StudentInfo.builder()
                .infoNote(studentInfoRequest.getInfoNote())   //request de var
                .absentee(studentInfoRequest.getAbsentee())
                .midtermExam(studentInfoRequest.getMidtermExam())
                .finalExam(studentInfoRequest.getFinalExam())
                .examAverage(average)                        //pojoda var dto da yok
                .letterGrade(note)                           //pojoda var dto da yok
                .build();
    }
    //islemlerimi daha kolay yapmak icin requestin kendisinin yaninda tekrar islem yapmamak icin ogrrencinin harf notunun bilgisinin hesaplanmis halini
    //notun yuzdeliklerinin alinmis halini alyioruz

    // !!! POJO --> DTO
    public StudentInfoResponse mapStudentInfoToStudentInfoResponse(StudentInfo studentInfo){

        return StudentInfoResponse.builder()
                .lessonName(studentInfo.getLesson().getLessonName())
                .creditScore(studentInfo.getLesson().getCreditScore())
                .isCompulsory(studentInfo.getLesson().getIsCompulsory())
                .educationTerm(studentInfo.getEducationTerm().getTerm())
                .id(studentInfo.getId())
                .absentee(studentInfo.getAbsentee())
                .midtermExam(studentInfo.getMidtermExam())
                .finalExam(studentInfo.getFinalExam())
                .infoNote(studentInfo.getInfoNote())
                .note(studentInfo.getLetterGrade())
                .average(studentInfo.getExamAverage())
                .studentResponse(studentMapper.mapStudentToStudentResponse(studentInfo.getStudent()))  //studentInfo uzerinden stuenti cegirinca respponse a donmek yerine pojo done rbu yuzden response dönen methodu cagirdik
                .build();
    }

    // !!! for Update Method --> DTO --> POJO
    public StudentInfo mapStudentInfoUpdateToStudentInfo(UpdateStudentInfoRequest studentInfoRequest,
                                                         Long studentInfoRequestId,
                                                         Lesson lesson,
                                                         EducationTerm educationTerm,
                                                         Note note,
                                                         Double average){
        return StudentInfo.builder()
                .id(studentInfoRequestId)
                .infoNote(studentInfoRequest.getInfoNote())
                .midtermExam(studentInfoRequest.getMidtermExam())
                .finalExam(studentInfoRequest.getMidtermExam())
                .absentee(studentInfoRequest.getAbsentee())
                .lesson(lesson)
                .educationTerm(educationTerm)
                .letterGrade(note)
                .examAverage(average)
                .build();
    }
    //requestdeki eksik verileri pojolardan aldik, bu sayede tüm fieldlari setlemis olduk !! --> setlemezsek null olur
}