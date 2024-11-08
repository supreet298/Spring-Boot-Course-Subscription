package com.project.course.subscription.service.Impl;

import java.util.List;
import java.util.stream.Collectors;
import com.project.course.subscription.dto.PaxHeadResponseDTO;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import com.project.course.subscription.dto.PaxHeadDTO;
import com.project.course.subscription.dto.PaxMemberPostDTO;
import com.project.course.subscription.dto.PaxUsersDTO;
import com.project.course.subscription.exception.ResourceNotFoundException;
import com.project.course.subscription.model.PaxUser;
import com.project.course.subscription.model.PaxUser.Type;
import com.project.course.subscription.repository.PaxUserRepository;
import com.project.course.subscription.service.PaxUserService;

@Service
public class PaxUserServiceImpl implements PaxUserService {

    @Autowired
    private PaxUserRepository paxUserRepository;

    @Autowired
    private ModelMapper modelMapper;

    @Override
    public PaxUser addPaxHead(PaxUser paxUser) {
        PaxUser head = new PaxUser();
        head.setName(paxUser.getName());
        if (paxUserRepository.existsByEmailAndIsActiveTrue(paxUser.getEmail())) {
            throw new RuntimeException("Email id already exists, try another.");
        }
        head.setEmail(paxUser.getEmail());
        head.setAddress(paxUser.getAddress());
        head.setCountry(paxUser.getCountry());
        head.setCountryCode(paxUser.getCountryCode());
        if (paxUserRepository.existsByPhoneNumberAndIsActiveTrue(paxUser.getPhoneNumber())) {
            throw new RuntimeException("Mobile number already exists, try another.");
        }
        head.setPhoneNumber(paxUser.getPhoneNumber());
        head.setType(PaxUser.Type.HEAD);
        return paxUserRepository.save(head);
    }

    @Override
    public PaxUser updatePaxHead(String uuid, PaxUser request) {
        PaxUser existingHead = paxUserRepository.findByUuid(uuid)
                .orElseThrow(() -> new UsernameNotFoundException("No HEAD found for UUID: " + uuid));

        existingHead.setName(request.getName());
        if (!existingHead.getEmail().equals(request.getEmail())) {
            // Step 3: If email is different, check if any other active user has the same
            // email
            if (paxUserRepository.existsByEmailAndIsActiveTrue(request.getEmail())) {
                throw new RuntimeException("Email id already exists, try another.");
            }
        }
        existingHead.setEmail(request.getEmail());
        if (!existingHead.getPhoneNumber().equals(request.getPhoneNumber())) {
            if (paxUserRepository.existsByPhoneNumberAndIsActiveTrue(request.getPhoneNumber())) {
                throw new RuntimeException("Mobile number already exists, try another.");
            }
        }
        existingHead.setCountryCode(request.getCountryCode());
        existingHead.setPhoneNumber(request.getPhoneNumber());
        existingHead.setAddress(request.getAddress());
        existingHead.setCountry(request.getCountry());
        existingHead.setType(PaxUser.Type.HEAD);
        return paxUserRepository.save(existingHead);
    }

    @Override
    public PaxUser addPaxMembers(String uuid, PaxMemberPostDTO paxMemberDTO) {
        PaxUser head = paxUserRepository.findByUuidAndTypeAndIsActiveTrue(uuid, Type.HEAD)
                .orElseThrow(() -> new IllegalArgumentException("Head's Uuid does not exist."));

        PaxUser member = new PaxUser();
        member.setName(paxMemberDTO.getUserName());

        if (paxUserRepository.existsByEmailAndIsActiveTrue(paxMemberDTO.getEmail())) {
            throw new RuntimeException("Email id already exists, try another.");
        }
        member.setEmail(paxMemberDTO.getEmail());

        if (paxUserRepository.existsByPhoneNumberAndIsActiveTrue(paxMemberDTO.getPhoneNumber())) {
            throw new RuntimeException("Mobile number already exists, try another.");
        }

        member.setCountryCode(paxMemberDTO.getCountryCode());
        member.setPhoneNumber(paxMemberDTO.getPhoneNumber());
        member.setAddress(paxMemberDTO.getAddress());
        member.setCountry(paxMemberDTO.getCountry());
        member.setType(PaxUser.Type.MEMBER);
        member.setHeadUuid(head.getUuid());
        member.setRelation(PaxUser.Relation.valueOf(paxMemberDTO.getRelation()));

        return paxUserRepository.save(member);
    }

    @Override
    public Page<PaxUsersDTO> getAllPaxMemberByHeadUuid(String uuid, Pageable pageable) {
        Page<PaxUser> allMembers = paxUserRepository.findAllActiveMembersByHeadUuid(uuid, pageable);

        if (!allMembers.isEmpty()) {
            return allMembers
                    .map(paxUser -> {
                        PaxUsersDTO dto = new PaxUsersDTO();
                        dto.setUuid(paxUser.getUuid());
                        dto.setName(paxUser.getName());
                        dto.setEmail(paxUser.getEmail());
                        dto.setHeadUuid(paxUser.getHeadUuid());
                        dto.setAddress(paxUser.getAddress());
                        dto.setCountry(paxUser.getCountry());
                        dto.setPhoneNumber(paxUser.getCountryCode() + paxUser.getPhoneNumber());
                        dto.setType(paxUser.getType().toString());
                        dto.setRelation(paxUser.getRelation().toString());
                        // Exclude relation
                        return dto;
                    });
        } else {
            throw new ResourceNotFoundException("No members are found under this head");
        }
    }

    @Override
    public PaxUser updatePaxMember(String uuid, PaxUser paxMember) {
        PaxUser existingMember = paxUserRepository.findByUuidAndTypeAndIsActiveTrue(uuid, Type.MEMBER)
                .orElseThrow(() -> new UsernameNotFoundException("No Member found for UUID: " + uuid));

        existingMember.setName(paxMember.getName());
        if (!existingMember.getEmail().equals(paxMember.getEmail())) {
            // Step 3: If email is different, check if any other active user has the same
            // email
            if (paxUserRepository.existsByEmailAndIsActiveTrue(paxMember.getEmail())) {
                throw new RuntimeException("Email id already exists, try another.");
            }
        }

        existingMember.setEmail(paxMember.getEmail());
        if (!existingMember.getPhoneNumber().equals(paxMember.getPhoneNumber())) {
            if (paxUserRepository.existsByPhoneNumberAndIsActiveTrue(paxMember.getPhoneNumber())) {
                throw new RuntimeException("Mobile number already exists, try another.");
            }
        }
        existingMember.setCountryCode(paxMember.getCountryCode());
        existingMember.setPhoneNumber(paxMember.getPhoneNumber());
        existingMember.setAddress(paxMember.getAddress());
        existingMember.setCountry(paxMember.getCountry());
        existingMember.setType(PaxUser.Type.MEMBER);
        existingMember.setRelation(paxMember.getRelation());
        return paxUserRepository.save(existingMember);

    }

    @Override
    public Page<PaxHeadDTO> getAllHead(Pageable pageable) {
        return paxUserRepository.findByIsActiveTrue(pageable).map(this::convertToHeadDTO);
    }

    @Override
    public PaxUser getHeadUserByUuid(String uuid) {
        PaxUser paxUser = paxUserRepository.findByUuidAndIsActiveTrue(uuid)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "PaxUser not found"));

        // Check if the PaxUser's type is HEAD
        if (!paxUser.getType().equals(PaxUser.Type.HEAD)) {
            throw new IllegalArgumentException("Only PaxUser of type HEAD can purchase subscription.");
        }

        return paxUser; // Return the PaxUser if the check passes
    }

    private PaxHeadDTO convertToHeadDTO(PaxUser paxUser) {
        PaxHeadDTO dto = new PaxHeadDTO();
        dto.setUuid(paxUser.getUuid());
        dto.setPhoneNumber(paxUser.getPhoneNumber());
        dto.setName(paxUser.getName());
        dto.setEmail(paxUser.getEmail());
        dto.setCountryCode(paxUser.getCountryCode());
        dto.setType(paxUser.getType());
        return dto;
    }


    @Override
    public PaxUser dropPaxUser(String uuid) {
        PaxUser existingHead = paxUserRepository.findByUuid(uuid)
                .orElseThrow(() -> new UsernameNotFoundException("No PaxUser found for this UUID: " + uuid));
        if (existingHead.isActive()) {
            existingHead.setActive(false);
            paxUserRepository.save(existingHead);
        }

        return existingHead;

    }


    @Override
    public PaxUser getPaxHeadById(String uuid) {
        PaxUser paxHead = paxUserRepository.findByUuidAndTypeAndIsActiveTrue(uuid, Type.HEAD)
                .orElseThrow(() -> new UsernameNotFoundException("No HEAD found for UUID: " + uuid));
        return paxHead;

    }

    @Override
    public PaxUser getPaxMemberById(String uuid) {
        PaxUser paxMember = paxUserRepository.findByUuidAndTypeAndIsActiveTrue(uuid, Type.MEMBER)
                .orElseThrow(() -> new UsernameNotFoundException("No Member found for UUID: " + uuid));
        return paxMember;

    }

    @Override
    public List<PaxHeadResponseDTO> searchHead(String query) {
        // Fetch the list of PaxUser entities
        List<PaxUser> paxUsers = paxUserRepository.searchByMultipleFieldsAndHeadType(query);

        // Manually map the fields, excluding the 'relation' field
        return paxUsers.stream()
                .map(paxUser -> {
                    PaxHeadResponseDTO dto = new PaxHeadResponseDTO();
                    dto.setUuid(paxUser.getUuid());
                    dto.setName(paxUser.getName());
                    dto.setEmail(paxUser.getEmail());
                    dto.setAddress(paxUser.getAddress());
                    dto.setCountry(paxUser.getCountry());
                    dto.setPhoneNumber(paxUser.getCountryCode() + paxUser.getPhoneNumber());
                    dto.setType(paxUser.getType().toString());
                    // Exclude relation
                    return dto;
                })
                .collect(Collectors.toList());
    }

    @Override
    public List<PaxUsersDTO> searchMemberByHeadUuid(String uuid, String query) {
        List<PaxUser> paxUsers = paxUserRepository.searchByMultipleFieldsAndMemberTypeByHeadUuid(uuid, query);
        return paxUsers.stream()
                .map(paxUser -> {
                    PaxUsersDTO dto = new PaxUsersDTO();
                    dto.setName(paxUser.getName());
                    dto.setEmail(paxUser.getEmail());
                    dto.setAddress(paxUser.getAddress());
                    dto.setCountry(paxUser.getCountry());
                    dto.setPhoneNumber(paxUser.getCountryCode() + paxUser.getPhoneNumber());
                    dto.setType(paxUser.getType().toString());
                    dto.setRelation(paxUser.getRelation().toString());
                    // Exclude relation
                    return dto;
                })
                .collect(Collectors.toList());
    }

}