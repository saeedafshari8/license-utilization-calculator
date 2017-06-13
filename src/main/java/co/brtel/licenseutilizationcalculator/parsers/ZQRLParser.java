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
	private static final String ENABLE = "ENABLE";

	private boolean isMUXEnable(String data) {
		Pattern compiledInitialPattern = Pattern.compile(ZQRLParser.ZQRL_MATCH_PATTERN);
		Matcher matcher = compiledInitialPattern.matcher(data);
		while (matcher.find())
			return matcher.group(1).equalsIgnoreCase(ENABLE);
		return false;
	}

	public Map<String, Boolean> readRNCsMUXStatus(String data) {
		Map<String, Boolean> resultMap = new HashMap<String, Boolean>();

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