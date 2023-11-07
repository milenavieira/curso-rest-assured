package rest;

import io.restassured.RestAssured;
import io.restassured.http.Method;
import io.restassured.response.Response;
import io.restassured.response.ValidatableResponse;

public class OlaMundo {

    public static void main(String[] args) {
        Response response = RestAssured.request(Method.GET,"http://restapi.wcaquino.me/ola");
        System.out.println(response.getBody().asString().equals("Ola Mundo!")); //como é uma string, consegue utilizar o equals para confirmar a resposta
        System.out.println(response.statusCode() == 200); //não faz cache, vai sempre buscar a informação na url

        ValidatableResponse validacao = response.then();
        validacao.statusCode(200); //valida o status novamente, mostrando um erro grande na tela em caso de "false"
    }
}
