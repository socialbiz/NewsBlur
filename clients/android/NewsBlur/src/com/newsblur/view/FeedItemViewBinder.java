package com.newsblur.view;

import android.content.Context;
import android.database.Cursor;
import android.graphics.drawable.Drawable;
import android.support.v4.widget.SimpleCursorAdapter.ViewBinder;
import android.text.Html;
import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;

import com.newsblur.R;
import com.newsblur.database.DatabaseConstants;
import com.newsblur.domain.Story;
import com.newsblur.util.StoryUtils;

import java.util.Date;

public class FeedItemViewBinder implements ViewBinder {

	private final Context context;
	private int darkGray;
	private int lightGray;

	public FeedItemViewBinder(final Context context) {
		this.context = context;
		darkGray = context.getResources().getColor(R.color.darkgray);
		lightGray = context.getResources().getColor(R.color.lightgray);
	}
	
	@Override
	public boolean setViewValue(View view, Cursor cursor, int columnIndex) {
		final String columnName = cursor.getColumnName(columnIndex);
		int hasBeenRead = cursor.getInt(cursor.getColumnIndex(DatabaseConstants.STORY_READ));
		if (TextUtils.equals(columnName, DatabaseConstants.STORY_READ)) {
			if (hasBeenRead == 0) {
				((TextView) view).setTextColor(darkGray);
				
			} else {
				((TextView) view).setTextColor(lightGray);
			}
			return true;
		} else if (TextUtils.equals(columnName, DatabaseConstants.STORY_AUTHORS)) {
			if (TextUtils.isEmpty(cursor.getString(columnIndex))) {
				view.setVisibility(View.GONE);
			} else {
				view.setVisibility(View.VISIBLE);
				((TextView) view).setText(cursor.getString(columnIndex).toUpperCase());
			}
			return true;
		} else if (TextUtils.equals(columnName, DatabaseConstants.STORY_INTELLIGENCE_AUTHORS)) {
			int authors = cursor.getInt(columnIndex);
			int tags = cursor.getInt(cursor.getColumnIndex(DatabaseConstants.STORY_INTELLIGENCE_TAGS));
			int feed = cursor.getInt(cursor.getColumnIndex(DatabaseConstants.STORY_INTELLIGENCE_FEED));
			int title = cursor.getInt(cursor.getColumnIndex(DatabaseConstants.STORY_INTELLIGENCE_TITLE));
			int score = Story.getIntelligenceTotal(title, authors, tags, feed);
			
			Drawable icon;
            if (score > 0) {
                icon = view.getResources().getDrawable(R.drawable.g_icn_focus);
			} else if (score == 0) {
                icon = view.getResources().getDrawable(R.drawable.g_icn_unread);
			} else {
                icon = view.getResources().getDrawable(R.drawable.g_icn_hidden);
			}
            icon.mutate().setAlpha(hasBeenRead == 0 ? 255 : 127);
            view.setBackgroundDrawable(icon);
			
			((TextView) view).setText("");
			return true;
		} else if (TextUtils.equals(columnName, DatabaseConstants.STORY_TITLE)) {
            ((TextView) view).setText(Html.fromHtml(cursor.getString(columnIndex)));
			return true;
		} else if (TextUtils.equals(columnName, DatabaseConstants.STORY_DATE)) {
            ((TextView) view).setText(StoryUtils.formatShortDate(context, new Date(cursor.getLong(columnIndex))));
            return true;
        }
		
		return false;
	}

}
