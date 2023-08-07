package com.schoolmanagement.repository.business;

import com.schoolmanagement.entity.concretes.business.LessonProgram;
import com.schoolmanagement.payload.response.LessonProgramResponse;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Set;

public interface LessonProgramRepository  extends JpaRepository<LessonProgram, Long> {

    List<LessonProgram> findByTeachers_IdNull();


    List<LessonProgram> findByTeachers_IdNotNull();


    @Query("SELECT l FROM LessonProgram l INNER JOIN l.teachers teachers WHERE teachers.username = ?1 ")
    Set<LessonProgram> getLessonProgramByTeacherUsername(String userName);
    // lessonProgram clasina git burdan teachers fieldini al bu ayni zamanda bir class

    @Query("SELECT l FROM LessonProgram l INNER JOIN l.students students WHERE students.username = ?1 ")
    Set<LessonProgram> getLessonProgramByStudentsUsername(String userName);


    // Not: TeacherService de kullanildi
    @Query("SELECT l FROM LessonProgram l WHERE l.id IN :lessonIdSet") // SELECT * FROM lesson_program WHERE lesson_program.id IN (2,3) ;
    Set<LessonProgram> getLessonProgramByLessonProgramByIdList(Set<Long> lessonIdSet); //2 ve 3 id li lesson programlari getir



}


/*
"INNER JOIN l.teachers teachers" ifadesi, "LessonProgram" sınıfının "teachers" adlı alanı ile "teachers" adlı başka
bir varlık sınıfı arasında iç içe geçmiş birleştirme yapar.
Bu, ders programlarıyla ilişkilendirilmiş öğretmenleri almak için yapılır.
 */