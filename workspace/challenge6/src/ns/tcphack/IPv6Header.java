package ns.tcphack;

import ns.tcphack.headersetting.*;

/**
 * IPv6 Header
 * Created by dorien.meijercluwen on 27/03/2017.
 */
public class IPv6Header extends AbstractHeader {
  enum Setting {
    VERSION, TRAFFIC_CLASS,FLOW_LABEL,PAYLOAD_LENGTH,
    NEXT_HEADER,HOP_LIMIT,SOURCE_ADDRESS,DESTINATION_ADDRESS
  }

  /* Set header settings to default */
  private void initialise() {
    addHeaderSetting(new HeaderSettingInteger(Setting.VERSION.toString(),0,4,0x6));
    addHeaderSetting(new HeaderSettingInteger(Setting.TRAFFIC_CLASS.toString(),4,8,0));
    addHeaderSetting(new HeaderSettingLong(Setting.FLOW_LABEL.toString(),12,20,0));
    addHeaderSetting(new HeaderSettingLong(Setting.PAYLOAD_LENGTH.toString(),32,16,20));
    addHeaderSetting(new HeaderSettingInteger(Setting.NEXT_HEADER.toString(),48,8,253));
    addHeaderSetting(new HeaderSettingInteger(Setting.HOP_LIMIT.toString(),56,8,255));
    addHeaderSetting(new HeaderSettingHexString(Setting.SOURCE_ADDRESS.toString(),64,128,""));
    addHeaderSetting(new HeaderSettingHexString(Setting.DESTINATION_ADDRESS.toString(),192,128,""));
  }

  /* Default constructor */
  IPv6Header() {
    super();
    initialise();
  }

  /* Create packet, sourceAddress and destAddress must be hexStrings! */
  public IPv6Header(int payload, String sourceAddress, String destAddress) {
    this();
    setSetting(Setting.PAYLOAD_LENGTH,new Integer(payload));
    setSetting(Setting.SOURCE_ADDRESS,Utils.parseAddress(sourceAddress));
    setSetting(Setting.DESTINATION_ADDRESS,Utils.parseAddress(destAddress));
  }

  /* Create Packet from int[] */
  public IPv6Header(int[] pkt) {
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

  public void setPayloadLength(long payloadLength) {
    setSetting(Setting.PAYLOAD_LENGTH,new Long(payloadLength));
  }

}

