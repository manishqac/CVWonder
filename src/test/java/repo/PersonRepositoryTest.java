package repo;

import static org.junit.Assert.assertEquals;

import java.util.List;

import org.junit.Before;
import org.junit.After;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import com.qa.cv.Application;
import com.qa.cv.ReportFile;
import com.qa.cv.model.Person;
import com.qa.cv.model.User;
import com.qa.cv.repo.PersonRepository;
import com.qa.cv.repo.UserRepository;
import com.relevantcodes.extentreports.LogStatus;


@RunWith(SpringRunner.class)
@SpringBootTest(classes = {Application.class})

@DataMongoTest
//@ExtendWith(SpringExtension.class)
public class PersonRepositoryTest {
	
	//@Autowired
	//private TestEntityManager entityManager; 
	
	@Autowired
	private PersonRepository personRepo;
	
	@Autowired
	private UserRepository userRepo;
	
	//Clear the repository before each test
	@Before
	public void deleteAllBeforeTests() throws Exception {
		personRepo.deleteAll();
		userRepo.deleteAll();
		ReportFile.createReport();
	}
	
	@Test
	public void retrievePersonByEmailTest()
	{
		ReportFile.createTest("Retrieve person By Email");
		Person person = new Person("lwhamilton03@outlook.com", "Lucy Hamilton", "Trainee", "password");
		personRepo.save(person); 
		System.out.println(person.getEmail());
				
		List<Person> checkList = personRepo.findByEmail(person.getEmail());
		
		for(Person check:checkList)
		{
			assertEquals("lwhamilton03@outlook.com", check.getEmail());
			
			if(check.getEmail().equals("lwhamilton03@outlook.com"))
			{
				ReportFile.logStatusTest(LogStatus.PASS, "Person has retrieved By Email: " + check.getEmail());
			}
			else
			{
				ReportFile.logStatusTest(LogStatus.FAIL, "Person has not been retrieved by Email");
			}
		}
		//findByEmail will return a list of ONE person with that email	
	}	
	
	@After
	public void tearDown()
	{
		ReportFile.endReport();
	}

}