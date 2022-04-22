# Item 60 정확한 답이 필요하다면 float와 double은 피하라

--------------------------------------------

float와 double타입은 과학과 공학 계산용으로 설계되어ㅆ다. 

이진 부동소수점 연산에 쓰이며, 넓은 범위의 수를 빠르게 정밀한 '근사치'로 계산하도록 세심하게 설계되었다. 

따라서 정확한 결과가 필요할 때는 사용하면 안 된다. 

> float와 double 타입은 특히 금융 관련 계산과는 맞지 않는다.

0.1 혹은 10의 음의 거듭 제곱수(10^-1, 10^-2 등)를 표현할 수 없기 때문이다. 
 
<hr>

``` java
System.out.println(1.03-0.42);
```
0.6100000000001(작성자:대략 작성했음)을 출력한다. 

``` java
System.out.println(1.00 - 9 * 0.10);
```
이 코드는 0.09999999999999998을 출력한다. 

코드 60-1 오류 발생! 금융 계산에 부동소수 타입을 사용했다.
``` java
public static void main(String[] args) {
    double funds = 1.00;
    int itemsBought = 0;
    for ( double price = 0.10; funds >= price; price += 0.10) {
        funds -= price;
        itemsBought++;
    }
    System.out.println(itemsBought + "개 구입");
    System.out.println("잔돈(달러):" + funds);
}
```
잔돈은 0.3999999999999999달러가 남았음을 알게 되고, 물론 잘못된 결과다 !

> 해결책: 금융 계산에는 BigDecimal, int 혹은 long을 사용해야 한다. 

<hr>
다음은 앞서의 코드에서 double 타입을 BigDecimal로 교체만 했다. 

코드 60-2 BigDecimal을 사용한 해법. 속도가 느리고 쓰기 불편하다. 
``` java
public static void main(String[] args) {
    final BigDecimal TEN_CENTS = new BigDecimal(".10");
    
    int itemsBought = 0;
    BigDecimal funds = new BigDecimal("1.00");
    for (BigDecimal price = TEN_CENTS; 
        funds.compareTo(price) >= 0;
        price = price.add(TEN_CENTS)) {
        funds -= price;
        itemsBought++;
    }
    System.out.println(itemsBought + "개 구입");
    System.out.println("잔돈(달러):" + funds);
}
```

잔돈은 0달러가 남는다. 올바른 답!

### 슬프게도 BigDecimal 단점 2 가지 있다는...
1. 기본타입 보다 쓰기가 훨씬 불편하다
2. 기본타입 보다 훨씬 느리다. 

### BigDecimal의 대안:  int, long 타입

다룰 수 있는 값의 크기가 제한되고, 소수점을 직접 관리해야 함. 

코드 60-3 정수 타입을 사용한 해법
``` java
public static void main(String[] args) {
    int itemsBought = 0;
    int funds = 100;
    for (int price = 10; funds >= price; price += 10) {
        funds -= price;
        itemsBought++;
    }
    System.out.println(itemsBought + "개 구입");
    System.out.println("잔돈(달러):" + funds);
}
```
모든 계산을 달러 대신 센트로 수행하면 문제가 해결된다. 

### 핵심 정리

- 정확한 답이 필요한 계산에는 float난 double을 피하라. 
- 소수점 추적은 시스템에 맡기고, 코딩 시의 불편함이나 성능 저하를 신경 쓰지 않겠다면 BigDecimal을 사용하라.
- BigDecimal이 제공하는 여덟 가지 반올림 모드를 이용하여 반올림을 완벽히 제어할 수 있다. 
- 법으로 정해진 반올림을 수행해야 하는 비즈니스 계산에서 아주 편리한 기능이다. 
- 반면, 성능이 중요하고 소수점을 직접 추적할 수 있고 숫자가 너무 크지 않다면 int나 long을 사용하라. 
- 숫자를 아홉 자리 십진수로 표현할 수 있다면 int를 사용
- 열여덟 자리 십진수로 표현할 수 있다면 long을 사용
- 열여덟 자리를 넘어가면 BigDecimal을 사용해야 한다. 