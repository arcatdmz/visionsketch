=CPPJARs for JavaCV 0.6 on Linux, Mac OS X, Windows, and Android=
==Introduction==
The JAR files of OpenCV and FFmpeg found in this package are meant to be used with [http://code.google.com/p/javacv/ JavaCV]. They were built on Fedora 17, so they may not work on all distributions of Linux, especially older ones. The binaries for Android were compiled for ARMv7 processors featuring an FPU, so they will not work on ancient devices such as the HTC Magic or some others with an ARMv6 CPU.

To compile binaries for an Android device with no FPU, first make sure this is what you want. Without FPU, the performance of either OpenCV or FFmpeg is bound to be unacceptable. If you still wish to continue down that road, then replace "libs/armeabi-v7a" by "libs/armeabi" and "-march=armv7-a -mfloat-abi=softfp -mfpu=vfp -mfpu=neon" with "-march=armv5te -mtune=xscale -msoft-float", inside the patch files.

==Rebuilding Instructions==
Required software:
 * A recent version of Linux (or Mac OS X) with at least Java SE 6
 * Android NDK r9  http://developer.android.com/sdk/ndk/

Additionally, although the script files should download them automatically, you may place these library archives inside this directory:
 * OpenCV 2.4.6.x  http://sourceforge.net/projects/opencvlibrary/files/
 * FFmpeg 2.0.1  http://ffmpeg.org/download.html
  * For Windows  http://ffmpeg.zeranoe.com/builds/  Known compatible builds:
   * http://ffmpeg.zeranoe.com/builds/win32/dev/ffmpeg-20130915-git-7ac6c63-win32-dev.7z
   * http://ffmpeg.zeranoe.com/builds/win64/dev/ffmpeg-20130915-git-7ac6c63-win64-dev.7z
   * http://ffmpeg.zeranoe.com/builds/win32/shared/ffmpeg-20130915-git-7ac6c63-win32-shared.7z
   * http://ffmpeg.zeranoe.com/builds/win64/shared/ffmpeg-20130915-git-7ac6c63-win64-shared.7z
 * x264  ftp://ftp.videolan.org/pub/videolan/x264/snapshots/last_stable_x264.tar.bz2

Then, execute:
    $ ANDROID_NDK=/path/to/android-ndk-r9/ MSVC_REDIST=/path/to/msvc/redist/ bash build_all.sh <android | linux | macosx | windows>-<arm | x86 | x86_64>

Finally, to build JavaCV against those, we need to actually install the libraries on the system with `make install` and what not. In particular, [http://ffmpeg.org/platform.html#Linking-to-FFmpeg-with-Microsoft-Visual-C_002b_002b we need to recreate the `.lib` files of the Zeranoe FFmpeg Windows builds]:
{{{
    lib /def:avcodec-55.def /out:avcodec.lib
    lib /def:avdevice-55.def /out:avdevice.lib
    lib /def:avfilter-3.def /out:avfilter.lib
    lib /def:avformat-55.def /out:avformat.lib
    lib /def:avutil-52.def /out:avutil.lib
    lib /def:postproc-52.def /out:postproc.lib
    lib /def:swresample-0.def /out:swresample.lib
    lib /def:swscale-2.def /out:swscale.lib
}}}

At runtime, however, JavaCPP can load the libraries from the created JAR files above, a useful feature for standalone applications or Java applets. Moreover, tricks such as the following work with JNLP:
{{{
    <resources os="Linux" arch="x86 i386 i486 i586 i686">
        <jar href="lib/javacv-linux-x86.jar"/>
        <jar href="lib/opencv-2.4.6.1-linux-x86.jar"/>
    </resources>
    <resources os="Linux" arch="x86_64 amd64">
        <jar href="lib/javacv-linux-x86_64.jar"/>
        <jar href="lib/opencv-2.4.6.1-linux-x86_64.jar"/>
    </resources>
}}}

Thanks to Jose Gómez for testing this out!

----
Copyright (C) 2011,2012,2013 Samuel Audet <samuel.audet@gmail.com>
Project site: http://code.google.com/p/javacv/
