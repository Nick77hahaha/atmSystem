package com.itheima.atmsystem;

import java.util.ArrayList;
import java.util.Random;
import java.util.Scanner;

/***
 * 這是ATM系統的入口類
 */
public class ATMSystem {
    public static void main(String[] args) {
        //1.定義帳戶類
        //2.定義集合容器，負責以後存儲全部的帳戶對象，進行相關業務操作
        ArrayList<Account> accounts = new ArrayList<>();
        Scanner sc = new Scanner(System.in);
        //3.展示系統首頁
        while (true) {//為了處理有人輸入錯誤command，故利用死循環，讓別人輸入錯誤可以重新再次輸入。
            System.out.println("============ATM system============");
            System.out.println("1. login");
            System.out.println("2. open account");//要先開戶才能登入
            System.out.println("choose your action");
            int command = sc.nextInt();
            switch(command){
                case 1:
                    //user login
                    login(accounts,sc);
                    break;
                case 2:
                    //user open account
                    register(accounts,sc);//可以先寫出方法名，再alt+enter可以自動生成方法。傳sc進去可以省內存。
                    break;
                default:
                    System.out.println("this command is not exist");
            }
        }

    }

    /**
     *登錄功能
     * @param accounts 全部帳戶對象的集合
     * @param sc 掃描器
     */
    private static void login(ArrayList<Account> accounts, Scanner sc) {
        System.out.println("============系統登入操作System login============");
        //STEP1:判斷集合帳戶中有沒有是否存在帳戶
        if(accounts.size() == 0){
            System.out.println("帳戶不存在account does not exist");
            return;
        }
        while (true) {
            //STEP2:正式進入登入操作
            System.out.println("請輸入卡號type card Number");
            String carId = sc.next();
            //STEP3:判斷卡號有無重複:根據卡號去集合中查詢帳戶對象
            Account acc = getAccountByCardId(carId,accounts);
            if(acc != null){//卡號存在
                while (true) {
                    //卡號存在
                    //STEP4:讓用戶輸入密碼，認證密碼
                    System.out.println("請輸入登入密碼type login password");
                    String password = sc.next();
                    if(acc.getPassword().equals(password)){
                        //登入成功
                        System.out.println("登入成功 login success");
                        //開始展示登入後的操作頁
                        showUserCommand(sc,acc,accounts);
                        return;//幹掉login方法(直接跳開private static void login)(此時會跳回到login(accounts,sc);)
                               //並透過login(accounts,sc)下方的break跳出switch迴圈，並藉由上方的while繼續顯示詢問要登入還是開戶
                    }else{
                        System.out.println("輸入的密碼有誤，請重新輸入type wrong password. type again!");//所以用死循環
                    }
                }
            }else{
                System.out.println("卡號不存在card Number does not exist");
            }
        }
    }

    /**
     * 展示登入後的操作頁
     */
    private static void showUserCommand(Scanner sc, Account acc, ArrayList<Account> accounts) {
        while (true) {
            System.out.println("===========用戶操作頁 user page==============");
            System.out.println("1.查詢帳戶 search account");
            System.out.println("2.存款 deposit money");
            System.out.println("3.取款 withdraw money");
            System.out.println("4.轉帳 transfer money");
            System.out.println("5.修改密碼 modify password");
            System.out.println("6.退出 exit");
            System.out.println("7.註銷帳戶 cancelled account");
            System.out.println("請選擇 Please choose");
            int command = sc.nextInt();
            switch (command){
                case 1://查詢帳戶(展示當前帳戶資訊)(那目前使用者的資訊在哪裡呢?)
                    showAccount(acc);
                    break;
                case 2://存款
                    depositMoney(acc,sc);//要存款當然要知道目前的帳戶對象才可以將錢存入，所以要傳入當前對象acc
                                         //要讓使用者輸入要存多少錢，所以要傳入sc才可讀取使用者輸入數值。
                    break;
                case 3://取款
                    withdrawMoney(acc,sc);
                    break;
                case 4://轉帳
                    transferMoney(sc,acc,accounts);//需要自己的帳戶跟對方的帳戶，但是目前無法確認對方帳戶。
                                                   // 所以應該先傳入所有的帳戶集合，然後當跳到該方法時在根據轉帳的卡號去找到對方的帳戶對象。
                    break;
                case 5://修改密碼
                    updatePassword(sc,acc);//掃描器讓使用者輸入新密碼//要改密碼當然要知道目前的帳戶對象，所以要傳入當前對象acc
                    return;//這裡不用break，因為改完密碼要離開操作頁(等同離開showUserCommand方法)，回到首頁(就是詢問要開戶或是登入頁面)
                case 6://退出
                    System.out.println("退出成功exit success");
                    return;//幹掉showUserCommand方法，不是只有跳出switch。此時會跳回到showUserCommand(sc,acc);
                    //這裡不用break;，因為需要用return跳出這個方法
                case 7://註銷帳戶
                    //就是從帳戶集合中刪除當前帳戶對象，銷毀帳戶就完成了。
                    System.out.println("真的要銷毀帳戶(y/n)??delete your account(y/n)??");
                    String rs = sc.next();
                    switch(rs){
                        case "y":
                            //真正的銷戶
                            accounts.remove(acc);
                            System.out.println("已經銷毀帳戶 already delete your account");
                            return;
                        default:
                            System.out.println("那保留目前帳戶 retain your account then");
                    }
                    break;
                default:
                    System.out.println("輸入操作指令不正確input command is not correct");
            }
        }
    }

    /**
     * 修改密碼
     * @param sc 掃描器
     * @param acc 當前成功登入的帳戶對象
     */
    private static void updatePassword(Scanner sc, Account acc) {
        System.out.println("=======用戶修改密碼 user modify password========");
        while (true) {
            System.out.println("請輸入當前密碼 please input current password");
            String password = sc.next();
            //判斷當前密碼是否正確
            if(acc.getPassword().equals(password)){
                while (true) {
                    System.out.println("請輸入新密碼 please input new password");
                    String newPassword = sc.next();
                    System.out.println("請再次輸入新密碼 please input new password again");
                    String okPassword = sc.next();
                    if(newPassword.equals(okPassword)){
                        acc.setPassword(newPassword);
                        System.out.println("密碼修改成功 modify password success");
                        return;
                    }else{
                        System.out.println("輸入2次密碼不相同 two password did not match");//如果輸入錯誤就應該繼續輸入，所以死循環
                    }
                }
            }else{
                System.out.println("你輸入密碼不正確 input password is not correct");
            }
        }
    }

    /**
     * 轉帳功能
     * @param sc
     * @param acc 自己的帳戶對象
     * @param accounts 所有的帳戶集合
     */
    private static void transferMoney(Scanner sc, Account acc, ArrayList<Account> accounts) {
        System.out.println("=======用戶轉帳操作 user transfer money========");
        //1.判斷是否足夠2個帳戶，2個以上才有可能轉帳
        if(accounts.size()<2){
            System.out.println("沒有2個帳戶以上，請去開戶 there is no more than 2 account");
            return;//結束當前方法
        }
        //2.判斷自己的帳戶是否有錢
        if(acc.getMoney() == 0){
            System.out.println("you do not have money");
            return;//結束當前方法
        }
        while (true) {
            //3.真正開始轉帳
            System.out.println("輸入對方卡號 input the card number who you want to transfer");
            String cardId = sc.next();
            //判斷此卡號不是輸入自己的卡號
            if(cardId.equals(acc.getCardId())){
                System.out.println("你不可以轉帳給自己 you can not transfer money to yourself");
                continue;//直接進入下一次死循環(結束當次執行)，下方程式碼不執行。別用return
            }
            //判斷此卡號是存在的:根據卡號去查詢對方帳戶對象
            Account account = getAccountByCardId(cardId,accounts);
            if(account == null){
                System.out.println("你輸入的卡號不存在 this card number does not exist");//如果輸入錯誤就應該繼續輸入，所以死循環
            }else{
                //這個帳戶對象有存在，但要繼續認證姓名
                String userName = account.getUserName();
                System.out.println("請輸入對方姓名 input his/her name");
                String checkName = sc.next();
                //認證姓名是否正確
                if(checkName.equals(userName)){
                    while (true) {
                        //開始轉帳了
                        System.out.println("輸入欲轉帳金額 how much you want to transfer");
                        double money = sc.nextDouble();
                        //判斷可以轉帳餘額
                        if(money > acc.getMoney()){
                            System.out.println("你餘額不足，最多轉帳 your balance only can transfer:"+acc.getMoney());
                        }else{
                            //可以轉帳了
                            acc.setMoney(acc.getMoney()-money);
                            account.setMoney(account.getMoney()+money);
                            System.out.println("轉帳成功 transfer success");
                            return;
                        }
                    }
                }else{
                    System.out.println("你輸入的姓名錯誤");
                }
            }
        }
    }

    /**
     * 取錢功能
     * @param acc 當前帳戶對象
     * @param sc 掃描器
     */
    private static void withdrawMoney(Account acc, Scanner sc) {
        //STEP1:判斷有無100
        System.out.println("=======用戶取錢操作 user withdraw money=======");
        if(acc.getMoney()<100){
            System.out.println("錢不夠100，不能取錢Balence is less than 100 so can not withdraw money");
            return;
        }
        while (true) {
            //STEP2:提示用戶輸入取錢金額
            System.out.println("輸入要取多少錢 how much you want to withdraw?");
            double money = sc.nextDouble();
            //STEP3:判斷金額是否合乎要求(不能超過每次取現額度)
            if(money > acc.getQuotaMoney()){
                System.out.println("超過每次取現額度，最多可取 exceed withdraw limitation, max money you can withdraw is :"+acc.getQuotaMoney());//因為程式邏輯的規定是一定要把錢取走，所以死循環
            }else{
                //沒超過取現額度
                //STEP4:判斷取錢金額是否超過該帳戶的總額
                if(money > acc.getMoney()){
                    System.out.println("餘額不足 Balance is not sufficient");
                }else{
                    System.out.println("取錢成功，取錢金額 withdraw success this amount:"+money);
                    acc.setMoney(acc.getMoney()-money);//把取出的錢從帳戶中扣掉
                    showAccount(acc);
                    return;//取完錢就不用跑了
                }
            }
        }
    }

    /**
     *存錢功能
     * @param acc 當前對象
     * @param sc 掃描器
     */
    private static void depositMoney(Account acc, Scanner sc) {
        System.out.println("========存錢操作deposit action==========");
        //千萬別把money設定給用戶，因為應該要設定(之前餘額+現在存入金額)的總和設定到系統中
        System.out.println("請輸入存款金額input the deposit money");
        double money = sc.nextDouble();
        acc.setMoney(acc.getMoney() + money);//不用再把新的帳戶對象加入到accounts集合中(不用寫accounts.add(acc))
        System.out.println("存款成功，帳戶資訊如下 deposit success");
        showAccount(acc);//呼叫方法並傳入當前帳戶對象
    }

    /**
     * 查詢帳戶(展示當前帳戶資訊)
     */
    private static void showAccount(Account acc) {
        System.out.println("當前帳戶訊息current information");
        System.out.println("卡號card number:"+acc.getCardId());
        System.out.println("姓名Name:"+acc.getUserName());
        System.out.println("餘額Balance"+acc.getMoney());
        System.out.println("每次提領限額withdraw limited:"+acc.getQuotaMoney());
    }

    /**
     * 開戶功能的實現
     * @param accounts 接收的帳戶集合
     */
    private static void register(ArrayList<Account> accounts, Scanner sc) {//account從這裡傳入:ArrayList<String> accounts = new ArrayList<>();
        System.out.println("============系統開戶操作System open account============");
        //STEP1:創建一個帳戶對象，用於後期封裝帳戶信息。
        Account account = new Account();
        //STEP2:錄入目前這個帳戶的信息，注入到帳戶對象中。
        System.out.println("請輸入用戶名: type userName");
        String userName = sc.next();
        account.setUserName(userName);//把接收到的名字傳入Account內
        while (true) {
            System.out.println("請輸入密碼: type password");
            String password = sc.next();
            System.out.println("請輸入密碼: type password again");
            String okPassword = sc.next();
            if(okPassword.equals(password)){
                account.setPassword(okPassword);//2次密碼相同才存入
                break;//已存入密碼，故不在需要死循環
            }else{
                System.out.println("2次密碼不一致，請重新輸入these two password do not mach and type again");//要再重來一次一律用死循環
            }
        }
        System.out.println("請輸入取現額度: please set withdraw cash limit");
        double quotaMoney = sc.nextDouble();
        account.setQuotaMoney(quotaMoney);

        //開始隨機生成8位且不跟其他帳戶重複的的卡號
        String cardId = getRandomCardId(accounts);//account從這裡傳入:static void register(ArrayList<String> accounts
        account.setCardId(cardId);

        //STEP3:把帳戶對象添加到帳戶集合中。
        accounts.add(account);
        System.out.println("開戶完成complete open account and card Number is :"+cardId);
    }

    /**
     * 為帳戶生成8位且不跟其他帳戶重複的的卡號
     * @return
     */
    private static String getRandomCardId(ArrayList<Account> accounts) {//accounts從這裡傳入:String cardId = getRandomCardId(accounts);
        Random r = new Random();
        while (true) {
            //STEP1:生成8位數字
            String cardId = "";
            for (int i = 0; i < 8; i++) {//i是指8位數字
                cardId += r.nextInt(10);//但是每一個數字都可以0~9，所以bound是10。cardId +=是將8位數字連起來
            }
            //重點來了，要如何去比較此卡號有沒有跟現有的卡號重複呢?
            //作法就是拿cardId，去accounts集合容器中去比對。accounts集合容器在這:static void register(ArrayList<Account> accounts
            //所以我必須將ArrayList<String> accounts傳入此方法中(getRandomCardId)
            //寫一個方法，功能是可以用卡號去找到帳號
            Account acc = getAccountByCardId(cardId,accounts);
            if(acc == null){
                return cardId;//沒在集合中查到，代表卡號沒重複
            }
            //若是有重複，則需要死循環去找到沒重複的卡號為止
        }
    }

    /**
     * 根據卡號去找到帳戶對象出來
     * @param cardId
     * @param accounts
     * @return 帳戶對象 or null
     */
    private static Account getAccountByCardId(String cardId,ArrayList<Account> accounts){//應該返回一個帳戶對象出來，所以用Account。同時也要接入[卡號&整個帳號集合]進來比對。
        for (int i = 0; i < accounts.size(); i++) {
            Account acc = accounts.get(i);
            if(acc.getCardId().equals(cardId)){
                return acc;
            }
        }
        return null;//查無此帳號
    }
}
