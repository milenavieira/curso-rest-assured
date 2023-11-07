package rest;

import org.junit.Assert;
import org.junit.jupiter.api.Test;
import java.util.HashMap;
import java.util.Map;
import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;

public class VerbosTest {


    //MÉTODO POST- incluindo um usuário

    @Test
    public void deveSalvarUsuario() {
        given()
                .log().all() //aparece o log no console
                .contentType("application/json") //indica que os dados do body são do tipo json
                .body("{ \"name\": \"Jose\", \"age\": 50}")
                .when()
                .post("https://restapi.wcaquino.me/users")//post deve ser feito na url generica
                .then()
                .log().all()
                .statusCode(201)
                .body("id", is(notNullValue())) //valor sequencial
                .body("name", is("Jose"))
                .body("age", is(50));

    }

    //validação do teste IMPORTANTE

    @Test
    public void naoDeveSalvarUsuarioSemNome() {
        given()
                .log().all()
                .contentType("application/json")
                .body("{ \"age\": 50}")
                .when()
                .post("https://restapi.wcaquino.me/users")
                .then()
                .log().all()
                .statusCode(400) //indicando o erro
                .body("id", is(nullValue()))
                .body("error", is("Name é um atributo obrigatório")); //descritor do erro

    }

    //MÉTODO PUT - alterando usuário

    @Test
    public void deveAlterarUsuario() {
        given()
                .log().all()
                .contentType("application/json")
                .body("{ \"name\": \"Usuario alterado\", \"age\": 80}")
                .when()
                .put("https://restapi.wcaquino.me/users/1")
                .then()
                .log().all()
                .statusCode(200)
                .body("id", is(1))
                .body("name", is("Usuario alterado"))
                .body("age", is(80))
                .body("salary", is(1234.5678f));

    }


    //PARAMETRIZANDO A URL

    @Test
    public void deveCustomizarURL() {
        given()
                .log().all()
                .contentType("application/json")
                .body("{ \"name\": \"Usuario alterado\", \"age\": 80}")
                .pathParam("entidade", "users")
                .pathParam("userId", 1)
                .when()
                .put("https://restapi.wcaquino.me/{entidade}/{userId}")
                .then()
                .log().all()
                .statusCode(200)
                .body("id", is(1))
                .body("name", is("Usuario alterado"))
                .body("age", is(80))
                .body("salary", is(1234.5678f));
    }


    //MÉTODO DELETE - remover usuário

    @Test
    public void deveRemoverUsuario() {
        given()
                .log().all()
                .when()
                .delete("https://restapi.wcaquino.me/users/1")
                .then()
                .log().all()
                .statusCode(204);
    }

    //validação do teste IMPORTANTE

    @Test
    public void naoDeveRemoverUsuarioInexistente() {
        given()
                .log().all()
                .when()
                .delete("https://restapi.wcaquino.me/users/1000")
                .then()
                .log().all()
                .statusCode(400)
                .body("error", is("Registro inexistente"));
    }

    //SERIALIZAÇÃO JSON

    @Test
    public void deveSalvarUsuarioUsandoMap() {
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("name", "Usuario via map");
        params.put("age", 25);

        given()
                .log().all()
                .contentType("application/json")
                .body(params) //só passa a variavel criada no Map
                .when()
                .post("https://restapi.wcaquino.me/users")
                .then()
                .log().all()
                .statusCode(201)
                .body("id", is(notNullValue()))
                .body("name", is("Usuario via map"))
                .body("age", is(25));
    }

    @Test
    public void deveSalvarUsuarioUsandoObjeto() {
        User user = new User("Usuario via objeto", 35); //instanciando o objeto da classe

        given()
                .log().all()
                .contentType("application/json")
                .body(user) //só passa a variavel instanciada
                .when()
                .post("https://restapi.wcaquino.me/users")
                .then()
                .log().all()
                .statusCode(201)
                .body("id", is(notNullValue()))
                .body("name", is("Usuario via objeto"))
                .body("age", is(35));
    }

    //DESERIALIZAÇÃO

    @Test
    public void deveDeserializarObjetoAoSalvarUsuario() {
        User user = new User("Usuario deserializado", 35); //instanciando o objeto da classe

        User usuarioInserido = given()
                .log().all()
                .contentType("application/json")
                .body(user) //só passa a variavel instanciada
                .when()
                .post("https://restapi.wcaquino.me/users")
                .then()
                .log().all()
                .statusCode(201)
                .extract().body().as(User.class); //aqui extrai e deserializa para o objeto da classe

        System.out.println(usuarioInserido);
        Assert.assertThat(usuarioInserido.getAge(), is(35));
    }
}
