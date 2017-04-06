'''
Simple and fast image transforms to mimic:
 - brightness
 - contrast
 - erosion 
 - dilation
'''

'''
Simple and fast image transforms to mimic:
 - brightness
 - contrast
 - erosion 
 - dilation
'''

import cv2
import sys
from pylab import array, plot, show, axis, arange, figure, uint8 

# Image data
if (len(sys.argv) < 2):
  print(' (ERROR) You must call this script with an argument (path_to_image_to_be_processed)\n')
  quit()

file_name = str(sys.argv[1])
image = cv2.imread(file_name,0) # load as 1-channel 8bit grayscale
maxIntensity = 255.0 # depends on dtype of image data
x = arange(maxIntensity) 

# Parameters for manipulating image data
phi = 1
theta = 1

# Increase intensity such that
# dark pixels become much brighter, 
# bright pixels become slightly bright
newImage0 = (maxIntensity/phi)*(image/(maxIntensity/theta))**0.9
newImage0 = array(newImage0,dtype=uint8)

#cv2.imshow('newImage0',newImage0)
cv2.imwrite('./input/newImage0.jpg',newImage0)

y = (maxIntensity/phi)*(x/(maxIntensity/theta))**0.5

# Decrease intensity such that
# dark pixels become much darker, 
# bright pixels become slightly dark 
newImage1 = (maxIntensity/phi)*(image/(maxIntensity/theta))**2
newImage1 = array(newImage1,dtype=uint8)

#cv2.imshow('newImage1',newImage1)
cv2.imwrite('./input/newImage1.jpg',newImage1)


z = (maxIntensity/phi)*(x/(maxIntensity/theta))**2

# Plot the figures
figure()
#plot(x,y,'r-') # Increased brightness
#plot(x,x,'k:') # Original image
#plot(x,z, 'b-') # Decreased brightness
#axis('off')
#axis('tight')
#show()

# Close figure window and click on other window 
# Then press any keyboard key to close all windows
closeWindow = -1
while closeWindow<0:
    closeWindow = cv2.waitKey(1) 
cv2.destroyAllWindows()
