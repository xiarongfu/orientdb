package com.orientechnologies.orient.jdbc;

import org.junit.Test;

import java.math.BigDecimal;
import java.sql.Date;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.Statement;
import java.sql.Types;
import java.util.Calendar;
import java.util.TimeZone;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.*;
import static org.junit.Assert.*;
import static org.junit.Assert.assertThat;

public class OrientJdbcResultSetMetaDataTest extends OrientJdbcBaseTest {

  @Test
  public void shouldMapReturnTypes() throws Exception {

    assertThat(conn.isClosed(), is(false));

    Statement stmt = conn.createStatement();
    ResultSet rs = stmt.executeQuery("SELECT stringKey, intKey, text, length, date, score FROM Item");

    //STRING
    assertThat(rs.getString(1), equalTo("1"));
    assertThat(rs.getString("stringKey"), equalTo("1"));
    assertThat(rs.findColumn("stringKey"), equalTo(1));

    //INT
    assertThat(rs.getInt(2), equalTo(1));
    assertThat(rs.getInt("intKey"), equalTo(1));

    assertThat(rs.getString("text").length(), equalTo(rs.getInt("length")));

    //DATE
    Calendar cal = Calendar.getInstance(TimeZone.getTimeZone("UTC"));
    cal.add(Calendar.HOUR_OF_DAY, -1);
    Date date = new Date(cal.getTimeInMillis());
    assertThat(rs.getDate("date").toString(), equalTo(date.toString()));
    assertThat(rs.getDate(5).toString(), equalTo(date.toString()));

    //DECIMAL
    assertThat(rs.getBigDecimal("score"), equalTo(BigDecimal.valueOf(959)));

  }

  @Test
  public void shouldNavigateResultSet() throws Exception {
    assertFalse(conn.isClosed());

    Statement stmt = conn.createStatement();
    ResultSet rs = stmt.executeQuery("SELECT * FROM Item");
    assertThat(rs.getFetchSize(), equalTo(20));

    assertThat(rs.isBeforeFirst(), is(true));

    assertThat(rs.next(), is(true));

    assertThat(rs.getRow(), equalTo(0));

    rs.last();

    assertThat(rs.getRow(), equalTo(19));

    assertThat(rs.next(), is(false));

    rs.afterLast();

    assertThat(rs.next(), is(false));

    rs.close();

    assertThat(rs.isClosed(), is(true));

    stmt.close();

    assertTrue(stmt.isClosed());
  }

  @Test
  public void shouldReturnResultSetAfterExecute() throws Exception {

    assertThat(conn.isClosed(), is(false));

    Statement stmt = conn.createStatement();

    assertThat(stmt.execute("SELECT stringKey, intKey, text, length, date FROM Item"), is(true));
    ResultSet rs = stmt.getResultSet();
    assertThat(rs, is(notNullValue()));
    assertThat(rs.getFetchSize(), equalTo(20));

  }

  @Test
  public void shouldNavigateResultSetByMetadata() throws Exception {
    assertFalse(conn.isClosed());

    Statement stmt = conn.createStatement();
    ResultSet rs = stmt.executeQuery("SELECT stringKey, intKey, text, length, date FROM Item");

    rs.next();
    ResultSetMetaData metaData = rs.getMetaData();
    assertThat(metaData.getColumnCount(), equalTo(5));

    assertThat(metaData.getColumnName(1), equalTo("stringKey"));
    assertThat(rs.getObject(1) instanceof String, is(true));

    assertThat(metaData.getColumnName(2), equalTo("intKey"));

    assertThat(metaData.getColumnName(3), equalTo("text"));
    assertThat(rs.getObject(3) instanceof String, is(true));

    assertThat(metaData.getColumnName(4), equalTo("length"));

    assertThat(metaData.getColumnName(5), equalTo("date"));

  }

  @Test
  public void shouldMapOrientTypesToJavaSQL() throws Exception {
    assertThat(conn.isClosed(), is(false));

    Statement stmt = conn.createStatement();
    ResultSet rs = stmt.executeQuery("SELECT stringKey, intKey, text, length, date, score FROM Item");

    rs.next();
    ResultSetMetaData metaData = rs.getMetaData();

    assertThat(metaData.getColumnCount(), equalTo(6));

    assertThat(metaData.getColumnType(2), equalTo(java.sql.Types.INTEGER));

    assertThat(metaData.getColumnType(3), equalTo(java.sql.Types.VARCHAR));
    assertThat(rs.getObject(3) instanceof String, is(true));

    assertThat(metaData.getColumnType(4), equalTo(java.sql.Types.BIGINT));

    assertThat(metaData.getColumnType(5), equalTo(java.sql.Types.TIMESTAMP));

    assertThat(metaData.getColumnType(6), equalTo(Types.DECIMAL));

    assertThat(metaData.getColumnClassName(1), equalTo(String.class.getName()));
    assertThat(metaData.getColumnType(1), equalTo(java.sql.Types.VARCHAR));

  }

  @Test
  public void shouldFetchMetadataTheSparkStyle() throws Exception {

    //set spark "profile"

    conn.getInfo().setProperty("spark", "true");
    Statement stmt = conn.createStatement();

    ResultSet rs = stmt.executeQuery("select * from (select * from item) where 1=0");

    ResultSetMetaData metaData = rs.getMetaData();

    assertThat(metaData.getColumnName(1)).isEqualTo("stringKey");
    assertThat(metaData.getColumnTypeName(1)).isEqualTo("STRING");
    assertThat(rs.getObject(1)).isInstanceOf(String.class);

  }

}
