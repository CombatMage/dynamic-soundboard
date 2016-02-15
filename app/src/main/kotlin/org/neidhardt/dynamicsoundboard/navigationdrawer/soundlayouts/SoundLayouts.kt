package org.neidhardt.dynamicsoundboard.navigationdrawer.soundlayouts

import android.content.Context
import android.support.v7.widget.DefaultItemAnimator
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import org.greenrobot.eventbus.EventBus
import org.neidhardt.dynamicsoundboard.R
import org.neidhardt.dynamicsoundboard.SoundboardApplication
import org.neidhardt.dynamicsoundboard.navigationdrawer.views.NavigationDrawerList
import org.neidhardt.dynamicsoundboard.soundlayoutmanagement.model.SoundLayoutsAccess
import org.neidhardt.dynamicsoundboard.soundlayoutmanagement.model.SoundLayoutsStorage
import org.neidhardt.dynamicsoundboard.views.recyclerviewhelpers.DividerItemDecoration

/**
 * File created by eric.neidhardt on 08.03.2015.
 */
class SoundLayouts : NavigationDrawerList
{
	private val eventBus = EventBus.getDefault()

	private val soundLayoutsAccess: SoundLayoutsAccess = SoundboardApplication.getSoundLayoutsAccess()
	private val soundLayoutsStorage: SoundLayoutsStorage = SoundboardApplication.getSoundLayoutsStorage()

	var presenter: SoundLayoutsPresenter = SoundLayoutsPresenter(this.eventBus, this.soundLayoutsAccess, this.soundLayoutsStorage)
	var adapter: SoundLayoutsAdapter = SoundLayoutsAdapter(this.presenter, this.eventBus)

	@SuppressWarnings("unused") constructor(context: Context) : super(context)
	{
		this.init(context)
	}

	@SuppressWarnings("unused") constructor(context: Context, attrs: AttributeSet) : super(context, attrs)
	{
		this.init(context)
	}

	@SuppressWarnings("unused") constructor(context: Context, attrs: AttributeSet, defStyle: Int) : super(context, attrs, defStyle)
	{
		this.init(context)
	}

	private fun init(context: Context)
	{
		this.presenter.adapter = this.adapter

		LayoutInflater.from(context).inflate(R.layout.view_sound_layout_list, this, true)

		val soundLayouts = this.findViewById(R.id.rv_sound_layouts_list) as RecyclerView
		if (!this.isInEditMode)
		{
			soundLayouts.addItemDecoration(DividerItemDecoration(this.context))
			soundLayouts.layoutManager = LinearLayoutManager(this.context)
			soundLayouts.itemAnimator = DefaultItemAnimator()
		}
		soundLayouts.adapter = this.adapter
	}

	override fun onAttachedToWindow()
	{
		super.onAttachedToWindow()
		this.presenter.onAttachedToWindow()
	}

	override fun onDetachedFromWindow()
	{
		this.presenter.onDetachedFromWindow()
		super.onDetachedFromWindow()
	}

	override fun onFinishInflate() {
		super.onFinishInflate()
		this.presenter.view = this
	}

	override val itemCount: Int
		get() = presenter.values.size

	override val actionModeTitle: Int
		get() = R.string.cab_title_delete_sound_layouts

	fun isActive(): Boolean
	{
		return this.visibility == View.VISIBLE
	}

	fun toggleVisibility()
	{
		if (this.visibility == View.VISIBLE)
			this.hideSelectSoundLayout()
		else
			this.showSelectSoundLayoutOverlay()
	}

	private fun showSelectSoundLayoutOverlay()
	{
		this.visibility = View.VISIBLE
	}

	private fun hideSelectSoundLayout()
	{
		this.visibility = View.INVISIBLE
	}

}
