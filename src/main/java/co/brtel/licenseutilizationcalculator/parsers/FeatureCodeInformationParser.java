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
	private static final String RNC_MATCH_PATTERN = ".*RNC[\\W]*([a-zA-Z0-9]*)[\\W]*([a-zA-Z0-9]*)[\\W]*([0-9-]*)[\\W]*([0-9:]*)";
	private static final String FEATURE_CODE_INFORMATION_MATCH_UNNECESSARY_TEXT = ".*FEATURE INFORMATION:[\\W]*(.*)COMMAND EXECUTED";
	private static final String FEATURE_CODE_INFORMATION_MATCH_PATTERN = "FEATURE CODE:[\\W]*(\\d*)[\\W]*FEATURE NAME:[\\W]*([a-zA-Z -/0-9_]*)[\\W]*FEATURE STATE:[\\W]*(ON|OFF)[\\W]*FEATURE CAPACITY:[\\W]*(\\d*)[\\W]*";

	private RNC readRncInfo(String data) {
		Matcher matcher = Pattern.compile(FeatureCodeInformationParser.RNC_MATCH_PATTERN).matcher(data);
		if (!matcher.find())
			return null;
		return new RNC() {
			{
				setName(matcher.group(1));
				setCode(matcher.group(2));
			}
		};
	}
	
	private String removeUnnecessaryData(String data) {
		String newData = null;
		Pattern compiledInitialPattern = Pattern.compile(FeatureCodeInformationParser.FEATURE_CODE_INFORMATION_MATCH_UNNECESSARY_TEXT);
		Matcher matcher = compiledInitialPattern.matcher(data);
		if(matcher.find())
			newData = matcher.group(1); 
		
		return newData;
	}

	public List<FeatureInformation> readRncFeatureCodesInformations(String data) {
		RNC rnc = readRncInfo(data);
		
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