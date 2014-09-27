package fr.atlasmuseum.contribution;

import java.io.File;
import java.io.Serializable;

import org.jdom2.Attribute;
import org.jdom2.Element;

import fr.atlasmuseum.search.SearchActivity;

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
	private String mJsonField;
	private int mTitle; /* reference to a resource string */
	private String mValue;
	private String mOriginalValue;
	private String mDefaultValue;
	private int mInfo; /* reference to a resource string */
	private ContribType mType;
	private String[] mChoices;
	private int mShowViewText;
	private int mShowViewToHide;
	private Boolean mDumpInXML;
	
	public ContributionProperty(
			String dbField,
			String jsonField,
			int title,
			String value,
			String defaultValue,
			int info,
			ContribType type,
			String[] choices,
			int showViewText,
			int showViewToHide,
			Boolean dumpInXML
			) {
		mDbField = dbField;
		mJsonField = jsonField;
		mTitle = title;
		mValue = value;
		mOriginalValue = value;
		mDefaultValue = defaultValue;
		mInfo = info;
		mType = type;
		mChoices = choices;
		mShowViewText = showViewText;
		mShowViewToHide = showViewToHide;
		mDumpInXML = dumpInXML;
	}
	
	public String getDbField() {
		return mDbField;
	}
	public void setDbField(String dbField) {
		mDbField = dbField;
	}

	public String getJsonField() {
		return mJsonField;
	}
	public void setJsonField(String jsonField) {
		mJsonField = jsonField;
	}

	public int getTitle() {
		return mTitle;
	}
	public void setTitle(int title) {
		mTitle = title;
	}

	public String getValue() {
		return mValue;
	}
	public void setValue(String value) {
		mValue = value;
		mValue = mValue.trim();
		if( mValue.equals("?") ) mValue = "";
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
	
	public int getInfo() {
		return mInfo;
	}
	public void setInfo(int info) {
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
		return (! mOriginalValue.equals(mValue));
	}
	
	public void updateFromDb(int index) {
		resetValue(SearchActivity.extractDataFromDb(index, mJsonField));
		
		// Check if the image is in cache and update with absolute path
		if( mDbField == Contribution.PHOTO ) {
			File file = new File(Contribution.getPhotoDir(), mValue);
			if( file.exists() ) {
				resetValue(file.getAbsolutePath());
			}
		}
	}
	
	public void addXML( Element parent, Boolean original ) {
		if( ! mDumpInXML ) {
			return;
		}
		if( mValue.equals("") && !original ) {
			return;
		}
		if( mOriginalValue.equals("") && original ) {
			return;
		}
		
		String value = original ? mOriginalValue : mValue;
		if( mDbField.equals(Contribution.PHOTO) ) {
			value = new File(value).getName();
		}
		Element elem = new Element(mDbField);
        Attribute attr = new Attribute(Contribution.VALUE, value);
        elem.setAttribute(attr);
        parent.addContent(elem);
	}
}