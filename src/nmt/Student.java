package nmt;

import java.util.*;
import java.io.*;

/**
 * 
 * Contains the data for a student
 */
public class Student implements Comparable, java.io.Serializable {

    private String name;
    private String school;
    private int grade;
    private int scoreGL;
    private int scoreML;

    public static final int NOSCORE = -1;

    public Student(String name) {
        this.name = name;
        scoreGL = scoreML = NOSCORE;
    }

    /**
     *
     * Used to output Students to a disk file and reauired by
     * the ObjectOutputStream class.
     * @param out
     * @throws IOException
     */
    private void writeObject(java.io.ObjectOutputStream out) throws IOException {
        out.writeObject(name);
        // System.out.print("B");
        out.writeObject(school);
        // System.out.print("B");
        out.writeObject(new Integer(grade));
        // System.out.print("B");
        out.writeObject(new Integer(scoreGL));
        // System.out.print("B");
        out.writeObject(new Integer(scoreML));
        // System.out.print("B");
    }

    private void readObject(java.io.ObjectInputStream in) throws IOException,
            ClassNotFoundException {
        name = (String) in.readObject();
        // / System.out.print("B");
        school = (String) in.readObject();
        // System.out.print("B");
        grade = ((Integer) in.readObject()).intValue();
        // System.out.print("B");
        scoreGL = ((Integer) in.readObject()).intValue();
        // System.out.print("B");
        scoreML = ((Integer) in.readObject()).intValue();
        // System.out.print("B");
    }

    /**
     *
     * Used to sort students by the .getScore method (total score)
     *
     * @param b
     *           Object - the Student we are comparing this student to Higher
     *           scores are placed before lower scores, so a negative number is
     *           returned if this student scores above b.
     * @return int
     */
    @Override
    public int compareTo(Object b) {
        Student opponent = (Student) b;
        if (this.getScore() != opponent.getScore()) {
            return opponent.getScore() - this.getScore();
        }

        // equal scores? Tie breaker is Mathletics
        if (this.scoreML != opponent.scoreML) {
            return opponent.scoreML - this.scoreML;
        } else {
            return this.getName().compareTo(opponent.getName());
        }
    }

    /**
     * Merges two student objects as long as the name is the same (representing
     * the same student). Thhe object with the higher scores is the one that will
     * be used. In practice, one object (either this or
     *
     * @param b)
     *           will likely have zeros in the fields for the scores.
     */
    public void mergeStudent(Student b) {
        if (!b.getName().equals(this.getName())) {
            javax.swing.JOptionPane.showMessageDialog(null,
                    "Cannot merge two teams because"
                    + " the students were entered in a different order:\n"
                    + b.toString() + "\n" + this.toString());
            return;
        }
        if (this.compareTo(b) == 1) {
            this.setScoreGL(b.getScoreGL());
            this.setScoreML(b.getScoreML());
        }
    }

    public String toString() {
        return new String(this.getName() + " from " + this.getSchool()
                + " scored a " + this.getScore());
    }

    public void setSchool(String school) {
        this.school = school;
    }

    public String getSchool() {
        return school;
    }

    public String getName() {
        return name;
    }

    public void setGrade(int grade) {
        if(grade <= 12)
            this.grade = grade;
    }

    public int getGrade() {
        return grade;
    }

    public void setScoreGL(int score) {
        scoreGL = score;
    }

    public int getScoreGL() {
        return scoreGL;
    }

    public void setScoreML(int score) {
        scoreML = score;
    }

    public int getScoreML() {
        return scoreML;
    }

    /**
     * returns the student's total score
     */
    public int getScore() {
        return scoreGL + scoreML;
    }

}
