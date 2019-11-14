from flask import Flask, request, abort

from order import new_order

app = Flask(__name__)


# Starts web server to listen for alerts
def start():
    app.run(host="0.0.0.0", port=80)


# Declares POST url to receive and handle alerts
@app.route('/newalert', methods=['POST'])
def newalert():
    if request.method == 'POST':
        alert = request.data.decode("utf-8")
        new_order(alert)
        return '', 200
    else:
        abort(400)
