package co.brtel.licenseutilizationcalculator.pojo;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name="raml")
public class Raml {
	private CmData cmData;

	public CmData getCmData() {
		return cmData;
	}

	@XmlElement(name = "cmData")
	public void setCmData(CmData cmData) {
		this.cmData = cmData;
	}
}
