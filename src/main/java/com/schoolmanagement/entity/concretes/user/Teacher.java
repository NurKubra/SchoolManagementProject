package com.schoolmanagement.entity.concretes.user;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.schoolmanagement.entity.abstracts.User;
import com.schoolmanagement.entity.concretes.business.LessonProgram;
import com.schoolmanagement.entity.concretes.business.StudentInfo;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

import javax.persistence.*;
import java.util.List;
import java.util.Set;

@Entity
@Getter
@Setter
@NoArgsConstructor
@SuperBuilder
@EqualsAndHashCode(callSuper = true, onlyExplicitlyIncluded = true)  //normalde @Data annotation bu isi yapar, burda farkli yol gostermek iicnbu sekilde yazdik
//equals ve hashcode methodlarini otomatik olarak olusmasini sagladim
//eger bu entity clasini baska birinden uretmissem ust siniflarin da hash code'unu hesapliyor
//iki teacher'i kiyaslayacagim zaman hashcode'lari hazir oldugu icin kiyaslama daha hizli gercklesir.
//parent ve child da da null olan fieldlarin hashcode unu hesaplamaz -->onlyExplicitlyIncluded = true sayesinde
public class Teacher extends User {

    //advisoryTeacher ile ilskisi --> bir ogretmen bir rehber ogretmeni olur,
    @OneToOne(mappedBy = "teacher",cascade = CascadeType.ALL,orphanRemoval = true) // "teacher" field'imimn bulundugu classta iliskiyi yonet, Teacher silindiginde ayni zamanda advisorTeacher sa onu da ayni zamanda silmek icin ekledik ikinci kismi, ucuncu kisim--> teacher, ayni zamanda advisorTaecher bu teacheri null a cekersem, null cekilenlerdi silmesi icin
    @JsonIgnore  //advisoryTaecher ile teacher arasinda bir kere dongu olduktan sonra kirmak icin
    private AdvisoryTeacher advisoryTeacher;


    private Boolean isAdvisor; // bir ogretmnein advisor olup olmaidgini kontrol etmek icin

    @Column(unique = true)
    private String email;

    //not: lessonProgram iliski
    @JsonIgnore
    @ManyToMany
    @JoinTable(
            name = "teacher_lessonprogram",
            joinColumns = @JoinColumn(name = "teacher_id"),
            inverseJoinColumns = @JoinColumn(name = "lesson_program_id")
    )
    private Set<LessonProgram> lessonsProgramList;

    //not: StudentInfo iliski
    @OneToMany(mappedBy = "teacher",cascade = CascadeType.REMOVE)
    private List<StudentInfo> studentInfos;


}


