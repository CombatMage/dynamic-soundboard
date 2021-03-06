package org.neidhardt.dynamicsoundboard.dialog.fileexplorer

import android.annotation.SuppressLint
import android.app.Dialog
import android.os.Bundle
import android.os.Environment
import android.support.v4.app.FragmentManager
import android.support.v7.app.AlertDialog
import android.support.v7.widget.DefaultItemAnimator
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.util.Log
import io.reactivex.Observable
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.dialog_add_new_sound.view.*
import org.neidhardt.androidutils.recyclerview_utils.decoration.DividerItemDecoration
import org.neidhardt.dynamicsoundboard.R
import org.neidhardt.dynamicsoundboard.SoundboardApplication
import org.neidhardt.dynamicsoundboard.dialog.fileexplorer.base.FileExplorerDialog
import org.neidhardt.dynamicsoundboard.manager.findByFragmentTag
import org.neidhardt.dynamicsoundboard.mediaplayer.MediaPlayerFactory
import java.io.File

/**
 * Project created by Eric Neidhardt on 30.09.2014.
 */
open class AddNewSoundFromDirectoryDialog : FileExplorerDialog() {

	private val soundManager = SoundboardApplication.soundManager
	private val soundSheetsManager = SoundboardApplication.soundSheetManager

	protected var callingFragmentTag: String? = null

	private var directories: RecyclerView? = null

	companion object {
		private val TAG = AddNewSoundFromDirectoryDialog::class.java.name

		fun showInstance(manager: FragmentManager, callingFragmentTag: String) {
			val dialog = AddNewSoundFromDirectoryDialog()

			val args = Bundle()
			args.putString(KEY_CALLING_FRAGMENT_TAG, callingFragmentTag)
			dialog.arguments = args

			dialog.show(manager, TAG)
		}
	}

	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)

		val args = this.arguments
		if (args != null)
			this.callingFragmentTag = args.getString(KEY_CALLING_FRAGMENT_TAG)
	}

	override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
		@SuppressLint("InflateParams")
		val view = this.activity.layoutInflater.inflate(R.layout.dialog_add_new_sound_from_directory, null)

		this.directories = view.rv_dialog.apply {
			this.addItemDecoration(DividerItemDecoration(this.context, R.color.background, R.color.divider))
			this.layoutManager = LinearLayoutManager(this.context)
			this.itemAnimator = DefaultItemAnimator()
		}
		this.directories?.adapter = super.adapter

		val previousPath = this.getPathFromSharedPreferences(TAG)
		val musicDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_MUSIC)

		// use previous dir if still valid, else fallback
		if (previousPath == null) {
			super.setStartDirectoryForAdapter(musicDir)
		} else {
			val previousMusicDir = File(previousPath)
			if (previousMusicDir.exists()) {
				super.setStartDirectoryForAdapter(previousMusicDir)
			} else {
				super.setStartDirectoryForAdapter(musicDir)
			}
		}

		return AlertDialog.Builder(this.activity).apply {
			this.setView(view)
			this.setNegativeButton(R.string.all_cancel, { _, _ -> dismiss() })
			this.setPositiveButton(R.string.all_add, { _, _ -> onConfirm() })
		}.create()
	}

	override fun onFileSelected(selectedFile: File) {
		val position = super.adapter.displayedFiles.indexOf(selectedFile)
		this.directories!!.scrollToPosition(position)
	}

	override fun canSelectDirectory(): Boolean = true

	override fun canSelectFile(): Boolean = true

	override fun canSelectMultipleFiles(): Boolean = true

	private fun onConfirm() {
		val currentDirectory = super.adapter.rootDirectory
		if (currentDirectory != null)
			this.storePathToSharedPreferences(TAG, currentDirectory.path)

		this.returnResults()
	}

	protected fun getFileListResult(): Collection<File> {
		val files = HashSet<File>()
		val adapter = super.adapter

		// merge all files to single list, remove duplicates
		for (file in adapter.selectedFiles) {
			if (!file.isDirectory)
				files.add(file)
			else
				file.listFiles()?.forEach { files.add(it) }
		}

		return files
	}

	protected open fun returnResults() {
		val fragmentToAddSounds = this.callingFragmentTag
		if (fragmentToAddSounds == null) {
			this.dismiss()
			return
		}

		val filesInDir = this.getFileListResult()
		if (filesInDir.isEmpty()) {
			this.dismiss()
			return
		}

		val soundSheet = this.soundSheetsManager.soundSheets.findByFragmentTag(fragmentToAddSounds)
				?: throw IllegalStateException("no soundSheet for given fragmentTag was found")

		Observable.just(filesInDir)
				.subscribeOn(Schedulers.computation())
				.map { files ->
					files.map { singleFile ->
						MediaPlayerFactory.getMediaPlayerDataFromFile(singleFile, fragmentToAddSounds)
					}
				}
				.subscribe({ playerData ->
					soundManager.add(soundSheet, playerData)
				}, { error ->
					Log.e(TAG, error.toString())
					this@AddNewSoundFromDirectoryDialog.dismiss()
				}, {
					this@AddNewSoundFromDirectoryDialog.dismiss()
				})
	}
}
