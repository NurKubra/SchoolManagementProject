package com.schoolmanagement.entity.concretes.business;


import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.schoolmanagement.entity.concretes.user.AdvisoryTeacher;
import com.schoolmanagement.entity.concretes.user.Student;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.time.LocalDate;
import java.util.List;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder(toBuilder = true)
public class Meet {


    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String description;

    @JsonFormat(shape = JsonFormat.Shape.STRING,pattern= "yyyy-MM-dd")
    private LocalDate date;

    @JsonFormat(shape = JsonFormat.Shape.STRING,pattern= "yyyy-MM-dd",timezone = "US")
    private LocalDate startTime;

    @JsonFormat(shape = JsonFormat.Shape.STRING,pattern= "yyyy-MM-dd",timezone = "US")
    private LocalDate stopTime;

    //advisoryTeacher ilsikisi
    @ManyToOne(cascade = CascadeType.PERSIST)            //bir ogretmein birden cok meeting i olabilir demis olduk
    @JsonIgnoreProperties("teacher")                     //advisory teacher icindeki teacher a gitmesin infinitive loop kirilmis olur
    private AdvisoryTeacher advisoryTeacher;


    //student ile iliskisi
    @ManyToMany                                         //bir meetinge birden fazla ogrenci katilabilir, her ogrencinin de birden fazla meetingi olabilir
    @JoinTable(
            name = "meet_student_table",
            joinColumns = @JoinColumn(name = "meet_id"),  //bu clastan gelen
            inverseJoinColumns = @JoinColumn(name = "student_id")  //diger taraftan gelen
    )
    private List<Student> studentList;
}
