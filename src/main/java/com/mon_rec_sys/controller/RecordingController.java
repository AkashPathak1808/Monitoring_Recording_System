package com.mon_rec_sys.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.mon_rec_sys.service.RecordingService;

@RestController
@RequestMapping("/recording")
public class RecordingController {

	@Autowired
	private RecordingService screenRecordingService;

	@GetMapping("/start")
	public ResponseEntity<?> startRecording() {
		String name = SecurityContextHolder.getContext().getAuthentication().getName();
		screenRecordingService.startRecording(name);
		return new ResponseEntity<>("Screen recording started for user: " + name, HttpStatus.OK);
	}

	@GetMapping("/stop")
	public ResponseEntity<?> stopRecording() {
		screenRecordingService.stopRecording();
		String filePath = screenRecordingService.getOutputFilePath();
		return new ResponseEntity<>("Screen recording stopped. Video saved to " + filePath, HttpStatus.OK);
	}
}
