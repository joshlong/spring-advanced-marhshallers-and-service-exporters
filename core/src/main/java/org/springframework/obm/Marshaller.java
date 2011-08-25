package org.springframework.obm;


import org.springframework.oxm.XmlMappingException;

import java.io.IOException;
import java.io.OutputStream;

public interface Marshaller {

	boolean supports(Class<?> clazz);

	void marshal(Object graph, OutputStream result) throws IOException, XmlMappingException;

}
