package rest;

import io.restassured.http.ContentType;
import org.hamcrest.Matchers;
import org.junit.Test;

import java.util.HashMap;
import java.util.Map;

import static io.restassured.RestAssured.given;

public class AuthTest {


       // ACESSANDO API ABERTA
    @Test
    public void deveAcessarSWAPI() {
        given()
                .log().all()
                .when()
                .get("https://swapi.dev/api/people/1/")
                .then()
                .log().all()
                .statusCode(200)
                .body("name", Matchers.is("Luke Skywalker"));
        }


        // ACESSANDO API COM AUTENTICAÇÃO
    @Test
    public void deveObterClima() {
        given()
                .log().all()
                .queryParam("q", "Fortaleza, BR") //todos esses parametros constam na url
                .queryParam("appid", "25446612654684485")
                .queryParam("units", "metric")
                .when()
                .get("https://api.openweathermap.org/data/2.5/weather")
                .then()
                .log().all()
                .statusCode(200)
                .body("name", Matchers.is("Fortaleza")) //validando conforme oq retornou no body
                .body("main.temp", Matchers.greaterThan(25f)); //validando que menos de 25C não é fortaleza
    }


        // ACESSANDO COM AUTENTICAÇÃO BÁSICA
    @Test
    public void naoDeveAcessarSemSenha() {
        given()
                .log().all()
                .when()
                .get("https://restapi.wcaquino.me/basicauth")
                .then()
                .log().all()
                .statusCode(401);
    }



    //Para esse tipo de autenticação com preenchimento de login e senha, deve-se colocar o login loga após o // seguido por : e a senha com @ no final

    @Test
    public void deveFazerAutenticacaoBasica() {
        given()
                .log().all()
                .when()
                .get("https://admin:senha@restapi.wcaquino.me/basicauth")//aqui
                .then()
                .log().all()
                .statusCode(200)
                .body("status", Matchers.is("logado"));
    }


    //Esse tipo de autenticação com login e senha é a mesma, mas os valores são passados no given
    @Test
    public void deveFazerAutenticacaoBasica2() {
        given()
                .log().all()
                .auth().basic("admin", "senha") //aqui
                .when()
                .get("https://restapi.wcaquino.me/basicauth")
                .then()
                .log().all()
                .statusCode(200)
                .body("status", Matchers.is("logado"));
    }



    //Autenticação básica Challenge ou Primitiva
    @Test
    public void deveFazerAutenticacaoBasicaChallenge() {
        given()
                .log().all()
                .auth().preemptive().basic("admin", "senha") //inclui o preemptive aqui
                .when()
                .get("https://restapi.wcaquino.me/basicauth2")
                .then()
                .log().all()
                .statusCode(200)
                .body("status", Matchers.is("logado"));
    }



    //Autenticação com token JWT
    @Test
    public void deveFazerAutenticacaoComTokenJWT() {
        //Map cpm a finalidade de organizar os valores de login e senha//
        Map<String, String> login = new HashMap<String, String>();
        login.put("email", "wagner@aquino");
        login.put("senha", "123456");

        //Realiza o login na api
        String token = given() //recebendo o token
                .log().all()
                .body(login)
                .contentType(ContentType.JSON)
                .when()
                .post("http://barrigarest.wcaquino.me/singin")
                .then()
                .log().all()
                .statusCode(200)
                .extract().path("token"); //manda o token para o given

        //Obtém as contas
        given()
                .log().all()
                .header("Authorization", "JWT " + token) //passando as infos no cabeçalho
                .when()
                .get("http://barrigarest.wcaquino.me/contas")
                .then()
                .log().all()
                .statusCode(200)
                .body("nome", Matchers.hasItem("Conta de teste"));
    }
}
