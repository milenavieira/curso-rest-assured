package rest;

import io.restassured.http.ContentType;
import org.junit.jupiter.api.Test;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;

public class HtmlTest {

    //HTML

    @Test
    public void deveFazerBuscasComHTML() {
        given()
                .log().all()
                .when()
                .get("https://restapi.wcaquino.me/v2/users")
                .then()
                .log().all()
                .statusCode(200)
                .contentType(ContentType.HTML)
                .body("html.body.div.table.tbody.tr.size()", is(3)) //precisa fazer o caminho em html até a tabela para saber o tamanho dela
                .body("html.body.div.table.tbody.tr[1].td[2]", is("25")); //faz o caminho até a segunda linha e terceira coluna pra retornar o valor que contém naquela celula da tabela
    }
}