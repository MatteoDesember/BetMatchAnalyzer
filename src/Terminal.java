import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.*;

import static java.time.LocalDateTime.from;

public class Terminal implements Runnable {

    //There are some needed variable to run Terminal thread
    private Thread t;
    private String threadName;
    private boolean working = true;

    //This program searches matches which with given criteria
    private static SoccerProgram soccerProgram;

    /**
     * Terminal is little interface to interact with user
     */
    Terminal(String threadName) {
        this.threadName = threadName;
    }

    /**
     * Run Terminal
     */
    public void run() {
        // Reading from System.in
        Scanner reader = new Scanner(System.in);

        while (working) {
            System.out.println("Press 'Enter' to process Tommorow, type 'exit' or 'e' to Exit: ");

            // Read command
            String command = reader.nextLine();
            String[] aray = command.split(" ");
            System.out.println("Processing: " + command);

            // If pressed 'enter' start SoccerProgram
            if (Objects.equals(aray[0], "")) {
                startSoccerProgram();

                // If typed date in yyy.MM.dd format start SoccerProgram selected date
//            } else if (isValidFormat("yyyy.MM.dd", aray[0], Locale.ENGLISH)) {
//                startSoccerProgram(aray[0]);
                // If typed 'exit' or , 'e' exit main program
            } else if (Objects.equals(aray[0], "exit") || Objects.equals(aray[0], "e")) {
                working = false;
                System.exit(0);
            } else {
                System.out.println("Don't found this command");
            }
        }
    }

    /**
     * startSoccerProgram starts SoccerProgram with date=tommorow
     * or with given date by user in yyyy.MM.dd format
     */
    private void startSoccerProgram(String... date) {
        try {
            soccerProgram = new SoccerProgram();
            if (date.length == 0) {
                soccerProgram.start(null);
            } else {
                Date dt = new SimpleDateFormat("yyyy.MM.dd").parse(date[0]);
                LocalDateTime dateToAnalyze = from(dt.toInstant().atZone(ZoneId.systemDefault()));
                soccerProgram.start(dateToAnalyze);
            }
        } catch (Exception e) {
            System.out.println(e.toString());
        }
        System.exit(0);
    }

//    /**
//     *
//     * isValidFormat checks if given date format is right
//     */
//    private boolean isValidFormat(String format, String value, Locale locale) {
//        LocalDateTime ldt;
//        DateTimeFormatter fomatter = DateTimeFormatter.ofPattern(format, locale);
//
//        try {
//            ldt = parse(value, fomatter);
//            String result = ldt.format(fomatter);
//            return result.equals(value);
//        } catch (DateTimeParseException e) {
//            try {
//                LocalDate ld = LocalDate.parse(value, fomatter);
//                String result = ld.format(fomatter);
//                return result.equals(value);
//            } catch (DateTimeParseException exp) {
//                try {
//                    LocalTime lt = LocalTime.parse(value, fomatter);
//                    String result = lt.format(fomatter);
//                    return result.equals(value);
//                } catch (DateTimeParseException e2) {
//                    e2.printStackTrace();//Debugging purposes
//                }
//            }
//        }
//        return false;
//    }

    /**
     * start Terminal thread
     */
    void start() {
        //Start thread
        if (t == null) {
            t = new Thread(this, threadName);
            t.start();
        }
    }
}
