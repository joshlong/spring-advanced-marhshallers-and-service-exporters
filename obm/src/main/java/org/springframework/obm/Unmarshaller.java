package org.springframework.obm;


import org.springframework.oxm.XmlMappingException;

import java.io.IOException;
import java.io.InputStream;

public interface Unmarshaller <T> {

    boolean supports(Class< T> clazz);

    T unmarshal(Class<T> clazz, InputStream source) throws IOException, XmlMappingException;

}
