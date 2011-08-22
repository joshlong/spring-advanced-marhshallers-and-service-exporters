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
