package ns.tcphack.headersetting;
import ns.tcphack.Utils;

/**
 * Created by dorien.meijercluwen on 06/04/2017.
 */
public class HeaderSettingHexString extends HeaderSetting {
  public HeaderSettingHexString(String name, int startBit, int length, String value){
    super(name,startBit,length,value,Type.HexString);
  }

  public String getValue() {
    return super.getValueAsHexadecimalString();
  }

  @Override
  public void setValue(Object value) {
    if (value instanceof String) {
      super.setStringValue((String)value);
    }
  }

  @Override
  public String toString() {
    return String.format("%s = %s, [%d,%d)", getName(), getValue(), getStartingBit(), getEndBit());
  }
}
