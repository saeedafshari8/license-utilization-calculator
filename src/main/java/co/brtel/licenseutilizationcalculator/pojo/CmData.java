package co.brtel.licenseutilizationcalculator.pojo;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name="cmData")
public class CmData {
	private Header header;
	private ManagedObject managedObject;

	public Header getHeader() {
		return header;
	}

	@XmlElement(name = "header")
	public void setHeader(Header header) {
		this.header = header;
	}

	public ManagedObject getManagedObject() {
		return managedObject;
	}

	@XmlElement(name = "managedObject")
	public void setManagedObject(ManagedObject managedObject) {
		this.managedObject = managedObject;
	}
}