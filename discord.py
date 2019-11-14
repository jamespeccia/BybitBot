import json

import requests as r

# Webhook urls of discord channels to report bot information
discord_urls = {
    'all': 'YOUR WEBHOOK URL HERE',
    'error': 'YOUR WEBHOOK URL HERE',
    'profit_and_loss': 'YOUR WEBHOOK URL HERE'
}


# Sends update message 'message' to the correct discord channel indicated by 'tag'
def send_update(tag, message):
    data = {"content": message, "username": "Trading Bot"}
    data = json.dumps(data)

    if tag == "all":
        r.post(url=discord_urls['all'], data=data, headers={"Content-Type": "application/json"})

    elif tag == "error" or tag == "profit_and_loss":
        r.post(url=discord_urls['all'], data=data, headers={"Content-Type": "application/json"})
        r.post(url=discord_urls[tag], data=data, headers={"Content-Type": "application/json"})

    elif tag == "allchannels":
        r.post(url=discord_urls['all'], data=data, headers={"Content-Type": "application/json"})
        r.post(url=discord_urls['error'], data=data, headers={"Content-Type": "application/json"})
        r.post(url=discord_urls['profit_and_loss'], data=data, headers={"Content-Type": "application/json"})
