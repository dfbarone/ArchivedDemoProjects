package com.squarespace.interview.visitors;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

class AnalyzeVisitorsSolution {

  // Time elapsed before a session should be renewed.
  private static final int INACTIVE_TIME_IN_SECONDS = 1800;

  /**
   * Given a {@link Iterable} of website hit data modeled as {@link WebsiteVisit} analyze it and return the
   * resulting page views, unique visitors, and sessions.
   *
   * @param websiteVisits Input hit data.
   * @return A 3-element array of long corresponding to 0: page views, 1: unique visitors, 2: view sessions
   */
  @SuppressWarnings("UnusedParameters")
  static long[] processPageViews(Iterable<WebsiteVisit> websiteVisits) {
    // Initialize return values. 0: visits, 1: unique visits, 2: sessions
    long[] pageViews = new long[]{0, 0, 0};

    // We will store visitor id and latest timestamp for book keeping.
    Map<String, Long> uniqueVisitorSessionMap = new HashMap<>();
    for (Iterator it = websiteVisits.iterator(); it.hasNext(); ) {
      WebsiteVisit websiteVisit = (WebsiteVisit) it.next();

      // If this is a pre-existing user, check for a new view session.
      if (uniqueVisitorSessionMap.containsKey(websiteVisit.getVisitorId())) {
        Long oldVisitorTimestamp = uniqueVisitorSessionMap.get(websiteVisit.getVisitorId());
        if ((websiteVisit.getTimestamp() - oldVisitorTimestamp) > INACTIVE_TIME_IN_SECONDS) {
          // If a view session was inactive, add a new view session.
          pageViews[2]++;
        }
        // Update timestamp in uniqueVisitorSessionMap. The timestamp will
        // always be the newer than the previous timestamp.
        uniqueVisitorSessionMap.replace(websiteVisit.getVisitorId(), websiteVisit.getTimestamp());
      } else {
        // If this is a new visitor add to uniqueVisitorSessionMap.
        uniqueVisitorSessionMap.put(websiteVisit.getVisitorId(), websiteVisit.getTimestamp());
      }

      // Increment page views
      pageViews[0]++;
    }

    // Set unique page views
    pageViews[1] = uniqueVisitorSessionMap.keySet().size();

    // Add initial view sessions (unique page views) with renewed view sessions.
    // This gives us the total number of sessions.
    pageViews[2] += uniqueVisitorSessionMap.keySet().size();

    //listPermutations("abc");
    permute("abc", 0, 2);

    return pageViews;
  }

  public static void permutationsWithPrefix(String thePrefix, String theString) {
  if ( theString.length() > 0 ) System.out.println(thePrefix + theString);
  for(int i = 0; i < theString.length(); i ++ ) {
    char c = theString.charAt(i);
    String workingOn = theString.substring(0, i) + theString.substring(i+1);
    permutationsWithPrefix(thePrefix + c, workingOn);
  }
}

  public static void listPermutations(String theString) {
    permutationsWithPrefix("", theString);
  }

  /**
   * permutation function
   * @param str string to calculate permutation for
   * @param l starting index
   * @param r end index
   */
  private static void permute(String str, int l, int r)
  {
    if (l == r)
      System.out.println(str);
    else
    {
      for (int i = l; i <= r; i++)
      {
        str = swap(str,l,i);
        permute(str, l+1, r);
        str = swap(str,l,i);
      }
    }
  }

  /**
   * Swap Characters at position
   * @param a string value
   * @param i position 1
   * @param j position 2
   * @return swapped string
   */
  public static String swap(String a, int i, int j)
  {
    char temp;
    char[] charArray = a.toCharArray();
    temp = charArray[i] ;
    charArray[i] = charArray[j];
    charArray[j] = temp;
    return String.valueOf(charArray);
  }
}
