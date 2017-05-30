package co.brtel.licenseutilizationcalculator.parsers;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import co.brtel.licenseutilizationcalculator.pojo.FeatureInformation;
import co.brtel.licenseutilizationcalculator.pojo.FeatureState;

public class FeatureCodeInformationParserTests {
	private FeatureCodeInformationParser parser;
	String oneRncFeatureCodesSampleData;
	String multipleRncFeatureCodesSampleData;
	
	@Before
	public void setUp() throws IOException {
		oneRncFeatureCodesSampleData = String.join("\r\n", Files.lines(new File(getClass().getResource("/feature-code-information-sample.txt").getFile()).toPath()).collect(Collectors.toList()));
		multipleRncFeatureCodesSampleData = String.join("\r\n", Files.lines(new File(getClass().getResource("/multiple-feature-code-information-sample.txt").getFile()).toPath()).collect(Collectors.toList()));
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
	
	@Test
	public void testReadRncsFeatureCodesInformations() {

		Map<String, List<FeatureInformation>> rncFeaturesMap = parser.readRncsFeatureCodesInformations(multipleRncFeatureCodesSampleData);

		Assert.assertEquals(2, rncFeaturesMap.size());
		String rnc1Name = "R862N";
		String rnc2Name = "R841N";
		
		FeatureInformation featureInformation975 = rncFeaturesMap.get(rnc1Name).stream().filter(item -> item.getCode().equals("975")).findFirst().get();
		Assert.assertEquals("Subscriber Trace", featureInformation975.getName());
		Assert.assertEquals(980, featureInformation975.getCapacity());
		Assert.assertEquals(FeatureState.ON, featureInformation975.getFeatureState());
		
		FeatureInformation featureInformation4780 = rncFeaturesMap.get(rnc2Name).stream().filter(item -> item.getCode().equals("4780")).findFirst().get();
		Assert.assertEquals("RRC Connection Setup Redirection", featureInformation4780.getName());
		Assert.assertEquals(600, featureInformation4780.getCapacity());
		Assert.assertEquals(FeatureState.ON, featureInformation4780.getFeatureState());
	}
}
