/*
 * 本项目大量借鉴学习了开源投屏软件：Scrcpy，在此对该项目表示感谢
 */
package com.shiyunjin.easycontrolnext.server.helper;

import android.graphics.Rect;
import android.hardware.display.VirtualDisplay;
import android.media.MediaCodec;
import android.media.MediaCodecInfo;
import android.media.MediaFormat;
import android.os.Build;
import android.os.IBinder;
import android.system.ErrnoException;
import android.view.Surface;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.nio.ByteBuffer;

import com.shiyunjin.easycontrolnext.server.Server;
import com.shiyunjin.easycontrolnext.server.entity.Device;
import com.shiyunjin.easycontrolnext.server.entity.Options;
import com.shiyunjin.easycontrolnext.server.wrappers.DisplayManager;
import com.shiyunjin.easycontrolnext.server.wrappers.SurfaceControl;

public final class VideoEncode {
  private static MediaCodec encedec;
  private static MediaFormat encodecFormat;
  public static boolean isHasChangeConfig = false;
  private static boolean useH265;

  private static IBinder display;

  private static VirtualDisplay virtualDisplay;

  public static void init() throws InvocationTargetException, NoSuchMethodException, IllegalAccessException, IOException, ErrnoException {
    useH265 = Options.supportH265 && EncodecTools.isSupportH265();
    ByteBuffer byteBuffer = ByteBuffer.allocate(9);
    byteBuffer.put((byte) (useH265 ? 1 : 0));
    byteBuffer.putInt(Device.videoSize.first);
    byteBuffer.putInt(Device.videoSize.second);
    byteBuffer.flip();
    Server.writeVideo(byteBuffer);
    // 创建Codec
    createEncodecFormat();
    startEncode();
  }

  private static void createEncodecFormat() throws IOException {
    String codecMime = useH265 ? MediaFormat.MIMETYPE_VIDEO_HEVC : MediaFormat.MIMETYPE_VIDEO_AVC;
    encedec = MediaCodec.createEncoderByType(codecMime);
    encodecFormat = new MediaFormat();
    encodecFormat.setString(MediaFormat.KEY_MIME, codecMime);
    encodecFormat.setInteger(MediaFormat.KEY_BIT_RATE, Options.maxVideoBit);
    encodecFormat.setInteger(MediaFormat.KEY_FRAME_RATE, Options.maxFps);
    encodecFormat.setInteger(MediaFormat.KEY_I_FRAME_INTERVAL, 10);
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) encodecFormat.setInteger(MediaFormat.KEY_INTRA_REFRESH_PERIOD, Options.maxFps * 3);
    encodecFormat.setFloat("max-fps-to-encoder", Options.maxFps);
    encodecFormat.setLong(MediaFormat.KEY_REPEAT_PREVIOUS_FRAME_AFTER, 50_000);
    encodecFormat.setInteger(MediaFormat.KEY_COLOR_FORMAT, MediaCodecInfo.CodecCapabilities.COLOR_FormatSurface);
  }

  // 初始化编码器
  private static Surface surface;

  public static void startEncode() throws InvocationTargetException, NoSuchMethodException, IllegalAccessException, IOException, ErrnoException {
    ControlPacket.sendVideoSizeEvent();
    encodecFormat.setInteger(MediaFormat.KEY_WIDTH, Device.videoSize.first);
    encodecFormat.setInteger(MediaFormat.KEY_HEIGHT, Device.videoSize.second);
    encedec.configure(encodecFormat, null, null, MediaCodec.CONFIGURE_FLAG_ENCODE);
    // 绑定Display和Surface

    surface = encedec.createInputSurface();

    // 创建显示器
    try {
      virtualDisplay = DisplayManager.createVirtualDisplay("easycontrol", Device.displayInfo.width, Device.displayInfo.height, Device.displayInfo.density, surface);
    } catch (Exception displayManagerException) {
      display = SurfaceControl.createDisplay("easycontrol", Build.VERSION.SDK_INT < Build.VERSION_CODES.R || (Build.VERSION.SDK_INT == Build.VERSION_CODES.R && !"S".equals(Build.VERSION.CODENAME)));
      setDisplaySurface(display, surface);
    }

    // 启动编码
    encedec.start();
  }

  public static void stopEncode() {
    encedec.stop();
    encedec.reset();
    surface.release();

    try {
      if (display != null) {
        SurfaceControl.destroyDisplay(display);
        display = null;
      }
      if (virtualDisplay != null) {
          virtualDisplay.release();
          virtualDisplay = null;
      }
    } catch (Exception ignored) {
    }
  }

  private static void setDisplaySurface(IBinder display, Surface surface) throws InvocationTargetException, NoSuchMethodException, IllegalAccessException {
    SurfaceControl.openTransaction();
    try {
      SurfaceControl.setDisplaySurface(display, surface);
      SurfaceControl.setDisplayProjection(display, 0, new Rect(0, 0, Device.displayInfo.width, Device.displayInfo.height), new Rect(0, 0, Device.videoSize.first, Device.videoSize.second));
      SurfaceControl.setDisplayLayerStack(display, Device.displayInfo.layerStack);
    } finally {
      SurfaceControl.closeTransaction();
    }
  }

  private static final MediaCodec.BufferInfo bufferInfo = new MediaCodec.BufferInfo();

  public static void encodeOut() throws IOException {
    try {
      // 找到已完成的输出缓冲区
      int outIndex;
      do outIndex = encedec.dequeueOutputBuffer(bufferInfo, -1); while (outIndex < 0);
      ByteBuffer buffer = encedec.getOutputBuffer(outIndex);
      if (buffer == null) return;
      ControlPacket.sendVideoEvent(bufferInfo.presentationTimeUs, buffer);
      encedec.releaseOutputBuffer(outIndex, false);
    } catch (IllegalStateException ignored) {
    }
  }

  public static void release() {
    try {
      stopEncode();
      encedec.release();

      if (display != null) {
        SurfaceControl.destroyDisplay(display);
        display = null;
      }

      if (virtualDisplay != null) {
        virtualDisplay.release();
        virtualDisplay = null;
      }
    } catch (Exception ignored) {
    }
  }

}
