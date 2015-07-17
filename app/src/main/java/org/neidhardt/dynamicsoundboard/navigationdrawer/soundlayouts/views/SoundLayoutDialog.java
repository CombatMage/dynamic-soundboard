package org.neidhardt.dynamicsoundboard.navigationdrawer.soundlayouts.views;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatDialog;
import android.view.View;
import org.neidhardt.dynamicsoundboard.R;
import org.neidhardt.dynamicsoundboard.views.BaseDialog;
import org.neidhardt.dynamicsoundboard.views.edittext.CustomEditText;

/**
 * File created by eric.neidhardt on 12.03.2015.
 */
public abstract class SoundLayoutDialog extends BaseDialog implements View.OnClickListener
{
	protected CustomEditText soundLayoutName;

	@NonNull
	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState)
	{
		@SuppressLint("InflateParams") View view = this.getActivity().getLayoutInflater().inflate(this.getLayoutId(), null);
		this.soundLayoutName = (CustomEditText)view.findViewById(R.id.et_name_sound_layout);
		this.soundLayoutName.setHint(this.getHintForName());

		view.findViewById(R.id.b_cancel).setOnClickListener(this);
		view.findViewById(R.id.b_ok).setOnClickListener(this);

		AppCompatDialog dialog = new AppCompatDialog(this.getActivity(), R.style.DialogThemeNoTitle);
		dialog.setContentView(view);

		return dialog;
	}

	protected abstract int getLayoutId();

	protected abstract String getHintForName();

	@Override
	public void onClick(@NonNull View v)
	{
		int id = v.getId();
		if (id == R.id.b_cancel)
			this.dismiss();
		else if (id == R.id.b_ok)
		{
			this.deliverResult();
			this.dismiss();
		}
	}

	protected abstract void deliverResult();
}
