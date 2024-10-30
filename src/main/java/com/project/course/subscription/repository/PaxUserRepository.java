package com.project.course.subscription.repository;

import com.project.course.subscription.dto.PaxMemberDTO;
import com.project.course.subscription.model.PaxUser;
import com.project.course.subscription.model.PaxUser.Type;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
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
    
//    @Query("SELECT p FROM PaxUser p WHERE p.type.uuid = 'HEAD':uuid AND p.isActive = true")
//    List<PaxMemberDTO> findAllIsActiveMemberByHeadUuid(@Param("uuid") String uuid);
//
//    @Query("SELECT p FROM PaxUser p WHERE p.head.uuid = :uuid AND p.isActive = true")
//    List<PaxUser> findAllActiveMembersByHeadUuid(@Param("uuid") String uuid);
    	
    @Query("SELECT p FROM PaxUser p WHERE p.headId = :headId AND p.type = 'MEMBER' AND p.isActive = true")
    List<PaxUser> findAllActiveMembersByHeadId(@Param("headId") Long headId);
    
    boolean existsByPhoneNumber(String phoneNumber);
    
    boolean existsByEmail(String email);
    
    Optional<PaxUser> findByUuidAndIsActiveTrue(String uuid);

    
}
