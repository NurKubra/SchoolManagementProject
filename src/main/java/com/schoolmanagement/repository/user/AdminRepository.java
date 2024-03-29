package com.schoolmanagement.repository.user;

import com.schoolmanagement.entity.concretes.user.Admin;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AdminRepository extends JpaRepository<Admin,Long> {


    boolean existsByUsername(String username);

    boolean existsBySsn(String ssn);

    boolean existsByPhoneNumber(String phone);

    Admin findByUsernameEquals(String username);
}
