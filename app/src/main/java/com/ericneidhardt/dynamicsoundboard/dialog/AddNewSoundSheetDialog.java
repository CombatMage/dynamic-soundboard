package com.ericneidhardt.dynamicsoundboard.dialog;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.FragmentManager;
import android.os.Bundle;
import android.view.View;
import com.ericneidhardt.dynamicsoundboard.R;
import com.ericneidhardt.dynamicsoundboard.customview.CustomEditText;
import com.ericneidhardt.dynamicsoundboard.storage.SoundSheetManagerFragment;


public class AddNewSoundSheetDialog extends BaseDialog implements View.OnClickListener
{
	public static final String TAG = AddNewSoundSheetDialog.class.getSimpleName();

	private static final String KEY_SUGGESTED_NAME = "com.ericneidhardt.dynamicsoundboard.dialog.AddNewSoundSheetDialog.suggestedName";

	private CustomEditText soundSheetName;
	private String suggestedName;

	public static void showInstance(FragmentManager manager, String suggestedName)
	{
		AddNewSoundSheetDialog dialog = new AddNewSoundSheetDialog();

		Bundle args = new Bundle();
		args.putString(KEY_SUGGESTED_NAME, suggestedName);
		dialog.setArguments(args);

		dialog.show(manager, TAG);
	}

	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);

		Bundle args = this.getArguments();
		if (args != null)
			this.suggestedName = args.getString(KEY_SUGGESTED_NAME);
	}

	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState)
	{
		View view = this.getActivity().getLayoutInflater().inflate(R.layout.dialog_add_new_sound_layout, null);
		this.soundSheetName = (CustomEditText)view.findViewById(R.id.et_name_new_sound_sheet);
		this.soundSheetName.setHint(this.suggestedName);

		view.findViewById(R.id.b_cancel).setOnClickListener(this);
		view.findViewById(R.id.b_add).setOnClickListener(this);

		AlertDialog.Builder builder = new AlertDialog.Builder(this.getActivity());
		builder.setView(view);

		return builder.create();
	}

	@Override
	public void onClick(View v)
	{
		switch (v.getId())
		{
			case R.id.b_cancel:
				this.dismiss();
				break;
			case R.id.b_add:
				this.deliverResult();
				this.dismiss();
		}
	}

	private void deliverResult()
	{
		SoundSheetManagerFragment caller = this.getSoundSheetManagerFragment();
		if (caller == null)
			return;
		String label = this.soundSheetName.getDisplayedText();
		caller.addSoundSheetAndNotifyFragment(caller.getNewSoundSheet(label));
	}


}
