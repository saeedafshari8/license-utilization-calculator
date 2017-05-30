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

	@Before
	public void setUp() throws IOException {
		oneRncutilizationSampleData = String.join(" ", Files.lines(new File(getClass().getResource("/ucap-sample.txt").getFile()).toPath()).collect(Collectors.toList()));
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

	@After
	public void tearDown() throws IOException {
		oneRncutilizationSampleData = null;
		parser = null;
	}
}