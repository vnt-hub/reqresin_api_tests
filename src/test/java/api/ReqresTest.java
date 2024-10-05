package api;

import io.restassured.http.ContentType;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

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

    public void checkRegistrationSuccess() {
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
}
