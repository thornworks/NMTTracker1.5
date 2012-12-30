package nmt;

import java.util.*;
import java.io.*;
import javax.swing.JOptionPane;

public class Team implements Comparable, Serializable {

  private Student[] students;
  private int teamNum; //a 3 digit number that uniquely identifies each team
  private String school;
  private int scoreTeam; //score on team round of contest (0 - 20)
  private char division; //'U' or 'L'

  /**
   * Starts with all students as null objects.
   */
  public Team() {
    students = new Student[5];
    scoreTeam = Student.NOSCORE;
  }

  public Team(Student[] s, int tN, String school, char d) {
    students = s;
    students = new Student[5];
    if(s.length > 5)
        throw new IllegalArgumentException("Error creating team with " + s.length + 
                " students.  A maximum of 5 students is allowed.");
    for (int i = 0; i < s.length; i++) {
      students[i] = s[i];
    }

    teamNum = tN;
    this.school = school;
    division = d;
  }

  public Team(Student[] s, int tN, String school, char d, int tS) {
    students = s;
    if (s.length < 5) {
      students = new Student[5];
      for (int i = 0; i < s.length; i++) {
        students[i] = s[i];
      }
    }
    teamNum = tN;
    this.school = school;
    division = d;
    scoreTeam = tS;
  }

  public Team(int tN, String school, char d) {
    students = new Student[5];
    teamNum = tN;
    this.school = school;
    division = d;
  }

  public Team(Student[] s, int tN, String school, int tS, char d) {
    students = s;
    teamNum = tN;
    this.school = school;
    scoreTeam = tS;
    division = d;
  }
  
//TODO - check to see where this is used, maybe move to public.
   private void writeObject(java.io.ObjectOutputStream out) throws IOException{
    //  System.out.print("C");
      out.writeObject(students[0]);
   //   System.out.print("A");
      out.writeObject(students[1]);
    //  System.out.print("A");
      out.writeObject(students[2]);
    //  System.out.print("A");
      out.writeObject(students[3]);
    //  System.out.print("A");
      out.writeObject(students[4]);
   //   System.out.print("A");
      out.writeObject(new Integer(teamNum));
   //   System.out.print("A");
      out.writeObject(school);
   //   System.out.print("A");
      out.writeObject(new Integer(scoreTeam));
   //   System.out.print("A");
      out.writeObject(new Character(division));
    //  System.out.print("A");
   }
   
   private void readObject(java.io.ObjectInputStream in) throws IOException, ClassNotFoundException{
      students=new Student[5];
      students[0]=(Student)in.readObject();
      //System.out.print("A");
      students[1]=(Student)in.readObject();
      //System.out.print("A");
      students[2]=(Student)in.readObject();
      //System.out.print("A");
      students[3]=(Student)in.readObject();
      //System.out.print("A");
      students[4]=(Student)in.readObject();
      //System.out.print("A");
      teamNum=((Integer)in.readObject()).intValue();
      //System.out.print("A");
      school=(String)in.readObject();
      //System.out.print("A");
      scoreTeam=((Integer)in.readObject()).intValue();
      //System.out.print("A");
      division=((Character)in.readObject()).charValue();
      //System.out.print("A");
   }
   
  /**
   * adds a student to the end of
   * the team list
   *
   * @return true if the student can be added to the team
   * @return false if the team is full
   */
  public boolean addStudent(Student s) {
    for (int x = 0; x < 5; x++) {
      if (students[x] == null) {
        setStudent(x, s);
        return true;
      }
    }
    return false;
  }

  /**
   *  All of the fields are compared between this object and the passed team, t.
   *  Whichever field contains more information is used.  For instance, if
   *  this contains all zeros in the student score fields and t has numbers
   *  in those fields, this.getStudents will be modified to reflect these scores.
   *  The students are compared and should be the same.
   * If this contains no students, then the students in t are added to this Team.
   * @param t Team - should have the same team number as this team.
   * If not, perhaps display an error message.
   */
  public void mergeTeam(Team t) {
    //we only merge if these two teams have the same ID.
    if (this.teamNum != t.teamNum) {
      return;
    }
    //merge the data from two team objects.
    if (!this.getSchool().equalsIgnoreCase(t.getSchool())) {
      this.setSchool(this.getSchool().trim());
      t.setSchool(t.getSchool().trim());
    }
    if (!this.getSchool().equalsIgnoreCase(t.getSchool())) {
      Object[] options = new Object[2];
      options[0] = this.getSchool();
      options[1] = t.getSchool();
      int choice = JOptionPane.showOptionDialog(NMTFrame.mainWindow,
                                                "Attempting to merge team #" +
                                                this.teamNum + ",\n" +
          "but the school names do not agree.  Which school is correct?",
                                                "Database merging conflict",
                                                JOptionPane.DEFAULT_OPTION,
                                                JOptionPane.WARNING_MESSAGE, null,
                                                options, options[0]);
      if (choice == 1) { //use school from the input team
        this.setSchool(t.getSchool());
      }
    }
    if (this.scoreTeam < t.scoreTeam) {
      this.setScore(t.scoreTeam);
    }
    //if (this.scoreTeam > t.scoreTeam)
    //  this.setScore(t.scoreTeam);


    Student[] t1 = this.getStudents();
    Student[] t2 = t.getStudents();

    for (int i = 0; i < 5; i++) {
      if (t1[i] != null && t2[i] != null) {
        t1[i].mergeStudent(t2[i]);
      }
      else if (t1[i] == null) {
        t1[i] = t2[i];
      }
      //otherwise both are null and there is nothing to merge.
    }
  }

  public String toString() {
    StringBuffer students = new StringBuffer(150);
    students.append(new String("Team #" + this.getTeamNum() + " Division:" +
                               this.getDivision() + ", " + this.getSchool() +
                               " with a score of " + this.getScore() + "\n"));
    Student st;
    for (int i = 0; i < 5; i++) {
      st = this.getStudent(i);
      if (st != null) {
        students.append(st.toString() + "\n");
      }
    }
    return students.toString();
  }

  /**
   *   Teams are ordered by the total team score.
   * @param b Object - the other team
   * @return int - 0 if the two teams tied, -1 if this team is higher,
   *             +1 if the Object team (input team) is higher.
   */
  @Override
  public int compareTo(Object b) {
    int teamAScore = this.getTotalScore();
    int teamBScore = ((Team) b).getTotalScore();
    if (teamAScore > teamBScore) {
      return -1;
    }
    else if (teamAScore == teamBScore) {
      return 0;
    }
    else {
      return 1;
    }
  }

  public Student[] getStudents() {
    return students;
  }

  public void setStudents(Student[] s) {
    students = s;
  }

  public void setStudent(int pos, Student s) {
    students[pos] = s;
  }

  public Student getStudent(int pos) {
    return students[pos];
  }

  public int getTeamNum() {
    return teamNum;
  }

  public void setTeamNum(int num) {
    teamNum = num;
  }

  public String getSchool() {
    return school;
  }

  public void setSchool(String s) {
    school = s;
  }

  /**
   * @return int - the score on the team round (0 - 20)
   */
  public int getScore() {
    return scoreTeam;
  }

  public void setScore(int tS) {
    scoreTeam = tS;
  }

  public char getDivision() {
    return division;
  }

  public void setDivision(char c) {
    division = c;
  }

  /**
   *  Computes the total score (all 5 students + getScore() )
   * @return int - the total score for this team.
   */
  public int getTotalScore() {
    int total = scoreTeam * 3;
    for (int x = 0; x < 5; x++) {
      if (students[x] == null) {
        break;
      }
      total += students[x].getScore();
    }
    return total;
  }

  public boolean allDataEntered() {
    return (this.getScore() > 0 && this.completedGL() && this.completedML());
  }

  /**
   *  Determines if the ML fields of the students have been entered.
   * @return boolean - true if at least 2 Mathletics scores have been entered.
   */
  public boolean completedML() {
    int count = 0;
    for (int x = 0; x < 5; x++) {
      if (students[x] == null) {
        break;
      }
      else {
        if (students[x].getScoreML() > 0) {
          count++;
        }
      }
    } //end for
    return (count > 1);
  }

  /**
   *  Determines if the GL (grade level) fields of the students have been entered.
   * @return boolean - true if at least 2 GL scores have been entered.
   */
  public boolean completedGL() {
    int count = 0;
    for (int x = 0; x < 5; x++) {
      if (students[x] == null) {
        break;
      }
      else {
        if (students[x].getScoreGL() > 0) {
          count++;
        }
      }
    } //end for
    return (count > 1);
  }


  public static void ryansTesting() {
    Student s = new Student("Ryan Adams");
    s.setScoreGL(10);
    s.setScoreML(10);
    s.setGrade(12);
    s.setSchool("Bethpage High School");

    Student k = new Student("Russel Kraner");
    k.setScoreGL(8);
    k.setScoreML(8);
    k.setGrade(12);
    k.setSchool("Bethpage High School");

    Student t = new Student("Kevin Sackel");
    t.setScoreGL(9);
    t.setScoreML(9);
    t.setGrade(11);
    t.setSchool("Bethpage High School");

    Student v = new Student("Sara Ong");
    v.setScoreGL(7);
    v.setScoreML(7);
    v.setGrade(12);
    v.setSchool("Bethpage High School");

    Student p = new Student("Andrea Ong");
    p.setScoreGL(7);
    p.setScoreML(7);
    p.setGrade(12);
    p.setSchool("Bethpage High School");

    Student[] studs1 = new Student[4];
    studs1[0] = s;
    studs1[1] = k;
    studs1[2] = t;
    studs1[3] = v;
    Team team1 = new Team(studs1, 102, v.getSchool(), 'U');
    team1.addStudent(p);
    team1.setScore(15);

    Student q = new Student("Ryan Adams");
    q.setGrade(12);
    q.setSchool("Bethpage High School");

    Student w = new Student("Russel Kraner");
    w.setGrade(12);
    w.setSchool("Bethpage High School");

    Student e = new Student("Kevin Sackel");
    e.setGrade(11);
    e.setSchool("Bethpage High School");

    Student r = new Student("Sara Ong");
    r.setGrade(12);
    r.setSchool("BHS");

    Student a = new Student("Andrea Ong");
    a.setGrade(12);
    a.setSchool("BHS");

    Student[] studs2 = new Student[5];
    studs2[0] = q;
    studs2[1] = w;
    studs2[2] = e;
    studs2[3] = r;
    studs2[4] = a;
    Team team2 = new Team(studs2, 102, a.getSchool(), 'U');

    //testing the two Teams:
    System.out.println(team1);
    System.out.println(team2);
    System.out.println("Testing the merge!");
    team2.mergeTeam(team1);
    System.out.println(team2);


  }

  public static void main(String[] args) {
    ryansTesting();
  }

}
