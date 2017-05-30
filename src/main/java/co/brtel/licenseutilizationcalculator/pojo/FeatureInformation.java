package co.brtel.licenseutilizationcalculator.pojo;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * 
 * @author saeed Feature code information model
 */
public class FeatureInformation {
	private RNC rnc;
	private String code;
	private String utilization;
	private String name;
	private FeatureState featureState;
	private int capacity;
	private static final Map<String, CapacityUnit> capacityUnitMap;
	static {
		Map<String, CapacityUnit> aMap = new HashMap<String, CapacityUnit>();
		aMap.put("619", CapacityUnit.CELL);
		aMap.put("620", CapacityUnit.DYNAMIC_CELL);
		aMap.put("621", CapacityUnit.DYNAMIC_CELL);
		aMap.put("622", CapacityUnit.DYNAMIC_CELL);
		aMap.put("623", CapacityUnit.DYNAMIC_CELL);
		aMap.put("625", CapacityUnit.CELL);
		aMap.put("627", CapacityUnit.DYNAMIC_CELL);
		aMap.put("628", CapacityUnit.CELL);
		aMap.put("630", CapacityUnit.CELL);
		aMap.put("633", CapacityUnit.CELL);
		aMap.put("638", CapacityUnit.CELL);
		aMap.put("641", CapacityUnit.WBTS);
		aMap.put("811", CapacityUnit.ON_OFF);
		aMap.put("885", CapacityUnit.CELL);
		aMap.put("958", CapacityUnit.MBPS);
		aMap.put("959", CapacityUnit.ERLANG);
		aMap.put("960", CapacityUnit.CARRIER);
		aMap.put("961", CapacityUnit.WBTS);
		aMap.put("962", CapacityUnit.WBTS);
		aMap.put("966", CapacityUnit.CELL);
		aMap.put("967", CapacityUnit.CELL);
		aMap.put("968", CapacityUnit.CELL);
		aMap.put("969", CapacityUnit.CELL);
		aMap.put("974", CapacityUnit.CELL);
		aMap.put("975", CapacityUnit.CELL);
		aMap.put("1028", CapacityUnit.CELL);
		aMap.put("1029", CapacityUnit.WBTS);
		aMap.put("1030", CapacityUnit.CELL);
		aMap.put("1057", CapacityUnit.CELL);
		aMap.put("1060", CapacityUnit.CELL);
		aMap.put("1061", CapacityUnit.CELL);
		aMap.put("1062", CapacityUnit.CELL);
		aMap.put("1069", CapacityUnit.CELL);
		aMap.put("1070", CapacityUnit.CELL);
		aMap.put("1071", CapacityUnit.CELL);
		aMap.put("1079", CapacityUnit.WBTS);
		aMap.put("1080", CapacityUnit.CELL);
		aMap.put("1085", CapacityUnit.WBTS);
		aMap.put("1086", CapacityUnit.CELL);
		aMap.put("1087", CapacityUnit.WBTS);
		aMap.put("1088", CapacityUnit.WBTS);
		aMap.put("1089", CapacityUnit.WBTS);
		aMap.put("1091", CapacityUnit.WBTS);
		aMap.put("1092", CapacityUnit.CELL);
		aMap.put("1093", CapacityUnit.WBTS);
		aMap.put("1107", CapacityUnit.CELL);
		aMap.put("1108", CapacityUnit.ON_OFF);
		aMap.put("1109", CapacityUnit.CELL);
		aMap.put("1110", CapacityUnit.CELL);
		aMap.put("1246", CapacityUnit.WBTS);
		aMap.put("1247", CapacityUnit.WBTS);
		aMap.put("1279", CapacityUnit.CELL);
		aMap.put("1302", CapacityUnit.CELL);
		aMap.put("1303", CapacityUnit.CELL);
		aMap.put("1305", CapacityUnit.ON_OFF);
		aMap.put("1306", CapacityUnit.ON_OFF);
		aMap.put("1375", CapacityUnit.CELL);
		aMap.put("1434", CapacityUnit.CELL);
		aMap.put("1435", CapacityUnit.CELL);
		aMap.put("1471", CapacityUnit.CELL);
		aMap.put("1475", CapacityUnit.CELL);
		aMap.put("1476", CapacityUnit.CELL);
		aMap.put("1478", CapacityUnit.DYNAMIC_CELL);
		aMap.put("1485", CapacityUnit.CELL);
		aMap.put("1490", CapacityUnit.CELL);
		aMap.put("1497", CapacityUnit.WBTS);
		aMap.put("1683", CapacityUnit.CELL);
		aMap.put("1684", CapacityUnit.CELL);
		aMap.put("1686", CapacityUnit.CELL);
		aMap.put("1690", CapacityUnit.CELL);
		aMap.put("1754", CapacityUnit.CELL);
		aMap.put("1755", CapacityUnit.CELL);
		aMap.put("1756", CapacityUnit.CELL);
		aMap.put("1795", CapacityUnit.CELL);
		aMap.put("1796", CapacityUnit.DYNAMIC_CELL);
		aMap.put("1798", CapacityUnit.CELL);
		aMap.put("1897", CapacityUnit.CELL);
		aMap.put("1898", CapacityUnit.CELL);
		aMap.put("1938", CapacityUnit.CELL);
		aMap.put("1939", CapacityUnit.ON_OFF);
		aMap.put("2117", CapacityUnit.CELL);
		aMap.put("3384", CapacityUnit.CELL);
		aMap.put("3414", CapacityUnit.CELL);
		aMap.put("3420", CapacityUnit.CELL);
		aMap.put("3422", CapacityUnit.CELL);
		aMap.put("3898", CapacityUnit.CELL);
		aMap.put("3903", CapacityUnit.CELL);
		aMap.put("4160", CapacityUnit.CELL);
		aMap.put("4290", CapacityUnit.CELL);
		aMap.put("4293", CapacityUnit.CELL);
		aMap.put("4348", CapacityUnit.CELL);
		aMap.put("4492", CapacityUnit.CELL);
		aMap.put("4504", CapacityUnit.CELL);
		aMap.put("4533", CapacityUnit.CELL);
		aMap.put("4538", CapacityUnit.DYNAMIC_CELL);
		aMap.put("4545", CapacityUnit.CELL);
		aMap.put("4780", CapacityUnit.CELL);
		aMap.put("4783", CapacityUnit.CELL);
		aMap.put("4839", CapacityUnit.CELL);
		capacityUnitMap = Collections.unmodifiableMap(aMap);
	}

	public RNC getRnc() {
		return rnc;
	}

	public void setRnc(RNC rnc) {
		this.rnc = rnc;
	}

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public String getName() {
		return name.trim();
	}

	public void setName(String name) {
		this.name = name;
	}

	public FeatureState getFeatureState() {
		return featureState;
	}

	public void setFeatureState(FeatureState featureState) {
		this.featureState = featureState;
	}

	public int getCapacity() {
		return capacity;
	}

	public void setCapacity(int capacity) {
		this.capacity = capacity;
	}

	public String getUtilization() {
		return utilization;
	}

	public void setUtilization(String utilization) {
		this.utilization = utilization;
	}

	public CapacityUnit getCapacityUnit() {
		return capacityUnitMap.get(code);
	}
}