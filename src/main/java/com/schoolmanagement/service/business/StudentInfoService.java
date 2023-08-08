package com.schoolmanagement.service.business;

import com.schoolmanagement.entity.concretes.business.EducationTerm;
import com.schoolmanagement.entity.concretes.business.Lesson;
import com.schoolmanagement.entity.concretes.user.Student;
import com.schoolmanagement.entity.concretes.user.Teacher;
import com.schoolmanagement.exception.ConflictException;
import com.schoolmanagement.payload.messages.ErrorMessages;
import com.schoolmanagement.payload.request.StudentInfoRequest;
import com.schoolmanagement.payload.response.ResponseMessage;
import com.schoolmanagement.payload.response.StudentInfoResponse;
import com.schoolmanagement.repository.business.StudentInfoRepository;
import com.schoolmanagement.service.user.StudentService;
import com.schoolmanagement.service.user.TeacherService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

@Service
@RequiredArgsConstructor
public class StudentInfoService {

    private final StudentInfoRepository studentInfoRepository;
    private final StudentService studentService;
    private final TeacherService teacherService;
    private final LessonService lessonService;
    private final EducationTermService educationTermService;

    @Value("${midterm.exam.impact.percentage}")
    private Double midtermExamPercentage;

    @Value("${final.exam.impact.percentage}")
    private Double finalExamPercentage;

    // Not :  Save() **********************************************************
    public ResponseMessage<StudentInfoResponse> saveStudentInfo(HttpServletRequest httpServletRequest, StudentInfoRequest studentInfoRequest) {

        String teacherUsername = (String) httpServletRequest.getAttribute("username");
        Student student = studentService.isStudentExist(studentInfoRequest.getStudentId());
        Teacher teacher =  teacherService.getTeacherByUsername(teacherUsername);
        Lesson lesson = lessonService.isLessonExistById(studentInfoRequest.getLessonId());
        EducationTerm educationTerm = educationTermService.getEducationTermById(studentInfoRequest.getEducationTermId());

        checkSameLesson(studentInfoRequest.getStudentId(), lesson.getLessonName());
        // TODO : calculateExamAverage



    }
    //db de boyle bir ogreci var mi, boyle bir username e sahip ogretmen var mi(bunu teacherService de yazdigimiz kodla yaptik )
    //id ye sahip lesson var mi diye kontrol ettik
    private void checkSameLesson(Long studentId, String lessonName){
        boolean isLessonDuplicationExist = studentInfoRepository.getAllByStudentId_Id(studentId) // List<StudentInfo>
                .stream() // stream<StudentInfo>
                .anyMatch((e)->e.getLesson().getLessonName().equalsIgnoreCase(lessonName));

        if(isLessonDuplicationExist){
            throw new ConflictException(ErrorMessages.LESSON_PROGRAM_ALREADY_EXIST);
        }
    }

    private Double calculateExamAverage(Double midtermExam , Double finalExam){
        return ((midtermExam*midtermExamPercentage) + (finalExam*finalExamPercentage));
    }

}

