import time
from threading import Thread

from log import log
from websocket import get_last_price


class Position:

    def __init__(self, user, order_id, side, entry_price, qty, stop_loss):
        """
        Constructor for Position
        'user' - user the position belongs to
        'order_id' - order id of the position
        'side' - 'Buy' or 'Sell'
        'entry_price' - price at which the order was executed
        'qty' - amount of money to place on the order
        'stop_loss' - stop loss price to exit order
        """
        self.entry_price = entry_price
        self.exit_price = -1
        self.side = side
        self.order_id = order_id
        self.position_exited = False
        self.qty = qty
        self.stop_loss = stop_loss
        self.take_profit_order_id = ""
        self.user = user

        Thread(target=self.monitor_position).start()

    def set_take_profit(self, order_id, price):
        # Allows a take profit to be set after the order is placed
        self.take_profit_order_id = order_id
        self.exit_price = price

    def monitor_position(self):
        # Monitors the current position
        reached_peak = False
        while not self.position_exited:
            last_price = get_last_price()
            if self.side == "Buy":
                roi = ((last_price - self.entry_price) / self.entry_price)
                if last_price <= self.stop_loss:
                    self.on_stop_loss_executed()
            else:
                roi = -((last_price - self.entry_price) / self.entry_price)
                if last_price >= self.stop_loss:
                    self.on_stop_loss_executed()

            # If roi increases then decreases, the position will be exited early to avoid a greater loss
            if roi > .0005 and not reached_peak:
                reached_peak = True

            elif roi < 0 and reached_peak:
                self.exit("retracement from peak")

            elif roi > .0015 and self.take_profit_order_id == "":
                self.exit("take profit but take profit was not set")

            elif roi > .0015 and self.take_profit_order_id is not "":
                if self.user.order_status(self.take_profit_order_id) == "Filled":
                    self.position_exited = True
                    self.profit_or_loss("Limit")
                time.sleep(2)

    def exit(self, reason):
        # Exits current position because of 'reason'
        if self.side == "Buy":
            side = "Sell"
        else:
            side = "Buy"

        log("all", "Exiting position due to " + reason + " (" + self.order_id + ")")
        is_canceled = self.user.cancel_active_order(self.take_profit_order_id)
        if is_canceled:
            log("all",
                "Successfully canceled take profit order (" + self.order_id + ")")
        else:
            log("error",
                "Failed to cancel take profit order (" + self.order_id + ")")

        order_id, exec_price = self.user.place_order(side, get_last_price(), self.qty, "Market", 0)
        if order_id is not "error":
            log("all", "Successfully exited position (" + self.order_id + ")")
            self.exit_price = get_last_price()
            self.position_exited = True
            self.profit_or_loss("Market")
        else:
            log("error", "Failed to exit position (" + self.order_id + ")")

    def on_stop_loss_executed(self):
        # Called after position should be closed due to stop loss
        log("all", "Stop loss executed (" + self.order_id + ")")
        is_canceled = self.user.cancel_active_order(self.take_profit_order_id)
        if is_canceled:
            log("all",
                "Successfully canceled take profit order (" + self.order_id + ")")
        else:
            log("error",
                "Failed to cancel take profit order (" + self.order_id + ")")
        self.exit_price = get_last_price()
        self.position_exited = True
        self.profit_or_loss("Market")

    def profit_or_loss(self, exit_type):
        # Called after position is closed, profit/loss is calculated according to 'exit_type'

        if exit_type == "Market":
            fee = .0015 * self.qty
        else:
            fee = .0005 * self.qty

        if self.side == "Buy":
            result = (self.exit_price / self.entry_price * self.qty) - self.qty - fee
            if result > 0:
                log("profit_and_loss", "Estimated profit $" + str(result) + " (" + self.order_id + ")")
            else:
                log("profit_and_loss", "Estimated loss $" + str(result) + " (" + self.order_id + ")")
        else:
            result = (self.entry_price / self.exit_price * self.qty) - self.qty - fee
            if result > 0:
                log("profit_and_loss", "Estimated profit $" + str(result) + " (" + self.order_id + ")")
            else:
                log("profit_and_loss", "Estimated loss $" + str(result) + " (" + self.order_id + ")")
