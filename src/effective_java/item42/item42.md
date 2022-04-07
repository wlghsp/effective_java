# Item 42 익명 클래스보다는 람다를 사용하라

--------------------------------------------

### 라떼는... in the past
자바에서 함수 타입 표현은 추상 메서드를 하나만 다음 인터페이스(드물게는 추상클래스)를 사용.

인터페이스의 인스턴스를 함수 객체(function object)라고 하여, 특정 함수나 동작을 나타내는데 씀. 

1997년 JDK 1.1이 등장. 함수 객체를 만드는 주요 수단은 **익명 클래스**로 바뀌었다.


<hr>

다음은 문자열을 길이순으로 정렬하는데, 정렬을 위한 비교 함수로 익명 클래스 사용한다. 

코드 42-1 익명 클래스의 인스턴스를 함수 객체로 사용 - 낡은 기법이다!
``` java
Collections.sort(words, new Comparator<String>() {
    public int compare(String s1, String s2) {
        return Integer.compare(s1.length(), s2.length());
    }
});
```

전략 패턴 처럼, 함수 객체를 사용하는 과거 객체 지향 디자인 패턴에는 익명 클래스명 充分했다.

이 코드에서 Comparator 인터페이스가 정렬을 담당하는 추상 전략을 뜻하며, 문자열을 정렬하는 구체적인 전략을 익명 클래스로 구현했다. 

But 익명 클래스는 코드가 너~무 길기 때문에 자바는 함수형 프로그래밍에 적합하지 않았다. (과거형)

<hr>
그러나 자바 8 두두등장, 추상 메서드 하나짜리 인터페이스의 이름을 불러주었고 특별한 의미 인정과 함께 특별한 대우 받게 되었다. 

지금은 함수형 인터페이스라 부르는 이 인터페이스들의 인스턴스를 람다식(lambda expression, 혹은 짧게 람다)을 사용해 만들 수 있게 되었다. 


코드 42-2 람다식을 함수 객체로 사용 - 익명 클래스 대체 
``` java
Collections.sort(words,(s1,s2) -> Integer.compare(s1.length(), s2.length());
```


> 타입을 명시해야 코드가 더 명확할 때만 제외하고는, 람다의 모든 매개변수 타입은 생략하자. 

그런 다음 컴파일러가 "타입을 알 수 없다"는 오류를 낼 때만 해당 타입을 명시하면 된다. 

람다 자리에 비교자 생성 메서드를 사용하면 이 코드를 더 간결하게 만들 수 있다. 

``` java
Collections.sort(words, comparingInt(String::length));
```

더 나아가 자바 8 때 List 인터페이스에 추가된 sort 메서드를 이용하면 더욱 짧아진다. 
``` java
words.sort(comparingInt(String::length));
```

코드 42-3 상수별 클래스 몸체와 데이터를 사용한 열거 타입(코드 34-6)
``` java
public enum Operation {
   PLUS("+") {
       public double apply(double x, double y) { return x + y;}
   },
   MINUS("-") {
       public double apply(double x, double y) { return x - y;}
   },
   TIMES("*") {
       public double apply(double x, double y) { return x * y;}
   },
   DIVIDE("/") {
       public double apply(double x, double y) { return x / y;}
   };
   
   private final String symbol;
   
   Operation(String symbol) { this.symbol = symbol; }

    @Override
    public String toString() {
        return symbol;
    }
    public abstract double apply(double x, double y);
}
```

코드 42-4 함수 객체(람다)를 인스턴스 필드에 저장해 상수별 동작을 구현한 열거 타입
``` java
public enum Operation {
    PLUS("+", (x,y) -> x + y),
    MINUS("-", (x,y) -> x - y)
    TIMES("*", (x,y) -> x * y)
    DIVIDE("/", (x,y) -> x / y)
    
    private final String symbol;
    private final DoubleBinaryOperator op;
    
    Operation(String symbol, DoubleBinaryOperator op) { 
        this.symbol = symbol;
        this.op = op; 
    }

    @Override
    public String toString() {
        return symbol;
    }
    public abstract double apply(double x, doubsle y) {
        return op.applyAsDouble(x, y);
    }
}
```
메서드나 클래스와 달리
**람다는 이름이 없고 문서화도 못 한다. 따라서 코드 자체로 동작이 명확히 설명되지 않거나 코드 줄 수가 많아지면 람다를 쓰지 말아야 한다.**

람다는 한 줄 일때 가장 좋고 길어야 세 줄 안에 끝내는게 좋다. 

세 줄을 넘어가면 가독성이 심하게 나빠진다. 


람다는 함수형인터페이스에서만 쓸 수 있다. 

추상 클래스의 인스턴스를 만들 때 람다를 쓸 수 없으니, 익명 클래스를 써야 한다. 

비슷하게 추상 메서드가 여러 개인 인터페이스의 인스턴스를 만들 때도 익명 클래스를 쓸 수 있다. 

마지막으로, 람다는 자신을 참조할 수 없다. 람다에서의 this 키워드는 바깥 인스턴스를 가리킨다. 

반면 익명 클래스에서의 this는 익명 클래스의 인스턴스 자신을 가리킨다. 그래서 함수 객체가 자신을 참조해야 한다면 반드시 익명 클래스를 써야 한다. 

> 람다를 직렬화하는 일은 삼가하자


### 핵심 정리
- 자바8이 등장하면서 작은 함수 객체를 구현하는 데 적합한 람다가 도입됨.
- 익명 클래스는 (함수형 인터페이스가 아닌) 타입의 인스턴스를 만들 때만 사용하라.
- 람다는 작은 함수 객체를 아주 쉽게 표현할 수 있어 함수형 프로그래밍의 지평을 열었다. 