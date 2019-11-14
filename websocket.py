import asyncio
import json
from threading import Thread

import websockets

websocket_address = "wss://stream.bybit.com/realtime"
last_price = None


def start():
    # Starts a new thread to listen for Bybit price action
    Thread(target=initialize).start()


def initialize():
    # Sets thread settings
    loop = asyncio.new_event_loop()
    asyncio.set_event_loop(loop)
    loop.run_until_complete(listen())


async def listen():
    # Listens for price action and sets the last price to 'last_price'
    global last_price
    async with websockets.connect(websocket_address) as w:
        await w.send('{"op":"subscribe","args":["instrument.BTCUSD"]}')
        while True:
            result = await w.recv()
            result = json.loads(result)
            try:
                last_price = result["data"][0]["last_price"]
                last_price = float(last_price)
            except:
                pass


def get_last_price():
    # Returns 'last_price'
    return last_price
