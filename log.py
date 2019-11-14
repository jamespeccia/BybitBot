import time

from discord import send_update


# Logs all information about the bot to the specified discord channel and console, as well as a local .txt file
def log(tag, message):
    print(time.asctime() + ":", message)
    send_update(tag, message)
    with open("log.txt", "a+") as f:
        f.write(time.asctime() + ": " + message + "\n")
