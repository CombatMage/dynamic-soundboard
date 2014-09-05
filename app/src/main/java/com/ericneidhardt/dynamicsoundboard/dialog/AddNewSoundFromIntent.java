package com.ericneidhardt.dynamicsoundboard.dialog;

import android.app.AlertDialog;
import android.content.Context;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Spinner;

import com.ericneidhardt.dynamicsoundboard.R;
import com.ericneidhardt.dynamicsoundboard.customview.CustomEditText;
import com.ericneidhardt.dynamicsoundboard.customview.CustomSpinner;
import com.ericneidhardt.dynamicsoundboard.dao.SoundSheet;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by eric.neidhardt on 04.09.2014.
 */
public class AddNewSoundFromIntent
{
	public static void show(Context context, Uri uri, String suggestedName, final OnAddSoundFromIntentListener listener)
	{
		final View dialogView = LayoutInflater.from(context).inflate(R.layout.dialog_add_new_sound_from_intent, null);
		final CustomEditText soundName = (CustomEditText)dialogView.findViewById(R.id.et_name_file);
		final CustomEditText soundSheetName = (CustomEditText)dialogView.findViewById(R.id.et_name_new_sound_sheet);

		soundName.setText(uri.toString());
		soundSheetName.setHint(suggestedName);

		AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(context);
		dialogBuilder.setView(dialogView);
		final AlertDialog dialog = dialogBuilder.create();

		dialogView.findViewById(R.id.b_cancel).setOnClickListener(new View.OnClickListener()
		{
			@Override
			public void onClick(View v)
			{
				dialog.dismiss();
			}
		});

		dialogView.findViewById(R.id.b_ok).setOnClickListener(new View.OnClickListener()
		{
			@Override
			public void onClick(View v)
			{
				String name = soundName.getText().toString();
				String sheet = soundSheetName.getDisplayedText().toString();
				listener.onAddSoundFromIntent(name, sheet, null);
				dialog.dismiss();
			}
		});
		dialog.show();
	}

	public static void show(Context context, Uri uri, String suggestedName, final List<SoundSheet> availableSoundSheets, final OnAddSoundFromIntentListener listener)
	{
		final View dialogView = LayoutInflater.from(context).inflate(R.layout.dialog_add_new_sound_from_intent_to_sound_sheet, null);
		final CustomEditText soundName = (CustomEditText)dialogView.findViewById(R.id.et_name_file);
		final CustomEditText soundSheetName = (CustomEditText)dialogView.findViewById(R.id.et_name_new_sound_sheet);
		final CustomSpinner soundSheetSpinner = (CustomSpinner)dialogView.findViewById(R.id.s_sound_sheets);

		soundName.setText(uri.toString());
		soundSheetName.setHint(suggestedName);
		soundSheetSpinner.setItems(getKeyValueMap(availableSoundSheets));

		AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(context);
		dialogBuilder.setView(dialogView);
		final AlertDialog dialog = dialogBuilder.create();

		dialogView.findViewById(R.id.b_cancel).setOnClickListener(new View.OnClickListener()
		{
			@Override
			public void onClick(View v)
			{
				dialog.dismiss();
			}
		});

		dialogView.findViewById(R.id.b_ok).setOnClickListener(new View.OnClickListener()
		{
			@Override
			public void onClick(View v)
			{
				// TODO trigger listener

				String name = soundName.getText().toString();
				SoundSheet selectedSoundSheet = availableSoundSheets.get(soundSheetSpinner.getSelectedItemPosition());


				dialog.dismiss();
			}
		});
		dialog.show();
	}

	private static List<String> getKeyValueMap(List<SoundSheet> soundSheets)
	{
		List<String> labels = new ArrayList<String>();
		for (SoundSheet soundSheet : soundSheets)
			labels.add(soundSheet.getLabel());
		return labels;
	}

	public interface OnAddSoundFromIntentListener
	{
		public void onAddSoundFromIntent(String soundName, String newSoundSheet, SoundSheet addToSoundSheet);
	}
}
