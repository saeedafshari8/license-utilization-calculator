package co.brtel.licenseutilizationcalculator.parsers;

import javax.xml.bind.JAXBException;

import co.brtel.licenseutilizationcalculator.commons.Serializer;
import co.brtel.licenseutilizationcalculator.pojo.Raml;

public class RamlSerializer {
	public static Raml deserialize(String xml) throws JAXBException{
		Serializer<Raml> serializer = new Serializer<Raml>(new Raml());
		return serializer.deserialize(xml);
	}
}
