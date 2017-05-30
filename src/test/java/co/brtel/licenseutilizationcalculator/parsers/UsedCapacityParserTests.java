package co.brtel.licenseutilizationcalculator.parsers;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import co.brtel.licenseutilizationcalculator.parsers.FeatureCodeUtilizedCapacityParser;
import co.brtel.licenseutilizationcalculator.pojo.FeatureInformation;

public class UsedCapacityParserTests {
	private FeatureCodeUtilizedCapacityParser parser;
	String oneRncutilizationSampleData;
	String multipleRncUtilizationSampleData;

	@Before
	public void setUp() throws IOException {
		oneRncutilizationSampleData = String.join(" ", Files.lines(new File(getClass().getResource("/ucap-sample.txt").getFile()).toPath()).collect(Collectors.toList()));
		multipleRncUtilizationSampleData = String.join(" ",
				Files.lines(new File(getClass().getResource("/ucap-multiple-sample.txt").getFile()).toPath()).collect(Collectors.toList()));
		parser = new FeatureCodeUtilizedCapacityParser();
	}

	@Test
	public void testReadRncFeatureCodesUtilizations() {

		List<FeatureInformation> features = parser.readRncFeatureCodesUtilizations(oneRncutilizationSampleData);

		Assert.assertEquals(true, features.stream().allMatch(item -> item.getRnc().getName().equals("R862N") && item.getRnc().getCode().equals("402471")));

		Assert.assertEquals(true, features.stream().filter(item -> item.getCode().equals("958")).findFirst().get().getCapacity() == 757);
		Assert.assertEquals(true, features.stream().filter(item -> item.getCode().equals("959")).findFirst().get().getCapacity() == 871);
		Assert.assertEquals(true, features.stream().filter(item -> item.getCode().equals("960")).findFirst().get().getCapacity() == 635);
	}

	@Test
	public void testReadRncsFeatureCodesUtilizations() {

		Map<String, List<FeatureInformation>> rncFeaturesMap = parser.readRncsFeatureCodesUtilizations(multipleRncUtilizationSampleData);

		Assert.assertEquals(2, rncFeaturesMap.size());
		String rnc1Name = "R862N";
		String rnc2Name = "R841N";
		Assert.assertEquals(true, rncFeaturesMap.get(rnc1Name).stream().filter(item -> item.getCode().equals("958")).findFirst().get().getCapacity() == 757);
		Assert.assertEquals(true, rncFeaturesMap.get(rnc1Name).stream().filter(item -> item.getCode().equals("959")).findFirst().get().getCapacity() == 871);
		Assert.assertEquals(true, rncFeaturesMap.get(rnc1Name).stream().filter(item -> item.getCode().equals("960")).findFirst().get().getCapacity() == 635);
		
		Assert.assertEquals(true, rncFeaturesMap.get(rnc2Name).stream().filter(item -> item.getCode().equals("958")).findFirst().get().getCapacity() == 658);
		Assert.assertEquals(true, rncFeaturesMap.get(rnc2Name).stream().filter(item -> item.getCode().equals("959")).findFirst().get().getCapacity() == 757);
		Assert.assertEquals(true, rncFeaturesMap.get(rnc2Name).stream().filter(item -> item.getCode().equals("960")).findFirst().get().getCapacity() == 553);
	}

	@After
	public void tearDown() throws IOException {
		oneRncutilizationSampleData = null;
		parser = null;
	}
}