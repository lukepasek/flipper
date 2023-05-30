/*
 * Copyright (c) Meta Platforms, Inc. and affiliates.
 *
 * This source code is licensed under the MIT license found in the
 * LICENSE file in the root directory of this source tree.
 */

package com.facebook.flipper.android;

import com.facebook.flipper.core.FlipperObject;
import com.facebook.flipper.core.FlipperSocketEventHandler;
import com.facebook.jni.HybridData;
import com.facebook.proguard.annotations.DoNotStrip;

import android.util.Log;

@DoNotStrip
class FlipperSocketEventHandlerImpl implements FlipperSocketEventHandler {

  private final HybridData mHybridData;

  private FlipperSocketEventHandlerImpl(HybridData hd) {
    mHybridData = hd;
  }

  @Override
  public void onConnectionEvent(SocketEvent event) {
    Log.d("FlipperSocketEventHandlerImpl", "connection event: " + event);

    reportConnectionEvent(event.ordinal());
  }

  @Override
  public void onMessageReceived(String message) {
    Log.d("FlipperSocketEventHandlerImpl", "message recv: " + message);

    reportMessageReceived(message);
  }

  @Override
  public FlipperObject onAuthenticationChallengeReceived() {
    return reportAuthenticationChallengeReceived();
  }

  private native void reportConnectionEvent(int code);

  private native void reportMessageReceived(String message);

  private native FlipperObject reportAuthenticationChallengeReceived();
}
