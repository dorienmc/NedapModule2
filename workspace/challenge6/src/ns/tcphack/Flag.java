package ns.tcphack;

import java.util.Set;

/**
 * Created by dorien.meijercluwen on 27/03/2017.
 */
public enum Flag {
  ACK(16), PUSH(8), RST(4), SYN(2), FIN(1);
  int value;

  Flag(int value) {
    this.value = value;
  }

  public static int setFlag(Flag flag, int controlFlags){
    if(!isSet(flag, controlFlags)) {
      controlFlags += flag.value;
    }
    return controlFlags;
  }

  public static boolean isSet(Flag flag, int controlFlags){
    return (controlFlags & flag.value) > 0;
  }

}
