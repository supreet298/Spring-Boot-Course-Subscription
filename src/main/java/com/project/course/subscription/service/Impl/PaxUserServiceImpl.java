package com.project.course.subscription.service.Impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import com.project.course.subscription.dto.PaxMemberPostDTO;
import com.project.course.subscription.exception.ResourceNotFoundException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import com.project.course.subscription.dto.PaxHeadDTO;
import com.project.course.subscription.dto.PaxMemberDTO;
import com.project.course.subscription.model.PaxUser;
import com.project.course.subscription.model.PaxUser.Type;
import com.project.course.subscription.notification.PhoneNumberValidation;
import com.project.course.subscription.repository.PaxUserRepository;
import com.project.course.subscription.service.PaxUserService;

@Service
public class PaxUserServiceImpl implements PaxUserService {

	@Autowired
	private PaxUserRepository paxUserRepository;

	@Override
	public PaxUser addPaxHead(PaxUser paxUser)   {
		PaxUser head = new PaxUser();
		head.setName(paxUser.getName());
		if (paxUserRepository.existsByEmail(paxUser.getEmail())) {
            throw new RuntimeException("Email id already exists, try another.");
        }
		head.setEmail(paxUser.getEmail());
		head.setAddress(paxUser.getAddress());
		head.setCountry(paxUser.getCountry());
//		if (!PhoneNumberValidation.isValid(paxUser.getPhoneNumber()))
//			throw new IllegalArgumentException(
//					"Invalid PhoneNumber,PhoneNumber starts with County code Eg: +91xxxxxxxxxx");
		if (paxUserRepository.existsByPhoneNumber(paxUser.getPhoneNumber())) {
            throw new RuntimeException("Mobile number already exists, try another.");
        }
		head.setPhoneNumber(paxUser.getPhoneNumber());
		head.setType(PaxUser.Type.HEAD);
		return paxUserRepository.save(head);
	}

	public PaxUser addPaxMember(PaxMemberPostDTO paxMemberDTO) {
		PaxUser head = paxUserRepository.findById(paxMemberDTO.getHeadId())
				.orElseThrow(() -> new IllegalArgumentException("Head ID does not exist."));

		if (head.getType() != PaxUser.Type.HEAD) {
			throw new IllegalArgumentException("Only users of type HEAD can create members.");
		}

		PaxUser member = new PaxUser();
		member.setName(paxMemberDTO.getUserName());
		member.setEmail(paxMemberDTO.getEmail());
		member.setPhoneNumber(paxMemberDTO.getPhoneNumber());
		member.setType(PaxUser.Type.MEMBER);
		member.setHeadId(head.getId());
		member.setRelation(PaxUser.Relation.valueOf(paxMemberDTO.getRelation()));
		return paxUserRepository.save(member);
	}

	public PaxUser addPaxMembers(String uuid, PaxMemberPostDTO paxMemberDTO) {
	    PaxUser head = paxUserRepository.findByUuidAndType(uuid, Type.HEAD)
	            .orElseThrow(() -> new IllegalArgumentException("Head's Uuid does not exist."));

	    PaxUser member = new PaxUser();
	    member.setName(paxMemberDTO.getUserName());
	    
	    if(paxUserRepository.existsByEmail(paxMemberDTO.getEmail()))
	    {
	        throw new RuntimeException("Email id already exists, try another.");
	    }
	    member.setEmail(paxMemberDTO.getEmail());
	    
	    if (paxUserRepository.existsByPhoneNumber(paxMemberDTO.getPhoneNumber())) {
	        throw new RuntimeException("Mobile number already exists, try another.");
	    }
	    
	    member.setPhoneNumber(paxMemberDTO.getPhoneNumber());
	    member.setAddress(paxMemberDTO.getAddress());
	    member.setCountry(paxMemberDTO.getCountry());
	    member.setType(PaxUser.Type.MEMBER);
	    member.setHeadId(head.getId());
	    member.setRelation(PaxUser.Relation.valueOf(paxMemberDTO.getRelation()));
	    
	    return paxUserRepository.save(member);
	}

	@Override
	public PaxUser updatePaxHead(String uuid, PaxUser request) {
		PaxUser existingHead = paxUserRepository.findByUuid(uuid)
				.orElseThrow(() -> new UsernameNotFoundException("No HEAD found for UUID: " + uuid));
			
		existingHead.setName(request.getName());
		existingHead.setEmail(request.getEmail());
		 if(paxUserRepository.existsByEmail(request.getEmail()))
		    {
		        throw new RuntimeException("Email id already exists, try another.");
		    }
		existingHead.setPhoneNumber(request.getPhoneNumber());
		existingHead.setAddress(request.getAddress());
		existingHead.setCountry(request.getCountry());
		existingHead.setType(PaxUser.Type.HEAD);
		return paxUserRepository.save(existingHead);
	}

	@Override
	public PaxUser updatePaxMember(String uuid, PaxUser paxMember) {
		PaxUser existingMember = paxUserRepository.findByUuidAndType(uuid, Type.MEMBER)
				.orElseThrow(() -> new UsernameNotFoundException("No Member found for UUID: " + uuid));
		existingMember.setName(paxMember.getName());
		existingMember.setEmail(paxMember.getEmail());
		existingMember.setPhoneNumber(paxMember.getPhoneNumber());
		existingMember.setAddress(paxMember.getAddress());
		existingMember.setCountry(paxMember.getCountry());
		existingMember.setType(PaxUser.Type.MEMBER);
		existingMember.setRelation(paxMember.getRelation());
		return paxUserRepository.save(existingMember);

	}

	@Override
	public List<PaxHeadDTO> getAllHead() {
		List<PaxUser> heads = paxUserRepository.findByIsActiveTrue();
		return heads.stream().map(this::convertToHeadDTO).collect(Collectors.toList());
	}

//    @Override
//    public List<PaxMemberDTO> getAllMember() {
//        List<PaxUser> members = paxUserRepository.findAllMembers();
//        Map<Long, String> headIdToNameMap = new HashMap<>();
//
//        // First, fetch heads to create a mapping
//        List<PaxUser> heads = paxUserRepository.findAllHeads();
//        heads.forEach(head -> headIdToNameMap.put(head.getId(), head.getName()));
//
//        return members.stream()
//                .map(member -> convertToMemberDTO(member, headIdToNameMap))
//                .collect(Collectors.toList());
//    }

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
		dto.setId(paxUser.getId());
		dto.setUuid(paxUser.getUuid());
		dto.setPhoneNumber(paxUser.getPhoneNumber());
		dto.setName(paxUser.getName());
		dto.setEmail(paxUser.getEmail());
		return dto;
	}

	private PaxMemberDTO convertToMemberDTO(PaxUser member, Map<Long, String> headIdToNameMap) {
		PaxMemberDTO dto = new PaxMemberDTO();
		dto.setUuid(member.getUuid());
		dto.setName(member.getName());
		dto.setEmail(member.getEmail());
		dto.setRelation(member.getRelation().name());

		// Retrieve head name from the map
		String headName = headIdToNameMap.get(member.getHeadId());
		dto.setHeadName(headName != null ? headName : "Head not found");

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

	public PaxUser getPaxHeadById(String uuid) {
		PaxUser paxHead = paxUserRepository.findByUuidAndType(uuid, Type.HEAD)
				.orElseThrow(() -> new UsernameNotFoundException("No HEAD found for UUID: " + uuid));
		return paxHead;

	}		
	public PaxUser getPaxMemberById(String uuid) {
		PaxUser paxMember = paxUserRepository.findByUuidAndType(uuid, Type.MEMBER)
				.orElseThrow(() -> new UsernameNotFoundException("No Member found for UUID: " + uuid));
		return paxMember;

	}

	@Override
	public List getAllPaxMemberByHeadId(Long id) {   
	    List<PaxUser> allMembers = paxUserRepository.findAllActiveMembersByHeadId(id);
	    
	    if (!allMembers.isEmpty()) {
	        return allMembers;
	    } else {
	        throw new ResourceNotFoundException("No members are found under this head");
	    }
	}



}
