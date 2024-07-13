package dev.obrienlabs.biometric.nbi.model;

import javax.persistence.MappedSuperclass;
import javax.persistence.Version;

@MappedSuperclass
public class DataObject {

    @Version
    private Long version;
 
	public Long getVersion() {
		return version;
	}
}
