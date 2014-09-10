package fr.atlasmuseum.contribution;

import java.io.Serializable;

public class ContributionProperty implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 968617293162758396L;

	public static enum ContribType
	{
		text, check, radio, date
	}

	private String mDbField;
	private String mTitle;
	private String mValue;
	private String mOriginalValue;
	private String mInfo;
	private ContribType mType;
	private String[] mChoices;
	private Boolean mIsModified;
	private int mShowView;
	
	public ContributionProperty(
			String dbField,
			String title,
			String value,
			String info,
			ContribType type,
			String[] choices,
			int showView
			) {
		mDbField = dbField;
		mTitle = title;
		mValue = value;
		mOriginalValue = value;
		mInfo = info;
		mType = type;
		mChoices = choices;
		mIsModified = false;
		mShowView = showView;
	}
	
	public String getDbField() {
		return mDbField;
	}
	public void setDbField(String dbField) {
		mDbField = dbField;
	}

	public String getTitle() {
		return mTitle;
	}
	public void setTitle(String title) {
		mTitle = title;
	}

	public String getValue() {
		return mValue;
	}
	public void setValue(String value) {
		mValue = value;
	}

	public String getOriginalValue() {
		return mOriginalValue;
	}
	public void setOriginalValue(String originalValue) {
		mOriginalValue = originalValue;
	}

	public String getInfo() {
		return mInfo;
	}
	public void setInfo(String info) {
		mInfo = info;
	}

	public ContribType getType() {
		return mType;
	}
	public void setType(ContribType type) {
		mType = type;
	}
	public Boolean isOfType( ContribType type ) {
		return (mType == type);
	}
	public Boolean isText() {
		return isOfType(ContribType.text);
	}
	public Boolean isCheck() {
		return isOfType(ContribType.check);
	}
	public Boolean isRadio() {
		return isOfType(ContribType.radio);
	}
	public Boolean isDate() {
		return isOfType(ContribType.date);
	}

	public String[] getChoices() {
		return mChoices;
	}
	public void setChoices(String[] choices) {
		mChoices = choices;
	}
	
	public Boolean isModified() {
		return mIsModified;
	}
	
}