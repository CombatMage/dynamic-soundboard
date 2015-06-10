package org.neidhardt.dynamicsoundboard.mediaplayer.events;

/**
 * Created by eric.neidhardt on 29.05.2015.
 */
public interface MediaPlayerEventListener
{
	/**
	 * This is called by greenRobot EventBus in case a MediaPlayer changed his state (ie. start or stops playing).
	 * @param event delivered MediaPlayerStateChangedEvent
	 */
	void onEvent(MediaPlayerStateChangedEvent event);

	/**
	 * This is called by greenRobot EventBus in case a MediaPlayer has finished playing.
	 * @param event delivered MediaPlayerStateChangedEvent
	 */
	void onEvent(MediaPlayerCompletedEvent event);
}