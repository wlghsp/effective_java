# Item 38 확장할 수 있는 열거 타입이 필요하면 인터페이스를 사용하라

--------------------------------------------

열거 타입은 거의 모든 상황에서 타입 안전 열거 패턴 보다 우수하다. 

단 예외가 있다. 

타입 안전 열거 패턴은 확장할 수 있으나 열거 타입은 그럴 수 없다. 

다르게 말하면 

타입 안전 열거 패턴은 열거한 값들을 그대로 가져온 다음 값을 더 추가하여 다른 목적으로 쓸 수 있는 반면,

열거 타입은 그렇게 할 수 없다. 

확장할 수 있는 열거 타입이 어울리는 쓰임은 하나다. **연산 코드(operation code or opcode)**

연산 코드의 각 원소는 특정 기계가 수행하는 연산을 뜻한다. 

열거 타입이 임의의 인터페이스를 구현할 수 있다는 사시을 이용, 연산 코드용 인터페이스를 정의하고 열거 타입이 이 인터페이스를 구현하게 하면 된다.

* 코드 38-1 인터페이스를 이용해 확장 가능 열거 타입을 흉내냈다. 
``` java
public interface Operation {
    double apply(double x, double y);
}

public enum BasicOperation implements Operation {
    PLUS("+") {
        public double apply(double x, double y) { return x + y; }
    },
    MINUS("-") {
        public double apply(double x, double y) { return x - y; }
    },
    TIMES("*") {
        public double apply(double x, double y) { return x * y; }
    },
    DIVIDE("/") {
        public double apply(double x, double y) { return x / y; }
    };
    
    private final String symbol;
    
    BasicOperation(String symbol) {
        this.symbol = symbol;
    }
    
    @Override public String toString() {
        return symbol;
    }
}
```
열거 타입인 BasicOperation은 확장할 수 없지만 인터페이스인 Operation은 확장할 수 있고, 

이 인터페이스를 연산의 타입으로 사용하면 된다. 이렇게 하면 Operation을 구현한 또 다른 열거 타입을 정의해 

기본 타입인 BasciOperation을 대체할 수 있다. 

예를 들어 앞의 연산 타입을 확장해 지수 연산(EXP)과 나머지 연산(REMAINDER)을 추가해보자.

이를 위해 우리가 할 일은 Operation 인터페이스를 구현한 열거 타입을 작성하는 것뿐이다. 






#### 핵심 정리
