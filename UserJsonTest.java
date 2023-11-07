package rest;

import io.restassured.RestAssured;
import io.restassured.http.Method;
import io.restassured.path.json.JsonPath;
import io.restassured.response.Response;
import org.junit.Assert;
import org.junit.jupiter.api.Test;
import java.util.ArrayList;
import java.util.Arrays;
import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;

public class UserJsonTest {

    //Como deixar uma rota padrão para execução, sendo que a mesma deverá ser utilizada por todos os
    // testes da classe?

    //ATRIBUTOS ESTÁTICOS

    //@BeforeClass
    //public static void setup() {
    //  RestAssured.baseURI = "https://restapi.waquino.me";
    //  RestAssured.port = 443;
    //  RestAssured.basePath = ""; exemplo:v1,v2,etc
    //@Test
    //public void....
    //given().log().all()
    //.when().get("/users")
    //.then().statusCode(200)



    //Como fazer com que uma mesma validação aconteça em todos os testes?

    //RESPONSE/REQUEST SPECIFICATION

    //public static RequestSpecification reqSpec;
    //public static ResponseSpecification resSpec;

    //@BeforeClass
    //public static void setup() {
    //  RestAssured.baseURI = "https://restapi.waquino.me";
    //  RestAssured.port = 443;
    //  RestAssured.basePath = ""; exemplo:v1,v2,etc

    //  RequestSpecBuilder reqBuilder = new RequestSpecBuilder();
    //  reqBuilder.log(logDetail.ALL); //dada toda a estrutura da página url
    //  reqSpec = reqBuilder.build();

    //  ResponseSpecBuilder resBuilder = new ResponseSpecBuilder();
    //  resBuilder.expectStatusCode(200); //vai validar que todos devem retornar statuscode 200
    //  resSpec = resBuilder.build();

    //  RestAssured.requestSpecification = reqSpec;  //essa parte é para nao ter que acrescentar o reqSpec/resSpec em cada teste, senao tiver isso tem que adc o req no given e o resp no then
    //  RestAssured.responseSpecification = resSpec;

    //@Test
    //public void....
    //given().                 ai nao precisa dessa parte -> log().all()
    //.when().get("/users")
    //.then().statusCode(200)


    //Níveis de Json

    @Test
    public void deveVerificarPrimeiroNivel() {
        given()
                .when()
                .get("http://restapi.wcaquino.me/users/1")
                .then()
                .statusCode(200)
                .body("id", is(1))
                .body("name", containsString("Silva"))
                .body("age", greaterThan(18));
    }

    @Test
    public void deveVerificarPrimeiroNivelOutrasFormas() {
        Response response = RestAssured.request(Method.GET, "http://restapi.wcaquino.me/users/1");

        //path
        Assert.assertEquals(new Integer(1), response.path("id"));
        Assert.assertEquals(new Integer(1), response.path("%s", "id")); //passando o id or parametro

        //jsonpath
        JsonPath jpath = new JsonPath(response.asString()); //passa todoo o corpo da resposta
        Assert.assertEquals(1, jpath.getInt("id"));

        //from
        int id = JsonPath.from(response.asString()).getInt("id");
        Assert.assertEquals(1, id);
    }

    @Test
    public void deveVerificarSegundoNivel() {
        given()
                .when()
                .get("http://restapi.wcaquino.me/users/2")
                .then()
                .statusCode(200)
                .body("name", containsString("Joaquina"))
                .body("endereco.rua", is("Rua dos bobos")); //cada nivel é separado por ponto final
    }

    @Test
    public void deveVerificarListal() {
        given()
                .when()
                .get("http://restapi.wcaquino.me/users/3")
                .then()
                .statusCode(200)
                .body("name", containsString("Ana"))
                .body("filhos", hasSize(2))
                .body("filhos[0].name", is("Zezinho"))
                .body("filhos[1].name", is("Luizinho"))
                .body("filhos.name", hasItems("Zezinho", "Luizinho"));
    }

    @Test
    public void deveRetornarErroUsuarioInexistente() {
        given()
                .when()
                .get("https://restapi.wcaquino.me/users/4")
                .then()
                .statusCode(404)
                .body("error", is("Usuário inexistente"));
    }

    @Test
    public void deveVerificarListaRaiz() {
        given()
                .when()
                .get("https://restapi.wcaquino.me/users")
                .then()
                .statusCode(200)
                .body("", hasSize(3)) //o parametro vai vazio, pois está pesquisando na raiz
                .body("name", hasItems("João da Silva", "Maria Joaquina", "Ana Júlia"))
                .body("age[1]", is(25)) //pesquisa no parametro do item 1
                .body("filhos.name", hasItems(Arrays.asList("Zezinho", "Luizinho"))) //nomes da lista de segundo nivel
                .body("salary", contains(1234.5678f, 2500, null)); //valores de sslario, mesmo onde nao tem (null)
    }

    @Test
    public void devoFazerVerificacoesAvancadas() {
        given()
                .when()
                .get("https://restapi.wcaquino.me/users")
                .then()
                .statusCode(200)
                .body("", hasSize(3))
                .body("age.findAll{it <= 25}.size()", is(2)) //verifica o age a partir da raiz, menor ou igual a 25 anos, o retorno deve ser tamanho 2
                .body("age.findAll{it <= 25 && it.age > 20}.size()", is(1)) //it significa o dado que estará sendo pesquisado - iterando
                .body("findAll{it.age <= 25 && it.age > 20}.name", hasItem("Maria Joaquina")) //como busca idade e nome (duas coisas diferentes) começa com findAll
                .body("findAll{it.age <= 25}[0].name", is("Maria Joaquina")) //busca o primeiro item da busca por idade na raiz
                .body("findAll{it.age <= 25}[-1].name", is("Ana Júlia")) //-1 é o último da lista
                .body("find{it.age <= 25}.name", is("Maria Joaquina"))//find busca um único elemento, nao uma coleção
                .body("findAll{it.name.contains('n')}.name", hasItems("Maria Joaquina", "Ana Júlia"))//busca os nomes que contém "n"
                .body("findAll{it.name.length() > 10}.name", hasItems("João da Silva", "Maria Joaquina"))//busca os nomes com mais de 10 letras
                .body("name.collect{it.toUpperCase()}", hasItem("MARIA JOAQUINA")) //transforma o nome em maiuscula
                .body("name.findAll{it.startsWith('Maria')}.collect{it.toUpperCase()}", hasItem("MARIA JOAQUINA")) //transforma os nomes que começam com "Maria" em maiuscula
                .body("name.findAll{it.startsWith('Maria')}.collect{it.toUpperCase()}.toArray()", allOf(arrayContaining("MARIA JOAQUINA"), arrayWithSize(1))) //confirmando com dois tipos de dado: o nome e o tamanho da lista
                .body("age.collect{it * 2}", hasItems(60, 50, 40))
                .body("id.max()", is(3)) //máximo dos id = 3
                .body("salary.min()", is(1234.5678f)) //buscas o menor salario
                .body("salary.findAll{it != null}.sum()", is(closeTo(3734.5678f, 0.001))); //a soma dos salarios, com um filtro para nao buscar null e não dar erro
    }

    @Test
    public void devoUnirJsonPathComJAVA() { //forma mais legivel
        ArrayList<String> names = //esse é o retorno, oq vai ser "dado"
                given()
                        .when()
                        .get("https://restapi.wcaquino.me/users")
                        .then()
                        .statusCode(200)
                        .extract().path("name.findAll{it.startsWith('Maria')}"); //extract vai extrair algo que determina oq será dado ex. uma lista de string

        //aqui vao as verificações interessantes
        Assert.assertEquals(1, names.size()); //verifica se o tamanho da lista é 1
        Assert.assertTrue(names.get(0).equalsIgnoreCase("mArIa Joaquina")); //verifica se o primeiro nome é maria joaquina
    }

}