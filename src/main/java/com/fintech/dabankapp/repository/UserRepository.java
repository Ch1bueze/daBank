package com.fintech.dabankapp.repository;

import com.fintech.dabankapp.entity.AppUser;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<AppUser,Long> {
    Boolean existsByEmail(String email);
    Boolean existsByAccountNumber(String accountNumber);
    AppUser findByAccountNumber(String accountNumber);
}
