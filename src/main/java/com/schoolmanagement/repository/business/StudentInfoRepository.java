package com.schoolmanagement.repository.business;

import com.schoolmanagement.entity.concretes.business.StudentInfo;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface StudentInfoRepository extends JpaRepository<StudentInfo, Long> {
    List<StudentInfo> getAllByStudentId_Id(Long studentId);

}