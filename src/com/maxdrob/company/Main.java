package com.maxdrob.company;

import com.google.gson.Gson;
import com.google.gson.annotations.SerializedName;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class Main {

    private static HttpURLConnection connection;

    public static void main(String[] args) throws IOException {

        Scanner sc = new Scanner(System.in);
        System.out.println("Что вы ищете?: ");
        String key = sc.nextLine();
        sc.close();
        String query = generateQuery(key);
        URL url = new URL(query);

        connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("GET");
        connection.setDoInput(true);
        connection.connect();

        InputStream input = connection.getInputStream();
        String contentAsString = new String(input.readAllBytes(), StandardCharsets.UTF_8);
        System.out.println(contentAsString);

        Gson gson = new Gson();
        ResponseDTO result = gson.fromJson(contentAsString, ResponseDTO.class);
        System.out.println();

        List<SearchDTO> searchList = result.getQuery().getSearch();
        List<String> output = new ArrayList<>();

        for (SearchDTO s : searchList) {
            output.add(s.getSnippet());
        }
        System.out.println("Результаты поиска:");


        for (String ans :
                output) {
            Document doc = Jsoup.parse(ans);
            System.out.println(doc.text());
            System.out.println();
        }
    }

    private static String generateQuery(String key) {
        return String.format("https://ru.wikipedia.org/w/api.php?action=query&list=search&utf8=&format=json&srsearch=%s",
                URLEncoder.encode(key, StandardCharsets.UTF_8));
    }

    class ResponseDTO {
        @SerializedName("query")
        private QueryDTO query;

        public QueryDTO getQuery() {
            return query;
        }
    }

    class QueryDTO {
        @SerializedName("search")
        private List<SearchDTO> search;

        public List<SearchDTO> getSearch() {
            return search;
        }
    }

    class SearchDTO {
        @SerializedName("snippet")
        private String snippet;

        public String getSnippet() {
            return snippet;
        }
    }
}
