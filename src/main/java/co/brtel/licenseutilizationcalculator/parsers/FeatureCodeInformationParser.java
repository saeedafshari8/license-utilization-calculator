package co.brtel.licenseutilizationcalculator.parsers;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import co.brtel.licenseutilizationcalculator.pojo.FeatureInformation;
import co.brtel.licenseutilizationcalculator.pojo.FeatureState;
import co.brtel.licenseutilizationcalculator.pojo.RNC;

public class FeatureCodeInformationParser {
	private static final String FEATURE_CODE_INFORMATION_MATCH_UNNECESSARY_TEXT = "FEATURE INFORMATION";
	private static final String FEATURE_CODE_INFORMATION_MATCH_PATTERN = "[\\W]*FEATURE CODE:[\\W]*(\\d*)[\\W]*FEATURE NAME:[\\W]*([a-zA-Z -/0-9_]*)[\\W]*FEATURE STATE:[\\W]*(ON|OFF)[\\W]*FEATURE CAPACITY:[\\W]*(\\d*)[\\W]*";

	private String removeUnnecessaryData(String data) {
		return data.split(FeatureCodeInformationParser.FEATURE_CODE_INFORMATION_MATCH_UNNECESSARY_TEXT)[1];
	}

	public List<FeatureInformation> readRncFeatureCodesInformations(String data) {
		RNC rnc = RNC.readRncInfo(data);

		List<FeatureInformation> features = new ArrayList<FeatureInformation>(3);
		String newData = removeUnnecessaryData(data);

		Pattern compiledPattern = Pattern.compile(FeatureCodeInformationParser.FEATURE_CODE_INFORMATION_MATCH_PATTERN);
		Matcher matcher = compiledPattern.matcher(newData);
		while (matcher.find()) {
			FeatureInformation fi = new FeatureInformation();
			fi.setCode(matcher.group(1));
			fi.setName(matcher.group(2));
			fi.setFeatureState(FeatureState.valueOf(matcher.group(3)));
			fi.setCapacity(Integer.parseInt(matcher.group(4)));
			fi.setRnc(rnc);
			features.add(fi);
		}
		return features;
	}
}