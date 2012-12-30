package nmt;

import java.util.*;
import java.io.*;
import javax.swing.*;
import java.awt.*;
import java.text.*; //used for Decimal Format (for stats)

/**
 *  This is a collection of static methods for file access I/O
 *
 *
 */
public class NMTParser {

    /**
     * parses the text file
     * specified by fileName
     * and divides the data
     * into Teams and Students
     *
     * @return an ArrayList<Team> that contains all the teams
     */
    public static ArrayList<Team> parse(File fileName) throws
            FileNotFoundException {
        Scanner s = new Scanner(fileName);
        ArrayList<Team> list = new ArrayList<Team>(); //the list of teams
        s.useDelimiter(","); //changes the delimiter to ","
        int tNum, teamScore;
        String school;
        char division;
        while (s.hasNext()) {
            tNum = s.nextInt(); //sets the team number to the next int
            division = s.next().charAt(0); //sets the division to the first char in the next string (which is only 1 char long)
            teamScore = s.nextInt(); //sets the team score to the next int
            school = s.next(); //sets the school to the next string
            Student[] stud = new Student[5]; //the array of students
            for (int x = 0; x < 5; x++) {
                s.next(); //skips the \n (scanner reads and records line breaks)
                String st = s.next(); //the student's name
                if (st.equals("*")) { //checks if there is a student in the slot (* as the name signifies no student)
                    continue;
                }
                stud[x] = new Student(st); //makes a new Student
                stud[x].setSchool(school);
                stud[x].setGrade(s.nextInt()); //sets the grade to the next int
                stud[x].setScoreGL(s.nextInt()); //sets the GL score to the next int
                stud[x].setScoreML(s.nextInt()); //sets the ML score to the next int
            }
            //creates a new team and adds it to the list of teams
            list.add(new Team(stud, tNum, school, division, teamScore));

            s.next(); //skips the \n (scanner reads and records line breaks)
        }
        return list;
    }

    public static void saveList(FileOutputStream stream, NMTracker dataBase) {
        PrintWriter out = null;
        try {
            out = new PrintWriter(stream);
        } catch (Exception ioe) {
            ioe.printStackTrace();
            return;
        }
        for (Team t : dataBase.lowerDivisionTeams.values()) {
            out.println("," + t.getTeamNum() + "," + t.getDivision() + ","
                    + t.getScore() + "," + t.getSchool() + ",");
            for (Student s : t.getStudents()) {
                if (s == null || s.getName() == null || s.getName().equals("")) {
                    out.println(",*,\n");
                } else {
                    out.println("," + s.getName() + "," + s.getGrade() + ","
                            + s.getScoreGL() + "," + s.getScoreML() + ",");
                }
            }
        }
        for (Team t : dataBase.upperDivisionTeams.values()) {
            out.println("," + t.getTeamNum() + "," + t.getDivision() + ","
                    + t.getScore() + "," + t.getSchool() + ",");
            for (Student s : t.getStudents()) {

                if (s == null || s.getName() == null || s.getName().equals("")) {
                    out.println(",*,\n");
                } else {
                    out.println("," + s.getName() + "," + s.getGrade() + ","
                            + s.getScoreGL() + "," + s.getScoreML() + ",");
                }
            }
        }

        out.close();
    }

    /**
     *  Outputs a list of top students.  The database (NMTTracker) getTopIndividuals is
     *  called and max determines how many students are exported.
     *  @ returns true if the file was created successfully.
     *  @returns false if there were no students in the database matching.
     */
    public static boolean exportStudent(String filename, NMTracker dB,
            int grade, int max) {

        PrintWriter out = null;
        Calendar date = Calendar.getInstance();
        ArrayList<Student> top;
        //pass max to dB.
        top = dB.getTopIndividuals(grade);
        if (top == null || top.size() == 0) {
            return false;
        }
        try {
            //handle bargraph images
            String imageFilenames[] = new String[2];
            imageFilenames[0] = "images/" + Integer.toString(date.get(Calendar.MONTH))
                    + Integer.toString(date.get(Calendar.DAY_OF_MONTH))
                    + Integer.toString(date.get(Calendar.YEAR))
                    + Integer.toString(grade) + "GL.png";
            imageFilenames[1] = "images/" + Integer.toString(date.get(Calendar.MONTH))
                    + Integer.toString(date.get(Calendar.DAY_OF_MONTH))
                    + Integer.toString(date.get(Calendar.YEAR))
                    + Integer.toString(grade) + "ML.png";

            JDialog win = new JDialog();
            int[] data = dB.getGradeLevelTally(grade);
            BarChartComponent hist = new BarChartComponent(data, Color.BLUE);

            win.add(hist);
            win.setSize(420, 420);
            win.setVisible(true);

            hist.saveImage("png", imageFilenames[0]);
            win.setVisible(false);

            data = dB.getMathleticsScoreTally(grade);
            hist.updateData(data);
            win.validate();
            hist.validate();
            win.repaint();
            win.show(true);
            hist.saveImage("png", imageFilenames[1]);
            win.show(false);

            FileOutputStream stream = new FileOutputStream(new File(filename));
            out = new PrintWriter(stream);
            out.println("<html><head>");
            out.println("<title>Nassau Math Tournament " + date.get(Calendar.YEAR) + "</title>");
            out.println("</head>");
            out.println("<body bgcolor=\"#E9F0FF\" text=\"#000000\">");
            out.println("<h1>NMT Grade " + grade + " Top " + max + "</h1>");
            out.println("<table width=\"95%\" border=\"1\">");
            out.println("  <tr>");
            out.println("    <td width=\"10%\"><b>Rank</b></td>");
            out.println("    <td width=\"27%\"><b>Name</b></td>");
            out.println("    <td width=\"28%\"><b>School</b></td>");
            out.println("    <td width=\"10%\"><b>GL</b></td>");
            out.println("    <td width=\"10%\"><b>ML</b></td>");
            out.println("    <td width=\"10%\"><b>Total</b></td>");
            out.println("  </tr>");
            int rank = 1;
            int lastScore = 0;
            int lastMathletics = 0;
            //place students in the table.
            for (int x = 0; x < top.size(); x++) {
                Student joe = top.get(x);
                out.println("  <tr>");
                if ((joe.getScore() != lastScore) || joe.getScoreML() != lastMathletics) {
                    rank = x + 1;
                }
                if (rank > max) {
                    break;
                }
                out.println("    <td>" + rank + "</td>");
                out.println("    <td>" + joe.getName() + "</td>");
                out.println("    <td>" + joe.getSchool() + "</td>");
                out.println("    <td>" + joe.getScoreGL() + "</td>");
                out.println("    <td>" + joe.getScoreML() + "</td>");
                out.println("    <td>" + joe.getScore() + "</td>");
                lastScore = joe.getScore();
                lastMathletics = joe.getScoreML();
                out.println("  </tr>");
            } //end for loop
            out.println("</table><br>");
            DecimalFormat rounder = new DecimalFormat("##.0#");
            out.print("<b>");
            out.println("Average Grade Level Score: "
                    + rounder.format(dB.averageGLGrade(grade)) + "<br>");
            out.println("Average Mathletics Score: "
                    + rounder.format(dB.averageMLGrade(grade)) + "<br>");
            out.println("Average Total: "
                    + rounder.format(dB.averageTotalGrade(grade)) + "<br><br>");

            out.println("Median Grade Level Score: "
                    + rounder.format(dB.medianIndividualScore(grade, 'G')) + "<br>");
            out.println("Median Mathletics Score: "
                    + rounder.format(dB.medianIndividualScore(grade, 'M')) + "<br>");
            out.println("Median Total Score: "
                    + rounder.format(dB.medianIndividualScore(grade, 'T')) + "<br>");
            out.println("<br> Number of students reporting: "
                    + dB.numStudentsByGrade(grade));
            out.println("<br/><br/>Grade Level Scores:<br/><img src=\"" + imageFilenames[0] + "\"><br/>Mathematics Level Scores:<br/><img src=\"" + imageFilenames[1] + "\"/>");
            out.println("</b></body></html>");
        } catch (Exception ioe) {
            System.err.println("Unable to create file.");
            ioe.printStackTrace();
            out.close();
            return false;
        }
        if (out != null) {
            out.close();
        }
        return true;
    }

    /**
     * This method is used for exporting a web page with the top teams displayed
     * in a table.
     * @param String Filename - name of the file to store the exported page.
     * @param dB NMTracker
     * @param division int - 0 or 1 indicating lower or upper division
     * @param max int - how many teams to display
     * @throws Exception if ??? maybe not?
     */
    public static boolean exportTeam(String filename, NMTracker dB,
            int division, int max) {
        //!! max not used.  pass 2 dB.
        Team[] top = dB.getTopTeams(division);
        if (top == null || top.length == 0) {
            return false;
        }

        Calendar date = Calendar.getInstance();
        int year = date.get(Calendar.YEAR);

        PrintWriter out = null;
        int numTeamsInDivision = 0;
        try {
            String imageFilename = "images/" + Integer.toString(date.get(Calendar.MONTH))
                    + Integer.toString(date.get(Calendar.DAY_OF_MONTH))
                    + Integer.toString(date.get(Calendar.YEAR))
                    + ((division == 0) ? "LD" : "UD") + "_TR.png";

            JDialog win = new JDialog();
            int[] data = dB.getTeamScoreTally(division);
            BarChartComponent hist = new BarChartComponent(data, Color.BLUE);

            win.add(hist);
            win.setSize(420, 420);
            win.setVisible(true);
            hist.saveImage("png", imageFilename);
            win.setVisible(true);

            FileOutputStream stream = new FileOutputStream(new File(filename));
            out = new PrintWriter(stream);
            out.println("<html><head>");

            out.println("<title>Nassau Math Tournament " + year + "</title>");
            out.println("</head>");
            out.println("<body bgcolor=\"#E9F0FF\" text=\"#000000\">");
            if (division == 0) {
                out.println("<h1>NMT Lower Division Teams Top " + top.length + "</h1>");
                numTeamsInDivision = dB.lowerDivisionTeams.size();
            } else {
                out.println("<h1>NMT Upper Division Teams Top " + top.length + "</h1>");
                numTeamsInDivision = dB.upperDivisionTeams.size();
            }
            out.println("<table width=\"100%\" border=\"1\">");
            out.println("  <tr>");
            out.println("    <td width=\"5%\"><b>Rank</b></td>");
            out.println("    <td width=\"8%\"><b>Team</b></td>");
            out.println("    <td width=\"30%\"><b>School</b></td>");
            out.println("    <td width=\"40%\"><b>Team Members</b></td>");
            out.println("    <td width=\"8%\"><b>Team Round</b></td>");
            out.println("    <td width=\"9%\"><b>Team Score</b></td>");

            out.println("  </tr>");
            int rank = 1;
            int lastScore = 0;
            for (int x = 0; x < top.length; x++) {
                Team t = (Team) top[x];
                out.println("  <tr>");
                if (t.getTotalScore() != lastScore) {
                    rank = x + 1;
                }
                if (rank > max) {
                    break;
                }
                out.println("    <td>" + rank + "</td>");
                out.println("    <td>" + t.getTeamNum() + "</td>");
                out.println("    <td>" + t.getSchool() + "</td>");
                out.print("    <td>");
                lastScore = t.getTotalScore();
                StringBuffer str = new StringBuffer(100);
                //display the student names in a smaller font.s
                Student[] members = t.getStudents();
                for (int i = 0; i < members.length; i++) {
                    if (members[i] != null) {
                        str.append(members[i].getName());
                    }
                    if (i == 2) {
                        str.append("<br>");
                    } else if (i < members.length - 1) {
                        str.append(", ");
                    }
                } //end student list for loop
                str.append("</td>");
                out.println(str.toString());
                out.println("    <td>" + t.getScore() + "</td>");
                out.println("    <td>" + t.getTotalScore() + "</td>");

                out.println("  </tr>");
            } //end outside for loop for table
            out.print("</table><br><b>");
            double averages[] = dB.averageTeamScore(division);
            out.println("Average Team Round Score: " + averages[1] + "<br>");
            out.println("Average Team Total: " + averages[0] + "<br>");
            out.println("Median Team Total: " + dB.medianScore(division) + "<br><br>");
            out.println("Number of teams registered: " + numTeamsInDivision);
            out.println("<br/><br/>Team Round:<br/><img src=\"" + imageFilename + "\"/>");
            out.print("</b></body></html>");
            out.close();
        } catch (Exception ioe) {
            System.out.println("\n***Unable to create team winning file.");
            System.out.println(ioe);
            ioe.printStackTrace();
            if (out != null) {
                out.close();
            }
            return false;
        }
        return true;
    }

    /**
     *  Creates a frame to view the team statistics in.
     *
     *
     */
    public static void dispTStats(int level, NMTracker dB, String f) throws Exception {
        if (exportTeam(f, dB, level, 30)) {
            new ResultsViewFrame(f);
        }
    }

    public static void dispIStats(int level, NMTracker dB, String filename) throws Exception {
        if (exportStudent(filename, dB, level, 30)) {
            new ResultsViewFrame(filename);
        }
    }
}
