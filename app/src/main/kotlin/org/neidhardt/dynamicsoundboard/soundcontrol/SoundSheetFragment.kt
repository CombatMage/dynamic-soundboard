package org.neidhardt.dynamicsoundboard.soundcontrol

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import com.emtronics.dragsortrecycler.DragSortRecycler
import de.greenrobot.event.EventBus
import jp.wasabeef.recyclerview.animators.SlideInLeftAnimator
import org.neidhardt.dynamicsoundboard.DynamicSoundboardApplication
import org.neidhardt.dynamicsoundboard.R
import org.neidhardt.dynamicsoundboard.dao.SoundSheet
import org.neidhardt.dynamicsoundboard.fileexplorer.AddNewSoundFromDirectoryDialog
import org.neidhardt.dynamicsoundboard.mediaplayer.EnhancedMediaPlayer
import org.neidhardt.dynamicsoundboard.misc.FileUtils
import org.neidhardt.dynamicsoundboard.misc.IntentRequest
import org.neidhardt.dynamicsoundboard.misc.Logger
import org.neidhardt.dynamicsoundboard.soundactivity.BaseFragment
import org.neidhardt.dynamicsoundboard.soundcontrol.events.OnOpenSoundDialogEventListener
import org.neidhardt.dynamicsoundboard.soundcontrol.events.OpenSoundRenameEvent
import org.neidhardt.dynamicsoundboard.soundcontrol.events.OpenSoundSettingsEvent
import org.neidhardt.dynamicsoundboard.soundmanagement.dialog.AddNewSoundDialog
import org.neidhardt.dynamicsoundboard.soundmanagement.dialog.RenameSoundFileDialog
import org.neidhardt.dynamicsoundboard.soundmanagement.events.*
import org.neidhardt.dynamicsoundboard.soundmanagement.model.SoundsDataAccess
import org.neidhardt.dynamicsoundboard.soundmanagement.model.SoundsDataStorage
import org.neidhardt.dynamicsoundboard.soundmanagement.views.ConfirmDeleteSoundsDialog
import org.neidhardt.dynamicsoundboard.soundmanagement.views.SoundSettingsDialog
import org.neidhardt.dynamicsoundboard.soundsheetmanagement.views.ConfirmDeleteSoundSheetDialog
import org.neidhardt.dynamicsoundboard.views.floatingactionbutton.AddPauseFloatingActionButton
import org.neidhardt.dynamicsoundboard.views.recyclerviewhelpers.DividerItemDecoration

/**
 * File created by eric.neidhardt on 02.07.2015.
 */
public class SoundSheetFragment :
		BaseFragment(),
		DragSortRecycler.OnDragStateChangedListener,
		DragSortRecycler.OnItemMovedListener,
		OnOpenSoundDialogEventListener,
		OnSoundsChangedEventListener
{

	companion object
	{
		private val KEY_FRAGMENT_TAG = "org.neidhardt.dynamicsoundboard.soundcontrol.SoundSheetFragment.fragmentTag"
		private val LOG_TAG = SoundSheetFragment::class.java.name

		public fun getNewInstance(soundSheet: SoundSheet): SoundSheetFragment
		{
			val fragment = SoundSheetFragment()
			val args = Bundle()
			args.putString(KEY_FRAGMENT_TAG, soundSheet.fragmentTag)
			fragment.arguments = args
			return fragment
		}
	}

	public var fragmentTag: String = javaClass.name

	private var eventBus: EventBus = EventBus.getDefault()

	private var dragSortRecycler: SoundDragSortRecycler? = null
	private var scrollListener: SoundSheetScrollListener? = null
	private var soundPresenter: SoundPresenter? = null
	private var soundAdapter: SoundAdapter? = null
	private var soundLayout: RecyclerView? = null
	private val soundLayoutAnimator = SlideInLeftAnimator()

	var soundsDataStorage: SoundsDataStorage = DynamicSoundboardApplication.getSoundsDataStorage()
	var soundsDataAccess: SoundsDataAccess = DynamicSoundboardApplication.getSoundsDataAccess()

	override fun onCreate(savedInstanceState: Bundle?)
	{
		super.onCreate(savedInstanceState)

		this.retainInstance = true
		this.setHasOptionsMenu(true)

		val args = this.arguments

		var fragmentTag: String? = args.getString(KEY_FRAGMENT_TAG)
				?: throw NullPointerException(LOG_TAG + ": cannot create fragment, given fragmentTag is null")

		this.fragmentTag = fragmentTag as String

		this.soundPresenter = SoundPresenter(this.fragmentTag, this.eventBus, this.soundsDataAccess)
		this.soundAdapter = SoundAdapter(this.soundPresenter as SoundPresenter, this.soundsDataStorage, this.eventBus)
		this.soundPresenter!!.adapter = this.soundAdapter

		this.dragSortRecycler = SoundDragSortRecycler(R.id.b_reorder)
		this.dragSortRecycler!!.setOnItemMovedListener(this)
		this.dragSortRecycler!!.setOnDragStateChangedListener(this)
		this.scrollListener = SoundSheetScrollListener(this.dragSortRecycler)
	}

	override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View?
	{
		if (container == null)
			return null

		val fragmentView = inflater.inflate(R.layout.fragment_soundsheet, container, false)

		val soundLayout = fragmentView.findViewById(R.id.rv_sounds) as RecyclerView
		soundLayout.adapter = this.soundAdapter
		soundLayout.layoutManager = LinearLayoutManager(this.activity)
		soundLayout.itemAnimator = this.soundLayoutAnimator
		soundLayout.addItemDecoration(DividerItemDecoration())
		soundLayout.addItemDecoration(this.dragSortRecycler)
		soundLayout.addOnItemTouchListener(this.dragSortRecycler)
		soundLayout.addOnScrollListener(this.scrollListener)
		soundLayout.addOnScrollListener(this.dragSortRecycler!!.scrollListener)
		this.soundLayout = soundLayout

		this.soundAdapter?.recyclerView = this.soundLayout

		return fragmentView
	}

	override fun onStart()
	{
		super.onStart()
		if (!this.eventBus.isRegistered(this))
			this.eventBus.register(this)
	}

	override fun onResume()
	{
		super.onResume()

		val activity = this.baseActivity
		activity.setSoundSheetActionsEnable(true)
		activity.findViewById(R.id.action_add_sound).setOnClickListener({ view
			-> AddNewSoundDialog(this.fragmentManager, this.fragmentTag) })
		activity.findViewById(R.id.action_add_sound_dir).setOnClickListener({ view
			-> AddNewSoundFromDirectoryDialog.showInstance(this.fragmentManager, this.fragmentTag) })

		this.soundPresenter!!.onAttachedToWindow()
		this.attachScrollViewToFab()

		this.soundAdapter!!.startProgressUpdateTimer()
	}

	override fun onPause()
	{
		super.onPause()
		this.soundPresenter!!.onDetachedFromWindow()
		this.soundAdapter!!.stopProgressUpdateTimer()
	}

	override fun onStop()
	{
		super.onStop()
		this.eventBus.unregister(this)
	}

	private fun attachScrollViewToFab()
	{
		val fab = this.activity.findViewById(R.id.fab_add) as AddPauseFloatingActionButton?
		if (fab == null || this.soundLayout == null)
			return

		//TODO remove when behaviour is implemeted
		fab.attachToRecyclerView(this.soundLayout)
		fab.show(false)
	}

	override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?)
	{
		if (resultCode == Activity.RESULT_OK)
		{
			if (requestCode == IntentRequest.GET_AUDIO_FILE)
			{
				val soundUri = data!!.data
				val soundLabel = FileUtils.stripFileTypeFromName(FileUtils.getFileNameFromUri(this.activity, soundUri))
				val playerData = EnhancedMediaPlayer.getMediaPlayerData(this.fragmentTag, soundUri, soundLabel)

				this.soundsDataStorage.createSoundAndAddToManager(playerData)
				return
			}
		}
		super.onActivityResult(requestCode, resultCode, data)
	}

	override fun onOptionsItemSelected(item: MenuItem): Boolean
	{
		super.onOptionsItemSelected(item)
		when (item.itemId)
		{
			R.id.action_clear_sounds_in_sheet ->
			{
				ConfirmDeleteSoundsDialog.showInstance(this.fragmentManager, this.fragmentTag)
				return true
			}
			R.id.action_delete_sheet ->
			{
				ConfirmDeleteSoundSheetDialog.showInstance(this.fragmentManager, this.fragmentTag)
				return true
			}
			else -> return false
		}
	}

	override fun onDragStart()
	{
		Logger.d(LOG_TAG, "onDragStart")
		this.soundLayout!!.itemAnimator = null // drag does not work with default animator
		this.soundAdapter!!.stopProgressUpdateTimer()
	}

	override fun onDragStop()
	{
		Logger.d(LOG_TAG, "onDragStop")
		this.soundLayout!!.invalidateItemDecorations()
		this.soundAdapter!!.notifyDataSetChanged()
		this.soundLayout!!.itemAnimator = this.soundLayoutAnimator // add animator for delete animation
		this.soundAdapter!!.startProgressUpdateTimer()
	}

	override fun onItemMoved(from: Int, to: Int)
	{
		this.soundsDataStorage.moveSoundInFragment(fragmentTag, from, to)
	}

	override fun onEvent(event: OpenSoundRenameEvent)
	{
		RenameSoundFileDialog(this.fragmentManager, event.data)
	}

	override fun onEvent(event: OpenSoundSettingsEvent)
	{
		SoundSettingsDialog.showInstance(this.fragmentManager, event.data)
	}

	override fun onEventMainThread(event: SoundsRemovedEvent)
	{
		if (this.soundAdapter!!.getValues().size() == 0)
		{
			val fab = this.activity.findViewById(R.id.fab_add) as AddPauseFloatingActionButton?
			fab?.show()
		}
	}

	override fun onEventMainThread(event: SoundMovedEvent) {}

	override fun onEventMainThread(event: SoundAddedEvent) {}

	override fun onEventMainThread(event: SoundChangedEvent) {}
}
