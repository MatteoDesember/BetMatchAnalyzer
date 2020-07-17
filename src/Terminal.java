import java.io.IOException;
import java.time.*;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.*;

import static java.time.LocalDateTime.*;

public class Terminal implements Runnable {


    private Thread t;
    private String threadName;
    private boolean working = true;

    private static Program program = new Program();

    Terminal(String threadName) {
        this.threadName = threadName;
    }


    public void run() {

        Scanner reader = new Scanner(System.in);  // Reading from System.in
        while (working) {
//            System.out.println("Enter date (eg. \"2020.07.14\") or press 'Enter' to process tommorow, 'exit' or 'e' to Exit: ");
            System.out.println("Press 'Enter' to process tommorow, 'exit' or 'e' to Exit: ");
            String command = reader.nextLine();

            String[] aray = command.split(" ");

            System.out.println("Processing: " + command);

            if (Objects.equals(aray[0], "")) {
                try {
                    program.start(null);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            } else if (Objects.equals(aray[0], "exit") || Objects.equals(aray[0], "e")) {

                working = false;
                System.exit(0);

//            } else if (isValidFormat("yyyy.MM.dd", aray[0], Locale.ENGLISH)) {
//                try {
//                    Date dt = new SimpleDateFormat("yyyy.MM.dd").parse(aray[0]);
//                    LocalDateTime dateToAnalyze = from(dt.toInstant().atZone(ZoneId.systemDefault()));
//                    program.start(dateToAnalyze);
//                } catch (ParseException | IOException e) {
//                    e.printStackTrace();
//                }

//            } else if (Objects.equals(aray[0], "h")) {
//
//                System.out.println("HELP");

            } else {

                System.out.println("Don't found this command");

            }
        }
    }

    public boolean isValidFormat(String format, String value, Locale locale) {
        LocalDateTime ldt = null;
        DateTimeFormatter fomatter = DateTimeFormatter.ofPattern(format, locale);

        try {
            ldt = parse(value, fomatter);
            String result = ldt.format(fomatter);
            return result.equals(value);
        } catch (DateTimeParseException e) {
            try {
                LocalDate ld = LocalDate.parse(value, fomatter);
                String result = ld.format(fomatter);
                return result.equals(value);
            } catch (DateTimeParseException exp) {
                try {
                    LocalTime lt = LocalTime.parse(value, fomatter);
                    String result = lt.format(fomatter);
                    return result.equals(value);
                } catch (DateTimeParseException e2) {

                    e2.printStackTrace();//Debugging purposes
                }
            }
        }

        return false;
    }

    public void start() {
        //Start thread
        if (t == null) {
            t = new Thread(this, threadName);
            t.start();
        }
    }
}
