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
    static final String[] imageList = {"http://cn.bing.com/az/hprichbg/rb/Dongdaemun_ZH-CN10736487148_1920x1080.jpg",
            "https://pic4.zhimg.com/v2-952eb86670a2f12a7a7ef8632d758b35_720w.jpg?rss\n",
            "https://pic2.zhimg.com/v2-37d34283d815e7a49aa7b65376122216_720w.jpg?rss",
            "https://pic3.zhimg.com/v2-fe43b3df3259b1c7d7d539893c272de6_720w.jpg?rss",
            "https://pic4.zhimg.com/v2-e7421fa257134a9c77420658286258d3_720w.jpg?rss",
            "https://pic2.zhimg.com/v2-d4391cd7f554d89fb7b356f278c293e4_720w.jpg?rss",
            "https://pic2.zhimg.com/v2-103e106de9bb686e38e3c3aefe4fb3bf_720w.jpg?rss",
            "https://pic2.zhimg.com/v2-cc1aeb95b68b63c5ec13a5a32b02e823_720w.jpg?rss",
            "https://pic1.zhimg.com/v2-0d04b67dae937c4a2c0be5d789fb0702_720w.jpg?rss",
            "https://pic4.zhimg.com/v2-4158a096b69bf7f37a415eacc61bd29f_720w.jpg?rss",
            "https://pic1.zhimg.com/v2-952eb86670a2f12a7a7ef8632d758b35_720w.jpg?rss",
            "https://pic2.zhimg.com/v2-37d34283d815e7a49aa7b65376122216_720w.jpg?rss"
    };
    static final String imageResource = "https://pic4.zhimg.com/v2-952eb86670a2f12a7a7ef8632d758b35_720w.jpg?rss\n";
    static public ArrayList<ArticleBrief> getData(int begin, int size){
        ArrayList<ArticleBrief> data = new ArrayList<>();
        for(int i = begin; i < begin + size; i++){
            ArticleBrief articleBrief;
            if(i == NAME.length){
                break;
            }
            if(i%imageList.length == 0){
                articleBrief = new ArticleBrief(NAME[i], BRIEF);
            }
            else {
                articleBrief = new ArticleBrief(NAME[i], imageList[i%imageList.length], BRIEF);
            }
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
