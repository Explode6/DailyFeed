package com.example.ArticleList.Data;

import com.example.ArticleList.R;

import java.util.ArrayList;
import java.util.Random;


/**
 * The type Data.
 */
public class ShuhaoJiekou {
    static final String[] NAME= {
        "Abbaye de Belloc", "Abbaye du Mont des Cats", "Abertam", "Abondance", "Ackawi",
                "Acorn", "Adelost", "Affidelice au Chablis", "Afuega'l Pitu", "Airag", "Airedale",
                "Aisy Cendre", "Allgauer Emmentaler", "Alverca", "Ambert", "American Cheese",
                "Ami du Chambertin", "Anejo Enchilado", "Anneau du Vic-Bilh", "Anthoriro", "Appenzell",
                "Aragon", "Ardi Gasna", "Ardrahan", "Armenian String", "Aromes au Gene de Marc",
                "Asadero", "Asiago", "Aubisque Pyrenees", "Autun", "Avaxtskyr", "Baby Swiss",
                "Babybel", "Baguette Laonnaise", "Bakers", "Baladi", "Balaton", "Bandal", "Banon",
                "Barry's Bay Cheddar", "Basing", "Basket Cheese", "Bath Cheese", "Bavarian Bergkase"};
    static final String BRIEF = "This is the best cheese! Its history is from 200 years age, and develop ovew these years. please enjoy it.If you like it, you can buy it on www.taobao.com";
    static final int imageResource = R.drawable.ima;
    static ArrayList<Article> getData(){
        ArrayList<Article> data = new ArrayList<>();
        for(int i = 0; i < NAME.length; i++){
            Article article = new Article(NAME[i], imageResource, BRIEF);
            data.add(article);
        }
        return data;
    }

    public static ArrayList<Article> getRandomData(){
        Random random = new Random();
        ArrayList<Article> data = new ArrayList<>();
        for(int i = 0; i < 10; i++){
            Article article = new Article(NAME[random.nextInt(NAME.length)], imageResource, BRIEF);
            data.add(article);
        }
        return data;
    }

    public static ArrayList<String> getName(){
        ArrayList<String> data = new ArrayList<>();
        for(int i = 0; i < NAME.length; i++){
            data.add(NAME[i]);
        }
        return data;
    }
}
