package com.example.crudapp.bdd;

import com.example.crudapp.dto.UserDto;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.cucumber.datatable.DataTable;
import io.cucumber.java.Before;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.core.JdbcTemplate;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

public class UserApiStepDefinitions {

    @Autowired
    private CucumberSpringConfiguration springConfiguration;

    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    private ResponseEntity<String> lastResponse;
    private UserDto createdUser;
    private Long currentUserId;

    @Before
    public void setUp() {
        jdbcTemplate.execute("DELETE FROM users");
    }

    private String getBaseUrl() {
        return "http://localhost:" + springConfiguration.getPort() + "/api/users";
    }

    @Given("the application is running")
    public void theApplicationIsRunning() {
        ResponseEntity<String> response = restTemplate.getForEntity(getBaseUrl(), String.class);
        assertNotNull(response);
    }

    @Given("the database is clean")
    public void theDatabaseIsClean() {
    }

    @When("I create a user with name {string}, email {string}, and age {int}")
    public void iCreateAUserWithNameEmailAndAge(String name, String email, int age) {
        UserDto userDto = new UserDto();
        userDto.setName(name);
        userDto.setEmail(email);
        userDto.setAge(age);
        lastResponse = restTemplate.postForEntity(getBaseUrl(), userDto, String.class);
        
        if (lastResponse.getStatusCode() == HttpStatus.CREATED) {
            try {
                createdUser = objectMapper.readValue(lastResponse.getBody(), UserDto.class);
                currentUserId = createdUser.getId();
            } catch (Exception e) {
                fail("Failed to parse created user response");
            }
        }
    }

    @Then("the user should be created successfully")
    public void theUserShouldBeCreatedSuccessfully() {
        assertEquals(HttpStatus.CREATED, lastResponse.getStatusCode());
        assertNotNull(createdUser);
    }

    @Then("the response should contain user details")
    public void theResponseShouldContainUserDetails() {
        assertNotNull(lastResponse.getBody());
        assertNotNull(createdUser);
    }

    @Then("the user should have a valid ID")
    public void theUserShouldHaveAValidId() {
        assertNotNull(createdUser.getId());
        assertTrue(createdUser.getId() > 0);
    }

    @Given("a user exists with email {string}")
    public void aUserExistsWithEmail(String email) {
        UserDto userDto = new UserDto();
        userDto.setName("Existing User");
        userDto.setEmail(email);
        userDto.setAge(25);
        ResponseEntity<String> response = restTemplate.postForEntity(getBaseUrl(), userDto, String.class);
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
    }

    @Then("the creation should fail with conflict error")
    public void theCreationShouldFailWithConflictError() {
        assertEquals(HttpStatus.CONFLICT, lastResponse.getStatusCode());
    }

    @Then("the error message should indicate email already exists")
    public void theErrorMessageShouldIndicateEmailAlreadyExists() {
        assertTrue(lastResponse.getBody().contains("already exists"));
    }

    @Then("the creation should fail with validation error")
    public void theCreationShouldFailWithValidationError() {
        assertEquals(HttpStatus.BAD_REQUEST, lastResponse.getStatusCode());
    }

    @Then("the error should contain validation details")
    public void theErrorShouldContainValidationDetails() {
        assertNotNull(lastResponse.getBody());
        assertTrue(lastResponse.getBody().contains("validation") || 
                  lastResponse.getBody().contains("required") ||
                  lastResponse.getBody().contains("valid"));
    }

    @Given("users exist in the system:")
    public void usersExistInTheSystem(DataTable dataTable) {
        List<Map<String, String>> users = dataTable.asMaps(String.class, String.class);
        for (Map<String, String> user : users) {
            UserDto userDto = new UserDto();
            userDto.setName(user.get("name"));
            userDto.setEmail(user.get("email"));
            userDto.setAge(Integer.parseInt(user.get("age")));
            ResponseEntity<String> response = restTemplate.postForEntity(getBaseUrl(), userDto, String.class);
            assertEquals(HttpStatus.CREATED, response.getStatusCode());
        }
    }

    @When("I request all users")
    public void iRequestAllUsers() {
        lastResponse = restTemplate.getForEntity(getBaseUrl(), String.class);
    }

    @Then("I should receive a list of {int} users")
    public void iShouldReceiveAListOfUsers(int expectedCount) {
        assertEquals(HttpStatus.OK, lastResponse.getStatusCode());
        try {
            List<?> users = objectMapper.readValue(lastResponse.getBody(), List.class);
            assertEquals(expectedCount, users.size());
        } catch (Exception e) {
            fail("Failed to parse users list response");
        }
    }

    @Then("the list should contain user {string}")
    public void theListShouldContainUser(String userName) {
        assertTrue(lastResponse.getBody().contains(userName));
    }

    @Given("a user exists with name {string}, email {string}, and age {int}")
    public void aUserExistsWithNameEmailAndAge(String name, String email, int age) {
        UserDto userDto = new UserDto();
        userDto.setName(name);
        userDto.setEmail(email);
        userDto.setAge(age);
        ResponseEntity<String> response = restTemplate.postForEntity(getBaseUrl(), userDto, String.class);
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        
        try {
            createdUser = objectMapper.readValue(response.getBody(), UserDto.class);
            currentUserId = createdUser.getId();
        } catch (Exception e) {
            fail("Failed to parse created user response");
        }
    }

    @When("I request the user by ID")
    public void iRequestTheUserById() {
        lastResponse = restTemplate.getForEntity(getBaseUrl() + "/" + currentUserId, String.class);
    }

    @Then("I should receive the user details")
    public void iShouldReceiveTheUserDetails() {
        assertEquals(HttpStatus.OK, lastResponse.getStatusCode());
        assertNotNull(lastResponse.getBody());
    }

    @Then("the user name should be {string}")
    public void theUserNameShouldBe(String expectedName) {
        assertTrue(lastResponse.getBody().contains("\"name\":\"" + expectedName + "\""));
    }

    @Then("the user email should be {string}")
    public void theUserEmailShouldBe(String expectedEmail) {
        assertTrue(lastResponse.getBody().contains("\"email\":\"" + expectedEmail + "\""));
    }

    @When("I request a user with ID {int}")
    public void iRequestAUserWithId(int userId) {
        lastResponse = restTemplate.getForEntity(getBaseUrl() + "/" + userId, String.class);
    }

    @Then("the request should fail with not found error")
    public void theRequestShouldFailWithNotFoundError() {
        assertEquals(HttpStatus.NOT_FOUND, lastResponse.getStatusCode());
    }

    @Then("the error message should indicate user not found")
    public void theErrorMessageShouldIndicateUserNotFound() {
        assertTrue(lastResponse.getBody().contains("not found") || 
                  lastResponse.getBody().contains("Not Found"));
    }

    @When("I update the user with name {string}, email {string}, and age {int}")
    public void iUpdateTheUserWithNameEmailAndAge(String name, String email, int age) {
        UserDto updateDto = new UserDto();
        updateDto.setName(name);
        updateDto.setEmail(email);
        updateDto.setAge(age);
        HttpEntity<UserDto> entity = new HttpEntity<>(updateDto);
        lastResponse = restTemplate.exchange(
            getBaseUrl() + "/" + currentUserId,
            HttpMethod.PUT,
            entity,
            String.class
        );
    }

    @Then("the user should be updated successfully")
    public void theUserShouldBeUpdatedSuccessfully() {
        assertEquals(HttpStatus.OK, lastResponse.getStatusCode());
    }

    @Then("the user age should be {int}")
    public void theUserAgeShouldBe(int expectedAge) {
        assertTrue(lastResponse.getBody().contains("\"age\":" + expectedAge));
    }

    @When("I update user {string} with email {string}")
    public void iUpdateUserWithEmail(String userName, String newEmail) {
        ResponseEntity<String> usersResponse = restTemplate.getForEntity(getBaseUrl(), String.class);
        try {
            List<Map<String, Object>> users = objectMapper.readValue(usersResponse.getBody(), List.class);
            Long userIdToUpdate = null;
            for (Map<String, Object> user : users) {
                if (userName.equals(user.get("name"))) {
                    userIdToUpdate = Long.valueOf(user.get("id").toString());
                    break;
                }
            }
            
            if (userIdToUpdate == null) {
                fail("User with name " + userName + " not found");
            }
            
            UserDto updateDto = new UserDto();
            updateDto.setName(userName);
            updateDto.setEmail(newEmail);
            updateDto.setAge(25);
            HttpEntity<UserDto> entity = new HttpEntity<>(updateDto);
            lastResponse = restTemplate.exchange(
                getBaseUrl() + "/" + userIdToUpdate,
                HttpMethod.PUT,
                entity,
                String.class
            );
        } catch (Exception e) {
            fail("Failed to find user for update: " + e.getMessage());
        }
    }

    @Then("the update should fail with conflict error")
    public void theUpdateShouldFailWithConflictError() {
        assertEquals(HttpStatus.CONFLICT, lastResponse.getStatusCode());
    }

    @When("I update a user with ID {int}")
    public void iUpdateAUserWithId(int userId) {
        UserDto updateDto = new UserDto();
        updateDto.setName("Updated Name");
        updateDto.setEmail("updated@example.com");
        updateDto.setAge(30);
        HttpEntity<UserDto> entity = new HttpEntity<>(updateDto);
        lastResponse = restTemplate.exchange(
            getBaseUrl() + "/" + userId,
            HttpMethod.PUT,
            entity,
            String.class
        );
    }

    @Then("the update should fail with not found error")
    public void theUpdateShouldFailWithNotFoundError() {
        assertEquals(HttpStatus.NOT_FOUND, lastResponse.getStatusCode());
    }

    @When("I delete the user")
    public void iDeleteTheUser() {
        lastResponse = restTemplate.exchange(
            getBaseUrl() + "/" + currentUserId,
            HttpMethod.DELETE,
            null,
            String.class
        );
    }

    @Then("the user should be deleted successfully")
    public void theUserShouldBeDeletedSuccessfully() {
        assertEquals(HttpStatus.NO_CONTENT, lastResponse.getStatusCode());
    }

    @Then("the user should no longer exist in the system")
    public void theUserShouldNoLongerExistInTheSystem() {
        ResponseEntity<String> response = restTemplate.getForEntity(
            getBaseUrl() + "/" + currentUserId, 
            String.class
        );
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }

    @When("I delete a user with ID {int}")
    public void iDeleteAUserWithId(int userId) {
        lastResponse = restTemplate.exchange(
            getBaseUrl() + "/" + userId,
            HttpMethod.DELETE,
            null,
            String.class
        );
    }

    @Then("the deletion should fail with not found error")
    public void theDeletionShouldFailWithNotFoundError() {
        assertEquals(HttpStatus.NOT_FOUND, lastResponse.getStatusCode());
    }

    @When("I request weather forecast for zipcode {string}")
    public void iRequestWeatherForecastForZipcode(String zipCode) {
        String url = "http://localhost:" + springConfiguration.getPort() + "/api/weather/forecast/" + zipCode;
        lastResponse = restTemplate.getForEntity(url, String.class);
    }

    @When("I request current weather for zipcode {string}")
    public void iRequestCurrentWeatherForZipcode(String zipCode) {
        String url = "http://localhost:" + springConfiguration.getPort() + "/api/weather/current/" + zipCode;
        lastResponse = restTemplate.getForEntity(url, String.class);
    }

    @Then("the response status should be {int}")
    public void theResponseStatusShouldBe(int expectedStatus) {
        assertEquals(expectedStatus, lastResponse.getStatusCodeValue());
    }

    @Then("the response should contain weather data for {string}")
    public void theResponseShouldContainWeatherDataFor(String zipCode) throws Exception {
        String responseBody = lastResponse.getBody();
        Map<String, Object> responseMap = objectMapper.readValue(responseBody, Map.class);
        assertEquals(zipCode, responseMap.get("zipCode"));
        assertNotNull(responseMap.get("location"));
    }

    @Then("the response should contain {int} days of forecast")
    public void theResponseShouldContainDaysOfForecast(int expectedDays) throws Exception {
        String responseBody = lastResponse.getBody();
        Map<String, Object> responseMap = objectMapper.readValue(responseBody, Map.class);
        List<Map<String, Object>> forecast = (List<Map<String, Object>>) responseMap.get("forecast");
        assertEquals(expectedDays, forecast.size());
    }

    @Then("each forecast day should have date, description, temperatures, humidity, and wind speed")
    public void eachForecastDayShouldHaveRequiredFields() throws Exception {
        String responseBody = lastResponse.getBody();
        Map<String, Object> responseMap = objectMapper.readValue(responseBody, Map.class);
        List<Map<String, Object>> forecast = (List<Map<String, Object>>) responseMap.get("forecast");
        
        for (Map<String, Object> day : forecast) {
            assertNotNull(day.get("date"));
            assertNotNull(day.get("description"));
            assertNotNull(day.get("temperatureHigh"));
            assertNotNull(day.get("temperatureLow"));
            assertNotNull(day.get("humidity"));
            assertNotNull(day.get("windSpeed"));
        }
    }

    @Then("the response should contain current weather data")
    public void theResponseShouldContainCurrentWeatherData() throws Exception {
        String responseBody = lastResponse.getBody();
        Map<String, Object> responseMap = objectMapper.readValue(responseBody, Map.class);
        assertNotNull(responseMap.get("date"));
        assertNotNull(responseMap.get("description"));
        assertNotNull(responseMap.get("temperatureHigh"));
        assertNotNull(responseMap.get("temperatureLow"));
        assertNotNull(responseMap.get("humidity"));
        assertNotNull(responseMap.get("windSpeed"));
    }

    @Then("the response should have date, description, temperatures, humidity, and wind speed")
    public void theResponseShouldHaveRequiredWeatherFields() throws Exception {
        String responseBody = lastResponse.getBody();
        Map<String, Object> responseMap = objectMapper.readValue(responseBody, Map.class);
        assertNotNull(responseMap.get("date"));
        assertNotNull(responseMap.get("description"));
        assertNotNull(responseMap.get("temperatureHigh"));
        assertNotNull(responseMap.get("temperatureLow"));
        assertNotNull(responseMap.get("humidity"));
        assertNotNull(responseMap.get("windSpeed"));
    }

    @Then("the response should contain error message {string}")
    public void theResponseShouldContainErrorMessage(String expectedMessage) throws Exception {
        String responseBody = lastResponse.getBody();
        Map<String, Object> responseMap = objectMapper.readValue(responseBody, Map.class);
        String actualMessage = (String) responseMap.get("message");
        assertEquals(expectedMessage, actualMessage);
    }
}
