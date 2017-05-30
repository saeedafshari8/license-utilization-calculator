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
	private static final String RNC_MATCH_PATTERN = ".*RNC[\\W]*([a-zA-Z0-9]*)[\\W]*([a-zA-Z0-9]*)[\\W]*([0-9-]*)[\\W]*([0-9:]*)";
	private static final String FEATURE_CODE_UTILIZATION_MATCH_INITIAL_PATTERN = ".*REQUEST STATUS[\\W]*([0-9]+)[\\W]*([0-9]+)[\\W]*([a-zA-Z]+)[\\W]*";
	private static final String FEATURE_CODE_UTILIZATION_MATCH_PATTERN = "[\\W]*([0-9]+)[\\W]*([0-9]+)[\\W]*([a-zA-Z]+)[\\W]*";

	private RNC readRncInfo(String data) {
		Matcher matcher = Pattern.compile(FeatureCodeUtilizedCapacityParser.RNC_MATCH_PATTERN).matcher(data);
		if (!matcher.find())
			return null;
		return new RNC() {
			{
				setName(matcher.group(1));
				setCode(matcher.group(2));
			}
		};
	}

	public List<FeatureInformation> readRncFeatureCodesUtilizations(String data) {
		RNC rnc = readRncInfo(data);

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
		
		String ucapMatchPattern = Pattern.quote(FeatureCodeUtilizedCapacityParser.UCAP_COMMAND_MATCH_PATTERN_HEAD) 
				+ "(.*?)"
				+ Pattern.quote(FeatureCodeUtilizedCapacityParser.UCAP_COMMAND_MATCH_PATTERN_TAIL);
		Pattern pattern = Pattern.compile(ucapMatchPattern);
		Matcher matcher = pattern.matcher(data);

		while (matcher.find()) {
			List<FeatureInformation> rncFCUs = readRncFeatureCodesUtilizations(matcher.group(1));
			rncsFeatureCodesUtilizations.put(rncFCUs.get(0).getRnc().getName(), rncFCUs);
		}
		return rncsFeatureCodesUtilizations;
	}
}