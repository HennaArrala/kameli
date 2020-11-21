package com.test.it;

import org.apache.camel.builder.RouteBuilder;
import org.springframework.stereotype.Component;

/**
 * A simple Camel route that triggers from a timer and calls a bean and prints to system out.
 * <p/>
 * Use <tt>@Component</tt> to make Camel auto detect this route when starting.
 *
 * Tämä on ROUTE. Yleisesti ottaen tämä on se missä tieto kulkee. Routelle tulee tavaraa sisään
 * ja sitä muokataan matkana varrella haluttuun muotoon. Routen luokan pitää aina extendata RouteBuilderia.
 * Routet rakennetaan aina tähän samaan malliin, tavaraa tavaraa vain tulee lisää, jos halutaan tehdä
 * monimutkaisempia asioita.
 */

// @-Merkillä alkavia sanoja kutsutaan java-ohjelmissa annotaatioiksi. Niiden avulla tuodaan
// monimutkaisia lisäosia helposti java-luokkaan. @Component kertoo tässä tapauksessa ohjelmalle,
// että tämä luokka kuuluu pakettiin ja on syytä huomioida.
@Component
public class MySpringBootRouter extends RouteBuilder {

    // Override tekee aikalailla sen, mitä sen kuvittelisikin tekevän, eli yliajaa ylemmän, niin
    // kutsutun super-luokan configure metodin.
    @Override
    public void configure() {
        // Route alkaa aina tavaran sisään ottamisella. From() ottaa sissänsä useita eri kamelin
        // komponentteja. tässä tapauksessa fromin sisään on laitettu ajastin, joka käynnistää routen
        // määritellyssä perioideissa. Tässä tapauksesssa periodi määritellään application.properties-tiedostossa.
        // Yleiesti ottaen on järkevää käyttää properties-tiedostoa. Tiedostoa käytetään routella aina samlla tavalla
        // haluttu "propsu" laitetaan kahden aaltosulje-parin sisään. Tässä tapauksessa routelle on haluttu antaa
        // myös id (routeId("hello"). Routen nimi näkyy logilla, joka helpottaa esimerksi vianmääritystä.
        from("timer:hello?period={{timer.period}}").routeId("hello")

            // Kun route on startattu timerilla, sille pitää saada tavaraa. Tässä tapauksessa routelle tulee
            // sisältöä beanin avulla. Bean sijaisee Luokassa MySpringBean Huomaa @Component annotaatio. Se on hieman
            // erilainen tämän luokan @Componenttiin.
            //
            // Tässä siis kutsutaan beanin myBean metordia saySomething. myBean on hieman harhaanjohtava, koska se
            // sijaitsee MySpringBean luokassa. Sille asetetaan siellä nimi myBean.
            .transform().method("myBean", "saySomething")

            // Tässä kohtaa routella olevaa bodia käpistellään. Routen sisässä kulkevaa tietoa kutsutaan bodyksi.
            // Body voidaan aina kaivaa esiin "komennolla" ${body}. Tässä kohta sisältöä filtteröidään sen mukaan
            // sisältääkö se sanan foo. Jos foo löytyy bodysta, niin lokiteaan foo + body. Filter pitää aina lopettaa
            // .end-komennolla. Jos sisältö ei pidä sisällään foota, niin siinä tapauksessa body tulostetaan lokille.
            .filter(simple("${body} contains 'foo'"))
                .to("log:foo")
            .end()
            .to("stream:out");
    }

}
