package tutorial;
import com.fasterxml.jackson.annotation.JsonValue;
import okhttp3.*;
import org.json.JSONObject;
import org.json.simple.JSONValue;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.io.IOException;
import java.util.List;
public class bot extends TelegramLongPollingBot {

    String bottoken = "6888288528:AAEgSRKC9ROx-OuWCBp8IndkerVQQjH0yF0";
    @Override
    public String getBotUsername() {
        return "peakymuthubot";
    }

    @Override
    public String getBotToken() {
        return bottoken;
    }

    static String link ="";
    static int unit =0;
    static String PreviousMsg ="";
    static String service ="";

    @Override
    public void onUpdateReceived(Update update) {
        var msg = update.getMessage();
        var user = msg.getFrom();
        var id = user.getId();
        KeyboardRow row1 = new KeyboardRow();
        row1.add("yes");
        row1.add("change");

        ReplyKeyboardMarkup rkb = ReplyKeyboardMarkup.builder()
                .keyboard(List.of(row1))
                .oneTimeKeyboard(true)
                .resizeKeyboard(true)
                .build();

        print(msg);
        if (msg.getText().equals("/order")) {
            PreviousMsg = "order";
            sendText(id, "enter your order id");
            return;
        }

        if (msg.getText().equals("/exit")){
            PreviousMsg = "";
            link = "";
            unit = 0;
            sendText(id ,"OK");
            sendText(id,"Choose any of the below Services \n--/followers\n--/likes");
        }

        if (PreviousMsg.equals("order")){
            sendReq send = new sendReq();
            send.checkOrder(id ,msg);
            return;
        }

        if (msg.getText().equals("/start")) {
            sendText(id, "Hello Homie !!");
            sendText(id, """
                    WELCOME TO INSTAGRAMBOT\s


                    This bot allows you to increase your followers and like to your instagram account
                    --Please make sure that the account is PUBLIC..
                    
                    --Please enter the correct LINK to your post or account otherwise it will not work..
                    
                    --An order may take upto 0-60 min to complete(Sometimes more than that) wait patiently..
                    Use the following command
                            /followers
                            /likes""");

            return;
        }

        if (PreviousMsg.isEmpty()||PreviousMsg.isBlank()) {
            if (msg.getText().equals("/followers")) {
                sendText(id, "Enter the link to your instagram account :");
                PreviousMsg = "/followers";
                return;
            } else if (msg.getText().equals("/likes")) {
                sendText(id, "Enter the link to your instagram account :");
                PreviousMsg = "/likes";
                return;
            }
        }

        if (PreviousMsg.equals("/followers")) {
            service = "followers";
            sendReq send = new sendReq();
            send.getLink(msg,id);
            return;
        } else if (PreviousMsg.equals("/likes")) {
            service = "likes";
            sendReq send = new sendReq();
            send.getLink(msg,id);
            return;
        }

        if (PreviousMsg.startsWith("https")){
            try {
                int num = Integer.parseInt(msg.getText());
                if (num >= 10 && num <100){
                    unit = Integer.parseInt(msg.getText());
                    sendText(id,"Please make sure the following credential are correct in order to move further..\n\n"+"link :" + link +"\n"+"unit :" + unit);
                    sendmenu(id,"Click yes to proceed..",rkb);
                    PreviousMsg = msg.getText();
                    return;
                }
                else  {
                    sendText(id,"Enter a number greater than 10 and lesser than 100");
                }
            }
            catch (NumberFormatException nfe){
                sendText(id,"Enter a number");
            }
        }

        if (Integer.parseInt((PreviousMsg))>= 10 &&Integer.parseInt((PreviousMsg))< 100 ){
            if (msg.getText().equals("yes")){
                sendReq send = new sendReq();
                if (service.equals("followers")) {
                    send.newOrder(link, unit, id,1971);
                    link = "";
                    unit = 0;
                    PreviousMsg = "";
                }
                else if (service.equals("likes")){
                    send.newOrder(link,unit,id,714);
                    link = "";
                    unit = 0;
                    PreviousMsg = "";
                }
            }
            else if(msg.getText().equals("change")){
                PreviousMsg = "/followers";
                link ="";
                unit = 0;
                sendText(id,"Enter the correct details");
                sendText(id,"Enter the link to your instagram account :");
                return;
            }
            else{
                sendText(id,"click any button");

            }
        }
    }

    public void sendText(Long who, String what) {
        String strwho = who.toString();
        SendMessage sm = SendMessage.builder()
                .chatId(strwho)
                .text(what).build();
        try {
            execute(sm);                        //Actually sending the message
        } catch (TelegramApiException e) {
            throw new RuntimeException(e);      //Any error will be printed here
        }
    }

    public void sendmenu(Long who, String what,ReplyKeyboardMarkup rkb) {
        String strwho = who.toString();
        SendMessage sm = SendMessage.builder()
                .chatId(strwho).parseMode("html")
                .text(what).replyMarkup(rkb).build();
        try {
            execute(sm);                        //Actually sending the message
        } catch (TelegramApiException e) {
            throw new RuntimeException(e);      //Any error will be printed here
        }
    }

    void print(Message message){
        System.out.println(message.getFrom().getFirstName() + " " + message.getText()) ;
        System.out.println("PM :" + PreviousMsg);
        System.out.println("link :" + link);
        System.out.println("unit :" + unit);
    }
}
     class sendReq{
         OkHttpClient client = new OkHttpClient().newBuilder().build();
         MediaType mediatype = MediaType.parse("text/plain");
         RequestBody body = RequestBody.create(mediatype,"");
         bot botobj =new bot();
        void newOrder (String link,int unit,long id,int serviceid) {

            String api_link = "https://cheapsmmpro.in/api/v2?action=add&service="+serviceid+"&link="+link+ "&quantity="+unit +"&key=c7f479a070cc8924b04f4756b07a89cb";
            Request req = new Request.Builder().url(api_link).method("POST",body).build();
            System.out.println(api_link);

            try {
                Response res = client.newCall(req).execute();
                assert res.body() != null;
                botobj.sendText(id ,"Your Order ID is"+ res.body().string());
                botobj.sendText(id, "You can track your Order via /order comment");
                System.out.println(res.body().string());
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }

        void checkOrder(long id,Message msg){
            try {
                int order_id = Integer.parseInt(msg.getText());
                String api_link = "https://cheapsmmpro.in/api/v2?action=status&key=c7f479a070cc8924b04f4756b07a89cb&order="+order_id;
                Request req = new Request.Builder().url(api_link).method("POST",body).build();
                System.out.println(order_id);

                try {
                    Response res = client.newCall(req).execute();
                    assert res.body() != null;
                    String jsonBody = res.body().string();
                    JSONObject jsonObject = new JSONObject(jsonBody);
                    String status =(String)jsonObject.get("status");
                    String remains = (String)jsonObject.get("remains");
                    String start_count = (String)jsonObject.get("start_count");
                    botobj.sendText(id, "Your Order Status is:\n" + "Status : "+status+"\nRemains : "+remains +"\nStart count : "+start_count);
                }
                 catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
            catch (NumberFormatException nfe){
                botobj.sendText(id,"enter the Correct order id");
            }
        }

        void getLink(Message msg,long id){
            if (msg.getText().startsWith("https")){
                bot.link = msg.getText();
                bot.PreviousMsg = msg.getText();
                if (bot.service.equals("followers"))
                    botobj.sendText(id,"Enter the amount of Followers");
                else if (bot.service.equals("likes")) {
                    botobj.sendText(id,"Enter the amount of Likes");
                }
            }
            else{
                botobj.sendText(id,"Enter the exact link of your Instagram page...");
                return;
            }
        }
    }