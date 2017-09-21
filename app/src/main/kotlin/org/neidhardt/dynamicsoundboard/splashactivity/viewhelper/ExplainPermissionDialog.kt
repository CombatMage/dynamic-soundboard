package org.neidhardt.dynamicsoundboard.splashactivity.viewhelper

import android.Manifest
import android.content.Context
import android.support.v4.app.FragmentManager
import org.neidhardt.dynamicsoundboard.R
import org.neihdardt.viewpagerdialog.ViewPagerDialog
import java.util.*

/**
 * Created by eric.neidhardt@gmail.com on 12.09.2017.
 */
class ExplainPermissionDialog : ViewPagerDialog() {

	companion object {
		private val TAG = ViewPagerDialog::class.java.name

		fun show(fragmentManager: FragmentManager, missingPermissions: Array<String>, context: Context) {
			val dialog = ExplainPermissionDialog()

			dialog.setTitle(context.getString(R.string.dialogexplainpermissions_title))
			dialog.setMessage(context.getString(R.string.dialogexplainpermissions_message))
			dialog.setViewData(missingPermissions, context)

			dialog.show(fragmentManager, TAG)
		}
	}

	fun setViewData(missingPermissions: Array<String>, context: Context) {
		val messagesToDisplay = ArrayList<String>()

		if (missingPermissions.contains(Manifest.permission.WRITE_EXTERNAL_STORAGE) ||
				missingPermissions.contains(Manifest.permission.READ_EXTERNAL_STORAGE))
		{
			messagesToDisplay.add(context.getString(R.string.dialogexplainpermissions_explainstorage))
		}

		if (missingPermissions.contains(Manifest.permission.READ_PHONE_STATE)) {
			messagesToDisplay.add(context.getString(R.string.dialogexplainpermissions_explainreadphonestate))
		}

		super.setViewData(messagesToDisplay.toTypedArray())
	}

	override fun getStringButtonOk(): String? = this.context?.getString(R.string.dialog_ok)
}