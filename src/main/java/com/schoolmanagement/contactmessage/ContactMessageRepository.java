package com.schoolmanagement.contactmessage;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;

@Repository
public interface ContactMessageRepository extends JpaRepository<ContactMessage,Long> {

    boolean existByEmailEqualsAndDateEquals(String email, LocalDate now);
}
