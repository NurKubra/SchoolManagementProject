package com.schoolmanagement.entity.concretes.user;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.List;

@Entity
@Data  //@Data bazen memory yani performans ozelliklerini olumsuz etkileyebilir.
@AllArgsConstructor  //yazmasak da olur zaten @Builder bu isi yapiyor
@NoArgsConstructor
@Builder
public class AdvisoryTeacher { //Userdan extends etmedik, bagimisz bir class


    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private UserRole userRole;

    //not : Teacher iliskisi
    @OneToOne
    private Teacher teacher;

    //not: student ile ilskilendirme
    @OneToMany(mappedBy = "advisoryTeacher", cascade = CascadeType.ALL)
    private List<Student> students;

    //not: LessonProgram ve StudentInfo ile ilskilendirilecek --ilskileri diger tarftan kontrol edecegiz





}
