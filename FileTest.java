package rest;

import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.jupiter.api.Test;
import org.omg.CORBA.portable.OutputStream;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.lessThan;

public class FileTest {


    //OBS: Não foram anexados arquivos para esses testes

    @Test
    public void deveObrigarEnvioArquivo() {
        given()
                .log().all()
                .when()
                .post("http://restapi.wcaquino.me/upload")//método post pq esta querendo enviar um arquivo
                .then()
                .log().all()
                .statusCode(404)
                .body("error", Matchers.is("Arquivo não enviado"));

    }

    @Test
    public void deveFazerUploadArquivo() {
        given()
                .log().all()
                .multiPart("arquivo", new File("src/main/resources/users.pdf")) //multipart
                // é uma função utilizada para envio de arquivo que contém o nome e o local onde o arquivo se encontra
                .when()
                .post("http://restapi.wcaquino.me/upload")
                .then()
                .log().all()
                .statusCode(200)
                .body("name", Matchers.is("users.pdf")); //o body sempre vai ser acrescentado depois de verificar o que de fato tal teste retorna
            //o upload do arquivo pode ser absoluto ou relativo
    }

    @Test
    public void naoDeveFazerUploadArquivoGrande() {
        given()
                .log().all()
                .multiPart("arquivo", new File("src/main/resources/iText-2.1.0.jar"))
                .when()
                .post("http://restapi.wcaquino.me/upload")
                .then()
                .log().all()
                .time(lessThan(5000L)) //define que o tempo de execução do teste nao deve ser maior que 5s - tipo "teste de performance"
                .statusCode(413);
    }

    @Test
    public void deveBaixarArquivo() throws IOException {
        byte[] image = given() //transformando em uma imagem
                .log().all()
                .when()
                .get("http://restapi.wcaquino.me/download") //método get pq está querendo receber um arquivo
                .then()
                .statusCode(200)
                .extract().asByteArray(); //transformando em um array de bytes ->> vai para o given

        File imagem = new File("src/main/resources/file.jpg");
    //    OutputStream out = new FileOutputStream(imagem); //adc uma exception, mas n precisa tratar
     //   out.write(image);
      //  out.close();

        System.out.println(imagem.length()); //validar o tamanho
      //  Assert.assertThat(imagem.length(), lessThan(100000)); //o tamanho da imagem nao pode ser mais que 10000 bytes

    }
}
