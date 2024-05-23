package me.flyray.bsin.utils;

import java.security.SecureRandom;
import java.util.HashSet;
import java.util.Set;

public class UniqueInviteCodeGenerator {

  private static final String CHARACTERS = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZ";

  private static SecureRandom random = new SecureRandom();

  public static String generateInviteCode(int codeLength) {
    StringBuilder sb = new StringBuilder();
    for (int i = 0; i < codeLength; i++) {
      int randomIndex = random.nextInt(CHARACTERS.length());
      char randomChar = CHARACTERS.charAt(randomIndex);
      sb.append(randomChar);
    }
    return sb.toString();
  }

  public static String generateUniqueInviteCode(int codeLength) {
    Set<String> inviteCodes = new HashSet<>();
    String inviteCode;
    do {
      inviteCode = generateInviteCode(codeLength);
    } while (!inviteCodes.add(inviteCode));
    return inviteCode;
  }
}
