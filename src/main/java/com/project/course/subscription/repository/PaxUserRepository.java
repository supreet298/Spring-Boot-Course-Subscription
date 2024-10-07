package com.project.course.subscription.repository;

import com.project.course.subscription.model.PaxUser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface PaxUserRepository extends JpaRepository<PaxUser,Long> {

    List<PaxUser> findByIsActiveTrue();

    @Query("SELECT u FROM PaxUser u WHERE u.type = 'HEAD'")
    List<PaxUser> findAllHeads();

    @Query("SELECT u FROM PaxUser u WHERE u.type = 'MEMBER'")
    List<PaxUser> findAllMembers();
}