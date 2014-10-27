package fr.atlasmuseum.contribution;

public class CheckBoxListElement {
	String mValue;
	Boolean mBool;
	
	public CheckBoxListElement(String value) {
		mValue = value;
		mBool = false;
	}

	public String getValue() {
		return mValue;
	}

	public void setValue(String value) {
		mValue = value;
	}

	public Boolean getBool() {
		return mBool;
	}

	public void setBool(Boolean bool) {
		mBool = bool;
	}
}

