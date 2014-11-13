package com.ericneidhardt.dynamicsoundboard.dialog;

import android.app.DialogFragment;
import android.os.Environment;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import com.ericneidhardt.dynamicsoundboard.R;
import com.ericneidhardt.dynamicsoundboard.misc.Util;

import java.io.File;
import java.util.List;

/**
 * File created by eric.neidhardt on 12.11.2014.
 */
public abstract class FileExplorerDialog extends DialogFragment
{
	protected DirectoryAdapter adapter;

	protected abstract boolean canSelectDirectory();

	protected abstract boolean canSelectFile();

	protected class DirectoryAdapter extends RecyclerView.Adapter<DirectoryEntry>
	{
		private File parent;
		protected File selectedFile;
		private List<File> fileList;

		public DirectoryAdapter()
		{
			this.setParent(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_MUSIC));
			this.notifyDataSetChanged();
		}

		public void setParent(File parent)
		{
			this.parent = parent;
			this.fileList = Util.getFilesInDirectory(this.parent);
			if (this.parent.getParentFile() != null)
				this.fileList.add(0, this.parent.getParentFile());
		}

		@Override
		public DirectoryEntry onCreateViewHolder(ViewGroup parent, int i)
		{
			View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.view_directory_item, parent, false);
			return new DirectoryEntry(view);
		}

		@Override
		public void onBindViewHolder(DirectoryEntry directoryEntry, int position)
		{
			File file = this.fileList.get(position);
			directoryEntry.bindData(file);
		}

		@Override
		public int getItemCount()
		{
			return this.fileList.size();
		}
	}

	protected class DirectoryEntry
			extends
				RecyclerView.ViewHolder
			implements
				View.OnClickListener,
				View.OnLongClickListener
	{
		private ImageView fileType;
		private ImageView selectionIndicator;
		private TextView fileName;


		public DirectoryEntry(View itemView)
		{
			super(itemView);
			this.fileName = (TextView) itemView.findViewById(R.id.tv_label);
			this.fileType = (ImageView) itemView.findViewById(R.id.iv_file_type);
			this.selectionIndicator = (ImageView) itemView.findViewById(R.id.iv_selected);

			itemView.setOnClickListener(this);
			itemView.setOnLongClickListener(this);
		}

		public void bindData(File file)
		{
			if (file.equals(adapter.parent.getParentFile()))
				this.bindParentDirectory();
			else
			{
				this.fileName.setText(file.getName());
				if (file.isDirectory())
					this.bindDirectory(file);
				else
					this.bindFile(file);

				if (file.equals(adapter.selectedFile))
				{
					this.selectionIndicator.setVisibility(View.VISIBLE);
					this.fileType.setSelected(true);
				} else
				{
					this.selectionIndicator.setVisibility(View.INVISIBLE);
					this.fileType.setSelected(false);
				}
			}
		}

		private void bindFile(File file)
		{
			if (Util.isAudioFile(file))
				this.fileType.setImageResource(R.drawable.selector_ic_file_sound);
			else
				this.fileType.setImageResource(R.drawable.selector_ic_file);
		}

		private void bindDirectory(File file)
		{
			if (Util.containsAudioFiles(file))
				this.fileType.setImageResource(R.drawable.selector_ic_folder_sound);
			else
				this.fileType.setImageResource(R.drawable.selector_ic_folder);
		}

		private void bindParentDirectory()
		{
			this.fileName.setText("..");
			this.fileType.setImageResource(R.drawable.selector_ic_parent_directory);
			this.selectionIndicator.setVisibility(View.GONE);
		}

		@Override
		public void onClick(View v)
		{
			File file = adapter.fileList.get(this.getPosition());
			if (!file.isDirectory())
				return;
			adapter.setParent(file);
			adapter.notifyDataSetChanged();
		}

		@Override
		public boolean onLongClick(View v)
		{
			File file = adapter.fileList.get(this.getPosition());
			if (file.equals(adapter.parent.getParentFile()))
				return false;

			if (file.isDirectory() && !canSelectDirectory())
				return false;

			if (!file.isDirectory() && !canSelectFile())
				return false;

			adapter.selectedFile = file;
			adapter.notifyDataSetChanged();
			return false;
		}
	}
}