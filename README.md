# BybitBot
Automated cryptocurrency bot that is invoked on TradingView alerts

All output is sent to a Discord server, so if an error ever occurs, the user will always know

The bot can set take profits, stop losses, and trailing stops

## Required Packages
Python packages Flask, hashlib, hmac, json, requests, time, and urllib are required

```text
pip install Flask
pip install hashlib
pip install hmac
pip install json
pip install requests
pip install time
pip install urllib3
```

## TradingView Alert Format
All TradingView alerts must only say "Buy" or "Sell" otherwise the alert will not invoke an order

## Authors
James Peccia
