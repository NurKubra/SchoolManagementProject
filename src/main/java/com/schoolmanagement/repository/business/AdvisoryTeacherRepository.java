package com.schoolmanagement.repository.business;

import com.schoolmanagement.entity.concretes.user.AdvisoryTeacher;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AdvisoryTeacherRepository extends JpaRepository<AdvisoryTeacher, Long> {
}