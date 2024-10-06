package api;

import org.testng.Assert;
import org.testng.annotations.Test;

import java.time.Clock;
import java.util.List;

import static io.restassured.RestAssured.given;

@Test
public class ReqresTest {
    private static final String URL = "https://reqres.in";

    public void checkAvatarAndIdTest() {
        Specs.installSpecification(Specs.requestSpec(URL), Specs.responseSpec200());
        List<UserData> users = given()
                .when()
                .get("/api/users?page=2")
                .then()
                .log().status()
                .log().body()
                .extract().body().jsonPath().getList("data", UserData.class);
        users.forEach(x->Assert.assertTrue(x.getAvatar().contains(x.getId().toString())));
        Assert.assertTrue(users.stream().allMatch(x->x.getEmail().endsWith("@reqres.in")));

        List<String> avatars = users.stream().map(UserData::getAvatar).toList();
        List<String> ids = users.stream().map(x->x.getId().toString()).toList();

        for (int i = 0; i < avatars.size(); i++) {
            Assert.assertTrue(avatars.get(i).contains(ids.get(i)));
        }

    }

    public void checkRegistrationSuccessTest() {
        Specs.installSpecification(Specs.requestSpec(URL), Specs.responseSpec200());
        Integer id = 4;
        String token = "QpwL5tke4Pnpja7X4";
        Registration user = new Registration("eve.holt@reqres.in","pistol");
        SuccessRegistration successRegistration = given()
                .body(user)
                .when()
                .post("/api/register")
                .then()
                .log().status()
                .log().body()
                .extract().as(SuccessRegistration.class);
        Assert.assertEquals(id, successRegistration.getId());
        Assert.assertEquals(token, successRegistration.getToken());
    }

    public void checkRegistrationUnSuccessTest() {
        Specs.installSpecification(Specs.requestSpec(URL), Specs.responseSpec400());
        String error = "Missing password";
        Registration user = new Registration("sydney@fife","");
        UnSucessRegistration unSucessRegistration = given()
                .body(user)
                .when()
                .post("/api/register")
                .then()
                .log().status()
                .log().body()
                .extract().as(UnSucessRegistration.class);
        Assert.assertEquals(error, unSucessRegistration.getError());
    }

    public void checkListResourceSortYearsTest() {
        Specs.installSpecification(Specs.requestSpec(URL), Specs.responseSpec200());
        List<Resource> resources = given()
                .when()
                .get("/api/unknown")
                .then()
                .log().status()
                .log().body()
                .extract().body().jsonPath().getList("data", Resource.class);
        List<Integer> years = resources.stream().map(Resource::getYear).toList();
        List<Integer> yearsSorted = years.stream().sorted().toList();
        Assert.assertEquals(yearsSorted, years);
        System.out.println(years);
        System.out.println(yearsSorted);
    }

    public void deleteTheSecondUserTest() {
        Specs.installSpecification(Specs.requestSpec(URL), Specs.responseSpec204());
        given()
                .when()
                .delete("/api/users/2")
                .then()
                .log().status()
                .log().body();
    }

    public void checkTimeTest() {
        Specs.installSpecification(Specs.requestSpec(URL), Specs.responseSpec200());
        UserTime user = new UserTime("morpheus", "zion resident");
        UserTimeResponse userTimeResponse = given()
                .body(user)
                .when()
                .put("/api/users/2")
                .then()
                .log().status()
                .log().body()
                .extract().as(UserTimeResponse.class);
        String currentTime = Clock.systemUTC().instant().toString().substring(0,15);
        String responseTime = userTimeResponse.getUpdatedAt().substring(0,15);
        Assert.assertEquals(currentTime, responseTime);
        System.out.println(currentTime);
        System.out.println(responseTime);
    }
}
