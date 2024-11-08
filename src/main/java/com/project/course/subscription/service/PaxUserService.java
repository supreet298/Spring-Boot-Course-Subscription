package com.project.course.subscription.service;

import java.util.List;

import com.project.course.subscription.dto.PaxHeadResponseDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
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

	PaxUser getPaxHeadById(String uuid);

	PaxUser getPaxMemberById(String uuid);

	PaxUser addPaxMembers(String uuid, PaxMemberPostDTO paxMemberDTO);

	Page<PaxUsersDTO> getAllPaxMemberByHeadUuid(String uuid,Pageable pageable);

	Page<PaxHeadDTO> getAllHead(Pageable pageable);

	List<PaxHeadResponseDTO> searchHead(String query);

	List<PaxUsersDTO> searchMemberByHeadUuid(String uuid,String query);


}