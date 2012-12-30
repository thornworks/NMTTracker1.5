package nmt;

import java.io.*;
import java.util.Random;

/**
 *
 * <p>Title: Nassau Math Tournament Tracker</p>
 * <p>Description: This class exists only for testing purporses.
 *     An arbitrarily large data (text) file is generated with names and
 *     schools with random tournament data. </p>
 * <p>Company: NCIML</p>
 * @author La Spina
 * @version 0.9
 */
public class RandomFileGenerator {

  private static String[] firstNames = {
      "Tom", "Bob", "Jill", "Chris", "Kevin", "Pin", "Gagneet", "Sahail",
      "Josh", "Jack", "Todd", "Nicole", "Mathew", "Mark", "Luke", "John",
      "Rachael", "Leah", "Noah", "Jacob", "Esau", "Peter", "Paul", "Jen",
      "Zahira", "Ruchika", "Rachi", "Vera", "Vivian", "Ann", "Elizabeth",
      "Laura", "Vanessa", "Roseanne", "Kathy", "Carmen", "Jon", "Sara"
  };

  private static String[] lastNames;
  private static int nextTeamNum = 101;  //unique 3 digit team number tracker
  static Random randGen = new Random();

  public RandomFileGenerator() {
  }

  /**
   * The field variable array lastNames is filled with 90 names.
   */
  private static void initLastNames() {
    lastNames = new String[90];
    int i = 0;
    lastNames[i++] = "Kraner";
    lastNames[i++] = "Adams";
    lastNames[i++] = "Cao";
    lastNames[i++] = "Choi";
    lastNames[i++] = "Adamo";
    lastNames[i++] = "Alfred";
    lastNames[i++] = "Amato";
    lastNames[i++] = "Ancell";
    lastNames[i++] = "Anderson";
    lastNames[i++] = "Beaton";
    lastNames[i++] = "Bertolami";
    lastNames[i++] = "Bonacore";
    lastNames[i++] = "Braun";
    lastNames[i++] = "Brody";
    lastNames[i++] = "Bullock";
    lastNames[i++] = "Bumb";
    lastNames[i++] = "Cantelmo";
    lastNames[i++] = "Chambers";
    lastNames[i++] = "Crewford";
    lastNames[i++] = "Crowder";
    lastNames[i++] = "Dan";
    lastNames[i++] = "Deale";
    lastNames[i++] = "Dellaquilla";
    lastNames[i++] = "Devery";
    lastNames[i++] = "Durdaller";
    lastNames[i++] = "Ernest";
    lastNames[i++] = "Fazio";
    lastNames[i++] = "Gascott";
    lastNames[i++] = "Gee";
    lastNames[i++] = "Geena";
    lastNames[i++] = "Graziadei";
    lastNames[i++] = "Groth";
    lastNames[i++] = "Gustavson";
    lastNames[i++] = "Hamlet";
    lastNames[i++] = "Harris";
    lastNames[i++] = "Helms";
    lastNames[i++] = "Hendriks";
    lastNames[i++] = "Heyward";
    lastNames[i++] = "Hunninghaus";
    lastNames[i++] = "Ishmael";
    lastNames[i++] = "Johnson";
    lastNames[i++] = "Kirshburger";
    lastNames[i++] = "Kister";
    lastNames[i++] = "Kozlik";
    lastNames[i++] = "Lane";
    lastNames[i++] = "Lee";
    lastNames[i++] = "Liva";
    lastNames[i++] = "Martinez";
    lastNames[i++] = "Matthews";
    lastNames[i++] = "McCristall";
    lastNames[i++] = "McLemore";
    lastNames[i++] = "Melton";
    lastNames[i++] = "Millen";
    lastNames[i++] = "Mitchell";
    lastNames[i++] = "Montgomery";
    lastNames[i++] = "Morales";
    lastNames[i++] = "Mueller";
    lastNames[i++] = "Nappi";
    lastNames[i++] = "Novell";
    lastNames[i++] = "Noya";
    lastNames[i++] = "Osborne";
    lastNames[i++] = "Otero";
    lastNames[i++] = "Palmer";
    lastNames[i++] = "Pfeiffer";
    lastNames[i++] = "Rani";
    lastNames[i++] = "Renner";
    lastNames[i++] = "Ritzman";
    lastNames[i++] = "Roberts";
    lastNames[i++] = "Ross";
    lastNames[i++] = "Santella";
    lastNames[i++] = "Scavuzzo";
    lastNames[i++] = "Scott";
    lastNames[i++] = "Shehab";
    lastNames[i++] = "Silver";
    lastNames[i++] = "Solomon";
    lastNames[i++] = "Spratt";
    lastNames[i++] = "Tierney";
    lastNames[i++] = "Tuttle";
    lastNames[i++] = "Vandett";
    lastNames[i++] = "Vella";
    lastNames[i++] = "Walker";
    lastNames[i++] = "Widhalm";
    lastNames[i++] = "Williams";
    lastNames[i++] = "Wintz";
    lastNames[i++] = "Woelhoff";
    lastNames[i++] = "Wolf";
    lastNames[i++] = "Wood";
    lastNames[i++] = "Wu";
    lastNames[i++] = "Zampani";
    lastNames[i++] = "Zikini";
  }

  private static String randomName() {
     int b = randGen.nextInt(lastNames.length);
     int a = randGen.nextInt(firstNames.length);
     return firstNames[a] + " " + lastNames[b];
  }

  /**
   *  Generates a random team with students and school
   * @param id int - a 3 digit ID number that must be unique for the data file.
   * @param level char, either 'U' or 'L'
   * @return String with 6 lines of text, a team line followed by 5 students
   */
  private static String makeTeam(int id, char level) {
    StringBuffer entry = new StringBuffer(200);
    char letter1 = (char) ('A' + randGen.nextInt(26));
    char letter2 = (char) ('A' + randGen.nextInt(26));
    char letter3 = (char) ('A' + randGen.nextInt(26));
    String schoolName = "," + letter1 + letter2 + letter3 +
           randGen.nextInt(100) + " High School";
    
    entry.append("," + id);
    entry.append("," + level);
    
    
    entry.append("," + randomScore(20,10));       
    entry.append(schoolName+",");
    entry.append("\n");
    int grade = 0;
    if(level=='U')
      grade = 11;
    if(level=='L')
      grade = 9;
      
    for(int i=0; i<5; i++) {
      entry.append(makeStudent(grade + randGen.nextInt(2)));
    }
    return entry.toString();
  }

  /**
   *  Generates a random students entry
   * @param grade int - 9 to 12
   * @return String with a line break at the end
   */
  private static String makeStudent(int grade) {
    int individualRound = randomScore(15,7);
    int mathletics = randomScore(10,4);
    return ","+randomName() + "," + grade + "," + 
           individualRound + "," + mathletics + ",\n";
  }

/**
 *  In order to make the data more realistic, a normal distribution
 *  is used to generate the random score values.
 *  @returns a random score value from 0 to max, normally distrubuted
 *  with a mean of avg.  The Standard Deviation is set to max/6
 */
  private static int randomScore(int max, int avg) {
    double gaussianDist = randGen.nextGaussian();
    //mean 0.0 and standard deviation 1.0
    gaussianDist *= (max/6.0);  //now the S.D. = max/6
    gaussianDist += avg; //now the mean shifts from 0 to avg.
    int score = (int) (gaussianDist + 0.5); //round it
    if(score<0) 
      score = 0;
    if(score>max) 
      score = max;
    return score;
  }
  
  public static void main(String[] arg) {
    //randGen = new Random();
    initLastNames();
    char division = 'U';
    PrintWriter out=null; 
    try {
    out = new PrintWriter(
                          new FileOutputStream("data.txt"));
    }
    catch(IOException ioe) {
      ioe.printStackTrace();
      System.exit(0);
    }

    for(int j=0; j<125; j++) {
      if(j%5 == 0) {
         if(division=='U')
            division = 'L';
         else
            division = 'U'; 
      }
      out.print(makeTeam(nextTeamNum++, division));
    }
    out.close();
  } //end of main
}
