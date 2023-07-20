package com.schoolmanagement.entity.concretes.user;

import com.schoolmanagement.entity.abstracts.User;
import lombok.*;
import lombok.experimental.SuperBuilder;

import javax.persistence.Column;
import javax.persistence.Entity;


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

}
