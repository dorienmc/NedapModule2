package ns.tcphack.headersetting;

import ns.tcphack.Utils;

/**
 * Abstract class for a header setting
 * Created by dorien.meijercluwen on 27/03/2017.
 */
public abstract class HeaderSetting {
  int startBit;
  int lengthInBits;
  String name;
  String value; //Value as hexstring
  public enum Type {HexString, Integer, Long}
  Type type;

  HeaderSetting(String name, int startBit, int length, String value, Type type) {
    this.name = name;
    this.startBit = startBit;
    this.lengthInBits = length;
    this.type = type;
    setStringValue(value);
  }

  public Type getType() {return type;}

  public int getStartingBit(){
    return startBit;
  }

  public int getEndBit() {
    return startBit + lengthInBits;
  }

  /* Get size in bits*/
  public int getSize() {
    return lengthInBits;
  }

  public String getName() {
    return name;
  }

  public String getValueAsHexadecimalString() {
    return value;
  }

  public abstract void setValue(Object value);

  public abstract <T> T getValue();

  void setStringValue(String value) {
    this.value = Utils.padString(value,lengthInBits/4);
  }

  @Override
  public String toString() {
    return String.format("%s = %s, [%d,%d)", getName(), value, getStartingBit(), getEndBit());
  }
}
