package dev.obrienlabs.biometric.nbi.service;

import dev.obrienlabs.biometric.nbi.model.Record;

public interface ApplicationServiceLocal {
	String getGps(Record aRecord);
	String health();
	String forward();
	String geohash(double lat, double lon);
}
	
