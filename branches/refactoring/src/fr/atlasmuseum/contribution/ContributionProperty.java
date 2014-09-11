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
	private String mDefaultValue;
	private String mInfo;
	private ContribType mType;
	private String[] mChoices;
	private Boolean mIsModified;
	private int mShowViewText;
	private int mShowViewToHide;
	
	public ContributionProperty(
			String dbField,
			String title,
			String value,
			String defaultValue,
			String info,
			ContribType type,
			String[] choices,
			int showViewText,
			int showViewToHide
			) {
		mDbField = dbField;
		mTitle = title;
		mValue = value;
		mOriginalValue = value;
		mDefaultValue = defaultValue;
		mInfo = info;
		mType = type;
		mChoices = choices;
		mIsModified = false;
		mShowViewText = showViewText;
		mShowViewToHide = showViewToHide;
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
		mValue = mValue.trim();
		if( mValue == "?" ) mValue = "";
	}

	public String getOriginalValue() {
		return mOriginalValue;
	}
	public void setOriginalValue(String originalValue) {
		mOriginalValue = originalValue;
		mOriginalValue = mOriginalValue.trim();
		if( mOriginalValue == "?" ) mOriginalValue = "";
	}

	public String getDefaultValue() {
		return mDefaultValue;
	}
	public void setDefaultValue(String defaultValue) {
		mDefaultValue = defaultValue;
	}

	public void resetValue(String value) {
		setValue( value );
		setOriginalValue( value );
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
	public Boolean isOfType(ContribType type) {
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
	
	public int getShowViewText() {
		return mShowViewText;
	}
	public void setShowViewText(int showViewText) {
		mShowViewText = showViewText;
	}

	public int getShowViewToHide() {
		return mShowViewToHide;
	}
	public void setShowViewToHide(int showViewToHide) {
		mShowViewToHide = showViewToHide;
	}

	public Boolean isModified() {
		return mIsModified;
	}
	
}