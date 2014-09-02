package com.ericneidhardt.dynamicsoundboard.soundsheet;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.ericneidhardt.dynamicsoundboard.R;
import com.ericneidhardt.dynamicsoundboard.dao.SoundSheet;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by eric.neidhardt on 29.08.2014.
 */
public class SoundSheetAdapter extends RecyclerView.Adapter<SoundSheetAdapter.ViewHolder>
{
	private List<SoundSheet> soundSheets;
	private OnItemClickedListener onItemClickListener;

	public SoundSheetAdapter()
	{
		this.soundSheets = new ArrayList<SoundSheet>();
	}

	public void setOnItemClickedListener(OnItemClickedListener onItemClickListener)
	{
		this.onItemClickListener = onItemClickListener;
	}

	public void add(SoundSheet soundSheet)
	{
		this.soundSheets.add(soundSheet);
		this.notifyItemInserted(this.soundSheets.size());
	}

	public void addAll(List<SoundSheet> soundSheets)
	{
		this.soundSheets.addAll(soundSheets);
		this.notifyDataSetChanged();
	}

	public void remove(SoundSheet soundSheet)
	{
		int position = this.soundSheets.indexOf(soundSheet);
		this.soundSheets.remove(position);
		this.notifyItemRemoved(position);
	}

	public void clear()
	{
		this.soundSheets.clear();
		this.notifyDataSetChanged();
	}

	public void setSelectedItem(int position)
	{
		for (int i = 0; i < this.soundSheets.size(); i++)
		{
			boolean isSelected = i == position ? true : false;
			this.soundSheets.get(i).setIsSelected(isSelected);
		}
		this.notifyDataSetChanged();
	}

	public List<SoundSheet> getValues()
	{
		return this.soundSheets;
	}

	@Override
	public int getItemCount()
	{
		return this.soundSheets.size();
	}

	@Override
	public ViewHolder onCreateViewHolder(ViewGroup parent, int position)
	{
		View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.view_sound_sheet_item, parent, false);
		return new ViewHolder(view);
	}

	@Override
	public void onBindViewHolder(ViewHolder holder, int position)
	{
		SoundSheet data = this.soundSheets.get(position);
		holder.textView.setText(data.getLabel());
		holder.selectionIndicator.setVisibility(data.getIsSelected() ? View.VISIBLE : View.INVISIBLE);
	}

	public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener
	{
		private TextView textView;
		private ImageView selectionIndicator;

		public ViewHolder(View itemView) {
			super(itemView);

			this.textView = (TextView)itemView.findViewById(R.id.tv_label);
			this.selectionIndicator = (ImageView)itemView.findViewById(R.id.iv_selected);

			itemView.setOnClickListener(this);
		}

		@Override
		public void onClick(View view)
		{
			int position = this.getPosition();
			if (onItemClickListener != null)
				onItemClickListener.onItemClicked(view, soundSheets.get(position), position);
		}
	}

	public static class DividerItemDecoration extends RecyclerView.ItemDecoration {

		private static final int[] ATTRS = new int[]{
				android.R.attr.listDivider
		};

		public static final int HORIZONTAL_LIST = LinearLayoutManager.HORIZONTAL;

		public static final int VERTICAL_LIST = LinearLayoutManager.VERTICAL;

		private Drawable mDivider;

		private int mOrientation;

		public DividerItemDecoration(Context context, int orientation) {
			final TypedArray a = context.obtainStyledAttributes(ATTRS);
			mDivider = a.getDrawable(0);
			a.recycle();
			setOrientation(orientation);
		}

		public void setOrientation(int orientation) {
			if (orientation != HORIZONTAL_LIST && orientation != VERTICAL_LIST) {
				throw new IllegalArgumentException("invalid orientation");
			}
			mOrientation = orientation;
		}

		@Override
		public void onDraw(Canvas c, RecyclerView parent) {
			if (mOrientation == VERTICAL_LIST) {
				drawVertical(c, parent);
			} else {
				drawHorizontal(c, parent);
			}
		}

		public void drawVertical(Canvas c, RecyclerView parent) {
			final int left = parent.getPaddingLeft();
			final int right = parent.getWidth() - parent.getPaddingRight();

			final int childCount = parent.getChildCount();
			for (int i = 0; i < childCount; i++) {
				final View child = parent.getChildAt(i);
				final RecyclerView.LayoutParams params = (RecyclerView.LayoutParams) child
						.getLayoutParams();
				final int top = child.getBottom() + params.bottomMargin;
				final int bottom = top + mDivider.getIntrinsicHeight();
				mDivider.setBounds(left, top, right, bottom);
				mDivider.draw(c);
			}
		}

		public void drawHorizontal(Canvas c, RecyclerView parent) {
			final int top = parent.getPaddingTop();
			final int bottom = parent.getHeight() - parent.getPaddingBottom();

			final int childCount = parent.getChildCount();
			for (int i = 0; i < childCount; i++) {
				final View child = parent.getChildAt(i);
				final RecyclerView.LayoutParams params = (RecyclerView.LayoutParams) child
						.getLayoutParams();
				final int left = child.getRight() + params.rightMargin;
				final int right = left + mDivider.getIntrinsicHeight();
				mDivider.setBounds(left, top, right, bottom);
				mDivider.draw(c);
			}
		}

		@Override
		public void getItemOffsets(Rect outRect, int itemPosition, RecyclerView parent) {
			if (mOrientation == VERTICAL_LIST) {
				outRect.set(0, 0, 0, mDivider.getIntrinsicHeight());
			} else {
				outRect.set(0, 0, mDivider.getIntrinsicWidth(), 0);
			}
		}
	}

	public static interface OnItemClickedListener
	{
		public void onItemClicked(View view, SoundSheet data, int position);
	}

}
