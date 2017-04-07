package ns.tcphack;

/**
 * Created by dorien.meijercluwen on 23/03/2017.
 */
public class Test {
  //253 ---> txpkt[6] = 0xFD;
  public static void main(String[] args) {
//    printArr(integerToHex(0x14,1)); //0x14 is 20
//    printArr(integerToHex(9292,2)); //9292 becomes 0x24,0x4C eg. 36 76
//    printArr(integerToHex(7710,2)); //7710 becomes 1E1E, eg. 30 30

    //System.out.println(Utils.parseAddress("fe80::146e:7d1d:5b4:1a24")); //fe80 0000 0000 0000 146e 7d1d 05b4 1a24
//    int[] arr1 = {3,3,3,3,3,3,3,3};
//    int[] arr2 = {1,2,3,4};
//    Utils.copyTo(arr2,arr1,3);
//    printArr(arr1);
//    char space = ' ';
//    char carriage = '\r';
//    char newline = '\n';
//    String[] request = {"GET", "/PT/64/73DAE61D60004FC159276227C9AB902C505D9AE6ACBCD336F6424BD0D7647329","HTTP/1.1"};
//    System.out.println(Utils.textToHexString(request[0]));

//    System.out.println(space + " " + (int)space + Integer.toHexString((int)space));
//    for(char c: request[1].toCharArray()) {
//      System.out.println(c + " " + (int)c + " " + Integer.toHexString((int)c));
//    }
//    System.out.println(space + " " + (int)space + Integer.toHexString((int)space));
//    for(char c: request[2].toCharArray()) {
//      System.out.println(c + " " + (int)c + " " + Integer.toHexString((int)c));
//    }
//    System.out.println(carriage + " " + (int)carriage + " " + Integer.toHexString((int)carriage));
//    System.out.println(newline + " " + (int)newline + " " + Integer.toHexString((int)newline));


    System.out.println(Integer.toBinaryString(Utils.hexStringToHex("9618")));

    System.out.println(Flag.isSet(Flag.SYN, 18));
  }

  private static int[] integerToHex(int number, int nBytes) {
    int[] arr = new int[nBytes];

    String hexString = String.format("%" + (nBytes * 2) + "s", Integer.toHexString(number)).replace(' ', '0');
//    System.out.println(hexString);
    for(int i = 0; i < 2*nBytes; i+=2) {
//      System.out.println(hexString.substring(i,i + 2));
      arr[i/2] = Integer.parseInt(hexString.substring(i,i + 2),16);
    }

    return arr;
  }

  private static void printArr(int[] arr) {
    for (int i=0;i<arr.length;i++) System.out.print(arr[i]+" ");
    System.out.println("\n");
  }

  //txpkt[40] = 0x24;//src port 9292
  //txpkt[41] = 0x4C;//src port 9292


}


/*

Internet Protocol Version 6, Src: 2001:67c:2564:a170:204:23ff:fede:4b2c, Dst: 2001:67c:2564:a125:a190:bba8:525c:aa5f

0000   60 00 00 00 00 18 fd 3e 20 01 06 7c 25 64 a1 70  `......> ..|%d.p
0010   02 04 23 ff fe de 4b 2c 20 01 06 7c 25 64 a1 25  ..#...K, ..|%d.%
0020   a1 90 bb a8 52 5c aa 5f                          ....R\._

Transmission Control Protocol, Src Port: 7710, Dst Port: 9292, Seq: 4172883536, Ack: 2, Len: 0

0000   1e 1e 24 4c f8 b9 26 50 00 00 00 02 60 12 38 40  ..$L..&P....`.8@
0010   59 21 00 00 02 04 05 a0                          Y!......
 */