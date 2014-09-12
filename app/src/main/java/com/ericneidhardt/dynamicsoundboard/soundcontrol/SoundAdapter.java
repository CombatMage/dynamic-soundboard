package com.ericneidhardt.dynamicsoundboard.soundcontrol;

import android.media.MediaPlayer;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import com.ericneidhardt.dynamicsoundboard.R;
import com.ericneidhardt.dynamicsoundboard.customview.CustomEditText;
import com.ericneidhardt.dynamicsoundboard.customview.DialogEditText;
import com.ericneidhardt.dynamicsoundboard.dao.MediaPlayerData;
import com.ericneidhardt.dynamicsoundboard.mediaplayer.EnhancedMediaPlayer;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by eric.neidhardt on 10.09.2014.
 */
public class SoundAdapter extends RecyclerView.Adapter<SoundAdapter.ViewHolder> implements MediaPlayer.OnCompletionListener
{
	private List<EnhancedMediaPlayer> mediaPlayers;

	public SoundAdapter()
	{
		this.mediaPlayers = new ArrayList<EnhancedMediaPlayer>();
	}

	public void add(EnhancedMediaPlayer mediaPlayer)
	{
		mediaPlayer.setOnCompletionListener(this);
		this.mediaPlayers.add(mediaPlayer);
		this.notifyItemInserted(this.mediaPlayers.size());
	}

	public void addAll(List<EnhancedMediaPlayer> mediaPlayers)
	{
		if (mediaPlayers == null)
			return;

		for (EnhancedMediaPlayer player : mediaPlayers)
			player.setOnCompletionListener(this);

		this.mediaPlayers.addAll(mediaPlayers);
		this.notifyDataSetChanged();
	}

	public void remove(EnhancedMediaPlayer mediaPlayer)
	{
		int position = this.mediaPlayers.indexOf(mediaPlayer);
		this.mediaPlayers.remove(position);
		this.notifyItemRemoved(position);
	}

	public void clear()
	{
		this.mediaPlayers.clear();
		this.notifyDataSetChanged();
	}

	public List<EnhancedMediaPlayer> getValues()
	{
		return this.mediaPlayers;
	}

	@Override
	public int getItemCount()
	{
		return this.mediaPlayers.size();
	}

	@Override
	public ViewHolder onCreateViewHolder(ViewGroup parent, int position)
	{
		View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.view_sound_item, parent, false);
		return new ViewHolder(view);
	}

	@Override
	public void onBindViewHolder(ViewHolder holder, int position)
	{
		EnhancedMediaPlayer player = this.mediaPlayers.get(position);
		MediaPlayerData data = player.getMediaPlayerData();

		holder.name.setText(data.getLabel());
		holder.play.setChecked(player.isPlaying());
		holder.loop.setChecked(data.getIsLoop());
		holder.inPlaylist.setChecked(data.getIsInPlaylist());
	}

	@Override
	public void onCompletion(MediaPlayer mp)
	{
		this.notifyItemChanged(this.mediaPlayers.indexOf(mp));
	}

	public class ViewHolder
			extends
				RecyclerView.ViewHolder
			implements
				View.OnClickListener,
				CustomEditText.OnTextEditedListener,
				CompoundButton.OnCheckedChangeListener
	{
		private DialogEditText name;
		private CheckBox play;
		private CheckBox loop;
		private CheckBox inPlaylist;
		private View stop;

		public ViewHolder(View itemView)
		{
			super(itemView);

			this.name = (DialogEditText)itemView.findViewById(R.id.et_name_file);
			this.play = (CheckBox) itemView.findViewById(R.id.cb_play);
			this.loop = (CheckBox) itemView.findViewById(R.id.cb_loop);
			this.inPlaylist = (CheckBox)itemView.findViewById(R.id.cb_add_to_playlist);
			this.stop = itemView.findViewById(R.id.b_stop);

			this.name.setOnTextEditedListener(this);
			this.play.setOnCheckedChangeListener(this);
			this.loop.setOnCheckedChangeListener(this);
			this.inPlaylist.setOnCheckedChangeListener(this);
			this.stop.setOnClickListener(this);
		}

		@Override
		public void onTextEdited(String text)
		{
			mediaPlayers.get(this.getPosition()).getMediaPlayerData().setLabel(text);
		}

		@Override
		public void onCheckedChanged(CompoundButton buttonView, boolean isChecked)
		{
			EnhancedMediaPlayer player = mediaPlayers.get(this.getPosition());
			switch (buttonView.getId())
			{
				case R.id.cb_play:
					if (isChecked)
						player.playSound();
					else
						player.pauseSound();
					break;
				case R.id.cb_loop:
					player.setLooping(isChecked);
					break;
				case R.id.cb_add_to_playlist:
					player.setInPlaylist(isChecked);
					break;
			}
		}

		@Override
		public void onClick(View view)
		{
			EnhancedMediaPlayer player = mediaPlayers.get(this.getPosition());
			switch (view.getId())
			{
				case R.id.b_stop:
					player.stopSound();
					notifyItemChanged(getPosition());
					break;
			}
		}
	}
}
