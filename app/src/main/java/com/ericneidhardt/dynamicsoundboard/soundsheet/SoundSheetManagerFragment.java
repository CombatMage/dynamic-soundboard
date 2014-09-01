package com.ericneidhardt.dynamicsoundboard.soundsheet;

import android.app.AlertDialog;
import android.app.Fragment;
import android.os.Bundle;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import com.ericneidhardt.dynamicsoundboard.BaseActivity;
import com.ericneidhardt.dynamicsoundboard.DynamicSoundboardApplication;
import com.ericneidhardt.dynamicsoundboard.R;
import com.ericneidhardt.dynamicsoundboard.customview.SlidingTabLayout;
import com.ericneidhardt.dynamicsoundboard.dao.DaoSession;
import com.ericneidhardt.dynamicsoundboard.dao.SoundSheet;
import com.ericneidhardt.dynamicsoundboard.misc.Logger;
import com.ericneidhardt.dynamicsoundboard.misc.Util;
import com.ericneidhardt.dynamicsoundboard.misc.safeasyncTask.SafeAsyncTask;

import java.util.List;

/**
 * Created by Eric Neidhardt on 29.08.2014.
 */
public class SoundSheetManagerFragment extends Fragment implements View.OnClickListener, SoundSheetAdapter.OnItemClickedListener
{
	public static final String TAG = SoundSheetManagerFragment.class.getSimpleName();

	private static final String DB_SOUND_SHEETS = "com.ericneidhardt.dynamicsoundboard.db_sound_sheets";

	private SoundSheetAdapter soundSheetAdapter;
	private DaoSession daoSession;
	private TabContentAdapter tabContentAdapter;

	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		this.setRetainInstance(true);
		this.setHasOptionsMenu(true);

		this.daoSession = Util.setupDatabase(this.getActivity(), DB_SOUND_SHEETS);
		this.soundSheetAdapter = new SoundSheetAdapter();
		this.soundSheetAdapter.setOnItemClickedListener(this);
		this.tabContentAdapter = new TabContentAdapter();

		LoadSoundSheetsTask task = new LoadSoundSheetsTask();
		task.execute();
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState)
	{
		super.onActivityCreated(savedInstanceState);

		this.getActivity().findViewById(R.id.action_new_sound_sheet).setOnClickListener(this);
		this.buildNavigationDrawerTabLayout();
	}

	private void buildNavigationDrawerTabLayout()
	{
		ViewPager tabContent = (ViewPager) this.getActivity().findViewById(R.id.vp_tab_content);
		tabContent.setAdapter(this.tabContentAdapter);

		SlidingTabLayout tabLayout = (SlidingTabLayout) this.getActivity().findViewById(R.id.layout_tab);
		tabLayout.setViewPager(tabContent);
		tabLayout.setCustomTabColorizer(new SlidingTabLayout.TabColorizer()
		{
			@Override
			public int getIndicatorColor(int position)
			{
				if (position == 0)
					return getResources().getColor(R.color.accent_200);
				else
					return getResources().getColor(R.color.primary_500);
			}

			@Override
			public int getDividerColor(int position)
			{
				return 0;
			}
		});

		RecyclerView listSoundSheets = (RecyclerView)this.getActivity().findViewById(R.id.rv_sound_sheets);
		listSoundSheets.setLayoutManager(new LinearLayoutManager(this.getActivity()));
		listSoundSheets.setItemAnimator(new DefaultItemAnimator());
		listSoundSheets.setAdapter(this.soundSheetAdapter);
	}

	@Override
	public void onPause()
	{
		super.onPause();
		StoreSoundSheetsTask task = new StoreSoundSheetsTask(this.soundSheetAdapter.getValues());
		task.execute();
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item)
	{
		super.onOptionsItemSelected(item);
		switch (item.getItemId())
		{
			case R.id.action_clear_sound_sheets:
				this.soundSheetAdapter.clear();
				return true;
			default:
				return false;
		}
	}

	@Override
	public void onClick(View view)
	{
		switch (view.getId())
		{
			case R.id.action_new_sound_sheet:
				this.openDialogAddNewSoundLayout();
				break;
			default:
				Logger.e(TAG, "unknown item clicked " + view);
		}
	}

	@Override
	public void onItemClicked(View view, SoundSheet data, int position)
	{
		if (this.getActivity() != null)
		{
			BaseActivity activity = (BaseActivity)this.getActivity();
			activity.toggleNavigationDrawer();
			activity.openSoundFragment(data);
		}
	}

	private void openDialogAddNewSoundLayout()
	{
		final View dialogView = LayoutInflater.from(this.getActivity()).inflate(R.layout.dialog_add_new_sound_layout, null);
		((EditText)dialogView.findViewById(R.id.et_input)).setText("test" + this.soundSheetAdapter.getItemCount());

		AlertDialog.Builder inputNameDialog = new AlertDialog.Builder(this.getActivity());
		inputNameDialog.setView(dialogView);

		final AlertDialog dialog = inputNameDialog.create();
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
				String label = ((EditText)dialogView.findViewById(R.id.et_input)).getText().toString();
				String tag = Integer.toString((label + DynamicSoundboardApplication.getRandomNumber()).hashCode());
				soundSheetAdapter.add(new SoundSheet(null, tag, label));
				dialog.dismiss();
			}
		});

		dialog.show();
	}

	private class TabContentAdapter extends PagerAdapter
	{
		@Override
		public CharSequence getPageTitle(int position)
		{
			if (position == 0)
				return getResources().getString(R.string.tab_sound_sheets);
			else
				return getResources().getString(R.string.tab_play_list);
		}

		@Override
		public int getCount()
		{
			return 2;
		}

		@Override
		public boolean isViewFromObject(View view, Object object)
		{
			return view.equals(object);
		}

		@Override
		public Object instantiateItem(ViewGroup container, int position)
		{
			int resId = 0;
			switch (position) {
				case 0:
					resId = R.id.rv_sound_sheets;
					break;
				case 1:
					resId = R.id.lv_playlist;
					break;
			}
			return getActivity().findViewById(resId);
		}
	}

	private class LoadSoundSheetsTask extends SafeAsyncTask<List<SoundSheet>>
	{
		@Override
		public List<SoundSheet> call() throws Exception
		{
			return daoSession.getSoundSheetDao().queryBuilder().list();
		}

		@Override
		protected void onSuccess(List<SoundSheet> soundSheets) throws Exception
		{
			super.onSuccess(soundSheets);
			soundSheetAdapter.addAll(soundSheets);
		}

		@Override
		protected void onException(Exception e) throws RuntimeException
		{
			super.onException(e);
			Logger.e(TAG, e.getMessage());
		}
	}

	private class StoreSoundSheetsTask extends SafeAsyncTask<Void>
	{
		private List<SoundSheet> soundSheets;

		private StoreSoundSheetsTask(List<SoundSheet> soundSheets)
		{
			this.soundSheets = soundSheets;
		}

		@Override
		public Void call() throws Exception
		{
			daoSession.getSoundSheetDao().insertOrReplaceInTx(soundSheets);
			return null;
		}

		@Override
		protected void onException(Exception e) throws RuntimeException
		{
			super.onException(e);
			Logger.e(TAG, e.getMessage());
		}
	}
}