#!/usr/bin/env python3
import requests
import json

url = "https://ophim.cc/api/v9/phim/quanh-ta-la-cac-sao"

try:
    response = requests.get(url, timeout=10)
    data = response.json()

    # Pretty print the JSON
    print(json.dumps(data, indent=2, ensure_ascii=False))

except Exception as e:
    print(f"Error: {e}")

