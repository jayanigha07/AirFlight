// ORM class for table 'airports'
// WARNING: This class is AUTO-GENERATED. Modify at your own risk.
//
// Debug information:
// Generated date: Tue Oct 13 19:50:47 IST 2026
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

public class airports extends SqoopRecord  implements DBWritable, Writable {
  private final int PROTOCOL_VERSION = 3;
  public int getClassFormatVersion() { return PROTOCOL_VERSION; }
  public static interface FieldSetterCommand {    void setField(Object value);  }  protected ResultSet __cur_result_set;
  private Map<String, FieldSetterCommand> setters = new HashMap<String, FieldSetterCommand>();
  private void init0() {
    setters.put("airport_name", new FieldSetterCommand() {
      @Override
      public void setField(Object value) {
        airports.this.airport_name = (String)value;
      }
    });
    setters.put("city", new FieldSetterCommand() {
      @Override
      public void setField(Object value) {
        airports.this.city = (String)value;
      }
    });
    setters.put("country", new FieldSetterCommand() {
      @Override
      public void setField(Object value) {
        airports.this.country = (String)value;
      }
    });
    setters.put("iata_code", new FieldSetterCommand() {
      @Override
      public void setField(Object value) {
        airports.this.iata_code = (String)value;
      }
    });
    setters.put("latitude", new FieldSetterCommand() {
      @Override
      public void setField(Object value) {
        airports.this.latitude = (Double)value;
      }
    });
    setters.put("longitude", new FieldSetterCommand() {
      @Override
      public void setField(Object value) {
        airports.this.longitude = (Double)value;
      }
    });
    setters.put("state", new FieldSetterCommand() {
      @Override
      public void setField(Object value) {
        airports.this.state = (String)value;
      }
    });
  }
  public airports() {
    init0();
  }
  private String airport_name;
  public String get_airport_name() {
    return airport_name;
  }
  public void set_airport_name(String airport_name) {
    this.airport_name = airport_name;
  }
  public airports with_airport_name(String airport_name) {
    this.airport_name = airport_name;
    return this;
  }
  private String city;
  public String get_city() {
    return city;
  }
  public void set_city(String city) {
    this.city = city;
  }
  public airports with_city(String city) {
    this.city = city;
    return this;
  }
  private String country;
  public String get_country() {
    return country;
  }
  public void set_country(String country) {
    this.country = country;
  }
  public airports with_country(String country) {
    this.country = country;
    return this;
  }
  private String iata_code;
  public String get_iata_code() {
    return iata_code;
  }
  public void set_iata_code(String iata_code) {
    this.iata_code = iata_code;
  }
  public airports with_iata_code(String iata_code) {
    this.iata_code = iata_code;
    return this;
  }
  private Double latitude;
  public Double get_latitude() {
    return latitude;
  }
  public void set_latitude(Double latitude) {
    this.latitude = latitude;
  }
  public airports with_latitude(Double latitude) {
    this.latitude = latitude;
    return this;
  }
  private Double longitude;
  public Double get_longitude() {
    return longitude;
  }
  public void set_longitude(Double longitude) {
    this.longitude = longitude;
  }
  public airports with_longitude(Double longitude) {
    this.longitude = longitude;
    return this;
  }
  private String state;
  public String get_state() {
    return state;
  }
  public void set_state(String state) {
    this.state = state;
  }
  public airports with_state(String state) {
    this.state = state;
    return this;
  }
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (!(o instanceof airports)) {
      return false;
    }
    airports that = (airports) o;
    boolean equal = true;
    equal = equal && (this.airport_name == null ? that.airport_name == null : this.airport_name.equals(that.airport_name));
    equal = equal && (this.city == null ? that.city == null : this.city.equals(that.city));
    equal = equal && (this.country == null ? that.country == null : this.country.equals(that.country));
    equal = equal && (this.iata_code == null ? that.iata_code == null : this.iata_code.equals(that.iata_code));
    equal = equal && (this.latitude == null ? that.latitude == null : this.latitude.equals(that.latitude));
    equal = equal && (this.longitude == null ? that.longitude == null : this.longitude.equals(that.longitude));
    equal = equal && (this.state == null ? that.state == null : this.state.equals(that.state));
    return equal;
  }
  public boolean equals0(Object o) {
    if (this == o) {
      return true;
    }
    if (!(o instanceof airports)) {
      return false;
    }
    airports that = (airports) o;
    boolean equal = true;
    equal = equal && (this.airport_name == null ? that.airport_name == null : this.airport_name.equals(that.airport_name));
    equal = equal && (this.city == null ? that.city == null : this.city.equals(that.city));
    equal = equal && (this.country == null ? that.country == null : this.country.equals(that.country));
    equal = equal && (this.iata_code == null ? that.iata_code == null : this.iata_code.equals(that.iata_code));
    equal = equal && (this.latitude == null ? that.latitude == null : this.latitude.equals(that.latitude));
    equal = equal && (this.longitude == null ? that.longitude == null : this.longitude.equals(that.longitude));
    equal = equal && (this.state == null ? that.state == null : this.state.equals(that.state));
    return equal;
  }
  public void readFields(ResultSet __dbResults) throws SQLException {
    this.__cur_result_set = __dbResults;
    this.airport_name = JdbcWritableBridge.readString(1, __dbResults);
    this.city = JdbcWritableBridge.readString(2, __dbResults);
    this.country = JdbcWritableBridge.readString(3, __dbResults);
    this.iata_code = JdbcWritableBridge.readString(4, __dbResults);
    this.latitude = JdbcWritableBridge.readDouble(5, __dbResults);
    this.longitude = JdbcWritableBridge.readDouble(6, __dbResults);
    this.state = JdbcWritableBridge.readString(7, __dbResults);
  }
  public void readFields0(ResultSet __dbResults) throws SQLException {
    this.airport_name = JdbcWritableBridge.readString(1, __dbResults);
    this.city = JdbcWritableBridge.readString(2, __dbResults);
    this.country = JdbcWritableBridge.readString(3, __dbResults);
    this.iata_code = JdbcWritableBridge.readString(4, __dbResults);
    this.latitude = JdbcWritableBridge.readDouble(5, __dbResults);
    this.longitude = JdbcWritableBridge.readDouble(6, __dbResults);
    this.state = JdbcWritableBridge.readString(7, __dbResults);
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
    JdbcWritableBridge.writeString(airport_name, 1 + __off, 12, __dbStmt);
    JdbcWritableBridge.writeString(city, 2 + __off, 12, __dbStmt);
    JdbcWritableBridge.writeString(country, 3 + __off, 12, __dbStmt);
    JdbcWritableBridge.writeString(iata_code, 4 + __off, 12, __dbStmt);
    JdbcWritableBridge.writeDouble(latitude, 5 + __off, 8, __dbStmt);
    JdbcWritableBridge.writeDouble(longitude, 6 + __off, 8, __dbStmt);
    JdbcWritableBridge.writeString(state, 7 + __off, 12, __dbStmt);
    return 7;
  }
  public void write0(PreparedStatement __dbStmt, int __off) throws SQLException {
    JdbcWritableBridge.writeString(airport_name, 1 + __off, 12, __dbStmt);
    JdbcWritableBridge.writeString(city, 2 + __off, 12, __dbStmt);
    JdbcWritableBridge.writeString(country, 3 + __off, 12, __dbStmt);
    JdbcWritableBridge.writeString(iata_code, 4 + __off, 12, __dbStmt);
    JdbcWritableBridge.writeDouble(latitude, 5 + __off, 8, __dbStmt);
    JdbcWritableBridge.writeDouble(longitude, 6 + __off, 8, __dbStmt);
    JdbcWritableBridge.writeString(state, 7 + __off, 12, __dbStmt);
  }
  public void readFields(DataInput __dataIn) throws IOException {
this.readFields0(__dataIn);  }
  public void readFields0(DataInput __dataIn) throws IOException {
    if (__dataIn.readBoolean()) { 
        this.airport_name = null;
    } else {
    this.airport_name = Text.readString(__dataIn);
    }
    if (__dataIn.readBoolean()) { 
        this.city = null;
    } else {
    this.city = Text.readString(__dataIn);
    }
    if (__dataIn.readBoolean()) { 
        this.country = null;
    } else {
    this.country = Text.readString(__dataIn);
    }
    if (__dataIn.readBoolean()) { 
        this.iata_code = null;
    } else {
    this.iata_code = Text.readString(__dataIn);
    }
    if (__dataIn.readBoolean()) { 
        this.latitude = null;
    } else {
    this.latitude = Double.valueOf(__dataIn.readDouble());
    }
    if (__dataIn.readBoolean()) { 
        this.longitude = null;
    } else {
    this.longitude = Double.valueOf(__dataIn.readDouble());
    }
    if (__dataIn.readBoolean()) { 
        this.state = null;
    } else {
    this.state = Text.readString(__dataIn);
    }
  }
  public void write(DataOutput __dataOut) throws IOException {
    if (null == this.airport_name) { 
        __dataOut.writeBoolean(true);
    } else {
        __dataOut.writeBoolean(false);
    Text.writeString(__dataOut, airport_name);
    }
    if (null == this.city) { 
        __dataOut.writeBoolean(true);
    } else {
        __dataOut.writeBoolean(false);
    Text.writeString(__dataOut, city);
    }
    if (null == this.country) { 
        __dataOut.writeBoolean(true);
    } else {
        __dataOut.writeBoolean(false);
    Text.writeString(__dataOut, country);
    }
    if (null == this.iata_code) { 
        __dataOut.writeBoolean(true);
    } else {
        __dataOut.writeBoolean(false);
    Text.writeString(__dataOut, iata_code);
    }
    if (null == this.latitude) { 
        __dataOut.writeBoolean(true);
    } else {
        __dataOut.writeBoolean(false);
    __dataOut.writeDouble(this.latitude);
    }
    if (null == this.longitude) { 
        __dataOut.writeBoolean(true);
    } else {
        __dataOut.writeBoolean(false);
    __dataOut.writeDouble(this.longitude);
    }
    if (null == this.state) { 
        __dataOut.writeBoolean(true);
    } else {
        __dataOut.writeBoolean(false);
    Text.writeString(__dataOut, state);
    }
  }
  public void write0(DataOutput __dataOut) throws IOException {
    if (null == this.airport_name) { 
        __dataOut.writeBoolean(true);
    } else {
        __dataOut.writeBoolean(false);
    Text.writeString(__dataOut, airport_name);
    }
    if (null == this.city) { 
        __dataOut.writeBoolean(true);
    } else {
        __dataOut.writeBoolean(false);
    Text.writeString(__dataOut, city);
    }
    if (null == this.country) { 
        __dataOut.writeBoolean(true);
    } else {
        __dataOut.writeBoolean(false);
    Text.writeString(__dataOut, country);
    }
    if (null == this.iata_code) { 
        __dataOut.writeBoolean(true);
    } else {
        __dataOut.writeBoolean(false);
    Text.writeString(__dataOut, iata_code);
    }
    if (null == this.latitude) { 
        __dataOut.writeBoolean(true);
    } else {
        __dataOut.writeBoolean(false);
    __dataOut.writeDouble(this.latitude);
    }
    if (null == this.longitude) { 
        __dataOut.writeBoolean(true);
    } else {
        __dataOut.writeBoolean(false);
    __dataOut.writeDouble(this.longitude);
    }
    if (null == this.state) { 
        __dataOut.writeBoolean(true);
    } else {
        __dataOut.writeBoolean(false);
    Text.writeString(__dataOut, state);
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
    __sb.append(FieldFormatter.escapeAndEnclose(airport_name==null?"null":airport_name, delimiters));
    __sb.append(fieldDelim);
    __sb.append(FieldFormatter.escapeAndEnclose(city==null?"null":city, delimiters));
    __sb.append(fieldDelim);
    __sb.append(FieldFormatter.escapeAndEnclose(country==null?"null":country, delimiters));
    __sb.append(fieldDelim);
    __sb.append(FieldFormatter.escapeAndEnclose(iata_code==null?"null":iata_code, delimiters));
    __sb.append(fieldDelim);
    __sb.append(FieldFormatter.escapeAndEnclose(latitude==null?"null":"" + latitude, delimiters));
    __sb.append(fieldDelim);
    __sb.append(FieldFormatter.escapeAndEnclose(longitude==null?"null":"" + longitude, delimiters));
    __sb.append(fieldDelim);
    __sb.append(FieldFormatter.escapeAndEnclose(state==null?"null":state, delimiters));
    if (useRecordDelim) {
      __sb.append(delimiters.getLinesTerminatedBy());
    }
    return __sb.toString();
  }
  public void toString0(DelimiterSet delimiters, StringBuilder __sb, char fieldDelim) {
    __sb.append(FieldFormatter.escapeAndEnclose(airport_name==null?"null":airport_name, delimiters));
    __sb.append(fieldDelim);
    __sb.append(FieldFormatter.escapeAndEnclose(city==null?"null":city, delimiters));
    __sb.append(fieldDelim);
    __sb.append(FieldFormatter.escapeAndEnclose(country==null?"null":country, delimiters));
    __sb.append(fieldDelim);
    __sb.append(FieldFormatter.escapeAndEnclose(iata_code==null?"null":iata_code, delimiters));
    __sb.append(fieldDelim);
    __sb.append(FieldFormatter.escapeAndEnclose(latitude==null?"null":"" + latitude, delimiters));
    __sb.append(fieldDelim);
    __sb.append(FieldFormatter.escapeAndEnclose(longitude==null?"null":"" + longitude, delimiters));
    __sb.append(fieldDelim);
    __sb.append(FieldFormatter.escapeAndEnclose(state==null?"null":state, delimiters));
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
    if (__cur_str.equals("null")) { this.airport_name = null; } else {
      this.airport_name = __cur_str;
    }

    if (__it.hasNext()) {
        __cur_str = __it.next();
    } else {
        __cur_str = "null";
    }
    if (__cur_str.equals("null")) { this.city = null; } else {
      this.city = __cur_str;
    }

    if (__it.hasNext()) {
        __cur_str = __it.next();
    } else {
        __cur_str = "null";
    }
    if (__cur_str.equals("null")) { this.country = null; } else {
      this.country = __cur_str;
    }

    if (__it.hasNext()) {
        __cur_str = __it.next();
    } else {
        __cur_str = "null";
    }
    if (__cur_str.equals("null")) { this.iata_code = null; } else {
      this.iata_code = __cur_str;
    }

    if (__it.hasNext()) {
        __cur_str = __it.next();
    } else {
        __cur_str = "null";
    }
    if (__cur_str.equals("null") || __cur_str.length() == 0) { this.latitude = null; } else {
      this.latitude = Double.valueOf(__cur_str);
    }

    if (__it.hasNext()) {
        __cur_str = __it.next();
    } else {
        __cur_str = "null";
    }
    if (__cur_str.equals("null") || __cur_str.length() == 0) { this.longitude = null; } else {
      this.longitude = Double.valueOf(__cur_str);
    }

    if (__it.hasNext()) {
        __cur_str = __it.next();
    } else {
        __cur_str = "null";
    }
    if (__cur_str.equals("null")) { this.state = null; } else {
      this.state = __cur_str;
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
    if (__cur_str.equals("null")) { this.airport_name = null; } else {
      this.airport_name = __cur_str;
    }

    if (__it.hasNext()) {
        __cur_str = __it.next();
    } else {
        __cur_str = "null";
    }
    if (__cur_str.equals("null")) { this.city = null; } else {
      this.city = __cur_str;
    }

    if (__it.hasNext()) {
        __cur_str = __it.next();
    } else {
        __cur_str = "null";
    }
    if (__cur_str.equals("null")) { this.country = null; } else {
      this.country = __cur_str;
    }

    if (__it.hasNext()) {
        __cur_str = __it.next();
    } else {
        __cur_str = "null";
    }
    if (__cur_str.equals("null")) { this.iata_code = null; } else {
      this.iata_code = __cur_str;
    }

    if (__it.hasNext()) {
        __cur_str = __it.next();
    } else {
        __cur_str = "null";
    }
    if (__cur_str.equals("null") || __cur_str.length() == 0) { this.latitude = null; } else {
      this.latitude = Double.valueOf(__cur_str);
    }

    if (__it.hasNext()) {
        __cur_str = __it.next();
    } else {
        __cur_str = "null";
    }
    if (__cur_str.equals("null") || __cur_str.length() == 0) { this.longitude = null; } else {
      this.longitude = Double.valueOf(__cur_str);
    }

    if (__it.hasNext()) {
        __cur_str = __it.next();
    } else {
        __cur_str = "null";
    }
    if (__cur_str.equals("null")) { this.state = null; } else {
      this.state = __cur_str;
    }

    } catch (RuntimeException e) {    throw new RuntimeException("Can't parse input data: '" + __cur_str + "'", e);    }  }

  public Object clone() throws CloneNotSupportedException {
    airports o = (airports) super.clone();
    return o;
  }

  public void clone0(airports o) throws CloneNotSupportedException {
  }

  public Map<String, Object> getFieldMap() {
    Map<String, Object> __sqoop$field_map = new HashMap<String, Object>();
    __sqoop$field_map.put("airport_name", this.airport_name);
    __sqoop$field_map.put("city", this.city);
    __sqoop$field_map.put("country", this.country);
    __sqoop$field_map.put("iata_code", this.iata_code);
    __sqoop$field_map.put("latitude", this.latitude);
    __sqoop$field_map.put("longitude", this.longitude);
    __sqoop$field_map.put("state", this.state);
    return __sqoop$field_map;
  }

  public void getFieldMap0(Map<String, Object> __sqoop$field_map) {
    __sqoop$field_map.put("airport_name", this.airport_name);
    __sqoop$field_map.put("city", this.city);
    __sqoop$field_map.put("country", this.country);
    __sqoop$field_map.put("iata_code", this.iata_code);
    __sqoop$field_map.put("latitude", this.latitude);
    __sqoop$field_map.put("longitude", this.longitude);
    __sqoop$field_map.put("state", this.state);
  }

  public void setField(String __fieldName, Object __fieldVal) {
    if (!setters.containsKey(__fieldName)) {
      throw new RuntimeException("No such field:"+__fieldName);
    }
    setters.get(__fieldName).setField(__fieldVal);
  }

}
