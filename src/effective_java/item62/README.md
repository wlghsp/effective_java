# Item 62 다른 타입이 적절하다면 문자열 사용을 피하라

--------------------------------------------

문자열(String)은 텍스트를 표현하도록 설계되었다. 

문자열은 워낙 흔하고 자바가 또 잘 지원해주어 원래 의도하지 않은 용도로도 쓰이는 경향이 있다. 

이번 아이템에서는 문자열을 쓰지 않아야 할 사례를 다룬다. 

<hr>

### 문자열은 다른 값 타입을 대신하기에 적합하지 않다. 
- 받은 데이터가 수치형이라면 int, float, BigInteger등 적당한 수치 타입으로 변환해야 한다. 
- '예/아니오' 질문의 답이라면 적절한 열거 타입이나 boolean으로 변환해야 한다. 
- 기본 타입이든 참조 타입이든 적절한 값 타입이 있다면 그것을 사용하고, 없다면 새로 하나 작성하라. 

### 문자열은 열거 타입을 대신하기에 적합하지 않다. 
- 아이템 34에서 이야기했듯, 상수를 열거할 때는 문자열보다는 열거 타입이 월등히 낫다. 

### 문자열은 혼합 타입을 대신하기에 적합하지 않다. 

여러 요소가 혼합된 데이터를 하나의 문자열로 표현하는 것은 대체로 좋지 않은 생각이다. 

코드 62-1 혼합 타입을 문자열로 처리한 부적절한 예
``` java
String compoundKey = className + "#" + i.next();
```
이는 단점이 많은 방식이다. 

### 문자열은 권한을 표현하기에 적합하지 않다. 

권한(capacity)을 문자열로 표현하는 경우가 종종 있다. 


클라이언트가 제공한 문자열 키로 스레드별 지역변수를 식별함. 

``` java
public class ThreadLocal {
    private ThreadLocal() { } //객체 생성 불가
     
    // 현 스레드의 값을 키로 구분해 저장한다. 
    public static void set(String key, Object value);
    
    // (키가 가리키는 ) 현 스레드의 값을 반환한다. 
    public static Object get(String key);;
}
```
* 이 방식의 문제점 : 스레드 구분용 문자열 키가 전역 이름공간에서 공유된다는 점

보안도 취약하다. 

<hr>

이 API는 문자열 대신 위조할 수 없는 키를 사용하면 해결된다. 이 키를 권한(capacity)이라고도 한다. 

코드 62-3 Key 클래스로 권한을 구분했다. 
``` java
public class ThreadLocal {
    private ThreadLocal() { } // 객체 생성 불가
    
    public static class Key { // (권한)
        Key() { }
    }
    
    // 위조 불가능한 고유 키를 생성한다.
    public static Key getKey() {
        return new Key();
    }
    
    public static void set(Key key, Object value);
    public static Object get(Key key);
}
```

위 코드를 개선한다. 

코드 62-4 리팩터링하여 Key를 ThreadLocal로 변경
``` java
public final class ThreadLocal {
    public ThreadLocal();
    public void set(Object value);
    public Object get();
}
```

이 API에서는 get으로 얻은 Object를 실제 타입으로 형변환해 써야 해서 타입 안전하지 않다. 

처음의 문자열 기반 API는 타입안전하게 만들 수 없으며, Key를 사용한 API도 타입안전하게 만들기 어렵다. 

하지만 ThreadLocal을 매개변수화 타입으로 선언하면 간단하게 문제가 해결된다. 

코드 62-5 매개변수화하여 타입안정성 확보
``` java
public final class ThreadLocal<T> {
    public ThreadLocal();
    public void set(T value);
    public T get();
}
```
이제 자바의 java.lang.ThreadLocal과 흡사해졌다. 문자열 기반 API의 문제를 해결해주며, 키 기반 API보다 빠르고 우아하다. 


<hr>

### 핵심 정리
- 더 적합한 데이터 타입이 있거나 새로 작성할 수 있다면, 문자열을 쓰고 싶은 유혹을 뿌리쳐라. 
- 문자열은 잘못 사용하면 번거롭고, 덜 유연하고, 느리고, 오류 가능성도 크다.
- 문자열을 잘못 사용하는 흔한 예로는 기본 타입, 열거 타입, 혼합 타입이 있다. 