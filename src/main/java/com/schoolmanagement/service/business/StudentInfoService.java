package com.schoolmanagement.service.business;

import com.schoolmanagement.entity.concretes.business.EducationTerm;
import com.schoolmanagement.entity.concretes.business.Lesson;
import com.schoolmanagement.entity.concretes.business.StudentInfo;
import com.schoolmanagement.entity.concretes.user.Student;
import com.schoolmanagement.entity.concretes.user.Teacher;
import com.schoolmanagement.entity.enums.Note;
import com.schoolmanagement.exception.ConflictException;
import com.schoolmanagement.exception.ResourceNotFoundException;
import com.schoolmanagement.payload.mappers.StudentInfoDto;
import com.schoolmanagement.payload.messages.ErrorMessages;
import com.schoolmanagement.payload.messages.SuccessMessages;
import com.schoolmanagement.payload.request.StudentInfoRequest;
import com.schoolmanagement.payload.request.UpdateStudentInfoRequest;
import com.schoolmanagement.payload.response.ResponseMessage;
import com.schoolmanagement.payload.response.StudentInfoResponse;
import com.schoolmanagement.repository.business.StudentInfoRepository;
import com.schoolmanagement.service.helper.PageableHelper;
import com.schoolmanagement.service.user.StudentService;
import com.schoolmanagement.service.user.TeacherService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
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
    private final PageableHelper pageableHelper;


    @Value("${midterm.exam.impact.percentage}")  //bunun degerini application.properties den al demek
    private Double midtermExamPercentage;

    @Value("${final.exam.impact.percentage}")
    private Double finalExamPercentage;

    // Not :  Save() **********************************************************
    public ResponseMessage<StudentInfoResponse> saveStudentInfo(HttpServletRequest httpServletRequest, StudentInfoRequest studentInfoRequest) {

        String teacherUsername = (String) httpServletRequest.getAttribute("username");  //object data tipinde old. icin Stringe ceviriyoruz
        //db de boyle bir student var mi diye bakiyoruz
        Student student = studentService.isStudentExist(studentInfoRequest.getStudentId());
        //teacher username ile boyle bir teacher var mi diye bakiyoruz
        Teacher teacher =  teacherService.getTeacherByUsername(teacherUsername);
        //Lessonid ile lesson var mi diye bakyioruz
        Lesson lesson = lessonService.isLessonExistById(studentInfoRequest.getLessonId());
        //educationTerm var mi diye kontrol diye kontrol ediyorum
        EducationTerm educationTerm = educationTermService.getEducationTermById(studentInfoRequest.getEducationTermId());
        //ogrencinin mevcutdaki studentinfo su ile eklenmek istenen dersler cakisiyor mu diye bakiyoruz
        checkSameLesson(studentInfoRequest.getStudentId(), lesson.getLessonName());
        //method icinde method yazdik --> direk harf notu doncek
        Note note = checkLetterGrade(calculateExamAverage(studentInfoRequest.getMidtermExam(),
                                                           studentInfoRequest.getFinalExam()));

        // !!! DTO --> POJO
        StudentInfo studentInfo =studentInfoDto.mapStudentInfoRequestToStudentInfo(studentInfoRequest,
                                                                                    note,
                calculateExamAverage(studentInfoRequest.getMidtermExam(), studentInfoRequest.getFinalExam()));
        //pojo olabilmesi icin eksik verileri setliyorum ve save islemi yapiyorum
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

    //studentInfo da ogrencinin eklenmek istenen studentInfo zaten var mi diye kontrol ediyoruz
    //akis icindeki lessonName
    private void checkSameLesson(Long studentId, String lessonName){
        boolean isLessonDuplicationExist = studentInfoRepository.getAllByStudentId_Id(studentId) // List<StudentInfo>
                .stream() // stream<StudentInfo>
                .anyMatch((e)->e.getLesson().getLessonName().equalsIgnoreCase(lessonName));
        //su id ye sahip studetnin tum studentInfo uzeriden lesson'larini ordan da lessonName yine giderek ders ismi var mi diye bakiyoruz
        if(isLessonDuplicationExist){
            throw new ConflictException(ErrorMessages.LESSON_PROGRAM_ALREADY_EXIST);
        }
    }

    //
    private Double calculateExamAverage(Double midtermExam , Double finalExam){
        return ((midtermExam*midtermExamPercentage) + (finalExam*finalExamPercentage));
    }

    private Note checkLetterGrade(Double average){  //enum type'lardan birini döndürcek
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

    // Not : Delete() ************************************************************
    public ResponseMessage deleteStudentInfo(Long studentInfoId) {
        StudentInfo studentInfo = isStudentInfoExistById(studentInfoId);  //var olan id li objeyi dönüyor
        studentInfoRepository.deleteById(studentInfoId);

        return ResponseMessage.builder()
                .message(SuccessMessages.STUDENT_INFO_DELETE)
                .httpStatus(HttpStatus.OK)
                .build();

    }

    public StudentInfo isStudentInfoExistById(Long id){

        boolean isExist = studentInfoRepository.existsByIdEquals(id);

        if(!isExist){
            throw  new ResourceNotFoundException(String.format(ErrorMessages.STUDENT_INFO_NOT_FOUND,id));
        } else {
            return studentInfoRepository.findById(id).get(); //yapi optional oldugu icin get() ile almam lazim varsa--> nesnenin kendisini döndür
        }
    }


    // Not: getAllWithPage ***********************************************************
    public Page<StudentInfoResponse> getAllStudentInfoByPage(int page, int size, String sort, String type) {

        Pageable pageable = pageableHelper.getPageableWithProperties(page, size, sort, type);

        return studentInfoRepository.findAll(pageable).map(studentInfoDto::mapStudentInfoToStudentInfoResponse);
    }

    // Not: Update() *************************************************************
    public ResponseMessage<StudentInfoResponse> update(UpdateStudentInfoRequest studentInfoRequest, Long studentInfoId) {

        Lesson lesson = lessonService.isLessonExistById(studentInfoRequest.getLessonId());
        StudentInfo studentInfo = isStudentInfoExistById(studentInfoId);
        EducationTerm educationTerm = educationTermService.getEducationTermById(studentInfoRequest.getEducationTermId());
        Double noteAverage = calculateExamAverage(studentInfoRequest.getMidtermExam(), studentInfo.getFinalExam());
        Note note = checkLetterGrade(noteAverage);

        StudentInfo studentInfoForUpdate = studentInfoDto.mapStudentInfoUpdateToStudentInfo(studentInfoRequest,studentInfoId,
                lesson,educationTerm,note, noteAverage);

        studentInfoForUpdate.setStudent(studentInfo.getStudent());
        studentInfoForUpdate.setTeacher(studentInfo.getTeacher());

        StudentInfo updatedStudentInfo = studentInfoRepository.save(studentInfoForUpdate);

        return ResponseMessage.<StudentInfoResponse>builder()
                .message(SuccessMessages.STUDENT_INFO_UPDATE)
                .httpStatus(HttpStatus.OK)
                .object(studentInfoDto.mapStudentInfoToStudentInfoResponse(updatedStudentInfo))
                .build();
    }

}
