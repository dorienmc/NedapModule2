package ns.tcphack;

import java.util.Arrays;
import java.util.List;

/**
 * Created by dorien.meijercluwen on 23/03/2017.
 */
public class Packet {
  private IPv6Header ipV6Header;
  private TCPHeader tcpHeader;
  private static final int IPV6_HEADER_SIZE = 40;
  private static final int TCP_HEADER_SIZE = 20;
  private int[] data;

  public Packet(String sourceAddress, String destAddress, int destPort) {
    tcpHeader = new TCPHeader();
    tcpHeader.setSetting(TCPHeader.Setting.DESTINATION_PORT, new Integer(destPort));
    ipV6Header = new IPv6Header(tcpHeader.getSizeInBytes(),sourceAddress, destAddress);
    data = new int[0];
  }

  public Packet(List<Flag> flagsToSet,String sourceAddress, String destAddress, int destPort){
    this(sourceAddress, destAddress, destPort);
    for(Flag flag: flagsToSet) {
      setFlag(flag);
    }
  }

  public Packet(int[] incomingPacket) {
    ipV6Header = new IPv6Header(Arrays.copyOfRange(incomingPacket, 0,IPV6_HEADER_SIZE));
    tcpHeader = new TCPHeader(Arrays.copyOfRange(incomingPacket,IPV6_HEADER_SIZE,IPV6_HEADER_SIZE + TCP_HEADER_SIZE));

    data = Arrays.copyOfRange(incomingPacket,IPV6_HEADER_SIZE + TCP_HEADER_SIZE, incomingPacket.length);
    ipV6Header.setPayloadLength(tcpHeader.getSizeInBytes() + data.length);
  }



  public int[] getPkt() {
    int[] pkt = new int[getSize()];
    Utils.copyTo(ipV6Header.getHeaderAsArr(), pkt, 0); //0 - 39
    Utils.copyTo(tcpHeader.getHeaderAsArr(), pkt, IPV6_HEADER_SIZE); //40 - 59
    Utils.copyTo(data, pkt, IPV6_HEADER_SIZE + TCP_HEADER_SIZE); //60 - end
    return pkt;
  }

  public int getSize() {
    return ipV6Header.getSizeInBytes() + tcpHeader.getSizeInBytes() + data.length;
  }


  public boolean isFlagSet(Flag flag){
    return tcpHeader.isFlagSet(flag);
  }

  public void setFlag(Flag flag) {
    tcpHeader.setFlag(flag);
  }

  public void clearFlags() {
    tcpHeader.clearFlags();
  }

  public int getFlags() {return tcpHeader.getFlags();}



  public long getSeqNumber(){
    Long tmp = tcpHeader.getSetting(TCPHeader.Setting.SEQUENCE_NUMBER);
    return tmp.longValue();
  }

  public long getNextSeqNumber() {return getSeqNumber() + 1;}// + this.data.length;}

  public void setSeqNumber(long number) {
    tcpHeader.setSetting(TCPHeader.Setting.SEQUENCE_NUMBER, new Long(number));
  }

  public long getAckNumber() {
    Long tmp = tcpHeader.getSetting(TCPHeader.Setting.ACKNOWLEDGMENT_NUMBER);
    return tmp.longValue();
  }

  public void setAckNumber(long number) {
    tcpHeader.setSetting(TCPHeader.Setting.ACKNOWLEDGMENT_NUMBER, new Long(number));
  }

  public long getNextAckNumber() {return getAckNumber() + 1;}



  public void setData(int[] data) {
    this.data = data;
    ipV6Header.setPayloadLength(tcpHeader.getSizeInBytes() + data.length);
  }

  public String getData() {
    return Utils.arrayToString(this.data);
  }


  @Override
  public String toString() {
    int[] pkt = getPkt();
    String result = "Packet (" + pkt.length + " bytes):";

    for (int i=0; i < pkt.length ;i++) {
      result += " " + pkt[i];
    }

    result += "\n aka:";
    result += ipV6Header;
    result += tcpHeader;
    result += getData();
    return result;
  }
}
