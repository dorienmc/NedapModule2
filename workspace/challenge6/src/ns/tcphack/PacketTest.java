package ns.tcphack;

import static org.junit.Assert.*;

import java.util.ArrayList;
import org.junit.*;
import org.junit.Test;

/**
 * Created by dorien.meijercluwen on 06/04/2017.
 */
public class PacketTest {
  Packet packet;
  private static String SOURCE_ADDR = "2001:67c:2564:a170:204:23ff:fede:4b2c";
  private static String DEST_ADDR = "2001:67c:2564:a125:a190:bba8:525c:aa5f";
  public static final int DEST_PORT = 7710;
  private static String STUDENT_NUMBER = "s0166367";

  @Before
  public void setUp() throws Exception {
    packet = new Packet(SOURCE_ADDR,DEST_ADDR,DEST_PORT);
  }

  @Test
  public void testSetUp() throws Exception {
    //Default packet has no data
    assertEquals("",packet.getData());
    //Has size 60
    assertEquals(60,packet.getSize());
    //Has no flags set
    assertEquals(0,packet.getFlags());
  }

  @Test
  public void isFlagSet() throws Exception {
    packet.setFlag(Flag.ACK);
    assertEquals(true,packet.isFlagSet(Flag.ACK));
    assertEquals(false,packet.isFlagSet(Flag.SYN));

    packet.setFlag(Flag.SYN);
    assertEquals(true,packet.isFlagSet(Flag.SYN));
  }

  @Test
  public void setFlag() throws Exception {
    assertEquals(false,packet.isFlagSet(Flag.SYN));

    packet.setFlag(Flag.SYN);
    assertEquals(true,packet.isFlagSet(Flag.SYN));
  }

  @Test
  public void clearFlags() throws Exception {
    packet.setFlag(Flag.ACK);
    packet.setFlag(Flag.SYN);
    assertEquals(true,packet.isFlagSet(Flag.ACK));
    assertEquals(true,packet.isFlagSet(Flag.SYN));

    packet.clearFlags();
    assertEquals(0,packet.getFlags());
  }

  @Test
  public void getFlags() throws Exception {
    packet.setFlag(Flag.ACK);
    packet.setFlag(Flag.SYN);
    assertEquals(Flag.ACK.value + Flag.SYN.value, packet.getFlags());
  }

  @Test
  public void seqNumber() throws Exception {
    Long seqNumber = new Long(3418330832L);
    assertEquals(0,packet.getSeqNumber());
    packet.setSeqNumber(seqNumber.longValue());
    assertEquals(seqNumber.longValue(),packet.getSeqNumber());
  }

  @Test
  public void getNextSeqNumber() throws Exception {
    Long seqNumber = new Long(3418330832L);
    packet.setSeqNumber(seqNumber.longValue());
    assertEquals(seqNumber.longValue() + 1, packet.getNextSeqNumber());
  }



  @Test
  public void ackNumber() throws Exception {
    Long ackNumber = new Long(3118431762L);
    assertEquals(0,packet.getAckNumber());
    packet.setAckNumber(ackNumber.longValue());
    assertEquals(ackNumber.longValue(),packet.getAckNumber());
  }

  @Test
  public void setGetData() throws Exception {
    packet.setData(Utils.textToHexArr("Test"));
    assertEquals(Utils.textToHexString("Test"),packet.getData());
  }

  //Test for first packet.
  @Test
  public void firstPacket() throws Exception {
    int[] data = Utils.textToHexArr("Test");
    packet.setFlag(Flag.SYN);
    packet.setData(data);

    assertEquals(Flag.SYN.value,packet.getFlags());
    assertEquals(0,packet.getSeqNumber());
    assertEquals(1,packet.getNextSeqNumber());
    assertEquals(0,packet.getAckNumber());
    assertEquals(Utils.arrayToString(data),packet.getData());
    assertEquals(60 + data.length, packet.getSize());

    //Build expected packet
    String ipv6Header = "6" +"00" + Utils.HexToString(0,20/4)
        + Utils.HexToString(24,16/4) + "fd" + "ff"
        + Utils.parseAddress(SOURCE_ADDR) + Utils.parseAddress(DEST_ADDR);
    String tcpHeader = "244c" + "1e1e" + "00000000" + "00000000" + "50"
        + Utils.HexToString(Flag.SYN.value,2) + "1000" + "0000" + "0000";
    String dataAsString = Utils.arrayToString(data);
    assertEquals(ipv6Header + tcpHeader + dataAsString, Utils.arrayToString(packet.getPkt()));

    int pkt[] = Utils.stringToHexArr(ipv6Header + tcpHeader + dataAsString);
    assertArrayEquals(pkt,packet.getPkt());
  }

  //Test for converting received SYN+ACK packet
  @Test
  public void firstReceivedPacket() throws Exception {
    int[] receivedPacket = Utils.stringToHexArr("600000000018fd3e2001067c2564a170"
        + "020423fffede4b2c2001067c2564a125a190bba8525caa5f"
        + "1e1e244cf8b92650000000016012384059210000020405a0");
    packet = new Packet(receivedPacket);
    String expectedData = "020405a0";
    long expectedSeqNumber = 4172883536L;

    assertEquals(expectedData,packet.getData());
    assertEquals(60 + expectedData.length()/2,packet.getSize());
    assertEquals(Flag.SYN.value + Flag.ACK.value, packet.getFlags());
    assertEquals(expectedSeqNumber,packet.getSeqNumber());
    assertEquals(expectedSeqNumber + 1, packet.getNextSeqNumber());
    assertEquals(1,packet.getAckNumber());

    assertArrayEquals(receivedPacket,packet.getPkt());
  }

  //Test for HTTP get packet
  @Test
  public void httpGetPacket() throws Exception {
    int[] receivedData = Utils.stringToHexArr("600000000018fd3e2001067c2564a170"
        + "020423fffede4b2c2001067c2564a125a190bba8525caa5f"
        + "1e1e244cf8b92650000000026012384059210000020405a0");
    Packet receivedPacket = new Packet(receivedData);

    packet.setSeqNumber(receivedPacket.getAckNumber());
    packet.setAckNumber(receivedPacket.getSeqNumber());
    packet.setFlag(Flag.ACK);
    String requestURI = String.format("http://[%s]:%d/%s", DEST_ADDR, DEST_PORT, STUDENT_NUMBER);
    int [] getRequestData = createGETrequest(requestURI);
    packet.setData(getRequestData);

    assertEquals(Utils.arrayToString(getRequestData),packet.getData());
    assertEquals(60 + getRequestData.length, packet.getSize());
    assertEquals(Flag.ACK.value, packet.getFlags());
    assertEquals(2,packet.getSeqNumber());
    assertEquals(3,packet.getNextSeqNumber());
    assertEquals(4172883536L + 1, packet.getNextAckNumber());

    //Build expected packet
    String ipv6Header = "6" +"00" + Utils.HexToString(0,20/4)
        + Utils.HexToString(20 + getRequestData.length,16/4) + "fd" + "ff"
        + Utils.parseAddress(SOURCE_ADDR) + Utils.parseAddress(DEST_ADDR);
    String tcpHeader = "244c" + "1e1e" + Utils.HexToString(2,8) + Utils.HexToString(4172883536L,8) + "50"
        + Utils.HexToString(Flag.ACK.value,2) + "1000" + "0000" + "0000";
    String dataAsString = Utils.arrayToString(getRequestData);
    //Same ipv6Header
    assertEquals(ipv6Header , Utils.arrayToString(packet.getPkt()).substring(0,ipv6Header.length()));
    //Same tcp header
    assertEquals(tcpHeader , Utils.arrayToString(packet.getPkt()).substring(ipv6Header.length(),ipv6Header.length() + tcpHeader.length()));
    //Same data
    assertEquals(dataAsString , Utils.arrayToString(packet.getPkt()).substring(ipv6Header.length() + tcpHeader.length(),
        ipv6Header.length() + tcpHeader.length() + dataAsString.length()));


  }

  /* Create get request */
  private int[] createGETrequest(String Request_URI) {
    String requestLine = Utils.textToHexString("GET") + "20" + Utils.textToHexString(Request_URI)
        + "20" + Utils.textToHexString("HTTP/1.1") + "0d0a";
    return Utils.stringToHexArr(requestLine);
  }

  private static void printArr(int[] arr) {
    for (int i=0;i<arr.length;i++) System.out.print(arr[i]+" ");
    System.out.println("\n");
  }



}