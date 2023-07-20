package com.schoolmanagement.entity.concretes.user;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.schoolmanagement.entity.abstracts.User;
import com.schoolmanagement.entity.concretes.business.*;
import lombok.*;
import lombok.experimental.SuperBuilder;

import javax.persistence.*;
import java.util.List;
import java.util.Set;


@Entity
@Getter
@Setter
@NoArgsConstructor
@SuperBuilder  //superBuilder kulllanimzasa paramtreli conts icine parent daki fieldlari de manuel olarak eklemek zorunda kalirim
@EqualsAndHashCode
@ToString(callSuper = true) //callSuper = true --> ile parentdaki fieldlari da alir ve yazdirir
public class Student extends User {


    private String motherName;
    private String fatherName;
    private int studentNumber;
    private boolean isActive;
    @Column(unique = true)
    private String email;


    //not: StudentInfo, LessonProgram, AdvisoryTeacher, Meet ile ilsikilendirilecek

    //lessonProgram ilskisi

    @JsonIgnore
    @ManyToMany
    @JoinTable(
            name = "student_lessonprogram",
            joinColumns = @JoinColumn(name = "student_id"),
            inverseJoinColumns = @JoinColumn(name = "lesson_program_id")
    )
    private Set<LessonProgram> lessonsProgramList;

    //advisoryTeacher ilskilendirmesi
    @JsonIgnore
    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "advisory_teacher_id")  //oluscak sutun adi
    private AdvisoryTeacher advisoryTeacher;

    //NOT :Requestin geldigi yerden loop kirilmali -->@JsonIgnore


    //Studentinfo ilskisi
    @OneToMany(mappedBy = "student",cascade = CascadeType.ALL)
    private List<StudentInfo> studentInfos;


    //meet ile
    @JsonIgnore
    @ManyToMany
    @JoinTable(
            name = "meet_student_table",
            joinColumns = @JoinColumn(name = "student_id"),
            inverseJoinColumns = @JoinColumn(name = "meet_id")
    )
    private List<Meet> meetList;


}
