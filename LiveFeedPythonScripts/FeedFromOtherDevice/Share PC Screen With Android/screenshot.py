import time
import cv2
import pyscreenshot as ImageGrab
import numpy as np

class Screenshot(object):
    def get_frame(self):
        img = np.array(ImageGrab.grab().convert('RGB'), dtype=np.uint8)
        img = cv2.cvtColor(img, cv2.COLOR_RGB2BGR)
        ret2, jpeg = cv2.imencode('.jpg', img)
        return jpeg.tostring()

    def __del__(self):
        self.cam.release()
