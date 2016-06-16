package org.neidhardt.dynamicsoundboard.soundmanagement.dialog

import android.annotation.SuppressLint
import android.app.Dialog
import android.net.Uri
import android.os.Bundle
import android.support.v4.app.FragmentManager
import android.support.v7.app.AlertDialog
import android.view.View
import android.widget.CheckBox
import android.widget.CompoundButton
import android.widget.EditText
import org.neidhardt.dynamicsoundboard.R
import org.neidhardt.dynamicsoundboard.SoundboardApplication
import org.neidhardt.dynamicsoundboard.dao.MediaPlayerData
import org.neidhardt.dynamicsoundboard.dao.SoundSheet
import org.neidhardt.dynamicsoundboard.misc.FileUtils
import org.neidhardt.dynamicsoundboard.views.BaseDialog
import org.neidhardt.ui_utils.views.SimpleSpinner
import java.util.*

class AddNewSoundFromIntentDialog : BaseDialog(), CompoundButton.OnCheckedChangeListener
{
	private val soundsDataStorage = SoundboardApplication.soundsDataStorage
	private val soundSheetsDataStorage = SoundboardApplication.soundSheetsDataStorage
	private val soundSheetsDataUtil = SoundboardApplication.soundSheetsDataUtil

	private var soundName: EditText? = null
	private var soundSheetName: EditText? = null
	private var soundSheetSpinner: SimpleSpinner? = null
	private var addNewSoundSheet: CheckBox? = null

	private var uri: Uri? = null
	private var suggestedName: String? = null
	private var availableSoundSheetLabels: List<String>? = null
	private var availableSoundSheetIds: List<String>? = null

	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)

		val args = this.arguments
		if (args != null) {
			this.uri = Uri.parse(args.getString(KEY_SOUND_URI))
			this.suggestedName = args.getString(KEY_SUGGESTED_NAME)
			this.availableSoundSheetLabels = args.getStringArrayList(KEY_AVAILABLE_SOUND_SHEET_LABELS)
			this.availableSoundSheetIds = args.getStringArrayList(KEY_AVAILABLE_SOUND_SHEET_IDS)
		}
	}

	override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
		if (this.availableSoundSheetLabels == null)
			return this.createDialogIfNoSheetsExists()
		else
			return this.createDialogToSelectSoundSheet()
	}

	private fun createDialogIfNoSheetsExists(): Dialog {
		@SuppressLint("InflateParams") val view = this.activity.layoutInflater.inflate(R.layout.dialog_add_new_sound_from_intent, null)
		this.soundName = view.findViewById(R.id.et_name_file) as EditText
		this.soundSheetName = view.findViewById(R.id.et_name_new_sound_sheet) as EditText

		return AlertDialog.Builder(context).apply {
			this.setTitle(R.string.dialog_add_new_sound_from_intent_title)
			this.setView(view)
			this.setPositiveButton(R.string.dialog_add, { dialogInterface, i ->
				deliverResult()
				dismiss()
			})
			this.setNegativeButton(R.string.dialog_cancel, { dialogInterface, i -> dismiss()})
		}.create()
	}

	private fun createDialogToSelectSoundSheet(): Dialog {
		@SuppressLint("InflateParams") val view = this.activity.layoutInflater.inflate(R.layout.dialog_add_new_sound_from_intent_to_sound_sheet, null)

		this.soundName = view.findViewById(R.id.et_name_file) as EditText
		this.soundSheetName = view.findViewById(R.id.et_name_new_sound_sheet) as EditText
		this.soundSheetSpinner = view.findViewById(R.id.s_sound_sheets) as SimpleSpinner
		this.addNewSoundSheet = view.findViewById(R.id.cb_add_new_sound_sheet) as CheckBox

		this.addNewSoundSheet!!.setOnCheckedChangeListener(this)

		return AlertDialog.Builder(context).apply {
			this.setTitle(R.string.dialog_add_new_sound_from_intent_title)
			this.setView(view)
			this.setPositiveButton(R.string.dialog_add, { dialogInterface, i ->
				deliverResult()
				dismiss()
			})
			this.setNegativeButton(R.string.dialog_cancel, { dialogInterface, i -> dismiss()})
		}.create()
	}

	override fun onActivityCreated(savedInstanceState: Bundle?)
	{
		super.onActivityCreated(savedInstanceState)

		if (this.uri != null)
			this.soundName!!.setText(FileUtils.stripFileTypeFromName(FileUtils.getFileNameFromUri(this.activity, this.uri as Uri)))

		val soundSheetLabels = this.availableSoundSheetLabels
		if (soundSheetLabels != null)
		{
			this.soundSheetSpinner?.items = soundSheetLabels
			this.soundSheetName?.visibility = View.GONE
		}
	}

	override fun onCheckedChanged(buttonView: CompoundButton, isChecked: Boolean) {
		if (isChecked) {
			soundSheetSpinner!!.visibility = View.GONE
			soundSheetName!!.visibility = View.VISIBLE
		} else {
			soundSheetName!!.visibility = View.GONE
			soundSheetSpinner!!.visibility = View.VISIBLE
		}
	}

	private fun deliverResult() {
		var newSoundSheetLabel = soundSheetName!!.text.toString()
		if (newSoundSheetLabel.length == 0)
			newSoundSheetLabel = this.suggestedName as String

		val soundSheetFragmentTag: String
		if (this.availableSoundSheetLabels == null || this.addNewSoundSheet!!.isChecked)
			soundSheetFragmentTag = this.addNewSoundSheet(newSoundSheetLabel)
		else
			soundSheetFragmentTag = this.availableSoundSheetIds!![this.soundSheetSpinner!!.selectedItemPosition]

		val soundLabel = this.soundName!!.text.toString()
		val soundUri = this.uri

		val mediaPlayerData = MediaPlayerData.getNewMediaPlayerData(soundSheetFragmentTag, soundUri, soundLabel)
		this.soundsDataStorage.createSoundAndAddToManager(mediaPlayerData)
	}

	private fun addNewSoundSheet(label: String): String {
		val newSoundSheet = this.soundSheetsDataUtil.getNewSoundSheet(label)
		newSoundSheet.isSelected = true
		this.soundSheetsDataStorage.addSoundSheetToManager(newSoundSheet)
		return newSoundSheet.fragmentTag
	}

	companion object {

		private val TAG = AddNewSoundFromIntentDialog::class.java.name

		private val KEY_SOUND_URI = "org.neidhardt.dynamicsoundboard.soundmanagement.views.AddNewSoundFromIntent.uri"
		private val KEY_SUGGESTED_NAME = "org.neidhardt.dynamicsoundboard.soundmanagement.views.AddNewSoundFromIntent.suggestedName"
		private val KEY_AVAILABLE_SOUND_SHEET_LABELS = "org.neidhardt.dynamicsoundboard.soundmanagement.views.AddNewSoundFromIntent.availableSoundSheetLabels"
		private val KEY_AVAILABLE_SOUND_SHEET_IDS = "org.neidhardt.dynamicsoundboard.soundmanagement.views.AddNewSoundFromIntent.availableSoundSheetIds"

		fun showInstance(manager: FragmentManager, uri: Uri, suggestedName: String, availableSoundSheets: List<SoundSheet>?) {
			val dialog = AddNewSoundFromIntentDialog()

			val args = Bundle()
			args.putString(KEY_SOUND_URI, uri.toString())
			args.putString(KEY_SUGGESTED_NAME, suggestedName)
			if (availableSoundSheets != null) {
				args.putStringArrayList(KEY_AVAILABLE_SOUND_SHEET_LABELS, getLabelsFromSoundSheets(availableSoundSheets))
				args.putStringArrayList(KEY_AVAILABLE_SOUND_SHEET_IDS, getIdsFromSoundSheets(availableSoundSheets))
			}
			dialog.arguments = args

			dialog.show(manager, TAG)
		}

		private fun getLabelsFromSoundSheets(soundSheets: List<SoundSheet>): ArrayList<String> {
			val labels = ArrayList<String>()
			for (soundSheet in soundSheets)
				labels.add(soundSheet.label)
			return labels
		}

		private fun getIdsFromSoundSheets(soundSheets: List<SoundSheet>): ArrayList<String> {
			val labels = ArrayList<String>()
			for (soundSheet in soundSheets)
				labels.add(soundSheet.fragmentTag)
			return labels
		}
	}

}