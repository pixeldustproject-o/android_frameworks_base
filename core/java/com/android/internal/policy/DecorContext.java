/*
 * Copyright (C) 2015 The Android Open Source Project
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

package com.android.internal.policy;

import android.content.Context;
import android.content.res.AssetManager;
import android.content.res.Resources;
import android.view.ContextThemeWrapper;
import android.view.WindowManager;
import android.view.WindowManagerImpl;

/**
 * Context for decor views which can be seeded with pure application context and not depend on the
 * activity, but still provide some of the facilities that Activity has,
 * e.g. themes, activity-based resources, etc.
 *
 * @hide
 */
class DecorContext extends ContextThemeWrapper {
    private PhoneWindow mPhoneWindow;
    private WindowManager mWindowManager;
    private Resources mActivityResources;

    public DecorContext(Context context, Resources activityResources) {
        super(context, null);
        mActivityResources = activityResources;
    }

    void setPhoneWindow(PhoneWindow phoneWindow) {
        mPhoneWindow = phoneWindow;
        mWindowManager = null;
    }

    @Override
    public Object getSystemService(String name) {
        if (Context.WINDOW_SERVICE.equals(name)) {
            if (mWindowManager == null) {
                WindowManagerImpl wm = (mPhoneWindow != null)
                    ? (WindowManagerImpl) mPhoneWindow.getWindowManager()
                    : null;
                if (wm == null) {
                    wm = (WindowManagerImpl) super.getSystemService(Context.WINDOW_SERVICE);
                }
                mWindowManager = wm.createLocalWindowManager(mPhoneWindow);
            }
            return mWindowManager;
        }
        return super.getSystemService(name);
    }

    @Override
    public Resources getResources() {
        return mActivityResources;
    }

    @Override
    public AssetManager getAssets() {
        return mActivityResources.getAssets();
    }
}
