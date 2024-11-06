package com.project.course.subscription.repository;

import com.project.course.subscription.dto.PaxMemberDTO;
import com.project.course.subscription.model.PaxUser;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface PaxUserRepository extends JpaRepository<PaxUser, Long> {

	@Query("SELECT u FROM PaxUser u WHERE u.type = 'HEAD'")
	List<PaxUser> findAllHeads(boolean var);

	@Query("SELECT u FROM PaxUser u WHERE u.type = 'MEMBER'")
	List<PaxUser> findAllMembers();

	Optional<PaxUser> findByUuidAndIsActiveTrue(String uuid);

	Optional<PaxUser> findByUuid(String uuid);

	Optional<PaxUser> findByUuidAndType(String uuid, PaxUser.Type type);

	@Query("SELECT u FROM PaxUser u WHERE u.type = 'HEAD' AND u.isActive = true")
	Page<PaxUser> findByIsActiveTrue(Pageable pageable);

	@Query("SELECT p FROM PaxUser p WHERE p.headUuid = :headUuid AND p.isActive = true")
	Page<PaxUser> findAllActiveMembersByHeadUuid(@Param("headUuid") String headUuid,Pageable pageable);

	boolean existsByPhoneNumberAndIsActiveTrue(String phoneNumber);

	boolean existsByEmailAndIsActiveTrue(String email);
	
	Page<PaxUser> findAll(Pageable pageable);

}