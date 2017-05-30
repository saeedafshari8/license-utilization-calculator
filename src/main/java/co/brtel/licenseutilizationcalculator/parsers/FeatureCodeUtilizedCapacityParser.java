package co.brtel.licenseutilizationcalculator.parsers;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import co.brtel.licenseutilizationcalculator.pojo.FeatureInformation;
import co.brtel.licenseutilizationcalculator.pojo.RNC;

public class FeatureCodeUtilizedCapacityParser {
	private static final String UCAP_COMMAND_MATCH_PATTERN_HEAD = "ZW7I:UCAP,FULL::;";
	private static final String UCAP_COMMAND_MATCH_PATTERN_TAIL = "COMMAND EXECUTED";
	private static final String FEATURE_CODE_UTILIZATION_MATCH_INITIAL_PATTERN = ".*REQUEST STATUS[\\W]*([0-9]+)[\\W]*([0-9]+)[\\W]*([a-zA-Z]+)[\\W]*";
	private static final String FEATURE_CODE_UTILIZATION_MATCH_PATTERN = "[\\W]*([0-9]+)[\\W]*([0-9]+)[\\W]*([a-zA-Z]+)[\\W]*";

	public List<FeatureInformation> readRncFeatureCodesUtilizations(String data) {
		RNC rnc = RNC.readRncInfo(data);

		Pattern compiledInitialPattern = Pattern.compile(FeatureCodeUtilizedCapacityParser.FEATURE_CODE_UTILIZATION_MATCH_INITIAL_PATTERN);
		Pattern compiledPattern = Pattern.compile(FeatureCodeUtilizedCapacityParser.FEATURE_CODE_UTILIZATION_MATCH_PATTERN);

		List<FeatureInformation> features = new ArrayList<FeatureInformation>(3);

		Matcher matcher = compiledInitialPattern.matcher(data);
		while (matcher.find()) {
			FeatureInformation fi = new FeatureInformation();
			fi.setCode(matcher.group(1));
			fi.setCapacity(Integer.parseInt(matcher.group(2)));
			fi.setRnc(rnc);
			features.add(fi);
			data = data.replace(matcher.group(), "");
			matcher = compiledPattern.matcher(data);
		}
		return features;
	}
	
	public Map<String, List<FeatureInformation>> readRncsFeatureCodesUtilizations(String data){
		Map<String, List<FeatureInformation>> rncsFeatureCodesUtilizations = new HashMap<String, List<FeatureInformation>>();
		
		String ucapMatchPattern = FeatureCodeUtilizedCapacityParser.UCAP_COMMAND_MATCH_PATTERN_HEAD 
				+ "(.*?)"
				+ FeatureCodeUtilizedCapacityParser.UCAP_COMMAND_MATCH_PATTERN_TAIL;
		Pattern pattern = Pattern.compile(ucapMatchPattern, Pattern.DOTALL);
		Matcher matcher = pattern.matcher(data);

		while (matcher.find()) {
			List<FeatureInformation> rncFCUs = readRncFeatureCodesUtilizations(matcher.group(1));
			rncsFeatureCodesUtilizations.put(rncFCUs.get(0).getRnc().getName(), rncFCUs);
		}
		return rncsFeatureCodesUtilizations;
	}
}