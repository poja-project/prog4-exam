package com.example.demo.endpoint.rest.controller;

import com.example.demo.repository.SubmissionRepository;
import com.example.demo.service.SubmissionService;
import com.example.demo.repository.model.Submission;
import java.io.IOException;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/submissions")
@AllArgsConstructor
public class SubmissionController {

    private final SubmissionService submissionService;
    private final SubmissionRepository submissionRepository;

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<Submission> submit(
            @RequestParam String email,
            @RequestParam("image") MultipartFile image) throws IOException {
        var submission = submissionService.submit(email, image);
        return ResponseEntity.status(HttpStatus.CREATED).body(submission);
    }

    @GetMapping
    public ResponseEntity<?> findAll() {
        var submissions = submissionRepository.findAll();
        return ResponseEntity.ok(submissions);
    }
}