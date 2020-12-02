import org.json.simple.parser.JSONParser;
import org.json.simple.JSONObject;
import org.telegram.telegrambots.ApiContextInitializer;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.meta.exceptions.TelegramApiRequestException;

import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

public class Bot extends TelegramLongPollingBot {
    static final String API_KEY = "60863f88c34e274c330b04f9cdd90e7d";
    static String url = "http://api.openweathermap.org/data/2.5/weather?";
    static public String city = "Chisinau";

    public static void main(String[] args) {

        ApiContextInitializer.init();
        TelegramBotsApi telegramBotsApi = new TelegramBotsApi();

        try {
            telegramBotsApi.registerBot(new Bot());
        } catch (TelegramApiRequestException e) {
            e.printStackTrace();
        }
    }

    public void onUpdateReceived(Update update) {
        // System.out.println(update.getMessage().getText());
        Message msg = update.getMessage();
        String txt = msg.getText();
        if (txt.equals("/start")) {
            sendMessage(msg, "Salut introdu numele orasului");
        } else {
            city = txt;
            try {
                fileWriter(createConnection(createUrl(url, API_KEY, city)));
                sendMessage(msg, parseJSON());
            } catch (Exception e) {
                System.out.println("Error");
            }
        }


    }

    public String getBotUsername() {
        return "vlad95_bot";
    }

    public String getBotToken() {
        return "1426506439:AAHoBHRNxu2DP2r0kgAXaDbzEAM6ZAsFIBc";
    }

    public void sendMessage(Message msg, String text) {
        SendMessage sendMessage = new SendMessage();
        sendMessage.setChatId(msg.getChatId());
        sendMessage.setText(text);
        try {
            execute(sendMessage);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }

    static String createUrl(String url, String key, String city) {
        HashMap<String, String> map = new HashMap<String, String>();
        map.put("q", city);
        map.put("appid", key);
        map.put("units", "metric");
        map.put("lang", "en");
        for (Map.Entry e : map.entrySet()) {
            url += e.getKey() + "=" + e.getValue() + "&";
        }

        return url;
    }

    static String createConnection(String url) {
        try {
            URL obj = new URL(url);
            HttpURLConnection connection = (HttpURLConnection)
                    obj.openConnection();
            connection.setRequestMethod("GET");
            Scanner fileScanner = new
                    Scanner(connection.getInputStream());
            String response = "";
            while (fileScanner.hasNextLine()) {
                response += fileScanner.nextLine();
            }
            return response;
        } catch (Exception e) {
            System.out.println("Exception connection " + e);
        }
        return null;
    }

    static void fileWriter(String response) {
        try {
            FileWriter fw = new FileWriter("data.json", false);
            fw.write(response);
            fw.close();
        } catch (IOException e) {
            System.out.println("File error");
        }

    }

    static String parseJSON() {
        String output = "";
        try {
            FileReader fr = new FileReader("data.json");
            JSONParser jp = new JSONParser();
            JSONObject jo = (JSONObject) jp.parse(fr);
            JSONObject main = (JSONObject) jo.get("main");
            JSONObject sys = (JSONObject) jo.get("sys");
            output += "Tara: " + sys.get("country") + "\n";
            output += "Orasul: " + jo.get("name") + "\n";
            output += "Temp: " + main.get("temp") + " °С\n";
            output += "Temp min: " + main.get("temp_min") + " °С\n";
            output += "Temp max: " + main.get("temp_max") + " °С\n";
            return output;
        } catch (Exception e) {
            System.out.println("Read error");
        }

        return null;
    }

}
