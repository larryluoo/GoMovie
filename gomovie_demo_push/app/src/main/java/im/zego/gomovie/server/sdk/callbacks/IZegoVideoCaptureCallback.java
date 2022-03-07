package im.zego.gomovie.server.sdk.callbacks;

import im.zego.zegoexpress.constants.ZegoPublishChannel;

public interface IZegoVideoCaptureCallback {

   void onStart(ZegoPublishChannel channel);

   void onStop(ZegoPublishChannel channel);
}
