XFileDialog ver 0.63 
============================================================================
Native windows filedialog for Java/Swing 
Author: stevpan@gmail.com
http://code.google.com/p/xfiledialog/


==Changelog:== 

1. A bug in folder dialog under Windows XP 64 bit was fixed. 
2. Applet deployment of XFileDialog was improved.  
3. The XFileDialog interface was changed a little. 

   1. A new public method String getSaveFile() was provided 
      for Save FileDialog only. 
   
   1. Some unnecessary or misleading methods were removed, e.g. 
      setMode(), getFilters(), setMultipleEnabled() 
      
      Actually, XFileDialog determines its internal native mode
      according the five calling functions: 
      getFile();  (single-selection, LOAD mode)
      getFilkes();(multi-selection, LOAD mode)
      getFolder();(single-selection, only dislplay folders, LOAD mode)
      getFolders(); (multi-selection, only display folder, LOAD mode)
      getSaveFile();(single-selection, SAVE mode) 
      
  
   2. The setFilters() method was replaced with two methods: 
      addFilters(), resetFilters(). 
   


===Program Demo/Test:=== 

run.bat

===Applet Demo:===

browse demo.html in appletdemo 



==Doc==
 
1. How to use in Java/Swing: http://code.google.com/p/xfiledialog/wiki/how_to_use_xfiledialog

2. How to use in Applet: 
http://code.google.com/p/xfiledialog/wiki/How_to_use_xfiledialog_in_applet

3. How to compile XFileDialog: 
http://code.google.com/p/xfiledialog/wiki/How_to_compile_xfiledialog


==Donation or Help:== 

A small donation is welcome if you are using it in commercial products. 

For freeware, Open-source, or private software developers, you could help me in another product of mine (Maple slideshow builder) if you are interested in graphic apps.  It still needs testing, correcting English syntax errors, 
advice or some promotion. 

Maple Java slideshow builder: http://sites.google.com/site/jtvmaker/
 
Email me if you wish to donate or help. 
stevpan@gmail.com

