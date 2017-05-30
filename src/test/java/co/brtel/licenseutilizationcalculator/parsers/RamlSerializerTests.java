package co.brtel.licenseutilizationcalculator.parsers;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.stream.Collectors;

import javax.xml.bind.JAXBException;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import co.brtel.licenseutilizationcalculator.commons.Serializer;
import co.brtel.licenseutilizationcalculator.pojo.Raml;

public class RamlSerializerTests {
	private String ramlSingleManagedObjectSampleXml;
	
	@Before
	public void setUp() throws IOException{
		ramlSingleManagedObjectSampleXml = String.join("\r\n", Files.lines(new File(getClass().getResource("/singleRamlSampleXml.xml").getFile()).toPath()).collect(Collectors.toList()));
	}
	
	@Test
	public void testRamlWithSingleManagedObjectDeserialization() throws JAXBException {
		Raml raml = RamlSerializer.deserialize(ramlSingleManagedObjectSampleXml);
		
		Assert.assertEquals("RNC", raml.getCmData().getManagedObject().getClassName());
		Assert.assertEquals("name", raml.getCmData().getManagedObject().getParameters()[0].getName());
		Assert.assertEquals("DSCPStatisticsGroupC", raml.getCmData().getManagedObject().getParameters()[10].getName());
		Assert.assertEquals("No measurement is made for value 255.", raml.getCmData().getManagedObject().getParameters()[10].getValue());
		
		Assert.assertEquals("WCDMA - GSM Inter-System Handover", raml.getCmData().getManagedObject().getRncOption().getParameters()[0].getValue());
		Assert.assertEquals("Throughput Based Optimisation of the Packet Scheduler Algorithm", raml.getCmData().getManagedObject().getRncOption().getParameters()[10].getValue());
		Assert.assertEquals("16 kbit/s Return Channel DCH Data Rate Support for HSDPA", raml.getCmData().getManagedObject().getRncOption().getParameters()[20].getValue());
	}
}