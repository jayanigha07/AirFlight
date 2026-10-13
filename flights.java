// ORM class for table 'flights'
// WARNING: This class is AUTO-GENERATED. Modify at your own risk.
//
// Debug information:
// Generated date: Tue Oct 13 19:52:18 IST 2026
// For connector: org.apache.sqoop.manager.MySQLManager
import org.apache.hadoop.io.BytesWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.io.Writable;
import org.apache.hadoop.mapred.lib.db.DBWritable;
import com.cloudera.sqoop.lib.JdbcWritableBridge;
import com.cloudera.sqoop.lib.DelimiterSet;
import com.cloudera.sqoop.lib.FieldFormatter;
import com.cloudera.sqoop.lib.RecordParser;
import com.cloudera.sqoop.lib.BooleanParser;
import com.cloudera.sqoop.lib.BlobRef;
import com.cloudera.sqoop.lib.ClobRef;
import com.cloudera.sqoop.lib.LargeObjectLoader;
import com.cloudera.sqoop.lib.SqoopRecord;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.sql.Date;
import java.sql.Time;
import java.sql.Timestamp;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.HashMap;

public class flights extends SqoopRecord  implements DBWritable, Writable {
  private final int PROTOCOL_VERSION = 3;
  public int getClassFormatVersion() { return PROTOCOL_VERSION; }
  public static interface FieldSetterCommand {    void setField(Object value);  }  protected ResultSet __cur_result_set;
  private Map<String, FieldSetterCommand> setters = new HashMap<String, FieldSetterCommand>();
  private void init0() {
    setters.put("air_system_delay", new FieldSetterCommand() {
      @Override
      public void setField(Object value) {
        flights.this.air_system_delay = (Double)value;
      }
    });
    setters.put("air_time", new FieldSetterCommand() {
      @Override
      public void setField(Object value) {
        flights.this.air_time = (Double)value;
      }
    });
    setters.put("airline", new FieldSetterCommand() {
      @Override
      public void setField(Object value) {
        flights.this.airline = (String)value;
      }
    });
    setters.put("airline_delay", new FieldSetterCommand() {
      @Override
      public void setField(Object value) {
        flights.this.airline_delay = (Double)value;
      }
    });
    setters.put("arrival_delay", new FieldSetterCommand() {
      @Override
      public void setField(Object value) {
        flights.this.arrival_delay = (Double)value;
      }
    });
    setters.put("arrival_time", new FieldSetterCommand() {
      @Override
      public void setField(Object value) {
        flights.this.arrival_time = (Integer)value;
      }
    });
    setters.put("cancellation_reason", new FieldSetterCommand() {
      @Override
      public void setField(Object value) {
        flights.this.cancellation_reason = (String)value;
      }
    });
    setters.put("cancelled", new FieldSetterCommand() {
      @Override
      public void setField(Object value) {
        flights.this.cancelled = (Integer)value;
      }
    });
    setters.put("day", new FieldSetterCommand() {
      @Override
      public void setField(Object value) {
        flights.this.day = (Integer)value;
      }
    });
    setters.put("day_of_week", new FieldSetterCommand() {
      @Override
      public void setField(Object value) {
        flights.this.day_of_week = (Integer)value;
      }
    });
    setters.put("departure_delay", new FieldSetterCommand() {
      @Override
      public void setField(Object value) {
        flights.this.departure_delay = (Double)value;
      }
    });
    setters.put("departure_time", new FieldSetterCommand() {
      @Override
      public void setField(Object value) {
        flights.this.departure_time = (Integer)value;
      }
    });
    setters.put("destination_airport", new FieldSetterCommand() {
      @Override
      public void setField(Object value) {
        flights.this.destination_airport = (String)value;
      }
    });
    setters.put("distance", new FieldSetterCommand() {
      @Override
      public void setField(Object value) {
        flights.this.distance = (Integer)value;
      }
    });
    setters.put("diverted", new FieldSetterCommand() {
      @Override
      public void setField(Object value) {
        flights.this.diverted = (Integer)value;
      }
    });
    setters.put("elapsed_time", new FieldSetterCommand() {
      @Override
      public void setField(Object value) {
        flights.this.elapsed_time = (Double)value;
      }
    });
    setters.put("flight_number", new FieldSetterCommand() {
      @Override
      public void setField(Object value) {
        flights.this.flight_number = (Integer)value;
      }
    });
    setters.put("id", new FieldSetterCommand() {
      @Override
      public void setField(Object value) {
        flights.this.id = (Integer)value;
      }
    });
    setters.put("late_aircraft_delay", new FieldSetterCommand() {
      @Override
      public void setField(Object value) {
        flights.this.late_aircraft_delay = (Double)value;
      }
    });
    setters.put("month", new FieldSetterCommand() {
      @Override
      public void setField(Object value) {
        flights.this.month = (Integer)value;
      }
    });
    setters.put("origin_airport", new FieldSetterCommand() {
      @Override
      public void setField(Object value) {
        flights.this.origin_airport = (String)value;
      }
    });
    setters.put("scheduled_arrival", new FieldSetterCommand() {
      @Override
      public void setField(Object value) {
        flights.this.scheduled_arrival = (Integer)value;
      }
    });
    setters.put("scheduled_departure", new FieldSetterCommand() {
      @Override
      public void setField(Object value) {
        flights.this.scheduled_departure = (Integer)value;
      }
    });
    setters.put("scheduled_time", new FieldSetterCommand() {
      @Override
      public void setField(Object value) {
        flights.this.scheduled_time = (Double)value;
      }
    });
    setters.put("security_delay", new FieldSetterCommand() {
      @Override
      public void setField(Object value) {
        flights.this.security_delay = (Double)value;
      }
    });
    setters.put("tail_number", new FieldSetterCommand() {
      @Override
      public void setField(Object value) {
        flights.this.tail_number = (String)value;
      }
    });
    setters.put("taxi_in", new FieldSetterCommand() {
      @Override
      public void setField(Object value) {
        flights.this.taxi_in = (Double)value;
      }
    });
    setters.put("taxi_out", new FieldSetterCommand() {
      @Override
      public void setField(Object value) {
        flights.this.taxi_out = (Double)value;
      }
    });
    setters.put("weather_delay", new FieldSetterCommand() {
      @Override
      public void setField(Object value) {
        flights.this.weather_delay = (Double)value;
      }
    });
    setters.put("wheels_off", new FieldSetterCommand() {
      @Override
      public void setField(Object value) {
        flights.this.wheels_off = (Integer)value;
      }
    });
    setters.put("wheels_on", new FieldSetterCommand() {
      @Override
      public void setField(Object value) {
        flights.this.wheels_on = (Integer)value;
      }
    });
    setters.put("year", new FieldSetterCommand() {
      @Override
      public void setField(Object value) {
        flights.this.year = (Integer)value;
      }
    });
  }
  public flights() {
    init0();
  }
  private Double air_system_delay;
  public Double get_air_system_delay() {
    return air_system_delay;
  }
  public void set_air_system_delay(Double air_system_delay) {
    this.air_system_delay = air_system_delay;
  }
  public flights with_air_system_delay(Double air_system_delay) {
    this.air_system_delay = air_system_delay;
    return this;
  }
  private Double air_time;
  public Double get_air_time() {
    return air_time;
  }
  public void set_air_time(Double air_time) {
    this.air_time = air_time;
  }
  public flights with_air_time(Double air_time) {
    this.air_time = air_time;
    return this;
  }
  private String airline;
  public String get_airline() {
    return airline;
  }
  public void set_airline(String airline) {
    this.airline = airline;
  }
  public flights with_airline(String airline) {
    this.airline = airline;
    return this;
  }
  private Double airline_delay;
  public Double get_airline_delay() {
    return airline_delay;
  }
  public void set_airline_delay(Double airline_delay) {
    this.airline_delay = airline_delay;
  }
  public flights with_airline_delay(Double airline_delay) {
    this.airline_delay = airline_delay;
    return this;
  }
  private Double arrival_delay;
  public Double get_arrival_delay() {
    return arrival_delay;
  }
  public void set_arrival_delay(Double arrival_delay) {
    this.arrival_delay = arrival_delay;
  }
  public flights with_arrival_delay(Double arrival_delay) {
    this.arrival_delay = arrival_delay;
    return this;
  }
  private Integer arrival_time;
  public Integer get_arrival_time() {
    return arrival_time;
  }
  public void set_arrival_time(Integer arrival_time) {
    this.arrival_time = arrival_time;
  }
  public flights with_arrival_time(Integer arrival_time) {
    this.arrival_time = arrival_time;
    return this;
  }
  private String cancellation_reason;
  public String get_cancellation_reason() {
    return cancellation_reason;
  }
  public void set_cancellation_reason(String cancellation_reason) {
    this.cancellation_reason = cancellation_reason;
  }
  public flights with_cancellation_reason(String cancellation_reason) {
    this.cancellation_reason = cancellation_reason;
    return this;
  }
  private Integer cancelled;
  public Integer get_cancelled() {
    return cancelled;
  }
  public void set_cancelled(Integer cancelled) {
    this.cancelled = cancelled;
  }
  public flights with_cancelled(Integer cancelled) {
    this.cancelled = cancelled;
    return this;
  }
  private Integer day;
  public Integer get_day() {
    return day;
  }
  public void set_day(Integer day) {
    this.day = day;
  }
  public flights with_day(Integer day) {
    this.day = day;
    return this;
  }
  private Integer day_of_week;
  public Integer get_day_of_week() {
    return day_of_week;
  }
  public void set_day_of_week(Integer day_of_week) {
    this.day_of_week = day_of_week;
  }
  public flights with_day_of_week(Integer day_of_week) {
    this.day_of_week = day_of_week;
    return this;
  }
  private Double departure_delay;
  public Double get_departure_delay() {
    return departure_delay;
  }
  public void set_departure_delay(Double departure_delay) {
    this.departure_delay = departure_delay;
  }
  public flights with_departure_delay(Double departure_delay) {
    this.departure_delay = departure_delay;
    return this;
  }
  private Integer departure_time;
  public Integer get_departure_time() {
    return departure_time;
  }
  public void set_departure_time(Integer departure_time) {
    this.departure_time = departure_time;
  }
  public flights with_departure_time(Integer departure_time) {
    this.departure_time = departure_time;
    return this;
  }
  private String destination_airport;
  public String get_destination_airport() {
    return destination_airport;
  }
  public void set_destination_airport(String destination_airport) {
    this.destination_airport = destination_airport;
  }
  public flights with_destination_airport(String destination_airport) {
    this.destination_airport = destination_airport;
    return this;
  }
  private Integer distance;
  public Integer get_distance() {
    return distance;
  }
  public void set_distance(Integer distance) {
    this.distance = distance;
  }
  public flights with_distance(Integer distance) {
    this.distance = distance;
    return this;
  }
  private Integer diverted;
  public Integer get_diverted() {
    return diverted;
  }
  public void set_diverted(Integer diverted) {
    this.diverted = diverted;
  }
  public flights with_diverted(Integer diverted) {
    this.diverted = diverted;
    return this;
  }
  private Double elapsed_time;
  public Double get_elapsed_time() {
    return elapsed_time;
  }
  public void set_elapsed_time(Double elapsed_time) {
    this.elapsed_time = elapsed_time;
  }
  public flights with_elapsed_time(Double elapsed_time) {
    this.elapsed_time = elapsed_time;
    return this;
  }
  private Integer flight_number;
  public Integer get_flight_number() {
    return flight_number;
  }
  public void set_flight_number(Integer flight_number) {
    this.flight_number = flight_number;
  }
  public flights with_flight_number(Integer flight_number) {
    this.flight_number = flight_number;
    return this;
  }
  private Integer id;
  public Integer get_id() {
    return id;
  }
  public void set_id(Integer id) {
    this.id = id;
  }
  public flights with_id(Integer id) {
    this.id = id;
    return this;
  }
  private Double late_aircraft_delay;
  public Double get_late_aircraft_delay() {
    return late_aircraft_delay;
  }
  public void set_late_aircraft_delay(Double late_aircraft_delay) {
    this.late_aircraft_delay = late_aircraft_delay;
  }
  public flights with_late_aircraft_delay(Double late_aircraft_delay) {
    this.late_aircraft_delay = late_aircraft_delay;
    return this;
  }
  private Integer month;
  public Integer get_month() {
    return month;
  }
  public void set_month(Integer month) {
    this.month = month;
  }
  public flights with_month(Integer month) {
    this.month = month;
    return this;
  }
  private String origin_airport;
  public String get_origin_airport() {
    return origin_airport;
  }
  public void set_origin_airport(String origin_airport) {
    this.origin_airport = origin_airport;
  }
  public flights with_origin_airport(String origin_airport) {
    this.origin_airport = origin_airport;
    return this;
  }
  private Integer scheduled_arrival;
  public Integer get_scheduled_arrival() {
    return scheduled_arrival;
  }
  public void set_scheduled_arrival(Integer scheduled_arrival) {
    this.scheduled_arrival = scheduled_arrival;
  }
  public flights with_scheduled_arrival(Integer scheduled_arrival) {
    this.scheduled_arrival = scheduled_arrival;
    return this;
  }
  private Integer scheduled_departure;
  public Integer get_scheduled_departure() {
    return scheduled_departure;
  }
  public void set_scheduled_departure(Integer scheduled_departure) {
    this.scheduled_departure = scheduled_departure;
  }
  public flights with_scheduled_departure(Integer scheduled_departure) {
    this.scheduled_departure = scheduled_departure;
    return this;
  }
  private Double scheduled_time;
  public Double get_scheduled_time() {
    return scheduled_time;
  }
  public void set_scheduled_time(Double scheduled_time) {
    this.scheduled_time = scheduled_time;
  }
  public flights with_scheduled_time(Double scheduled_time) {
    this.scheduled_time = scheduled_time;
    return this;
  }
  private Double security_delay;
  public Double get_security_delay() {
    return security_delay;
  }
  public void set_security_delay(Double security_delay) {
    this.security_delay = security_delay;
  }
  public flights with_security_delay(Double security_delay) {
    this.security_delay = security_delay;
    return this;
  }
  private String tail_number;
  public String get_tail_number() {
    return tail_number;
  }
  public void set_tail_number(String tail_number) {
    this.tail_number = tail_number;
  }
  public flights with_tail_number(String tail_number) {
    this.tail_number = tail_number;
    return this;
  }
  private Double taxi_in;
  public Double get_taxi_in() {
    return taxi_in;
  }
  public void set_taxi_in(Double taxi_in) {
    this.taxi_in = taxi_in;
  }
  public flights with_taxi_in(Double taxi_in) {
    this.taxi_in = taxi_in;
    return this;
  }
  private Double taxi_out;
  public Double get_taxi_out() {
    return taxi_out;
  }
  public void set_taxi_out(Double taxi_out) {
    this.taxi_out = taxi_out;
  }
  public flights with_taxi_out(Double taxi_out) {
    this.taxi_out = taxi_out;
    return this;
  }
  private Double weather_delay;
  public Double get_weather_delay() {
    return weather_delay;
  }
  public void set_weather_delay(Double weather_delay) {
    this.weather_delay = weather_delay;
  }
  public flights with_weather_delay(Double weather_delay) {
    this.weather_delay = weather_delay;
    return this;
  }
  private Integer wheels_off;
  public Integer get_wheels_off() {
    return wheels_off;
  }
  public void set_wheels_off(Integer wheels_off) {
    this.wheels_off = wheels_off;
  }
  public flights with_wheels_off(Integer wheels_off) {
    this.wheels_off = wheels_off;
    return this;
  }
  private Integer wheels_on;
  public Integer get_wheels_on() {
    return wheels_on;
  }
  public void set_wheels_on(Integer wheels_on) {
    this.wheels_on = wheels_on;
  }
  public flights with_wheels_on(Integer wheels_on) {
    this.wheels_on = wheels_on;
    return this;
  }
  private Integer year;
  public Integer get_year() {
    return year;
  }
  public void set_year(Integer year) {
    this.year = year;
  }
  public flights with_year(Integer year) {
    this.year = year;
    return this;
  }
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (!(o instanceof flights)) {
      return false;
    }
    flights that = (flights) o;
    boolean equal = true;
    equal = equal && (this.air_system_delay == null ? that.air_system_delay == null : this.air_system_delay.equals(that.air_system_delay));
    equal = equal && (this.air_time == null ? that.air_time == null : this.air_time.equals(that.air_time));
    equal = equal && (this.airline == null ? that.airline == null : this.airline.equals(that.airline));
    equal = equal && (this.airline_delay == null ? that.airline_delay == null : this.airline_delay.equals(that.airline_delay));
    equal = equal && (this.arrival_delay == null ? that.arrival_delay == null : this.arrival_delay.equals(that.arrival_delay));
    equal = equal && (this.arrival_time == null ? that.arrival_time == null : this.arrival_time.equals(that.arrival_time));
    equal = equal && (this.cancellation_reason == null ? that.cancellation_reason == null : this.cancellation_reason.equals(that.cancellation_reason));
    equal = equal && (this.cancelled == null ? that.cancelled == null : this.cancelled.equals(that.cancelled));
    equal = equal && (this.day == null ? that.day == null : this.day.equals(that.day));
    equal = equal && (this.day_of_week == null ? that.day_of_week == null : this.day_of_week.equals(that.day_of_week));
    equal = equal && (this.departure_delay == null ? that.departure_delay == null : this.departure_delay.equals(that.departure_delay));
    equal = equal && (this.departure_time == null ? that.departure_time == null : this.departure_time.equals(that.departure_time));
    equal = equal && (this.destination_airport == null ? that.destination_airport == null : this.destination_airport.equals(that.destination_airport));
    equal = equal && (this.distance == null ? that.distance == null : this.distance.equals(that.distance));
    equal = equal && (this.diverted == null ? that.diverted == null : this.diverted.equals(that.diverted));
    equal = equal && (this.elapsed_time == null ? that.elapsed_time == null : this.elapsed_time.equals(that.elapsed_time));
    equal = equal && (this.flight_number == null ? that.flight_number == null : this.flight_number.equals(that.flight_number));
    equal = equal && (this.id == null ? that.id == null : this.id.equals(that.id));
    equal = equal && (this.late_aircraft_delay == null ? that.late_aircraft_delay == null : this.late_aircraft_delay.equals(that.late_aircraft_delay));
    equal = equal && (this.month == null ? that.month == null : this.month.equals(that.month));
    equal = equal && (this.origin_airport == null ? that.origin_airport == null : this.origin_airport.equals(that.origin_airport));
    equal = equal && (this.scheduled_arrival == null ? that.scheduled_arrival == null : this.scheduled_arrival.equals(that.scheduled_arrival));
    equal = equal && (this.scheduled_departure == null ? that.scheduled_departure == null : this.scheduled_departure.equals(that.scheduled_departure));
    equal = equal && (this.scheduled_time == null ? that.scheduled_time == null : this.scheduled_time.equals(that.scheduled_time));
    equal = equal && (this.security_delay == null ? that.security_delay == null : this.security_delay.equals(that.security_delay));
    equal = equal && (this.tail_number == null ? that.tail_number == null : this.tail_number.equals(that.tail_number));
    equal = equal && (this.taxi_in == null ? that.taxi_in == null : this.taxi_in.equals(that.taxi_in));
    equal = equal && (this.taxi_out == null ? that.taxi_out == null : this.taxi_out.equals(that.taxi_out));
    equal = equal && (this.weather_delay == null ? that.weather_delay == null : this.weather_delay.equals(that.weather_delay));
    equal = equal && (this.wheels_off == null ? that.wheels_off == null : this.wheels_off.equals(that.wheels_off));
    equal = equal && (this.wheels_on == null ? that.wheels_on == null : this.wheels_on.equals(that.wheels_on));
    equal = equal && (this.year == null ? that.year == null : this.year.equals(that.year));
    return equal;
  }
  public boolean equals0(Object o) {
    if (this == o) {
      return true;
    }
    if (!(o instanceof flights)) {
      return false;
    }
    flights that = (flights) o;
    boolean equal = true;
    equal = equal && (this.air_system_delay == null ? that.air_system_delay == null : this.air_system_delay.equals(that.air_system_delay));
    equal = equal && (this.air_time == null ? that.air_time == null : this.air_time.equals(that.air_time));
    equal = equal && (this.airline == null ? that.airline == null : this.airline.equals(that.airline));
    equal = equal && (this.airline_delay == null ? that.airline_delay == null : this.airline_delay.equals(that.airline_delay));
    equal = equal && (this.arrival_delay == null ? that.arrival_delay == null : this.arrival_delay.equals(that.arrival_delay));
    equal = equal && (this.arrival_time == null ? that.arrival_time == null : this.arrival_time.equals(that.arrival_time));
    equal = equal && (this.cancellation_reason == null ? that.cancellation_reason == null : this.cancellation_reason.equals(that.cancellation_reason));
    equal = equal && (this.cancelled == null ? that.cancelled == null : this.cancelled.equals(that.cancelled));
    equal = equal && (this.day == null ? that.day == null : this.day.equals(that.day));
    equal = equal && (this.day_of_week == null ? that.day_of_week == null : this.day_of_week.equals(that.day_of_week));
    equal = equal && (this.departure_delay == null ? that.departure_delay == null : this.departure_delay.equals(that.departure_delay));
    equal = equal && (this.departure_time == null ? that.departure_time == null : this.departure_time.equals(that.departure_time));
    equal = equal && (this.destination_airport == null ? that.destination_airport == null : this.destination_airport.equals(that.destination_airport));
    equal = equal && (this.distance == null ? that.distance == null : this.distance.equals(that.distance));
    equal = equal && (this.diverted == null ? that.diverted == null : this.diverted.equals(that.diverted));
    equal = equal && (this.elapsed_time == null ? that.elapsed_time == null : this.elapsed_time.equals(that.elapsed_time));
    equal = equal && (this.flight_number == null ? that.flight_number == null : this.flight_number.equals(that.flight_number));
    equal = equal && (this.id == null ? that.id == null : this.id.equals(that.id));
    equal = equal && (this.late_aircraft_delay == null ? that.late_aircraft_delay == null : this.late_aircraft_delay.equals(that.late_aircraft_delay));
    equal = equal && (this.month == null ? that.month == null : this.month.equals(that.month));
    equal = equal && (this.origin_airport == null ? that.origin_airport == null : this.origin_airport.equals(that.origin_airport));
    equal = equal && (this.scheduled_arrival == null ? that.scheduled_arrival == null : this.scheduled_arrival.equals(that.scheduled_arrival));
    equal = equal && (this.scheduled_departure == null ? that.scheduled_departure == null : this.scheduled_departure.equals(that.scheduled_departure));
    equal = equal && (this.scheduled_time == null ? that.scheduled_time == null : this.scheduled_time.equals(that.scheduled_time));
    equal = equal && (this.security_delay == null ? that.security_delay == null : this.security_delay.equals(that.security_delay));
    equal = equal && (this.tail_number == null ? that.tail_number == null : this.tail_number.equals(that.tail_number));
    equal = equal && (this.taxi_in == null ? that.taxi_in == null : this.taxi_in.equals(that.taxi_in));
    equal = equal && (this.taxi_out == null ? that.taxi_out == null : this.taxi_out.equals(that.taxi_out));
    equal = equal && (this.weather_delay == null ? that.weather_delay == null : this.weather_delay.equals(that.weather_delay));
    equal = equal && (this.wheels_off == null ? that.wheels_off == null : this.wheels_off.equals(that.wheels_off));
    equal = equal && (this.wheels_on == null ? that.wheels_on == null : this.wheels_on.equals(that.wheels_on));
    equal = equal && (this.year == null ? that.year == null : this.year.equals(that.year));
    return equal;
  }
  public void readFields(ResultSet __dbResults) throws SQLException {
    this.__cur_result_set = __dbResults;
    this.air_system_delay = JdbcWritableBridge.readDouble(1, __dbResults);
    this.air_time = JdbcWritableBridge.readDouble(2, __dbResults);
    this.airline = JdbcWritableBridge.readString(3, __dbResults);
    this.airline_delay = JdbcWritableBridge.readDouble(4, __dbResults);
    this.arrival_delay = JdbcWritableBridge.readDouble(5, __dbResults);
    this.arrival_time = JdbcWritableBridge.readInteger(6, __dbResults);
    this.cancellation_reason = JdbcWritableBridge.readString(7, __dbResults);
    this.cancelled = JdbcWritableBridge.readInteger(8, __dbResults);
    this.day = JdbcWritableBridge.readInteger(9, __dbResults);
    this.day_of_week = JdbcWritableBridge.readInteger(10, __dbResults);
    this.departure_delay = JdbcWritableBridge.readDouble(11, __dbResults);
    this.departure_time = JdbcWritableBridge.readInteger(12, __dbResults);
    this.destination_airport = JdbcWritableBridge.readString(13, __dbResults);
    this.distance = JdbcWritableBridge.readInteger(14, __dbResults);
    this.diverted = JdbcWritableBridge.readInteger(15, __dbResults);
    this.elapsed_time = JdbcWritableBridge.readDouble(16, __dbResults);
    this.flight_number = JdbcWritableBridge.readInteger(17, __dbResults);
    this.id = JdbcWritableBridge.readInteger(18, __dbResults);
    this.late_aircraft_delay = JdbcWritableBridge.readDouble(19, __dbResults);
    this.month = JdbcWritableBridge.readInteger(20, __dbResults);
    this.origin_airport = JdbcWritableBridge.readString(21, __dbResults);
    this.scheduled_arrival = JdbcWritableBridge.readInteger(22, __dbResults);
    this.scheduled_departure = JdbcWritableBridge.readInteger(23, __dbResults);
    this.scheduled_time = JdbcWritableBridge.readDouble(24, __dbResults);
    this.security_delay = JdbcWritableBridge.readDouble(25, __dbResults);
    this.tail_number = JdbcWritableBridge.readString(26, __dbResults);
    this.taxi_in = JdbcWritableBridge.readDouble(27, __dbResults);
    this.taxi_out = JdbcWritableBridge.readDouble(28, __dbResults);
    this.weather_delay = JdbcWritableBridge.readDouble(29, __dbResults);
    this.wheels_off = JdbcWritableBridge.readInteger(30, __dbResults);
    this.wheels_on = JdbcWritableBridge.readInteger(31, __dbResults);
    this.year = JdbcWritableBridge.readInteger(32, __dbResults);
  }
  public void readFields0(ResultSet __dbResults) throws SQLException {
    this.air_system_delay = JdbcWritableBridge.readDouble(1, __dbResults);
    this.air_time = JdbcWritableBridge.readDouble(2, __dbResults);
    this.airline = JdbcWritableBridge.readString(3, __dbResults);
    this.airline_delay = JdbcWritableBridge.readDouble(4, __dbResults);
    this.arrival_delay = JdbcWritableBridge.readDouble(5, __dbResults);
    this.arrival_time = JdbcWritableBridge.readInteger(6, __dbResults);
    this.cancellation_reason = JdbcWritableBridge.readString(7, __dbResults);
    this.cancelled = JdbcWritableBridge.readInteger(8, __dbResults);
    this.day = JdbcWritableBridge.readInteger(9, __dbResults);
    this.day_of_week = JdbcWritableBridge.readInteger(10, __dbResults);
    this.departure_delay = JdbcWritableBridge.readDouble(11, __dbResults);
    this.departure_time = JdbcWritableBridge.readInteger(12, __dbResults);
    this.destination_airport = JdbcWritableBridge.readString(13, __dbResults);
    this.distance = JdbcWritableBridge.readInteger(14, __dbResults);
    this.diverted = JdbcWritableBridge.readInteger(15, __dbResults);
    this.elapsed_time = JdbcWritableBridge.readDouble(16, __dbResults);
    this.flight_number = JdbcWritableBridge.readInteger(17, __dbResults);
    this.id = JdbcWritableBridge.readInteger(18, __dbResults);
    this.late_aircraft_delay = JdbcWritableBridge.readDouble(19, __dbResults);
    this.month = JdbcWritableBridge.readInteger(20, __dbResults);
    this.origin_airport = JdbcWritableBridge.readString(21, __dbResults);
    this.scheduled_arrival = JdbcWritableBridge.readInteger(22, __dbResults);
    this.scheduled_departure = JdbcWritableBridge.readInteger(23, __dbResults);
    this.scheduled_time = JdbcWritableBridge.readDouble(24, __dbResults);
    this.security_delay = JdbcWritableBridge.readDouble(25, __dbResults);
    this.tail_number = JdbcWritableBridge.readString(26, __dbResults);
    this.taxi_in = JdbcWritableBridge.readDouble(27, __dbResults);
    this.taxi_out = JdbcWritableBridge.readDouble(28, __dbResults);
    this.weather_delay = JdbcWritableBridge.readDouble(29, __dbResults);
    this.wheels_off = JdbcWritableBridge.readInteger(30, __dbResults);
    this.wheels_on = JdbcWritableBridge.readInteger(31, __dbResults);
    this.year = JdbcWritableBridge.readInteger(32, __dbResults);
  }
  public void loadLargeObjects(LargeObjectLoader __loader)
      throws SQLException, IOException, InterruptedException {
  }
  public void loadLargeObjects0(LargeObjectLoader __loader)
      throws SQLException, IOException, InterruptedException {
  }
  public void write(PreparedStatement __dbStmt) throws SQLException {
    write(__dbStmt, 0);
  }

  public int write(PreparedStatement __dbStmt, int __off) throws SQLException {
    JdbcWritableBridge.writeDouble(air_system_delay, 1 + __off, 8, __dbStmt);
    JdbcWritableBridge.writeDouble(air_time, 2 + __off, 8, __dbStmt);
    JdbcWritableBridge.writeString(airline, 3 + __off, 12, __dbStmt);
    JdbcWritableBridge.writeDouble(airline_delay, 4 + __off, 8, __dbStmt);
    JdbcWritableBridge.writeDouble(arrival_delay, 5 + __off, 8, __dbStmt);
    JdbcWritableBridge.writeInteger(arrival_time, 6 + __off, 4, __dbStmt);
    JdbcWritableBridge.writeString(cancellation_reason, 7 + __off, 12, __dbStmt);
    JdbcWritableBridge.writeInteger(cancelled, 8 + __off, 4, __dbStmt);
    JdbcWritableBridge.writeInteger(day, 9 + __off, 4, __dbStmt);
    JdbcWritableBridge.writeInteger(day_of_week, 10 + __off, 4, __dbStmt);
    JdbcWritableBridge.writeDouble(departure_delay, 11 + __off, 8, __dbStmt);
    JdbcWritableBridge.writeInteger(departure_time, 12 + __off, 4, __dbStmt);
    JdbcWritableBridge.writeString(destination_airport, 13 + __off, 12, __dbStmt);
    JdbcWritableBridge.writeInteger(distance, 14 + __off, 4, __dbStmt);
    JdbcWritableBridge.writeInteger(diverted, 15 + __off, 4, __dbStmt);
    JdbcWritableBridge.writeDouble(elapsed_time, 16 + __off, 8, __dbStmt);
    JdbcWritableBridge.writeInteger(flight_number, 17 + __off, 4, __dbStmt);
    JdbcWritableBridge.writeInteger(id, 18 + __off, 4, __dbStmt);
    JdbcWritableBridge.writeDouble(late_aircraft_delay, 19 + __off, 8, __dbStmt);
    JdbcWritableBridge.writeInteger(month, 20 + __off, 4, __dbStmt);
    JdbcWritableBridge.writeString(origin_airport, 21 + __off, 12, __dbStmt);
    JdbcWritableBridge.writeInteger(scheduled_arrival, 22 + __off, 4, __dbStmt);
    JdbcWritableBridge.writeInteger(scheduled_departure, 23 + __off, 4, __dbStmt);
    JdbcWritableBridge.writeDouble(scheduled_time, 24 + __off, 8, __dbStmt);
    JdbcWritableBridge.writeDouble(security_delay, 25 + __off, 8, __dbStmt);
    JdbcWritableBridge.writeString(tail_number, 26 + __off, 12, __dbStmt);
    JdbcWritableBridge.writeDouble(taxi_in, 27 + __off, 8, __dbStmt);
    JdbcWritableBridge.writeDouble(taxi_out, 28 + __off, 8, __dbStmt);
    JdbcWritableBridge.writeDouble(weather_delay, 29 + __off, 8, __dbStmt);
    JdbcWritableBridge.writeInteger(wheels_off, 30 + __off, 4, __dbStmt);
    JdbcWritableBridge.writeInteger(wheels_on, 31 + __off, 4, __dbStmt);
    JdbcWritableBridge.writeInteger(year, 32 + __off, 4, __dbStmt);
    return 32;
  }
  public void write0(PreparedStatement __dbStmt, int __off) throws SQLException {
    JdbcWritableBridge.writeDouble(air_system_delay, 1 + __off, 8, __dbStmt);
    JdbcWritableBridge.writeDouble(air_time, 2 + __off, 8, __dbStmt);
    JdbcWritableBridge.writeString(airline, 3 + __off, 12, __dbStmt);
    JdbcWritableBridge.writeDouble(airline_delay, 4 + __off, 8, __dbStmt);
    JdbcWritableBridge.writeDouble(arrival_delay, 5 + __off, 8, __dbStmt);
    JdbcWritableBridge.writeInteger(arrival_time, 6 + __off, 4, __dbStmt);
    JdbcWritableBridge.writeString(cancellation_reason, 7 + __off, 12, __dbStmt);
    JdbcWritableBridge.writeInteger(cancelled, 8 + __off, 4, __dbStmt);
    JdbcWritableBridge.writeInteger(day, 9 + __off, 4, __dbStmt);
    JdbcWritableBridge.writeInteger(day_of_week, 10 + __off, 4, __dbStmt);
    JdbcWritableBridge.writeDouble(departure_delay, 11 + __off, 8, __dbStmt);
    JdbcWritableBridge.writeInteger(departure_time, 12 + __off, 4, __dbStmt);
    JdbcWritableBridge.writeString(destination_airport, 13 + __off, 12, __dbStmt);
    JdbcWritableBridge.writeInteger(distance, 14 + __off, 4, __dbStmt);
    JdbcWritableBridge.writeInteger(diverted, 15 + __off, 4, __dbStmt);
    JdbcWritableBridge.writeDouble(elapsed_time, 16 + __off, 8, __dbStmt);
    JdbcWritableBridge.writeInteger(flight_number, 17 + __off, 4, __dbStmt);
    JdbcWritableBridge.writeInteger(id, 18 + __off, 4, __dbStmt);
    JdbcWritableBridge.writeDouble(late_aircraft_delay, 19 + __off, 8, __dbStmt);
    JdbcWritableBridge.writeInteger(month, 20 + __off, 4, __dbStmt);
    JdbcWritableBridge.writeString(origin_airport, 21 + __off, 12, __dbStmt);
    JdbcWritableBridge.writeInteger(scheduled_arrival, 22 + __off, 4, __dbStmt);
    JdbcWritableBridge.writeInteger(scheduled_departure, 23 + __off, 4, __dbStmt);
    JdbcWritableBridge.writeDouble(scheduled_time, 24 + __off, 8, __dbStmt);
    JdbcWritableBridge.writeDouble(security_delay, 25 + __off, 8, __dbStmt);
    JdbcWritableBridge.writeString(tail_number, 26 + __off, 12, __dbStmt);
    JdbcWritableBridge.writeDouble(taxi_in, 27 + __off, 8, __dbStmt);
    JdbcWritableBridge.writeDouble(taxi_out, 28 + __off, 8, __dbStmt);
    JdbcWritableBridge.writeDouble(weather_delay, 29 + __off, 8, __dbStmt);
    JdbcWritableBridge.writeInteger(wheels_off, 30 + __off, 4, __dbStmt);
    JdbcWritableBridge.writeInteger(wheels_on, 31 + __off, 4, __dbStmt);
    JdbcWritableBridge.writeInteger(year, 32 + __off, 4, __dbStmt);
  }
  public void readFields(DataInput __dataIn) throws IOException {
this.readFields0(__dataIn);  }
  public void readFields0(DataInput __dataIn) throws IOException {
    if (__dataIn.readBoolean()) { 
        this.air_system_delay = null;
    } else {
    this.air_system_delay = Double.valueOf(__dataIn.readDouble());
    }
    if (__dataIn.readBoolean()) { 
        this.air_time = null;
    } else {
    this.air_time = Double.valueOf(__dataIn.readDouble());
    }
    if (__dataIn.readBoolean()) { 
        this.airline = null;
    } else {
    this.airline = Text.readString(__dataIn);
    }
    if (__dataIn.readBoolean()) { 
        this.airline_delay = null;
    } else {
    this.airline_delay = Double.valueOf(__dataIn.readDouble());
    }
    if (__dataIn.readBoolean()) { 
        this.arrival_delay = null;
    } else {
    this.arrival_delay = Double.valueOf(__dataIn.readDouble());
    }
    if (__dataIn.readBoolean()) { 
        this.arrival_time = null;
    } else {
    this.arrival_time = Integer.valueOf(__dataIn.readInt());
    }
    if (__dataIn.readBoolean()) { 
        this.cancellation_reason = null;
    } else {
    this.cancellation_reason = Text.readString(__dataIn);
    }
    if (__dataIn.readBoolean()) { 
        this.cancelled = null;
    } else {
    this.cancelled = Integer.valueOf(__dataIn.readInt());
    }
    if (__dataIn.readBoolean()) { 
        this.day = null;
    } else {
    this.day = Integer.valueOf(__dataIn.readInt());
    }
    if (__dataIn.readBoolean()) { 
        this.day_of_week = null;
    } else {
    this.day_of_week = Integer.valueOf(__dataIn.readInt());
    }
    if (__dataIn.readBoolean()) { 
        this.departure_delay = null;
    } else {
    this.departure_delay = Double.valueOf(__dataIn.readDouble());
    }
    if (__dataIn.readBoolean()) { 
        this.departure_time = null;
    } else {
    this.departure_time = Integer.valueOf(__dataIn.readInt());
    }
    if (__dataIn.readBoolean()) { 
        this.destination_airport = null;
    } else {
    this.destination_airport = Text.readString(__dataIn);
    }
    if (__dataIn.readBoolean()) { 
        this.distance = null;
    } else {
    this.distance = Integer.valueOf(__dataIn.readInt());
    }
    if (__dataIn.readBoolean()) { 
        this.diverted = null;
    } else {
    this.diverted = Integer.valueOf(__dataIn.readInt());
    }
    if (__dataIn.readBoolean()) { 
        this.elapsed_time = null;
    } else {
    this.elapsed_time = Double.valueOf(__dataIn.readDouble());
    }
    if (__dataIn.readBoolean()) { 
        this.flight_number = null;
    } else {
    this.flight_number = Integer.valueOf(__dataIn.readInt());
    }
    if (__dataIn.readBoolean()) { 
        this.id = null;
    } else {
    this.id = Integer.valueOf(__dataIn.readInt());
    }
    if (__dataIn.readBoolean()) { 
        this.late_aircraft_delay = null;
    } else {
    this.late_aircraft_delay = Double.valueOf(__dataIn.readDouble());
    }
    if (__dataIn.readBoolean()) { 
        this.month = null;
    } else {
    this.month = Integer.valueOf(__dataIn.readInt());
    }
    if (__dataIn.readBoolean()) { 
        this.origin_airport = null;
    } else {
    this.origin_airport = Text.readString(__dataIn);
    }
    if (__dataIn.readBoolean()) { 
        this.scheduled_arrival = null;
    } else {
    this.scheduled_arrival = Integer.valueOf(__dataIn.readInt());
    }
    if (__dataIn.readBoolean()) { 
        this.scheduled_departure = null;
    } else {
    this.scheduled_departure = Integer.valueOf(__dataIn.readInt());
    }
    if (__dataIn.readBoolean()) { 
        this.scheduled_time = null;
    } else {
    this.scheduled_time = Double.valueOf(__dataIn.readDouble());
    }
    if (__dataIn.readBoolean()) { 
        this.security_delay = null;
    } else {
    this.security_delay = Double.valueOf(__dataIn.readDouble());
    }
    if (__dataIn.readBoolean()) { 
        this.tail_number = null;
    } else {
    this.tail_number = Text.readString(__dataIn);
    }
    if (__dataIn.readBoolean()) { 
        this.taxi_in = null;
    } else {
    this.taxi_in = Double.valueOf(__dataIn.readDouble());
    }
    if (__dataIn.readBoolean()) { 
        this.taxi_out = null;
    } else {
    this.taxi_out = Double.valueOf(__dataIn.readDouble());
    }
    if (__dataIn.readBoolean()) { 
        this.weather_delay = null;
    } else {
    this.weather_delay = Double.valueOf(__dataIn.readDouble());
    }
    if (__dataIn.readBoolean()) { 
        this.wheels_off = null;
    } else {
    this.wheels_off = Integer.valueOf(__dataIn.readInt());
    }
    if (__dataIn.readBoolean()) { 
        this.wheels_on = null;
    } else {
    this.wheels_on = Integer.valueOf(__dataIn.readInt());
    }
    if (__dataIn.readBoolean()) { 
        this.year = null;
    } else {
    this.year = Integer.valueOf(__dataIn.readInt());
    }
  }
  public void write(DataOutput __dataOut) throws IOException {
    if (null == this.air_system_delay) { 
        __dataOut.writeBoolean(true);
    } else {
        __dataOut.writeBoolean(false);
    __dataOut.writeDouble(this.air_system_delay);
    }
    if (null == this.air_time) { 
        __dataOut.writeBoolean(true);
    } else {
        __dataOut.writeBoolean(false);
    __dataOut.writeDouble(this.air_time);
    }
    if (null == this.airline) { 
        __dataOut.writeBoolean(true);
    } else {
        __dataOut.writeBoolean(false);
    Text.writeString(__dataOut, airline);
    }
    if (null == this.airline_delay) { 
        __dataOut.writeBoolean(true);
    } else {
        __dataOut.writeBoolean(false);
    __dataOut.writeDouble(this.airline_delay);
    }
    if (null == this.arrival_delay) { 
        __dataOut.writeBoolean(true);
    } else {
        __dataOut.writeBoolean(false);
    __dataOut.writeDouble(this.arrival_delay);
    }
    if (null == this.arrival_time) { 
        __dataOut.writeBoolean(true);
    } else {
        __dataOut.writeBoolean(false);
    __dataOut.writeInt(this.arrival_time);
    }
    if (null == this.cancellation_reason) { 
        __dataOut.writeBoolean(true);
    } else {
        __dataOut.writeBoolean(false);
    Text.writeString(__dataOut, cancellation_reason);
    }
    if (null == this.cancelled) { 
        __dataOut.writeBoolean(true);
    } else {
        __dataOut.writeBoolean(false);
    __dataOut.writeInt(this.cancelled);
    }
    if (null == this.day) { 
        __dataOut.writeBoolean(true);
    } else {
        __dataOut.writeBoolean(false);
    __dataOut.writeInt(this.day);
    }
    if (null == this.day_of_week) { 
        __dataOut.writeBoolean(true);
    } else {
        __dataOut.writeBoolean(false);
    __dataOut.writeInt(this.day_of_week);
    }
    if (null == this.departure_delay) { 
        __dataOut.writeBoolean(true);
    } else {
        __dataOut.writeBoolean(false);
    __dataOut.writeDouble(this.departure_delay);
    }
    if (null == this.departure_time) { 
        __dataOut.writeBoolean(true);
    } else {
        __dataOut.writeBoolean(false);
    __dataOut.writeInt(this.departure_time);
    }
    if (null == this.destination_airport) { 
        __dataOut.writeBoolean(true);
    } else {
        __dataOut.writeBoolean(false);
    Text.writeString(__dataOut, destination_airport);
    }
    if (null == this.distance) { 
        __dataOut.writeBoolean(true);
    } else {
        __dataOut.writeBoolean(false);
    __dataOut.writeInt(this.distance);
    }
    if (null == this.diverted) { 
        __dataOut.writeBoolean(true);
    } else {
        __dataOut.writeBoolean(false);
    __dataOut.writeInt(this.diverted);
    }
    if (null == this.elapsed_time) { 
        __dataOut.writeBoolean(true);
    } else {
        __dataOut.writeBoolean(false);
    __dataOut.writeDouble(this.elapsed_time);
    }
    if (null == this.flight_number) { 
        __dataOut.writeBoolean(true);
    } else {
        __dataOut.writeBoolean(false);
    __dataOut.writeInt(this.flight_number);
    }
    if (null == this.id) { 
        __dataOut.writeBoolean(true);
    } else {
        __dataOut.writeBoolean(false);
    __dataOut.writeInt(this.id);
    }
    if (null == this.late_aircraft_delay) { 
        __dataOut.writeBoolean(true);
    } else {
        __dataOut.writeBoolean(false);
    __dataOut.writeDouble(this.late_aircraft_delay);
    }
    if (null == this.month) { 
        __dataOut.writeBoolean(true);
    } else {
        __dataOut.writeBoolean(false);
    __dataOut.writeInt(this.month);
    }
    if (null == this.origin_airport) { 
        __dataOut.writeBoolean(true);
    } else {
        __dataOut.writeBoolean(false);
    Text.writeString(__dataOut, origin_airport);
    }
    if (null == this.scheduled_arrival) { 
        __dataOut.writeBoolean(true);
    } else {
        __dataOut.writeBoolean(false);
    __dataOut.writeInt(this.scheduled_arrival);
    }
    if (null == this.scheduled_departure) { 
        __dataOut.writeBoolean(true);
    } else {
        __dataOut.writeBoolean(false);
    __dataOut.writeInt(this.scheduled_departure);
    }
    if (null == this.scheduled_time) { 
        __dataOut.writeBoolean(true);
    } else {
        __dataOut.writeBoolean(false);
    __dataOut.writeDouble(this.scheduled_time);
    }
    if (null == this.security_delay) { 
        __dataOut.writeBoolean(true);
    } else {
        __dataOut.writeBoolean(false);
    __dataOut.writeDouble(this.security_delay);
    }
    if (null == this.tail_number) { 
        __dataOut.writeBoolean(true);
    } else {
        __dataOut.writeBoolean(false);
    Text.writeString(__dataOut, tail_number);
    }
    if (null == this.taxi_in) { 
        __dataOut.writeBoolean(true);
    } else {
        __dataOut.writeBoolean(false);
    __dataOut.writeDouble(this.taxi_in);
    }
    if (null == this.taxi_out) { 
        __dataOut.writeBoolean(true);
    } else {
        __dataOut.writeBoolean(false);
    __dataOut.writeDouble(this.taxi_out);
    }
    if (null == this.weather_delay) { 
        __dataOut.writeBoolean(true);
    } else {
        __dataOut.writeBoolean(false);
    __dataOut.writeDouble(this.weather_delay);
    }
    if (null == this.wheels_off) { 
        __dataOut.writeBoolean(true);
    } else {
        __dataOut.writeBoolean(false);
    __dataOut.writeInt(this.wheels_off);
    }
    if (null == this.wheels_on) { 
        __dataOut.writeBoolean(true);
    } else {
        __dataOut.writeBoolean(false);
    __dataOut.writeInt(this.wheels_on);
    }
    if (null == this.year) { 
        __dataOut.writeBoolean(true);
    } else {
        __dataOut.writeBoolean(false);
    __dataOut.writeInt(this.year);
    }
  }
  public void write0(DataOutput __dataOut) throws IOException {
    if (null == this.air_system_delay) { 
        __dataOut.writeBoolean(true);
    } else {
        __dataOut.writeBoolean(false);
    __dataOut.writeDouble(this.air_system_delay);
    }
    if (null == this.air_time) { 
        __dataOut.writeBoolean(true);
    } else {
        __dataOut.writeBoolean(false);
    __dataOut.writeDouble(this.air_time);
    }
    if (null == this.airline) { 
        __dataOut.writeBoolean(true);
    } else {
        __dataOut.writeBoolean(false);
    Text.writeString(__dataOut, airline);
    }
    if (null == this.airline_delay) { 
        __dataOut.writeBoolean(true);
    } else {
        __dataOut.writeBoolean(false);
    __dataOut.writeDouble(this.airline_delay);
    }
    if (null == this.arrival_delay) { 
        __dataOut.writeBoolean(true);
    } else {
        __dataOut.writeBoolean(false);
    __dataOut.writeDouble(this.arrival_delay);
    }
    if (null == this.arrival_time) { 
        __dataOut.writeBoolean(true);
    } else {
        __dataOut.writeBoolean(false);
    __dataOut.writeInt(this.arrival_time);
    }
    if (null == this.cancellation_reason) { 
        __dataOut.writeBoolean(true);
    } else {
        __dataOut.writeBoolean(false);
    Text.writeString(__dataOut, cancellation_reason);
    }
    if (null == this.cancelled) { 
        __dataOut.writeBoolean(true);
    } else {
        __dataOut.writeBoolean(false);
    __dataOut.writeInt(this.cancelled);
    }
    if (null == this.day) { 
        __dataOut.writeBoolean(true);
    } else {
        __dataOut.writeBoolean(false);
    __dataOut.writeInt(this.day);
    }
    if (null == this.day_of_week) { 
        __dataOut.writeBoolean(true);
    } else {
        __dataOut.writeBoolean(false);
    __dataOut.writeInt(this.day_of_week);
    }
    if (null == this.departure_delay) { 
        __dataOut.writeBoolean(true);
    } else {
        __dataOut.writeBoolean(false);
    __dataOut.writeDouble(this.departure_delay);
    }
    if (null == this.departure_time) { 
        __dataOut.writeBoolean(true);
    } else {
        __dataOut.writeBoolean(false);
    __dataOut.writeInt(this.departure_time);
    }
    if (null == this.destination_airport) { 
        __dataOut.writeBoolean(true);
    } else {
        __dataOut.writeBoolean(false);
    Text.writeString(__dataOut, destination_airport);
    }
    if (null == this.distance) { 
        __dataOut.writeBoolean(true);
    } else {
        __dataOut.writeBoolean(false);
    __dataOut.writeInt(this.distance);
    }
    if (null == this.diverted) { 
        __dataOut.writeBoolean(true);
    } else {
        __dataOut.writeBoolean(false);
    __dataOut.writeInt(this.diverted);
    }
    if (null == this.elapsed_time) { 
        __dataOut.writeBoolean(true);
    } else {
        __dataOut.writeBoolean(false);
    __dataOut.writeDouble(this.elapsed_time);
    }
    if (null == this.flight_number) { 
        __dataOut.writeBoolean(true);
    } else {
        __dataOut.writeBoolean(false);
    __dataOut.writeInt(this.flight_number);
    }
    if (null == this.id) { 
        __dataOut.writeBoolean(true);
    } else {
        __dataOut.writeBoolean(false);
    __dataOut.writeInt(this.id);
    }
    if (null == this.late_aircraft_delay) { 
        __dataOut.writeBoolean(true);
    } else {
        __dataOut.writeBoolean(false);
    __dataOut.writeDouble(this.late_aircraft_delay);
    }
    if (null == this.month) { 
        __dataOut.writeBoolean(true);
    } else {
        __dataOut.writeBoolean(false);
    __dataOut.writeInt(this.month);
    }
    if (null == this.origin_airport) { 
        __dataOut.writeBoolean(true);
    } else {
        __dataOut.writeBoolean(false);
    Text.writeString(__dataOut, origin_airport);
    }
    if (null == this.scheduled_arrival) { 
        __dataOut.writeBoolean(true);
    } else {
        __dataOut.writeBoolean(false);
    __dataOut.writeInt(this.scheduled_arrival);
    }
    if (null == this.scheduled_departure) { 
        __dataOut.writeBoolean(true);
    } else {
        __dataOut.writeBoolean(false);
    __dataOut.writeInt(this.scheduled_departure);
    }
    if (null == this.scheduled_time) { 
        __dataOut.writeBoolean(true);
    } else {
        __dataOut.writeBoolean(false);
    __dataOut.writeDouble(this.scheduled_time);
    }
    if (null == this.security_delay) { 
        __dataOut.writeBoolean(true);
    } else {
        __dataOut.writeBoolean(false);
    __dataOut.writeDouble(this.security_delay);
    }
    if (null == this.tail_number) { 
        __dataOut.writeBoolean(true);
    } else {
        __dataOut.writeBoolean(false);
    Text.writeString(__dataOut, tail_number);
    }
    if (null == this.taxi_in) { 
        __dataOut.writeBoolean(true);
    } else {
        __dataOut.writeBoolean(false);
    __dataOut.writeDouble(this.taxi_in);
    }
    if (null == this.taxi_out) { 
        __dataOut.writeBoolean(true);
    } else {
        __dataOut.writeBoolean(false);
    __dataOut.writeDouble(this.taxi_out);
    }
    if (null == this.weather_delay) { 
        __dataOut.writeBoolean(true);
    } else {
        __dataOut.writeBoolean(false);
    __dataOut.writeDouble(this.weather_delay);
    }
    if (null == this.wheels_off) { 
        __dataOut.writeBoolean(true);
    } else {
        __dataOut.writeBoolean(false);
    __dataOut.writeInt(this.wheels_off);
    }
    if (null == this.wheels_on) { 
        __dataOut.writeBoolean(true);
    } else {
        __dataOut.writeBoolean(false);
    __dataOut.writeInt(this.wheels_on);
    }
    if (null == this.year) { 
        __dataOut.writeBoolean(true);
    } else {
        __dataOut.writeBoolean(false);
    __dataOut.writeInt(this.year);
    }
  }
  private static final DelimiterSet __outputDelimiters = new DelimiterSet((char) 44, (char) 10, (char) 0, (char) 0, false);
  public String toString() {
    return toString(__outputDelimiters, true);
  }
  public String toString(DelimiterSet delimiters) {
    return toString(delimiters, true);
  }
  public String toString(boolean useRecordDelim) {
    return toString(__outputDelimiters, useRecordDelim);
  }
  public String toString(DelimiterSet delimiters, boolean useRecordDelim) {
    StringBuilder __sb = new StringBuilder();
    char fieldDelim = delimiters.getFieldsTerminatedBy();
    __sb.append(FieldFormatter.escapeAndEnclose(air_system_delay==null?"\\N":"" + air_system_delay, delimiters));
    __sb.append(fieldDelim);
    __sb.append(FieldFormatter.escapeAndEnclose(air_time==null?"\\N":"" + air_time, delimiters));
    __sb.append(fieldDelim);
    __sb.append(FieldFormatter.escapeAndEnclose(airline==null?"\\N":airline, delimiters));
    __sb.append(fieldDelim);
    __sb.append(FieldFormatter.escapeAndEnclose(airline_delay==null?"\\N":"" + airline_delay, delimiters));
    __sb.append(fieldDelim);
    __sb.append(FieldFormatter.escapeAndEnclose(arrival_delay==null?"\\N":"" + arrival_delay, delimiters));
    __sb.append(fieldDelim);
    __sb.append(FieldFormatter.escapeAndEnclose(arrival_time==null?"\\N":"" + arrival_time, delimiters));
    __sb.append(fieldDelim);
    __sb.append(FieldFormatter.escapeAndEnclose(cancellation_reason==null?"\\N":cancellation_reason, delimiters));
    __sb.append(fieldDelim);
    __sb.append(FieldFormatter.escapeAndEnclose(cancelled==null?"\\N":"" + cancelled, delimiters));
    __sb.append(fieldDelim);
    __sb.append(FieldFormatter.escapeAndEnclose(day==null?"\\N":"" + day, delimiters));
    __sb.append(fieldDelim);
    __sb.append(FieldFormatter.escapeAndEnclose(day_of_week==null?"\\N":"" + day_of_week, delimiters));
    __sb.append(fieldDelim);
    __sb.append(FieldFormatter.escapeAndEnclose(departure_delay==null?"\\N":"" + departure_delay, delimiters));
    __sb.append(fieldDelim);
    __sb.append(FieldFormatter.escapeAndEnclose(departure_time==null?"\\N":"" + departure_time, delimiters));
    __sb.append(fieldDelim);
    __sb.append(FieldFormatter.escapeAndEnclose(destination_airport==null?"\\N":destination_airport, delimiters));
    __sb.append(fieldDelim);
    __sb.append(FieldFormatter.escapeAndEnclose(distance==null?"\\N":"" + distance, delimiters));
    __sb.append(fieldDelim);
    __sb.append(FieldFormatter.escapeAndEnclose(diverted==null?"\\N":"" + diverted, delimiters));
    __sb.append(fieldDelim);
    __sb.append(FieldFormatter.escapeAndEnclose(elapsed_time==null?"\\N":"" + elapsed_time, delimiters));
    __sb.append(fieldDelim);
    __sb.append(FieldFormatter.escapeAndEnclose(flight_number==null?"\\N":"" + flight_number, delimiters));
    __sb.append(fieldDelim);
    __sb.append(FieldFormatter.escapeAndEnclose(id==null?"\\N":"" + id, delimiters));
    __sb.append(fieldDelim);
    __sb.append(FieldFormatter.escapeAndEnclose(late_aircraft_delay==null?"\\N":"" + late_aircraft_delay, delimiters));
    __sb.append(fieldDelim);
    __sb.append(FieldFormatter.escapeAndEnclose(month==null?"\\N":"" + month, delimiters));
    __sb.append(fieldDelim);
    __sb.append(FieldFormatter.escapeAndEnclose(origin_airport==null?"\\N":origin_airport, delimiters));
    __sb.append(fieldDelim);
    __sb.append(FieldFormatter.escapeAndEnclose(scheduled_arrival==null?"\\N":"" + scheduled_arrival, delimiters));
    __sb.append(fieldDelim);
    __sb.append(FieldFormatter.escapeAndEnclose(scheduled_departure==null?"\\N":"" + scheduled_departure, delimiters));
    __sb.append(fieldDelim);
    __sb.append(FieldFormatter.escapeAndEnclose(scheduled_time==null?"\\N":"" + scheduled_time, delimiters));
    __sb.append(fieldDelim);
    __sb.append(FieldFormatter.escapeAndEnclose(security_delay==null?"\\N":"" + security_delay, delimiters));
    __sb.append(fieldDelim);
    __sb.append(FieldFormatter.escapeAndEnclose(tail_number==null?"\\N":tail_number, delimiters));
    __sb.append(fieldDelim);
    __sb.append(FieldFormatter.escapeAndEnclose(taxi_in==null?"\\N":"" + taxi_in, delimiters));
    __sb.append(fieldDelim);
    __sb.append(FieldFormatter.escapeAndEnclose(taxi_out==null?"\\N":"" + taxi_out, delimiters));
    __sb.append(fieldDelim);
    __sb.append(FieldFormatter.escapeAndEnclose(weather_delay==null?"\\N":"" + weather_delay, delimiters));
    __sb.append(fieldDelim);
    __sb.append(FieldFormatter.escapeAndEnclose(wheels_off==null?"\\N":"" + wheels_off, delimiters));
    __sb.append(fieldDelim);
    __sb.append(FieldFormatter.escapeAndEnclose(wheels_on==null?"\\N":"" + wheels_on, delimiters));
    __sb.append(fieldDelim);
    __sb.append(FieldFormatter.escapeAndEnclose(year==null?"\\N":"" + year, delimiters));
    if (useRecordDelim) {
      __sb.append(delimiters.getLinesTerminatedBy());
    }
    return __sb.toString();
  }
  public void toString0(DelimiterSet delimiters, StringBuilder __sb, char fieldDelim) {
    __sb.append(FieldFormatter.escapeAndEnclose(air_system_delay==null?"\\N":"" + air_system_delay, delimiters));
    __sb.append(fieldDelim);
    __sb.append(FieldFormatter.escapeAndEnclose(air_time==null?"\\N":"" + air_time, delimiters));
    __sb.append(fieldDelim);
    __sb.append(FieldFormatter.escapeAndEnclose(airline==null?"\\N":airline, delimiters));
    __sb.append(fieldDelim);
    __sb.append(FieldFormatter.escapeAndEnclose(airline_delay==null?"\\N":"" + airline_delay, delimiters));
    __sb.append(fieldDelim);
    __sb.append(FieldFormatter.escapeAndEnclose(arrival_delay==null?"\\N":"" + arrival_delay, delimiters));
    __sb.append(fieldDelim);
    __sb.append(FieldFormatter.escapeAndEnclose(arrival_time==null?"\\N":"" + arrival_time, delimiters));
    __sb.append(fieldDelim);
    __sb.append(FieldFormatter.escapeAndEnclose(cancellation_reason==null?"\\N":cancellation_reason, delimiters));
    __sb.append(fieldDelim);
    __sb.append(FieldFormatter.escapeAndEnclose(cancelled==null?"\\N":"" + cancelled, delimiters));
    __sb.append(fieldDelim);
    __sb.append(FieldFormatter.escapeAndEnclose(day==null?"\\N":"" + day, delimiters));
    __sb.append(fieldDelim);
    __sb.append(FieldFormatter.escapeAndEnclose(day_of_week==null?"\\N":"" + day_of_week, delimiters));
    __sb.append(fieldDelim);
    __sb.append(FieldFormatter.escapeAndEnclose(departure_delay==null?"\\N":"" + departure_delay, delimiters));
    __sb.append(fieldDelim);
    __sb.append(FieldFormatter.escapeAndEnclose(departure_time==null?"\\N":"" + departure_time, delimiters));
    __sb.append(fieldDelim);
    __sb.append(FieldFormatter.escapeAndEnclose(destination_airport==null?"\\N":destination_airport, delimiters));
    __sb.append(fieldDelim);
    __sb.append(FieldFormatter.escapeAndEnclose(distance==null?"\\N":"" + distance, delimiters));
    __sb.append(fieldDelim);
    __sb.append(FieldFormatter.escapeAndEnclose(diverted==null?"\\N":"" + diverted, delimiters));
    __sb.append(fieldDelim);
    __sb.append(FieldFormatter.escapeAndEnclose(elapsed_time==null?"\\N":"" + elapsed_time, delimiters));
    __sb.append(fieldDelim);
    __sb.append(FieldFormatter.escapeAndEnclose(flight_number==null?"\\N":"" + flight_number, delimiters));
    __sb.append(fieldDelim);
    __sb.append(FieldFormatter.escapeAndEnclose(id==null?"\\N":"" + id, delimiters));
    __sb.append(fieldDelim);
    __sb.append(FieldFormatter.escapeAndEnclose(late_aircraft_delay==null?"\\N":"" + late_aircraft_delay, delimiters));
    __sb.append(fieldDelim);
    __sb.append(FieldFormatter.escapeAndEnclose(month==null?"\\N":"" + month, delimiters));
    __sb.append(fieldDelim);
    __sb.append(FieldFormatter.escapeAndEnclose(origin_airport==null?"\\N":origin_airport, delimiters));
    __sb.append(fieldDelim);
    __sb.append(FieldFormatter.escapeAndEnclose(scheduled_arrival==null?"\\N":"" + scheduled_arrival, delimiters));
    __sb.append(fieldDelim);
    __sb.append(FieldFormatter.escapeAndEnclose(scheduled_departure==null?"\\N":"" + scheduled_departure, delimiters));
    __sb.append(fieldDelim);
    __sb.append(FieldFormatter.escapeAndEnclose(scheduled_time==null?"\\N":"" + scheduled_time, delimiters));
    __sb.append(fieldDelim);
    __sb.append(FieldFormatter.escapeAndEnclose(security_delay==null?"\\N":"" + security_delay, delimiters));
    __sb.append(fieldDelim);
    __sb.append(FieldFormatter.escapeAndEnclose(tail_number==null?"\\N":tail_number, delimiters));
    __sb.append(fieldDelim);
    __sb.append(FieldFormatter.escapeAndEnclose(taxi_in==null?"\\N":"" + taxi_in, delimiters));
    __sb.append(fieldDelim);
    __sb.append(FieldFormatter.escapeAndEnclose(taxi_out==null?"\\N":"" + taxi_out, delimiters));
    __sb.append(fieldDelim);
    __sb.append(FieldFormatter.escapeAndEnclose(weather_delay==null?"\\N":"" + weather_delay, delimiters));
    __sb.append(fieldDelim);
    __sb.append(FieldFormatter.escapeAndEnclose(wheels_off==null?"\\N":"" + wheels_off, delimiters));
    __sb.append(fieldDelim);
    __sb.append(FieldFormatter.escapeAndEnclose(wheels_on==null?"\\N":"" + wheels_on, delimiters));
    __sb.append(fieldDelim);
    __sb.append(FieldFormatter.escapeAndEnclose(year==null?"\\N":"" + year, delimiters));
  }
  private static final DelimiterSet __inputDelimiters = new DelimiterSet((char) 44, (char) 10, (char) 0, (char) 0, false);
  private RecordParser __parser;
  public void parse(Text __record) throws RecordParser.ParseError {
    if (null == this.__parser) {
      this.__parser = new RecordParser(__inputDelimiters);
    }
    List<String> __fields = this.__parser.parseRecord(__record);
    __loadFromFields(__fields);
  }

  public void parse(CharSequence __record) throws RecordParser.ParseError {
    if (null == this.__parser) {
      this.__parser = new RecordParser(__inputDelimiters);
    }
    List<String> __fields = this.__parser.parseRecord(__record);
    __loadFromFields(__fields);
  }

  public void parse(byte [] __record) throws RecordParser.ParseError {
    if (null == this.__parser) {
      this.__parser = new RecordParser(__inputDelimiters);
    }
    List<String> __fields = this.__parser.parseRecord(__record);
    __loadFromFields(__fields);
  }

  public void parse(char [] __record) throws RecordParser.ParseError {
    if (null == this.__parser) {
      this.__parser = new RecordParser(__inputDelimiters);
    }
    List<String> __fields = this.__parser.parseRecord(__record);
    __loadFromFields(__fields);
  }

  public void parse(ByteBuffer __record) throws RecordParser.ParseError {
    if (null == this.__parser) {
      this.__parser = new RecordParser(__inputDelimiters);
    }
    List<String> __fields = this.__parser.parseRecord(__record);
    __loadFromFields(__fields);
  }

  public void parse(CharBuffer __record) throws RecordParser.ParseError {
    if (null == this.__parser) {
      this.__parser = new RecordParser(__inputDelimiters);
    }
    List<String> __fields = this.__parser.parseRecord(__record);
    __loadFromFields(__fields);
  }

  private void __loadFromFields(List<String> fields) {
    Iterator<String> __it = fields.listIterator();
    String __cur_str = null;
    try {
    if (__it.hasNext()) {
        __cur_str = __it.next();
    } else {
        __cur_str = "null";
    }
    if (__cur_str.equals("null") || __cur_str.length() == 0) { this.air_system_delay = null; } else {
      this.air_system_delay = Double.valueOf(__cur_str);
    }

    if (__it.hasNext()) {
        __cur_str = __it.next();
    } else {
        __cur_str = "null";
    }
    if (__cur_str.equals("null") || __cur_str.length() == 0) { this.air_time = null; } else {
      this.air_time = Double.valueOf(__cur_str);
    }

    if (__it.hasNext()) {
        __cur_str = __it.next();
    } else {
        __cur_str = "null";
    }
    if (__cur_str.equals("null")) { this.airline = null; } else {
      this.airline = __cur_str;
    }

    if (__it.hasNext()) {
        __cur_str = __it.next();
    } else {
        __cur_str = "null";
    }
    if (__cur_str.equals("null") || __cur_str.length() == 0) { this.airline_delay = null; } else {
      this.airline_delay = Double.valueOf(__cur_str);
    }

    if (__it.hasNext()) {
        __cur_str = __it.next();
    } else {
        __cur_str = "null";
    }
    if (__cur_str.equals("null") || __cur_str.length() == 0) { this.arrival_delay = null; } else {
      this.arrival_delay = Double.valueOf(__cur_str);
    }

    if (__it.hasNext()) {
        __cur_str = __it.next();
    } else {
        __cur_str = "null";
    }
    if (__cur_str.equals("null") || __cur_str.length() == 0) { this.arrival_time = null; } else {
      this.arrival_time = Integer.valueOf(__cur_str);
    }

    if (__it.hasNext()) {
        __cur_str = __it.next();
    } else {
        __cur_str = "null";
    }
    if (__cur_str.equals("null")) { this.cancellation_reason = null; } else {
      this.cancellation_reason = __cur_str;
    }

    if (__it.hasNext()) {
        __cur_str = __it.next();
    } else {
        __cur_str = "null";
    }
    if (__cur_str.equals("null") || __cur_str.length() == 0) { this.cancelled = null; } else {
      this.cancelled = Integer.valueOf(__cur_str);
    }

    if (__it.hasNext()) {
        __cur_str = __it.next();
    } else {
        __cur_str = "null";
    }
    if (__cur_str.equals("null") || __cur_str.length() == 0) { this.day = null; } else {
      this.day = Integer.valueOf(__cur_str);
    }

    if (__it.hasNext()) {
        __cur_str = __it.next();
    } else {
        __cur_str = "null";
    }
    if (__cur_str.equals("null") || __cur_str.length() == 0) { this.day_of_week = null; } else {
      this.day_of_week = Integer.valueOf(__cur_str);
    }

    if (__it.hasNext()) {
        __cur_str = __it.next();
    } else {
        __cur_str = "null";
    }
    if (__cur_str.equals("null") || __cur_str.length() == 0) { this.departure_delay = null; } else {
      this.departure_delay = Double.valueOf(__cur_str);
    }

    if (__it.hasNext()) {
        __cur_str = __it.next();
    } else {
        __cur_str = "null";
    }
    if (__cur_str.equals("null") || __cur_str.length() == 0) { this.departure_time = null; } else {
      this.departure_time = Integer.valueOf(__cur_str);
    }

    if (__it.hasNext()) {
        __cur_str = __it.next();
    } else {
        __cur_str = "null";
    }
    if (__cur_str.equals("null")) { this.destination_airport = null; } else {
      this.destination_airport = __cur_str;
    }

    if (__it.hasNext()) {
        __cur_str = __it.next();
    } else {
        __cur_str = "null";
    }
    if (__cur_str.equals("null") || __cur_str.length() == 0) { this.distance = null; } else {
      this.distance = Integer.valueOf(__cur_str);
    }

    if (__it.hasNext()) {
        __cur_str = __it.next();
    } else {
        __cur_str = "null";
    }
    if (__cur_str.equals("null") || __cur_str.length() == 0) { this.diverted = null; } else {
      this.diverted = Integer.valueOf(__cur_str);
    }

    if (__it.hasNext()) {
        __cur_str = __it.next();
    } else {
        __cur_str = "null";
    }
    if (__cur_str.equals("null") || __cur_str.length() == 0) { this.elapsed_time = null; } else {
      this.elapsed_time = Double.valueOf(__cur_str);
    }

    if (__it.hasNext()) {
        __cur_str = __it.next();
    } else {
        __cur_str = "null";
    }
    if (__cur_str.equals("null") || __cur_str.length() == 0) { this.flight_number = null; } else {
      this.flight_number = Integer.valueOf(__cur_str);
    }

    if (__it.hasNext()) {
        __cur_str = __it.next();
    } else {
        __cur_str = "null";
    }
    if (__cur_str.equals("null") || __cur_str.length() == 0) { this.id = null; } else {
      this.id = Integer.valueOf(__cur_str);
    }

    if (__it.hasNext()) {
        __cur_str = __it.next();
    } else {
        __cur_str = "null";
    }
    if (__cur_str.equals("null") || __cur_str.length() == 0) { this.late_aircraft_delay = null; } else {
      this.late_aircraft_delay = Double.valueOf(__cur_str);
    }

    if (__it.hasNext()) {
        __cur_str = __it.next();
    } else {
        __cur_str = "null";
    }
    if (__cur_str.equals("null") || __cur_str.length() == 0) { this.month = null; } else {
      this.month = Integer.valueOf(__cur_str);
    }

    if (__it.hasNext()) {
        __cur_str = __it.next();
    } else {
        __cur_str = "null";
    }
    if (__cur_str.equals("null")) { this.origin_airport = null; } else {
      this.origin_airport = __cur_str;
    }

    if (__it.hasNext()) {
        __cur_str = __it.next();
    } else {
        __cur_str = "null";
    }
    if (__cur_str.equals("null") || __cur_str.length() == 0) { this.scheduled_arrival = null; } else {
      this.scheduled_arrival = Integer.valueOf(__cur_str);
    }

    if (__it.hasNext()) {
        __cur_str = __it.next();
    } else {
        __cur_str = "null";
    }
    if (__cur_str.equals("null") || __cur_str.length() == 0) { this.scheduled_departure = null; } else {
      this.scheduled_departure = Integer.valueOf(__cur_str);
    }

    if (__it.hasNext()) {
        __cur_str = __it.next();
    } else {
        __cur_str = "null";
    }
    if (__cur_str.equals("null") || __cur_str.length() == 0) { this.scheduled_time = null; } else {
      this.scheduled_time = Double.valueOf(__cur_str);
    }

    if (__it.hasNext()) {
        __cur_str = __it.next();
    } else {
        __cur_str = "null";
    }
    if (__cur_str.equals("null") || __cur_str.length() == 0) { this.security_delay = null; } else {
      this.security_delay = Double.valueOf(__cur_str);
    }

    if (__it.hasNext()) {
        __cur_str = __it.next();
    } else {
        __cur_str = "null";
    }
    if (__cur_str.equals("null")) { this.tail_number = null; } else {
      this.tail_number = __cur_str;
    }

    if (__it.hasNext()) {
        __cur_str = __it.next();
    } else {
        __cur_str = "null";
    }
    if (__cur_str.equals("null") || __cur_str.length() == 0) { this.taxi_in = null; } else {
      this.taxi_in = Double.valueOf(__cur_str);
    }

    if (__it.hasNext()) {
        __cur_str = __it.next();
    } else {
        __cur_str = "null";
    }
    if (__cur_str.equals("null") || __cur_str.length() == 0) { this.taxi_out = null; } else {
      this.taxi_out = Double.valueOf(__cur_str);
    }

    if (__it.hasNext()) {
        __cur_str = __it.next();
    } else {
        __cur_str = "null";
    }
    if (__cur_str.equals("null") || __cur_str.length() == 0) { this.weather_delay = null; } else {
      this.weather_delay = Double.valueOf(__cur_str);
    }

    if (__it.hasNext()) {
        __cur_str = __it.next();
    } else {
        __cur_str = "null";
    }
    if (__cur_str.equals("null") || __cur_str.length() == 0) { this.wheels_off = null; } else {
      this.wheels_off = Integer.valueOf(__cur_str);
    }

    if (__it.hasNext()) {
        __cur_str = __it.next();
    } else {
        __cur_str = "null";
    }
    if (__cur_str.equals("null") || __cur_str.length() == 0) { this.wheels_on = null; } else {
      this.wheels_on = Integer.valueOf(__cur_str);
    }

    if (__it.hasNext()) {
        __cur_str = __it.next();
    } else {
        __cur_str = "null";
    }
    if (__cur_str.equals("null") || __cur_str.length() == 0) { this.year = null; } else {
      this.year = Integer.valueOf(__cur_str);
    }

    } catch (RuntimeException e) {    throw new RuntimeException("Can't parse input data: '" + __cur_str + "'", e);    }  }

  private void __loadFromFields0(Iterator<String> __it) {
    String __cur_str = null;
    try {
    if (__it.hasNext()) {
        __cur_str = __it.next();
    } else {
        __cur_str = "null";
    }
    if (__cur_str.equals("null") || __cur_str.length() == 0) { this.air_system_delay = null; } else {
      this.air_system_delay = Double.valueOf(__cur_str);
    }

    if (__it.hasNext()) {
        __cur_str = __it.next();
    } else {
        __cur_str = "null";
    }
    if (__cur_str.equals("null") || __cur_str.length() == 0) { this.air_time = null; } else {
      this.air_time = Double.valueOf(__cur_str);
    }

    if (__it.hasNext()) {
        __cur_str = __it.next();
    } else {
        __cur_str = "null";
    }
    if (__cur_str.equals("null")) { this.airline = null; } else {
      this.airline = __cur_str;
    }

    if (__it.hasNext()) {
        __cur_str = __it.next();
    } else {
        __cur_str = "null";
    }
    if (__cur_str.equals("null") || __cur_str.length() == 0) { this.airline_delay = null; } else {
      this.airline_delay = Double.valueOf(__cur_str);
    }

    if (__it.hasNext()) {
        __cur_str = __it.next();
    } else {
        __cur_str = "null";
    }
    if (__cur_str.equals("null") || __cur_str.length() == 0) { this.arrival_delay = null; } else {
      this.arrival_delay = Double.valueOf(__cur_str);
    }

    if (__it.hasNext()) {
        __cur_str = __it.next();
    } else {
        __cur_str = "null";
    }
    if (__cur_str.equals("null") || __cur_str.length() == 0) { this.arrival_time = null; } else {
      this.arrival_time = Integer.valueOf(__cur_str);
    }

    if (__it.hasNext()) {
        __cur_str = __it.next();
    } else {
        __cur_str = "null";
    }
    if (__cur_str.equals("null")) { this.cancellation_reason = null; } else {
      this.cancellation_reason = __cur_str;
    }

    if (__it.hasNext()) {
        __cur_str = __it.next();
    } else {
        __cur_str = "null";
    }
    if (__cur_str.equals("null") || __cur_str.length() == 0) { this.cancelled = null; } else {
      this.cancelled = Integer.valueOf(__cur_str);
    }

    if (__it.hasNext()) {
        __cur_str = __it.next();
    } else {
        __cur_str = "null";
    }
    if (__cur_str.equals("null") || __cur_str.length() == 0) { this.day = null; } else {
      this.day = Integer.valueOf(__cur_str);
    }

    if (__it.hasNext()) {
        __cur_str = __it.next();
    } else {
        __cur_str = "null";
    }
    if (__cur_str.equals("null") || __cur_str.length() == 0) { this.day_of_week = null; } else {
      this.day_of_week = Integer.valueOf(__cur_str);
    }

    if (__it.hasNext()) {
        __cur_str = __it.next();
    } else {
        __cur_str = "null";
    }
    if (__cur_str.equals("null") || __cur_str.length() == 0) { this.departure_delay = null; } else {
      this.departure_delay = Double.valueOf(__cur_str);
    }

    if (__it.hasNext()) {
        __cur_str = __it.next();
    } else {
        __cur_str = "null";
    }
    if (__cur_str.equals("null") || __cur_str.length() == 0) { this.departure_time = null; } else {
      this.departure_time = Integer.valueOf(__cur_str);
    }

    if (__it.hasNext()) {
        __cur_str = __it.next();
    } else {
        __cur_str = "null";
    }
    if (__cur_str.equals("null")) { this.destination_airport = null; } else {
      this.destination_airport = __cur_str;
    }

    if (__it.hasNext()) {
        __cur_str = __it.next();
    } else {
        __cur_str = "null";
    }
    if (__cur_str.equals("null") || __cur_str.length() == 0) { this.distance = null; } else {
      this.distance = Integer.valueOf(__cur_str);
    }

    if (__it.hasNext()) {
        __cur_str = __it.next();
    } else {
        __cur_str = "null";
    }
    if (__cur_str.equals("null") || __cur_str.length() == 0) { this.diverted = null; } else {
      this.diverted = Integer.valueOf(__cur_str);
    }

    if (__it.hasNext()) {
        __cur_str = __it.next();
    } else {
        __cur_str = "null";
    }
    if (__cur_str.equals("null") || __cur_str.length() == 0) { this.elapsed_time = null; } else {
      this.elapsed_time = Double.valueOf(__cur_str);
    }

    if (__it.hasNext()) {
        __cur_str = __it.next();
    } else {
        __cur_str = "null";
    }
    if (__cur_str.equals("null") || __cur_str.length() == 0) { this.flight_number = null; } else {
      this.flight_number = Integer.valueOf(__cur_str);
    }

    if (__it.hasNext()) {
        __cur_str = __it.next();
    } else {
        __cur_str = "null";
    }
    if (__cur_str.equals("null") || __cur_str.length() == 0) { this.id = null; } else {
      this.id = Integer.valueOf(__cur_str);
    }

    if (__it.hasNext()) {
        __cur_str = __it.next();
    } else {
        __cur_str = "null";
    }
    if (__cur_str.equals("null") || __cur_str.length() == 0) { this.late_aircraft_delay = null; } else {
      this.late_aircraft_delay = Double.valueOf(__cur_str);
    }

    if (__it.hasNext()) {
        __cur_str = __it.next();
    } else {
        __cur_str = "null";
    }
    if (__cur_str.equals("null") || __cur_str.length() == 0) { this.month = null; } else {
      this.month = Integer.valueOf(__cur_str);
    }

    if (__it.hasNext()) {
        __cur_str = __it.next();
    } else {
        __cur_str = "null";
    }
    if (__cur_str.equals("null")) { this.origin_airport = null; } else {
      this.origin_airport = __cur_str;
    }

    if (__it.hasNext()) {
        __cur_str = __it.next();
    } else {
        __cur_str = "null";
    }
    if (__cur_str.equals("null") || __cur_str.length() == 0) { this.scheduled_arrival = null; } else {
      this.scheduled_arrival = Integer.valueOf(__cur_str);
    }

    if (__it.hasNext()) {
        __cur_str = __it.next();
    } else {
        __cur_str = "null";
    }
    if (__cur_str.equals("null") || __cur_str.length() == 0) { this.scheduled_departure = null; } else {
      this.scheduled_departure = Integer.valueOf(__cur_str);
    }

    if (__it.hasNext()) {
        __cur_str = __it.next();
    } else {
        __cur_str = "null";
    }
    if (__cur_str.equals("null") || __cur_str.length() == 0) { this.scheduled_time = null; } else {
      this.scheduled_time = Double.valueOf(__cur_str);
    }

    if (__it.hasNext()) {
        __cur_str = __it.next();
    } else {
        __cur_str = "null";
    }
    if (__cur_str.equals("null") || __cur_str.length() == 0) { this.security_delay = null; } else {
      this.security_delay = Double.valueOf(__cur_str);
    }

    if (__it.hasNext()) {
        __cur_str = __it.next();
    } else {
        __cur_str = "null";
    }
    if (__cur_str.equals("null")) { this.tail_number = null; } else {
      this.tail_number = __cur_str;
    }

    if (__it.hasNext()) {
        __cur_str = __it.next();
    } else {
        __cur_str = "null";
    }
    if (__cur_str.equals("null") || __cur_str.length() == 0) { this.taxi_in = null; } else {
      this.taxi_in = Double.valueOf(__cur_str);
    }

    if (__it.hasNext()) {
        __cur_str = __it.next();
    } else {
        __cur_str = "null";
    }
    if (__cur_str.equals("null") || __cur_str.length() == 0) { this.taxi_out = null; } else {
      this.taxi_out = Double.valueOf(__cur_str);
    }

    if (__it.hasNext()) {
        __cur_str = __it.next();
    } else {
        __cur_str = "null";
    }
    if (__cur_str.equals("null") || __cur_str.length() == 0) { this.weather_delay = null; } else {
      this.weather_delay = Double.valueOf(__cur_str);
    }

    if (__it.hasNext()) {
        __cur_str = __it.next();
    } else {
        __cur_str = "null";
    }
    if (__cur_str.equals("null") || __cur_str.length() == 0) { this.wheels_off = null; } else {
      this.wheels_off = Integer.valueOf(__cur_str);
    }

    if (__it.hasNext()) {
        __cur_str = __it.next();
    } else {
        __cur_str = "null";
    }
    if (__cur_str.equals("null") || __cur_str.length() == 0) { this.wheels_on = null; } else {
      this.wheels_on = Integer.valueOf(__cur_str);
    }

    if (__it.hasNext()) {
        __cur_str = __it.next();
    } else {
        __cur_str = "null";
    }
    if (__cur_str.equals("null") || __cur_str.length() == 0) { this.year = null; } else {
      this.year = Integer.valueOf(__cur_str);
    }

    } catch (RuntimeException e) {    throw new RuntimeException("Can't parse input data: '" + __cur_str + "'", e);    }  }

  public Object clone() throws CloneNotSupportedException {
    flights o = (flights) super.clone();
    return o;
  }

  public void clone0(flights o) throws CloneNotSupportedException {
  }

  public Map<String, Object> getFieldMap() {
    Map<String, Object> __sqoop$field_map = new HashMap<String, Object>();
    __sqoop$field_map.put("air_system_delay", this.air_system_delay);
    __sqoop$field_map.put("air_time", this.air_time);
    __sqoop$field_map.put("airline", this.airline);
    __sqoop$field_map.put("airline_delay", this.airline_delay);
    __sqoop$field_map.put("arrival_delay", this.arrival_delay);
    __sqoop$field_map.put("arrival_time", this.arrival_time);
    __sqoop$field_map.put("cancellation_reason", this.cancellation_reason);
    __sqoop$field_map.put("cancelled", this.cancelled);
    __sqoop$field_map.put("day", this.day);
    __sqoop$field_map.put("day_of_week", this.day_of_week);
    __sqoop$field_map.put("departure_delay", this.departure_delay);
    __sqoop$field_map.put("departure_time", this.departure_time);
    __sqoop$field_map.put("destination_airport", this.destination_airport);
    __sqoop$field_map.put("distance", this.distance);
    __sqoop$field_map.put("diverted", this.diverted);
    __sqoop$field_map.put("elapsed_time", this.elapsed_time);
    __sqoop$field_map.put("flight_number", this.flight_number);
    __sqoop$field_map.put("id", this.id);
    __sqoop$field_map.put("late_aircraft_delay", this.late_aircraft_delay);
    __sqoop$field_map.put("month", this.month);
    __sqoop$field_map.put("origin_airport", this.origin_airport);
    __sqoop$field_map.put("scheduled_arrival", this.scheduled_arrival);
    __sqoop$field_map.put("scheduled_departure", this.scheduled_departure);
    __sqoop$field_map.put("scheduled_time", this.scheduled_time);
    __sqoop$field_map.put("security_delay", this.security_delay);
    __sqoop$field_map.put("tail_number", this.tail_number);
    __sqoop$field_map.put("taxi_in", this.taxi_in);
    __sqoop$field_map.put("taxi_out", this.taxi_out);
    __sqoop$field_map.put("weather_delay", this.weather_delay);
    __sqoop$field_map.put("wheels_off", this.wheels_off);
    __sqoop$field_map.put("wheels_on", this.wheels_on);
    __sqoop$field_map.put("year", this.year);
    return __sqoop$field_map;
  }

  public void getFieldMap0(Map<String, Object> __sqoop$field_map) {
    __sqoop$field_map.put("air_system_delay", this.air_system_delay);
    __sqoop$field_map.put("air_time", this.air_time);
    __sqoop$field_map.put("airline", this.airline);
    __sqoop$field_map.put("airline_delay", this.airline_delay);
    __sqoop$field_map.put("arrival_delay", this.arrival_delay);
    __sqoop$field_map.put("arrival_time", this.arrival_time);
    __sqoop$field_map.put("cancellation_reason", this.cancellation_reason);
    __sqoop$field_map.put("cancelled", this.cancelled);
    __sqoop$field_map.put("day", this.day);
    __sqoop$field_map.put("day_of_week", this.day_of_week);
    __sqoop$field_map.put("departure_delay", this.departure_delay);
    __sqoop$field_map.put("departure_time", this.departure_time);
    __sqoop$field_map.put("destination_airport", this.destination_airport);
    __sqoop$field_map.put("distance", this.distance);
    __sqoop$field_map.put("diverted", this.diverted);
    __sqoop$field_map.put("elapsed_time", this.elapsed_time);
    __sqoop$field_map.put("flight_number", this.flight_number);
    __sqoop$field_map.put("id", this.id);
    __sqoop$field_map.put("late_aircraft_delay", this.late_aircraft_delay);
    __sqoop$field_map.put("month", this.month);
    __sqoop$field_map.put("origin_airport", this.origin_airport);
    __sqoop$field_map.put("scheduled_arrival", this.scheduled_arrival);
    __sqoop$field_map.put("scheduled_departure", this.scheduled_departure);
    __sqoop$field_map.put("scheduled_time", this.scheduled_time);
    __sqoop$field_map.put("security_delay", this.security_delay);
    __sqoop$field_map.put("tail_number", this.tail_number);
    __sqoop$field_map.put("taxi_in", this.taxi_in);
    __sqoop$field_map.put("taxi_out", this.taxi_out);
    __sqoop$field_map.put("weather_delay", this.weather_delay);
    __sqoop$field_map.put("wheels_off", this.wheels_off);
    __sqoop$field_map.put("wheels_on", this.wheels_on);
    __sqoop$field_map.put("year", this.year);
  }

  public void setField(String __fieldName, Object __fieldVal) {
    if (!setters.containsKey(__fieldName)) {
      throw new RuntimeException("No such field:"+__fieldName);
    }
    setters.get(__fieldName).setField(__fieldVal);
  }

}
