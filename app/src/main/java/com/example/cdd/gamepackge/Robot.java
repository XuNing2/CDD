package com.example.cdd.gamepackge;

import android.util.Log;
import android.widget.Toast;

import java.util.ArrayList;

public class Robot{
    private int playerId;
    private boolean isHuman;
    private PlayerState playerState;
    //手牌
    private ArrayList<Card> cardsAtHand;
    //要打的牌
    private ArrayList<Card> cardsToShow;

    //新添加的代码
    //五个二维数组
    private ArrayList<ArrayList<Card>> cardPack_1 = new ArrayList<>();//用来装单张牌
    private ArrayList<ArrayList<Card>> cardPack_2 = new ArrayList<>();//用来装对子
    private ArrayList<ArrayList<Card>> cardPack_3 = new ArrayList<>();//用来装三连
    private ArrayList<ArrayList<Card>> cardPack_4 = new ArrayList<>();//用来装四条，
    private ArrayList<ArrayList<Card>> cardPack_5 = new ArrayList<>();//用来装五个一组的牌组


    //

    //可能出问题
    public Robot(){
    playerId = -1;
    playerState = PlayerState.Passed;
    cardsAtHand = new ArrayList<Card>();
    cardsToShow = new ArrayList<Card>();
    isHuman=false;
//    this.updateCardPacks();
    }


    //可能出问题
    public int RobotFirstShow(){
        this.updateCardPacks();
        int temp=0;
        for (int i=0;i<cardPack_1.size();i++){
            if (cardPack_1.get(i).get(0).getCardId()==41){
                cardsToShow.add(cardPack_1.get(i).get(0));
               if(Ruler.CanPlayerShowCard(this)) {
                   temp=cardsToShow.size();
                   showCards();
                   Ruler.toTheTurnOfNextPlayer(this);

               }
               break;
            }
        }
        return temp;
    }

    public void setHumanTrue(){
        isHuman=true;
    }

    public int getPlayerId() {
        return playerId;
    }

    public PlayerState getPlayerState() {
        return playerState;
    }

    public ArrayList<Card> getCardsToShow() {
        return cardsToShow;
    }

    public void setPlayerId(int playerId) {
        this.playerId = playerId;
    }

    public void setPlayerState(PlayerState playerState) {
        this.playerState = playerState;
    }

    public void addCardAtHand(Card card){
        cardsAtHand.add(card);
    }

    public ArrayList<Card> getCardsAtHand() {
        return cardsAtHand;
    }

    public void removeCardsAtHand(Card card){
        cardsAtHand.remove(card);
    }

    public void removeAllCardsAtHand(){
        for(int i = 0; i < cardsAtHand.size(); ++i){
            cardsAtHand.remove(i--);
        }
    }

    public void addCardToShow(Card card){
        cardsToShow.add(card);
    }

    public void addCardToShow(int ID){
        for(int i=0;i<cardsAtHand.size();i++){
            if (ID==cardsAtHand.get(i).getCardId()){
                cardsToShow.add(cardsAtHand.get(i));
            }
        }
    }
    public void removeCardsToShow(Card card){
        cardsToShow.remove(card);
    }
    public void removeCardsToShow(int ID){
        for(int i=0;i<cardsAtHand.size();i++){
            if (ID==cardsAtHand.get(i).getCardId()){
                cardsToShow.remove(cardsAtHand.get(i));
            }
        }
    }

    public void removeAllCardsToShow(){
        cardsToShow.clear();
    }

    //出牌,先删除桌上原有的牌，再添加现在桌上的牌
    public void showCards() {

        if(cardsToShow.size()!=0) {
            Ruler.removeAllCardsShowedByPreviousPlayer();
            Ruler.addCardToCardsShowedByPreviousPlayer(getCardsToShow());
            Ruler.setTypeOfCards(cardsToShow);
            //改变牌的状态和拥有者
            for (int i = 0; i < getCardsToShow().size(); ++i) {
                getCardsToShow().get(i).setCardState(CardState.Showed);
                getCardsToShow().get(i).setOwner(-1);
            }

            //根据出牌的情况从手牌中删除已经打出的牌
            for (int i = 0; i < getCardsToShow().size(); ++i) {
                removeCardsAtHand(getCardsToShow().get(i));
            }
                Ruler.setIdOfPlayerWhoShowCardSucessfully(this.playerId);
            //将待出牌序列清空
            removeAllCardsToShow();
        }
    }

    //新添加的代码

    //传入参数：五张牌的牌组，上家打出的牌
    //ok
    public ArrayList<Card> getFlush(ArrayList<ArrayList<Card>> cardPack, ArrayList<Card> cardsShowed) {
    //10121
        //获得上家打的同花顺中最大的牌的值
        Ruler.sort(cardsShowed);
        int maxNumOfCardsShowed = Ruler.getMaxiumCard(cardsShowed, TypeOfCards.Flush).getCardValue();

        //手里有一副趟儿
        if (cardPack.size() == 1) {
            //如果没有同花顺
            if (!Ruler.willBeFlush(cardPack_5.get(0)))
                return null;

            //获得手里同花顺最大的卡牌的数字
            Ruler.sort(cardPack.get(0));
            int maxNum = Ruler.getMaxiumCard(cardPack.get(0), TypeOfCards.Flush).getCardValue();

            //手里同花顺最大的卡牌的数字比上家的最大的卡牌的数字大
            if (maxNum > maxNumOfCardsShowed)
                //返回可出的牌
                return cardPack.get(0);

            //同花顺两者最大卡牌数字一样
            else if (maxNum == maxNumOfCardsShowed) {

                //比较两张牌的花色大小。0:card1小于card2；1：等于；2：大于
                //因为是同花顺，所以各取第一张牌比较就行
                int compareResultOfCards = Ruler.compareTypesOfTwoCards(cardPack.get(0).get(0), cardsShowed.get(0));

                if (compareResultOfCards == 2) { //可以出牌
                    return cardPack.get(0);
                }
            }

        }

        //手里有两副趟儿
        if (cardPack.size() == 2) {
            //如果没有同花顺
            if (!Ruler.willBeFlush(cardPack_5.get(0)) && !Ruler.willBeFlush(cardPack_5.get(1)))
                return null;

            if (Ruler.willBeFlush(cardPack_5.get(0))) {//判断第一个卡组

                Ruler.sort(cardPack.get(0));
                int maxNum = Ruler.getMaxiumCard(cardPack.get(0), TypeOfCards.Flush).getCardValue();

                if (maxNum > maxNumOfCardsShowed)
                    return cardPack.get(0);
                else if (maxNum == maxNumOfCardsShowed) {


                    //比较两张牌的花色大小。0:card1小于card2；1：等于；2：大于
                    int compareResultOfCards = Ruler.compareTypesOfTwoCards(cardPack.get(0).get(0), cardsShowed.get(0));

                    if (compareResultOfCards == 2) { //可以出牌
                        return cardPack.get(0);
                    }
                }
            }

            if (Ruler.willBeFlush(cardPack_5.get(1))) {//判断第二个卡组

                Ruler.sort(cardPack.get(1));
                int maxNum = Ruler.getMaxiumCard(cardPack.get(1), TypeOfCards.Flush).getCardValue();
                if (maxNum > maxNumOfCardsShowed)
                    return cardPack.get(1);
                else if (maxNum == maxNumOfCardsShowed) {


                    //比较两张牌的花色大小。0:card1小于card2；1：等于；2：大于
                    int compareResultOfCards = Ruler.compareTypesOfTwoCards(cardPack.get(1).get(0), cardsShowed.get(0));

                    if (compareResultOfCards == 2) { //可以出牌
                        return cardPack.get(1);
                    }

                }

            }
            return null;
        }
        return null;
    }

//ok
    public ArrayList<Card> getCardsAgainstFour_one(ArrayList<ArrayList<Card>> cardPack, ArrayList<Card> cardsShowed) {
        if (cardPack.size() == 0)
            return null;

        if(cardPack.size()==1) {
            //有同花顺直接打
            if (Ruler.willBeFlush(cardPack.get(0)))
                return cardPack.get(0);

            //如果没有四带一
            if (!Ruler.willBeFour_One(cardPack.get(0)))
                return null;

            //分别求上家出牌的四带一的最大值和我手上的四带一的最大值

            //按照注释的要求，求出最大值前先排序

            Ruler.sort(cardsShowed);
            Card max_card_pre = Ruler.getMaxiumCard(cardsShowed,TypeOfCards.Four_One);

            //按照注释的要求，求出最大值前先排序
            Ruler.sort(cardPack.get(0));
            Card max_card_of_mine = Ruler.getMaxiumCard(cardPack.get(0),TypeOfCards.Four_One);

            //比较两张牌的数值大小,不包含顺子。0:card1小于card2；1：等于；2：大于。
            int result_of_comparing= Ruler.compareValuesOfTwoCards( max_card_of_mine,max_card_pre);
            if(result_of_comparing==2)
                return cardPack.get(0);
            else return null;

        }
        if(cardPack.size()==2)
        {
            //有同花顺直接打
            if (Ruler.willBeFlush(cardPack.get(0)))
                return cardPack.get(0);
            else if (Ruler.willBeFlush(cardPack.get(1)))
                return cardPack.get(1);


            //如果没有四带一
            if ((!Ruler.willBeFour_One(cardPack.get(0)))&&(!Ruler.willBeFour_One(cardPack.get(1))))
                return null;

            //检查第一组牌
            if(Ruler.willBeFour_One(cardPack.get(0)))
            {
                //分别求上家出牌的四带一的最大值和我手上的四带一的最大值

                //按照注释的要求，求出最大值前先排序
                Ruler.sort(cardsShowed);
                Card max_card_pre = Ruler.getMaxiumCard(cardsShowed,TypeOfCards.Four_One);

                //按照注释的要求，求出最大值前先排序
                Ruler.sort(cardPack.get(0));
                Card max_card_of_mine = Ruler.getMaxiumCard(cardPack.get(0),TypeOfCards.Four_One);

                //比较两张牌的数值大小,不包含顺子。0:card1小于card2；1：等于；2：大于。
                int result_of_comparing= Ruler.compareValuesOfTwoCards(max_card_of_mine,max_card_pre);
                if(result_of_comparing==2)
                    return cardPack.get(0);
            }


            //检查第二组牌
            if(Ruler.willBeFour_One(cardPack.get(1))) {
                //分别求上家出牌的四带一的最大值和我手上的四带一的最大值

                //按照注释的要求，求出最大值前先排序
                Ruler.sort(cardsShowed);
                Card max_card_pre = Ruler.getMaxiumCard(cardsShowed,TypeOfCards.Four_One);

                //按照注释的要求，求出最大值前先排序
                Ruler.sort(cardPack.get(1));
                Card max_card_of_mine = Ruler.getMaxiumCard(cardPack.get(1),TypeOfCards.Four_One);

                //比较两张牌的数值大小,不包含顺子。0:card1小于card2；1：等于；2：大于。
                int result_of_comparing= Ruler.compareValuesOfTwoCards(max_card_of_mine,max_card_pre);
                if(result_of_comparing==2)
                    return cardPack.get(1);
                else return null;
            }
        }
        return null;
    }

    public ArrayList<Card> getCardsAgainstThree_Two(ArrayList<ArrayList<Card>> cardPack, ArrayList<Card> cardsShowed) {
        if (cardPack.size() == 0)
            return null;

        if(cardPack.size()==1)
        {
            //有同花顺直接打 //有四带一直接打
            if (Ruler.willBeFlush(cardPack.get(0))||Ruler.willBeFour_One(cardPack.get(0)))
                return cardPack.get(0);


            //如果没有三带二
            if (!Ruler.willBeThree_Two(cardPack.get(0)))
                return null;


            //分别求上家出牌的四带一的最大值和我手上的四带一的最大值

            //按照注释的要求，求出最大值前先排序
            Ruler.sort(cardsShowed);
            Card max_card_pre = Ruler.getMaxiumCard(cardsShowed,TypeOfCards.Three_Two);

            //按照注释的要求，求出最大值前先排序
            Ruler.sort(cardPack.get(0));
            Card max_card_of_mine = Ruler.getMaxiumCard(cardPack.get(0),TypeOfCards.Three_Two);

            //比较两张牌的数值大小,不包含顺子。0:card1小于card2；1：等于；2：大于。
            int result_of_comparing= Ruler.compareValuesOfTwoCards(max_card_of_mine,max_card_pre);
            if(result_of_comparing==2)
                return cardPack.get(0);
            else return null;
        }
        if(cardPack.size()==2)
        {
            //有同花顺直接打 //有四带一直接打
            if (Ruler.willBeFlush(cardPack.get(0))||Ruler.willBeFour_One(cardPack.get(0)))
                return cardPack.get(0);
            else if (Ruler.willBeFlush(cardPack.get(1))||Ruler.willBeFour_One(cardPack.get(1)))
                return cardPack.get(1);


            //如果没有三带二
            if (!Ruler.willBeThree_Two(cardPack.get(0))&&!Ruler.willBeThree_Two(cardPack.get(1)))
                return null;
            //检查第一组牌
            if(Ruler.willBeThree_Two(cardPack.get(0)))
            {
                //分别求上家出牌的四带一的最大值和我手上的四带一的最大值

                //按照注释的要求，求出最大值前先排序
                Ruler.sort(cardsShowed);
                Card max_card_pre = Ruler.getMaxiumCard(cardsShowed,TypeOfCards.Three_Two);

                //按照注释的要求，求出最大值前先排序
                Ruler.sort(cardPack.get(0));
                Card max_card_of_mine = Ruler.getMaxiumCard(cardPack.get(0),TypeOfCards.Three_Two);

                //比较两张牌的数值大小,不包含顺子。0:card1小于card2；1：等于；2：大于。
                int result_of_comparing= Ruler.compareValuesOfTwoCards(max_card_of_mine,max_card_pre);
                if(result_of_comparing==2)
                    return cardPack.get(0);
            }


            //检查第二组牌
            if(Ruler.willBeThree_Two(cardPack.get(1)))
            {
                //分别求上家出牌的四带一的最大值和我手上的四带一的最大值

                //按照注释的要求，求出最大值前先排序
                Ruler.sort(cardsShowed);
                Card max_card_pre = Ruler.getMaxiumCard(cardsShowed,TypeOfCards.Three_Two);

                //按照注释的要求，求出最大值前先排序
                Ruler.sort(cardPack.get(1));
                Card max_card_of_mine = Ruler.getMaxiumCard(cardPack.get(1),TypeOfCards.Three_Two);

                //比较两张牌的数值大小,不包含顺子。0:card1小于card2；1：等于；2：大于。
                int result_of_comparing= Ruler.compareValuesOfTwoCards( max_card_of_mine,max_card_pre);
                if(result_of_comparing==2)
                    return cardPack.get(1);
                else return null;
            }
        }
        return null;
    }

    public ArrayList<Card> getCardsAgainstFive_The_Same_Type(ArrayList<ArrayList<Card>> cardPack, ArrayList<Card> cardsShowed) {
        if (cardPack.size() == 0)
            return null;

        if (cardPack.size() == 1) {
            //有同花顺直接打 //有四带一直接打//有三带二直接打
            if (Ruler.willBeFlush(cardPack.get(0)) || Ruler.willBeFour_One(cardPack.get(0))) {
                Ruler.willBeThree_Two(cardPack.get(0));
                return cardPack.get(0);
            }

            //如果没有同花
            if (!Ruler.willBeThree_Two(cardPack.get(0)))
                return null;


            //分别求上家出牌的同花的最大值和我手上的同花的最大值

            //按照注释的要求，求出最大值前先排序
            Ruler.sort(cardsShowed);
            Card max_card_pre = Ruler.getMaxiumCard(cardsShowed, TypeOfCards.Five_Same_Type);

            //按照注释的要求，求出最大值前先排序
            Ruler.sort(cardPack.get(0));
            Card max_card_of_mine = Ruler.getMaxiumCard(cardPack.get(0), TypeOfCards.Five_Same_Type);

            //比较两张牌的数值大小,不包含顺子。0:card1小于card2；1：等于；2：大于。
            int result_of_comparing_1 = Ruler.compareValuesOfTwoCards(max_card_pre, max_card_of_mine);
            if (result_of_comparing_1 == 2)
                return cardPack.get(0);

            //如果相同，则比较花色
            if (result_of_comparing_1 == 1) {
                int result_of_comparing_2 = Ruler.compareTypesOfTwoCards(max_card_pre, max_card_of_mine);
                if (result_of_comparing_2 == 2)
                    return cardPack.get(0);
            } else return null;


        }
        if (cardPack.size() == 2) {
            //检查第一副牌组
            //有同花顺直接打 //有四带一直接打//有三带二直接打
            if (Ruler.willBeFlush(cardPack.get(0)) || Ruler.willBeFour_One(cardPack.get(0)) ||
                    Ruler.willBeThree_Two(cardPack.get(0)))
                return cardPack.get(0);


            //如果有同花
            if (Ruler.willBeFive_Same_Type(cardPack.get(0))) {

                //分别求上家出牌的同花的最大值和我手上的同花的最大值

                //按照注释的要求，求出最大值前先排序
                Ruler.sort(cardsShowed);
                Card max_card_pre = Ruler.getMaxiumCard(cardsShowed, TypeOfCards.Five_Same_Type);

                //按照注释的要求，求出最大值前先排序
                Ruler.sort(cardsShowed);
                Card max_card_of_mine = Ruler.getMaxiumCard(cardPack.get(0), TypeOfCards.Five_Same_Type);

                //比较两张牌的数值大小,不包含顺子。0:card1小于card2；1：等于；2：大于。
                int result_of_comparing_1 = Ruler.compareValuesOfTwoCards( max_card_of_mine,max_card_pre);
                if (result_of_comparing_1 == 2)
                    return cardPack.get(0);

                //如果相同，则比较花色
                if (result_of_comparing_1 == 1) {
                    int result_of_comparing_2 = Ruler.compareTypesOfTwoCards( max_card_of_mine,max_card_pre);
                    if (result_of_comparing_2 == 2)
                        return cardPack.get(0);
                }
            }


            //检查第二组牌
            //有同花顺直接打 //有四带一直接打//有三带二直接打
            if (Ruler.willBeFlush(cardPack.get(1)) || Ruler.willBeFour_One(cardPack.get(1)) ||
                    Ruler.willBeThree_Two(cardPack.get(1)))
                return cardPack.get(1);


            //如果有同花
            if (Ruler.willBeFive_Same_Type(cardPack.get(1))) {

                //分别求上家出牌的同花的最大值和我手上的同花的最大值

                //按照注释的要求，求出最大值前先排序
                Ruler.sort(cardsShowed);
                Card max_card_pre = Ruler.getMaxiumCard(cardsShowed, TypeOfCards.Five_Same_Type);

                //按照注释的要求，求出最大值前先排序
                Ruler.sort(cardPack.get(1));
                Card max_card_of_mine = Ruler.getMaxiumCard(cardPack.get(1), TypeOfCards.Five_Same_Type);

                //比较两张牌的数值大小,不包含顺子。0:card1小于card2；1：等于；2：大于。
                int result_of_comparing_1 = Ruler.compareValuesOfTwoCards(max_card_of_mine,max_card_pre );
                if (result_of_comparing_1 == 2)
                    return cardPack.get(1);

                //如果相同，则比较花色
                if (result_of_comparing_1 == 1) {
                    int result_of_comparing_2 = Ruler.compareTypesOfTwoCards( max_card_of_mine,max_card_pre);
                    if (result_of_comparing_2 == 2)
                        return cardPack.get(1);
                }
            }
            return null;
        }
        return null;
    }
    public ArrayList<Card> getCardsAgainstShunZi(ArrayList<ArrayList<Card>> cardPack, ArrayList<Card> cardsShowed) {
        if (cardPack.size() == 0)
            return null;

        if(cardPack.size()==1)
        {
            //有同花顺直接打 //有四带一直接打//有三带二直接打//有同花直接打
            if (Ruler.willBeFlush(cardPack.get(0))||Ruler.willBeFour_One(cardPack.get(0))||
                    Ruler.willBeThree_Two(cardPack.get(0))||Ruler.willBeFive_Same_Type(cardPack.get(0)))
                return cardPack.get(0);


            //如果没有顺子
            if (!Ruler.willBeThree_Two(cardPack.get(0)))
                return null;


            //分别求上家出牌的顺子的最大值和我手上的顺子的最大值

            //按照注释的要求，求出最大值前先排序
            Ruler.sort(cardsShowed);
            Card max_card_pre = Ruler.getMaxiumCard(cardsShowed,TypeOfCards.Shunzi);

            //按照注释的要求，求出最大值前先排序
            Ruler.sort(cardPack.get(0));
            Card max_card_of_mine = Ruler.getMaxiumCard(cardPack.get(0),TypeOfCards.Shunzi);

            //比较两张牌的数值大小。0:card1小于card2；1：等于；2：大于。
            int result_of_comparing_1= Ruler.compareValuesOfTwoCards(max_card_of_mine,max_card_pre);
            if(result_of_comparing_1==2)
                return cardPack.get(0);

            //如果相同，则比较花色
            if(result_of_comparing_1==1)
            {
                int result_of_comparing_2= Ruler.compareTypesOfTwoCards(max_card_of_mine,max_card_pre);
                if(result_of_comparing_2==2)
                    return cardPack.get(0);
            }

            else return null;


        }
        if(cardPack.size()==2)
        {
            //检查第一副牌组
            //有同花顺直接打 //有四带一直接打//有三带二直接打//有同花直接打
            if (Ruler.willBeFlush(cardPack.get(0))||Ruler.willBeFour_One(cardPack.get(0))
                    ||Ruler.willBeThree_Two(cardPack.get(0))||Ruler.willBeFive_Same_Type(cardPack.get(0)))
                return cardPack.get(0);


            //如果有顺子
            if (Ruler.willBeFive_Same_Type(cardPack.get(0))) {
                //分别求上家出牌的顺子的最大值和我手上的顺子的最大值

                //按照注释的要求，求出最大值前先排序
                Ruler.sort(cardsShowed);
                Card max_card_pre = Ruler.getMaxiumCard(cardsShowed, TypeOfCards.Shunzi);

                //按照注释的要求，求出最大值前先排序
                Ruler.sort(cardPack.get(0));
                Card max_card_of_mine = Ruler.getMaxiumCard(cardPack.get(0), TypeOfCards.Shunzi);

                //比较两张牌的数值大小,不包含顺子。0:card1小于card2；1：等于；2：大于。
                int result_of_comparing_1 = Ruler.compareValuesOfTwoCards(max_card_pre, max_card_of_mine);
                if (result_of_comparing_1 == 2)
                    return cardPack.get(0);

                //如果相同，则比较花色
                if (result_of_comparing_1 == 1) {
                    int result_of_comparing_2 = Ruler.compareTypesOfTwoCards(max_card_pre, max_card_of_mine);
                    if (result_of_comparing_2 == 2)
                        return cardPack.get(0);
                }
            }


            //检查第二组牌
            //有同花顺直接打 //有四带一直接打//有三带二直接打
            if (Ruler.willBeFlush(cardPack.get(1))||Ruler.willBeFour_One(cardPack.get(1))||
                    Ruler.willBeThree_Two(cardPack.get(1)))
                return cardPack.get(1);


            //如果没有顺子
            if (Ruler.willBeFive_Same_Type(cardPack.get(1))) {

                //分别求上家出牌的同花的最大值和我手上的同花的最大值

                //按照注释的要求，求出最大值前先排序
                Ruler.sort(cardsShowed);
                Card max_card_pre = Ruler.getMaxiumCard(cardsShowed, TypeOfCards.Shunzi);

                //按照注释的要求，求出最大值前先排序
                Ruler.sort(cardPack.get(1));
                Card max_card_of_mine = Ruler.getMaxiumCard(cardPack.get(1), TypeOfCards.Shunzi);

                //比较两张牌的数值大小。0:card1小于card2；1：等于；2：大于。
                int result_of_comparing_1 = Ruler.compareValuesOfTwoCards(max_card_of_mine,max_card_pre);
                if (result_of_comparing_1 == 2)
                    return cardPack.get(1);

                //如果相同，则比较花色
                if (result_of_comparing_1 == 1) {
                    int result_of_comparing_2 = Ruler.compareTypesOfTwoCards(max_card_of_mine,max_card_pre);
                    if (result_of_comparing_2 == 2)
                        return cardPack.get(1);
                }
            }
        }
        return null;
    }

    public void updateCardPacks(){

        //先取一张牌的组，存入cardPack_1
        if (cardsAtHand==null){
            System.exit(0);
        }
        cardPack_1.clear();
        cardPack_2.clear();
        cardPack_3.clear();
        cardPack_4.clear();
        cardPack_5.clear();
        for (int i = 0; i < cardsAtHand.size(); i++) {
            ArrayList<Card> cards = new ArrayList<>();
            Card card = cardsAtHand.get(i);
            cards.add(card);
            cardPack_1.add(cards);
        }

        //从头至倒数第一检阅cardPack_1，以检查对的存在（三连只能检测出两组）
        for (int i = 0; i < cardPack_1.size() - 1; i++) {
            Card card_1 = cardPack_1.get(i).get(0);
            Card card_2 = cardPack_1.get(i + 1).get(0);
            if (card_1.getCardValue() == card_2.getCardValue()) {
                ArrayList<Card> cards = new ArrayList<>();
                cards.add(card_1);
                cards.add(card_2);
                cardPack_2.add(cards);
            }
        }


        //从头至倒数第一检阅cardPack_2, 以检查三连的存在
        int cardPack_2_Size = cardPack_2.size();
        for (int i = 0; i < cardPack_2_Size - 1; i++) {
            //检查第i组的第0张和第i+1组的第1张，若相同，存入三张到cardPack_3并且也存入两张到cardPack_2以补充上面的
            Card card_1 = cardPack_2.get(i).get(0);
            Card card_3 = cardPack_2.get(i + 1).get(1);
            if (card_1.getCardValue() == card_3.getCardValue()) {
                ArrayList<Card> cards = new ArrayList<>();
                cards.add(card_1);
                cards.add(cardPack_2.get(i).get(1));
                cards.add(card_3);
                cardPack_3.add(cards);

                //这里把两张牌补充至cardPack_2
                ArrayList<Card> cards_added = new ArrayList<>();
                cards_added.add(card_1);
                cards_added.add(card_3);
                cardPack_2.add(cards_added);

            }
        }

        //从头至倒数第一检阅cardPack_3, 以检查四连的存在
        int cardPack_3_Size = cardPack_3.size();
        for (int i = 0; i < cardPack_3_Size - 1; i++) {
            //检查第i组的第0张和第i+1组的第2张，若相同，存入四张到cardPack_4并且也存入三张到cardPack_3以补充上面的
            //并且，补充头尾两张至cardPack_2
            Card card_1 = cardPack_3.get(i).get(0);
            Card card_4 = cardPack_3.get(i + 1).get(2);
            if (card_1.getCardValue() == card_4.getCardValue()) {
                Card card_2 = cardPack_3.get(i).get(1);
                Card card_3 = cardPack_3.get(i).get(2);
                ArrayList<Card> cards = new ArrayList<>();
                cards.add(card_1);
                cards.add(card_2);
                cards.add(card_3);
                cards.add(card_4);
                cardPack_4.add(cards);

                //这里把三张牌补充至cardPack_3
                ArrayList<Card> cards_added_3_1 = new ArrayList<>();
                ArrayList<Card> cards_added_3_2 = new ArrayList<>();
                //先补充1、2、4张
                cards_added_3_1.add(card_1);
                cards_added_3_1.add(card_2);
                cards_added_3_1.add(card_4);
                //再补充1、3、4张
                cards_added_3_2.add(card_1);
                cards_added_3_2.add(card_3);
                cards_added_3_2.add(card_4);

                cardPack_3.add(cards_added_3_1);
                cardPack_3.add(cards_added_3_2);

                //这里把两张牌补充至cardPack_2
                ArrayList<Card> cards_added_2 = new ArrayList<>();
                //补充首尾两张
                cards_added_2.add(card_1);
                cards_added_2.add(card_4);

                cardPack_2.add(cards_added_2);
            }
        }



        //这里按次序检查顺子、同花顺、同花、葫芦和金刚的存在

        //顺子
        ArrayList<ArrayList<Card>> cardBucket = new ArrayList<>();//建一个二维数组，每个列存入相同点数的牌
        for (int i = 0; i < cardsAtHand.size(); ) {
            ArrayList<Card> cardsFigureOnly = new ArrayList<>();
            cardsFigureOnly.add(cardsAtHand.get(i));
            i++;
            while (i < cardsAtHand.size()) {
                if (cardsAtHand.get(i).getCardValue() == cardsAtHand.get(i - 1).getCardValue()) {
                    cardsFigureOnly.add(cardsAtHand.get(i));
                    i++;
                } else {
                    break;
                }
            }
            cardBucket.add(cardsFigureOnly);
        }


        //连续检阅cardBucket每五项的第一张牌, 若点数递增则存入所有排列组合(不包括A2345和23456)
        for (int i = 0; i < cardBucket.size() - 4; i++) {
            ArrayList<Card> cardsSample = new ArrayList<>();//取第一张作为样本，因为后面的几张的数字完全相同
            for (int j = i; j < i + 5; j++) {
                cardsSample.add(cardBucket.get(j).get(0));
            }
            sequentialCardMatch(cardPack_5,cardBucket,cardsSample,i, i + 1, i + 2, i + 3, i + 4);
        }

        //葫芦
        //葫芦取cardPack_3和cardPack_2中不一样点数的进行排列组合, 由于葫芦是看3张牌的最大张大小, 因此2张牌的部分越小越有利
        //所以3张牌的3张出牌权重直接降低，2张牌的5张出牌权重按照点数从小到大越逐渐降低。
        for (int i = 0; i < cardPack_3.size(); i++) {
            ArrayList<Card> cards_3 = new ArrayList<>();
            cards_3.addAll(cardPack_3.get(i));
            for (int i1 = 0; i1 < cardPack_2.size(); i1++) {
                if (cardPack_2.get(i1).get(0).getCardValue() != cards_3.get(0).getCardValue()) {
                    ArrayList<Card> cards_2 = cardPack_2.get(i1);

                    ArrayList<Card> cards = new ArrayList<>();
                    cards.addAll(cards_2);
                    cards.addAll(cards_3);
                    cardPack_5.add(cards);
                }
            }
        }

        //金刚
        //金刚最好判, 因为只要四带一，任意一张都可以拼
        //但是, 其中的5张出牌权重要根据牌从大到小递增, 并且要根据牌的1张出牌权重从大到小递减
        for (int i = 0; i < cardPack_4.size(); i++) {
            for (int i1 = 0; i1 < cardsAtHand.size(); i1++) {
                if (cardsAtHand.get(i1).getCardValue() != cardPack_4.get(i).get(0).getCardValue()) {
                    Card card = cardsAtHand.get(i1);
                    ArrayList<Card> cards_4 = cardPack_4.get(i);

                    ArrayList<Card> cards = new ArrayList<>();
                    cards.add(card);
                    cards.addAll(cards_4);
                    cardPack_5.add(cards);
                }
            }
        }

    }

    public void randomCardShow(){

        if (cardsAtHand.size()==0){
            System.exit(0);
        }
        if (this.playerState != PlayerState.PlayerRound)  //是否是此玩家的回合
            return ;   //如果不是则报错
        //修改
        Ruler.sortForTypeOfSingle(cardsAtHand);
        cardsToShow.add(cardsAtHand.get(0));
    }
    //-1 不是他的回合， 0为pass， 1为出牌
    public int  RobotShowCard() {
        Log.d("s","更新前");
        this.updateCardPacks();
        Log.d("s","更新后");
        if (this.playerState != PlayerState.PlayerRound)  //是否是此玩家的回合
            return -1;   //如果不是则报错
        //是则进行下一步
//        if(!Ruler.CanPlayerShowCard(this)){
//            cardsToShow.clear();
//            //    return;
//        }

        //获取上一个玩家出的牌
        ArrayList<Card> cardsShowed = Ruler.getCardsShowedByPreviousPlayer();
        //修改
        Ruler.sortForTypeOfSingle(cardsAtHand);
        Log.d("s","单牌排序后");
        switch (cardsShowed.size()) {
            case 1:
                if (cardsAtHand.size()==0){
                    System.exit(0);
                }
                int compare_result1_of_case1 = 0;    //手里的牌与上家的牌比较结果
                for (int i = 0; i < cardsAtHand.size(); i++) { //如果
                    //0<;1==;2>
                    compare_result1_of_case1 = Ruler.compareValuesOfTwoCards(this.cardsAtHand.get(i), cardsShowed.get(0));
                    if (compare_result1_of_case1 == 2) {
                        //将这张牌加入cardsToShow里面
                        // 不考虑这张牌是否可以组成三连、三带二、四带一、顺子等
                        cardsToShow.add(cardsAtHand.get(i));
                        return 1;
                    }
                    if (compare_result1_of_case1 == 1) {
                        //两张牌相等时
                        // 不考虑这张牌是否可以组成三连、三带二、四带一、顺子等
                        int compareResult2 = Ruler.compareTypesOfTwoCards(this.cardsAtHand.get(i), cardsShowed.get(0));
                        if (compareResult2 == 2) {
                            //将这张牌加入cardsToShow里面
                            // 不考虑这张牌是否可以组成三连、三带二、四带一、顺子等
                            cardsToShow.add(cardsAtHand.get(i));
                            return 1;
                        }
                    }

                }
                Log.d("tttoo", "一张pass");
                pass();
                return 0;
            case 2:
                if (cardsAtHand.size()==0){
                    System.exit(0);
                }
                int compare_result1_of_case2 = 0;    //手里的牌与上家的牌比较结果
                int compare_result2_of_case2 = 0;
                if (this.cardsAtHand.size() >= 2) {
                    for (int i = 0; i < cardsAtHand.size() - 1; i++) { //如果
                        //0<;1==;2>
                        compare_result1_of_case2 = Ruler.compareValuesOfTwoCards(this.cardsAtHand.get(i), cardsShowed.get(0));

                        if (compare_result1_of_case2 == 2) {
                            compare_result2_of_case2 = Ruler.compareValuesOfTwoCards(this.cardsAtHand.get(i), this.cardsAtHand.get(i + 1));
                            if (compare_result2_of_case2 == 1) {
                                cardsToShow.add(cardsAtHand.get(i));
                                cardsToShow.add(cardsAtHand.get(i + 1));
                                return 1;
                            }
                        }
                    }
                }
                Log.d("147", "两张pass");
                pass();
                return 0;
            case 3: //三条
                if (cardsAtHand.size()==0){
                    System.exit(0);
                }
                int compareResult1 = 0;    //手里的牌与上家的牌比较结果
                int compareResult2 = 0;
                int compareResult3 = 0;
                if (this.cardsAtHand.size() >= 3) {
                    for (int i = 0; i < cardsAtHand.size() - 2; i++) { //如果
                        //0<;1==;2>
                        compareResult1 = Ruler.compareValuesOfTwoCards(this.cardsAtHand.get(i), cardsShowed.get(0));

                        if (compareResult1 == 2) {
                            compareResult2 = Ruler.compareValuesOfTwoCards(this.cardsAtHand.get(i), this.cardsAtHand.get(i + 1));
                            if (compareResult2 == 1) {
                                compareResult3 = Ruler.compareValuesOfTwoCards(this.cardsAtHand.get(i), this.cardsAtHand.get(i + 2));
                                if (compareResult3 == 1) {
                                    cardsToShow.add(cardsAtHand.get(i));
                                    cardsToShow.add(cardsAtHand.get(i + 1));
                                    cardsToShow.add(cardsAtHand.get(i + 2));
                                    return 1;
                                }
                            }
                        }
                    }
                }
                Log.d("t886", "三张pass");
                pass();
                return 0;
            //case 4://没有打四个的
            case 5: //可能是顺子，同花、三带二、四带一、同花顺
                if (cardsAtHand.size()==0){
                    System.exit(0);
                }
                if (this.cardsAtHand.size() >= 5) {
                    if (cardPack_5.size() == 0)
                        //假如没有五张牌组成的牌组，直接break
                        break;
                    else if (cardPack_5.size() == 1 || cardPack_5.size() == 2) {
                        //判断牌型，返回值是1,2,3,4,5,分别对应同花顺、四带一、三带二、同花、顺子
                        switch (Ruler.judgeCardType(cardsShowed)) {
                            case 1: {//同花顺的情况
                                cardsToShow = getFlush(cardPack_5, cardsShowed);
                                if (cardsToShow != null)
                                    return 1;
                                else return 0;
                                //如果返回值为null，则pass(
                            }
                            case 2: //四带一的情况
                                cardsToShow = getCardsAgainstFour_one(cardPack_5, cardsShowed);
                                //如果返回值为null，则pass(
                                if (cardsToShow != null)
                                    return 1;
                                else return 0;
                            case 3: //三带二的情况
                                cardsToShow = getCardsAgainstThree_Two(cardPack_5, cardsShowed);
                                //如果返回值为null，则pass(
                                if (cardsToShow != null)
                                    return 1;
                                else return 0;
                            case 4://同花
                                cardsToShow = getCardsAgainstFive_The_Same_Type(cardPack_5, cardsShowed);
                                //如果返回值为null，则pass(
                                if (cardsToShow != null)
                                    return 1;
                                else return 0;
                            case 5://顺子
                                cardsToShow = getCardsAgainstShunZi(cardPack_5, cardsShowed);
                                //如果返回值为null，则pass(
                                if (cardsToShow != null)
                                    return 1;
                                else return 0;
                            default:
                                break;
                        }

                    }
                }
                Log.d("t944o", "五张pass");
                pass();
                return 0;
            default:
                return 0;
        }
        Log.d("s","执行出牌后");
        return 0;
    }


    public void sequentialCardMatch(ArrayList<ArrayList<Card>> cardPack_5, ArrayList<ArrayList<Card>> cardBucket, ArrayList<Card> cardsSample, int... i) {
        int cardType = Ruler.judgeCardType(cardsSample);
        if (cardType == 0 || cardType == 4) {//判定为顺子
            for (int i1 = 0; i1 < cardBucket.get(i[0]).size(); i1++) {
                //第一层循环, 放入第一张
                Card card_1 = cardBucket.get(i[0]).get(i1);
                for (int i2 = 0; i2 < cardBucket.get(i[1]).size(); i2++) {
                    //第二层循环, 放入第二张
                    Card card_2 = cardBucket.get(i[1]).get(i2);
                    for (int i3 = 0; i3 < cardBucket.get(i[2]).size(); i3++) {
                        //第三层循环, 放入第三张
                        Card card_3 = cardBucket.get(i[2]).get(i3);
                        for (int i4 = 0; i4 < cardBucket.get(i[3]).size(); i4++) {
                            //第四层循环, 放入第四张
                            Card card_4 = cardBucket.get(i[3]).get(i4);
                            for (int i5 = 0; i5 < cardBucket.get(i[4]).size(); i5++) {
                                //第五层循环, 放入第五张
                                Card card_5 = cardBucket.get(i[4]).get(i5);
                                //把以上数组合并
                                ArrayList<Card> cards = new ArrayList<>();
                                cards.add(card_1);
                                cards.add(card_2);
                                cards.add(card_3);
                                cards.add(card_4);
                                cards.add(card_5);

                                cardPack_5.add(cards);

                            }
                        }
                    }
                }
            }
        }
    }

    //
    //返回一个boolean值，如果为真，则可以pass，否则不能
    public boolean canPass(){
        return Ruler.getIdOfPlayerWhoShowCardSuccessfully() != this.getPlayerId();
    }

    public void pass(){
        if(canPass()) {
            Log.d("ee","rr");
            removeAllCardsToShow();//回合第一个出牌的人不能跳过
            Ruler.toTheTurnOfNextPlayer(this);

        }
    }



}