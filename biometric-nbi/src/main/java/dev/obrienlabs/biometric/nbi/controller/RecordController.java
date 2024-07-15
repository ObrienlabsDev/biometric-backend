package dev.obrienlabs.biometric.nbi.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import dev.obrienlabs.biometric.repository.RecordRepository;
import dev.obrienlabs.biometric.repository.RecordRepositoryImpl;

import java.util.List;
import dev.obrienlabs.biometric.nbi.model.Record;

@RestController
public class RecordController {

	private final RecordRepository recordRepository;
	
	RecordController(RecordRepository recordRepository) {
		this.recordRepository = recordRepository;
	}
	
	@GetMapping("records/{userId}")
	List<Record> getRecords(Long userId) {
		return recordRepository.findAllByUserId(userId);
	}
}
