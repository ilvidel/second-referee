package net.ilvidel.secref;

import java.io.Serializable;
import java.util.Arrays;

public class Team implements Serializable{

    private int[] roster;
    private int subsCount;
    private int timeoutCount;
    private int score;
    private boolean serving;

    public Team() {
        roster = new int[6];
        subsCount = 6;
        timeoutCount = 2;
    }

    @Override
    public String toString() {
        return Arrays.toString(roster);
    }

    public void startSet() {
        subsCount = 6;
        timeoutCount = 2;
        score = 0;
    }

    public void rotate() {
        int aux = roster[0];
        roster[0] = roster[1];
        roster[1] = roster[2];
        roster[2] = roster[3];
        roster[3] = roster[4];
        roster[4] = roster[5];
        roster[5] = aux;
    }

    public void unrotate() {
        int aux = roster[0];
        roster[0] = roster[5];
        roster[5] = roster[4];
        roster[4] = roster[3];
        roster[3] = roster[2];
        roster[2] = roster[1];
        roster[1] = aux;
    }

    public Team clone() {
        Team newTeam = new Team();
        newTeam.setScore(this.getScore());
        newTeam.roster(this.roster());
        newTeam.setSubsCount(this.subsCount);
        newTeam.setTimeoutCount(this.timeoutCount);
        newTeam.serving(this.serving);

        return newTeam;
    }

    public void score(){
        score++;
    }

    public int getScore() {
        return score;
    }

    public void setScore(int score) {
        this.score = score;
    }

    public boolean isServing() {
        return serving;
    }

    public void serving(boolean serving) {
        this.serving = serving;
    }

    public int getSubsCount() {
        return subsCount;
    }

    public void setSubsCount(int subsCount) {
        this.subsCount = subsCount;
    }

    public int getTimeoutCount() {
        return timeoutCount;
    }

    public void setTimeoutCount(int timeoutCount) {
        this.timeoutCount = timeoutCount;
    }

    public int[] roster() {
        return roster;
    }

    public void roster(int[] roster) {
        this.roster = roster;
    }

    public int roster(int pos) {
        return roster[pos];
    }

    public void timeout() {
        timeoutCount--;
    }

    public void substitution(int in, int out) {
        subsCount--;

        for(int i=0; i<roster.length; i++) {
            if(roster[i] == out) {
                roster[i] = in;
                break;
            }
        }
    }
}
