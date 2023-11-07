package rest;

import io.restassured.http.ContentType;
import org.junit.jupiter.api.Test;
import static io.restassured.RestAssured.given;

public class FormasEnvioDadosTest {


    //Query

    @Test
    public void deveEnviarValorViaQuery() {
        given()
                .log().all()
                .when()
                .get("https://restapi.wcaquino.me/v2/users?format=json")
                .then()
                .log().all()
                .statusCode(200)
                .contentType(ContentType.JSON);
    }

    //Query com Parametros

    @Test
    public void deveEnviarValorViaQueryParam() {
        given()
                .log().all()
                .queryParam("format", "xml") //os parametros sempre vao em given //atributo, formato
                .when()
                .get("https://restapi.wcaquino.me/v2/users")
                .then()
                .log().all()
                .statusCode(200)
                .contentType(ContentType.XML);
    }

    //Header

    @Test
    public void deveEnviarValorViaHeader() {
        given()
                .log().all()
                .accept(ContentType.XML) //somente aceita se for XML
                .when()
                .get("https://restapi.wcaquino.me/v2/users")
                .then()
                .log().all()
                .statusCode(200)
                .contentType(ContentType.XML);
    }
}
