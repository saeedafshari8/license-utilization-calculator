package co.brtel.licenseutilizationcalculator.pojo;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "managedObject")
public class ManagedObject {
	private String className;
	private String distName;
	private RncOption rncOption;
	private Parameter[] parameters;

	public String getClassName() {
		return className;
	}

	@XmlAttribute(name = "class")
	public void setClassName(String className) {
		this.className = className;
	}
	
	public String getDistName() {
		return distName;
	}

	@XmlAttribute(name = "distName")
	public void setDistName(String distName) {
		this.distName = distName;
	}

	public Parameter[] getParameters() {
		return parameters;
	}

	@XmlElement(name = "p")
	public void setParameters(Parameter[] parameters) {
		this.parameters = parameters;
	}

	public RncOption getRncOption() {
		return rncOption;
	}

	@XmlElement(name = "list")
	public void setRncOption(RncOption rncOption) {
		this.rncOption = rncOption;
	}
}
