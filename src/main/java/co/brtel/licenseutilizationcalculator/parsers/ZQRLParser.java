package co.brtel.licenseutilizationcalculator.parsers;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import co.brtel.licenseutilizationcalculator.pojo.RNC;

public class ZQRLParser {
	private static final String ZQRL_COMMAND_MATCH_PATTERN_HEAD = "ZQRL;";
	private static final String ZQRL_COMMAND_MATCH_PATTERN_TAIL = "COMMAND EXECUTED";
	private static final String ZQRL_MATCH_PATTERN = ".*MUX[\\W]*LOCAL MUX REMOTE MUX MAX[\\W]*DSCP[\\W]*([a-zA-Z]+)";

	private String isMUXEnable(String data) {
		Pattern compiledInitialPattern = Pattern.compile(ZQRLParser.ZQRL_MATCH_PATTERN);
		Matcher matcher = compiledInitialPattern.matcher(data);
		while (matcher.find())
			return matcher.group(1);
		return null;
	}

	public Map<String, String> readRNCsMUXStatus(String data) {
		Map<String, String> resultMap = new HashMap<String, String>();

		String matchPattern = ZQRL_COMMAND_MATCH_PATTERN_HEAD + "(.*?)" + ZQRL_COMMAND_MATCH_PATTERN_TAIL;
		Pattern pattern = Pattern.compile(matchPattern, Pattern.DOTALL);
		Matcher matcher = pattern.matcher(data);

		while (matcher.find()) {
			String grp = matcher.group(1);
			resultMap.put(RNC.readRncInfo(grp).getName(), isMUXEnable(grp));
		}
		return resultMap;
	}
}