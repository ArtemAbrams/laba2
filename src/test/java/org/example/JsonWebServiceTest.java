package org.example.lab2;

import org.example.JsonUserService;
import org.example.User;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.both;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.emptyOrNullString;
import static org.hamcrest.Matchers.everyItem;
import static org.hamcrest.Matchers.greaterThan;
import static org.hamcrest.Matchers.hasProperty;
import static org.hamcrest.Matchers.lessThan;
import static org.hamcrest.Matchers.not;
import static org.hamcrest.Matchers.notNullValue;

public class JsonWebServiceTest {

    private JsonUserService service;
    private String initialJson;

    @BeforeClass
    public void setupClass() {
        initialJson = "{\"1\":{\"id\":\"1\", \"name\":\"John\", \"age\":30}}";
    }

    @BeforeMethod(alwaysRun = true)
    public void setup() throws Exception {
        service = new JsonUserService(initialJson);
    }

    @Test(groups = {"group1"})
    public void userExistsAfterAddition() throws Exception {
        User newUser = new User("2", "Jane", 25);
        service.addUser(newUser);
        Assert.assertNotNull(service.getUser("2"));
    }

    @Test(groups = {"group2"})
    public void userHasCorrectName() throws Exception {
        User newUser = new User("3", "Mike", 30);
        service.addUser(newUser);
        Assert.assertEquals("Mike", service.getUser("3").getName());
    }

    @Test(groups = {"group1"})
    public void userRemovalEffectiveness() throws Exception {
        service.addUser(new User("4", "Emily", 22));
        service.removeUser("4");
        Assert.assertNull(service.getUser("4"));
    }

    @Test(groups = {"group2"})
    public void allUsersRetrieval() throws Exception {
        User user = service.getAllUsers()
                .get(0);
        Assert.assertTrue(user.getName().equals("John"));
    }

    @Test(expectedExceptions = IllegalArgumentException.class, groups = {"group1"})
    public void addUserAlreadyExistsThrowsException() throws Exception {
        User existingUser = new User("1", "John", 30);
        service.addUser(existingUser);
    }

    @Test(dataProvider = "provideUsersForNameCheck", groups = {"group2"})
    public void userHasCorrectNameParameterized(String id, String expectedName) throws Exception {
        User newUser = new User("2", "Jane", 25);
        service.addUser(newUser);
        Assert.assertEquals(expectedName, service.getUser(id).getName());
    }

    @DataProvider
    public static Object[][] provideUsersForNameCheck() {
        return new Object[][] {
                {"1", "John"},
                {"2", "Jane"}
        };
    }

    @Test(dataProvider = "userExistenceCheckDataProvider", groups = {"group1"})
    public void userExistenceCheck(String id, boolean expectedExistence) throws Exception {
        if (expectedExistence) {
            Assert.assertNotNull(service.getUser(id));
        } else {
            Assert.assertNull(service.getUser(id));
        }
    }

    @DataProvider
    public static Object[][] userExistenceCheckDataProvider() {
        return new Object[][] {
                {"1", true},
                {"2", false},
                {"3", false}
        };
    }

    @Test(groups = {"group2"})
    public void userPropertiesMatchComplexConditions() throws Exception {
        User user = new User("3", "Jane Smith", 35);
        service.addUser(user);
        User fetchedUser = service.getUser("3");

        assertThat(fetchedUser, allOf(
                hasProperty("name", allOf(notNullValue(), containsString("Smith"))),
                hasProperty("age", allOf(greaterThan(30), lessThan(40)))
        ));
    }

    @Test(groups = {"group1"})
    public void allUsersMatchComplexConditions() throws Exception {
        service.addUser(new User("2", "Jane Doe", 25));

        List<User> allUsersJson = service.getAllUsers();

        assertThat(allUsersJson, everyItem(
                both(hasProperty("name", not(emptyOrNullString())))
                        .and(hasProperty("age", greaterThan(18)))
        ));
    }
}