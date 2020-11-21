package com.test.it;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * A bean that returns a message when you call the {@link #saySomething()} method.
 * <p/>
 * Uses <tt>@Component("myBean")</tt> to register this bean with the name <tt>myBean</tt>
 * that we use in the Camel route to lookup this bean.
 */
@Component("myBean")
public class MySpringBean {

    // Tällä tavalla tuodaan application.properties-tiedostossa olevia propsuja beaniin.
    // greeting-prosun sisältämä informaatio asetetaan String-muuttujaan nimeltä say.
    @Value("${greeting}")
    private String say;

    // Luokan ainoa metodi saySomething, palauttaa srtingin say arvon
    public String saySomething() {
        return say;
    }

}
