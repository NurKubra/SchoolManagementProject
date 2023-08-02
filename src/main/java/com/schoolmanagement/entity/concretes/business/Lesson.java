package com.schoolmanagement.entity.concretes.business;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.Set;
@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Lesson {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long lessonId;

    private String lessonName;

    private Integer creditScore;

    private Boolean isCompulsory;

    @JsonIgnore
    @ManyToMany(mappedBy = "lessons", cascade = CascadeType.REMOVE, fetch = FetchType.EAGER)
    private Set<LessonProgram> lessonPrograms;

}




/*@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Lesson {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long lessonId;

    private String lessonName;

    private Integer creditScore;

    private Boolean isCompulsory;   //zorunlu bir ders mi


    //not: lessonProgram iliskisi
    //teacher? ile ilskilendirmedik ->lessonProgram ile zaten lesson'nin hangi ogretmene ait oldugunu ogrencem o yuzden buraya eklemedik

    @JsonIgnore
    @ManyToMany(mappedBy = "lessons",cascade = CascadeType.REMOVE)  //mesela maetemtik silinirse matematik programi da silinsin
    private Set<LessonProgram> lessonPrograms;


}*/
