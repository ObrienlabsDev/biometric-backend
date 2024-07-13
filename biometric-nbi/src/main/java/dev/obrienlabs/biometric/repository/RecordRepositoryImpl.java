package dev.obrienlabs.biometric.repository;

import java.util.List;
import java.util.Optional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import dev.obrienlabs.biometric.nbi.model.Record;

@Repository
public class RecordRepositoryImpl {// implements RecordRepository {
	
	  @PersistenceContext
	  private EntityManager entityManager;

	//Record findById(long id);
	  //@Override
	  @Transactional
	  public void persist(Record record) {
		  entityManager.persist(record);
	  }


}
