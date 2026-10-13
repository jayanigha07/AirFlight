#!/usr/bin/env python3
"""
AirFlight REST API & Web Application Server
Exposes existing AirFlight analytics directly to the frontend web application.
Preserves existing Big Data pipeline and analytics without modification.
"""

import os
import sys
import json
import mimetypes
import urllib.parse
from http.server import HTTPServer, BaseHTTPRequestHandler
from typing import Optional, Dict, Any

# Ensure project root is in sys.path
BASE_DIR = os.path.dirname(os.path.abspath(__file__))
if BASE_DIR not in sys.path:
    sys.path.insert(0, BASE_DIR)

from analytics.airflight_engine import AirFlightAnalytics

# Common known airports in BTS dataset
AIRPORTS = [
    {"code": "ORD", "name": "ORD — Chicago O'Hare", "city": "Chicago"},
    {"code": "LGA", "name": "LGA — New York LaGuardia", "city": "New York"},
    {"code": "JFK", "name": "JFK — New York JFK", "city": "New York"},
    {"code": "EWR", "name": "EWR — Newark Liberty", "city": "Newark"},
    {"code": "LAX", "name": "LAX — Los Angeles Intl", "city": "Los Angeles"},
    {"code": "SFO", "name": "SFO — San Francisco Intl", "city": "San Francisco"},
    {"code": "ATL", "name": "ATL — Atlanta Hartsfield", "city": "Atlanta"},
    {"code": "DFW", "name": "DFW — Dallas/Fort Worth", "city": "Dallas"},
    {"code": "BOS", "name": "BOS — Boston Logan", "city": "Boston"},
    {"code": "SEA", "name": "SEA — Seattle-Tacoma", "city": "Seattle"},
    {"code": "DEN", "name": "DEN — Denver Intl", "city": "Denver"},
    {"code": "LAS", "name": "LAS — Las Vegas McCarran", "city": "Las Vegas"},
    {"code": "MCO", "name": "MCO — Orlando Intl", "city": "Orlando"},
    {"code": "PHX", "name": "PHX — Phoenix Sky Harbor", "city": "Phoenix"},
    {"code": "MIA", "name": "MIA — Miami Intl", "city": "Miami"},
    {"code": "DTW", "name": "DTW — Detroit Metropolitan", "city": "Detroit"},
    {"code": "MSP", "name": "MSP — Minneapolis-St. Paul", "city": "Minneapolis"}
]

AIRLINES = [
    {"code": "UA", "name": "United Airlines (UA)"},
    {"code": "AA", "name": "American Airlines (AA)"},
    {"code": "DL", "name": "Delta Air Lines (DL)"},
    {"code": "WN", "name": "Southwest Airlines (WN)"},
    {"code": "B6", "name": "JetBlue Airways (B6)"},
    {"code": "AS", "name": "Alaska Airlines (AS)"},
    {"code": "NK", "name": "Spirit Airlines (NK)"},
    {"code": "F9", "name": "Frontier Airlines (F9)"},
    {"code": "VX", "name": "Virgin America (VX)"},
    {"code": "HA", "name": "Hawaiian Airlines (HA)"}
]

# Robust Month Normalization Map (supports numbers 1-12, names January-December, abbreviations)
MONTH_NAME_TO_INT = {
    "1": 1, "01": 1, "january": 1, "jan": 1,
    "2": 2, "02": 2, "february": 2, "feb": 2,
    "3": 3, "03": 3, "march": 3, "mar": 3,
    "4": 4, "04": 4, "april": 4, "apr": 4,
    "5": 5, "05": 5, "may": 5,
    "6": 6, "06": 6, "june": 6, "jun": 6,
    "7": 7, "07": 7, "july": 7, "jul": 7,
    "8": 8, "08": 8, "august": 8, "aug": 8,
    "9": 9, "09": 9, "september": 9, "sep": 9, "sept": 9,
    "10": 10, "october": 10, "oct": 10,
    "11": 11, "november": 11, "nov": 11,
    "12": 12, "december": 12, "dec": 12
}

MIME_TYPES = {
    ".html": "text/html; charset=utf-8",
    ".css": "text/css; charset=utf-8",
    ".js": "application/javascript; charset=utf-8",
    ".json": "application/json; charset=utf-8",
    ".png": "image/png",
    ".jpg": "image/jpeg",
    ".svg": "image/svg+xml",
    ".ico": "image/x-icon"
}

def parse_month(val: Any) -> Optional[int]:
    """
    Parses month parameter into integer 1-12 or None for All Months.
    Never confuses December (12) with All months (None).
    """
    if val is None:
        return None
    s = str(val).strip().lower()
    if s in ["", "all", "all months", "all_months", "none", "null"]:
        return None
    if s in MONTH_NAME_TO_INT:
        return MONTH_NAME_TO_INT[s]
    # Check if purely digits
    if s.isdigit():
        n = int(s)
        if 1 <= n <= 12:
            return n
    return None

def parse_departure_time(val: Any) -> int:
    """
    Parses departure window or time string into military integer HHMM.
    Supports names ('MORNING', 'AFTERNOON', 'EVENING', 'NIGHT'),
    full descriptions ('Evening (17:00–21:59)'), and direct military times ('1830').
    """
    if val is None:
        return 1830
    s = str(val).upper().strip()

    if "MORN" in s:
        return 830
    elif "AFT" in s:
        return 1400
    elif "EVE" in s:
        return 1830
    elif "NIGHT" in s or "RED" in s:
        return 2230

    # Extract digits if direct time was provided
    digits = "".join(c for c in s if c.isdigit())
    if digits:
        v = int(digits)
        if "PM" in s and v < 1200:
            v += 1200
        return v

    return 1830

class AirFlightServerHandler(BaseHTTPRequestHandler):
    analytics_engine = None

    @classmethod
    def get_engine(cls):
        if cls.analytics_engine is None:
            cls.analytics_engine = AirFlightAnalytics()
        return cls.analytics_engine

    def _send_json(self, data: Any, status_code: int = 200):
        body = json.dumps(data).encode("utf-8")
        self.send_response(status_code)
        self.send_header("Content-Type", "application/json; charset=utf-8")
        self.send_header("Content-Length", str(len(body)))
        self.send_header("Access-Control-Allow-Origin", "*")
        self.send_header("Access-Control-Allow-Methods", "GET, POST, OPTIONS")
        self.send_header("Access-Control-Allow-Headers", "Content-Type")
        self.end_headers()
        self.wfile.write(body)

    def _send_error_json(self, message: str, status_code: int = 400):
        self._send_json({"status": "ERROR", "message": message}, status_code)

    def do_OPTIONS(self):
        self.send_response(204)
        self.send_header("Access-Control-Allow-Origin", "*")
        self.send_header("Access-Control-Allow-Methods", "GET, POST, OPTIONS")
        self.send_header("Access-Control-Allow-Headers", "Content-Type")
        self.end_headers()

    def do_GET(self):
        parsed = urllib.parse.urlparse(self.path)
        path = parsed.path
        query = urllib.parse.parse_qs(parsed.query)

        # 1. API: Airport listing
        if path == "/api/airports":
            self._send_json({"status": "SUCCESS", "airports": AIRPORTS})
            return

        # 2. API: Airline listing
        if path == "/api/airlines":
            self._send_json({"status": "SUCCESS", "airlines": AIRLINES})
            return

        # 3. API: Best Time to Fly
        if path == "/api/best-time":
            origin = query.get("origin", [""])[0].strip()
            dest = query.get("dest", [""])[0].strip()
            month_param = query.get("month", [None])[0]

            if not origin or not dest:
                self._send_error_json("Both origin and destination airport codes are required.")
                return

            month = parse_month(month_param)
            try:
                engine = self.get_engine()
                res = engine.best_time_to_fly(origin, dest, month)
                self._send_json(res)
            except Exception as e:
                self._send_error_json(f"Analytics query error: {str(e)}", 500)
            return

        # 4. API: Route Reliability Score
        if path == "/api/route-reliability":
            origin = query.get("origin", [""])[0].strip()
            dest = query.get("dest", [""])[0].strip()
            month_param = query.get("month", [None])[0]

            if not origin or not dest:
                self._send_error_json("Both origin and destination airport codes are required.")
                return

            month = parse_month(month_param)
            try:
                engine = self.get_engine()
                res = engine.route_reliability_score(origin, dest)

                # Enrich with best/worst departure periods from best_time_to_fly
                if res.get("status") == "SUCCESS":
                    time_comp = engine.best_time_to_fly(origin, dest, month)
                    if time_comp.get("status") == "SUCCESS":
                        periods = time_comp.get("all_periods_comparison", [])
                        if periods:
                            res["best_period"] = periods[0]
                            res["most_challenging_period"] = periods[-1]
                            res["periods_breakdown"] = periods

                self._send_json(res)
            except Exception as e:
                self._send_error_json(f"Analytics query error: {str(e)}", 500)
            return

        # 5. API: Historical Delay Risk
        if path == "/api/delay-risk":
            airline = query.get("airline", [""])[0].strip()
            origin = query.get("origin", [""])[0].strip()
            dest = query.get("dest", [""])[0].strip()
            month_param = query.get("month", ["6"])[0]
            time_param = query.get("time", ["1830"])[0]

            if not airline or not origin or not dest:
                self._send_error_json("Airline, origin, and destination codes are required.")
                return

            month = parse_month(month_param)
            if month is None:
                month = 6
            dep_time = parse_departure_time(time_param)

            try:
                engine = self.get_engine()
                res = engine.historical_delay_risk(airline, origin, dest, month, dep_time)
                self._send_json(res)
            except Exception as e:
                self._send_error_json(f"Analytics query error: {str(e)}", 500)
            return

        # Static File Serving from frontend/ directory
        self._serve_static(path)

    def do_POST(self):
        parsed = urllib.parse.urlparse(self.path)
        path = parsed.path

        # Parse JSON body
        try:
            length = int(self.headers.get("Content-Length", 0))
            body_bytes = self.rfile.read(length) if length > 0 else b"{}"
            data = json.loads(body_bytes.decode("utf-8")) if body_bytes else {}
        except Exception:
            self._send_error_json("Invalid JSON body.")
            return

        engine = self.get_engine()

        if path == "/api/best-time":
            origin = data.get("origin", "").strip()
            dest = data.get("dest", "").strip()
            month_param = data.get("month")
            if not origin or not dest:
                self._send_error_json("origin and dest are required.")
                return
            month = parse_month(month_param)
            res = engine.best_time_to_fly(origin, dest, month)
            self._send_json(res)
            return

        if path == "/api/route-reliability":
            origin = data.get("origin", "").strip()
            dest = data.get("dest", "").strip()
            month_param = data.get("month")
            if not origin or not dest:
                self._send_error_json("origin and dest are required.")
                return
            month = parse_month(month_param)
            res = engine.route_reliability_score(origin, dest)
            if res.get("status") == "SUCCESS":
                time_comp = engine.best_time_to_fly(origin, dest, month)
                if time_comp.get("status") == "SUCCESS":
                    periods = time_comp.get("all_periods_comparison", [])
                    if periods:
                        res["best_period"] = periods[0]
                        res["most_challenging_period"] = periods[-1]
                        res["periods_breakdown"] = periods
            self._send_json(res)
            return

        if path == "/api/delay-risk":
            airline = data.get("airline", "").strip()
            origin = data.get("origin", "").strip()
            dest = data.get("dest", "").strip()
            month_param = data.get("month", 6)
            dep_time_param = data.get("time", 1830)

            if not airline or not origin or not dest:
                self._send_error_json("airline, origin, and dest are required.")
                return

            month = parse_month(month_param)
            if month is None:
                month = 6
            dep_time = parse_departure_time(dep_time_param)

            res = engine.historical_delay_risk(airline, origin, dest, month, dep_time)
            self._send_json(res)
            return

        self._send_error_json(f"Unknown API endpoint: {path}", 404)

    def _serve_static(self, path: str):
        if path in ["", "/", "/index.html"]:
            rel_file = "index.html"
        else:
            rel_file = path.lstrip("/")

        frontend_dir = os.path.join(BASE_DIR, "frontend")
        full_path = os.path.normpath(os.path.join(frontend_dir, rel_file))

        if not full_path.startswith(frontend_dir) or not os.path.exists(full_path) or os.path.isdir(full_path):
            self.send_response(404)
            self.send_header("Content-Type", "text/plain")
            self.end_headers()
            self.wfile.write(b"404 Not Found")
            return

        _, ext = os.path.splitext(full_path)
        mime_type = MIME_TYPES.get(ext.lower())
        if not mime_type:
            mime_type, _ = mimetypes.guess_type(full_path)
        if not mime_type:
            mime_type = "application/octet-stream"

        with open(full_path, "rb") as f:
            content = f.read()

        self.send_response(200)
        self.send_header("Content-Type", mime_type)
        self.send_header("Content-Length", str(len(content)))
        self.end_headers()
        self.wfile.write(content)

def run(port: int = 3000):
    server_address = ("0.0.0.0", port)
    httpd = HTTPServer(server_address, AirFlightServerHandler)
    print(f"AirFlight Web & API Server active on http://0.0.0.0:{port}")
    httpd.serve_forever()

if __name__ == "__main__":
    port = int(sys.argv[1]) if len(sys.argv) > 1 and sys.argv[1].isdigit() else 3000
    run(port)
