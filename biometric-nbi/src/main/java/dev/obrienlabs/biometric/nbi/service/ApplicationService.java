package dev.obrienlabs.biometric.nbi.service;

//import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

@Service
public class ApplicationService implements ApplicationServiceLocal {

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
