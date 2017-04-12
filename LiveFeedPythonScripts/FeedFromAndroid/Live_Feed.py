# Run this on PC ( or any device that supports python and has required dependencies ) and it would receive and show feed from Android Device

import socket
import cv2
import numpy as np

TCP_IP = "192.168.43.1"
TCP_PORT = 8080

sock = socket.socket()
sock.connect((TCP_IP, TCP_PORT))

cv2.namedWindow("Feed", cv2.WINDOW_AUTOSIZE)

imgData = ''

while True:
    k = cv2.waitKey(1)
    if k & 0xff is 27:
        break
    data = sock.recv(1024)
    if not data:
        continue
    imgData += data
    a = imgData.find('\xff\xd8')
    b = imgData.find('\xff\xd9')
    if a != -1 and b != -1:
        feed = cv2.imdecode(np.fromstring(imgData[a:b + 2], dtype=np.uint8), 1)
        cv2.imshow("Feed", feed)
        imgData = imgData[b + 2:]
