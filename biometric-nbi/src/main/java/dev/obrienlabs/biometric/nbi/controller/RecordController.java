package dev.obrienlabs.biometric.nbi.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import dev.obrienlabs.biometric.repository.RecordRepository;
import java.util.stream.Collectors;

import java.util.List;
import dev.obrienlabs.biometric.nbi.model.Record;
import dev.obrienlabs.biometric.nbi.model.Coordinate;

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
	
    @GetMapping("coordinates/{userId}")
 // select DISTINCT LATITUDE, LONGITUDE from biometric.gps_record r where r.userId like '201611185'
    List<Coordinate> getCoordinates(@PathVariable Long userId) {
            return recordRepository.findAllByUserId(userId)
                            .stream()
                            .map(r -> new Coordinate(r.getLattitude(), r.getLongitude()))
                            .collect(Collectors.toList());
    }
}
