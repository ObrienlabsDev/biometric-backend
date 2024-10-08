// Copyright 2023 Google LLC
// 
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
// 
//     https://www.apache.org/licenses/LICENSE-2.0
// 
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.
package dev.obrienlabs.biometric.nbi.controller;

import java.io.PrintWriter;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;
import java.util.function.Consumer;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import java.util.stream.Stream;


// spring boot 2.x
import javax.servlet.http.HttpServletRequest;
// spring boot 3+
//import jakarta.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import dev.obrienlabs.biometric.nbi.model.Api;
import dev.obrienlabs.biometric.nbi.model.Record;
import dev.obrienlabs.biometric.nbi.service.ApplicationServiceLocal;

// change to RestController
@Controller
@RequestMapping("/api")
// http://localhost:8080/nbi/api
public class ApiController {
	
	private final static Logger LOG = Logger.getLogger(ApiController.class.getName());
	
	@Autowired
	ApplicationServiceLocal applicationService;
	

	
    private static AtomicLong nextSessionId = new AtomicLong(1);
    //private Map<Long, AtomicLong> lastTimestampMap = new ConcurrentHashMap<>();
    //private Map<Long, AtomicLong> nextReadingSequenceIdMap = new ConcurrentHashMap<>();
    private static AtomicLong nextReadingSequenceId = new AtomicLong(1);

    private final AtomicLong counter = new AtomicLong();
	private static final CharSequence PASS = ""; // OK
	
    public static final Record fakeRecord;
    static {
    	fakeRecord = new Record();
    	fakeRecord.setHeartRate1(0);
    	fakeRecord.setHeartRate2(0);
    	fakeRecord.setLongitude(0.0);
    	fakeRecord.setLattitude(0.0);
    	fakeRecord.setSendSeq(100L);
    }
    
	// http://127.0.0.1:8080/nbi/swagger-ui.html#/api-controller/processUsingGET
	@RequestMapping(method=RequestMethod.GET)
	public @ResponseBody Api process(
			@RequestParam(value="action", required=true, defaultValue="u2") String action2,
			@RequestParam(value="lg", required=true, defaultValue="0") String lg,
			@RequestParam(value="lt", required=true, defaultValue="0") String lt,
			@RequestParam(value="ac", required=true, defaultValue="0") String ac,
			@RequestParam(value="al", required=true, defaultValue="0") String al,
			@RequestParam(value="ts", required=true, defaultValue="0") String ts,
			@RequestParam(value="p", required=true, defaultValue="0") String p,
			@RequestParam(value="te", required=true, defaultValue="0") String te,
			@RequestParam(value="be", required=true, defaultValue="0") String be,
			@RequestParam(value="s", required=true, defaultValue="0") String s,
			@RequestParam(value="pr", required=true, defaultValue="0") String pr,
			@RequestParam(value="hr1", required=true, defaultValue="0") String hr1,
			@RequestParam(value="hr2", required=true, defaultValue="0") String hr2,
			@RequestParam(value="hrd1", required=true, defaultValue="0") String hrd1,
			@RequestParam(value="hrd2", required=true, defaultValue="0") String hrd2,
			@RequestParam(value="grx", required=true, defaultValue="0") String grx,
			@RequestParam(value="gry", required=true, defaultValue="0") String gry,
			@RequestParam(value="grz", required=true, defaultValue="0") String grz,
			@RequestParam(value="arx", required=true, defaultValue="0") String arx,
			@RequestParam(value="ary", required=true, defaultValue="0") String ary,
			@RequestParam(value="arz", required=true, defaultValue="0") String arz,
			@RequestParam(value="gsx", required=true, defaultValue="0") String gsx,
			@RequestParam(value="gsy", required=true, defaultValue="0") String gsy,
			@RequestParam(value="gsz", required=true, defaultValue="0") String gsz,
			@RequestParam(value="li", required=true, defaultValue="0") String li,
			@RequestParam(value="lax", required=true, defaultValue="0") String lax,
			@RequestParam(value="lay", required=true, defaultValue="0") String lay,
			@RequestParam(value="laz", required=true, defaultValue="0") String laz,
			@RequestParam(value="px", required=true, defaultValue="0") String px,
			@RequestParam(value="hu", required=true, defaultValue="0") String hu,
			@RequestParam(value="rvx", required=true, defaultValue="0") String rvx,
			@RequestParam(value="rvy", required=true, defaultValue="0") String rvy,
			@RequestParam(value="rvz", required=true, defaultValue="0") String rvz,
			@RequestParam(value="mfx", required=true, defaultValue="0") String mfx,
			@RequestParam(value="mfy", required=true, defaultValue="0") String mfy,
			@RequestParam(value="mfz", required=true, defaultValue="0") String mfz,
			@RequestParam(value="up", required=true, defaultValue="0") String up,	
			@RequestParam(value="u", required=true, defaultValue="0") String user,	
	    		 HttpServletRequest request) {
			Record aRecord = processGpsPrivate(request);
	    	String message = PASS.toString();

		message = message + ":" + aRecord.getGeohash() + ":" + aRecord;// xmlBuffer.toString());
                //message = message +  " remoteAddr: " +
                //      request.getRemoteAddr() + " localAddr: " + 
                //      request.getLocalAddr() + " remoteHost: " +
                //      request.getRemoteHost() + " serverName: " + 
                //      request.getServerName() + " " + aRecord;

	    	// + " requestedSessionId: " + 
	    	//request.getRequestedSessionId() + " X-FORWARDED-FOR: " +
			//request.getHeader("X-FORWARDED-FOR"));
	     	Api api = new Api(counter.incrementAndGet(), message);
	     	
	     	LOG.info(api.toString());
	     	// used by RE in BMViewController.m
	     	LOG.info("message: " + message);
	     	// return
	     	// data: 126: OK:f21czytg7206:Record(null,20240505,18,1662449,null,null,45.343881,-75.94045,292,104.69228,1714950768577,1714950768598,null)
	    	// need
		// OK:....
		return api;
	    } 
	
	
	private Record processGpsPrivate(HttpServletRequest request) {

		String user = request.getParameter("u");

		StringBuffer xmlBuffer = new StringBuffer();
		xmlBuffer.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>");


			String lg = request.getParameter("lg");
			String lt = request.getParameter("lt");
			String ac = request.getParameter("ac");
			String al = request.getParameter("al");
			String ts = request.getParameter("ts");
			String pressure = request.getParameter("p");
			String temp = request.getParameter("te");
			String bearing = request.getParameter("be");
			String speed = request.getParameter("s");
			String provider = request.getParameter("pr");
			String heartRate1 = request.getParameter("hr1");
			String heartRate2 = request.getParameter("hr2");
			String heartRateDevice1 = request.getParameter("hrd1");
			String heartRateDevice2 = request.getParameter("hrd2");

			String gravityX = request.getParameter("grx");
			String gravityY = request.getParameter("gry");
			String gravityZ = request.getParameter("grz");
			String accelerometerX = request.getParameter("arx");
			String accelerometerY = request.getParameter("ary");
			String accelerometerZ = request.getParameter("arz");
			String gyroscopeX = request.getParameter("gsx");
			String gyroscopeY = request.getParameter("gsy");
			String gyroscopeZ = request.getParameter("gsz");
			String light = request.getParameter("li");
			String linearAccelerationX = request.getParameter("lax");
			String linearAccelerationY = request.getParameter("lay");
			String linearAccelerationZ = request.getParameter("laz");
			String proximity = request.getParameter("px");
			String humidity = request.getParameter("hu");
			String rotationVectorX = request.getParameter("rvx");
			String rotationVectorY = request.getParameter("rvy");
			String rotationVectorZ = request.getParameter("rvz");

			String magFieldX = request.getParameter("mfx");
			String magFieldY = request.getParameter("mfy");
			String magFieldZ = request.getParameter("mfz");

			String sendSeq = request.getParameter("up");

			Record aRecord = new Record();
			if (null != ts && !ts.isEmpty()) {
				aRecord.setTsStart(Long.valueOf(ts));
			}
			aRecord.setTsStop(System.currentTimeMillis());
			if (null != lt && !lt.isEmpty()) {
				aRecord.setLattitude(new Double(lt));
			}
			if (null != lg && !lg.isEmpty()) {
				aRecord.setLongitude(new Double(lg));
			}

			aRecord.setUserId(new Long(user));
			if (null != ac && !ac.isEmpty()) {
				aRecord.setAccuracy(new Double(ac));
			}
			if (null != al && !al.isEmpty()) {
				aRecord.setAltitude(new Double(al));
			}
			if (null != pressure && !pressure.isEmpty()) {
				aRecord.setPressure(new Double(pressure));
			}
			if (null != temp && !temp.isEmpty()) {
				aRecord.setTemp(new Double(temp));
			}
			if (null != bearing && !bearing.isEmpty()) {
				aRecord.setBearing(new Integer(bearing));
			}
			if (null != speed && !speed.isEmpty()) {
				aRecord.setSpeed(new Double(speed));
			}
			if (null != provider && !provider.isEmpty()) {
				aRecord.setProvider(provider);
			}
			if (null != heartRate1 && !heartRate1.isEmpty()) {
				aRecord.setHeartRate1(new Integer(heartRate1));
			}
			if (null != heartRate2 && !heartRate2.isEmpty()) {
				aRecord.setHeartRate2(new Integer(heartRate2));
			}
			if (null != heartRateDevice1 && !heartRateDevice1.isEmpty()) {
				aRecord.setHeartRateDevice1(heartRateDevice1);
			}
			if (null != heartRateDevice2 && !heartRateDevice2.isEmpty()) {
				aRecord.setHeartRateDevice2(heartRateDevice2);
			}

			if (null != gravityX && !gravityX.isEmpty()) {
				aRecord.setGravityX(gravityX);
			}
			if (null != gravityY && !gravityY.isEmpty()) {
				aRecord.setGravityY(gravityY);
			}
			if (null != gravityZ && !gravityZ.isEmpty()) {
				aRecord.setGravityZ(gravityZ);
			}
			if (null != accelerometerX && !accelerometerX.isEmpty()) {
				aRecord.setAccelerometerX(accelerometerX);
			}
			if (null != accelerometerY && !accelerometerY.isEmpty()) {
				aRecord.setAccelerometerY(accelerometerY);
			}
			if (null != accelerometerZ && !accelerometerZ.isEmpty()) {
				aRecord.setAccelerometerZ(accelerometerZ);
			}
			if (null != gyroscopeX && !gyroscopeX.isEmpty()) {
				aRecord.setGyroscopeX(gyroscopeX);
			}
			if (null != gyroscopeY && !gyroscopeY.isEmpty()) {
				aRecord.setGyroscopeY(gyroscopeY);
			}
			if (null != gyroscopeZ && !gyroscopeZ.isEmpty()) {
				aRecord.setGyroscopeZ(gyroscopeZ);
			}
			if (null != light && !light.isEmpty()) {
				aRecord.setLight(light);
			}
			if (null != linearAccelerationX && !linearAccelerationX.isEmpty()) {
				aRecord.setLinearAccelerationX(linearAccelerationX);
			}
			if (null != linearAccelerationY && !linearAccelerationY.isEmpty()) {
				aRecord.setLinearAccelerationY(linearAccelerationY);
			}
			if (null != linearAccelerationZ && !linearAccelerationZ.isEmpty()) {
				aRecord.setLinearAccelerationZ(linearAccelerationZ);
			}
			if (null != proximity && !proximity.isEmpty()) {
				aRecord.setProximity(proximity);
			}
			if (null != humidity && !humidity.isEmpty()) {
				aRecord.setHumidity(humidity);
			}
			if (null != rotationVectorX && !rotationVectorX.isEmpty()) {
				aRecord.setRotationVectorX(rotationVectorX);
			}
			if (null != rotationVectorY && !rotationVectorY.isEmpty()) {
				aRecord.setRotationVectorY(rotationVectorY);
			}
			if (null != rotationVectorZ && !rotationVectorZ.isEmpty()) {
				aRecord.setRotationVectorZ(rotationVectorZ);
			}
			if (null != magFieldX && !magFieldX.isEmpty()) {
				aRecord.setTeslaX(magFieldX);
			}
			if (null != magFieldY && !magFieldY.isEmpty()) {
				aRecord.setTeslaY(magFieldY);
			}
			if (null != magFieldZ && !magFieldZ.isEmpty()) {
				aRecord.setTeslaZ(magFieldZ);
			}
			if (null != sendSeq && !sendSeq.isEmpty()) {
				aRecord.setSendSeq(new Long(sendSeq));
			}

			aRecord.setRecvSeq(counter.get());//nextReadingSequenceId.addAndGet(1));

			String geohash = applicationService.geohash(Double.valueOf(lt), Double.valueOf(lg));
			System.out.println(geohash);
			aRecord.setGeohash(geohash);
			//String status = service.persist(aRecord);
			//out.println(status + ":" + geohash + ":" + aRecord);// xmlBuffer.toString());
			
			//normalize(aRecord);
		//}
			return aRecord;

	}
	    
	// curl -X GET "http://127.0.0.1:8888/nbi/api/getGps?ac=0&action=u2&al=0&arx=0&ary=0&arz=0&be=0&grx=0&gry=0&grz=0&gsx=0&gsy=0&gsz=0&hr1=0&hr2=0&hrd1=0&hrd2=0&hu=0&lax=0&lay=0&laz=0&lg=0&li=0&lt=0&mfx=0&mfy=0&mfz=0&p=0&pr=0&px=0&rvx=0&rvy=0&rvz=0&s=0&te=0&ts=0&u=0&up=0" -H "accept: */*"
	
	@GetMapping("/getGps")
	@RequestMapping("/getGps")
	public @ResponseBody String getGps(@RequestParam(value="action", required=true, defaultValue="u2") String action2,
			@RequestParam(value="lg", required=true, defaultValue="0") String lg,
			@RequestParam(value="lt", required=true, defaultValue="0") String lt,
			@RequestParam(value="ac", required=true, defaultValue="0") String ac,
			@RequestParam(value="al", required=true, defaultValue="0") String al,
			@RequestParam(value="ts", required=true, defaultValue="0") String ts,
			@RequestParam(value="p", required=true, defaultValue="0") String p,
			@RequestParam(value="te", required=true, defaultValue="0") String te,
			@RequestParam(value="be", required=true, defaultValue="0") String be,
			@RequestParam(value="s", required=true, defaultValue="0") String s,
			@RequestParam(value="pr", required=true, defaultValue="0") String pr,
			@RequestParam(value="hr1", required=true, defaultValue="0") String hr1,
			@RequestParam(value="hr2", required=true, defaultValue="0") String hr2,
			@RequestParam(value="hrd1", required=true, defaultValue="0") String hrd1,
			@RequestParam(value="hrd2", required=true, defaultValue="0") String hrd2,
			@RequestParam(value="grx", required=true, defaultValue="0") String grx,
			@RequestParam(value="gry", required=true, defaultValue="0") String gry,
			@RequestParam(value="grz", required=true, defaultValue="0") String grz,
			@RequestParam(value="arx", required=true, defaultValue="0") String arx,
			@RequestParam(value="ary", required=true, defaultValue="0") String ary,
			@RequestParam(value="arz", required=true, defaultValue="0") String arz,
			@RequestParam(value="gsx", required=true, defaultValue="0") String gsx,
			@RequestParam(value="gsy", required=true, defaultValue="0") String gsy,
			@RequestParam(value="gsz", required=true, defaultValue="0") String gsz,
			@RequestParam(value="li", required=true, defaultValue="0") String li,
			@RequestParam(value="lax", required=true, defaultValue="0") String lax,
			@RequestParam(value="lay", required=true, defaultValue="0") String lay,
			@RequestParam(value="laz", required=true, defaultValue="0") String laz,
			@RequestParam(value="px", required=true, defaultValue="0") String px,
			@RequestParam(value="hu", required=true, defaultValue="0") String hu,
			@RequestParam(value="rvx", required=true, defaultValue="0") String rvx,
			@RequestParam(value="rvy", required=true, defaultValue="0") String rvy,
			@RequestParam(value="rvz", required=true, defaultValue="0") String rvz,
			@RequestParam(value="mfx", required=true, defaultValue="0") String mfx,
			@RequestParam(value="mfy", required=true, defaultValue="0") String mfy,
			@RequestParam(value="mfz", required=true, defaultValue="0") String mfz,
			@RequestParam(value="up", required=true, defaultValue="0") String up,	
			@RequestParam(value="u", required=true, defaultValue="0") String user,	
	    		 HttpServletRequest request) {
			Record aRecord = processGpsPrivate(request);
			
		return applicationService.getGps(aRecord).toString();
	}
	
	
    private Record getLatestRecordPrivate(String user) {
    	Record record = applicationService.latest(user);//null;
        // send fake record;
        if(record == null ) {
        	record = fakeRecord;
        }	
        return record;
    }
    
	@GetMapping(value = "/latest", produces = MediaType.APPLICATION_JSON_VALUE)
	@RequestMapping(value = "/latest", produces = MediaType.APPLICATION_JSON_VALUE)
    public @ResponseBody String latest(
    		@RequestParam(value="user", required=true, defaultValue="") String user,
    		HttpServletRequest request) {
        //String user = request.getParameter("u");        
        StringBuffer xmlBuffer = new StringBuffer();
        Record record = getLatestRecordPrivate(user);
        	xmlBuffer.append("{ \"tsStop\" : ").append(record.getTsStop()).append(",");
        	xmlBuffer.append(" \"tsStart\" : ").append(record.getTsStart()).append(",");
        	xmlBuffer.append(" \"sendSeq\" : ").append(record.getSendSeq()).append(",");
        	xmlBuffer.append(" \"recvSeq\" : ").append(record.getRecvSeq()).append(",");
        	xmlBuffer.append(" \"heartRate1\" : ").append(record.getHeartRate1()).append(",");
        	xmlBuffer.append(" \"heartRate2\" : ").append(record.getHeartRate2()).append(",");
        	xmlBuffer.append(" \"longitude\" : ").append(record.getLongitude()).append(",");
        	xmlBuffer.append(" \"lattitude\" : ").append(record.getLattitude()).append("");
        	xmlBuffer.append("}");
        	LOG.info(xmlBuffer.toString());
        return xmlBuffer.toString(); 
    }
		
	@GetMapping(value = "/activeId", produces = MediaType.APPLICATION_JSON_VALUE)
	@RequestMapping(value = "/activeId", produces = MediaType.APPLICATION_JSON_VALUE)
    public @ResponseBody String activeId(HttpServletRequest request) {
        String user = request.getParameter("u");        
        StringBuffer xmlBuffer = new StringBuffer();
        // check local cache first
        String activeId = applicationService.activeId();//null;
        // make sure the content type is application/json on the response header
        xmlBuffer.append("{\"id\" : ").append(activeId).append(" }");
        //xmlBuffer.append(activeId);
        return xmlBuffer.toString(); 
    }
	
	@GetMapping("/health")
	@RequestMapping("/health")
	public String getHealth() {
		return applicationService.health().toString();
	}
}
