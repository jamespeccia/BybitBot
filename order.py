from log import log
from position import Position
from user import User
from websocket import get_last_price

# Bybit API key and secret
API_KEY = "YOUR_API_KEY"
API_SECRET = "YOUR_API_SECRET"

# Creates new user object
user = User(API_KEY, API_SECRET)

# Amount of money to trade each time a new alert occurs
qty = 1

# Sets take profit and stop loss
take_profit_decimal = .0015
stop_loss_decimal = .0015


def new_order(alert):
    # Places a new order according to the contents of 'alert'
    last_price = get_last_price()

    if alert == "Buy":
        take_profit_price = int(last_price * (1 + take_profit_decimal))
        stop_loss_price = int(last_price * (1 - stop_loss_decimal))
        order_id, exec_price = user.place_order("Buy", last_price, qty, "Market", stop_loss_price)
        if order_id is not "error":
            log("all", "Executed long order (" + order_id + ")")
            position = Position(user, order_id, alert, last_price, qty, stop_loss_price)
            take_profit_order_id, _ = user.place_order("Sell", take_profit_price, qty, "Limit", 0)
            if take_profit_order_id is not "error":
                log("all", "Executed take profit (" + order_id + ")")
                position.set_take_profit(take_profit_order_id, take_profit_price)
            else:
                log("error", "Failed to execute take profit (" + order_id + ")")
        else:
            log("error", "Failed to execute long order")

    elif alert == "Sell":
        take_profit_price = int(last_price * (1 - take_profit_decimal))
        stop_loss_price = int(last_price * (1 + stop_loss_decimal))
        order_id, exec_price = user.place_order("Sell", last_price, qty, "Market", stop_loss_price)
        if order_id is not "error":
            log("all", "Executed short order (" + order_id + ")")
            position = Position(user, order_id, alert, last_price, qty, stop_loss_price)
            take_profit_order_id, _ = user.place_order("Buy", take_profit_price, qty, "Limit", 0)
            if take_profit_order_id is not "error":
                log("all", "Executed take profit (" + order_id + ")")
                position.set_take_profit(order_id, take_profit_price)
            else:
                log("error", "Failed to execute take profit (" + order_id + ")")
        else:
            log("error", "Failed to execute short order")

    else:
        log("error", "Alert received, but could not be understood")
