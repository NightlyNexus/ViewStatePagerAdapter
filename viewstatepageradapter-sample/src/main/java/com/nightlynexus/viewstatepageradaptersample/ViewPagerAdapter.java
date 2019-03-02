package com.nightlynexus.viewstatepageradaptersample;

import android.view.View;
import android.view.ViewGroup;
import androidx.viewpager.widget.PagerAdapter;

abstract class ViewPagerAdapter extends PagerAdapter {
  protected abstract View createView(ViewGroup container, int position);

  protected void destroyView(ViewGroup container, int position, View view) {
  }

  @Override public final Object instantiateItem(ViewGroup container, int position) {
    View view = createView(container, position);
    if (view == null) {
      throw new NullPointerException(
          "createView must not return null. (position: " + position + ")");
    }
    container.addView(view);
    return view;
  }

  @Override public final void destroyItem(ViewGroup container, int position, Object object) {
    View view = (View) object;
    destroyView(container, position, view);
    container.removeView(view);
  }

  @Override public final Object instantiateItem(View container, int position) {
    throw new UnsupportedOperationException();
  }

  @Override public final void destroyItem(View container, int position, Object object) {
    throw new UnsupportedOperationException();
  }

  @Override public final boolean isViewFromObject(View view, Object object) {
    return view == object;
  }
}
