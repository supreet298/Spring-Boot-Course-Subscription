package com.project.course.subscription.controller;

import com.project.course.subscription.dto.PaxHeadDTO;
import com.project.course.subscription.dto.PaxMemberPostDTO;
import com.project.course.subscription.model.PaxUser;
import com.project.course.subscription.service.PaxUserService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("api/admin/pax")
public class PaxUserController {

	@Autowired
	private PaxUserService paxUserService;

	@PostMapping("/addHead")
	public ResponseEntity<Object> addPaxHead(@Valid @RequestBody PaxUser paxUser) {
		try {
			PaxUser createdUser = paxUserService.addPaxHead(paxUser);
			return ResponseEntity.status(HttpStatus.CREATED).body(createdUser);
		} catch (Exception e) {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
		}
	}

	@PostMapping("/addMember")
	public ResponseEntity<PaxUser> addPaxMember(@Valid @RequestBody PaxMemberPostDTO paxUser) {
		PaxUser createdUser = paxUserService.addPaxMember(paxUser);
		return ResponseEntity.status(HttpStatus.CREATED).body(createdUser);
	}

	@GetMapping("/head")
	public ResponseEntity<?> getAllPaxHeads() {
		List<PaxHeadDTO> head = paxUserService.getAllHead();
		return ResponseEntity.ok(head);
	}

//	@GetMapping("/member")
//	public ResponseEntity<List<PaxMemberDTO>> getAllPaxMembers() {
//		List<PaxMemberDTO> member = paxUserService.getAllMember();
//		return ResponseEntity.ok(member);
//	}

	@PutMapping("/updateHead/{uuid}")
	public ResponseEntity<?> updateHead(@PathVariable String uuid, @RequestBody PaxUser request) {
		try {
			PaxUser updatedPaxHead = paxUserService.updatePaxHead(uuid, request);
			return new ResponseEntity<>(updatedPaxHead, HttpStatus.OK);
		} catch (Exception e) {
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
		}
	}

	@PutMapping("/UpdateMember/{uuid}")
	public ResponseEntity<?> updateMember(@PathVariable String uuid, @RequestBody PaxUser request) {
		try {
			PaxUser updatedPaxMember = paxUserService.updatePaxMember(uuid, request);
			return new ResponseEntity<>(updatedPaxMember, HttpStatus.OK);
		} catch (Exception e) {
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
		}
	}

	@DeleteMapping("/dropPaxUser/{uuid}")
	public ResponseEntity<?> dropPaxUser(@PathVariable String uuid) {
		try {
			PaxUser dropPaxHead = paxUserService.dropPaxUser(uuid);
			return new ResponseEntity<>(dropPaxHead.getName() + " Deleted Successfully", HttpStatus.OK);
		} catch (Exception e) {
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
		}
	}

	@GetMapping("/{uuid}")
	public ResponseEntity<?> getPaxHeadById(@PathVariable String uuid) {
		try {
			PaxUser paxHead = paxUserService.getPaxHeadById(uuid);
			return new ResponseEntity<>(paxHead, HttpStatus.OK);
		} catch (Exception e) {
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
		}
	}

	@GetMapping("/getMember/{uuid}")
	public ResponseEntity<?> getPaxMemberById(@PathVariable String uuid) {
		try {
			PaxUser paxHead = paxUserService.getPaxMemberById(uuid);
			return new ResponseEntity<>(paxHead, HttpStatus.OK);
		} catch (Exception e) {
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
		}
	}

	@PostMapping("/addPaxMembers/{uuid}")
	public ResponseEntity<?> addPaxMembers(@PathVariable String uuid, @RequestBody PaxMemberPostDTO paxMember) {
		try {
			PaxUser addpaxMember = paxUserService.addPaxMembers(uuid, paxMember);
			return ResponseEntity.status(HttpStatus.CREATED).body(addpaxMember);
		} catch (Exception e) {
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
		}
	}

	@GetMapping("/getMemberByHeadId/{id}")
	public ResponseEntity<?> getPaxAllMemberByHadaId(@PathVariable Long id) {
		try {
			List<PaxMemberDTO> AllMember = paxUserService.getAllPaxMemberByHeadId(id);
			return new ResponseEntity<>(AllMember, HttpStatus.OK);
		} catch (Exception e) {
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
		}
	}
}
