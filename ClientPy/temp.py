import socket
import sys


host = "192.168.43.1"
port = 8080



pic = 1

while True:
    inp = input()
    if inp == 'x':
        break
    s = socket.socket()
    s.connect((host, port))
    file = open("temp" + str(pic) + ".jpg", "wb")
    while True:
        strng = s.recv(1024)
        if not strng:
            break
        file.write(strng)

    file.close()
    print("Image Saved\n")
    pic += 1
    s.close()
