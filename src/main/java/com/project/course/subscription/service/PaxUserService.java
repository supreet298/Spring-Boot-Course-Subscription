package com.project.course.subscription.service;

import com.project.course.subscription.dto.PaxHeadDTO;
import com.project.course.subscription.dto.PaxMemberDTO;
import com.project.course.subscription.dto.PaxMemberPostDTO;
import com.project.course.subscription.model.PaxUser;
import jakarta.validation.Valid;
import java.util.List;

public interface PaxUserService {

    PaxUser addPaxHead(@Valid PaxUser paxUser);

    PaxUser addPaxMember(@Valid PaxMemberPostDTO paxMemberDTO);

    List<PaxHeadDTO> getAllHead();

   // List<PaxMemberDTO> getAllMember();

    PaxUser updatePaxHead(String uuid,PaxUser request);
    
    PaxUser updatePaxMember(String uuid, PaxUser paxUserMember);
    
    PaxUser dropPaxUser(String uuid);

    PaxUser getHeadUserById(Long id);
}
