package co.brtel.licenseutilizationcalculator.parsers;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.xml.bind.JAXBException;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import co.brtel.licenseutilizationcalculator.pojo.FeatureInformation;
import co.brtel.licenseutilizationcalculator.pojo.FeatureState;
import co.brtel.licenseutilizationcalculator.pojo.ManagedObject;

public class FeatureCodeInformationUtilizationCalculatorTests {
	private FeatureCodeInformationUtilizationCalculator utilizationCalculator;
	Map<String,List<FeatureInformation>> rncsFeaturesMap;
	String multipleRncFeatureCodesSampleData;
	
	@Before
	public void setUp() throws IOException, JAXBException {
		multipleRncFeatureCodesSampleData = String.join("\r\n", Files.lines(new File(getClass().getResource("/multiple-feature-code-information-sample2.txt").getFile()).toPath()).collect(Collectors.toList()));
		FeatureCodeInformationParser featureInformationParser = new FeatureCodeInformationParser();
		FeatureCodeUtilizedCapacityParser capacityParser = new FeatureCodeUtilizedCapacityParser();
		rncsFeaturesMap = featureInformationParser.readRncsFeatureCodesInformations(multipleRncFeatureCodesSampleData);
		Map<String, List<FeatureInformation>> rncsUtilizationFeaturesMap = capacityParser.readRncsFeatureCodesUtilizations(multipleRncFeatureCodesSampleData);
		for (String key : rncsUtilizationFeaturesMap.keySet()) {
			for (FeatureInformation fi : rncsUtilizationFeaturesMap.get(key)) {
				Optional<FeatureInformation> optFI = rncsFeaturesMap.get(key).stream().filter(item -> item.getCode().equals(fi.getCode())).findAny();
				if(optFI.isPresent()){
					optFI.get().setUtilization(fi.getUtilization());
				}
			}
		}
		List<ManagedObject> managedObjectsMap = RamlSerializer.extractManagedObjects(Arrays.asList(getClass().getResource("/hugeDump3.xml").getFile()));
		utilizationCalculator = new FeatureCodeInformationUtilizationCalculator(managedObjectsMap, rncsFeaturesMap.keySet());
	}
	
	@Test
	public void testCalculateUtilizationBasedOnRnfc() {
		for (String key : rncsFeaturesMap.keySet()) {
			utilizationCalculator.calculateUtilization(rncsFeaturesMap.get(key));
		}
		String rnc841Name = "R841N";
		Assert.assertEquals("2", rncsFeaturesMap.get(rnc841Name).stream().filter(item -> item.getCode().equals("630")).findAny().get().getUtilization());
		Assert.assertEquals("0", rncsFeaturesMap.get(rnc841Name).stream().filter(item -> item.getCode().equals("885")).findAny().get().getUtilization());
		Assert.assertEquals("2", rncsFeaturesMap.get(rnc841Name).stream().filter(item -> item.getCode().equals("967")).findAny().get().getUtilization());
		Assert.assertEquals("0", rncsFeaturesMap.get(rnc841Name).stream().filter(item -> item.getCode().equals("968")).findAny().get().getUtilization());
		Assert.assertEquals("2", rncsFeaturesMap.get(rnc841Name).stream().filter(item -> item.getCode().equals("975")).findAny().get().getUtilization());
		Assert.assertEquals("2", rncsFeaturesMap.get(rnc841Name).stream().filter(item -> item.getCode().equals("1060")).findAny().get().getUtilization());
		Assert.assertEquals("2", rncsFeaturesMap.get(rnc841Name).stream().filter(item -> item.getCode().equals("1061")).findAny().get().getUtilization());
		Assert.assertEquals("0", rncsFeaturesMap.get(rnc841Name).stream().filter(item -> item.getCode().equals("1062")).findAny().get().getUtilization());
		Assert.assertEquals("1", rncsFeaturesMap.get(rnc841Name).stream().filter(item -> item.getCode().equals("1686")).findAny().get().getUtilization());
		Assert.assertEquals("1", rncsFeaturesMap.get(rnc841Name).stream().filter(item -> item.getCode().equals("1690")).findAny().get().getUtilization());
		Assert.assertEquals("2", rncsFeaturesMap.get(rnc841Name).stream().filter(item -> item.getCode().equals("3384")).findAny().get().getUtilization());
		Assert.assertEquals("0", rncsFeaturesMap.get(rnc841Name).stream().filter(item -> item.getCode().equals("4290")).findAny().get().getUtilization());
		Assert.assertEquals("0", rncsFeaturesMap.get(rnc841Name).stream().filter(item -> item.getCode().equals("4293")).findAny().get().getUtilization());
		Assert.assertEquals("0", rncsFeaturesMap.get(rnc841Name).stream().filter(item -> item.getCode().equals("4348")).findAny().get().getUtilization());
		Assert.assertEquals("0", rncsFeaturesMap.get(rnc841Name).stream().filter(item -> item.getCode().equals("4780")).findAny().get().getUtilization());
	}
}
