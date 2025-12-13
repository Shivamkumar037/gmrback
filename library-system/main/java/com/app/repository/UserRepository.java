package com.app.repository;

import com.app.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    
    Optional<User> findByMobile(String mobile);
    
    Optional<User> findByReferralCode(String referralCode);
    
    boolean existsByMobile(String mobile);
    
    boolean existsByReferralCode(String referralCode);
}