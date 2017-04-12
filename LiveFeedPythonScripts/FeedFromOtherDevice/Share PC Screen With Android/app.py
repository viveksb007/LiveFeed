from flask import Flask, render_template, Response
from screenshot import Screenshot
import os
import socket, errno, time

app = Flask(__name__)


@app.route('/')
def index():
    return render_template('index.html')


def gen(screenshot):
    while True:
        frame = screenshot.get_frame()
        yield (b'--frame\r\n'
               b'Content-Type: image/jpeg\r\n\r\n' + frame + b'\r\n')


@app.route('/video_feed')
def video_feed():
    return Response(gen(Screenshot()),
                    mimetype='multipart/x-mixed-replace; boundary=frame')
    



if __name__ == '__main__':
    port = int(os.environ.get("PORT", 5000))
    app.run(host='0.0.0.0', port=port)
