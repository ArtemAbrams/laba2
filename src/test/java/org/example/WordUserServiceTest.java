package org.example.lab2;

import org.example.User;
import org.example.WordUserService;
import org.testng.Assert;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.everyItem;
import static org.hamcrest.Matchers.greaterThan;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.hasProperty;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.startsWith;

public class WordUserServiceTest {

    private static WordUserService wordUserService;
    private static final String USER_ID = "1";
    private static final String USER_NAME = "Test User";
    private static final int USER_AGE = 30;
    private static User testUser;

    @BeforeClass
    public void setUpBeforeClass() throws IOException {
        Path tempDir = Files.createTempDirectory("test");
        Path testFile = tempDir.resolve("testlab.docx");
        wordUserService = new WordUserService(testFile.toString());
        testUser = new User(USER_ID, USER_NAME, USER_AGE);
    }

    @BeforeMethod
    public void setUp() throws IOException {
        wordUserService.addUser(testUser);
    }

    @Test
    public void addUserAndRetrieveUser() throws IOException {
        User retrievedUser = wordUserService.getUser(0);
        Assert.assertNotNull(retrievedUser, "User should not be null after adding");
        Assert.assertEquals(testUser.toString(), retrievedUser.toString());
    }

    @AfterMethod
    public void tearDown() throws IOException {
        Path tempDir = Files.createTempDirectory("test");
        Path testFile = tempDir.resolve("test.docx");
        File file = new File(testFile.toUri());
        if (file.exists()) {
            file.delete();
        }
    }

    @Test
    public void checkUserNotExists() {
        Assert.assertNull(wordUserService.getUser(1000), "User should be null if not exists");
    }

    @Test
    public void removeUserAndCheckExistence() throws IOException {
        Assert.assertThrows(IndexOutOfBoundsException.class, () -> wordUserService.removeUser(-10));
    }

    @Test
    public void getAllUsersCheckSize() {
        List<User> users = wordUserService.getAllUsers();
        Assert.assertFalse(users.isEmpty(), "User list should not be empty after adding users");
    }

    @Test
    public void operationShouldNotThrowException() throws IOException {
        User retrievedUser = wordUserService.getUser(0);
        Assert.assertEquals("1", retrievedUser.getId());
    }

    @Test(dataProvider = "userDataProvider")
    public void addUserAndRetrieveUser(String id, String name, int age) throws IOException {
        User user = new User(id, name, age);
        wordUserService.addUser(user);

        int userIndex = wordUserService.getAllUsers().size() - 1;
        User retrievedUser = wordUserService.getUser(userIndex);

        Assert.assertNotNull(retrievedUser, "User should not be null after adding");
        Assert.assertEquals(user.toString(), retrievedUser.toString());
    }

    @DataProvider(name = "userDataProvider")
    public Object[][] provideUserData() {
        return new Object[][] {
                {"1", "Test User One", 25},
                {"2", "Test User Two", 30},
                {"3", "Test User Three", 35}
        };
    }

    @Test
    public void getAllUsersShouldMatchSpecificCriteria() throws IOException {
        wordUserService.addUser(new User("1", "John Doe", 30));
        wordUserService.addUser(new User("2", "Jane Doe", 25));
        List<User> users = wordUserService.getAllUsers();

        assertThat(users, hasItem(hasProperty("name", is("John Doe"))));
        assertThat(users, hasItem(hasProperty("age", greaterThan(20))));

        assertThat(users, hasItem(hasProperty("name", startsWith("Jane"))));
    }

    @Test
    public void getAllUsersShouldMatchSpecificCriteria_2() throws IOException {
        wordUserService.addUser(new User("1", "John Doe", 30));
        wordUserService.addUser(new User("2", "Jane Doe", 25));
        List<User> users = wordUserService.getAllUsers();

        assertThat(users, everyItem(hasProperty("age", greaterThan(15))));
    }
}
