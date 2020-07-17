//import java.io.File;
//import java.io.FileNotFoundException;
//import java.io.FileOutputStream;
//import java.io.PrintStream;
//import java.text.DateFormat;
//import java.text.SimpleDateFormat;
//import java.util.Date;
//
//public class Logger {
//
//    private static final DateFormat sdf = new SimpleDateFormat("yyyy.MM.dd HH.mm.ss");
//    private static final PrintStream console = new PrintStream(System.out);
//    private static PrintStream file = null;
//
//    public Logger() {
//        try {
//            File test = new File("logs/log_" + System.currentTimeMillis() + ".txt");
//            test.getParentFile().mkdirs();
//            file = new PrintStream(new FileOutputStream(test));
//
//        } catch (FileNotFoundException e) {
//            e.printStackTrace();
//        }
//    }
//
//
//    public void print(String x) {
//        Date date = new Date();
//        String logMsg = "[" + sdf.format(date) + "] " + x;
//        console.println(logMsg);
//        file.println(logMsg);
//
//    }
//}
