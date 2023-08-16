package com.schoolmanagement.controller.user;

import com.schoolmanagement.entity.concretes.user.Student;
import com.schoolmanagement.payload.request.ChooseLessonProgramWithId;
import com.schoolmanagement.payload.request.StudentRequest;
import com.schoolmanagement.payload.response.ResponseMessage;
import com.schoolmanagement.payload.response.StudentResponse;
import com.schoolmanagement.service.user.StudentService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/students")
@RequiredArgsConstructor
public class StudentController {

    private final StudentService studentService;

    // Not :  Save() **********************************************************
    @PreAuthorize("hasAnyAuthority('ADMIN','MANAGER','ASSISTANT_MANAGER')")
    @PostMapping("/save")  // http://localhost:8080/students/save   +  POST  + JSON
    public ResponseMessage<StudentResponse> saveStudent(@RequestBody @Valid StudentRequest studentRequest) {
        return studentService.saveStudent(studentRequest);
    }


    //ogrencinin aktiflik durumunu degistirmek icin yazdik
    // Not: ChangeActıveStatus() ***********************************************
    @PreAuthorize("hasAnyAuthority('ADMIN','MANAGER','ASSISTANT_MANAGER')")
    @GetMapping("/changeStatus") // http://localhost:8080/students/changeStatus?id=1&status=false  + GET
    public ResponseMessage changeStatus(@RequestParam Long id , @RequestParam boolean status) {
        return studentService.changeStatus(id,status);
    }

    //patchMapping gibi calsitirdik sitemi --> arka planda save ya da update yapilcaksa da GrtMapping yazilabilir onu gördük


    // Not : getAll() ***********************************************************
    @PreAuthorize("hasAnyAuthority('ADMIN','MANAGER','ASSISTANT_MANAGER')")
    @GetMapping("/getAll")  // http://localhost:8080/students/getAll + GET
    public List<StudentResponse> getAllStudents(){

        return studentService.getAllStudents();
    }


    // Not: Update() *************************************************************
    @PreAuthorize("hasAnyAuthority('ADMIN','MANAGER','ASSISTANT_MANAGER')")
    @PutMapping("/update/{id}") // http://localhost:8080/students/upfdate/2 + PUT
    public ResponseMessage<StudentResponse> updateStudent(@PathVariable Long id, @RequestBody @Valid StudentRequest studentRequest){

        return studentService.updateStudent(id, studentRequest);
    }

    // Not : Delete() ************************************************************
    @PreAuthorize("hasAnyAuthority('ADMIN','MANAGER','ASSISTANT_MANAGER')")
    @DeleteMapping("/delete/{id}")  // http://localhost:8080/students/delete/2 +  DELETE
    public ResponseMessage deleteStudentById(@PathVariable Long id) {
        return studentService.deleteStudentById(id);
    }


    // Not : getByName() *********************************************************
    @PreAuthorize("hasAnyAuthority('ADMIN','MANAGER','ASSISTANT_MANAGER')")
    @GetMapping("/getStudentByName")  // http://localhost:8080/students/getStudentByName +  GET
    public List<StudentResponse>  getStudentByName(@RequestParam(name = "name") String studentName) {

        return studentService.findStudentByName(studentName);
    }

    //client side pageing ya da server side pageing yapilabilir ? --> frontend kisminda pageable yapiya cevirme (clinet side)
    //biz server side pageing yaptik

    // Not: getAllWithPage ***********************************************************
    @PreAuthorize("hasAnyAuthority('ADMIN','MANAGER','ASSISTANT_MANAGER')")
    @GetMapping("/getAllStudentByPage")  // http://localhost:8080/students/getAllStudentByPage +  GET
    public Page<StudentResponse> getAllStudentByPage(
            @RequestParam(value = "page") int page,
            @RequestParam(value = "size") int size,
            @RequestParam(value = "sort") String sort,
            @RequestParam(value = "type") String type
    ){
        return studentService.getAllStudentByPage(page,size,sort,type);
    }


    // Not : getById() ********************************************************
    // Todo : DONEN DEGERIN dto OLMASI GEREKIYOR
    @PreAuthorize("hasAnyAuthority('ADMIN','MANAGER','ASSISTANT_MANAGER')")
    @GetMapping("/getStudentById")  // http://localhost:8080/students/getStudentById +  GET
    public Student getStudentById(@RequestParam(name = "id") Long id) {
        return studentService.getStudentById(id);
    }


    //ögretmenin username'i su olan ogretmenlerin ogrencilerini getir(rehber ogretmenin kullanici adi uzerinden ogrencileri cagircaz)
    //getAtribute a alternatif bir yolla yaziyoruz
    //username i burda henuz setlemedik bunu postmande yapiyoruz
    //header icinde gondermek guvenligi sagliyor username direk gozukmuyor
    //yetkilendirmesine admin ve teacher yaptik
    //requestin attribute'una ulasmak icin HttpServletRequest cagiriyorum.
    //requestin header kismindaki key degeri username olan degeri getir demek --> herhangi bir kullanicinin username i gelmis oluyor
    //bu header dan username gelebilmesi icin simdilik postmanin header kisminda setleyecegiz --> bu sayede burda get ile getirebildik

    // Not: GetAllByAdvisoryTeacherUserName() ************************************************
    @PreAuthorize("hasAnyAuthority('ADMIN','TEACHER')")
    @GetMapping("/getAllByAdvisorId")  // http://localhost:8080/students/getAllByAdvisorId +  GET
    public List<StudentResponse> getAllByAdvisoryTeacherUserName(HttpServletRequest request){
        String userName = request.getHeader("username");        //normalde bunu service katmaninda yazmak best practice!
        return studentService.getAllByAdvisoryTeacherUserName(userName);

    }

    //ogrenci kendi lesson programina yeni bir lessonProgram ekliyor
    //currenti login olan bir kullanici icin yaziypruz
    //ögrencinin kendisini ya da lessonProgram donulebilir biz ogrenci donuyoruz

    // Not: addLessonProgramToStudentLessonsProgram() *************************
    @PreAuthorize("hasAnyAuthority('STUDENT')")
    @PostMapping("/chooseLesson")  // http://localhost:8080/students/chooseLesson +  POST
    public ResponseMessage<StudentResponse> chooseLesson(HttpServletRequest request,
                                                         @RequestBody @Valid ChooseLessonProgramWithId chooseLessonProgramWithId){
        String userName = (String) request.getAttribute("username");

        return studentService.chooseLesson(userName,chooseLessonProgramWithId);
    }
    //birden fazla id alcagimiz icin @RequestBody kullandik




}
