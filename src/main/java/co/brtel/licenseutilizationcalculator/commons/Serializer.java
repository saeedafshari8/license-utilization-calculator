package co.brtel.licenseutilizationcalculator.commons;

import java.io.StringReader;
import java.io.StringWriter;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;

public class Serializer<T> {
	T obj;
	public Serializer(T obj){
		this.obj = obj;
	}
	
	public T deserialize(String message) throws JAXBException
    {
		JAXBContext jaxbContext = JAXBContext.newInstance(obj.getClass());
    	Unmarshaller jaxbUnmarshaller = jaxbContext.createUnmarshaller();
    	
		T o = (T)jaxbUnmarshaller.unmarshal(new StringReader(message));
		return o;
    }

    public String serialize() throws JAXBException
    {
    	JAXBContext jaxbContext = JAXBContext.newInstance(obj.getClass());
    	Marshaller jaxbMarshaller = jaxbContext.createMarshaller();
    	jaxbMarshaller.setProperty( Marshaller.JAXB_FORMATTED_OUTPUT, true );
    	StringWriter sw = new StringWriter();
    	jaxbMarshaller.marshal( this.obj, sw );
    	return sw.toString();
    }
}
