public class Logger {

    public static void Log(String message) {
        System.out.println(java.time.LocalTime.now() + " " + message);
    }
}
