package Model.DataBase.SQLite.Tables;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;


/**
 * Created by Bartek on 2017-09-06.
 */


@Entity
@Table(name = "flashcard")
public class Flashcard {

    private int id;
    private String engWord;
    private String plWord;
    private String engSentence;
    private String plSentence;

    public Flashcard(String engWord, String plWord, String engSentence, String plSentence) {

        this.engWord = engWord;
        this.plWord = plWord;
        this.engSentence = engSentence;
        this.plSentence = plSentence;
    }

    @Id
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getEngWord() {
        return engWord;
    }

    public void setEngWord(String engWord) {
        this.engWord = engWord;
    }

    public String getPlWord() {
        return plWord;
    }

    public void setPlWord(String plWord) {
        this.plWord = plWord;
    }

    public String getEngSentence() {
        return engSentence;
    }

    public void setEngSentence(String engSentence) {
        this.engSentence = engSentence;
    }

    public String getPlSentence() {
        return plSentence;
    }

    public void setPlSentence(String plSentence) {
        this.plSentence = plSentence;
    }



}
