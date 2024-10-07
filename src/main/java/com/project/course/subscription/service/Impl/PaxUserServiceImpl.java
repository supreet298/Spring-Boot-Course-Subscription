package com.project.course.subscription.service.Impl;

import com.project.course.subscription.dto.PaxHeadDTO;
import com.project.course.subscription.dto.PaxMemberDTO;
import com.project.course.subscription.model.PaxUser;
import com.project.course.subscription.repository.PaxUserRepository;
import com.project.course.subscription.service.PaxUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class PaxUserServiceImpl implements PaxUserService {

    @Autowired
    private PaxUserRepository paxUserRepository;

    @Override
    public PaxUser addPaxHead(PaxUser paxUser) {
        PaxUser head = new PaxUser();
        head.setUserName(paxUser.getUserName());
        head.setEmail(paxUser.getEmail());
        head.setPhoneNumber(paxUser.getPhoneNumber());
        head.setType(PaxUser.Type.HEAD);
        return paxUserRepository.save(head);
    }

    @Override
    public PaxUser addPaxMember(PaxUser paxUser) {

        PaxUser head = paxUserRepository.findById(paxUser.getHeadId())
                .orElseThrow(() -> new IllegalArgumentException("Head ID does not exist."));

        if (head.getType() != PaxUser.Type.HEAD) {
            throw new IllegalArgumentException("Only users of type HEAD can create members.");
        }

        PaxUser member = new PaxUser();
        member.setUserName(paxUser.getUserName());
        member.setEmail(paxUser.getEmail());
        member.setPhoneNumber(paxUser.getPhoneNumber());
        member.setType(PaxUser.Type.MEMBER);
        member.setHeadId(head.getId()); // Associate with the head
        member.setRelation(paxUser.getRelation()); // Set relation
        return paxUserRepository.save(member);
    }

    @Override
    public List<PaxHeadDTO> getAllHead() {
        List<PaxUser> heads = paxUserRepository.findAllHeads();
        return heads.stream()
                .map(this::convertToHeadDTO)
                .collect(Collectors.toList());
    }

    @Override
    public List<PaxMemberDTO> getAllMember() {
        List<PaxUser> members = paxUserRepository.findAllMembers();
        Map<Long, String> headIdToNameMap = new HashMap<>();

        // First, fetch heads to create a mapping
        List<PaxUser> heads = paxUserRepository.findAllHeads();
        heads.forEach(head -> headIdToNameMap.put(head.getId(), head.getUserName()));

        return members.stream()
                .map(member -> convertToMemberDTO(member, headIdToNameMap))
                .collect(Collectors.toList());
    }

    @Override
    public PaxUser getHeadUserById(Long id) {
        PaxUser paxUser = paxUserRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "PaxUser not found"));

        // Check if the PaxUser's type is HEAD
        if (!paxUser.getType().equals(PaxUser.Type.HEAD)) {
            throw new BadCredentialsException("Only PaxUser of type HEAD can purchase subscription.");
        }

        return paxUser; // Return the PaxUser if the check passes
    }

    private PaxHeadDTO convertToHeadDTO(PaxUser paxUser){
        PaxHeadDTO dto =  new PaxHeadDTO();
        dto.setUuid(paxUser.getUuid());
        dto.setUserName(paxUser.getUserName());
        dto.setEmail(paxUser.getEmail());
        return dto;
    }

    private PaxMemberDTO convertToMemberDTO(PaxUser member, Map<Long, String> headIdToNameMap) {
        PaxMemberDTO dto = new PaxMemberDTO();
        dto.setUuid(member.getUuid());
        dto.setUserName(member.getUserName());
        dto.setEmail(member.getEmail());
        dto.setRelation(member.getRelation().name());

        // Retrieve head name from the map
        String headName = headIdToNameMap.get(member.getHeadId());
        dto.setHeadName(headName != null ? headName : "Head not found");

        return dto;
    }
}
