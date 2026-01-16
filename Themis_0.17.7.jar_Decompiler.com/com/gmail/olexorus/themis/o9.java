package com.gmail.olexorus.themis;

import java.lang.invoke.MethodHandles;

public enum O9 {
   BUG_REPORT,
   COMMUNITY_GUIDELINES,
   SUPPORT,
   STATUS,
   FEEDBACK,
   COMMUNITY,
   WEBSITE,
   FORUMS,
   NEWS,
   ANNOUNCEMENTS;

   private static final O9[] F;

   private static O9[] q() {
      return new O9[]{BUG_REPORT, COMMUNITY_GUIDELINES, SUPPORT, STATUS, FEEDBACK, COMMUNITY, WEBSITE, FORUMS, NEWS, ANNOUNCEMENTS};
   }

   static {
      long var0 = kt.a(-2202410805477040530L, -2395753392348042967L, MethodHandles.lookup().lookupClass()).a(174240915050390L) ^ 118156357032508L;
      BUG_REPORT = new O9("BUG_REPORT", 0);
      COMMUNITY_GUIDELINES = new O9("COMMUNITY_GUIDELINES", 1);
      SUPPORT = new O9("SUPPORT", 2);
      STATUS = new O9("STATUS", 3);
      FEEDBACK = new O9("FEEDBACK", 4);
      COMMUNITY = new O9("COMMUNITY", 5);
      WEBSITE = new O9("WEBSITE", 6);
      FORUMS = new O9("FORUMS", 7);
      NEWS = new O9("NEWS", 8);
      ANNOUNCEMENTS = new O9("ANNOUNCEMENTS", 9);
      F = q();
   }
}
