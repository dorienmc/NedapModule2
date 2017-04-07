package ns.tcphack.headersetting;
import ns.tcphack.Utils;

/**
 * Created by dorien.meijercluwen on 06/04/2017.
 */
public class HeaderSettingLong extends HeaderSetting {
  Long longValue; //64 bit int

  public HeaderSettingLong(String name, int startBit, int length, long value){
    super(name,startBit,length,"",Type.Long);
    setValue(value);
  }

  public Long getValue() {
    return longValue;
  }

  @Override
  public void setValue(Object value) {
    if(value instanceof Long) {
      longValue = (Long) value;
      super.setStringValue(Long.toHexString(getValue().longValue()));
    }
  }

  @Override
  public String toString() {
    return String.format("%s = %s, [%d,%d)", getName(), getValue(), getStartingBit(), getEndBit());
  }
}
