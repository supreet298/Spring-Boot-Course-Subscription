package com.project.course.subscription.controller;

import com.project.course.subscription.dto.PaxHeadDTO;
import com.project.course.subscription.dto.PaxMemberDTO;
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
    public ResponseEntity<PaxUser> addPaxHead(@Valid @RequestBody PaxUser paxUser) {
        PaxUser createdUser = paxUserService.addPaxHead(paxUser);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdUser);
    }

    @PostMapping("/addMember")
    public ResponseEntity<PaxUser> addPaxMember(@Valid @RequestBody PaxUser paxUser) {
        PaxUser createdUser = paxUserService.addPaxMember(paxUser);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdUser);
    }

    @GetMapping("/head")
    public ResponseEntity<List<PaxHeadDTO>> getAllPaxHeads() {
        List<PaxHeadDTO> head = paxUserService.getAllHead();
        return ResponseEntity.ok(head);
    }

    @GetMapping("/member")
    public ResponseEntity<List<PaxMemberDTO>> getAllPaxMembers() {
        List<PaxMemberDTO> member = paxUserService.getAllMember();
        return ResponseEntity.ok(member);
    }
}