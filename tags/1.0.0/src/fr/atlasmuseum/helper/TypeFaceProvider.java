package fr.atlasmuseum.helper;

import java.util.Hashtable;

import android.content.Context;
import android.graphics.Typeface;

public class TypeFaceProvider {

	private static Hashtable<String, Typeface> sTypeFaces = new Hashtable<String, Typeface>(
			4);

	public static Typeface getTypeFace(Context context, String fileName) {
		Typeface tempTypeface = sTypeFaces.get(fileName);

		if (tempTypeface == null) {
			tempTypeface = Typeface.createFromAsset(context.getAssets(),
					fileName);
			sTypeFaces.put(fileName, tempTypeface);
		}

		return tempTypeface;
	}
}
