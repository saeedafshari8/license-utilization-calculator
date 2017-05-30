package co.brtel.licenseutilizationcalculator.pojo;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.stream.Collectors;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class RNCTests {
	String rncInUcapSample;
	String rncInFeatureCodeSample;
	
	@Before
	public void setUp() throws IOException {
		rncInUcapSample = String.join("\r\n", Files.lines(new File(getClass().getResource("/ucap-sample.txt").getFile()).toPath()).collect(Collectors.toList()));
		rncInFeatureCodeSample = String.join("\r\n", Files.lines(new File(getClass().getResource("/feature-code-information-sample.txt").getFile()).toPath()).collect(Collectors.toList()));
	}
	
	@Test
	public void testReadRncFeatureCodesInformations() {
		RNC rnc = RNC.readRncInfo(rncInUcapSample);
		Assert.assertEquals("402471", rnc.getCode());
		Assert.assertEquals("R862N", rnc.getName());
		
		rnc = RNC.readRncInfo(rncInFeatureCodeSample);
		Assert.assertEquals("402471", rnc.getCode());
		Assert.assertEquals("R862N", rnc.getName());
	}
}
