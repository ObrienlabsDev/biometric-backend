package dev.obrienlabs.biometric.repository;

import org.springframework.data.repository.CrudRepository;

public interface RecordRepository extends CrudRepository<Record, Long> {

	Record findById(long id);
}
