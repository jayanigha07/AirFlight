package com.airflight.mr;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import org.apache.hadoop.io.Writable;

/**
 * Custom Hadoop Writable for aggregating flight operational metrics per airline carrier.
 */
public class AirlineStatsWritable implements Writable {
    private long totalFlights;
    private long delayedFlights;
    private long cancelledFlights;
    private double totalArrivalDelay;
    private long validArrivalDelays;
    private double totalDepartureDelay;
    private long validDepartureDelays;

    public AirlineStatsWritable() {
        this(0, 0, 0, 0.0, 0, 0.0, 0);
    }

    public AirlineStatsWritable(long totalFlights, long delayedFlights, long cancelledFlights,
                                double totalArrivalDelay, long validArrivalDelays,
                                double totalDepartureDelay, long validDepartureDelays) {
        this.totalFlights = totalFlights;
        this.delayedFlights = delayedFlights;
        this.cancelledFlights = cancelledFlights;
        this.totalArrivalDelay = totalArrivalDelay;
        this.validArrivalDelays = validArrivalDelays;
        this.totalDepartureDelay = totalDepartureDelay;
        this.validDepartureDelays = validDepartureDelays;
    }

    @Override
    public void write(DataOutput out) throws IOException {
        out.writeLong(totalFlights);
        out.writeLong(delayedFlights);
        out.writeLong(cancelledFlights);
        out.writeDouble(totalArrivalDelay);
        out.writeLong(validArrivalDelays);
        out.writeDouble(totalDepartureDelay);
        out.writeLong(validDepartureDelays);
    }

    @Override
    public void readFields(DataInput in) throws IOException {
        totalFlights = in.readLong();
        delayedFlights = in.readLong();
        cancelledFlights = in.readLong();
        totalArrivalDelay = in.readDouble();
        validArrivalDelays = in.readLong();
        totalDepartureDelay = in.readDouble();
        validDepartureDelays = in.readLong();
    }

    public long getTotalFlights() { return totalFlights; }
    public long getDelayedFlights() { return delayedFlights; }
    public long getCancelledFlights() { return cancelledFlights; }
    public double getTotalArrivalDelay() { return totalArrivalDelay; }
    public long getValidArrivalDelays() { return validArrivalDelays; }
    public double getTotalDepartureDelay() { return totalDepartureDelay; }
    public long getValidDepartureDelays() { return validDepartureDelays; }

    public void add(AirlineStatsWritable other) {
        this.totalFlights += other.totalFlights;
        this.delayedFlights += other.delayedFlights;
        this.cancelledFlights += other.cancelledFlights;
        this.totalArrivalDelay += other.totalArrivalDelay;
        this.validArrivalDelays += other.validArrivalDelays;
        this.totalDepartureDelay += other.totalDepartureDelay;
        this.validDepartureDelays += other.validDepartureDelays;
    }
}
