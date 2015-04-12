package org.neidhardt.dynamicsoundboard.mediaplayer;

import org.neidhardt.dynamicsoundboard.dao.MediaPlayerData;

/**
 * Created by eric.neidhardt on 11.02.2015.
 */
public class MediaPlayerCompletedEvent
{
	private MediaPlayerData data;

	public MediaPlayerCompletedEvent(MediaPlayerData data)
	{
		this.data = data;
	}

	public MediaPlayerData getData()
	{
		return data;
	}
}
