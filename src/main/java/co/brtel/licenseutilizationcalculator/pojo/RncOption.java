package co.brtel.licenseutilizationcalculator.pojo;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "list")
public class RncOption {
	private String name;
	private Parameter[] parameters;

	public String getName() {
		return name;
	}

	@XmlAttribute(name = "name")
	public void setName(String name) {
		this.name = name;
	}

	public Parameter[] getParameters() {
		return parameters;
	}

	@XmlElement(name = "p")
	public void setParameters(Parameter[] parameters) {
		this.parameters = parameters;
	}
}
