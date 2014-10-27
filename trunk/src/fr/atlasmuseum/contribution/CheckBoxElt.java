package fr.atlasmuseum.contribution;

public class CheckBoxElt {
	
	String value;
	Boolean bool;
	
	public CheckBoxElt(String ch)
	{
		value =ch;
		bool=false;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

	public Boolean getBool() {
		return bool;
	}

	public void setBool(Boolean bool) {
		this.bool = bool;
	}

}
