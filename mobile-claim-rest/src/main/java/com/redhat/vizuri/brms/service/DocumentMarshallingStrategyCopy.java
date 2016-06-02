package com.redhat.vizuri.brms.service;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.Date;
import java.util.Map;

import org.jbpm.document.Document;
import org.jbpm.document.marshalling.DocumentMarshallingStrategy;

public  class DocumentMarshallingStrategyCopy extends DocumentMarshallingStrategy{

	@Override
	public Document buildDocument(String name, long size, Date lastModified, Map<String, String> params) {
		
		try {
			return super.buildDocument(name, size, lastModified, params);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return null;
	}

	@Override
	public void write(ObjectOutputStream os, Object object) throws IOException {
		// TODO Auto-generated method stub
		try {
			super.write(os, object);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@Override
	public Object read(ObjectInputStream os) throws IOException, ClassNotFoundException {
		// TODO Auto-generated method stub
		return super.read(os);
	}

	@Override
	public byte[] marshal(Context context, ObjectOutputStream objectOutputStream, Object o) throws IOException {
		// TODO Auto-generated method stub
		return super.marshal(context, objectOutputStream, o);
	}

	@Override
	public Object unmarshal(Context context, ObjectInputStream objectInputStream, byte[] object,
			ClassLoader classLoader) throws IOException, ClassNotFoundException {
		// TODO Auto-generated method stub
		try {
			return super.unmarshal(context, objectInputStream, object, classLoader);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}

	@Override
	public Context createContext() {
		// TODO Auto-generated method stub
		return super.createContext();
	}
	
}

