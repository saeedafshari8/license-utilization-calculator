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

import co.brtel.licenseutilizationcalculator.pojo.CapacityUnit;
import co.brtel.licenseutilizationcalculator.pojo.FeatureInformation;
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
		Assert.assertEquals("2", rncsFeaturesMap.get(rnc841Name).stream().filter(item -> item.getCode().equals("968")).findAny().get().getUtilization());
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
	
	@Test
	public void testCalculateUtilizationBasedOnWcell() {
		for (String key : rncsFeaturesMap.keySet()) {
			utilizationCalculator.calculateUtilization(rncsFeaturesMap.get(key));
		}
		String rnc841Name = "R841N";
		Assert.assertEquals("2", rncsFeaturesMap.get(rnc841Name).stream().filter(item -> item.getCode().equals("638")).findAny().get().getUtilization());
		Assert.assertEquals("1", rncsFeaturesMap.get(rnc841Name).stream().filter(item -> item.getCode().equals("961")).findAny().get().getUtilization());
		Assert.assertEquals("1", rncsFeaturesMap.get(rnc841Name).stream().filter(item -> item.getCode().equals("962")).findAny().get().getUtilization());
		Assert.assertEquals("2", rncsFeaturesMap.get(rnc841Name).stream().filter(item -> item.getCode().equals("966")).findAny().get().getUtilization());
		Assert.assertEquals("1", rncsFeaturesMap.get(rnc841Name).stream().filter(item -> item.getCode().equals("1086")).findAny().get().getUtilization());
		Assert.assertEquals("1", rncsFeaturesMap.get(rnc841Name).stream().filter(item -> item.getCode().equals("1087")).findAny().get().getUtilization());
		Assert.assertEquals("1", rncsFeaturesMap.get(rnc841Name).stream().filter(item -> item.getCode().equals("1089")).findAny().get().getUtilization());
		Assert.assertEquals("1", rncsFeaturesMap.get(rnc841Name).stream().filter(item -> item.getCode().equals("1091")).findAny().get().getUtilization());
		Assert.assertEquals("1", rncsFeaturesMap.get(rnc841Name).stream().filter(item -> item.getCode().equals("1471")).findAny().get().getUtilization());
		Assert.assertEquals("1", rncsFeaturesMap.get(rnc841Name).stream().filter(item -> item.getCode().equals("1475")).findAny().get().getUtilization());
		Assert.assertEquals("1", rncsFeaturesMap.get(rnc841Name).stream().filter(item -> item.getCode().equals("1476")).findAny().get().getUtilization());
		Assert.assertEquals("1", rncsFeaturesMap.get(rnc841Name).stream().filter(item -> item.getCode().equals("1485")).findAny().get().getUtilization());
		Assert.assertEquals("1", rncsFeaturesMap.get(rnc841Name).stream().filter(item -> item.getCode().equals("1497")).findAny().get().getUtilization());
		Assert.assertEquals("1", rncsFeaturesMap.get(rnc841Name).stream().filter(item -> item.getCode().equals("1755")).findAny().get().getUtilization());
		Assert.assertEquals("1", rncsFeaturesMap.get(rnc841Name).stream().filter(item -> item.getCode().equals("1756")).findAny().get().getUtilization());
		Assert.assertEquals("1", rncsFeaturesMap.get(rnc841Name).stream().filter(item -> item.getCode().equals("1795")).findAny().get().getUtilization());
		Assert.assertEquals("1", rncsFeaturesMap.get(rnc841Name).stream().filter(item -> item.getCode().equals("1798")).findAny().get().getUtilization());
		Assert.assertEquals("1", rncsFeaturesMap.get(rnc841Name).stream().filter(item -> item.getCode().equals("3414")).findAny().get().getUtilization());
		Assert.assertEquals("1", rncsFeaturesMap.get(rnc841Name).stream().filter(item -> item.getCode().equals("3420")).findAny().get().getUtilization());
		Assert.assertEquals("1", rncsFeaturesMap.get(rnc841Name).stream().filter(item -> item.getCode().equals("3422")).findAny().get().getUtilization());
		Assert.assertEquals("1", rncsFeaturesMap.get(rnc841Name).stream().filter(item -> item.getCode().equals("3898")).findAny().get().getUtilization());
		Assert.assertEquals("1", rncsFeaturesMap.get(rnc841Name).stream().filter(item -> item.getCode().equals("3903")).findAny().get().getUtilization());
		Assert.assertEquals("1", rncsFeaturesMap.get(rnc841Name).stream().filter(item -> item.getCode().equals("4160")).findAny().get().getUtilization());
		Assert.assertEquals("1", rncsFeaturesMap.get(rnc841Name).stream().filter(item -> item.getCode().equals("4492")).findAny().get().getUtilization());
		Assert.assertEquals("0", rncsFeaturesMap.get(rnc841Name).stream().filter(item -> item.getCode().equals("4504")).findAny().get().getUtilization());
		Assert.assertEquals("1", rncsFeaturesMap.get(rnc841Name).stream().filter(item -> item.getCode().equals("4533")).findAny().get().getUtilization());
		Assert.assertEquals("1", rncsFeaturesMap.get(rnc841Name).stream().filter(item -> item.getCode().equals("4538")).findAny().get().getUtilization());
		Assert.assertEquals("1", rncsFeaturesMap.get(rnc841Name).stream().filter(item -> item.getCode().equals("4545")).findAny().get().getUtilization());
		Assert.assertEquals("1", rncsFeaturesMap.get(rnc841Name).stream().filter(item -> item.getCode().equals("4839")).findAny().get().getUtilization());
	}
	
	@Test
	public void testCalculateUtilizationForNotObjectType() {
		for (String key : rncsFeaturesMap.keySet()) {
			utilizationCalculator.calculateUtilization(rncsFeaturesMap.get(key));
		}
		String rnc841Name = "R841N";
		Assert.assertEquals("2", rncsFeaturesMap.get(rnc841Name).stream().filter(item -> item.getCode().equals("619")).findAny().get().getUtilization());
		Assert.assertEquals(CapacityUnit.DYNAMIC_CELL.toString(), rncsFeaturesMap.get(rnc841Name).stream().filter(item -> item.getCode().equals("620")).findAny().get().getUtilization());
		Assert.assertEquals(CapacityUnit.DYNAMIC_CELL.toString(), rncsFeaturesMap.get(rnc841Name).stream().filter(item -> item.getCode().equals("621")).findAny().get().getUtilization());
		Assert.assertEquals(CapacityUnit.DYNAMIC_CELL.toString(), rncsFeaturesMap.get(rnc841Name).stream().filter(item -> item.getCode().equals("622")).findAny().get().getUtilization());
		Assert.assertEquals(CapacityUnit.DYNAMIC_CELL.toString(), rncsFeaturesMap.get(rnc841Name).stream().filter(item -> item.getCode().equals("623")).findAny().get().getUtilization());
		Assert.assertEquals("2", rncsFeaturesMap.get(rnc841Name).stream().filter(item -> item.getCode().equals("625")).findAny().get().getUtilization());
		Assert.assertEquals("0", rncsFeaturesMap.get(rnc841Name).stream().filter(item -> item.getCode().equals("969")).findAny().get().getUtilization());
		Assert.assertEquals("2", rncsFeaturesMap.get(rnc841Name).stream().filter(item -> item.getCode().equals("974")).findAny().get().getUtilization());
		Assert.assertEquals("1", rncsFeaturesMap.get(rnc841Name).stream().filter(item -> item.getCode().equals("1029")).findAny().get().getUtilization());
		Assert.assertEquals("2", rncsFeaturesMap.get(rnc841Name).stream().filter(item -> item.getCode().equals("1030")).findAny().get().getUtilization());
		Assert.assertEquals("0", rncsFeaturesMap.get(rnc841Name).stream().filter(item -> item.getCode().equals("1057")).findAny().get().getUtilization());
		Assert.assertEquals("1", rncsFeaturesMap.get(rnc841Name).stream().filter(item -> item.getCode().equals("1079")).findAny().get().getUtilization());
		Assert.assertEquals("2", rncsFeaturesMap.get(rnc841Name).stream().filter(item -> item.getCode().equals("1107")).findAny().get().getUtilization());
		Assert.assertEquals(CapacityUnit.ON_OFF.toString(), rncsFeaturesMap.get(rnc841Name).stream().filter(item -> item.getCode().equals("1108")).findAny().get().getUtilization());
		Assert.assertEquals("2", rncsFeaturesMap.get(rnc841Name).stream().filter(item -> item.getCode().equals("1110")).findAny().get().getUtilization());
		Assert.assertEquals(FeatureCodeInformationUtilizationCalculator.NOT_USED_IN_NETWORK, rncsFeaturesMap.get(rnc841Name).stream().filter(item -> item.getCode().equals("1246")).findAny().get().getUtilization());
		Assert.assertEquals(CapacityUnit.ON_OFF.toString(), rncsFeaturesMap.get(rnc841Name).stream().filter(item -> item.getCode().equals("1305")).findAny().get().getUtilization());
		Assert.assertEquals(FeatureCodeInformationUtilizationCalculator.NA, rncsFeaturesMap.get(rnc841Name).stream().filter(item -> item.getCode().equals("1435")).findAny().get().getUtilization());
		Assert.assertEquals(CapacityUnit.DYNAMIC_CELL.toString(), rncsFeaturesMap.get(rnc841Name).stream().filter(item -> item.getCode().equals("1478")).findAny().get().getUtilization());
		Assert.assertEquals("2", rncsFeaturesMap.get(rnc841Name).stream().filter(item -> item.getCode().equals("1490")).findAny().get().getUtilization());
		Assert.assertEquals("2", rncsFeaturesMap.get(rnc841Name).stream().filter(item -> item.getCode().equals("1683")).findAny().get().getUtilization());
		Assert.assertEquals(CapacityUnit.DYNAMIC_CELL.toString(), rncsFeaturesMap.get(rnc841Name).stream().filter(item -> item.getCode().equals("1796")).findAny().get().getUtilization());
		Assert.assertEquals("2", rncsFeaturesMap.get(rnc841Name).stream().filter(item -> item.getCode().equals("1897")).findAny().get().getUtilization());
		Assert.assertEquals(FeatureCodeInformationUtilizationCalculator.NOT_USED_IN_NETWORK, rncsFeaturesMap.get(rnc841Name).stream().filter(item -> item.getCode().equals("1898")).findAny().get().getUtilization());
		Assert.assertEquals("0", rncsFeaturesMap.get(rnc841Name).stream().filter(item -> item.getCode().equals("1938")).findAny().get().getUtilization());
		Assert.assertEquals("2", rncsFeaturesMap.get(rnc841Name).stream().filter(item -> item.getCode().equals("2117")).findAny().get().getUtilization());
	}
	
	@Test
	public void testCalculateUtilizationBasedOnWbts() {
		for (String key : rncsFeaturesMap.keySet()) {
			utilizationCalculator.calculateUtilization(rncsFeaturesMap.get(key));
		}
		String rnc841Name = "R841N";
		Assert.assertEquals("1", rncsFeaturesMap.get(rnc841Name).stream().filter(item -> item.getCode().equals("641")).findAny().get().getUtilization());
		Assert.assertEquals("1", rncsFeaturesMap.get(rnc841Name).stream().filter(item -> item.getCode().equals("1085")).findAny().get().getUtilization());
		Assert.assertEquals("1", rncsFeaturesMap.get(rnc841Name).stream().filter(item -> item.getCode().equals("1088")).findAny().get().getUtilization());
		Assert.assertEquals("1", rncsFeaturesMap.get(rnc841Name).stream().filter(item -> item.getCode().equals("1093")).findAny().get().getUtilization());
		Assert.assertEquals("1", rncsFeaturesMap.get(rnc841Name).stream().filter(item -> item.getCode().equals("1303")).findAny().get().getUtilization());
		Assert.assertEquals("2", rncsFeaturesMap.get(rnc841Name).stream().filter(item -> item.getCode().equals("4783")).findAny().get().getUtilization());
		Assert.assertEquals("2", rncsFeaturesMap.get(rnc841Name).stream().filter(item -> item.getCode().equals("633")).findAny().get().getUtilization());
		Assert.assertEquals("2", rncsFeaturesMap.get(rnc841Name).stream().filter(item -> item.getCode().equals("1092")).findAny().get().getUtilization());
		Assert.assertEquals("1", rncsFeaturesMap.get(rnc841Name).stream().filter(item -> item.getCode().equals("1247")).findAny().get().getUtilization());
		Assert.assertEquals("2", rncsFeaturesMap.get(rnc841Name).stream().filter(item -> item.getCode().equals("1279")).findAny().get().getUtilization());
	}
	
	@Test
	public void testCalculateUtilizationBasedOnRNHSPA() {
		for (String key : rncsFeaturesMap.keySet()) {
			utilizationCalculator.calculateUtilization(rncsFeaturesMap.get(key));
		}
		String rnc841Name = "R841N";
		Assert.assertEquals("2", rncsFeaturesMap.get(rnc841Name).stream().filter(item -> item.getCode().equals("1028")).findAny().get().getUtilization());
	}
	
	@Test
	public void testCalculateUtilizationBasedOnFMCG() {
		for (String key : rncsFeaturesMap.keySet()) {
			utilizationCalculator.calculateUtilization(rncsFeaturesMap.get(key));
		}
		String rnc841Name = "R841N";
		Assert.assertEquals("1", rncsFeaturesMap.get(rnc841Name).stream().filter(item -> item.getCode().equals("1302")).findAny().get().getUtilization());
	}
	
	@Test
	public void testCalculateUtilizationBasedOnRNMOBI() {
		for (String key : rncsFeaturesMap.keySet()) {
			utilizationCalculator.calculateUtilization(rncsFeaturesMap.get(key));
		}
		String rnc841Name = "R841N";
		Assert.assertEquals("2", rncsFeaturesMap.get(rnc841Name).stream().filter(item -> item.getCode().equals("628")).findAny().get().getUtilization());
	}
	
	@Test
	public void testCalculateUtilizationBasedOnFMCS() {
		for (String key : rncsFeaturesMap.keySet()) {
			utilizationCalculator.calculateUtilization(rncsFeaturesMap.get(key));
		}
		String rnc841Name = "R841N";
		Assert.assertEquals("2", rncsFeaturesMap.get(rnc841Name).stream().filter(item -> item.getCode().equals("1080")).findAny().get().getUtilization());
		Assert.assertEquals("2", rncsFeaturesMap.get(rnc841Name).stream().filter(item -> item.getCode().equals("1109")).findAny().get().getUtilization());
	}
}