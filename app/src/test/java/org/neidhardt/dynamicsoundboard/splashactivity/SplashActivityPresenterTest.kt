package org.neidhardt.dynamicsoundboard.splashactivity

import android.Manifest
import org.junit.Before
import org.junit.Test
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.Mockito.verify
import org.mockito.MockitoAnnotations
import org.neidhardt.dynamicsoundboard.soundactivity.SoundActivity
import java.util.*

/**
 * Created by eric.neidhardt@gmail.com on 29.06.2017.
 */
class SplashActivityPresenterTest {

	@Mock private lateinit var view: SplashActivityContract.View
	private lateinit var unit: SplashActivityPresenter

	@Before
	fun setUp() {
		MockitoAnnotations.initMocks(this)
		this.unit = SplashActivityPresenter(this.view)
	}

	@Test
	fun openSoundActivityOnNormalStart() {
		// arrange
		Mockito.`when`(this.view.getMissingPermissions()).thenReturn(emptyArray())

		// action
		this.unit.onCreated()

		// verify
		verify(this.view).openActivity(SoundActivity::class.java)
	}

	@Test
	fun explainPermissionsIfMissingOnStart() {
		// arrange
		val missingPermissions = Arrays.asList(Manifest.permission.READ_EXTERNAL_STORAGE).toTypedArray()
		Mockito.`when`(this.view.getMissingPermissions()).thenReturn(missingPermissions)

		// action
		this.unit.onCreated()

		// verify
		verify(this.view).explainPermissions(missingPermissions)
	}

	@Test
	fun closeAppIfMandatoryPermissionsWhereRefused() {
		// arrange
		Mockito.`when`(this.view.getMissingPermissions()).thenReturn(
				Arrays.asList(Manifest.permission.READ_EXTERNAL_STORAGE).toTypedArray())

		// action
		this.unit.onUserHasChangedPermissions()

		// verify
		verify(this.view).closeApplication(true)
	}

	@Test
	fun openSoundActivityIfMandatoryPermissionsWhereGranted() {
		// arrange
		Mockito.`when`(this.view.getMissingPermissions()).thenReturn(
				Arrays.asList(Manifest.permission.READ_PHONE_STATE).toTypedArray())

		// action
		this.unit.onUserHasChangedPermissions()

		// verify
		verify(this.view).openActivity(SoundActivity::class.java)
	}

}