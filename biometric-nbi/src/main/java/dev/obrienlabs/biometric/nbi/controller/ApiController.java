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

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import dev.obrienlabs.biometric.nbi.model.Api;
import dev.obrienlabs.biometric.nbi.service.ApplicationServiceLocal;

// change to RestController
@Controller
@RequestMapping("/api")
// http://localhost:8080/nbi/api
public class ApiController {
	
	private final static Logger LOG = Logger.getLogger(ApiController.class.getName());
	
	@Autowired
	ApplicationServiceLocal applicationService;

    private final AtomicLong counter = new AtomicLong();
	private static final CharSequence PASS = "PASS";
    
	  @RequestMapping(method=RequestMethod.GET)
	    public @ResponseBody Api process(@RequestParam(
	    		value="action", required=true, defaultValue="undefined") String action,
	    		 HttpServletRequest request) {
	    	String message = PASS.toString();
	    	message = message +  " remoteAddr: " +
	        	request.getRemoteAddr() + " localAddr: " + 
	    		request.getLocalAddr() + " remoteHost: " +
	        	request.getRemoteHost() + " serverName: " + 
	    		request.getServerName();
	    	// + " requestedSessionId: " + 
	    	//request.getRequestedSessionId() + " X-FORWARDED-FOR: " +
			//request.getHeader("X-FORWARDED-FOR"));
	     	Api api = new Api(counter.incrementAndGet(), message);
	     	
	     	LOG.info(message);
	    	return api;
	    } 
	    
		@GetMapping("/health")
		@RequestMapping("/health")
		public String getHealth() {
			return applicationService.health().toString();
		}
}
