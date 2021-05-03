package com.example.ArticleList.Data;

import com.example.ArticleList.R;

import java.util.ArrayList;
import java.util.Random;


/**
 * The type Data.
 */
public class ShuhaoJiekou {
    static final String[] NAME= {
        "1Abbaye de Belloc", "2Abbaye du Mont des Cats", "3Abertam", "4Abondance", "5Ackawi",
                "6Acorn", "7Adelost", "8Affidelice au Chablis", "9Afuega'l Pitu", "10Airag", "11Airedale",
                "12Aisy Cendre", "13Allgauer Emmentaler", "14Alverca", "15Ambert", "16American Cheese",
                "17Ami du Chambertin", "18Anejo Enchilado", "19Anneau du Vic-Bilh", "20Anthoriro", "21Appenzell",
                "22Aragon", "23Ardi Gasna", "24Ardrahan", "25Armenian String", "26Aromes au Gene de Marc",
                "27Asadero", "28Asiago", "29Aubisque Pyrenees", "30Autun", "31Avaxtskyr", "32Baby Swiss",
                "33Babybel", "34Baguette Laonnaise", "35Bakers", "36Baladi", "37Balaton", "38Bandal", "39Banon",
                "40Barry's Bay Cheddar", "41Basing", "42Basket Cheese", "43Bath Cheese", "44Bavarian Bergkase"};
    static final String BRIEF = "This is the best cheese! Its history is from 200 years age, and develop ovew these years. please enjoy it.If you like it, you can buy it on www.taobao.com";
    static final int imageResource = R.drawable.ima;
    static public ArrayList<ArticleBrief> getData(int begin, int size){
        ArrayList<ArticleBrief> data = new ArrayList<>();
        for(int i = begin; i < begin + size; i++){
            if(i == NAME.length){
                break;
            }
            ArticleBrief articleBrief = new ArticleBrief(NAME[i], imageResource, BRIEF);
            data.add(articleBrief);
        }
        return data;
    }

    public static ArrayList<ArticleBrief> getRandomData(){
        Random random = new Random();
        ArrayList<ArticleBrief> data = new ArrayList<>();
        for(int i = 0; i < 10; i++){
            ArticleBrief articleBrief = new ArticleBrief(NAME[random.nextInt(NAME.length)], imageResource, BRIEF);
            data.add(articleBrief);
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
