# Homework02
HackMD版: [HackMD](https://hackmd.io/@4COkbbBoTXCZqkARAfT_sg/SJpkaVlnye)
## A tail-matching game

我們研究以下的遊戲。我們有一個矩形網格，可以在其上放置和移動水果。網格的每個方格只能包含一個水果。遊戲中有多種類型的水果。當至少有三個相同類型的水果在同一行或同一列相鄰時，它們會被消除並從網格中消失。玩家根據被消除的水果數量獲得分數。

給定網格的尺寸以及水果的種類數量，我們希望計算穩定配置的數量。穩定配置指的是在任意時刻，網格中都不會有三個相同類型的水果在同一行或同一列相鄰的配置，也就是沒有水果會被消除的情況。

本次作業的目標是計算在一個$10×10$的網格中，擁有兩種類型水果的穩定配置數量。

## 1 Game Modeling 遊戲建模
為了計算網格的數量，我們將首先表示網格的行（Row）。每一行使用`Row`類別的物件來表示。這個類別包含一個私有的`final int[] fruits`欄位，它是一個整數陣列，其中每個整數代表一種類型的水果。

為了簡化問題，我們假設只有兩種類型的水果，分別由整數`0`和`1`來表示。

在`Row`類別中，提供了以下內容：
- 兩個建構子：`Row()`和`Row(int[] fruits)`；
- 一個`public String toString()`方法，能夠提供該行的良好表示；
- 一個`public boolean equals(Object o)`方法，用於比較兩行是否相等；
- 一個`public int hashCode()`方法，目前我們不需要關心它。

請在 Row 類別中完成以下方法：
- `Row extendedWith(int fruit)`：該方法應該返回一個新的`Row`物件，它是從當前`Row`擴展而來，在末尾新增一個指定類型`fruit`的水果。
- `static LinkedList<Row> allStableRows(int width)`：該方法用於產生並返回所有穩定行的列表，這些行的寬度為 width（即不包含三個連續相同的水果）。
 **提示**：要創建所有寬度為`width`的穩定行，我們可以先遍歷所有寬度為`width-1`的穩定行，然後對於每一行，在其末尾新增一個水果`fruit`（0 或 1），但需確保新增後的行中最後三個水果不完全相同。
- `boolean areStackable(Row r1, Row r2)`：如果`r1`和`r2`與當前`Row`具有相同的長度，並且可以相互堆疊（無論順序如何）而不會在同一列中出現三個相同類型的水果，則該方法應返回`true`。

執行`Test1.java`來測試您的程式碼。
## 2 Naive enumeration
我們現在將對穩定配置的數量進行一種簡單的計數方法，並在`CountConfigurationsNaive`類別中實作這個過程。

在`CountConfigurationsNaive`類別中，請完成遞迴方法：
```java
static long count(Row r1, Row r2, LinkedList<Row> rows, int height)
```
該方法的目標是計算具有以下條件的網格數量：
- 第一、二行為`r1`和`r2`；
- 可用的行集合為`rows`；
- 網格的高度為`height`。

演算法如下：
1. 基礎情況：
    - 若`height ≤ 1`，則返回`0`（因為至少需要兩行才能形成合法網格）。
    - 若`height = 2`，則返回`1`（`r1`和`r2`已構成唯一的可能網格）。
2. 遞歸計算：
    - 遍歷`rows`中的所有行`r3`，檢查是否可以將`r3`疊加在`r1`和`r2`上。
    - 若可以，則遞歸計算`count(r2, r3, rows, height - 1)`，並將所有這些結果加總。
    - 返回總和。

**改進`count(int n)`方法**
請在`CountConfigurationsNaive`類別中完成：
`static long count(int n)`
此方法應該計算$n × n$網格的總數量。

**測試**
執行`Test2.java`來測試你的程式碼。

### What do you notice?
當你執行程式時，你會注意到：
- 程式是正確的，但時間複雜度非常高，當 n > 6 時，計算時間變得不可接受。

原因：
- `count(r1, r2, rows, height)`在計算過程中，會對相同的`r1`、`r2`、`height`重複進行計算，導致大量冗餘運算。

解決方案：
- **使用記憶化 (Memoization)：**
    - 在第一次計算`count(r1, r2, rows, height)`時，將`(r1, r2, height, count(r1, r2, rows, height))`存入一個查詢表中。
    - 注意：由於`rows`參數始終不變，因此不需要存入表格。

如何實作？
- 我們可以使用`Java`的`HashMap`來實現這個查詢表。
- 不過，為了更深入理解這種數據結構，我們將自己實作一個哈希表來完成記憶化存儲。

## 3 Hash table
我們將撰寫一個資料結構，用來將兩行`r1`和`r2`以及一個高度`height`對應到一個`long`型別的值。

我們使用`Quadruple`類別來表示這個表中的元素，即四元組`(r1, r2, height, result)`，其中`r1`和`r2`是`Row`類別的行，`height`是`int`型別的整數，而`result`是`long`型別的整數。`Quadruple`類別已經在`HW2.java`檔案中提供，無需修改此類別。

一個雜湊表本質上是一個由 四元組的連結串列所組成的陣列。陣列中索引為`i`的元素對應到那些雜湊值為`i`（對陣列大小取模）之四元組。我們使用 `LinkedList`類別來表示每個bucket（桶），並選擇一個任意大小（這裡為 50000）作為陣列大小。

因此，我們會實作一個`HashTable`類別，其包含以下內容：
- 一個`final static int M`常數，代表陣列中的「桶數量」，也就是陣列的長度。我們在這裡設為`50000`。
- 一個`Vector<LinkedList<Quadruple>> buckets`欄位，代表這個陣列，也就是我們根據雜湊值來儲存四元組的集合。

### 3.1 Initialization
請完成`HashTable`類別的建構子，讓`buckets`陣列中的每一個元素都被正確初始化為一個新的`LinkedList<Quadruple>`。
⚠️ 注意：`new Vector<...>(M)`的呼叫會回傳一個可調整大小的陣列，其容量為`M`，但其實際大小為 0。因此，必須使用例如`add()`方法來填入元素。請參閱`Vector`類別的說明文件。

### 3.2 Hash function
在`HashTable`類別中，請完成靜態方法
```java
static int hashCode(Row r1, Row r2, int height)
```
讓它可以為三元組`(r1, r2, height)`計算出一個任意的雜湊值。只要這個雜湊函式不是過於簡單的公式，即可。這個公式應該涉及`r1.hashCode()`、`r2.hashCode()`和`height`。
如果公式過於單調，四元組在雜湊表中的分佈就會很差，導致記憶化效率下降。
然後，在`HashTable`類別中，請完成
```java
static int bucket(Row r1, Row r2, int height)
```
這個方法要回傳`hashCode(r1, r2, height) % M`的值，表示該三元組應該對應到哪個`bucket`。

⚠️ 注意：Java中的`%`運算對於負數也可能回傳負值。你應確保回傳值在`0`（包含）到`M`（不包含）之間。

### 3.3 Adding to the table
請完成`HashTable`類別中的方法
```java
void add(Row r1, Row r2, int height, long result)
```
讓它能將四元組`(r1, r2, height, result)`新增到由`bucket()`方法指定的bucket裡。
我們不會檢查該條目是否已存在，**每次都直接新增至 bucket 中的串列尾端即可。**

### 3.4 Searching the table
請完成`HashTable`類別中的方法
```java
Long find(Row r1, Row r2, int height)
```
這個方法的目標是：在表中搜尋是否存在形如`(r1, r2, height, result)`的四元組。
- 如果存在，回傳一個`Long`物件，表示結果為`result`；
- 如果不存在，回傳`null`。
⚠️ 注意：此處需使用`Long`類別而非原始型別`long`，才能允許回傳`null`。你可以使用`new Long(...)`或`Long.valueOf(...)`來將基本型別轉換成物件。這樣一來，若找不到符合條件的四元組，就能安全回傳`null`。

### 測試方式
完成後請執行`Test3.java`來測試你的程式碼是否正確。

## 4 Counting with memoization
我們回到最初的組合問題。現在，我們要在`CountConfigurationsHashTable`類別中工作，其中包含一個名為`memo`的欄位，其型別為`HashTable`。
請參考你在`CountConfigurationsNaive`類別中所完成的：
```java
static long count(Row r1, Row r2, LinkedList<Row> rows, int height)
```
方法，來完成`CountConfigurationsHashTable`類別中相對應的`count`方法，並使用`memo`欄位來記住已經計算過的結果。

同樣地，請參考你在`CountConfigurationsNaive`類別中所完成的
```java
static long count(int n)
```
方法，來完成`CountConfigurationsHashTable`類別中的相對應方法，以計算一個$n × n$的穩定網格數量。

你可以透過執行`Test4.java`來測試你的程式，這個測試會計算一個 $10 × 10$ 網格的數量（應該只需要幾秒鐘即可完成）。

## 5 Using HashMap

請參考你在`CountConfigurationsHashTable`類別中的作法，來完成 `CountConfigurationsHashMap`類別中的以下方法：
- `static long count(Row r1, Row r2, LinkedList<Row> rows, int height)``
- `static long count(int n)`
但這次，請使用`Java`標準函式庫中的`HashMap`類別來取代你自己寫的`HashTable`類別。

🔍 提示： 由於雜湊表的鍵是一個三元組`(Row, Row, int)`，你需要建立一個 `Triple` 類別來表示這個鍵，以便能使用`HashMap<Triple, Long>`。

這個`Triple`類別必須覆寫以下兩個方法：
- `public boolean equals(Object o)`
（⚠️ 注意：這個方法的參數型別是 Object 而不是 Triple）
- `public int hashCode()`
在這個`hashCode()`方法中，你可以呼叫`HashTable`類別中已有的`static int hashCode(Row r1, Row r2, int height)`方法來生成雜湊值。
✅ 建議你在這兩個方法前加上`@Override`註解，以提醒編譯器進行檢查。

完成後，請執行`Test5.java`來測試你的程式。
