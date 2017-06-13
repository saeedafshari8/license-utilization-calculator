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
import co.brtel.licenseutilizationcalculator.pojo.ManagedObjectType;

public class FeatureCodeInformationUtilizationCalculatorTests {
	private FeatureCodeInformationUtilizationCalculator utilizationCalculator;
	Map<String, List<FeatureInformation>> rncsFeaturesMap;
	String multipleRncFeatureCodesSampleData;
	String multipleZQRLSampleData;

	@Before
	public void setUp() throws IOException, JAXBException {
		multipleRncFeatureCodesSampleData = String.join("\r\n", Files.lines(new File(getClass().getResource("/multiple-feature-code-information-sample2.txt").getFile()).toPath())
				.collect(Collectors.toList()));
		multipleZQRLSampleData = String.join("\r\n", Files.lines(new File(getClass().getResource("/zqrl-multiple-sample.txt").getFile()).toPath()).collect(Collectors.toList()));
		
		FeatureCodeInformationParser featureInformationParser = new FeatureCodeInformationParser();
		FeatureCodeUtilizedCapacityParser capacityParser = new FeatureCodeUtilizedCapacityParser();
		rncsFeaturesMap = featureInformationParser.readRncsFeatureCodesInformations(multipleRncFeatureCodesSampleData);
		Map<String, List<FeatureInformation>> rncsUtilizationFeaturesMap = capacityParser.readRncsFeatureCodesUtilizations(multipleRncFeatureCodesSampleData);
		for (String key : rncsUtilizationFeaturesMap.keySet()) {
			for (FeatureInformation fi : rncsUtilizationFeaturesMap.get(key)) {
				Optional<FeatureInformation> optFI = rncsFeaturesMap.get(key).stream().filter(item -> item.getCode().equals(fi.getCode())).findAny();
				if (optFI.isPresent()) {
					optFI.get().setUtilization(fi.getUtilization());
				}
			}
		}
		List<ManagedObject> managedObjectsMap = RamlSerializer.extractManagedObjects(Arrays.asList(getClass().getResource("/hugeDump3.xml").getFile()));
		utilizationCalculator = new FeatureCodeInformationUtilizationCalculator(managedObjectsMap, rncsFeaturesMap.keySet());
	}

	private String getUtilization(String rncName, String featureCode) {
		return rncsFeaturesMap.get(rncName).stream().filter(item -> item.getCode().equals(featureCode)).findAny().get().getUtilization();
	}

	@Test
	public void testCalculateUtilizationBasedOnRnfc() {
		for (String key : rncsFeaturesMap.keySet()) {
			utilizationCalculator.calculateUtilization(rncsFeaturesMap.get(key));
		}
		String rnc841Name = "R841N";
		Assert.assertEquals("2", getUtilization(rnc841Name, "630"));
		Assert.assertEquals("0", getUtilization(rnc841Name, "885"));
		Assert.assertEquals("2", getUtilization(rnc841Name, "967"));
		Assert.assertEquals("2", getUtilization(rnc841Name, "968"));
		Assert.assertEquals("2", getUtilization(rnc841Name, "975"));
		Assert.assertEquals("2", getUtilization(rnc841Name, "1060"));
		Assert.assertEquals("2", getUtilization(rnc841Name, "1061"));
		Assert.assertEquals("0", getUtilization(rnc841Name, "1062"));
		Assert.assertEquals("1", getUtilization(rnc841Name, "1686"));
		Assert.assertEquals("1", getUtilization(rnc841Name, "1690"));
		Assert.assertEquals("2", getUtilization(rnc841Name, "3384"));
		Assert.assertEquals("0", getUtilization(rnc841Name, "4290"));
		Assert.assertEquals("0", getUtilization(rnc841Name, "4293"));
		Assert.assertEquals("0", getUtilization(rnc841Name, "4348"));
		Assert.assertEquals("0", getUtilization(rnc841Name, "4780"));
	}

	@Test
	public void testCalculateUtilizationBasedOnWcell() {
		for (String key : rncsFeaturesMap.keySet()) {
			utilizationCalculator.calculateUtilization(rncsFeaturesMap.get(key));
		}
		String rnc841Name = "R841N";
		Assert.assertEquals("2", getUtilization(rnc841Name, "638"));
		Assert.assertEquals("1", getUtilization(rnc841Name, "961"));
		Assert.assertEquals("1", getUtilization(rnc841Name, "962"));
		Assert.assertEquals("2", getUtilization(rnc841Name, "966"));
		Assert.assertEquals("1", getUtilization(rnc841Name, "1086"));
		Assert.assertEquals("1", getUtilization(rnc841Name, "1087"));
		Assert.assertEquals("1", getUtilization(rnc841Name, "1089"));
		Assert.assertEquals("1", getUtilization(rnc841Name, "1091"));
		Assert.assertEquals("1", getUtilization(rnc841Name, "1471"));
		Assert.assertEquals("1", getUtilization(rnc841Name, "1475"));
		Assert.assertEquals("1", getUtilization(rnc841Name, "1476"));
		Assert.assertEquals("1", getUtilization(rnc841Name, "1485"));
		Assert.assertEquals("1", getUtilization(rnc841Name, "1497"));
		Assert.assertEquals("1", getUtilization(rnc841Name, "1755"));
		Assert.assertEquals("1", getUtilization(rnc841Name, "1756"));
		Assert.assertEquals("1", getUtilization(rnc841Name, "1795"));
		Assert.assertEquals("1", getUtilization(rnc841Name, "1798"));
		Assert.assertEquals("1", getUtilization(rnc841Name, "3414"));
		Assert.assertEquals("1", getUtilization(rnc841Name, "3420"));
		Assert.assertEquals("1", getUtilization(rnc841Name, "3422"));
		Assert.assertEquals("1", getUtilization(rnc841Name, "3898"));
		Assert.assertEquals("1", getUtilization(rnc841Name, "3903"));
		Assert.assertEquals("1", getUtilization(rnc841Name, "4160"));
		Assert.assertEquals("1", getUtilization(rnc841Name, "4492"));
		Assert.assertEquals("0", getUtilization(rnc841Name, "4504"));
		Assert.assertEquals("1", getUtilization(rnc841Name, "4533"));
		Assert.assertEquals("1", getUtilization(rnc841Name, "4538"));
		Assert.assertEquals("1", getUtilization(rnc841Name, "4545"));
		Assert.assertEquals("1", getUtilization(rnc841Name, "4839"));
		Assert.assertEquals("2", getUtilization(rnc841Name, "1754"));
	}

	@Test
	public void testCalculateUtilizationForNotObjectType() {
		for (String key : rncsFeaturesMap.keySet()) {
			utilizationCalculator.calculateUtilization(rncsFeaturesMap.get(key));
		}
		String rnc841Name = "R841N";
		Assert.assertEquals("2", getUtilization(rnc841Name, "619"));
		Assert.assertEquals(CapacityUnit.DYNAMIC_CELL.toString(), getUtilization(rnc841Name, "620"));
		Assert.assertEquals(CapacityUnit.DYNAMIC_CELL.toString(), getUtilization(rnc841Name, "621"));
		Assert.assertEquals(CapacityUnit.DYNAMIC_CELL.toString(), getUtilization(rnc841Name, "622"));
		Assert.assertEquals(CapacityUnit.DYNAMIC_CELL.toString(), getUtilization(rnc841Name, "623"));
		Assert.assertEquals("2", getUtilization(rnc841Name, "625"));
		Assert.assertEquals("0", getUtilization(rnc841Name, "969"));
		Assert.assertEquals("2", getUtilization(rnc841Name, "974"));
		Assert.assertEquals("1", getUtilization(rnc841Name, "1029"));
		Assert.assertEquals("2", getUtilization(rnc841Name, "1030"));
		Assert.assertEquals("0", getUtilization(rnc841Name, "1057"));
		Assert.assertEquals("2", getUtilization(rnc841Name, "1069"));
		Assert.assertEquals("2", getUtilization(rnc841Name, "1070"));
		Assert.assertEquals("2", getUtilization(rnc841Name, "1071"));
		Assert.assertEquals("1", getUtilization(rnc841Name, "1079"));
		Assert.assertEquals("2", getUtilization(rnc841Name, "1107"));
		Assert.assertEquals(CapacityUnit.ON_OFF.toString(), getUtilization(rnc841Name, "1108"));
		Assert.assertEquals("2", getUtilization(rnc841Name, "1110"));
		Assert.assertEquals(FeatureCodeInformationUtilizationCalculator.NOT_USED_IN_NETWORK, getUtilization(rnc841Name, "1246"));
		Assert.assertEquals(CapacityUnit.ON_OFF.toString(), getUtilization(rnc841Name, "1305"));
		Assert.assertEquals(FeatureCodeInformationUtilizationCalculator.NA, getUtilization(rnc841Name, "1435"));
		Assert.assertEquals(CapacityUnit.DYNAMIC_CELL.toString(), getUtilization(rnc841Name, "1478"));
		Assert.assertEquals("2", getUtilization(rnc841Name, "1490"));
		Assert.assertEquals("2", getUtilization(rnc841Name, "1683"));
		Assert.assertEquals(CapacityUnit.DYNAMIC_CELL.toString(), getUtilization(rnc841Name, "1796"));
		Assert.assertEquals("2", getUtilization(rnc841Name, "1897"));
		Assert.assertEquals(FeatureCodeInformationUtilizationCalculator.NOT_USED_IN_NETWORK, getUtilization(rnc841Name, "1898"));
		Assert.assertEquals("1", getUtilization(rnc841Name, "1938"));
		Assert.assertEquals("2", getUtilization(rnc841Name, "2117"));
	}

	@Test
	public void testCalculateUtilizationBasedOnWbts() {
		for (String key : rncsFeaturesMap.keySet()) {
			utilizationCalculator.calculateUtilization(rncsFeaturesMap.get(key));
		}
		String rnc841Name = "R841N";
		Assert.assertEquals("1", getUtilization(rnc841Name, "641"));
		Assert.assertEquals("1", getUtilization(rnc841Name, "1085"));
		Assert.assertEquals("1", getUtilization(rnc841Name, "1088"));
		Assert.assertEquals("1", getUtilization(rnc841Name, "1093"));
		Assert.assertEquals("1", getUtilization(rnc841Name, "1303"));
		Assert.assertEquals("2", getUtilization(rnc841Name, "4783"));
		Assert.assertEquals("2", getUtilization(rnc841Name, "633"));
		Assert.assertEquals("2", getUtilization(rnc841Name, "1092"));
		Assert.assertEquals("1", getUtilization(rnc841Name, "1247"));
		Assert.assertEquals("2", getUtilization(rnc841Name, "1279"));
	}

	@Test
	public void testCalculateUtilizationBasedOnRNHSPA() {
		for (String key : rncsFeaturesMap.keySet()) {
			utilizationCalculator.calculateUtilization(rncsFeaturesMap.get(key));
		}
		String rnc841Name = "R841N";
		Assert.assertEquals("2", getUtilization(rnc841Name, "1028"));
	}

	@Test
	public void testCalculateUtilizationBasedOnFMCG() {
		for (String key : rncsFeaturesMap.keySet()) {
			utilizationCalculator.calculateUtilization(rncsFeaturesMap.get(key));
		}
		String rnc841Name = "R841N";
		Assert.assertEquals("1", getUtilization(rnc841Name, "1302"));
	}

	@Test
	public void testCalculateUtilizationBasedOnRNMOBI() {
		for (String key : rncsFeaturesMap.keySet()) {
			utilizationCalculator.calculateUtilization(rncsFeaturesMap.get(key));
		}
		String rnc841Name = "R841N";
		Assert.assertEquals("2", getUtilization(rnc841Name, "628"));
	}

	@Test
	public void testCalculateUtilizationBasedOnFMCS() {
		for (String key : rncsFeaturesMap.keySet()) {
			utilizationCalculator.calculateUtilization(rncsFeaturesMap.get(key));
		}
		String rnc841Name = "R841N";
		Assert.assertEquals("2", getUtilization(rnc841Name, "1080"));
		Assert.assertEquals("2", getUtilization(rnc841Name, "1109"));
	}

	@Test
	public void testCalculateUtilizationBasedOnRNC() {
		for (String key : rncsFeaturesMap.keySet()) {
			utilizationCalculator.calculateUtilization(rncsFeaturesMap.get(key));
		}
		String rnc841Name = "R841N";
		Assert.assertEquals("2", getUtilization(rnc841Name, "1375"));
		Assert.assertEquals("2", getUtilization(rnc841Name, "1434"));
	}
	
	@Test
	public void testCalculateUtilizationBasedOnZQRL() {
		ZQRLParser zqrlParser = new ZQRLParser();
		Map<String, Boolean> result = zqrlParser.readRNCsMUXStatus(multipleZQRLSampleData);
		
		for (String key : rncsFeaturesMap.keySet()) {
			for (FeatureInformation fi : rncsFeaturesMap.get(key).stream().filter(item -> item.getManagedObjectType() == ManagedObjectType.COMMAND_ZQRL).collect(Collectors.toList())) {
				fi.getRnc().setMuxEnable(result.get(fi.getRnc().getName()));
			}
			utilizationCalculator.calculateUtilization(rncsFeaturesMap.get(key));
		}
		String rnc841Name = "R841N";
		Assert.assertEquals("2", getUtilization(rnc841Name, "1375"));
	}
}