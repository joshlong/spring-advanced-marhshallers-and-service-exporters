package org.springframework.obm.messagepack;

import org.junit.Before;
import org.junit.Test;
import org.springframework.obm.BaseMarshallerTest;

/**
 * Test of the {@link MessagePackMarshaller}
 *
 * @author Josh Long
 */
public class TestMessagePackMarshaller extends BaseMarshallerTest<Cat> {

    private Cat cat = new Cat();

    @Before
    public void before() throws Throwable {

        cat.setAge(4);
        cat.setName("Felix");

        MessagePackMarshaller<Cat> messagePackMarshaller = new MessagePackMarshaller<Cat>();
        messagePackMarshaller.afterPropertiesSet();

        setMarshaller(messagePackMarshaller);
        setUnmarshaller(messagePackMarshaller);
    }

    @Test
    public void testMarshalling() throws Throwable {
        doTestMarshalling(Cat.class, this.cat);
    }
}
