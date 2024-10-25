package com.project.course.subscription.repository;

import com.project.course.subscription.model.PaxUser;
import com.project.course.subscription.model.PaxUser.Type;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface PaxUserRepository extends JpaRepository<PaxUser,Long> {

    //List<PaxUser> findByIsActiveTrue();

    @Query("SELECT u FROM PaxUser u WHERE u.type = 'HEAD'")
    List<PaxUser> findAllHeads(boolean var);

    @Query("SELECT u FROM PaxUser u WHERE u.type = 'MEMBER'")
    List<PaxUser> findAllMembers();
    
    Optional<PaxUser> findByUuid(String uuid);
    
    Optional<PaxUser> findByUuidAndType(String uuid, PaxUser.Type type);
    
    @Query("SELECT u FROM PaxUser u WHERE u.type = 'HEAD' AND u.isActive = true")
    List<PaxUser> findByIsActiveTrue();



}
