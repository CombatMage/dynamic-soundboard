package org.neidhardt.dynamicsoundboard.soundmanagement.views;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.FragmentManager;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import de.greenrobot.event.EventBus;
import org.neidhardt.dynamicsoundboard.R;
import org.neidhardt.dynamicsoundboard.dao.MediaPlayerData;
import org.neidhardt.dynamicsoundboard.dao.SoundSheet;
import org.neidhardt.dynamicsoundboard.mediaplayer.EnhancedMediaPlayer;
import org.neidhardt.dynamicsoundboard.misc.FileUtils;
import org.neidhardt.dynamicsoundboard.soundactivity.SoundActivity;
import org.neidhardt.dynamicsoundboard.soundmanagement.events.AddNewSoundEvent;
import org.neidhardt.dynamicsoundboard.soundsheetmanagement.model.SoundSheetsDataAccess;
import org.neidhardt.dynamicsoundboard.soundsheetmanagement.model.SoundSheetsDataStorage;
import org.neidhardt.dynamicsoundboard.soundsheetmanagement.model.SoundSheetsDataUtil;
import org.neidhardt.dynamicsoundboard.views.BaseDialog;
import org.neidhardt.dynamicsoundboard.views.edittext.CustomEditText;
import org.neidhardt.dynamicsoundboard.views.spinner.CustomSpinner;

import java.util.ArrayList;
import java.util.List;

public class AddNewSoundFromIntent extends BaseDialog implements View.OnClickListener, CompoundButton.OnCheckedChangeListener
{
	private static final String TAG = AddNewSoundFromIntent.class.getName();

	private static final String KEY_SOUND_URI = "org.neidhardt.dynamicsoundboard.soundmanagement.views.AddNewSoundFromIntent.uri";
	private static final String KEY_SUGGESTED_NAME = "org.neidhardt.dynamicsoundboard.soundmanagement.views.AddNewSoundFromIntent.suggestedName";
	private static final String KEY_AVAILABLE_SOUND_SHEET_LABELS = "org.neidhardt.dynamicsoundboard.soundmanagement.views.AddNewSoundFromIntent.availableSoundSheetLabels";
	private static final String KEY_AVAILABLE_SOUND_SHEET_IDS = "org.neidhardt.dynamicsoundboard.soundmanagement.views.AddNewSoundFromIntent.availableSoundSheetIds";

	private SoundSheetsDataStorage soundSheetsDataStorage;
	private SoundSheetsDataUtil soundSheetsDataUtil;

	private CustomEditText soundName;
	private CustomEditText soundSheetName;
	private CustomSpinner soundSheetSpinner;
	private CheckBox addNewSoundSheet;

	private Uri uri;
	private String suggestedName;
	private List<String> availableSoundSheetLabels;
	private List<String> availableSoundSheetIds;

	private boolean soundSheetsAlreadyExists;

	public static void showInstance(FragmentManager manager, Uri uri, String suggestedName, List<SoundSheet> availableSoundSheets)
	{
		AddNewSoundFromIntent dialog = new AddNewSoundFromIntent();

		Bundle args = new Bundle();
		args.putString(KEY_SOUND_URI, uri.toString());
		args.putString(KEY_SUGGESTED_NAME, suggestedName);
		if (availableSoundSheets != null)
		{
			args.putStringArrayList(KEY_AVAILABLE_SOUND_SHEET_LABELS, getLabelsFromSoundSheets(availableSoundSheets));
			args.putStringArrayList(KEY_AVAILABLE_SOUND_SHEET_IDS, getIdsFromSoundSheets(availableSoundSheets));
		}
		dialog.setArguments(args);

		dialog.show(manager, TAG);
	}

	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);

		Bundle args = this.getArguments();
		if (args != null)
		{
			this.uri = Uri.parse(args.getString(KEY_SOUND_URI));
			this.suggestedName = args.getString(KEY_SUGGESTED_NAME);
			this.availableSoundSheetLabels = args.getStringArrayList(KEY_AVAILABLE_SOUND_SHEET_LABELS);
			this.availableSoundSheetIds = args.getStringArrayList(KEY_AVAILABLE_SOUND_SHEET_IDS);
		}
		this.soundSheetsAlreadyExists = this.availableSoundSheetLabels != null;

		this.soundSheetsDataStorage = SoundActivity.getSoundSheetsDataStorage();
		this.soundSheetsDataUtil = SoundActivity.getSoundSheetsDataUtil();
	}

	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState)
	{
		if (!this.soundSheetsAlreadyExists)
			return this.createDialogIfNoSheetsExists();
		else
			return this.createDialogToSelectSoundSheet();
	}

	private Dialog createDialogIfNoSheetsExists()
	{
		@SuppressLint("InflateParams") View view = this.getActivity().getLayoutInflater().inflate(R.layout.dialog_add_new_sound_from_intent, null);
		this.soundName = (CustomEditText)view.findViewById(R.id.et_name_file);
		this.soundSheetName = (CustomEditText)view.findViewById(R.id.et_name_new_sound_sheet);

		view.findViewById(R.id.b_cancel).setOnClickListener(this);
		view.findViewById(R.id.b_ok).setOnClickListener(this);

		AlertDialog.Builder builder = new AlertDialog.Builder(this.getActivity());
		builder.setView(view);

		return builder.create();
	}

	private Dialog createDialogToSelectSoundSheet()
	{
		@SuppressLint("InflateParams") View view = this.getActivity().getLayoutInflater().inflate(R.layout.dialog_add_new_sound_from_intent_to_sound_sheet, null);

		this.soundName = (CustomEditText)view.findViewById(R.id.et_name_file);
		this.soundSheetName = (CustomEditText)view.findViewById(R.id.et_name_new_sound_sheet);
		this.soundSheetSpinner = (CustomSpinner)view.findViewById(R.id.s_sound_sheets);
		this.addNewSoundSheet = (CheckBox)view.findViewById(R.id.cb_add_new_sound_sheet);

		this.addNewSoundSheet.setOnCheckedChangeListener(this);

		view.findViewById(R.id.b_cancel).setOnClickListener(this);
		view.findViewById(R.id.b_ok).setOnClickListener(this);

		AlertDialog.Builder builder = new AlertDialog.Builder(this.getActivity());
		builder.setView(view);

		return builder.create();
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState)
	{
		super.onActivityCreated(savedInstanceState);
		this.soundName.setText(FileUtils.stripFileTypeFromName(FileUtils.getFileNameFromUri(this.getActivity(), this.uri)));
		this.soundSheetName.setHint(this.suggestedName);
		if (this.soundSheetsAlreadyExists)
		{
			this.soundSheetSpinner.setItems(this.availableSoundSheetLabels);
			this.soundSheetName.setVisibility(View.GONE);
		}
	}

	@Override
	public void onCheckedChanged(CompoundButton buttonView, boolean isChecked)
	{
		if (isChecked)
		{
			soundSheetSpinner.setVisibility(View.GONE);
			soundSheetName.setVisibility(View.VISIBLE);
		}
		else
		{
			soundSheetName.setVisibility(View.GONE);
			soundSheetSpinner.setVisibility(View.VISIBLE);
		}
	}

	@Override
	public void onClick(View v)
	{
		switch (v.getId())
		{
			case R.id.b_cancel:
				this.dismiss();
				break;
			case R.id.b_ok:
				this.deliverResult();
				this.dismiss();
				break;
		}
	}

	private void deliverResult()
	{
		String newSoundSheetLabel = soundSheetName.getDisplayedText();
		String soundSheetFragmentTag = this.availableSoundSheetIds.get(this.soundSheetSpinner.getSelectedItemPosition());
		if (!this.soundSheetsAlreadyExists || this.addNewSoundSheet.isChecked())
			soundSheetFragmentTag = this.addNewSoundSheet(newSoundSheetLabel);

		String soundLabel = this.soundName.getText().toString();
		Uri soundUri = this.uri;

		MediaPlayerData mediaPlayerData = EnhancedMediaPlayer.getMediaPlayerData(soundSheetFragmentTag, soundUri, soundLabel);
		EventBus.getDefault().post(new AddNewSoundEvent(mediaPlayerData, false));
	}

	private String addNewSoundSheet(String label)
	{
		SoundSheet newSoundSheet = this.soundSheetsDataUtil.getNewSoundSheet(label);
		return this.soundSheetsDataStorage.addOrUpdateSoundSheet(newSoundSheet);
	}

	private static ArrayList<String> getLabelsFromSoundSheets(List<SoundSheet> soundSheets)
	{
		ArrayList<String> labels = new ArrayList<>();
		for (SoundSheet soundSheet : soundSheets)
			labels.add(soundSheet.getLabel());
		return labels;
	}

	private static ArrayList<String> getIdsFromSoundSheets(List<SoundSheet> soundSheets)
	{
		ArrayList<String> labels = new ArrayList<>();
		for (SoundSheet soundSheet : soundSheets)
			labels.add(soundSheet.getFragmentTag());
		return labels;
	}

}