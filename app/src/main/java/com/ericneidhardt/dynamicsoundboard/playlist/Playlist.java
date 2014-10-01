package com.ericneidhardt.dynamicsoundboard.playlist;

import android.content.Context;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import com.ericneidhardt.dynamicsoundboard.NavigationDrawerFragment;
import com.ericneidhardt.dynamicsoundboard.R;
import com.ericneidhardt.dynamicsoundboard.customview.DividerItemDecoration;
import com.ericneidhardt.dynamicsoundboard.customview.NavigationDrawerList;
import com.ericneidhardt.dynamicsoundboard.mediaplayer.EnhancedMediaPlayer;
import com.ericneidhardt.dynamicsoundboard.storage.SoundManagerFragment;

import java.util.ArrayList;
import java.util.List;

/**
 * Project created by eric.neidhardt on 27.08.2014.
 */
public class Playlist extends NavigationDrawerList implements PlaylistAdapter.OnItemClickListener
{
	public static final String TAG = Playlist.class.getSimpleName();

	private PlaylistAdapter adapter;

	@SuppressWarnings("unused")
	public Playlist(Context context)
	{
		super(context);
		this.inflateLayout(context);
		this.initRecycleView(context);
	}

	@SuppressWarnings("unused")
	public Playlist(Context context, AttributeSet attrs)
	{
		super(context, attrs);
		this.inflateLayout(context);
		this.initRecycleView(context);
	}

	@SuppressWarnings("unused")
	public Playlist(Context context, AttributeSet attrs, int defStyle)
	{
		super(context, attrs, defStyle);
		this.inflateLayout(context);
		this.initRecycleView(context);
	}

	private void inflateLayout(Context context)
	{
		LayoutInflater.from(context).inflate(R.layout.view_playlist, this, true);
	}

	private void initRecycleView(Context context)
	{
		RecyclerView playlist = (RecyclerView)this.findViewById(R.id.rv_playlist);
		playlist.addItemDecoration(new DividerItemDecoration(context, DividerItemDecoration.VERTICAL_LIST, null));
		playlist.setLayoutManager(new LinearLayoutManager(context));
		playlist.setItemAnimator(new DefaultItemAnimator());

		this.adapter = new PlaylistAdapter();
		this.adapter.setOnItemClickListener(this);
		playlist.setAdapter(this.adapter);
	}

	public void onActivityCreated(NavigationDrawerFragment parent)
	{
		super.parent = parent;
		this.notifyDataSetChanged(true);
	}

	@Override
	public void onItemClick(View view, EnhancedMediaPlayer player, int position)
	{
		if (super.isInSelectionMode)
			super.onItemSelected(view, position);
		else if (this.parent != null)
			this.adapter.startPlayList(player, position);
	}

	@Override
	protected List<View> getAllItems()
	{
		List<View> viewsInPlayList = new ArrayList<View>(this.adapter.getItemCount());
		RecyclerView playlist = (RecyclerView)this.findViewById(R.id.rv_playlist);
		for (int i = 0; i < this.adapter.getItemCount(); i++)
			viewsInPlayList.add(playlist.getChildAt(i));
		return viewsInPlayList;
	}

	@Override
	protected void onDeleteSelected(SparseArray<View> selectedItems)
	{
		List<EnhancedMediaPlayer> playersToRemove = new ArrayList<EnhancedMediaPlayer>(selectedItems.size());
		for(int i = 0; i < selectedItems.size(); i++) {
			int index = selectedItems.keyAt(i);
			playersToRemove.add(this.adapter.getValues().get(index));
		}

		this.adapter.removeAll(playersToRemove);

		SoundManagerFragment soundManagerFragment = (SoundManagerFragment) this.parent.getFragmentManager()
				.findFragmentByTag(SoundManagerFragment.TAG);

		soundManagerFragment.removeFromPlaylist(playersToRemove);
		soundManagerFragment.notifyFragments();

		this.adapter.notifyDataSetChanged();
	}

	@Override
	protected int getItemCount() {
		return this.adapter.getItemCount();
	}

	public void notifyDataSetChanged(boolean newSoundAvailable)
	{
		if (newSoundAvailable)
		{
			SoundManagerFragment fragment = (SoundManagerFragment)this.parent.getFragmentManager().findFragmentByTag(SoundManagerFragment.TAG);
			this.adapter.clear();
			this.adapter.addAll(fragment.getPlayList());
		}
		this.adapter.notifyDataSetChanged();
	}

}