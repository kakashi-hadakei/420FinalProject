# 420FinalProject
$project: Business Card Information Extraction
========

This project extracts contact information on business cards and import it into contact list on android mobile device.

device used: NVIDIA SHIELD Tablet

Activities:
--------
In 420FinalProject/BusinessCard/app/src/main/java/com/example/ece420final/businesscard/:

-MainActivity.java: Primary User Interface for taking the picture
-DetectionRecognitionActivity.java: Show user the result of text detection of business card and prepare for the launch of contact list.
-Recognition.java: Class for recognizing the text using Tesseract and parsing the recognized contact information
-ContactInfo.java: Class for storing contact information and identifying name,phone number and email address.


Contribute
----------

- Source Code: https://github.com/kakashi-hadakei/420FinalProject

Dependencies
----------
installation:
OpenCV 3.2 sdk for android: please refer to OpenCV official website
Tess-two: compile 'com.rmtheis:tess-two:6.3.0'(in gradle build script)
Android image cropper:'compile 'com.theartofdev.edmodo:android-image-cropper:2.4.+'(in gradle build script)

source code for tess-two and image cropper:
tess-two:https://github.com/rmtheis/tess-two
image cropper:https://github.com/ArthurHub/Android-Image-Cropper
please refer to the source code repository for detailed operations.

  
License
-------

None
