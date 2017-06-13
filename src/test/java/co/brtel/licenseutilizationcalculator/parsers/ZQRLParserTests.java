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
import co.brtel.licenseutilizationcalculator.pojo.FeatureState;

public class ZQRLParserTests {
	private ZQRLParser parser;
	String multipleZQRLSampleData;

	@Before
	public void setUp() throws IOException {
		multipleZQRLSampleData = String.join("\r\n", Files.lines(new File(getClass().getResource("/zqrl-multiple-sample.txt").getFile()).toPath()).collect(Collectors.toList()));
		parser = new ZQRLParser();
	}

	@Test
	public void testReadRNCsMUXStatus() {
		Map<String, Boolean> result = parser.readRNCsMUXStatus(multipleZQRLSampleData);

		Assert.assertEquals(true, result.get("R841N"));
		Assert.assertEquals(false, result.get("R862N"));
	}
}