

package com.woting.common.widgetui.wheelview;

/**
 * Wheel changed listener interface.
 * <p>
 * The currentItemChanged() method is called whenever current wheel positions is
 * changed:
 * <li>New Wheel position is set
 * <li>Wheel view is scrolled
 */
public interface OnWheelChangedListener {

	void onChanged(WheelView wheel, int oldValue, int newValue);
	void onChangeds(CityWheelView wheel, int oldValue, int newValue);
}
