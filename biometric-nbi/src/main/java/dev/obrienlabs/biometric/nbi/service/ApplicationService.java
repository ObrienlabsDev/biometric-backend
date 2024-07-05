package dev.obrienlabs.biometric.nbi.service;

//import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import dev.obrienlabs.biometric.nbi.model.Record;

@Service
public class ApplicationService implements ApplicationServiceLocal {


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
		return aRecord.toString();
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
