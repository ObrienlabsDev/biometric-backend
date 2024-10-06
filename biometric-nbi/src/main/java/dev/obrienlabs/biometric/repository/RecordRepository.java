package dev.obrienlabs.biometric.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import dev.obrienlabs.biometric.nbi.model.Record;
import java.util.List;

import javax.persistence.EntityManager;

//public interface RecordRepository extends CrudRepository<Record, Long> {
//@Repository
public interface RecordRepository extends JpaRepository<Record, Long>{

	//Record findById(long id);
	  
	  public void persist(Record record);
	  
	  public List<Record> findAllByUserId(Long userId);
	  
	  public EntityManager getEntityManager();
}
