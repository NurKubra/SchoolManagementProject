package com.schoolmanagement.repository.business;

import com.schoolmanagement.entity.concretes.business.EducationTerm;
import com.schoolmanagement.entity.enums.Term;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface EducationTermRepository extends JpaRepository<EducationTerm,Long> {



        @Query("SELECT (count(e)>0) FROM EducationTerm e WHERE e.term=?1 AND EXTRACT(YEAR FROM e.startDate) = ?2")
        boolean existsByTermAndYear(Term term, int year);

        //2. kisimda exact ile startDate icindeki yil bilgisini cektik disardan gelen yil ile ayni diye kontrol ettik
    }