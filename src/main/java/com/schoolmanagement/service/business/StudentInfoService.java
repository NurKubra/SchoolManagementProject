package com.schoolmanagement.service.business;

import com.schoolmanagement.entity.concretes.business.EducationTerm;
import com.schoolmanagement.entity.concretes.business.Lesson;
import com.schoolmanagement.entity.concretes.business.StudentInfo;
import com.schoolmanagement.entity.concretes.user.Student;
import com.schoolmanagement.entity.concretes.user.Teacher;
import com.schoolmanagement.entity.enums.Note;
import com.schoolmanagement.exception.ConflictException;
import com.schoolmanagement.payload.mappers.StudentInfoDto;
import com.schoolmanagement.payload.messages.ErrorMessages;
import com.schoolmanagement.payload.messages.SuccessMessages;
import com.schoolmanagement.payload.request.StudentInfoRequest;
import com.schoolmanagement.payload.response.ResponseMessage;
import com.schoolmanagement.payload.response.StudentInfoResponse;
import com.schoolmanagement.repository.business.StudentInfoRepository;
import com.schoolmanagement.service.user.StudentService;
import com.schoolmanagement.service.user.TeacherService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
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
    private final StudentInfoDto studentInfoDto;

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
        Note note = checkLetterGrade(calculateExamAverage(studentInfoRequest.getMidtermExam(), studentInfoRequest.getFinalExam()));

        // !!! DTO --> POJO
        StudentInfo studentInfo =studentInfoDto.mapStudentInfoRequestToStudentInfo(studentInfoRequest,
                note,
                calculateExamAverage(studentInfoRequest.getMidtermExam(), studentInfoRequest.getFinalExam()));
        studentInfo.setStudent(student);
        studentInfo.setEducationTerm(educationTerm);
        studentInfo.setTeacher(teacher);
        studentInfo.setLesson(lesson);

        StudentInfo savedStudentInfo =  studentInfoRepository.save(studentInfo);

        return ResponseMessage.<StudentInfoResponse>builder()
                .message(SuccessMessages.STUDENT_INFO_SAVE)
                .object(studentInfoDto.mapStudentInfoToStudentInfoResponse(savedStudentInfo))
                .httpStatus(HttpStatus.CREATED)
                .build();


    }

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

    private Note checkLetterGrade(Double average){
        if(average<50.0) {
            return Note.FF;
        } else if (average<60) {
            return Note.DD;
        } else if (average<65) {
            return Note.CC;
        } else if (average<70) {
            return  Note.CB;
        } else if (average<75) {
            return  Note.BB;
        } else if (average<80) {
            return Note.BA;
        } else {
            return Note.AA;
        }
    }

}

//db de boyle bir ogreci var mi, boyle bir username e sahip ogretmen var mi(bunu teacherService de yazdigimiz kodla yaptik )
//id ye sahip lesson var mi diye kontrol ettik