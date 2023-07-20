package com.schoolmanagement.entity.concretes.business;


import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.schoolmanagement.entity.concretes.user.Student;
import com.schoolmanagement.entity.concretes.user.Teacher;
import com.schoolmanagement.entity.enums.Note;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder  //herhangi bir yerden extends edilmedigi iicn @builder yeterli
public class StudentInfo {  //user degil o yuzden extends etmiyoruz

    //StudentInfo her ders icin ayri olcak ve bir ogrecinin birden fazla StudentInfo olurken bir StudentInfo bir ogrenciye ait olcak ?

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Integer absentee;

    private Double midtermExam;

    private Double finalExam;

    private Double examAverage;

    private String infoNote;

    //not: Teacher ve Student

    @ManyToOne  //her ogrencinin bir tane
    private Student student;

    @ManyToOne
    private Teacher teacher;

    @Enumerated(EnumType.STRING)
    private Note letterGrade;   //ogrenci harf notlari , AA,BA gibi

    //Not :Lesson ve EducationTerm ile ilsískilendirilcek
    // EducationTerm --> guz ve yaz donemi

    //EducationTerm ilskisi
    @OneToOne     //manytoone ??
    private EducationTerm educationTerm;

    //lesson iliskisi
    @ManyToOne
    @JsonIgnoreProperties("lesson") //loopu kirmanin diger bir yolu
    private Lesson lesson;



}
