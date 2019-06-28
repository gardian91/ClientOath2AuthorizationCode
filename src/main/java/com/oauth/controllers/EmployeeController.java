package com.oauth.controllers;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.apache.commons.codec.binary.Base64;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.http.converter.support.AllEncompassingFormHttpMessageConverter;
import org.springframework.stereotype.Controller;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.servlet.ModelAndView;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.oauth.model.Employee;

@RestController
public class EmployeeController {

	@RequestMapping(value = "/getEmployees", method = RequestMethod.GET)
	public ModelAndView getEmployeeInfo() {
		
		return new ModelAndView("getEmployees");
		//return new ModelAndView("getEmployees");//////////////////////////////////////////
		
		/*
		 * RestTemplate restTemplate = new RestTemplate(); String url
		 * ="http://localhost:8080/oauth/authorize"; HttpHeaders headers = new
		 * HttpHeaders(); headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
		 * 
		 * MultiValueMap<String, String> map = new LinkedMultiValueMap<String,
		 * String>(); map.add("response_type", "code");
		 * map.add("client_id","javainuse");
		 * map.add("redirect_uri","http://localhost:8090/showEmployees");
		 * map.add("scope", "read");
		 * 
		 * HttpEntity<MultiValueMap<String, String>> request = new
		 * HttpEntity<MultiValueMap<String, String>>(map, headers);
		 * 
		 * ResponseEntity<String> response = restTemplate.postForEntity(url ,
		 * request,String.class);
		 * 
		 * return new ModelAndView("redirect:/showEmployees");
		 */
		
	}

	@RequestMapping(value = "/showEmployees", method = RequestMethod.GET)
	public Employee[] showEmployees(@RequestParam("code") String code) throws JsonProcessingException, IOException {
		ResponseEntity<String> response = null;
		System.out.println("Authorization Ccode------" + code);

		RestTemplate restTemplate = new RestTemplate();

		String credentials = "javainuse:secret";
		String encodedCredentials = new String(Base64.encodeBase64(credentials.getBytes()));

		HttpHeaders headers = new HttpHeaders();
		headers.setAccept(Arrays.asList(MediaType.APPLICATION_JSON));
		headers.add("Authorization", "Basic " + encodedCredentials);

		HttpEntity<String> request = new HttpEntity<String>(headers);

		String access_token_url = "http://localhost:8080/oauth/token";
		access_token_url += "?code=" + code;
		access_token_url += "&grant_type=authorization_code";
		access_token_url += "&redirect_uri=http://localhost:8090/showEmployees";

		response = restTemplate.exchange(access_token_url, HttpMethod.POST, request, String.class);

		System.out.println("Access Token Response ---------" + response.getBody());

		// Get the Access Token From the recieved JSON response
		ObjectMapper mapper = new ObjectMapper();
		JsonNode node = mapper.readTree(response.getBody());
		String token = node.path("access_token").asText();

		String url = "http://localhost:8080/user/getEmployeesList";

		// Use the access token for authentication
		HttpHeaders headers1 = new HttpHeaders();
		headers1.add("Authorization", "Bearer " + token);
		HttpEntity<String> entity = new HttpEntity<>(headers1);

		ResponseEntity<Employee[]> employees = restTemplate.exchange(url, HttpMethod.GET, entity, Employee[].class);
		System.out.println(employees);
		Employee[] employee = employees.getBody();

		return employee;
	}
}