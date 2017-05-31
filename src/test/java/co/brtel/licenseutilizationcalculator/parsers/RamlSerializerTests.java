package co.brtel.licenseutilizationcalculator.parsers;

import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.nio.file.Files;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import javax.xml.bind.JAXBException;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import co.brtel.licenseutilizationcalculator.pojo.ManagedObject;
import co.brtel.licenseutilizationcalculator.pojo.Raml;

public class RamlSerializerTests {
	private String ramlSingleManagedObjectSampleXml;
	private String ramlMultipleManagedObjectSampleXml;
	private String ramlHugeNumberOfManagedObjectSampleXml1;
	private String ramlHugeNumberOfManagedObjectSampleXml2;

	@Before
	public void setUp() throws IOException {
		ramlSingleManagedObjectSampleXml = String.join("\r\n",
				Files.lines(new File(getClass().getResource("/singleRamlSampleXml.xml").getFile()).toPath()).collect(Collectors.toList()));
		ramlMultipleManagedObjectSampleXml = String.join("\r\n",
				Files.lines(new File(getClass().getResource("/multipleRamlSampleXml.xml").getFile()).toPath()).collect(Collectors.toList()));

		ramlHugeNumberOfManagedObjectSampleXml1 = getClass().getResource("/hugeDump1.xml").getFile();
		ramlHugeNumberOfManagedObjectSampleXml2 = getClass().getResource("/hugeDump2.xml").getFile();
	}

	@Test
	public void testRamlWithSingleManagedObjectDeserialization() throws JAXBException {
		Raml raml = RamlSerializer.deserialize(ramlSingleManagedObjectSampleXml);

		testRamlContent(raml);
	}

	@Test
	public void testRamlWithMultipleManagedObjectDeserialization() throws JAXBException {
		Raml raml = RamlSerializer.deserialize(ramlMultipleManagedObjectSampleXml);

		Assert.assertEquals(2, raml.getCmData().getManagedObject().length);
		testRamlContent(raml);
	}

	@Test
	public void testRamlWithHugeNumberOfManagedObjectDeserialization() throws JAXBException, UnsupportedEncodingException, IOException {
		List<ManagedObject> managedObjects = RamlSerializer.extractManagedObjects(Arrays.asList(ramlHugeNumberOfManagedObjectSampleXml1, ramlHugeNumberOfManagedObjectSampleXml2));
		long rnfcCount = managedObjects.stream().filter(item -> item.getClassName().equals("RNC")).count();
		long ipbrCount = managedObjects.stream().filter(item -> item.getClassName().equals("IPBR")).count();

		Assert.assertEquals(13, rnfcCount);
		Assert.assertEquals(62, ipbrCount);
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