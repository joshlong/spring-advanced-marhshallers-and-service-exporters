package org.org.springframework.obm;


import org.springframework.oxm.XmlMappingException;

import java.io.IOException;
import java.io.InputStream;

public interface Unmarshaller {

	boolean supports(Class<?> clazz);

	Object unmarshal(InputStream source) throws IOException, XmlMappingException;

}
