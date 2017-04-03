import cv2
import numpy as np
from matplotlib import pyplot as plt
img = cv2.imread('ececard1.jpg',0)



kernel = np.ones((5,5), np.uint8)

img_erosion = cv2.erode(img, kernel, iterations=2)
cv2.imwrite("erosion.jpg",img_erosion)
img_dilation = cv2.dilate(img, kernel, iterations=2)

plt.subplot(131),plt.imshow(img,cmap = 'gray')
plt.title('Original Image'), plt.xticks([]), plt.yticks([])
plt.subplot(132),plt.imshow(img_erosion,cmap = 'gray')
plt.title("erosion")
plt.subplot(133),plt.imshow(img_dilation,cmap = 'gray')
plt.title("dilation")



plt.figure()


edges = cv2.Canny(img_erosion,50,100)
plt.subplot(121),plt.imshow(img_erosion,cmap = 'gray')
plt.title('Original Image'), plt.xticks([]), plt.yticks([])
plt.subplot(122),plt.imshow(edges,cmap = 'gray')
plt.title('Edge Image'), plt.xticks([]), plt.yticks([])
plt.show()