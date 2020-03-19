import lombok.Getter;
import lombok.Setter;
import lombok.SneakyThrows;
import org.apache.commons.io.FileUtils;
import org.apache.log4j.Logger;
import org.telegram.telegrambots.ApiContextInitializer;
import org.telegram.telegrambots.bots.DefaultBotOptions;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.ApiContext;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.send.SendPhoto;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.meta.exceptions.TelegramApiRequestException;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
public class Bot extends TelegramLongPollingBot {
    private static final String NAME = "MyNothingSpecialBot";
    private static final String TOKEN = "1119158933:AAEXX7Z_ovYnCXbVc7sFAy1DQ7Oz_6lysGU";
    private static final Logger log = Logger.getLogger(Bot.class);
    private static volatile Bot bot;
    private static boolean dir;

    public static Bot getInstance(){
        if(bot == null){
            synchronized (Bot.class) {
                if(bot == null) {
                    bot = new Bot();
                }
            }
        }
        return bot;
    }


    @SneakyThrows
    public void onUpdateReceived(Update update) {
        Message message = update.getMessage();
        if(message != null && message.hasText() && message.getText().contains("Оценить фото")){
            if(message.getText().contains("по твоему мнению")){
                sendMsg(message, "Мне кажется, что эта фотка бомба....");
                sendMsg(message, "Секундочку...Процессор грузится, фотки мутятся");
                sendMsg(message, HtmlParser.rate(HtmlParser.getPhotosFromVk(message.getText().split(" ")[2])));
            }
            else{
                sendMsg(message, "1-3 минуты, сейчас я скину все фотки.... Прошу не злиться на меня за медленную работу, я стараюсь...");
                int x = HtmlParser.getPhotosFromVk(message.getText().split(" ")[2]).size();
                sendMsg(message, "Почти готово...");
                for (int i = 0; i < x; i++) {
                    sendPhto(message, new File("data/" + message.getText().split(" ")[2] + "/img" + i + ".jpg"));
                }
                if(x > 0) {
                    sendMsg(message, "Уууу, сколько классных фоток я нашел " + "\n" + "≧(◕ ‿‿ ◕)≦");
                }else{
                    sendMsg(message, "Мда уж, что-то я ничего не нашел " + "\n" + "( ￣ー￣)");
                }

                /*for (String link : HtmlParser.getPhotosFromVk(message.getText().split(" ")[2])) {
                    sendMsg(message, link);
                }*/
            }
        }
        if(message != null && message.hasText() && !message.getText().contains("Оценить фото")){
            switch (message.getText()){
                case "/help":
                    sendMsg(message, "Привет, я пока что не особо многое умею, могу лишь сказать тебе привет ");
                    break;
                case "Помощь":
                    sendMsg(message, "Привет, я пока что не особо многое умею, могу лишь сказать тебе привет");
                    break;
                case "Привет":
                    sendMsg(message, "Ого, вы даже умеете здороваться 0_0. Приятно, однако....");
                    break;
                case "Настройки бота":
                    sendMsg(message, "Какие настройки я же еще ничего не умею! Ало");
                    break;
                case "/settings":
                    sendMsg(message, "Какие настройки я же еще ничего не умею! Ало");
                    break;
                case "/start":
                    sendMsg(message, "О, вы уже начали общаться со мной! Успех....");
                    break;
                case "/name":
                    sendMsg(message, update.getMessage().getAuthorSignature());
                    break;
                default:
                    sendMsg(message, "Не знаю я, что это такое, блин...");
                    break;

            }
        }

    }

    private synchronized void sendMsg(Message message, String text) {
        SendMessage sendMessage = new SendMessage();
        sendMessage.enableMarkdown(true);
        sendMessage.setChatId(message.getChatId().toString());
        sendMessage.setText(text);
        try{
            setButtons(sendMessage);
            execute(sendMessage);
        }catch (TelegramApiException e){e.printStackTrace();}

    }

    private synchronized void sendPhto(Message message, File file) throws TelegramApiException {
        SendPhoto sendPhoto = new SendPhoto();
        sendPhoto.setChatId(message.getChatId().toString());
        sendPhoto.setPhoto(file);
        execute(sendPhoto);
    }



    public synchronized void setButtons(SendMessage sendMessage){
        ReplyKeyboardMarkup replyKeyboardMarkup = new ReplyKeyboardMarkup();
        sendMessage.setReplyMarkup(replyKeyboardMarkup);
        replyKeyboardMarkup.setSelective(true);
        replyKeyboardMarkup.setResizeKeyboard(true);
        replyKeyboardMarkup.setOneTimeKeyboard(false);

        List<KeyboardRow> keyboardRows = new ArrayList<>();

        KeyboardRow first = new KeyboardRow();
        first.add(new KeyboardButton("Помощь"));

        KeyboardRow second = new KeyboardRow();
        second.add(new KeyboardButton("Привет"));

        KeyboardRow third = new KeyboardRow();
        third.add(new KeyboardButton("Настройки бота"));

        keyboardRows.add(first);
        keyboardRows.add(second);
        keyboardRows.add(third);

        replyKeyboardMarkup.setKeyboard(keyboardRows);
    }


    public String getBotUsername() {
        return Bot.NAME;
    }

    public String getBotToken() {
        return Bot.TOKEN;
    }

    public static void deleteDirectory(File dir) {
        if (dir.isDirectory()) {
            String[] children = dir.list();
            for (int i=0; i<children.length; i++) {
                File f = new File(dir, children[i]);
                deleteDirectory(f);
            }
            dir.delete();
        } else dir.delete();
    }
}
