package pjwstk.edu.pl.resistor_calculator

import android.graphics.Color
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(manifest = Config.NONE)
class GetStripesTest {

    private lateinit var chooseStripesActivity: ChooseStripesActivity

    @Before
    fun setUp() {
        chooseStripesActivity = ChooseStripesActivity()
    }

    @Test
    fun getColorForStripe_None_ReturnsTransparent() {
        val color = chooseStripesActivity.getColorForStripe("None")
        assertEquals(Color.TRANSPARENT, color)
    }

    @Test
    fun getStripeColorOptions_ThreeStripes_ReturnsCorrectOptions() {
        val expectedOptions = chooseStripesActivity.normalColorOptions
        val actualOptions = chooseStripesActivity.getStripeColorOptions(1, 3)
        assertArrayEquals(expectedOptions, actualOptions)
    }

    @Test
    fun getStripeColorOptions_FourStripes_ReturnsCorrectOptions() {
        val expectedOptions = chooseStripesActivity.multiplierColorOptions
        val actualOptions = chooseStripesActivity.getStripeColorOptions(3, 4)
        assertArrayEquals(expectedOptions, actualOptions)
    }

    @Test
    fun getStripeColorOptions_FiveStripes_ReturnsCorrectOptions() {
        val expectedOptions = chooseStripesActivity.toleranceColorOptions
        val actualOptions = chooseStripesActivity.getStripeColorOptions(5, 5)
        assertArrayEquals(expectedOptions, actualOptions)
    }

    @Test
    fun getStripeColorOptions_SixStripes_ReturnsCorrectOptions() {
        val expectedOptions = chooseStripesActivity.temperatureColorOptions
        val actualOptions = chooseStripesActivity.getStripeColorOptions(6, 6)
        assertArrayEquals(expectedOptions, actualOptions)
    }



}

