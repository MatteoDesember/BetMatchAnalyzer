import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class Match {
    public String ID;
    public String name;
    public LocalDateTime date;
    public String BEURL;
    public String FSURL;
    public String score;
    public List<Match> h2hMatchList = new ArrayList<>();
    public List<Integer> goalTimeListFirstHalf = new ArrayList<>();
    public List<Integer> goalTimeListSecondHalf = new ArrayList<>();
}
