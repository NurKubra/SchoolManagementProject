package com.schoolmanagement.entity.concretes.user;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Entity
@Data  //@Data bazen memory yani performans ozelliklerini olumsuz etkileyebilir.
@AllArgsConstructor  //yazmasak da olur zaten @Builder bu isi yapiyor
@NoArgsConstructor
@Builder
public class AdvisoryTeacher { //Userdan extends etmedik


    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private UserRole userRole;

    //not : Teacher- Student ile ilsikilendirilecek
    @OneToOne
    private Teacher teacher;

    @Column(unique = true)
    private String email;

    //not: LessonProgram ve StudentInfo ile ilskilendirilecek



}
