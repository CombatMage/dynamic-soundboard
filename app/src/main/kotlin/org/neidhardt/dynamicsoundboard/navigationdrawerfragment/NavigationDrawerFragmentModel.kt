package org.neidhardt.dynamicsoundboard.navigationdrawerfragment

import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import org.neidhardt.dynamicsoundboard.manager.RxNewSoundSheetManager
import org.neidhardt.dynamicsoundboard.manager.SoundSheetManager
import org.neidhardt.dynamicsoundboard.model.SoundSheet

/**
 * Created by eric.neidhardt@gmail.com on 04.09.2017.
 */
class NavigationDrawerFragmentModel(
		private val soundSheetManager: SoundSheetManager
) : NavigationDrawerFragmentContract.Model {

	override val soundSheets: Observable<List<SoundSheet>>
		get() {
			return RxNewSoundSheetManager.soundSheetsChanged(this.soundSheetManager)
					.observeOn(AndroidSchedulers.mainThread())
		}

	override fun setSoundSheetSelected(soundSheet: SoundSheet) {
		this.soundSheetManager.setSelected(soundSheet)
	}
}