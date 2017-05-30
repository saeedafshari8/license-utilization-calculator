package co.brtel.licenseutilizationcalculator.parsers;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.List;
import java.util.stream.Collectors;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import co.brtel.licenseutilizationcalculator.pojo.FeatureInformation;
import co.brtel.licenseutilizationcalculator.pojo.FeatureState;

public class FeatureCodeInformationParserTests {
	private FeatureCodeInformationParser parser;
	String oneRncFeatureCodesSampleData;
	
	@Before
	public void setUp() throws IOException {
		oneRncFeatureCodesSampleData = String.join("\r\n", Files.lines(new File(getClass().getResource("/feature-code-information-sample.txt").getFile()).toPath()).collect(Collectors.toList()));
		parser = new FeatureCodeInformationParser();
	}
	
	@Test
	public void testReadRncFeatureCodesInformations() {

		List<FeatureInformation> features = parser.readRncFeatureCodesInformations(oneRncFeatureCodesSampleData);

		Assert.assertEquals(98, features.size());

		FeatureInformation featureCode1475 = features.stream().filter(item -> item.getCode().equals("1475")).findFirst().get();
		Assert.assertEquals(980, featureCode1475.getCapacity());
		Assert.assertEquals("HSUPA 2 ms TTI", featureCode1475.getName());
		Assert.assertEquals(FeatureState.ON, featureCode1475.getFeatureState());
		
		FeatureInformation featureCode1490 = features.stream().filter(item -> item.getCode().equals("1490")).findFirst().get();
		Assert.assertEquals(980, featureCode1490.getCapacity());
		Assert.assertEquals("Channel Element Utilization in WBTS", featureCode1490.getName());
		Assert.assertEquals(FeatureState.ON, featureCode1490.getFeatureState());
	}
}
