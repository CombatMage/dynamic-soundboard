package org.neidhardt.dynamicsoundboard.views.edittext

import android.content.Context
import android.util.AttributeSet
import android.view.KeyEvent
import com.devspark.robototextview.widget.RobotoEditText

/**
 * Project created by eric.neidhardt on 08.09.2014.
 */
class EditTextBackEvent : RobotoEditText
{
	var onImeBackListener: EditTextImeBackListener? = null

	@SuppressWarnings("unused")
	constructor(context: Context) : super(context)

	@SuppressWarnings("unused")
	constructor(context: Context, attrs: AttributeSet) : super(context, attrs)

	@SuppressWarnings("unused")
	constructor(context: Context, attrs: AttributeSet, defStyle: Int) : super(context, attrs, defStyle)

	override fun onKeyPreIme(keyCode: Int, event: KeyEvent): Boolean
	{
		if (event.keyCode == KeyEvent.KEYCODE_BACK && event.action == KeyEvent.ACTION_UP)
			this.onImeBackListener?.onImeBack(this, this.text.toString())

		return super.onKeyPreIme(keyCode, event)
	}

	interface EditTextImeBackListener
	{
		fun onImeBack(ctrl: EditTextBackEvent, text: String)
	}
}


