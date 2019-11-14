import webserver
import websocket
from log import log

# Logs the bot has been started
log('allchannels', "New instance of the trading bot started")

# Starts websocket to receive Bybit price action
websocket.start()
# Starts web server to receive TradingView alerts
webserver.start()
