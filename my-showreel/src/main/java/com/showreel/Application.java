package com.showreel;

import org.apache.camel.CamelContext;
import org.apache.camel.Exchange;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.impl.DefaultCamelContext;
import org.apache.camel.model.rest.RestBindingMode;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.stereotype.Component;

/**
 * Yksinkertainen rest-service joka palauttaa annetun nimi-datan ja pienen tervehdyksen.
 * Palvelun tarkoituksena on demonstroida osaamista ja ymmärrystä, sekä kykyä oppia seuraavissa:
 *
 * * Java
 * * Camel
 * * Json
 * * REST
 *
 * Voit kutsua palvelua esimerkiksi seuraavalla curl-komennolla:
 *
 * curl -d '{"name": "Nimi"}' -H "Content-Type: application/json" -X POST http://localhost:8080/camel/api/bean
 */

@SpringBootApplication
@ComponentScan(basePackages="com.showreel")
public class Application {

    @Value("${showreel.api.path}")
    String contextPath;

    @Value("${server.port}")
    String serverPort;

    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);

    }

    /**
     * Extendaa RouteBuileder-luokkaa ja asettaa sen komponentiksi, jolloin komponenttiskannaus käsittää sen osaksi palvelua
     */
    @Component
    class RestApi extends RouteBuilder {

        @Override
        public void configure() {
            CamelContext context = new DefaultCamelContext();

            // rest-palvelun konfiguraatio
            restConfiguration()
                    .contextPath(contextPath)
                    .port(serverPort)
                    .enableCORS(true)
                    .apiContextPath("/api-doc")
                    .apiProperty("api.title", "Test REST API")
                    .apiProperty("api.version", "v1")
                    .apiContextRouteId("doc-api")
                    .component("servlet")
                    .bindingMode(RestBindingMode.json);

            // Kuuntelija/palauttaja-route
            rest("/api/")
                    .id("api-route")
                    .consumes("application/json")
                    .post("/bean")
                    .bindingMode(RestBindingMode.auto)
                    .type(JsonModel.class)
                    .to("direct:remoteService");

            // Käsittelijä ja sanoman luominen
            from("direct:remoteService")
                    .routeId("direct-route")
                    .tracing()
                    .log(">>> ${body.name}")
                    .transform().simple("Hei ${in.body.name}! " +
                    "Tässä pieni taidonnäyte osaamisestani.")
                    .setHeader(Exchange.HTTP_RESPONSE_CODE, constant(200));

        }
    }

}
