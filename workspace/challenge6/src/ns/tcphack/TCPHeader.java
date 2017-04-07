package ns.tcphack;

import ns.tcphack.headersetting.HeaderSetting;
import ns.tcphack.headersetting.HeaderSettingLong;
import ns.tcphack.headersetting.HeaderSettingInteger;

/**
 * TCP Header
 * Created by dorien.meijercluwen on 27/03/2017.
 */
public class TCPHeader extends AbstractHeader {
  enum Setting {
    SOURCE_PORT,
    DESTINATION_PORT,
    SEQUENCE_NUMBER,
    ACKNOWLEDGMENT_NUMBER,
    DATA_OFFSET,
    RESERVED,
    CONTROL_FLAGS,
    WINDOW_SIZE,
    CHECKSUM,
    URGENT_POINTER;
  }

  /* Set header settings to default */
  private void initialise() {
    addHeaderSetting(new HeaderSettingInteger(Setting.SOURCE_PORT.toString(), 0,16,9292));
    addHeaderSetting(new HeaderSettingInteger(Setting.DESTINATION_PORT.toString(), 16,16,7710));
    addHeaderSetting(new HeaderSettingLong(Setting.SEQUENCE_NUMBER.toString(), 32,32,0));
    addHeaderSetting(new HeaderSettingLong(Setting.ACKNOWLEDGMENT_NUMBER.toString(), 64,32,0));
    addHeaderSetting(new HeaderSettingInteger(Setting.DATA_OFFSET.toString(),96,4,5));
    addHeaderSetting(new HeaderSettingInteger(Setting.RESERVED.toString(),100,4,0));
    addHeaderSetting(new HeaderSettingInteger(Setting.CONTROL_FLAGS.toString(),104,8,0));
    addHeaderSetting(new HeaderSettingInteger(Setting.WINDOW_SIZE.toString(),112,16,4096));
    addHeaderSetting(new HeaderSettingInteger(Setting.CHECKSUM.toString(),128,16,0));
    addHeaderSetting(new HeaderSettingInteger(Setting.URGENT_POINTER.toString(),144,16,0));
  }

  /* Default constructor */
  TCPHeader() {
    super();
    initialise();
  }

  /* Create Packet from int[] */
  public TCPHeader(int[] pkt) {
    this();
    for(int i = 0; i < Setting.values().length; i++) {
      String name = (Setting.values())[i].toString();
      setHeaderSetting(name,pkt);
    }
  }

  /* Set header setting */
  public <T> void setSetting(Setting setting, T value){
    setHeaderSetting(setting.toString(), value);
  }

  /* Get header setting (value)*/
  public <T> T getSetting(Setting setting) {
    return getValue(setting.toString());
  }

  /* Get header setting value as hexstring*/
  public String getSettingAsHexString(Setting setting) {
    HeaderSetting hdrSetting = getHeaderSetting(setting.toString());
    return hdrSetting.getValueAsHexadecimalString();
  }

  public void clearFlags(){
    setHeaderSetting(Setting.CONTROL_FLAGS.toString(),0);
  }

  /* Sets flag if not set yet.*/
  public void setFlag(Flag flag){
    int controlFlags = Flag.setFlag(flag, getFlags());
    setHeaderSetting(Setting.CONTROL_FLAGS.toString(),controlFlags);
  }

  /* Check if given flag is set */
  public boolean isFlagSet(Flag flag) {
    return Flag.isSet(flag, getFlags());
  }

  /* Get all flags */
  public int getFlags() {
    return getValue(Setting.CONTROL_FLAGS.toString());
  }
}
