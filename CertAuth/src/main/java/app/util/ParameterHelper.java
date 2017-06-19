package app.util;

import java.io.*;

public class ParameterHelper {

    private static final String FILE = "src" + File.separator + "main" + File.separator + "webapp" + File.separator + "config" + File.separator + "config.txt";
    private static String defaultCronExp = null;
    private static String defaultFreqDesc = null;

    public static String getDefaultCron(){
        if (defaultCronExp == null)
            readDefaultCronAndDescription();
        return defaultCronExp;
    }

    public static String getDefaultFrequencyDescription(){
        if (defaultFreqDesc == null)
            readDefaultCronAndDescription();
        return defaultFreqDesc;
    }

    public static void setDefaultCronAndDescription(String cron, String desc){
        defaultCronExp = cron;
        defaultFreqDesc = desc;
        writeDefaultCronAndDescription();
    }

    private static void readDefaultCronAndDescription(){
        String fileContent = readFileIntoString(FILE);
        String[] lines = fileContent.split("\n");
        defaultCronExp = lines[0].trim();
        defaultFreqDesc = lines[1].trim();
    }

    private static void writeDefaultCronAndDescription(){
        PrintWriter writer = null;
        try {
            writer = new PrintWriter(FILE, "UTF-8");
            writer.println(defaultCronExp);
            writer.println(defaultFreqDesc);
            writer.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
    }

    private static String readFileIntoString(String filepath){
        InputStream is = null;
        try {
            is = new FileInputStream(FILE);
            BufferedReader buf = new BufferedReader(new InputStreamReader(is));
            String line = buf.readLine();
            StringBuilder sb = new StringBuilder();
            while (line != null){
                sb.append(line).append('\n');
                line = buf.readLine();
            }
            return sb.toString();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }



}
