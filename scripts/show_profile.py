import json

with open("data/dataset_profile.json") as f:
    p = json.load(f)

print(f"Total Rows: {p['total_rows']}")
print(f"Total Columns: {p['total_columns']}")
print(f"{'COLUMN NAME':<22} | {'TYPE':<8} | {'NULL COUNT':<10} | {'NULL %':<8} | {'UNIQUE'}")
print("-" * 65)
for col, info in p["columns"].items():
    print(f"{col:<22} | {info['dtype']:<8} | {info['null_count']:<10} | {info['null_pct']:<7}% | {info['unique_count']}")
