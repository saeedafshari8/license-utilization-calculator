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
	private String ramlMultipleManagedObjectSampleXml;

	@Before
	public void setUp() throws IOException {
		ramlSingleManagedObjectSampleXml = String.join("\r\n",
				Files.lines(new File(getClass().getResource("/singleRamlSampleXml.xml").getFile()).toPath()).collect(Collectors.toList()));
		ramlMultipleManagedObjectSampleXml = String.join("\r\n",
				Files.lines(new File(getClass().getResource("/multipleRamlSampleXml.xml").getFile()).toPath()).collect(Collectors.toList()));
	}

	@Test
	public void testRamlWithSingleManagedObjectDeserialization() throws JAXBException {
		Raml raml = RamlSerializer.deserialize(ramlSingleManagedObjectSampleXml);

		testRamlContent(raml);
	}

	@Test
	public void testRamlWithMultipleManagedObjectDeserialization() throws JAXBException {
		Raml raml = RamlSerializer.deserialize(ramlMultipleManagedObjectSampleXml);

		Assert.assertEquals(1950, raml.getCmData().getManagedObject().length);
		testRamlContent(raml);
	}

	private void testRamlContent(Raml raml) {
		Assert.assertEquals("RNC", raml.getCmData().getManagedObject()[0].getClassName());
		Assert.assertEquals("name", raml.getCmData().getManagedObject()[0].getParameters()[0].getName());
		Assert.assertEquals("DSCPStatisticsGroupC", raml.getCmData().getManagedObject()[0].getParameters()[10].getName());
		Assert.assertEquals("No measurement is made for value 255.", raml.getCmData().getManagedObject()[0].getParameters()[10].getValue());

		Assert.assertEquals("WCDMA - GSM Inter-System Handover", raml.getCmData().getManagedObject()[0].getRncOption().getParameters()[0].getValue());
		Assert.assertEquals("Throughput Based Optimisation of the Packet Scheduler Algorithm", raml.getCmData().getManagedObject()[0].getRncOption().getParameters()[10].getValue());
		Assert.assertEquals("16 kbit/s Return Channel DCH Data Rate Support for HSDPA", raml.getCmData().getManagedObject()[0].getRncOption().getParameters()[20].getValue());
	}
}