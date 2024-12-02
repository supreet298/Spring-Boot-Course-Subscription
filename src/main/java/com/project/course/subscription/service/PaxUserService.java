package com.project.course.subscription.service;

import java.util.List;

import org.springframework.data.domain.Page;

import com.project.course.subscription.dto.PaxHeadDTO;
import com.project.course.subscription.dto.PaxMemberPostDTO;
import com.project.course.subscription.dto.PaxUsersDTO;
import com.project.course.subscription.model.PaxUser;

import jakarta.validation.Valid;

public interface PaxUserService {

	PaxUser addPaxHead(@Valid PaxUser paxUser) throws Exception;

	PaxUser updatePaxHead(String uuid, PaxUser request);

	PaxUser updatePaxMember(String uuid, PaxUser paxUserMember);

	PaxUser dropPaxUser(String uuid);

	PaxUser getHeadUserByUuid(String uuid);

	PaxHeadDTO getPaxHeadById(String uuid);

	PaxUsersDTO getPaxMemberById(String uuid);

	PaxUser addPaxMembers(String uuid, PaxMemberPostDTO paxMemberDTO);

	List<PaxHeadDTO> searchHead(String query,String sortBy, String direction);

	List<PaxUsersDTO> searchMemberByHeadUuid(String uuid,String query,String sortBy, String direction);

	Page<PaxHeadDTO> getAllPaginatedAndSortedHeads(int page, int size, String sortBy, String direction);
	
	Page<PaxUsersDTO> getAllPaginatedAndSortedPaxMembersByHeadUuid(String uuid, int page, int size, String sortBy,
			String direction);

}