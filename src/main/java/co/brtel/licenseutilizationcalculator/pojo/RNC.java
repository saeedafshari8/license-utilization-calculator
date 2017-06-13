package co.brtel.licenseutilizationcalculator.pojo;

import java.util.Arrays;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 
 * @author saeed RNC class model
 */
public class RNC {
	private static final String RNC_MATCH_PATTERN = "RNC[\\W]*([a-zA-Z0-9]*)[\\W]*(\\d*)[\\W]*([0-9-]*)[\\W]*([0-9:]*)";

	private String name;
	private String code;
	private boolean muxEnable;

	public static RNC readRncInfo(String data) {
		Optional<String> lines = Arrays.stream(data.split("\r\n")).filter(item -> item.startsWith("RNC")).findFirst();
		if(!lines.isPresent())
			return null;
		Matcher matcher = Pattern.compile(RNC.RNC_MATCH_PATTERN).matcher(lines.get());
		if (!matcher.find())
			return null;
		return new RNC() {
			{
				setName(matcher.group(1));
				setCode(matcher.group(2));
			}
		};
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public boolean isMuxEnable() {
		return muxEnable;
	}

	public void setMuxEnable(boolean muxEnable) {
		this.muxEnable = muxEnable;
	}
}