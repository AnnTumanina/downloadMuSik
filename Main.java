package ru.demo.downloadmusic;

import java.io.*;
import java.net.URL;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class Main {

    private static final String IN_FILE_TXT = "src\\ru\\demo\\downloadmusic\\inFile.txt";//ссылка на ресурс
    private static final String OUT_FILE_TXT = "src\\ru\\demo\\downloadmusic\\outFile.txt";//ссылка на кнопку
    private static final String PATH_TO_MUSIC = "src\\ru\\demo\\downloadmusic\\mmmUsicccc";//ссылка на папку с песнями

    public static void main(String[] args) {
        String Url;
        try (BufferedReader inFile = new BufferedReader(new FileReader(IN_FILE_TXT));//читаем ссылку
             BufferedWriter outFile = new BufferedWriter(new FileWriter(OUT_FILE_TXT))) {//записываем музыку
            while ((Url = inFile.readLine()) != null) {
                URL url = new URL(Url);

                String result;//запуск потока чтения
                try (BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(url.openStream()))) {//читаем сайт
                    result = bufferedReader.lines().collect(Collectors.joining("\n"));//прочитываем построчно сайт и собираем строки в коллекции
                }
                Pattern email_pattern = Pattern.compile("\\/load\\/\\d+\\/(.+?(.+?)).mp3");//ищем "ссылку" на скачивание
                Matcher matcher = email_pattern.matcher(result);
                int i = 0;
                if(matcher.find()){
                    System.out.println("Yes");
                }
                else {
                    System.out.println("No");
                }
                while (matcher.find() && i < 10) {//при условии что сайт что то нашёл, записать 10 ссылок
                    outFile.write("https://ruo.morsmusic.org"+matcher.group() + "\r\n");//формируем в группу и записываем в файл ссылки
                    i++;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        try (BufferedReader musicFile = new BufferedReader(new FileReader(OUT_FILE_TXT))) {//прочитываем файл с ссыллками на скачивание музыки
            String music;
            int count = 0;
            try {
                while ((music = musicFile.readLine()) != null) {//читаем файл построчно
                    downloadUsingNIO(music, PATH_TO_MUSIC + String.valueOf(count) + ".mp3");
                    count++;

                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    private static void downloadUsingNIO(String strUrl, String file) throws IOException {
        URL url = new URL(strUrl);
        ReadableByteChannel byteChannel = Channels.newChannel(url.openStream());
        FileOutputStream stream = new FileOutputStream(file);
        stream.getChannel().transferFrom(byteChannel, 0, Long.MAX_VALUE);
        stream.close();
        byteChannel.close();
    }
}
