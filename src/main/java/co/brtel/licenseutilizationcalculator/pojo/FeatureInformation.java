package co.brtel.licenseutilizationcalculator.pojo;

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
}