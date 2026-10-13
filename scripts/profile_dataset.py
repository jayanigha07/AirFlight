import pyarrow.parquet as pq
import pandas as pd
import json

print("=== 1. AIRLINES ===")
df_airlines = pd.read_parquet("data_raw/airlines.parquet")
print(f"Shape: {df_airlines.shape}")
print(df_airlines.head(14))
print()

print("=== 2. AIRPORTS ===")
df_airports = pd.read_parquet("data_raw/airports.parquet")
print(f"Shape: {df_airports.shape}")
print(df_airports.head(5))
print()

print("=== 3. CANCELLATION CODES ===")
df_canc = pd.read_parquet("data_raw/cancellation_codes.parquet")
print(f"Shape: {df_canc.shape}")
print(df_canc)
print()

print("=== 4. FLIGHTS ===")
parquet_file = pq.ParquetFile("data_raw/flights.parquet")
print(f"Total rows in flights.parquet: {parquet_file.metadata.num_rows}")
print(f"Total columns: {parquet_file.metadata.num_columns}")
print("Column schema:")
for i, col in enumerate(parquet_file.schema):
    print(f"  {i}: {col.name} ({col.physical_type})")

# Read a sample of first 1000 rows to inspect values and types
df_sample = parquet_file.read_row_group(0).to_pandas().head(10)
print("\nSample values:")
print(df_sample.iloc[0].to_dict())
