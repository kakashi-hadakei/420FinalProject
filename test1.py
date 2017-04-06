import cv2
import matplotlib.pyplot as plt
import os
import sys
import time

if (len(sys.argv) < 2):
  print(' (ERROR) You must call this script with an argument (path_to_image_to_be_processed)\n')
  quit()

file_name = str(sys.argv[1])
image = cv2.imread(file_name)
gray = cv2.cvtColor(image,cv2.COLOR_BGR2GRAY) # grayscale
_,thresh = cv2.threshold(gray,150,255,cv2.THRESH_BINARY_INV) # threshold
kernel = cv2.getStructuringElement(cv2.MORPH_CROSS,(6,1))
dilated = cv2.dilate(thresh,kernel,iterations = 10) # dilate

img,contours, hierarchy = cv2.findContours(dilated,cv2.RETR_EXTERNAL,cv2.CHAIN_APPROX_NONE) # get contours


# for each contour found, draw a rectangle around it on original image
num = 0;
if os.path.exists("./output") is False:
    os.mkdir("output")

namelist = file_name.split('/')
name = namelist[2]
for contour in contours:
    # get rectangle bounding contour
    [x,y,w,h] = cv2.boundingRect(contour)

    # discard areas that are too large
    #if h>300 and w>300:
        #continue

    # discard areas that are too small
    if h<10 or w<10:
	   continue
    image_out = image[y:y+h,x:x+w]
    nameid = './output/outfile '+name+' '+str(num)+'.jpg'
    cv2.imwrite(nameid,image_out)
    num += 1

    # draw rectangle around contour on original image
    cv2.rectangle(image,(x,y),(x+w,y+h),(255,0,255),2)

# write original image with added contours to disk  
cv2.imwrite("./output/contoured.jpg", image)
plt.imshow(dilated)
plt.show()
