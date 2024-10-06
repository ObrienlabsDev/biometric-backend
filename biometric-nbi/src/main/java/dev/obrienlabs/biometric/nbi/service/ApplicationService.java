package dev.obrienlabs.biometric.nbi.service;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Logger;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import javax.persistence.metamodel.SingularAttribute;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
//import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import dev.obrienlabs.biometric.nbi.controller.ApiController;
import dev.obrienlabs.biometric.nbi.model.Record;
import dev.obrienlabs.biometric.repository.RecordRepository;
import dev.obrienlabs.biometric.repository.RecordRepositoryImpl;

@Service
public class ApplicationService implements ApplicationServiceLocal {
	
	private final static Logger LOG = Logger.getLogger(ApplicationService.class.getName());

	@Autowired
	// add following to the Application class @EnableJpaRepositories("dev.obrienlabs.biometric.repository")
	private RecordRepositoryImpl recordRepository;
	
    private Map<Long,Record> userRecordMap = new ConcurrentHashMap<>();
    private Long currentUser = 0L;
    
	private static final GeoHash _geohash = new GeoHash();
	
	@Transactional(propagation = Propagation.NEVER)
	public String geohash(double lat, double lon) {
		return _geohash.encode(lat, lon);
	}
	
	@Override
	public String getGps(Record aRecord
/*			String action2,
			String lg,
			String lt,
			String ac,
			String al,
			String ts,
			String p,
			String te,
			String be,
			String s,
			String pr,
			String hr1,
			String hr2,
			String hrd1,
			String hrd2,
			String grx,
			String gry,
			String grz,
			String arx,
			String ary,
			String arz,
			String gsx,
			String gsy,
			String gsz,
			String li,
			String lax,
			String lay,
			String laz,
			String px,
			String hu,
			String rvx,
			String rvy,
			String rvz,
			String mfx,
			String mfy,
			String mfz,
			String up,	
			String user	*/

			) {
		
		// persist - id is auto generatedc
		//recordRepository.save(aRecord, null);
		recordRepository.persist(aRecord);
		// cache value
		userRecordMap.put(aRecord.getUserId(), aRecord);
		currentUser = aRecord.getUserId();
		
		return aRecord.toString();
	}
	
	
    private Record getLatestRecordPrivate(String user) {
    	Record record = null;
        // check cache
        if(currentUser > 0) {
        	record = userRecordMap.get(Long.valueOf(user));
        	if(null != record) { 
        	    LOG.info("latest from cache");//: {} {}", user, record);
        	}
        }
        return record;
    }
    
    @Override
    public String activeId() {
        // check local cache first
        //String activeId = null;
        if(currentUser > 0) {
        	return currentUser.toString();
        }
    	Long aLong = null;
		CriteriaBuilder cb = recordRepository.getEntityManager().getCriteriaBuilder();
	    CriteriaQuery<Long> query = cb.createQuery(Long.class);
	    Root<Record> target = query.from(Record.class);
	    // workaround for http://stackoverflow.com/questions/16348354/how-do-i-write-a-max-query-with-a-where-clause-in-jpa-2-0
	    SingularAttribute<? super Record, Long> userIdAttribute = recordRepository.getEntityManager().getMetamodel()
	    		.entity(Record.class).getSingularAttribute("userId", Long.class);
	    SingularAttribute<? super Record, Long> tsStopAttribute = recordRepository.getEntityManager().getMetamodel()
	    		.entity(Record.class).getSingularAttribute("tsStop", Long.class);
	    query.select(target.get(userIdAttribute));
	    query.orderBy(
	    		cb.desc(target.get(tsStopAttribute))); // deprecated in mysql 5.7+
	    //query.distinct(true); // fix for mysql 5.7
	    TypedQuery<Long> typedQuery = recordRepository.getEntityManager().createQuery(query);
		typedQuery.setMaxResults(1);
	    // see http://bugs.eclipse.org/303205
	    try {
	    	aLong = typedQuery.getSingleResult();
	    } catch (NoResultException nre) {
	    	nre.printStackTrace();
	    }
	    System.out.println("id: " + aLong);
		return String.valueOf(aLong.longValue());    	
    }
    
    private void latestPrivate(String user, CriteriaQuery<Record> query, CriteriaBuilder cb, Root<Record> target) {
    	// special case null or 0 means any user id
    	if(null != user && !user.equalsIgnoreCase("0")) {
    		query.where(
    				cb.equal(target.get("userId"), user));
    	}
    }
    
    private TypedQuery<Record> getTypedCriteriaQuery(EntityManager entityManager, String user) {
 		CriteriaBuilder cb = entityManager.getCriteriaBuilder();
		CriteriaQuery<Record> query = cb.createQuery(Record.class);
		Root<Record> target = query.from(Record.class);
		// workaround for http://stackoverflow.com/questions/16348354/how-do-i-write-a-max-query-with-a-where-clause-in-jpa-2-0
		SingularAttribute<? super Record, Long> anAttribute = entityManager.getMetamodel()
    		.entity(Record.class).getSingularAttribute("tsStop", Long.class);
		query.orderBy(
    		cb.desc(target.get(anAttribute)));
		latestPrivate(user, query, cb, target);
		TypedQuery<Record> typedQuery = entityManager.createQuery(query);
		return typedQuery;	
    }
    
    @Override
    public Record latest(String user) {
    	Record result = getLatestRecordPrivate(user);
    	if(null == result) {
    		try {
    			TypedQuery<Record> typedQuery = getTypedCriteriaQuery(recordRepository.getEntityManager(), user);
    			typedQuery.setMaxResults(1);
    			// see http://bugs.eclipse.org/303205
    			try {
    				result = (Record)typedQuery.getSingleResult();
    			} catch (NoResultException nre) {
    				System.out.println("No result for " + user);
    			} finally {	    	   				
    			}
    		} finally {
    		}
    		System.out.println("latest: " + user + ": " + result);
    		//System.out.println(server);
    	}
		return result;    	   	
    }

    @Override
    public Record latest() {
    	return latest(null);
    }
	@Override
	public String health() {
		return "OK";
	}

	@Override
	public String forward() {
		// TODO Auto-generated method stub
		return "OK";
	}

}
