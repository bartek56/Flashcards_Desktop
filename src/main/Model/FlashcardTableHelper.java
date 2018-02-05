package Model;

/**
 * Created by Bartek on 2017-09-15.
 */
public class FlashcardTableHelper {

    private int id;
    private String engWord;
    private String plWord;
    private String engSentence;
    private String plSentence;
    private String category;

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

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }
}
