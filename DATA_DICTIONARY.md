# AirFlight — Data Dictionary

## 1. Dataset Overview

The dataset used in the AirFlight project is the official **U.S. Department of Transportation (DOT) / Bureau of Transportation Statistics (BTS) Airline On-Time Performance Dataset (2015)**.
The active analytical corpus contains a stratified, authentic sample of **216,000 flight records** covering all **12 months of the year** (18,000 flights per month), all **14 major US commercial carriers**, and **624 origin/destination airport endpoints**.

---

## 2. Table Specifications

### A. Flights Table (`flights_raw_200k.csv` / MySQL `flights`)
| Column Name | Physical Type | Logical Type | Nullable | Description / Business Rules |
| :--- | :--- | :--- | :--- | :--- |
| `YEAR` | INT | Integer | No | Flight year (2015). |
| `MONTH` | INT | Integer | No | Flight month (1 to 12). Used for monthly and seasonal grouping. |
| `DAY` | INT | Integer | No | Day of month (1 to 31). |
| `DAY_OF_WEEK` | INT | Integer | No | Day of week (1 = Monday, 7 = Sunday). |
| `AIRLINE` | VARCHAR(10) | String | No | 2-letter IATA carrier code (e.g., `AA`, `DL`, `UA`, `WN`). Foreign key to `airlines`. |
| `FLIGHT_NUMBER` | INT | Integer | No | Flight identifier assigned by the carrier. |
| `TAIL_NUMBER` | VARCHAR(10) | String | Yes | Aircraft registration number (e.g., `N407AS`). |
| `ORIGIN_AIRPORT` | VARCHAR(10) | String | No | 3-letter IATA airport code of departure (e.g., `JFK`, `ORD`, `ATL`). |
| `DESTINATION_AIRPORT` | VARCHAR(10) | String | No | 3-letter IATA airport code of arrival (e.g., `LAX`, `SFO`, `MIA`). |
| `SCHEDULED_DEPARTURE` | VARCHAR(10) | String | No | Scheduled departure time in local 24-hr military format (`HHMM`). |
| `DEPARTURE_TIME` | VARCHAR(10) | String | Yes | Actual gate departure time in `HHMM`. NULL if flight was cancelled. |
| `DEPARTURE_DELAY` | DOUBLE / INT | Minutes | Yes | Difference in minutes between actual and scheduled departure. Negative indicates early departure. |
| `TAXI_OUT` | INT | Minutes | Yes | Taxi time from gate to runway wheels-off. |
| `WHEELS_OFF` | VARCHAR(10) | String | Yes | Time wheels left the ground in `HHMM`. |
| `SCHEDULED_TIME` | INT | Minutes | Yes | Scheduled flight duration including taxi. |
| `ELAPSED_TIME` | INT | Minutes | Yes | Actual total elapsed gate-to-gate duration. |
| `AIR_TIME` | INT | Minutes | Yes | Airborne flight duration in minutes. |
| `DISTANCE` | INT | Miles | No | Direct distance between origin and destination in statutory miles. |
| `WHEELS_ON` | VARCHAR(10) | String | Yes | Time wheels touched the runway in `HHMM`. |
| `TAXI_IN` | INT | Minutes | Yes | Taxi time from runway touchdown to arrival gate. |
| `SCHEDULED_ARRIVAL` | VARCHAR(10) | String | No | Scheduled gate arrival time in `HHMM`. |
| `ARRIVAL_TIME` | VARCHAR(10) | String | Yes | Actual gate arrival time in `HHMM`. NULL if cancelled/diverted. |
| `ARRIVAL_DELAY` | DOUBLE / INT | Minutes | Yes | Difference in minutes between actual and scheduled arrival. Primary metric for on-time performance. |
| `DIVERTED` | TINYINT | Boolean | No | `1` if flight landed at an airport other than scheduled destination, `0` otherwise. |
| `CANCELLED` | TINYINT | Boolean | No | `1` if flight was cancelled before departure, `0` otherwise. |
| `CANCELLATION_REASON` | VARCHAR(10) | Code | Yes | Reason for cancellation: `A` (Carrier), `B` (Weather), `C` (NAS), `D` (Security). NULL for operated flights. |
| `AIR_SYSTEM_DELAY` | DOUBLE / INT | Minutes | Yes | Delay attributed to National Airspace System (FAA, traffic, non-extreme weather). Present only if delay >= 15 min. |
| `SECURITY_DELAY` | DOUBLE / INT | Minutes | Yes | Delay attributed to security breach or terminal evacuation. |
| `AIRLINE_DELAY` | DOUBLE / INT | Minutes | Yes | Delay within control of carrier (maintenance, crew, baggage). |
| `LATE_AIRCRAFT_DELAY` | DOUBLE / INT | Minutes | Yes | Delay caused by previous flight with same aircraft arriving late. |
| `WEATHER_DELAY` | DOUBLE / INT | Minutes | Yes | Delay caused by severe meteorological conditions. |

---

### B. Airlines Reference Table (`airlines.csv` / MySQL `airlines`)
| Column Name | Physical Type | Description |
| :--- | :--- | :--- |
| `IATA_CODE` | VARCHAR(10) PRIMARY KEY | 2-character carrier code (`AA`, `DL`, `UA`, etc.). |
| `AIRLINE` | VARCHAR(100) | Full legal operating name of the commercial carrier. |

### C. Cancellation Codes Reference Table (`cancellation_codes.csv` / MySQL `cancellation_codes`)
| Code | Category | Official DOT Definition |
| :--- | :--- | :--- |
| `A` | Carrier | Airline/Carrier controlled issues (mechanical, dispatch, crew duty limits). |
| `B` | Weather | Meteorological conditions preventing safe flight operations. |
| `C` | NAS | National Air System (air traffic volume, airport throughput, non-extreme weather). |
| `D` | Security | Security incidents, terminal evictions, re-screening operations. |
