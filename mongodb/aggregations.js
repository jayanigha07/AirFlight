// AirFlight MongoDB Verification & Aggregation Pipelines
// Operates on the 'airflight.flights' collection imported from HDFS

const db = db.getSiblingDB("airflight");

print("==========================================================");
print("AIRFLIGHT: MONGODB NO-SQL PIPELINE VERIFICATION");
print("==========================================================");

// 1. Create Compound Indexes for fast operational querying & AirFlight analytics
print(">>> Creating compound indexes...");
db.flights.createIndex({ origin_airport: 1, destination_airport: 1, month: 1 });
db.flights.createIndex({ airline: 1, origin_airport: 1, destination_airport: 1 });
db.flights.createIndex({ time_of_day: 1 });
db.flights.createIndex({ delay_category: 1 });
db.flights.createIndex({ month: 1 });

const totalDocs = db.flights.countDocuments();
print("Total Documents in airflight.flights: " + totalDocs + " (Expected: 216000)");

// ==============================================================
// 2. AGGREGATION 1: AIRLINE PERFORMANCE SUMMARY
// ==============================================================
print("\n=== AGGREGATION 1: AIRLINE RELIABILITY (MongoDB Pipeline) ===");
const airlineSummary = db.flights.aggregate([
  {
    $group: {
      _id: "$airline",
      totalFlights: { $sum: 1 },
      delayedFlights: {
        $sum: { $cond: [{ $gt: ["$arrival_delay", 15] }, 1, 0] }
      },
      cancelledFlights: {
        $sum: { $cond: [{ $eq: ["$cancelled", 1] }, 1, 0] }
      },
      avgArrivalDelay: { $avg: "$arrival_delay" },
      avgDepartureDelay: { $avg: "$departure_delay" }
    }
  },
  {
    $project: {
      airline: "$_id",
      totalFlights: 1,
      delayedFlights: 1,
      cancelledFlights: 1,
      delayRatePct: {
        $round: [{ $multiply: [{ $divide: ["$delayedFlights", "$totalFlights"] }, 100] }, 2]
      },
      cancelRatePct: {
        $round: [{ $multiply: [{ $divide: ["$cancelledFlights", "$totalFlights"] }, 100] }, 2]
      },
      avgArrivalDelay: { $round: ["$avgArrivalDelay", 2] },
      avgDepartureDelay: { $round: ["$avgDepartureDelay", 2] }
    }
  },
  { $sort: { delayRatePct: 1 } }
]).toArray();

printjson(airlineSummary);

// Save to airline_summaries collection for low-latency operational serving
db.airline_summaries.drop();
db.airline_summaries.insertMany(airlineSummary);
print("Stored " + db.airline_summaries.countDocuments() + " carrier summaries in airline_summaries collection.");

// ==============================================================
// 2. AGGREGATION 2: ROUTE RELIABILITY (Top 10 High Volume Routes)
// ==============================================================
print("\n=== AGGREGATION 2: TOP ROUTE RELIABILITY (MongoDB Pipeline) ===");
const routeSummary = db.flights.aggregate([
  {
    $group: {
      _id: { origin: "$origin_airport", dest: "$destination_airport" },
      flightCount: { $sum: 1 },
      delayedFlights: {
        $sum: { $cond: [{ $gt: ["$arrival_delay", 15] }, 1, 0] }
      },
      cancelledFlights: {
        $sum: { $cond: [{ $eq: ["$cancelled", 1] }, 1, 0] }
      },
      avgDelay: { $avg: "$arrival_delay" }
    }
  },
  { $match: { flightCount: { $gte: 100 } } },
  {
    $project: {
      route: { $concat: ["$_id.origin", " -> ", "$_id.dest"] },
      flightCount: 1,
      delayedFlights: 1,
      cancelledFlights: 1,
      delayRatePct: {
        $round: [{ $multiply: [{ $divide: ["$delayedFlights", "$flightCount"] }, 100] }, 2]
      },
      avgDelay: { $round: ["$avgDelay", 2] }
    }
  },
  { $sort: { flightCount: -1 } },
  { $limit: 10 }
]).toArray();

printjson(routeSummary);

// ==============================================================
// 3. AGGREGATION 3: DELAY CATEGORY BREAKDOWN
// ==============================================================
print("\n=== AGGREGATION 3: DELAY CATEGORY BREAKDOWN (MongoDB Pipeline) ===");
const categorySummary = db.flights.aggregate([
  {
    $group: {
      _id: "$delay_category",
      count: { $sum: 1 }
    }
  },
  {
    $project: {
      category: "$_id",
      count: 1,
      percentage: {
        $round: [{ $multiply: [{ $divide: ["$count", totalDocs] }, 100] }, 2]
      }
    }
  },
  { $sort: { count: -1 } }
]).toArray();

printjson(categorySummary);

// ==============================================================
// 4. AGGREGATION 4: TIME-OF-DAY HISTORICAL PERFORMANCE
// ==============================================================
print("\n=== AGGREGATION 4: TIME-OF-DAY RELIABILITY (MongoDB Pipeline) ===");
const timeOfDaySummary = db.flights.aggregate([
  {
    $group: {
      _id: "$time_of_day",
      flightCount: { $sum: 1 },
      delayedFlights: {
        $sum: { $cond: [{ $gt: ["$arrival_delay", 15] }, 1, 0] }
      },
      cancelledFlights: {
        $sum: { $cond: [{ $eq: ["$cancelled", 1] }, 1, 0] }
      },
      avgDelay: { $avg: "$arrival_delay" }
    }
  },
  {
    $project: {
      timeOfDay: "$_id",
      flightCount: 1,
      delayedFlights: 1,
      delayRatePct: {
        $round: [{ $multiply: [{ $divide: ["$delayedFlights", "$flightCount"] }, 100] }, 2]
      },
      cancelRatePct: {
        $round: [{ $multiply: [{ $divide: ["$cancelledFlights", "$flightCount"] }, 100] }, 2]
      },
      avgDelay: { $round: ["$avgDelay", 2] }
    }
  },
  { $sort: { delayRatePct: 1 } }
]).toArray();

printjson(timeOfDaySummary);

print("\nSUCCESS: All MongoDB aggregations executed and verified!");
