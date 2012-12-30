package nmt;
import java.util.*;

/**
 * <p>Title: Nassau Math Tournament Tracker</p>
 *
 * <p>Description: Used for imposing some other ordering on students.
 *    The Student class is naturally ordered by total score.
 *    We may also want to sort student objects based on name, school,
 *    Mathletics Score, or Grade Level Score.
 *
 *   These latter two options would work well for computing the median score
 *   of these tests.</p>
 *
 * <p>Copyright: Copyright (c) 2007</p>
 *
 * <p>Company: NCIML</p>
 *
 * @author Leon La Spina
 * @version 1.0
 */
public class StudentComparator implements Comparator<Student> {

  private char sortCode; //either 'M', 'G', 'T', or 'A'
  // for mathletics, grade level, total, alphabetical.

  /**
   * Builds a comparator, a way of comparing (for sorting) students that
   * will either compare students based on Grade level, Mathletics, Total or Alpha.
   * @param code char - either 'M', 'G', T', or 'A'
   */
  public StudentComparator(char sortCode) {
    this.sortCode = sortCode;
    //if the input was not valid, we use the default sorting by total score.
    if (sortCode != 'M' && sortCode != 'G' && sortCode != 'T' &&
        sortCode != 'A') {
      sortCode = 'T';
    }
  }

  /**
   * Compares two students according to the sortCode set by the constructor:
   * Either by Mathletics Score, Grade Level Score, total score.
   * @param a Student
   * @param b Student
   * @return int
   */
  public int compare(Student a, Student b) {
    int studAScore; // = a.getScore();
    int studBScore; // = b.getScore();
    //normal sorting is based on total score
    if (sortCode == 'T') {
      return a.compareTo(b);
    }
    
    if (sortCode == 'M') {
      studAScore = a.getScoreML();
      studBScore = b.getScoreML();
    }
    else if (sortCode == 'G') {
      studAScore = a.getScoreGL();
      studBScore = b.getScoreGL();
    }
    else { //if(sortCode=='A')
      studAScore = 0; //ignore the scores if we want alphabetical sorting
      studBScore = 0;
    }

    if (studAScore > studBScore) {
      return -1;
    }
    else if (studAScore == studBScore) {
      if (a.getName().compareTo(((Student) b).getName()) == 0) {
        return a.getSchool().compareTo(b.getSchool());
      }
      else {
        return a.getName().compareTo(b.getName());
      }
    }
    else {
      return 1;
    }
  }
}
