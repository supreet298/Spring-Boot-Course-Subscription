package com.project.course.subscription.repository;

import com.project.course.subscription.model.AdminUser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface AdminUserRepository extends JpaRepository<AdminUser,Long>{

    Optional<AdminUser> findByEmail(String email);
}
