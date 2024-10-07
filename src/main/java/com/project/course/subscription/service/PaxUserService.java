package com.project.course.subscription.service;

import com.project.course.subscription.dto.PaxHeadDTO;
import com.project.course.subscription.dto.PaxMemberDTO;
import com.project.course.subscription.model.PaxUser;
import jakarta.validation.Valid;
import java.util.List;

public interface PaxUserService {

    PaxUser addPaxHead(@Valid PaxUser paxUser);

    PaxUser addPaxMember(@Valid PaxUser paxUser);

    List<PaxHeadDTO> getAllHead();

    List<PaxMemberDTO> getAllMember();

    PaxUser getHeadUserById(Long id);
}
