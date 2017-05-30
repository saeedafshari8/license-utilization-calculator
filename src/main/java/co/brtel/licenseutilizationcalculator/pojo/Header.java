package co.brtel.licenseutilizationcalculator.pojo;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "header")
public class Header {
	private String log;

	public String getLog() {
		return log;
	}

	@XmlElement(name = "log")
	public void setLog(String log) {
		this.log = log;
	}
}
