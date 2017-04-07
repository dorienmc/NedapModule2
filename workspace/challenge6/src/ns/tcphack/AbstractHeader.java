package ns.tcphack;

import java.util.ArrayList;
import java.util.List;
import ns.tcphack.headersetting.HeaderSetting;

/**
 * Created by dorien.meijercluwen on 27/03/2017.
 */
public abstract class AbstractHeader {
  List<HeaderSetting> headerSettings;

  AbstractHeader() {
    this.headerSettings = new ArrayList<>();
  }

  /* Add header setting*/
  protected void addHeaderSetting(HeaderSetting setting) {
    headerSettings.add(setting);
  }

  /* Set header setting value, if found */
  protected <T> void setHeaderSetting(String name, T value) {
    if(getHeaderSetting(name) != null) {
      getHeaderSetting(name).setValue(value);
    }
  }

  /* Set header setting value from packet, if found */
  protected void setHeaderSetting(String name, int[] pkt) {
    String value = getPacketValueString(getHeaderSetting(name), pkt);
    HeaderSetting setting = getHeaderSetting(name);
    switch (setting.getType()) {
      case Integer: {
        setHeaderSetting(name, Integer.parseInt(value,16));
        break;
      }
      case Long: {
        setHeaderSetting(name, Long.parseLong(value,16));
        break;
      }
      case HexString: {
        setHeaderSetting(name, value);
        break;
      }
    }

  }

  /* Return value of given setting from packet in String */
  protected String getPacketValueString(HeaderSetting headerSetting, int[] packetList) {
    int start = headerSetting.getStartingBit() / 4;
    int end = headerSetting.getEndBit() / 4;
    return Utils.arrayToString(packetList).substring(start, end);
  }

  /* Get header setting by name, returns null if not found */
  protected HeaderSetting getHeaderSetting(String name) {
    for(HeaderSetting setting: headerSettings){
      if(setting.getName().equals(name)){
        return setting;
      }
    }
    return null;
  }

  /* Get value of given headerSetting, return null if not found */
  protected <T> T getValue(String headerName) {
    if(getHeaderSetting(headerName) != null) {
      return getHeaderSetting(headerName).getValue();
    } else {
      return null;
    }
  }

  /* Return starting Bit of given headersetting, return -1 if not found*/
  public int getStartingBit(String headerName) {
    if(getHeaderSetting(headerName) != null) {
      return getHeaderSetting(headerName).getStartingBit();
    } else {
      return -1;
    }
  }

  /* Get size in bytes*/
  public int getSizeInBytes() {
    int size = 0; //size in bits
    for (HeaderSetting headerSetting : headerSettings) {
      size += headerSetting.getSize();
    }
    return size/8;
  }

  public String getHeaderAsString() {
    String result = "";
    for(HeaderSetting setting: headerSettings) {
      result += setting.getValueAsHexadecimalString();
    }
    return result;
  }

  public int[] getHeaderAsArr() {
    return Utils.stringToHexArr(getHeaderAsString());
  }

  @Override
  public String toString() {
    String result = "";
    for(HeaderSetting setting: headerSettings) {
      result += setting;
    }
    return result;
  }
}
