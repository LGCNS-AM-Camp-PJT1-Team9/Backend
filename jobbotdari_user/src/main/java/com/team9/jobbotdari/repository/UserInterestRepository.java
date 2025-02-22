package com.team9.jobbotdari.repository;

import com.team9.jobbotdari.entity.User;
import com.team9.jobbotdari.entity.UserInterest;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserInterestRepository extends JpaRepository<UserInterest, Long> {
    boolean existsByUserAndCompanyId(User user, Long companyId);
}
