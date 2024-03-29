package com.schoolmanagement.service.user;

import com.schoolmanagement.entity.concretes.business.LessonProgram;
import com.schoolmanagement.entity.concretes.user.AdvisoryTeacher;
import com.schoolmanagement.entity.concretes.user.Student;
import com.schoolmanagement.entity.enums.RoleType;
import com.schoolmanagement.exception.ResourceNotFoundException;
import com.schoolmanagement.payload.mappers.StudentMapper;
import com.schoolmanagement.payload.messages.ErrorMessages;
import com.schoolmanagement.payload.messages.SuccessMessages;
import com.schoolmanagement.payload.request.ChooseLessonProgramWithId;
import com.schoolmanagement.payload.request.StudentRequest;
import com.schoolmanagement.payload.response.ResponseMessage;
import com.schoolmanagement.payload.response.StudentResponse;
import com.schoolmanagement.repository.user.StudentRepository;
import com.schoolmanagement.service.business.AdvisoryTeacherService;
import com.schoolmanagement.service.business.LessonProgramService;
import com.schoolmanagement.service.helper.PageableHelper;
import com.schoolmanagement.service.validator.DateTimeValidator;
import com.schoolmanagement.service.validator.UniquePropertyValidator;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;


import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class StudentService {

    //HttpStatuscode yazilmazsa defauldaki statuscode gider

    private final StudentRepository studentRepository;
    private final AdvisoryTeacherService advisoryTeacherService;
    private final UniquePropertyValidator uniquePropertyValidator;
    private final PasswordEncoder passwordEncoder;
    private final UserRoleService userRoleService;
    private final StudentMapper studentMapper;
    private final PageableHelper pageableHelper;
    private final LessonProgramService lessonProgramService;
    private final DateTimeValidator dateTimeValidator;

    // Not :  Save() **********************************************************
    public ResponseMessage<StudentResponse> saveStudent(StudentRequest studentRequest) {

        // !!! AdvisorTeacher id kontrol
        AdvisoryTeacher advisoryTeacher =
                advisoryTeacherService.getAdvisoryTeacherById(studentRequest.getAdvisorTeacherId());

        // !!! duplicate kontrolu
        uniquePropertyValidator.checkDuplicate(studentRequest.getUsername(), studentRequest.getSsn(),
                studentRequest.getPhoneNumber(), studentRequest.getEmail());

        // !!! DTO --> POJO
        Student student = studentMapper.mapStudentRequestToStudent(studentRequest);

        student.setAdvisoryTeacher(advisoryTeacher);
        student.setPassword(passwordEncoder.encode(studentRequest.getPassword()));
        student.setUserRole(userRoleService.getUserRole(RoleType.STUDENT));
        student.setActive(true);
        // !!! Ogrnci numarasi setleniyor :
        student.setStudentNumber(getLastNumber());

        return ResponseMessage.<StudentResponse>builder()
                .object(studentMapper.mapStudentToStudentResponse(studentRepository.save(student)))
                .message(SuccessMessages.STUDENT_SAVE)
                .build();
    }

    private int getLastNumber() {
        // !!! DB hic ogrenci yoksa
        if (!studentRepository.findStudent()) {
            return 1000;
        }

        return studentRepository.getMaxStudentNumber() + 1;
    }


    // Not: ChangeActıveStatus() ***********************************************
    public ResponseMessage changeStatus(Long id, boolean status) {
        Student student = isStudentExist(id);
        student.setActive(status);
        studentRepository.save(student);

        return ResponseMessage.builder()
                .message("Student is " + (status ? "active" : "passive"))
                .httpStatus(HttpStatus.OK)
                .build();

    }

    public Student isStudentExist(Long studentId) {

        return studentRepository.findById(studentId).orElseThrow(() ->
                new ResourceNotFoundException(String.format(ErrorMessages.NOT_FOUND_USER_MESSAGE, studentId)));
    }

    // Not : getAll() ***********************************************************
    public List<StudentResponse> getAllStudents() {

        return studentRepository.findAll()
                .stream()
                .map(studentMapper::mapStudentToStudentResponse)
                .collect(Collectors.toList());
    }


    // Not: Update() *************************************************************
    public ResponseMessage<StudentResponse> updateStudent(Long studentId, StudentRequest studentRequest) {
        Student student = isStudentExist(studentId);
        AdvisoryTeacher advisoryTeacher = advisoryTeacherService.getAdvisoryTeacherById(studentRequest.getAdvisorTeacherId());
        uniquePropertyValidator.checkUniqueProperties(student, studentRequest);

        Student studentForUpdate = studentMapper.mapStudentRequestToUpdatedStudent(studentRequest, studentId);

        studentForUpdate.setPassword(passwordEncoder.encode(studentRequest.getPassword()));
        studentForUpdate.setAdvisoryTeacher(advisoryTeacher);
        studentForUpdate.setStudentNumber(student.getStudentNumber());
        studentForUpdate.setUserRole(userRoleService.getUserRole(RoleType.STUDENT));
        studentForUpdate.setActive(true);

        Student updatedStudent = studentRepository.save(studentForUpdate);

        return ResponseMessage.<StudentResponse>builder()
                .object(studentMapper.mapStudentToStudentResponse(updatedStudent))
                .message(SuccessMessages.STUDENT_UPDATE)
                .httpStatus(HttpStatus.OK)
                .build();
    }

    // Not : Delete() ************************************************************
    public ResponseMessage deleteStudentById(Long studentId) {

        isStudentExist(studentId);
        studentRepository.deleteById(studentId);

        return ResponseMessage.builder()
                .message(SuccessMessages.STUDENT_DELETE)
                .httpStatus(HttpStatus.OK)
                .build();
    }

    // Not : getByName() *********************************************************
    public List<StudentResponse> findStudentByName(String studentName) {

        return studentRepository.getStudentByNameContaining(studentName)
                .stream()
                .map(studentMapper::mapStudentToStudentResponse)
                .collect(Collectors.toList());
    }

    // Not: getAllWithPage ***********************************************************
    public Page<StudentResponse> getAllStudentByPage(int page, int size, String sort, String type) {

        Pageable pageable = pageableHelper.getPageableWithProperties(page, size, sort, type);

        return studentRepository.findAll(pageable).map(studentMapper::mapStudentToStudentResponse);
    }

    // Not : getById() ********************************************************
    public Student getStudentById(Long id) {
        return isStudentExist(id); //direk Student donuyor zaten POJO halde
    }

    // Not: GetAllByAdvisoryTeacherUserName() ************************************************
    public List<StudentResponse> getAllByAdvisoryTeacherUserName(String advisoryTeacherUserName) {

        return studentRepository.getStudentByAdvisoryTeacher_Username(advisoryTeacherUserName) //username ile calistigimizdan _useername yazdik
                .stream()
                .map(studentMapper::mapStudentToStudentResponse)
                .collect(Collectors.toList());
    }




    // Not: addLessonProgramToStudentLessonsProgram() *************************
    public ResponseMessage<StudentResponse> chooseLesson(String userName, ChooseLessonProgramWithId chooseLessonProgramWithId) {
        //username kontrolü (bu username li bir ogrenci var mi)
        Student student = isStudentExistByUsername(userName);
        //requestden gelen eklenmek istenen lessonProgram id kontrolü
        Set<LessonProgram> lessonProgramSet = lessonProgramService.getLessonProgramById(chooseLessonProgramWithId.getLessonProgramId());
        //ogrenicinin mevcutta olan lessonprogramlarini alip duplicate olmasini önlüyorum
        Set<LessonProgram> studentCurrentLessonProgram = student.getLessonsProgramList();
        //dublicate i burda kontrol ediyoruz--> eklenmesini istedigim ve mevcutta olan lessonProgramlari kontorl ediyorum
        dateTimeValidator.checkLessonPrograms(studentCurrentLessonProgram,lessonProgramSet);
        //cakisma yoksa yeni gelen lessonProgramlari mevcuttakilere ekledik
        studentCurrentLessonProgram.addAll(lessonProgramSet);
        //core java tarfinda lessonProgram guncellendi
        student.setLessonsProgramList(studentCurrentLessonProgram);
        //db de lessonProgram guncelendi
        Student savedStudent = studentRepository.save(student);

        return ResponseMessage.<StudentResponse>builder()
                .message(SuccessMessages.LESSON_PROGRAM_ADD_TO_STUDENT)
                .object(studentMapper.mapStudentToStudentResponse(savedStudent))
                .httpStatus(HttpStatus.OK)
                .build();

    }

    //username var mi kontrolü--> login olan kisinin username i zaten db de mevcut olmasi lazim ama aradan zaman gecerse baska
    //user giris yapmis olabilir
    //gaarnti olsun diye kontrol ediyoruz
    //requestden gelen ile mevcutta olan lessonProgramlari aldim ve onceden yazdigimiz method ile cakisma var mi diye kontrol ediyorum
    //kontrol ettiktan sonra cakisma yoksa mevcut olan listeye yeni listeyi ekeldik addAll() ile
    //student clasinda lessonProgrammList i setledim ve db ye kaydetttim

    public  Student isStudentExistByUsername(String username){
        Student student = studentRepository.findByUsernameEquals(username);   //arguman olarak gonderdigim username i sahip ogrenciyi dön!!
        if(student.getId()==null){                                            //optional yapmadigim icin null olup olmadigini kontrol etmem lazim, id yi kontrol ediyorum
            throw  new ResourceNotFoundException(ErrorMessages.NOT_FOUND_USER_MESSAGE);
        }
        return  student;
    }
}