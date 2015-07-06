package org.neidhardt.dynamicsoundboard.views.recyclerviewhelpers

import android.support.v7.widget.RecyclerView

/**
 * File created by eric.neidhardt on 17.06.2015.
 */
public abstract class BaseAdapter<Type, ViewHolder : RecyclerView.ViewHolder> : RecyclerView.Adapter<ViewHolder>(), ListAdapter<Type>
{
	public abstract fun getValues(): List<Type>

	override fun notifyItemChanged(data: Type)
	{
		val index = this.getValues().indexOf(data)
		if (index == -1)
			this.notifyDataSetChanged()
		else
			this.notifyItemChanged(index)
	}
}