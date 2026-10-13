package com.airflight.mr;

import java.io.IOException;
import java.util.Locale;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.mapreduce.Reducer;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.input.TextInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;
import org.apache.hadoop.mapreduce.lib.output.TextOutputFormat;

/**
 * AirFlight Genuine Java MapReduce Job on YARN
 *
 * Calculates total flights, delayed flights (arrival delay > 15 min),
 * average arrival delay, average departure delay, and cancellation statistics
 * per airline carrier from Sqoop-imported HDFS data.
 */
public class AirlineDelayStats {

    public static class FlightDelayMapper
            extends Mapper<LongWritable, Text, Text, AirlineStatsWritable> {

        private final Text airlineKey = new Text();
        private final AirlineStatsWritable statsValue = new AirlineStatsWritable();

        @Override
        public void map(LongWritable key, Text value, Context context)
                throws IOException, InterruptedException {
            String line = value.toString().trim();
            if (line.isEmpty()) {
                return;
            }

            // Sqoop CSV tokens (32 fields in alphabetical schema order):
            // 2: airline, 4: arrival_delay, 7: cancelled, 10: departure_delay
            String[] tokens = line.split(",", -1);
            if (tokens.length < 32) {
                return;
            }

            String airline = tokens[2].trim();
            if (airline.isEmpty() || "\\N".equals(airline)) {
                return;
            }

            int cancelled = 0;
            String cancStr = tokens[7].trim();
            if ("1".equals(cancStr)) {
                cancelled = 1;
            }

            double arrDelay = 0.0;
            long validArrDelay = 0;
            long delayed = 0;
            String arrDelayStr = tokens[4].trim();
            if (!arrDelayStr.isEmpty() && !"\\N".equals(arrDelayStr)) {
                try {
                    arrDelay = Double.parseDouble(arrDelayStr);
                    validArrDelay = 1;
                    if (arrDelay > 15.0) {
                        delayed = 1;
                    }
                } catch (NumberFormatException ignored) {}
            }

            double depDelay = 0.0;
            long validDepDelay = 0;
            String depDelayStr = tokens[10].trim();
            if (!depDelayStr.isEmpty() && !"\\N".equals(depDelayStr)) {
                try {
                    depDelay = Double.parseDouble(depDelayStr);
                    validDepDelay = 1;
                } catch (NumberFormatException ignored) {}
            }

            airlineKey.set(airline);
            AirlineStatsWritable stats = new AirlineStatsWritable(
                    1, delayed, cancelled,
                    arrDelay, validArrDelay,
                    depDelay, validDepDelay
            );

            context.write(airlineKey, stats);
        }
    }

    public static class FlightDelayCombiner
            extends Reducer<Text, AirlineStatsWritable, Text, AirlineStatsWritable> {

        @Override
        public void reduce(Text key, Iterable<AirlineStatsWritable> values, Context context)
                throws IOException, InterruptedException {
            long totalFlights = 0;
            long delayedFlights = 0;
            long cancelledFlights = 0;
            double totalArrDelay = 0.0;
            long validArrDelays = 0;
            double totalDepDelay = 0.0;
            long validDepDelays = 0;

            for (AirlineStatsWritable val : values) {
                totalFlights += val.getTotalFlights();
                delayedFlights += val.getDelayedFlights();
                cancelledFlights += val.getCancelledFlights();
                totalArrDelay += val.getTotalArrivalDelay();
                validArrDelays += val.getValidArrivalDelays();
                totalDepDelay += val.getTotalDepartureDelay();
                validDepDelays += val.getValidDepartureDelays();
            }

            context.write(key, new AirlineStatsWritable(
                    totalFlights, delayedFlights, cancelledFlights,
                    totalArrDelay, validArrDelays,
                    totalDepDelay, validDepDelays
            ));
        }
    }

    public static class FlightDelayReducer
            extends Reducer<Text, AirlineStatsWritable, Text, Text> {

        private final Text resultText = new Text();

        @Override
        public void reduce(Text key, Iterable<AirlineStatsWritable> values, Context context)
                throws IOException, InterruptedException {
            long totalFlights = 0;
            long delayedFlights = 0;
            long cancelledFlights = 0;
            double totalArrDelay = 0.0;
            long validArrDelays = 0;
            double totalDepDelay = 0.0;
            long validDepDelays = 0;

            for (AirlineStatsWritable val : values) {
                totalFlights += val.getTotalFlights();
                delayedFlights += val.getDelayedFlights();
                cancelledFlights += val.getCancelledFlights();
                totalArrDelay += val.getTotalArrivalDelay();
                validArrDelays += val.getValidArrivalDelays();
                totalDepDelay += val.getTotalDepartureDelay();
                validDepDelays += val.getValidDepartureDelays();
            }

            double avgArrDelay = (validArrDelays > 0) ? (totalArrDelay / validArrDelays) : 0.0;
            double avgDepDelay = (validDepDelays > 0) ? (totalDepDelay / validDepDelays) : 0.0;
            double delayRate = (totalFlights > 0) ? ((delayedFlights * 100.0) / totalFlights) : 0.0;
            double cancelRate = (totalFlights > 0) ? ((cancelledFlights * 100.0) / totalFlights) : 0.0;

            String formatted = String.format(Locale.US,
                    "Total=%-6d | Delayed=%-5d (%5.2f%%) | AvgArrDelay=%6.2f min | AvgDepDelay=%6.2f min | Cancelled=%-5d (%5.2f%%)",
                    totalFlights, delayedFlights, delayRate, avgArrDelay, avgDepDelay, cancelledFlights, cancelRate
            );

            resultText.set(formatted);
            context.write(key, resultText);
        }
    }

    public static void main(String[] args) throws Exception {
        Configuration conf = new Configuration();
        Job job = Job.getInstance(conf, "AirFlight Airline Delay Statistics");

        job.setJarByClass(AirlineDelayStats.class);
        job.setMapperClass(FlightDelayMapper.class);
        job.setCombinerClass(FlightDelayCombiner.class);
        job.setReducerClass(FlightDelayReducer.class);

        job.setMapOutputKeyClass(Text.class);
        job.setMapOutputValueClass(AirlineStatsWritable.class);

        job.setOutputKeyClass(Text.class);
        job.setOutputValueClass(Text.class);

        job.setInputFormatClass(TextInputFormat.class);
        job.setOutputFormatClass(TextOutputFormat.class);

        Path inputPath = new Path(args.length > 0 ? args[0] : "/airflight/raw/flights");
        Path outputPath = new Path(args.length > 1 ? args[1] : "/airflight/mapreduce_output/airline_delays");

        // Clean output directory if already present
        FileSystem fs = FileSystem.get(conf);
        if (fs.exists(outputPath)) {
            fs.delete(outputPath, true);
        }

        FileInputFormat.addInputPath(job, inputPath);
        FileOutputFormat.setOutputPath(job, outputPath);

        boolean success = job.waitForCompletion(true);
        System.exit(success ? 0 : 1);
    }
}
