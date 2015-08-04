package org.neidhardt.dynamicsoundboard.soundmanagement.model

import org.neidhardt.dynamicsoundboard.dao.SoundLayout
import org.neidhardt.dynamicsoundboard.mediaplayer.EnhancedMediaPlayer
import org.neidhardt.dynamicsoundboard.soundlayoutmanagement.model.SoundLayoutsAccess

/**
 * File created by eric.neidhardt on 15.06.2015.
 */
internal val DB_SOUNDS_DEFAULT = "org.neidhardt.dynamicsoundboard.storage.SoundManagerFragment.db_sounds"
internal val DB_SOUNDS_PLAYLIST_DEFAULT = "org.neidhardt.dynamicsoundboard.storage.SoundManagerFragment.db_sounds_playlist"

private val DB_SOUNDS = "db_sounds"
private val DB_SOUNDS_PLAYLIST = "db_sounds_playlist"

internal fun getDatabaseNameSounds(soundLayoutsAccess: SoundLayoutsAccess): String
{
	val activeLayout = soundLayoutsAccess.getActiveSoundLayout()
	if (activeLayout.isDefaultLayout())
		return DB_SOUNDS_DEFAULT
	val baseName = activeLayout.getDatabaseId()
	return baseName + DB_SOUNDS
}

internal fun getDatabaseNamePlayList(soundLayoutsAccess: SoundLayoutsAccess): String
{
	val activeLayout = soundLayoutsAccess.getActiveSoundLayout()
	if (activeLayout.isDefaultLayout())
		return DB_SOUNDS_PLAYLIST_DEFAULT
	val baseName = activeLayout.getDatabaseId()
	return baseName + DB_SOUNDS_PLAYLIST
}

internal fun searchInListForId(playerId: String, sounds: List<EnhancedMediaPlayer>): EnhancedMediaPlayer?
{
	return sounds.firstOrNull { player -> player.getMediaPlayerData().getPlayerId() == playerId }
}

internal fun searchInMapForId(playerId: String, sounds: Map<String, List<EnhancedMediaPlayer>>): EnhancedMediaPlayer?
{
	val soundSheets = sounds.keySet()
	for (soundSheet in soundSheets)
	{
		val playersInSoundSheet = sounds.get(soundSheet)
		val player = searchInListForId(playerId, playersInSoundSheet.orEmpty())
		if (player != null)
			return player
	}
	return null
}

