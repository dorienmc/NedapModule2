package ns.tcphack.headersetting;
import ns.tcphack.Utils;

/**
 * Created by dorien.meijercluwen on 06/04/2017.
 */
public class HeaderSettingInteger extends HeaderSetting {
  Integer intValue = 0;

  public HeaderSettingInteger(String name, int startBit, int length, int value){
    super(name,startBit,length,"",Type.Integer);
    setValue(new Integer(value));
  }

  @Override
  public Integer getValue() {
    return intValue;
  }

  @Override
  public void setValue(Object value) {
    if (value instanceof Integer) {
      intValue = (Integer) value;
      super.setStringValue(Integer.toHexString(getValue().intValue()));
    }
  }

  @Override
  public String toString() {
    return String.format("%s = %s, [%d,%d)", getName(), getValue(), getStartingBit(), getEndBit());
  }
}
