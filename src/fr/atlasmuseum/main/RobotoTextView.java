package fr.atlasmuseum.main;

import java.util.Hashtable;

import android.content.Context;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.widget.TextView;

public class RobotoTextView extends TextView {

	Context context;

	public RobotoTextView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		this.context = context;
	}

	public RobotoTextView(Context context, AttributeSet attrs) {
		super(context, attrs);
		this.context = context;
	}

	public RobotoTextView(Context context) {
		super(context);
		this.context = context;
	}

	public void setTypeface(Typeface tf, int style) {
		if (!isInEditMode()) {
			if (style == Typeface.NORMAL) {
				super.setTypeface(TypeFaceProvider.getTypeFace(getContext(), "fonts/Roboto-Light.ttf"));
			} else if (style == Typeface.ITALIC) {
				super.setTypeface(TypeFaceProvider.getTypeFace(getContext(), "fonts/Roboto-LightItalic.ttf"));
			} else if (style == Typeface.BOLD) {
				super.setTypeface(TypeFaceProvider.getTypeFace(getContext(), "fonts/Roboto-Bold.ttf"));
			} else if (style == Typeface.BOLD_ITALIC) {
				super.setTypeface(TypeFaceProvider.getTypeFace(getContext(), "fonts/Roboto-BoldItalic.ttf"));
			}

		}
	}
}