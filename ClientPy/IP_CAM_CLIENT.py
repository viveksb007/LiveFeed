import cv2
import numpy as np
import sys
import threading
import urllib2
from socket import error as SocketError

'''
Codes to Check button press (might not be same for yours)
So find codes with ArrowKeyCodes script and change code accordingly
Up : 1113938
Down : 1113940
Left : 1113937
Right : 1113939
'''

class AndroidCamFeed:
    __bytes = ''
    __stream = None
    __isOpen = False
    __feed = None
    __bytes = ''
    __noStreamCount = 1
    __loadCode = cv2.IMREAD_COLOR if sys.version_info[0] > 2 else 1

    def __init__(self, host):
        self.hoststr = 'http://' + host + '/video'
        try:
            AndroidCamFeed.__stream = urllib2.urlopen(self.hoststr, timeout=3)
            AndroidCamFeed.__isOpen = True
        except (SocketError, urllib2.URLError) as err:
            print "Failed to connect to stream. \nError: " + str(err)
            self.__close()
        t = threading.Thread(target=self.__captureFeed)
        t.start()

    def __captureFeed(self):
        while AndroidCamFeed.__isOpen:
            newbytes = AndroidCamFeed.__stream.read(1024)
            if not newbytes:
                self.__noStream()
                continue
            AndroidCamFeed.__bytes += newbytes
            self.a = AndroidCamFeed.__bytes.find('\xff\xd8')
            self.b = AndroidCamFeed.__bytes.find('\xff\xd9')
            if self.a != -1 and self.b != -1:
                self.jpg = AndroidCamFeed.__bytes[self.a: self.b + 2]
                AndroidCamFeed.__bytes = AndroidCamFeed.__bytes[self.b + 2:]
                AndroidCamFeed.__feed = cv2.imdecode(np.fromstring(self.jpg,
                                                                   dtype=np.uint8),
                                                     AndroidCamFeed.__loadCode)
        return

    def __close(self):
        AndroidCamFeed.__isOpen = False
        AndroidCamFeed.__noStreamCount = 1

    def __noStream(self):
        AndroidCamFeed.__noStreamCount += 1
        if AndroidCamFeed.__noStreamCount > 10:
            try:
                AndroidCamFeed.__stream = urllib2.urlopen(
                    self.hoststr, timeout=3)
            except (SocketError, urllib2.URLError) as err:
                print "Failed to connect to stream: Error: " + str(err)
                self.__close()

    def isOpened(self):
        return AndroidCamFeed.__isOpen

    def read(self):
        if AndroidCamFeed.__feed is not None:
            return True, AndroidCamFeed.__feed
        else:
            return False, None

    def release(self):
        self.__close()


def main():
    host = "192.168.43.1:8080"
    cv2.namedWindow('Android Feed', cv2.WINDOW_AUTOSIZE)

    acf = AndroidCamFeed(host)

    while acf.isOpened():
        ret, frame = acf.read()
        if ret:
            cv2.imshow('Android Feed', frame)
        k = cv2.waitKey(1)
        if k & 0xff is 27:
            break
        if k == 1113938:
            print "up"
        if k == 1113940:
            print "down"
        if k == 1113937:
            print "left"
        if k == 1113939:
            print "right"

    acf.release()
    cv2.destroyAllWindows()
    return


if __name__ == '__main__':
    main()

