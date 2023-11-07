package rest;

import io.restassured.RestAssured;
import io.restassured.http.Method;
import io.restassured.response.Response;
import io.restassured.response.ValidatableResponse;
import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.jupiter.api.Test;
import java.util.Arrays;
import java.util.List;

public class OlaMundoTest {

    @Test //anotação do JUnit
    public void testOlaMundo() {
        Response response = RestAssured.request(Method.GET, "http://restapi.wcaquino.me/ola");
        Assert.assertTrue(response.getBody().asString().equals("Ola Mundo!"));
        Assert.assertTrue("O statusCode deveria ser 200", response.statusCode() == 200); //forma 1
        Assert.assertEquals(200, response.statusCode()); //forma 2 (é melhor) - 1º valor esperado, 2º valor atual

        ValidatableResponse validacao = response.then();
        validacao.statusCode(200);
    }

    @Test
    public void devoConhecerOutrasFormasRestAssured() {
        Response response = RestAssured.request(Method.GET, "http://restapi.wcaquino.me/ola"); //forma 3
        ValidatableResponse validacao = response.then(); //então
        validacao.statusCode(200);

        RestAssured.get("http://restapi.wcaquino.me/ola").then().statusCode(200); // forma 4

        RestAssured.given()  //forma 5
                .when()
                .get("http://restapi.wcaquino.me/ola")
                .then()
                .statusCode(200);
    }

    //Rest Assured básico

    @Test
    public void devoConhecerMatcherHamcrest() {
        Assert.assertThat("Maria", Matchers.is("Maria")); //se maria é maria
        Assert.assertThat(128, Matchers.is(128));
        Assert.assertThat(128, Matchers.isA(Integer.class)); //se 128 é um inteiro
        Assert.assertThat(128d, Matchers.greaterThan(120d)); //se 128 é maior que 120
        Assert.assertThat(128, Matchers.lessThan(130)); //se 128 é menor que 130

        List<Integer> impares = Arrays.asList(1,3,5,7,9);
        Assert.assertThat(impares, Matchers.hasSize(5)); //verifica se a lista tem 5 itens
        Assert.assertThat(impares, Matchers.contains(1,3,5,7,9)); // se contém
        Assert.assertThat(impares, Matchers.containsInAnyOrder(1,3,5,9,7)); //se contém independente da ordem
        Assert.assertThat(impares, Matchers.hasItem(1));
        Assert.assertThat(impares, Matchers.hasItems(1,5));

        Assert.assertThat("Maria", Matchers.not("João"));
        Assert.assertThat("Maria", Matchers.anyOf(Matchers.is("Joaquina"), Matchers.is(("Maria")))); //OU
        Assert.assertThat("Joaquina", Matchers.allOf(Matchers.startsWith("Joa"), Matchers.endsWith("ina"), Matchers.containsString("qui"))); //contém começo, fim e meio
    }

    @Test
    public void devoValidarBody() {
        RestAssured.given()
                .when()
                .get("http://restapi.wcaquino.me/ola")
                .then()
                .statusCode(200)
                .body(Matchers.is("Ola Mundo!")) //contém isso
                .body(Matchers.containsString("Mundo")) //contém essa parte
                .body(Matchers.not(Matchers.nullValue())); //contém alguma coisa
    }
}
