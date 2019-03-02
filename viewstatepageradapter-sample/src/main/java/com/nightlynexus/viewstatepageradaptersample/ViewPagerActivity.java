package com.nightlynexus.viewstatepageradaptersample;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.viewpager.widget.ViewPager;
import com.nightlynexus.viewstatepageradapter.ViewStatePagerAdapter;

public final class ViewPagerActivity extends Activity {
  @Override protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.pagers);
    ViewPager withState = findViewById(R.id.with_state);
    ViewPager withoutState = findViewById(R.id.without_state);
    withState.setAdapter(new SampleEditTextViewStatePagerAdapter(withState.getContext()));
    withoutState.setAdapter(new SampleEditTextViewPagerAdapter(withoutState.getContext()));
  }

  private static final class SampleEditTextViewStatePagerAdapter extends ViewStatePagerAdapter {
    private final Context context;

    SampleEditTextViewStatePagerAdapter(Context context) {
      this.context = context;
    }

    @SuppressLint("SetTextI18n") @Override
    protected View createView(ViewGroup container, int position) {
      View view = LayoutInflater.from(context).inflate(R.layout.page_top, container, false);
      TextView pagerNumber = view.findViewById(R.id.page_number);
      pagerNumber.setText(Integer.toString(position));
      return view;
    }

    @Override public int getCount() {
      return 10;
    }
  }

  private static final class SampleEditTextViewPagerAdapter extends ViewPagerAdapter {
    private final Context context;

    SampleEditTextViewPagerAdapter(Context context) {
      this.context = context;
    }

    @SuppressLint("SetTextI18n") @Override
    protected View createView(ViewGroup container, int position) {
      View view = LayoutInflater.from(context).inflate(R.layout.page_bottom, container, false);
      TextView pagerNumber = view.findViewById(R.id.page_number);
      pagerNumber.setText(Integer.toString(position));
      return view;
    }

    @Override public int getCount() {
      return 10;
    }
  }
}
