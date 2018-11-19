package com.qa.cv.test;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.io.File;
import java.io.FileInputStream;

import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import com.qa.cv.CvManagerApplication;
import com.qa.cv.ReportFile;
import com.qa.cv.model.Person;
import com.qa.cv.repo.CVRepository;
import com.qa.cv.repo.PersonRepository;
import com.relevantcodes.extentreports.LogStatus;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = {CvManagerApplication.class})
@AutoConfigureMockMvc
public class IntegrationTests {

	@Autowired
	private MockMvc mvc;
	
	@Autowired
	private PersonRepository personRepo;
	
	@Autowired
	private CVRepository cvRepo;
	
	@BeforeClass
	public static void beforeClass() {
		ReportFile.createReport();
	}
	
	@Before
	public void startup() {
		personRepo.deleteAll();
		cvRepo.deleteAll();
	}
	
	@After
	public void tearDown() {
		ReportFile.endTest();
	}
	
	@AfterClass
	public static void afterClass() {
		ReportFile.endReport();
	}
	
	//Add person to database
	@Test @Ignore
	public void addPersonToDatabase() throws Exception {
		
		ReportFile.createTest("Test: Add person to database");
		
		Person person = new Person("hsimpson@springfieldnuclearpowerplant.com", "Homer", "Trainee", "Donut");		
		ReportFile.logStatusTest(LogStatus.INFO, "Test entity created");
	
		ReportFile.logStatusTest(LogStatus.INFO, "Post request sent");
		
		MvcResult result = mvc.perform(post("/api/people")
							  .contentType(MediaType.APPLICATION_JSON)
							  .content(person.toString()))
							  .andExpect(status().isOk())
							  .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
							  .andExpect(jsonPath("$.name", is("Homer")))
							  .andReturn();
		
		if(result.getResponse().getStatus() == 200) {			
			ReportFile.logStatusTest(LogStatus.PASS, "HTTP status code " + result.getResponse().getStatus() + " returned");
		}
		else ReportFile.logStatusTest(LogStatus.FAIL, "HTTP status code " + result.getResponse().getStatus() + " returned");
		
		if(result.getResponse().getContentType().contains("json")) {			
			ReportFile.logStatusTest(LogStatus.PASS, "Content returned as JSON object");
		}
		else ReportFile.logStatusTest(LogStatus.FAIL, "Incorrect content type returned");
		
		if(result.getResponse().getContentAsString().contains(person.toStringNoPassword().substring(1, person.toStringNoPassword().length()-1))) {			
			ReportFile.logStatusTest(LogStatus.PASS, "Expected content found in response body");
		}
		else ReportFile.logStatusTest(LogStatus.FAIL, "Incorrect content found in response body");
	}
	
	//Get person from database by ID
	@Test @Ignore
	public void getPersonFromDatabase() throws Exception {
		
		ReportFile.createTest("Test: Retrieve person from database");
		
		Person person = new Person("benderbrodriguez@planetexpress.com", "Bender", "Trainer", "001010110");
		personRepo.save(person);
		String id = person.getId();	
		ReportFile.logStatusTest(LogStatus.INFO, "Test entity created and added to database");
		
		ReportFile.logStatusTest(LogStatus.INFO, "Get request sent");
		
		MvcResult result = mvc.perform(get("/api/people/" + id)
							  .contentType(MediaType.APPLICATION_JSON))
							  .andExpect(status().isOk())
							  .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
							  .andExpect(jsonPath("$.name", is("Bender")))
							  .andReturn();
		
		if(result.getResponse().getStatus() == 200) {			
			ReportFile.logStatusTest(LogStatus.PASS, "HTTP status code " + result.getResponse().getStatus() + " returned");
		}
		else ReportFile.logStatusTest(LogStatus.FAIL, "HTTP status code " + result.getResponse().getStatus() + " returned");
		
		if(result.getResponse().getContentType().contains("json")) {			
			ReportFile.logStatusTest(LogStatus.PASS, "Content returned as JSON object");
		}
		else ReportFile.logStatusTest(LogStatus.FAIL, "Incorrect content type returned");
		
		if(result.getResponse().getContentAsString().contains(person.toStringNoPassword().substring(1, person.toStringNoPassword().length()-1))) {			
			ReportFile.logStatusTest(LogStatus.PASS, "Expected content found in response body");
		}
		else ReportFile.logStatusTest(LogStatus.FAIL, "Incorrect content found in response body");
	}
	
	//Get all people from database
	@Test @Ignore
	public void getAllPeopleFromDatabase() throws Exception {
		
		ReportFile.createTest("Test: Retrieve all persons from database");
		
		Person person1 = new Person("turangaleela@cookievilleminimumsecurityorphanarium.com", "Leela", "Trainer", "1i");
		Person person2 = new Person("proffarnsworth@planetexpress.com", "Hubert", "Professor", "Good news everyone!");

		personRepo.save(person1);
		ReportFile.logStatusTest(LogStatus.INFO, "Test entity 1 created and added to database");
		personRepo.save(person2);	
		ReportFile.logStatusTest(LogStatus.INFO, "Test entity 2 created and added to database");

		ReportFile.logStatusTest(LogStatus.INFO, "Get request sent");
		
		MvcResult result = mvc.perform(get("/api/people")
							  .contentType(MediaType.APPLICATION_JSON))
							  .andExpect(status().isOk())
							  .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
							  .andReturn();
		
		if(result.getResponse().getStatus() == 200) {			
			ReportFile.logStatusTest(LogStatus.PASS, "HTTP status code " + result.getResponse().getStatus() + " returned");
		}
		else ReportFile.logStatusTest(LogStatus.FAIL, "HTTP status code " + result.getResponse().getStatus() + " returned");
		
		if(result.getResponse().getContentType().contains("json")) {			
			ReportFile.logStatusTest(LogStatus.PASS, "Content returned as JSON object");
		}
		else ReportFile.logStatusTest(LogStatus.FAIL, "Incorrect content type returned");
		
		if(result.getResponse().getContentAsString().contains(person1.toStringNoPassword().substring(1, person1.toStringNoPassword().length()-1)) 
		&& result.getResponse().getContentAsString().contains(person2.toStringNoPassword().substring(1, person2.toStringNoPassword().length()-1))) {			
			ReportFile.logStatusTest(LogStatus.PASS, "Expected content found in response body");
		}
		else ReportFile.logStatusTest(LogStatus.FAIL, "Incorrect content found in response body");
	}
	
	//Update person in database by ID
	@Test @Ignore
	public void editOwnerInDatabase() throws Exception {
		
		ReportFile.createTest("Test: Update person in database");
		
		Person person = new Person("supergeniusrick@universec137.com", "Rick", "Training Manager", "wubbalubbadubdub");
		personRepo.save(person);	
		String id = person.getId();	
		ReportFile.logStatusTest(LogStatus.INFO, "Test entity created and added to database");
		
		person.setPassword("Rikkitikkitaki");
		ReportFile.logStatusTest(LogStatus.INFO, "Test entity password changed");
		
		ReportFile.logStatusTest(LogStatus.INFO, "Put request sent");
		
		MvcResult result = mvc.perform(put("/api/people/" + id)
							  .contentType(MediaType.APPLICATION_JSON)
							  .content(person.toString()))
							  .andExpect(status().isOk())
							  .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
							  .andExpect(jsonPath("$.password", is("Rikkitikkitaki")))
							  .andReturn();
		
		if(result.getResponse().getStatus() == 200) {			
			ReportFile.logStatusTest(LogStatus.PASS, "HTTP status code " + result.getResponse().getStatus() + " returned");
		}
		else ReportFile.logStatusTest(LogStatus.FAIL, "HTTP status code " + result.getResponse().getStatus() + " returned");
		
		if(result.getResponse().getContentType().contains("json")) {			
			ReportFile.logStatusTest(LogStatus.PASS, "Content returned as JSON object");
		}
		else ReportFile.logStatusTest(LogStatus.FAIL, "Incorrect content type returned");
		
		if(result.getResponse().getContentAsString().contains("\"password\":\"Rikkitikkitaki\"")) {			
			ReportFile.logStatusTest(LogStatus.PASS, "Expected password found in response body");
		}
		else ReportFile.logStatusTest(LogStatus.FAIL, "Incorrect password found in response body");
	}
	
	//Delete person from database by ID
	@Test @Ignore
	public void deletePersonFromDatabase() throws Exception {
		
		ReportFile.createTest("Test: Delete person from database by ID");
		
		Person person = new Person("stansmith@cia.com", "Stan", "Trainer", "bush4life");
		personRepo.save(person);	
		String id = person.getId();	
		ReportFile.logStatusTest(LogStatus.INFO, "Test entity created and added to database");
		
		ReportFile.logStatusTest(LogStatus.INFO, "Delete request sent");
		
		MvcResult result = mvc.perform(delete("/api/people/" + id)
							  .contentType(MediaType.APPLICATION_JSON))
							  .andExpect(status().isOk())
							  .andReturn();
		
		if(result.getResponse().getStatus() == 200) {			
			ReportFile.logStatusTest(LogStatus.PASS, "HTTP status code " + result.getResponse().getStatus() + " returned");
		}
		else ReportFile.logStatusTest(LogStatus.FAIL, "HTTP status code " + result.getResponse().getStatus() + " returned");
		
		if(personRepo.findByEmail("stansmith@cia.com").isEmpty()) {			
			ReportFile.logStatusTest(LogStatus.PASS, "Test entity deleted from database");
		}
		else ReportFile.logStatusTest(LogStatus.FAIL, "Test entity not deleted from database");
	}
	
	//Search for person in database
	@Test @Ignore
	public void search() throws Exception {
		
		ReportFile.createTest("Test: Search for trainee");
		
		Person person = new Person("zappbrannigan@theinfosphere.com", "Zapp", "Training Manager", "Champagan");
		personRepo.save(person);
		ReportFile.logStatusTest(LogStatus.INFO, "Test entity created and added to database");
		
		ReportFile.logStatusTest(LogStatus.INFO, "Get request sent");
		MvcResult result = mvc.perform(get("/api/find?search=")
							  .contentType(MediaType.APPLICATION_JSON)
							  .content("zap"))
							  .andExpect(status().isOk())
							  .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
							  .andReturn();
		
		if(result.getResponse().getStatus() == 200) {			
			ReportFile.logStatusTest(LogStatus.PASS, "HTTP status code " + result.getResponse().getStatus() + " returned");
		}
		else ReportFile.logStatusTest(LogStatus.FAIL, "HTTP status code " + result.getResponse().getStatus() + " returned");
		
		if(result.getResponse().getContentType().contains("json")) {			
			ReportFile.logStatusTest(LogStatus.PASS, "Content returned as JSON object");
		}
		else ReportFile.logStatusTest(LogStatus.FAIL, "Incorrect content type returned");
		
		if(result.getResponse().getContentAsString().contains("\"name\":\"Zapp\"")) {			
			ReportFile.logStatusTest(LogStatus.PASS, "Expected content found in response body");
		}
		else ReportFile.logStatusTest(LogStatus.FAIL, "Incorrect content found in response body");
	}
	
	//Login
	@Test @Ignore
	public void login() throws Exception {
		
		ReportFile.createTest("Test: Login");
		
		Person person = new Person("pgriffin@happygoluckytoyfactory", "Peter", "Trainee", "Nyahhhh");
		personRepo.save(person);
		
		ReportFile.logStatusTest(LogStatus.INFO, "Test entity created and added to database");

		ReportFile.logStatusTest(LogStatus.INFO, "Post request sent");
		MvcResult result = mvc.perform(post("/api/login")
							  .contentType(MediaType.APPLICATION_JSON)
							  .content("{\"email\":\"pgriffin@happygoluckytoyfactory.com\",\"password\":\"Nyahhhh\"}"))
							  .andExpect(status().isOk())
							  .andReturn();
		
		if(result.getResponse().getStatus() == 200) {			
			ReportFile.logStatusTest(LogStatus.PASS, "HTTP status code " + result.getResponse().getStatus() + " returned");
		}
		else ReportFile.logStatusTest(LogStatus.FAIL, "HTTP status code " + result.getResponse().getStatus() + " returned");
	}
	
	//Add CV to Database	
	@Test
	public void addCVToDatabase() throws Exception {
		
		ReportFile.createTest("Test: Add CV to database");
		
		Person person = new Person("pgriffin@happygoluckytoyfactory", "Peter", "Trainee", "Nyahhhh");
		personRepo.save(person);
		
		ReportFile.logStatusTest(LogStatus.INFO, "Test entity person created and added to database");
		
		String id = person.getId();

		MultipartEntityBuilder builder = MultipartEntityBuilder.create();
		builder.addTextBody("field1", "yes", ContentType.TEXT_PLAIN);
		
		File f = new File("TestPlan.docx");
		builder.addBinaryBody(
		    "file",
		    new FileInputStream(f),
		    ContentType.APPLICATION_OCTET_STREAM,
		    f.getName()
		);
		ReportFile.logStatusTest(LogStatus.INFO, "Test entity CV attached to post request");

		HttpPost uploadFile = new HttpPost("http://localhost:8090/api/" + id + "/upload");
		HttpEntity multipart = builder.build();
		uploadFile.setEntity(multipart);
		
		ReportFile.logStatusTest(LogStatus.INFO, "Post request sent");
		CloseableHttpClient httpClient = HttpClients.createDefault();
		CloseableHttpResponse response = httpClient.execute(uploadFile);
		
		assertEquals(200, response.getStatusLine().getStatusCode());
		
		if(response.getStatusLine().getStatusCode() == 200) {			
			ReportFile.logStatusTest(LogStatus.PASS, "HTTP status code " + response.getStatusLine().getStatusCode() + " returned");
		}
		else ReportFile.logStatusTest(LogStatus.FAIL, "HTTP status code " + response.getStatusLine().getStatusCode() + " returned");
	}

	//Get CV from database by ID
	@Test @Ignore
	public void getCVFromDatabase() throws Exception {
				
		ReportFile.createTest("Test: Get CV from database");

		Person person = new Person("stansmith@cia.com", "Stan", "Trainer", "bush4life");
		personRepo.save(person);
		String id = person.getId();
		ReportFile.logStatusTest(LogStatus.INFO, "Test entity person created and added to database");

		MultipartEntityBuilder builder = MultipartEntityBuilder.create();
		builder.addTextBody("field1", "yes", ContentType.TEXT_PLAIN);
		
		
		File f = new File("TestPlan.docx");
		builder.addBinaryBody(
		    "file",
		    new FileInputStream(f),
		    ContentType.APPLICATION_OCTET_STREAM,
		    f.getName()
		);
		ReportFile.logStatusTest(LogStatus.INFO, "Test entity CV attached to post request");

		CloseableHttpClient httpClient = HttpClients.createDefault();
		HttpPost uploadFile = new HttpPost("http://localhost:8090/api/" + id + "/upload");
		HttpEntity multipart = builder.build();
		uploadFile.setEntity(multipart);
		httpClient.execute(uploadFile);
		ReportFile.logStatusTest(LogStatus.INFO, "Test entity CV and added to database");
		
		ReportFile.logStatusTest(LogStatus.INFO, "Get request sent");
		HttpGet getFile = new HttpGet("http://localhost:8090/api/" + id + "/cv/" + "test");
		CloseableHttpResponse response = httpClient.execute(getFile);
		
		assertEquals(200, response.getStatusLine().getStatusCode());
		
		if(response.getStatusLine().getStatusCode() == 200) {			
			ReportFile.logStatusTest(LogStatus.PASS, "HTTP status code " + response.getStatusLine().getStatusCode() + " returned");
		}
		else ReportFile.logStatusTest(LogStatus.FAIL, "HTTP status code " + response.getStatusLine().getStatusCode() + " returned");
	}
	
	//Get all CVs
	
	//Delete CV
	
	//Get state of CV
	
	//Update state of CV
//	@Test @Ignore
//	public void updateStatus() throws Exception {
//		
//		ReportFile.createTest("Test: Update state of CV in database");
//		
//		Person person = new Person("psherman@42wallabyway.com", "P Sherman", "Trainer", "Password", "CV", "Unapproved");
//		personRepo.save(person);
//		ReportFile.logStatusTest(LogStatus.INFO, "Test entity created and added to database");
//				
//		person.setState("Approved");
//		ReportFile.logStatusTest(LogStatus.INFO, "Test entity state updated");
//		
//		ReportFile.logStatusTest(LogStatus.INFO, "Put request sent");
//		MvcResult result = mvc.perform(post("/api/people/" + id + "/n")
//							  .contentType(MediaType.APPLICATION_JSON)
//							  .content(person.toString()))
//							  .andExpect(status().isOk())
//							  .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
//							  .andExpect(jsonPath("$.state", is("Approved")))
//							  .andReturn();
//		
//		ReportFile.logStatusTest(LogStatus.INFO, "HTTP status code " + result.getResponse().getStatus() + " returned");
//
//		if(result.getResponse().getStatus() == 200) {			
//			ReportFile.logStatusTest(LogStatus.PASS, "HTTP status code " + result.getResponse().getStatus() + " returned");
//		}
//		else ReportFile.logStatusTest(LogStatus.FAIL, "HTTP status code " + result.getResponse().getStatus() + " returned");
//		
//		if(result.getResponse().getContentType().contains("json")) {			
//			ReportFile.logStatusTest(LogStatus.PASS, "Content returned as JSON object");
//		}
//		else ReportFile.logStatusTest(LogStatus.FAIL, "Incorrect content type returned");
//		
//		if(result.getResponse().getContentAsString().contains("\"state\":\"Approved\"")) {			
//			ReportFile.logStatusTest(LogStatus.PASS, "Correct password found in response body");
//		}
//		else ReportFile.logStatusTest(LogStatus.FAIL, "Incorrect password found in response body");
//	}
	
}