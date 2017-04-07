package ns.tcphack.headersetting;

import static org.junit.Assert.*;

import ns.tcphack.Utils;
import org.junit.Before;
import org.junit.Test;

/**
 * Created by dorien.meijercluwen on 06/04/2017.
 */
public class HeaderSettingTest {
  private static String SOURCE_ADDR = Utils.parseAddress("2001:67c:2564:a125:cc2a:4556:4263:b0e2");
  private static String DEST_ADDR = Utils.parseAddress("2001:67c:2564:a170:204:23ff:fede:4b2c");


  @Test
  public void testSomeHeaderSettings() throws Exception{
    testSetting("VERSION",0,4,new Integer(6),"6",
        new Integer(4),"4");
    testSetting("PAYLOAD_LENGTH",32,16,new Long(24),"0018",
        new Long(20),"0014");
    testSetting("SOURCE_PORT",0,16,new Integer(9292),"244c",
        new Integer(7710),"1e1e");
    testSetting("SEQNO",32,32,new Long(4172883536L),
        "f8b92650",new Long(2L),"00000002");
    System.out.println(SOURCE_ADDR);
    testSetting("SOURCE_ADDRESS",64,128,SOURCE_ADDR,SOURCE_ADDR,DEST_ADDR,DEST_ADDR);
  }

  public <T> void testSetting(String name, int startBit, int length, T val,
      String valueAsHex, T newVal, String newValAsHex) throws Exception{
    HeaderSetting setting;
    if(val instanceof Integer){
      setting = new HeaderSettingInteger(name,startBit,length,((Integer)val).intValue());
    } else if(val instanceof Long) {
      setting = new HeaderSettingLong(name,startBit,length,((Long)val).longValue());
    } else {
      setting = new HeaderSettingHexString(name,startBit,length,(String)val);
    }

    getStartingBit(setting,startBit);
    getEndBit(setting,startBit + length);
    getSize(setting,length);
    getName(setting, name);
    getValueAsHexadecimalString(setting,valueAsHex);
    getValue(setting,val);
    setValue(setting,newVal,newValAsHex);
  }


  public void getStartingBit(HeaderSetting setting, int expected) throws Exception {
    assertEquals(expected,setting.getStartingBit());
  }


  public void getEndBit(HeaderSetting setting, int expected) throws Exception {
    assertEquals(expected, setting.getEndBit());
  }


  public void getSize(HeaderSetting setting, int expected) throws Exception {
    assertEquals(expected,setting.getSize());
  }


  public void getName(HeaderSetting setting, String expected) throws Exception {
    assertEquals(expected,setting.getName());
  }


  public void getValueAsHexadecimalString(HeaderSetting setting, String expected) throws Exception {
    assertEquals(expected,setting.getValueAsHexadecimalString());
  }


  public <T> void getValue(HeaderSetting setting, T expected) throws Exception {
    assertEquals(expected,setting.getValue());
  }


  public <T> void setValue(HeaderSetting setting, T newVal, String expectedHex) throws Exception {
    setting.setValue(newVal);
    assertEquals(expectedHex,setting.getValueAsHexadecimalString());
  }



}