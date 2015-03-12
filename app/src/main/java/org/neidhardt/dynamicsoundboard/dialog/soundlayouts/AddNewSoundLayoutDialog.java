package org.neidhardt.dynamicsoundboard.dialog.soundlayouts;

import android.app.FragmentManager;
import android.os.Bundle;
import org.neidhardt.dynamicsoundboard.NavigationDrawerFragment;
import org.neidhardt.dynamicsoundboard.R;

/**
 * Created by eric.neidhardt on 12.03.2015.
 */
public class AddNewSoundLayoutDialog extends SoundLayoutDialog
{
	private static final String TAG = AddNewSoundLayoutDialog.class.getName();

	private static final String KEY_SUGGESTED_NAME = "org.neidhardt.dynamicsoundboard.dialog.AddNewSoundLayoutDialog.suggestedName";

	private String suggestedName;

	public static void showInstance(FragmentManager manager, String suggestedName)
	{
		AddNewSoundLayoutDialog dialog = new AddNewSoundLayoutDialog();

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
	protected int getLayoutId()
	{
		return R.layout.dialog_add_new_sound_layout;
	}

	@Override
	protected String getHintForName()
	{
		return this.suggestedName;
	}

	@Override
	protected void deliverResult()
	{
		NavigationDrawerFragment fragment = this.getNavigationDrawerFragment();
		if (fragment != null)
			fragment.addNewSoundLayout(super.soundLayoutName.getDisplayedText());
	}
}
