import hmac
import json
import time
from hashlib import sha256
from urllib.parse import urlencode

import requests as r


class User:

    def __init__(self, api_key, api_secret):
        """
        Constructor for User object
        'api_key' and 'secret_key' are the api key and secret key for the Bybit account
        """
        self.API_KEY = api_key
        self.API_SECRET = api_secret

    def sign(self, data):
        # Signs the request for authentication with the Bybit servers
        hash = hmac.new(self.API_SECRET.encode(), urlencode(data).encode(), sha256)
        return hash.hexdigest()

    def change_leverage(self, leverage):
        # Changes the account leverage to 'leverage'
        API_ENDPOINT = "https://api.bybit.com/user/leverage/save"
        current_time = int(time.time() * 1000)

        data = {
            'api_key': self.API_KEY,
            'leverage': leverage,
            'symbol': 'BTCUSD',
            'timestamp': current_time
        }

        signature = self.sign(data)
        data.update({'sign': signature})
        r.post(API_ENDPOINT, params=urlencode(data))

    def get_leverage(self):
        # Returns the current account leverage
        API_ENDPOINT = "https://api.bybit.com/user/leverage"
        current_time = int(time.time() * 1000)

        data = {
            'api_key': self.API_KEY,
            'symbol': 'BTCUSD',
            'timestamp': current_time
        }

        signature = self.sign(data)
        data.update({'sign': signature})
        response = r.get(API_ENDPOINT, params=urlencode(data))

        try:
            response = json.loads(response.text)
            leverage = int(response['result']['BTCUSD']['leverage'])
        except:
            return 'error'

        return leverage

    def place_order(self, side, price, qty, order_type, stop_loss):
        """
        Places order
        'side' - 'Buy' or 'Sell'
        'price' - price for order to execute
        'qty' - amount of money to place on the order
        'order_type' - 'Market' or 'Limit'
        'stop_loss' - stop loss price to exit order
        """

        API_ENDPOINT = "https://api.bybit.com/open-api/order/create"
        current_time = int(time.time() * 1000)

        if stop_loss is not 0:
            data = {
                'api_key': self.API_KEY,
                'order_type': order_type,
                'price': price,
                'qty': qty,
                'side': side,
                'stop_loss': stop_loss,
                'symbol': 'BTCUSD',
                'time_in_force': "PostOnly",
                'timestamp': current_time
            }
        else:
            data = {
                'api_key': self.API_KEY,
                'order_type': order_type,
                'price': price,
                'qty': qty,
                'side': side,
                'symbol': 'BTCUSD',
                'time_in_force': "PostOnly",
                'timestamp': current_time
            }

        signature = self.sign(data)
        data.update({'sign': signature})

        response = r.post(API_ENDPOINT, params=urlencode(data))
        a = response.text

        try:
            response = json.loads(response.text)
            order_id = response['result']['order_id']
            exec_price = float(response['result']['last_exec_price'])
            print(a)
        except:
            print(a)
            order_id = "error"
            exec_price = -1

        return order_id, exec_price

    def order_status(self, order_id):
        # Returns the order status of 'order_id'
        API_ENDPOINT = "https://api.bybit.com/open-api/order/list"
        current_time = int(time.time() * 1000)

        data = {
            'api_key': self.API_KEY,
            'order_id': order_id,
            'timestamp': current_time
        }

        signature = self.sign(data)
        data.update({'sign': signature})

        response = r.get(API_ENDPOINT, params=urlencode(data))

        try:
            response = json.loads(response.text)
            order_status = response['result']['data'][0]['order_status']
        except:
            order_status = "error"

        return order_status

    def cancel_active_order(self, order_id):
        # Cancels an order 'order_id' that has been placed but not executed
        API_ENDPOINT = "https://api.bybit.com/open-api/order/cancel"
        current_time = int(time.time() * 1000)

        data = {
            'api_key': self.API_KEY,
            'order_id': order_id,
            'timestamp': current_time
        }

        signature = self.sign(data)
        data.update({'sign': signature})

        response = r.post(API_ENDPOINT, params=urlencode(data))

        try:
            response = json.loads(response.text)
            if response['result']['order_status'] == "Cancelled":
                return True
        except:
            return False

        return False
