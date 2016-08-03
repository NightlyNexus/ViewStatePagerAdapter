/*
 * Copyright (C) 2016 Eric Cochran
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.nightlynexus.viewstatepageradapter;

import android.os.Parcel;
import android.os.Parcelable;
import android.support.v4.view.PagerAdapter;
import android.util.SparseArray;
import android.view.View;
import android.view.ViewGroup;

public abstract class ViewStatePagerAdapter extends PagerAdapter {
  private final SparseArray<View> attached;
  private SparseArray<SparseArray<Parcelable>> detached;

  public ViewStatePagerAdapter() {
    this(3);
  }

  /**
   * @param capacity the initial capacity of the collection of attached Views.
   */
  public ViewStatePagerAdapter(int capacity) {
    attached = new SparseArray<>(capacity);
  }

  protected abstract View createView(ViewGroup container, int position);

  protected void destroyView(ViewGroup container, int position, View view) {
  }

  @Override public final SavedState saveState() {
    for (int i = 0; i < attached.size(); i++) {
      int position = attached.keyAt(i);
      View view = attached.valueAt(i);
      putInDetached(position, view);
    }
    return new SavedState(detached);
  }

  @Override public final void restoreState(Parcelable state, ClassLoader loader) {
    SavedState savedState = (SavedState) state;
    detached = savedState.detached;
  }

  @Override public final Object instantiateItem(ViewGroup container, int position) {
    if (detached == null) {
      detached = new SparseArray<>();
    }
    View view = createView(container, position);
    if (view == null) {
      throw new NullPointerException(
          "createView must not return null. (position: " + position + ")");
    }
    SparseArray<Parcelable> viewState = detached.get(position);
    if (viewState != null) {
      view.restoreHierarchyState(viewState);
    }
    container.addView(view);
    attached.put(position, view);
    return view;
  }

  @Override public final void destroyItem(ViewGroup container, int position, Object object) {
    View view = (View) object;
    destroyView(container, position, view);
    putInDetached(position, view);
    container.removeView(view);
    attached.remove(position);
  }

  private void putInDetached(int position, View view) {
    SparseArray<Parcelable> viewState = new SparseArray<>();
    view.saveHierarchyState(viewState);
    detached.put(position, viewState);
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

  public static final class SavedState implements Parcelable {
    final SparseArray<SparseArray<Parcelable>> detached;

    SavedState(SparseArray<SparseArray<Parcelable>> detached) {
      this.detached = detached;
    }

    @Override public void writeToParcel(Parcel dest, int flags) {
      writeNestedSparseArray(dest, detached, flags);
    }

    public static final Creator<SavedState> CREATOR = new Creator<SavedState>() {
      @Override public SavedState createFromParcel(Parcel in) {
        SparseArray<SparseArray<Parcelable>> detached =
            readNestedSparseArray(in, SavedState.class.getClassLoader());
        return new SavedState(detached);
      }

      @Override public SavedState[] newArray(int size) {
        return new SavedState[size];
      }
    };

    @Override public int describeContents() {
      return 0;
    }

    static SparseArray<SparseArray<Parcelable>> readNestedSparseArray(Parcel in,
        ClassLoader loader) {
      int size = in.readInt();
      if (size == -1) {
        return null;
      }
      SparseArray<SparseArray<Parcelable>> map = new SparseArray<>(size);
      while (size != 0) {
        int key = in.readInt();
        SparseArray<Parcelable> value = readSparseArray(in, loader);
        map.append(key, value);
        size--;
      }
      return map;
    }

    static SparseArray<Parcelable> readSparseArray(Parcel in, ClassLoader loader) {
      int size = in.readInt();
      if (size == -1) {
        return null;
      }
      SparseArray<Parcelable> map = new SparseArray<>(size);
      while (size != 0) {
        int key = in.readInt();
        Parcelable value = in.readParcelable(loader);
        map.append(key, value);
        size--;
      }
      return map;
    }

    static void writeNestedSparseArray(Parcel dest, SparseArray<SparseArray<Parcelable>> map,
        int flags) {
      if (map == null) {
        dest.writeInt(-1);
        return;
      }
      int size = map.size();
      dest.writeInt(size);
      int i = 0;
      while (i != size) {
        dest.writeInt(map.keyAt(i));
        writeSparseArray(dest, map.valueAt(i), flags);
        i++;
      }
    }

    static void writeSparseArray(Parcel dest, SparseArray<Parcelable> map, int flags) {
      if (map == null) {
        dest.writeInt(-1);
        return;
      }
      int size = map.size();
      dest.writeInt(size);
      int i = 0;
      while (i != size) {
        dest.writeInt(map.keyAt(i));
        dest.writeParcelable(map.valueAt(i), flags);
        i++;
      }
    }
  }
}
