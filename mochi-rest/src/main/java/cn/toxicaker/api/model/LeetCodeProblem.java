package cn.toxicaker.api.model;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;

@Document(collection = "leetcode_problems")
public class LeetCodeProblem {
    @Id
    public String id;

    public Integer leetCodeId;

    public Integer problemNum;

    public String title;

    public String content;

    public Boolean type;   //normal, premium

    public Double acceptance;

    public Integer difficulty;

    public Double frequency;

    public String titleSlug;

    public List<String> companyTags;

    public List<LeetCodeTag> tags;

    public enum Type {

        NORMAL("Normal", false),
        PREMIUM("Premium", true);

        public String val;

        public boolean type;

        Type(String val, boolean type) {
            this.val = val;
            this.type = type;
        }

        public static String getType(boolean type) {
            return !type ? "Normal" : "Premium";
        }
    }

    public enum Difficulty {

        EASY("Easy", 1),
        MEDIUM("Medium", 2),
        HARD("Hard", 3);

        public String val;

        public int num;

        Difficulty(String val, int num) {
            this.val = val;
            this.num = num;
        }

        public static String getDifficulty(int num) {
            switch (num) {
                case 1:
                    return "Easy";
                case 2:
                    return "Medium";
                case 3:
                    return "Hard";
            }
            return "";
        }
    }
}
