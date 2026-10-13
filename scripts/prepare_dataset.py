import pandas as pd
import pyarrow.parquet as pq
import os
import json

print("=== Stratified Multi-Month Extraction (200,000+ flights across all 12 months) ===")
parquet_file = pq.ParquetFile("data_raw/flights.parquet")

# We want ~18,000 rows per month across all 12 months to satisfy seasonal & monthly requirements
month_dfs = {m: [] for m in range(1, 13)}
rows_per_month = 18000

print(f"Reading parquet across {parquet_file.num_row_groups} row groups...")
for rg in range(parquet_file.num_row_groups):
    df_group = parquet_file.read_row_group(rg).to_pandas()
    # Group by month
    for m in range(1, 13):
        curr_count = sum(len(x) for x in month_dfs[m])
        if curr_count < rows_per_month:
            sub = df_group[df_group['MONTH'] == m]
            if not sub.empty:
                needed = rows_per_month - curr_count
                month_dfs[m].append(sub.iloc[:needed])
    
    # Check if all months reached target
    all_done = all(sum(len(x) for x in month_dfs[m]) >= rows_per_month for m in range(1, 13))
    if all_done:
        print(f"All 12 months reached target at row group {rg}!")
        break

combined_batches = []
for m in range(1, 13):
    m_df = pd.concat(month_dfs[m], ignore_index=True)
    print(f"  Month {m:2d}: {len(m_df)} rows")
    combined_batches.append(m_df)

df_flights = pd.concat(combined_batches, ignore_index=True)
csv_path = "data/flights_raw_200k.csv"
df_flights.to_csv(csv_path, index=False)
print(f"Saved total {len(df_flights)} flights across all 12 months to {csv_path} ({os.path.getsize(csv_path)/1024/1024:.2f} MB)")

print("\n=== Profiling Real Multi-Month Data ===")
profile = {
    "total_rows": len(df_flights),
    "total_columns": len(df_flights.columns),
    "month_distribution": {int(m): int(c) for m, c in df_flights['MONTH'].value_counts().sort_index().items()},
    "columns": {}
}

for col in df_flights.columns:
    null_count = int(df_flights[col].isna().sum())
    unique_count = int(df_flights[col].nunique())
    col_type = str(df_flights[col].dtype)
    profile["columns"][col] = {
        "dtype": col_type,
        "null_count": null_count,
        "null_pct": round(null_count / len(df_flights) * 100, 2),
        "unique_count": unique_count
    }

with open("data/dataset_profile.json", "w") as f:
    json.dump(profile, f, indent=2)

print("Profile saved to data/dataset_profile.json")
print("Month distribution:", profile["month_distribution"])
