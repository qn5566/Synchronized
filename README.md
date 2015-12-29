# Synchronized
前言

我們都知道 Android 常會利用 multi-thread 來做多工的處理
但是如果同時有兩個以上的 Thread 對同一個物件做資料的存取甚至是更改
沒有處理好的話會導致意想不到的結果發生．

舉個例子來說，假設有一個地方郵局的郵箱如下圖所示
這個郵局有兩個郵差 A 跟 B 來負責送信
並且有一個人會負責記錄剩餘的郵件數量
我們先假設這個記錄的人叫做 Kenji 好了


有一天郵差 A 到了郵局，Kenji 告訴他目前待處理的郵件有 100 封
接著 A 就開始從郵箱尋找他要負責處理的信件
然後郵差 B 也來了，Kenji 一樣告訴他還剩下 100 封郵件
一樣郵差 B 也去郵箱找信件
這時郵差 A 拿了他現在要處理的 20 封郵件離開，
告訴 Kenji 現在剩下 80 封

過了不久郵差 B 也拿了要處理的 30 封郵件
但他並不知道郵差 A 已經拿走了一些郵件，於是告訴 Kenji 現在剩下 70 封
Kenji 記錄下現在的郵件數量：70，但很明顯地我們知道實際上只剩下 50 封

這就是 multi-thread 同時對一個物件存取有可能產生錯誤的情境
為了避免這樣的情形發生
以下我們要來介紹 synchronized 宣告的使用


Synchronized 的使用方法

1. Synchronized Method

synchronized public void method1() {
     ...
}
此種方法對於只有一個 instance 的 class 來說
能夠確保在同一個時間只有一個 Thread 正在執行此 Method
但是如果 new 了很多 instance 出來就沒有辦法保證了


2. Synchronized Static Method

synchronized public static void method2() {
     ...
}
為上一種方法的加強版，就算有很多 instance 還是可以確保
同一時間只有一個 Thread 正在執行此 Method


3. Synchronized(this)

public void method3() {
     ...
     synchronized(this) {
          ...
     }
     ...
}
此種方法跟第一種方式類似
差別只是寫在 synchronized(this) 之前的程式碼可以同時被執行
synchronized(this) 之後的程式碼同時只有一個 Thread 可執行

我知道這樣講完還是有點抽象
讓我們來看看下面的例子！


Synchronized 的實際例子

Synchronized.class
首先我們先建立一個 class
裡面有四個 Method
1. method0(): 普通沒有做 synchronized 的 method
2. method1(): 實作上面的 Synchronized Method
3. method2(): 實作上面的 Synchronized Static Method
4. method3(): 實作上面的 Synchronized(this)
做的事情很簡單，只是每過一秒吐一個 Log 出來
並且顯示現在正在執行的 Thread

public class Synchronized {

     final static String TAG = "Ted Scott";

     public static void method0() {
          int i = 0;
          while (i++ < 3) {
               Log.d(TAG, Thread.currentThread().getName() + ":" + i);
               try {
                    Thread.sleep(1000);
               } catch (InterruptedException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
               }
          }
     }

     synchronized public void method1() {
          method0();
     }

     synchronized public static void method2() {
          method0();
     }

     public void method3() {
          Log.d(TAG, "Start method3");

          synchronized (this) {
               method0();
          }

          Log.d(TAG, "End method3");
     }
}
MainActivity.class
接著我們在 MainActivity 新增兩個 Thread
名字分別為 A 跟 B
接著觀察執行不同的 method 時 Log 的變化

@Override
protected void onCreate(Bundle savedInstanceState) {
     super.onCreate(savedInstanceState);
     setContentView(R.layout.activity_main);

     final Synchronized mSynchronized = new Synchronized();

     Thread thread1 = new Thread(new Runnable() {
          @Override
          public void run() {
               mSynchronized.method0();
               // mSynchronized.method1();
               // new Synchronized().method1();
               // new Synchronized().method2();
               // mSynchronized.method1();
          }
     },"A");


     Thread thread2 = new Thread(new Runnable() {
          @Override
          public void run() {
               mSynchronized.method0();
               // mSynchronized.method1();
               // new Synchronized().method1();
               // new Synchronized().method2();
               // mSynchronized.method3();
          }
     }, "B");

     Log.d(Synchronized.TAG, "Start Thread!");
     thread1.start();
     thread2.start();
}
運行的結果如下：
1. 執行正常的 method 兩個 thread A, B 會交錯出現

thread1:     mSynchronized.method0();
thread2:     mSynchronized.method0();
--------------------------------------------
Start Thread!
A:1
B:1
B:2
A:2
A:3
B:3

2. 執行 Synchronized method 會先等其中一個 thread 執行完
才會再度執行相同的程式碼區塊

thread1:     mSynchronized.method1();
thread2:     mSynchronized.method1();
--------------------------------------------
Start Thread!
A:1
A:2
A:3
B:1
B:2
B:3

3. 但是如果有超過一個物件的話，程式碼會同時執行

thread1:     new Synchronized().method1();
thread2:     new Synchronized().method1();
--------------------------------------------
Start Thread!
B:1
A:1
A:2
B:2
A:3
B:3

4. 如果採用 Synchronized Static Method 的話
就可以解決上面同時執行的問題

thread1:     new Synchronized().method2();
thread2:     new Synchronized().method2();
--------------------------------------------
Start Thread!
A:1
A:2
A:3
B:1
B:2
B:3

5. 寫在 synchronized(this) 之前的程式碼可以先同時執行

thread1:     mSynchronized.method1();
thread2:     mSynchronized.method3();
--------------------------------------------
Start Thread!
A:1
Start method3
A:2
A:3
B:1
B:2
B:3
End method3


總結

Synchronized 宣告其實是在 multi-thread 中蠻值得學習的一些觀念
以下是我對 Synchronized 的一些理解：
1. 只要有 thread 存取了某個 class 中的 synchronized method，
那麼其他的 thread 也不能存取整個 class 中所有的 synchronized method
也就是在同一個時間內只會有一個 method 存取 synchronized 區塊的程式碼
2. 相反的，當一個 thread 正在執行 synchronized 區塊時
其他 thread 仍然可以執行 非synchronized 區塊的程式碼

希望對大家有幫助囉！謝謝！

參考資料：
1. java同步機制：synchronized
http://blog.csdn.net/cjjky/article/details/7353390
2. 執行緒與 synchronized 同步函式的應用
http://dazi2012.blogspot.tw/2013/04/synchronized.html
3. [Java] Synchronized 心得
http://www.jackforfun.com/2007/07/java-synchronized.html
4. 批踢踢實業坊›看板 AndroidDev - punk86862001 (趙MAN)
https://www.ptt.cc/bbs/AndroidDev/M.1390316957.A.0D4.html
