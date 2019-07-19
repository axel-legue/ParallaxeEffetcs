package hmi.swl.renault.parallaxeffect

import android.util.SparseArray
import android.view.View
import androidx.viewpager2.widget.ViewPager2
import java.util.*

class ParallaxePageTransformer : ViewPager2.PageTransformer {

    companion object {
        private const val FOREGROUND_FACTOR = 0.5f
    }

    private val cache = WeakHashMap<View, ViewMappings>()


    /**
     * Arguments :
     *  1- View that that scrolling applies
     *  2- Scrolling position for the View
     *
     * During a Scroll, this will be called twice for each positional update,
     * one for each of the Views representing the two visible pages in the ViewPager2.
     * This Views will actually be the layouts for the individual Fragments representing
     * the pages in the ViewPager2.
     * The position will be a "float" value between -1 and 1, with:
     *      -1 representing the View being completely off the left hand side of the screen,
     *      0 representing the View being centred and fully visible on the screen
     *      1 rerpesenting the View being completely off the right hand side of the screen.
     *
     * We first calculate an "offset" value in pexels which we obtain from the width of the View
     * multiplied by the position value( this implementation assumes horizontal scrolling)
     *
     * Next we look up a set of View mapping. We use a "cache" here to avoid having to make multiple
     * "findViewById()" calls within what is effectively an animation as this could cause jank if we do
     * not render the frame within 16ms.
     * We use a "WeakHashMap" to store these mappings which use a key of the View representing the page being updated
     *
     * A "WeakHashmap" is usefull here because it will only hold a weak reference to the key, and therefore that they
     * can be garbage collected. If it's GC'd then the value for that key / value pair in the WeakHashMap will be removed.
     * Therefore this protects us from leaking the Fragment layouts by holding string references to them.
     *
     * The value of each entry in "WeakHashMap" is a "SparseArray" which is a mapping between
     *
     * the resources IDs and the individual Views from those IDs within the layout.
     * Essentially this caches the "ImageViews" representing the background, middleground,foreground images
     * meaning that we don't have to do a "findViewById()" for each one in every frame of the animation -
     * instead we do a much cheaper "WeakhashMap", than "SparseArray" lookup.
     *
     *
     * Once we have these mappings, we can apply a translationX to each to tweak it’s position.
     * For the background :
     *      image, we apply the negated offset value which we calculated earlier.
     *      This will essentially lock the background in to position, and prevent it from moving during the scroll.
     * For the middleground :
     *      We are not actually applying the translationX to this component, so this will move at the same speed
     *      as the ViewPager2 scrolling – i.e. it will track the user’s finger / or the fling.
     * For the foreground :
     *      we apply the offset mutliplied by a scaling factor. I played with various values here,
     *      but rather liked the effect with a scale factor of 0.5 so that’s what we’ve gone with.
     *      That causes the foreground elements to move out of the frame faster than the swipe of fling.
     */
    override fun transformPage(page: View, position: Float) {
        val offset = page.width * position
        page.getMappings().also { mappings ->
            mappings[R.id.image_background]?.translationX = -offset
            mappings[R.id.image_foreground]?.translationX = offset * FOREGROUND_FACTOR

        }
    }


    private fun View.getMappings(): ViewMappings =
        cache[this] ?: ViewMappings().also { mappings ->
            mappings.put(R.id.image_background, findViewById(R.id.image_background))
            mappings.put(R.id.image_middleground, findViewById(R.id.image_middleground))
            mappings.put(R.id.image_foreground, findViewById(R.id.image_foreground))
            cache[this] = mappings
        }

    private class ViewMappings : SparseArray<View>()

}