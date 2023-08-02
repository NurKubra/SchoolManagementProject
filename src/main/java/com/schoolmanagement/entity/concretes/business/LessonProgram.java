package com.schoolmanagement.entity.concretes.business;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.schoolmanagement.entity.concretes.user.Student;
import com.schoolmanagement.entity.concretes.user.Teacher;
import com.schoolmanagement.entity.enums.Day;
import lombok.*;

import javax.persistence.*;
import java.time.LocalTime;
import java.util.Set;


@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder(toBuilder = true)
public class LessonProgram {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    private Day day;


    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "HH:mm", timezone = "US")
    private LocalTime startTime;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "HH:mm", timezone = "US")
    private LocalTime stopTime;

    @JsonIgnore
    @ManyToMany
    @JoinTable(
            name = "lesson_program_lesson",
            joinColumns = @JoinColumn(name="lessonprogram_id"),
            inverseJoinColumns =@JoinColumn(name = "lesson_id")
    )
    private Set<Lesson> lessons;



    @ManyToOne(cascade = CascadeType.PERSIST)
    private EducationTerm educationTerm;



    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    @ManyToMany(mappedBy = "lessonsProgramList", fetch = FetchType.EAGER)
    private Set<Teacher> teachers;

    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    @ManyToMany(mappedBy = "lessonsProgramList", fetch = FetchType.EAGER)
    private Set<Student> students;

    @PreRemove
    private void removeLessonProgramFromStudent(){
        teachers.forEach(teacher -> teacher.getLessonsProgramList().remove(this));
        students.forEach(student -> student.getLessonsProgramList().remove(this));
    }

}

















/*
package com.schoolmanagement.entity.concretes.business;


import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.schoolmanagement.entity.concretes.user.Student;
import com.schoolmanagement.entity.concretes.user.Teacher;
import com.schoolmanagement.entity.enums.Day;
import lombok.*;

import javax.persistence.*;
import java.time.LocalTime;
import java.util.Set;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder(toBuilder = true)
public class LessonProgram {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    private Day day;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "HH:mm",timezone = "US")
    private LocalTime startTime;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "HH:mm",timezone = "US")
    private LocalTime stopTime;


    //Not: Leseon ile iliskilendirlmeli
    @JsonIgnore
    @ManyToMany
    @JoinTable(
            name = "lesson_program_lesson",
            joinColumns = @JoinColumn(name = "lessonprogram_id"),
            inverseJoinColumns = @JoinColumn(name = "lesson_id")
    )
    private Set<Lesson> lessons;
    //collection icindeki yapilarin unique olmasini saglamk icin set kullandim
    //mantomany 3. bir tablo olusturur bu tablonun adini verdik
    //bu olusan yeni tablonun 2 tane header'i olcak onlarin da ismini verdik


    //not: educationTerm ile ilskisi
    @ManyToOne(cascade = CascadeType.PERSIST)  //neden Persist yaptik ?  //bir educationTerm icinde birden fazla ders programi olabilir
    private EducationTerm educationTerm;


    //not: teacher lar ile iliskisi
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)  // ogrenci ders programini cagidriginda ogretmnelrin ismini programda gorsun istedgimiz icin yazdik
    @ManyToMany(mappedBy = "lessonProgramList", fetch = FetchType.EAGER)                                           //birden fazla matematik ogretmeni olabilir ve her ogretmenin birden fazla lessonProgrami vardir
    private Set<Teacher> teachers;                         //list yapsaydik unique olmazdi, set hem unique hem de size i sinirli degildir

    //onemli FETCHTYPE --> EAGER --> lessonProgrami cektigimde arkasindan ogretmneler de gelsin demek
    //lazy --> lessonProgrami cektigimizde

    //not: student lar ile iliskisi
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    @ManyToMany(mappedBy = "lessonProgramList", fetch = FetchType.EAGER)
    private Set<Student> students;

    //lessonProgrami silmek istedgimde student ve teacher lar da etkilenir--> bu yuzden teacher ve student daki lessonProgramlari silemm lazim

    @PreRemove  //db de Lessonprogram silinmeden bu methodu calistir
    private void removeLessonProgramFromStudent(){
        teachers.forEach(teacher -> teacher.getLessonsProgramList().remove(this));
        students.forEach(student -> student.getLessonsProgramList().remove(this));

        //set yapinin icinde dolan, her gelen teacheri teacher objesiyle karsila ve her ogretmenin getLessonProgramList ine git ve bu methodu tetikleyen hangi LessonProgramsa onu sil (this keyword)
    }


}
*/
