import cv2

cv2.namedWindow('yo')
print "Press Up arrow"
res = cv2.waitKey(0)
print "Up : " + str(res)
print "Press Down arrow"
res = cv2.waitKey(0)
print "Down : " + str(res)
print "Press Left arrow"
res = cv2.waitKey(0)
print "Left : " + str(res)
print "Press Right arrow"
res = cv2.waitKey(0)
print "Right : " + str(res)
