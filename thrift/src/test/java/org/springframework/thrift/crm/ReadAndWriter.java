/*
 * Copyright 2002-2008 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.springframework.thrift.crm;

import org.apache.thrift.TBase;
import org.apache.thrift.TDeserializer;
import org.apache.thrift.TSerializer;
import org.apache.thrift.protocol.TProtocol;

public class ReadAndWriter {
	 private Object read (Class<? extends TBase> clzz , TProtocol tProtocol ) throws Throwable {
		 TBase tBase =clzz.newInstance();
		 tBase.read(tProtocol);
		 return tBase;
	}

	void read2(Class<? extends TBase> clzz ){
		TDeserializer deserializer = new TDeserializer();
		TSerializer serializer = new TSerializer();
//		deserializer.
	}

	static public void main(String arg []) throws Throwable {
		Customer customer = new Customer() ;
//		customer.read( );
	}

}
