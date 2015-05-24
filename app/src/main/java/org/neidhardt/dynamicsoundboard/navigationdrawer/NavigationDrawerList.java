package org.neidhardt.dynamicsoundboard.navigationdrawer;

import android.content.Context;
import android.util.AttributeSet;
import android.util.SparseArray;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;
import de.greenrobot.event.EventBus;
import org.neidhardt.dynamicsoundboard.navigationdrawer.events.ActionModeEvent;


public abstract class NavigationDrawerList
		extends
			FrameLayout
		implements
			android.support.v7.view.ActionMode.Callback
{
	protected NavigationDrawerFragment parent;
	protected boolean isInSelectionMode;

	private SparseArray<View> selectedItems;

	public NavigationDrawerList(Context context) {
		super(context);
	}

	public NavigationDrawerList(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public NavigationDrawerList(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
	}

	protected void onItemSelected(View view, int indexOfSelectedItem)
	{
		if (view.isSelected())
			this.selectedItems.remove(indexOfSelectedItem);
		else
			this.selectedItems.put(indexOfSelectedItem, view);

		EventBus.getDefault().post(new ActionModeEvent(this, ActionModeEvent.REQUEST.INVALIDATE));
		view.setSelected(!view.isSelected());
	}

	public void prepareItemDeletion()
	{
		EventBus.getDefault().post(new ActionModeEvent(this, ActionModeEvent.REQUEST.START));
	}

	public void deleteSelected()
	{
		EventBus.getDefault().post(new ActionModeEvent(this, ActionModeEvent.REQUEST.STOP));
		onDeleteSelected(selectedItems);
	}

	protected abstract void onDeleteSelected(SparseArray<View> selectedItems);

	protected abstract int getItemCount();

	protected abstract int getActionModeTitle();

	@Override
	public boolean onCreateActionMode(android.support.v7.view.ActionMode actionMode, Menu menu)
	{
		this.isInSelectionMode = true;
		this.selectedItems = new SparseArray<>(this.getItemCount());

		return true;
	}

	@Override
	public boolean onPrepareActionMode(android.support.v7.view.ActionMode actionMode, Menu menu)
	{
		actionMode.setTitle(this.getActionModeTitle());

		int count = this.selectedItems.size();
		String countString = Integer.toString(count);
		if (countString.length() == 1)
			countString = " " + countString;
		countString = countString + "/" + this.getItemCount();

		actionMode.setSubtitle(countString);
		return true;
	}

	@Override
	public boolean onActionItemClicked(android.support.v7.view.ActionMode actionMode, MenuItem menuItem)
	{
		return false;
	}

	@Override
	public void onDestroyActionMode(android.support.v7.view.ActionMode actionMode)
	{
		this.isInSelectionMode = false;
		for(int i = 0; i < this.selectedItems.size(); i++)
			this.selectedItems.valueAt(i).setSelected(false);

		EventBus.getDefault().post(new ActionModeEvent(this, ActionModeEvent.REQUEST.STOPPED));
	}

	public void setParentFragment(NavigationDrawerFragment parent)
	{
		this.parent = parent;
	}
}