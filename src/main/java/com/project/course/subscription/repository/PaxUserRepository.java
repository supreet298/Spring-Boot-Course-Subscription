package com.project.course.subscription.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.project.course.subscription.model.PaxUser;

@Repository
public interface PaxUserRepository extends JpaRepository<PaxUser, Long> {

	Optional<PaxUser> findByUuidAndIsActiveTrue(String uuid);

	Optional<PaxUser> findByUuid(String uuid);

	Optional<PaxUser> findByUuidAndTypeAndIsActiveTrue(String uuid, PaxUser.Type type);
	
	@Query("SELECT p FROM PaxUser p WHERE p.headUuid = :headUuid AND p.isActive = true")
	List<PaxUser> findAllActiveMembersByHeadUuid(@Param("headUuid") String headUuid);


	@Query("SELECT u FROM PaxUser u WHERE u.type = 'HEAD' AND u.isActive = true")
	Page<PaxUser> findByIsActiveTrue(Pageable pageable);

	@Query("SELECT p FROM PaxUser p WHERE p.headUuid = :headUuid AND p.isActive = true")
	Page<PaxUser> findAllActiveMembersByHeadUuid(@Param("headUuid") String headUuid,Pageable pageable);

	boolean existsByPhoneNumberAndIsActiveTrue(String phoneNumber);

	boolean existsByEmailAndIsActiveTrue(String email);
	
	@Query("SELECT i FROM PaxUser i WHERE (" +
		       "LOWER(i.name) LIKE LOWER(CONCAT('%', :query, '%')) OR " +
		       "LOWER(i.email) LIKE LOWER(CONCAT('%', :query, '%')) OR " +
		       "LOWER(i.countryCode || i.phoneNumber) LIKE LOWER(CONCAT('%', :query, '%')) )" +
		       "AND i.type = 'HEAD' AND i.isActive = true")
		List<PaxUser> searchByMultipleFieldsAndHeadType(@Param("query") String query, Sort sort);

	@Query("SELECT i FROM PaxUser i WHERE i.headUuid = :uuid " +
		       "AND (LOWER(i.name) LIKE LOWER(CONCAT('%', :query, '%')) OR " +
		       "LOWER(i.email) LIKE LOWER(CONCAT('%', :query, '%')) OR " +
		       "LOWER(i.countryCode || i.phoneNumber) LIKE LOWER(CONCAT('%', :query, '%')) )" +
		       "AND i.type = 'MEMBER' AND i.isActive = true")
		List<PaxUser> searchByMultipleFieldsAndMemberTypeByHeadUuid(@Param("uuid") String uuid, @Param("query") String query, Sort sort);

}