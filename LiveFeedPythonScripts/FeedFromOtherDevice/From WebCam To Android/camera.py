import time
import cv2


class Camera(object):
    def __init__(self):
        self.cam = cv2.VideoCapture(0)
        self.cam.set(3, 800)
        self.cam.set(4, 600)
        time.sleep(1)

    def get_frame(self):
        ret, img = self.cam.read()
        ret2, jpeg = cv2.imencode('.jpg', img)
        return jpeg.tostring()

    def __del__(self):
        self.cam.release()
