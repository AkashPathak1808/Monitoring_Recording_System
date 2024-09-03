package com.mon_rec_sys.controller;

import org.springframework.beans.factory.annotation.Autowired;
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
	public String startRecording() {
		String name = SecurityContextHolder.getContext().getAuthentication().getName();
		screenRecordingService.startRecording(name);
		return "Screen recording started for user: " + name;
	}

	@GetMapping("/stop")
	public String stopRecording() {
		screenRecordingService.stopRecording();
		String filePath = screenRecordingService.getOutputFilePath();
		return "Screen recording stopped. Video saved to " + filePath;
	}
}
