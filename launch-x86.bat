REM Comment line 5 and uncomment line 4 if the default VM in your environment is x86.
REM Change Java VM path at line 4 if neeed.

REM set JAVA=java
set JAVA="C:\Program Files (x86)\Java\jdk1.7.0_45\bin\java.exe"

%JAVA% -d32 -cp bin;lib/ecj/ecj-4.3.1.jar;lib/javacv/javacpp.jar;lib/javacv/javacv.jar;lib/javacv/javacv-windows-x86.jar;lib/opencv/opencv-2.4.6.0-windows-x86.jar;lib/opencv/ffmpeg-20130915-git-7ac6c63-windows-x86.jar;lib/rsyntaxtextarea/rsyntaxtextarea.jar;lib/simple/simple-xml-2.7.1.jar;lib/xfiledialog/xfiledialog.jar;lib/resources.jar jp.junkato.vsketch.VsketchMain
