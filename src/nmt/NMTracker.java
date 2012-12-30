package nmt;

import java.util.*;
import java.io.*;

/**
 * NMTracker.java provides functionality to the list of students and teams. Its
 * constructor generates a TreeMap for each division of Teams (Upper and Lower)
 * and then adds the students to their appropriate grade set. An NMTracker can
 * calculate averages, modes, retrieve the top 20 individuals from a grade or
 * top 20 teams from a division.
 */

//TODO - create a method that extracts the Student[] array and then calls
//the average and SD methods.
public class NMTracker implements Serializable {

    private static final long serialVersionUID = 2010L;
    TreeMap<Integer, Team> lowerDivisionTeams;
    TreeMap<Integer, Team> upperDivisionTeams;
    private int[] studentCount;    // only positions 9-12 are used.
    public static int TOPSCORERS_LENGTH = 20;

    /*
     *  Creates the two treeMaps that will be used to store upper and lower division teams.
     */
    public NMTracker() {
        lowerDivisionTeams = new TreeMap();
        upperDivisionTeams = new TreeMap();
        studentCount = new int[13];  //initialized to 0
    }

    /*
     * Constructor creates an NMTracker object, initializes local variables, and
     * adds the students from the teams in data to their appropriate grade Set
     *
     * @param data ArrayList<Team> - list of Teams.
     */
    public NMTracker(ArrayList<Team> data) {
        lowerDivisionTeams = new TreeMap();
        upperDivisionTeams = new TreeMap();
        studentCount = new int[13];  //initialized to 0
        for (int i = 0; i < data.size(); i++) {
            addTeam( data.get(i));
        }
    }

    // specifies how to send and recieve data
    private void writeObject(java.io.ObjectOutputStream out) throws IOException {
        out.writeInt(upperDivisionTeams.size() + lowerDivisionTeams.size());
        Set<Integer> lowerSet = lowerDivisionTeams.keySet();// A set of team
        // numbers
        // iterate through the maps
        for (Integer i : lowerSet) {
            out.writeObject(lowerDivisionTeams.get(i));
        }
        Set<Integer> upperSet = upperDivisionTeams.keySet();// A set of team
        // numbers


        // iterate through the maps
        for (Integer i : upperSet) {
            out.writeObject(upperDivisionTeams.get(i));
        }
    }

    private void readObject(java.io.ObjectInputStream in) throws IOException,
            ClassNotFoundException {
        int numTeams = in.readInt();
        for (int x = 0; x < numTeams; x++) {
            addTeam((Team) in.readObject());
        }
    }

    /**
     *
     * Takes the student, S, to be added to a specific team with teamNum finds
     * the student's grade, adds the student to the appropriate division gets the
     * student's team via teamNum, and adds the student
     *
     * @param teamNum
     *           int - the 3 digit team number
     * @param s
     *           Student - the student to be added to this team.
     * @throws Exception
     *            if there are already 5 students on the specified team.
     */
    public void addStudent(int teamNum, Student s) throws Exception {
        if (s.getGrade() < 11) {
            Team t = (Team) lowerDivisionTeams.get(new Integer(teamNum));
            if (!t.addStudent(s)) {
                throw new Exception("Team Size Exception: No Room on Team");
            }
        } else {
            Team t = (Team) upperDivisionTeams.get(new Integer(teamNum));
            if (!t.addStudent(s)) {
                throw new Exception("Team Size Exception: No Room on Team");
            }
        }
    }

    /**
     * Adds team, T, to an upper or lower division according to grade level
     *
     * @param t
     *           Team - Team to be added
     */
    public void addTeam(Team t) {
        if (t.getDivision() == 'U') {
            upperDivisionTeams.put(new Integer(t.getTeamNum()), t);
        } else if (t.getDivision() == 'L') {
            lowerDivisionTeams.put(new Integer(t.getTeamNum()), t);
        }
    }

    /**
     * removes a team and the students from the database.
     *
     * @returns true if the team was found and removed. A return value of false
     *          indicates that the team was not found in the database.
     */
    public boolean removeTeam(int teamNum) {
        Team t = null;
        if (containsTeamNum('U', teamNum)) {
            t = upperDivisionTeams.remove(new Integer(teamNum));
        } else if (containsTeamNum('L', teamNum)) {
            t = lowerDivisionTeams.remove(new Integer(teamNum));
        } else // team not found!
        {
            return false;
        }
        return true;
    }

    /*
     * Checks a division's list of keys for n, returns true if n is already a key
     *
     * @param division char - the division to be checked ('U'=Upper, 'L'=Lower)
     * @param n int - The team number to be checked
     */
    public boolean containsTeamNum(char division, int n) {
        if (division == 'U') {
            return upperDivisionTeams.keySet().contains(new Integer(n));
        }
        if (division == 'L') {
            return lowerDivisionTeams.keySet().contains(new Integer(n));
        }
        return false;
    }

    /*
     * Removes a team using its number n
     *
     * @param n int -- the team number @return Team -- the team removed
     */
    public Team getTeam(int n) {
        Team t = null;
        if (n >= 200) {
            t = upperDivisionTeams.get(new Integer(n));
        }
        //if we didn't find it in upper division teams, check lower division.
        if (t == null) {
            t = lowerDivisionTeams.get(new Integer(n));
        }
        return t;
    }

    /**
     * Gets the list of team numbers from a division
     *
     * @param division
     *           char - the division to be checked ('U'=Upper, 'L'=Lower)
     */
    public ArrayList<Integer> getListTeamNum(char division) {
        ArrayList<Integer> teamNum = new ArrayList();
        Set<Integer> keys;
        if (division == 'U') {
            keys = upperDivisionTeams.keySet();
        } else {
            keys = lowerDivisionTeams.keySet();
        }

        Iterator<Integer> iter = keys.iterator();
        while (iter.hasNext()) {
            teamNum.add(iter.next());
        }
        return teamNum;
    }

    public ArrayList<Integer> getTeamsNoScore() {
        ArrayList<Integer> teams = new ArrayList<Integer>();

        for (Integer n : upperDivisionTeams.keySet()) {
            Team t = upperDivisionTeams.get(n);
            if (!t.completedGL() && !t.completedML() && !t.allDataEntered()) {
                teams.add(t.getTeamNum());
            }
        }

        for (Integer n : lowerDivisionTeams.keySet()) {
            Team t = lowerDivisionTeams.get(n);
            if (!t.completedGL() && !t.completedML() && !t.allDataEntered()) {
                teams.add(t.getTeamNum());
            }
        }

        /*while(upperDivisionTeams.keySet().iterator().hasNext())
        teams.add(upperDivisionTeams.keySet().iterator().next());

        while(lowerDivisionTeams.keySet().iterator().hasNext())
        teams.add(lowerDivisionTeams.keySet().iterator().next());*/

        return teams;
    }

    public int numTeams() {
        return upperDivisionTeams.keySet().size()
                + lowerDivisionTeams.keySet().size();
    }

    /**
     *
     *   Do we even need this method???
     * @return the estimated number of students, assuming 5 students per team.
     */
    public int numStudents() {
        return numTeams() * 5;
    }

    /**
     *
     *  Returns a count of the number of students in the given grade.
     *  This method relies on the studentCount array being up to date.
     *  It is not updated by adding or deleting students, but only by
     *  computing statistcs on students (median, average, etc)
     */
    public int numStudentsByGrade(int g) {
        if (g < 9 || g > 12) {
            return 0;
        }
        return this.studentCount[g];
    }

    /**
     * Uses the Student, S,'s grade to determine the correct division, then goes
     * to the teamNum in that division, and then goes to the position studentNum
     * on that team, and changes its points to scores[]
     *
     * @param s
     *           Student
     * @param teamNum
     *           int
     * @param scores
     *           int[] {scores[0]=GL Score, scores[1]=ML Score}
     */
    public void setStudentScore(Student s, int teamNum, int[] scores) {
        int studGrad = s.getGrade();
        int i;
        if (studGrad < 11) {
            Team t = lowerDivisionTeams.get(new Integer(teamNum));
            Student[] teamMembers = t.getStudents();
            for (i = 0; i < teamMembers.length; i++) {
                Student temp = teamMembers[i];
                if (temp.equals(s)) {
                    temp.setScoreGL(scores[0]);
                    temp.setScoreML(scores[1]);
                    i = 99;
                }
            }
        } else {
            Team t = upperDivisionTeams.get(new Integer(teamNum));
            Student[] teamMembers = t.getStudents();
            for (i = 0; i < teamMembers.length; i++) {
                Student temp = teamMembers[i];
                if (temp.equals(s)) {
                    temp.setScoreGL(scores[0]);
                    temp.setScoreML(scores[1]);
                    i = 99;
                }
            }
        } // end else
        //What if we do not find this student on the specified team?
        if (i != 99) {
            System.err.print("Student name spelled incorrectly or team mismatch error: ");
            System.err.println(s.getName() + " attempd to update in team #" + teamNum);
        }
    }

    /**
     *
     * This method replaces the variables previously used to store the arrays of students
     *  (grdae9Students, grade10Students, ...
     *  The Team Maps are parsed and the students in the given grade are placed in an array.
     *
     * @param g - the specified grade (9 - 12)
     * @return all students in this grade from all teams in database.
     * The array of students is not sorted, the order is determined by the order of teams
     * in the team map (TreeMap arranged by team number)
     *
     *  studentCount is updated by this method.
     */
    public Student[] getAllStudentsInGrade(int g) {
        Student[] teamMembers, allStudents;
        TreeMap<Integer, Team> teams;
        if (g <= 10) {
            teams = this.lowerDivisionTeams;
        } else {
            teams = this.upperDivisionTeams;
        }
        Set<Integer> teamNumbers = teams.keySet();
        if (teamNumbers.size() == 0) {
            return null;
        }
        allStudents = new Student[teamNumbers.size() * 5];
        int studentIndex = 0;
        for (int teamNum : teamNumbers) {
            teamMembers = teams.get(teamNum).getStudents();
            if (teamMembers == null) {
                continue;
            }
            for (Student s : teamMembers) {
                if (s != null && s.getGrade() == g) {
                    allStudents[studentIndex++] = s;
                }
            }
        }
        //must eliminate any nulls from the end before returning the array.
        int lastIndex = allStudents.length - 1;
        while (allStudents[lastIndex] == null) {
            lastIndex--;
        }
        lastIndex++;
        Student[] trimedArray;
        if (lastIndex == allStudents.length) {
            trimedArray = allStudents;
        } else {
            trimedArray = new Student[lastIndex];
            System.arraycopy(allStudents, 0, trimedArray, 0, lastIndex);
        }
//arraycopy(Object src, int srcPos, Object dest, int destPos, int length) 
        studentCount[g] = trimedArray.length;
        return trimedArray;
    }

    /**
     * getTopIndividuals: takes as input a grade and returns an array of 20
     * students in order from #1 to #20 by score
     *
     * @param grade
     *           int - level 9 to 12
     * @return Student[], an array of the top 20 students in a given grade level
     * ??????????????? should we return an ArrayList instead?
     * @throws Exception
     *            if level is not 9 to 12.
     */
    public ArrayList<Student> getTopIndividuals(int grade) {
        Student[] allStudents = getAllStudentsInGrade(grade);
        if (allStudents != null && allStudents.length > 0) {
            Arrays.sort(allStudents);
            ArrayList<Student> winners = new ArrayList<Student>(TOPSCORERS_LENGTH + 5);
            int i = 0;
            boolean tie = true;
            while (i < TOPSCORERS_LENGTH || tie) {
                winners.add(allStudents[i]);
                if (i < allStudents.length - 1) {
                    tie = (allStudents[i].getScore() != allStudents[i + 1].getScore());
                } else {
                    break;
                }
                i++;
            }
            System.out.println("Winners list created with size: " + winners.size());
            return winners;
        } else {
            return null;
        }
    }

    /**
     * Takes as input a number indicating upper (level=1) or lower (level=0)
     * division, and returns the top 20 teams
     *
     * @param level
     *           int is 0 (for lower division) or 1 (for upper division)
     * @return Team[] - an array of the top 20 teams in that division
     * @throws Exception
     *            if the parameter is invalid.
     */
    public Team[] getTopTeams(int level) {
        Team[] teamList = getAllTeamsSorted(level);
        if (teamList == null) {
            return teamList;
        }
        if (teamList.length <= TOPSCORERS_LENGTH) {
            return teamList;
        }

        //since we have more teams than are asked for in the high score list,
        //we return only the cream of the crop.
        Team[] top = new Team[TOPSCORERS_LENGTH];

        for (int i = 0; i < top.length; i++) {
            top[i] = teamList[i];
        }
        return top;
    }

    /**
     *
     * Returns a sorted array of all teams in a division.
     * @param level is 0 (for lower division) or 1 (for upper division)
     *
     */
    public Team[] getAllTeamsSorted(int level) {
        TreeMap<Integer, Team> allTeams;
        ArrayList temp = new ArrayList();
        if (level == 1) {
            allTeams = this.upperDivisionTeams;
        } else if (level == 0) {
            allTeams = this.lowerDivisionTeams;
        } else {
            System.out.println("Non-existent division. Choose either Upper(1) or Lower(0)"
                    + "For the NMTracker getTopTeams() method");
            return null;
        }
        Set<Integer> teamNumbers = allTeams.keySet();
        if (teamNumbers.size() == 0) {
            return null;
        }
        Team[] allTeamsSort = new Team[teamNumbers.size()];
        Iterator<Integer> iter = teamNumbers.iterator();
        int i = 0;
        while (iter.hasNext()) {
            allTeamsSort[i++] = allTeams.get(iter.next());
        }
        Arrays.sort(allTeamsSort);
        return allTeamsSort;
    }

    /*
     * Gets an array of team numbers from a string, school @param school String --
     * the team's school @return int[] -- the team #'s from that school
     */
    public int[] getTeamsFromSchools(String school) {
        Iterator liter = lowerDivisionTeams.keySet().iterator();
        Iterator uiter = upperDivisionTeams.keySet().iterator();
        school = school.toLowerCase();
        ArrayList<Integer> tempList = new ArrayList();
        Team temp;
        while (liter.hasNext()) {
            int teamNum = Integer.parseInt(liter.next().toString());
            temp = lowerDivisionTeams.get(teamNum);
            if (temp.getSchool().toLowerCase().indexOf(school) != -1) {
                tempList.add(teamNum);
            }
        }
        while (uiter.hasNext()) {
            int teamNum = Integer.parseInt(uiter.next().toString());
            temp = upperDivisionTeams.get(teamNum);
            if (temp.getSchool().toLowerCase().indexOf(school) != -1) {
                tempList.add(teamNum);
            }
        }
        int[] out = new int[tempList.size()];
        for (int i = 0; i < tempList.size(); i++) {
            out[i] = tempList.get(i);
        }
        return out;
    }

    /**
     * Private method used for getTopIndividuals, that gets the top twenty
     * objects in a TreeSet
     *
     * @param list
     *           TreeSet - the list of students in a grade
     * @return ArrayList - an array list of the top twenty objects
     */
    private ArrayList getTopTwentyStudents(TreeSet list) {
        System.out.println("Getting top students");
        ArrayList top20 = new ArrayList();
        if (list.size() == 0) {
            return top20;
        }
        Iterator iter = list.iterator();
        while (iter.hasNext() && top20.size() < TOPSCORERS_LENGTH) {
            top20.add(iter.next());
        }
        return top20;
    }

    /*
     * Private method used for getTopTeams, that gets the top twenty objects in a
     * TreeMap
     *
     * @param list TreeSet - the list of teams in a division @return ArrayList -
     * an arraylist of the top twenty objects
     * @depricated
     *****  This method does not work when there is a tie because a set
     *      cannot score two equal values.
     *
     */
    private ArrayList getTopTwentyTeams(TreeMap list) {
        System.out.println("Getting top teams");
        ArrayList top20 = new ArrayList();
        TreeSet teams = new TreeSet(list.values());
        // This fails because the values of TreeMap are the teams and they are sorted
        // by score.  You cannot have two identical values (ties!) with a set.
        Iterator iter = teams.iterator();
        while (iter.hasNext() && top20.size() < TOPSCORERS_LENGTH) {
            top20.add(iter.next());
        }
        return top20;
    }

    /**
     *  Returns data that can be used to generate a historgram of the scores achieved by students
     *  @param grade - the grade to analyze.  grade=0 means all grades combined.
     *  @returns an array of length 11 where arr[i] = the number of students answering i questions correctly.
     *  Note: We assume that the max score is 10 for mathletics in this method.
     */
    public int[] getMathleticsScoreTally(int grade) {
        Student[] studentsInGrade = null;
        int[] tallys = new int[11];
        if (grade >= 9) {
            studentsInGrade = this.getAllStudentsInGrade(grade);
            if (studentsInGrade != null && studentsInGrade.length > 0) {
                for (Student joe : studentsInGrade) {
                    tallys[joe.getScoreML()]++;
                }
            }
        }
        return tallys;
    }

    /**
     *  Returns data that can be used to generate a historgram of the scores achieved by students
     *  @param grade - the grade to analyze.
     *  @returns an array of length 16 where arr[i] = the number of students answering i questions correctly.
     *  Note: We assume that the max score is 15 for the grade level contest.
     */
    public int[] getGradeLevelTally(int grade) {
        Student[] studentsInGrade = this.getAllStudentsInGrade(grade);
        int[] tallys = new int[16];

        if (studentsInGrade != null && studentsInGrade.length > 0) {
            for (Student joe : studentsInGrade) {
                tallys[joe.getScoreGL()]++;
            }
        }

        return tallys;
    }

    /**
     *  Returns data that can be used to generate a histogram of the team scores.
     *  @param level is 0 (for lower division) or 1 (for upper division)
     *  @returns an array of length 21 where arr[i] = the number of teams acheiving this score.
     */
    public int[] getTeamScoreTally(int level) {
        TreeMap<Integer, Team> teams;
        int[] tallys = new int[21];
        if (level == 1) {
            teams = this.upperDivisionTeams;
        } else if (level == 0) {
            teams = this.lowerDivisionTeams;
        } else {
            System.out.println("Non-existent division. Choose either Upper(1) or Lower(0)"
                    + "For the NMTracker getTopTeams() method");
            return null;
        }
        Set<Integer> teamNumbers = teams.keySet();
        Iterator<Integer> iter = teamNumbers.iterator();
        while (iter.hasNext()) {
            tallys[teams.get(iter.next()).getScore()]++;
        }
        return tallys;
    }

    /**
     *
     * Takes as input a number indicating upper (level=1) or lower (level=0)
     * division, and returns the top 20 teams
     *
     * @param level
     *           int is 0 (for lower division) or 1 (for upper division)
     * @return double[] - an array of length two:
     * Average Team Total:  averages[0]
     * average team round: averages[1]
     */
    public double[] averageTeamScore(int level) {
        int total0, total1;
        total1 = total0 = 0;
        //team round is 1, total score is 0
        TreeMap<Integer, Team> teams;
        if (level == 1) {
            teams = this.upperDivisionTeams;
        } else {
            teams = this.lowerDivisionTeams;
        }
        Set<Integer> teamNumbers = teams.keySet();
        for (Integer teamNum : teamNumbers) {
            total1 += teams.get(teamNum).getScore();
            total0 += teams.get(teamNum).getTotalScore();
        }
        double[] avg = new double[2];
        avg[0] = (double) total0 / teamNumbers.size();
        avg[0] = Math.round(avg[0] * 10) / 10.0;
        avg[1] = (double) total1 / teamNumbers.size();
        avg[1] = Math.round(avg[1] * 10) / 10.0;
        return avg;
    }

    /**
     * Calculates the average GL score of a grade
     *
     * @param grade
     *           int - grade of the students whose average you want
     * @return double - the average GL score of a grade
     */
    public double averageGLGrade(int grade) {
        int total = 0;
        int c=0;
        Student[] studentsInGrade = this.getAllStudentsInGrade(grade);
        for (Student s : studentsInGrade) {
            if(s.getScoreGL() != Student.NOSCORE) {
                total += s.getScoreGL();
                c++;
            }
        }
        return ((double) total) /c;
    }

    /*
     * Calculates the average ML score of a grade
     *
     * @param grade int - grade of the students whose average you want @return
     * double - the average ML score of a grade
     */
    public double averageMLGrade(int grade) {
        Student[] studentsInGrade = this.getAllStudentsInGrade(grade);
        int totalML = 0, c=0;
        for (Student s : studentsInGrade) {
            if(s.getScoreML() != Student.NOSCORE) {
                totalML += s.getScoreML();
                c++;
            }
        }
        return ((double) totalML) / c;
    }

    /*
     * Calculates the average total score of a grade
     *
     * @param grade int - grade of the students whose average you want @return
     * double - the average total score of a grade
     */
    public double averageTotalGrade(int grade) {
        Student[] studentsInGrade = this.getAllStudentsInGrade(grade);
        int total = 0, c=0;
        for (Student s : studentsInGrade) {
            if(s.getScoreML()!= Student.NOSCORE) {
                total += s.getScore();
                c++;
            }
        }
        return ((double) total) /c;
    }

//***** note name change: was medianScore.
    //TODO: change other method to medianTeamScore
    /**
     *
     * @param grade
     * @param scoreType
     * @return
     */
    public double medianIndividualScore(int grade, char scoreType) {
        Student[] allStudents = this.getAllStudentsInGrade(grade);
        Arrays.sort(allStudents, new StudentComparator(scoreType));
        // if even, average the two middle scores.
        // ex: 0,1,2,3, 4,5, 6,7,8,9 : 10 scores, middle is 4&5.
        Student mid1 = allStudents[allStudents.length / 2];
        double median = 0.0;
        if (allStudents.length % 2 == 0) {
            System.out.println(mid1.getScore());
            Student mid2 = allStudents[allStudents.length / 2 + 1];
            // here we are adding one because it seems that the
            // StudentComparator was written backwards, so the
            // students are sorted in reverse order.
            System.out.println(mid2.getScore());
            if (scoreType == 'G') {
                median = (mid1.getScoreGL() + mid2.getScoreGL()) / 2.0;
            } else if (scoreType == 'M') {
                median = (mid1.getScoreML() + mid2.getScoreML()) / 2.0;
            } else //total score
            {
                median = (mid1.getScore() + mid2.getScore()) / 2.0;
            }
        } // odd number of students: 0,1,2,3,4 : 5 students, middle is 2
        else {
            if (scoreType == 'G') {
                median = mid1.getScoreGL();
            } else if (scoreType == 'M') {
                median = mid1.getScoreML();
            } else if (scoreType == 'T') {
                median = mid1.getScore();
            }
        }
        return median;
    }

    /*
     * Calculates the median team score for a division
     *
     * @param division int - division of teams (0=lower, 1=upper) @return double -
     * the median score of choice of a grade
     */
    public double medianScore(int division) {
        TreeMap teamMap;
        double median = 0.0;
        if (division == 0)
            teamMap = lowerDivisionTeams;
        else // if(division==1)
            teamMap = upperDivisionTeams;
        if (teamMap.size() == 0) {
            return median;
        }
        int i = 0;
        Iterator iter = teamMap.keySet().iterator();
        if (teamMap.keySet().size() == 0) {
            return 0;
        }
        Team[] teams = new Team[teamMap.keySet().size()];

        while (iter.hasNext()) {
            teams[i] = (Team) teamMap.get(iter.next());
            i++;
        }
        // the ordering in the TreeMap is by the key, which is the id #
        // the ordering on Teams is by score
        Arrays.sort(teams);
        if (teams.length % 2 == 0) {
            Team mid1 = teams[teams.length / 2];
            Team mid2 = teams[teams.length / 2 - 1];
            median = (mid1.getTotalScore() + mid2.getScore()) / 2.0;
        } else {
            Team mid1 = teams[teams.length / 2];
            median = mid1.getTotalScore();
            System.out.println("Team median = " + median);
        }
        return median;
    }
    /**
     *
     * @param students - the list of students we are calculating the standard deviation for.
     *        All students in this array should be from the same grade.
     * @param averageGL - passed to this method so that we do not need to re-compute it.
     * @param averageML - the average Mathletics score for this list of students.
     * @return an array of doubles with size 2.  The first element (sd[0]) is the standard deviation for
     * grade level and the second element, sd[1] is the standard devation for mathletics.
     */

    public float[] standardDeviations(Student[] students, float averageGL, float averageML) {
        float[] sd = new float[2];
        int c=0;
        float tallyGL=0, tallyML=0, diff;

        for(Student s : students) {
            if(s.getScoreGL()!=Student.NOSCORE) {
                diff = (s.getScoreGL() - averageGL);
                tallyGL += diff*diff;
                c++;
            }
            else   //skip kids with no score.
                continue;
            if(s.getScoreML()!=Student.NOSCORE) {
                diff = (s.getScoreML() - averageML);
                tallyML += diff*diff;
            }
        }
        //grade level Standard Deviation:
        sd[0] = (float) Math.sqrt(tallyGL/c);
        sd[0] = Math.round(sd[0]*100)/100.0F;
        sd[1] = (float) Math.sqrt(tallyML/c);
        sd[1] = Math.round(sd[1]*100)/100.0F;
        return sd;
    }
}
