# ITEM 34 int 상수 대신 열거 타입을 사용하라 

--------------------------------------------

열거 타입은 일정 개수의 상수 값을 정의한 다음, 그 외의 값은 허용하지 않는 타입이다. 
사계절, 태양계의 행성, 카드게임의 카드 종류 등이 좋은 예다. 자바에서 열거 타입을 지원하기 전에는 다음 코드처럼 정수 상수를 한 묶음 선언해서 사용하곤 했다. 

코드 34-1 정수 열거 패턴 - 상당히 취약하다!

---

``` java
public static final int APPLE_FUJI          = 0;
public static final int APPLE_PIPPIN        = 1;
public static final int APPLE_GRANNY_SMITH  = 2;

public static final int ORANGE_NAVEL        = 0;
public static final int ORANGE_TEMPLE       = 1;
public static final int ORANGE_BLOOD        = 2;
```
정수 열거 패턴(int enum pattern)  기법에는 단점이 많다. 타입 안전을 보장할 방법이 없으며 표현력도 좋지 않다. 
오렌지를 건네야 할 메서드에 사과를 보내고 동등 연산자(==)로 비교하더라도 컴파일러는 아무런 경고

``` java
// 향긋한 오렌지 향의 사과 소스!
int i = (APPLE_FUJI - ORANGE_TEMPLE) / APPLE_PIPPIN;
```
사과용 상수의 이름은 모두 APPLE_로 시작하고, 오렌지용 상수는 ORANGE_로 시작한다. 
자바가 정수 열거 패턴을 위한 별도 이름공간(namespace)을 지원하지 않기 때문에 어쩔 수 없이 접두어를 써서 이름 충돌을 방지하는 것이다. 

정수 열거 패턴을 사용한 프로그램은 깨지기 쉽다. 평범한 상수를 나열한 것뿐이라 컴파일하면 그 값이 클라이언트 파일에 그대로 새겨진다. 
따라서 상수의 값이 바뀌면 클라이언트도 반드시 다시 컴파일해야 한다. 다시 컴파일하지 않은 클라이언트는 실행이 되더라도 엉뚱하게 동작할 것이다. 

정수 상수는 문자열로 출력하기가 다소 까다롭다. 그 값을 출력하거나 디버거로 살펴보면(의미가 아닌) 단지 숫자로만 보여서 썩 도움이 되지 않는다. 
같은 정수 열거 그룹에 속한 모든 상수를 한 바퀴 순회하는 방법도 마땅치 않다. 심지어 그 안에 상수가 몇 개인지도 알 수 없다. 

정수 대신 문자열 상수를 사용하는 변형 패턴도 있다. 문자열 열거 패턴(string enum pattern)이라 하는 이 변형은 더 나쁘다.
상수의 의미를 출력할 수 있다는 점은 좋지만, 경험이 부족한 프로그래머가 문자열 상수의 이름 대신 문자열 값을 그대로 하드코딩하게 만들기 때문이다. 
이렇게 하드코딩한 문자열에 오타가 있어도 컴파일러는 확인할 길이 없으니 자연스럽게 런타임 버그가 생긴다. 문자열 비교에 따른 성능 저하 역시 당연한 결과다.

다행히 자바는 열거 패턴의 단점을 말끔히 씻어주는 동시에 여러 장점을 안겨주는 대안을 제시했다. 바로 열거 타입(enum type)이다. 
다음은 열거 타입의 가장 단순한 형태다. 

코드 34-2 가장 단순한 열거 타입
``` java
public enum Apple { FUJI, PIPPIN, GRANNY_SMITH }
public enum Orange { NAVEL, TEMPLE, BLOOD }
```
 겉보기에는 C, C++, C#같은 다른 언어의 열거 타입과 비슷하지만, 보이는 것이 다가 아니다. 자바의 열거 타입은 완전한 형태의 클래스라서 (단순한 정숫값일 뿐인)
 다른 언어의 열거 타입보다 훨씬 강력하다. 
 
자바 열거 타입을 뒷받침하는 아이디어는 단순하다. 열거 타입 자체는 클래스이며, 상수 하나당 자신의 인스턴스를 하나씩 만들어 public static final  필드로 공개한다. 
열거 타입은 밖에서 접근할 수 있는 생성자를 제공하지 않으므로 사실상 final이다. 따라서 클라이언트가 인스턴스를 직접 생성하거나 확장할 수 없으니 열거 타입 선언으로 만들어진 인스턴스들은 딱 하나씩만 존재함이 보장된다.
다시 말해 열거 타입은 인스턴스 통제된다. 싱글턴은 원소가 하나뿐인 열거 타입이라 할 수 있고, 거꾸로 열거 타입은 싱글턴을 일반화한 형태라고 볼 수 있다. 

열거 타입은 컴파일타임 타입 안전성을 제공한다. 코드 34-2의 Apple의 열거 타입을 매개변수로 받는 메서드를 선언했다면, 건네받은 참조는 (null이 아니라면) Apple의 세 가지 값 중 하나임이 확실하다. 
다른 타입의 값을 넘기려 하면 컴파일 오류가 난다. 타입이 다른 열거 타입 변수에 할당하려 하거나 다른 열거 타입의 값끼리 == 연산자로 비교하려는 꼴이기 때문이다. 

열거 타입에는 각자의 이름공간이 있어서 이름이 같은 상수도 평화롭게 공존한다. 열거 타입에 새로운 상수를 추가하거나 순서를 바꿔도 다시 컴파일하지 않아도 된다. 
공개되는 것이 오직 필드의 이름뿐이라, 정수 열거 패턴과 달리 상수 값이 클라이언트로 컴파일되어 각인되지 않기 때문이다. 마지막으로 열거 타입의 toString메서드는 출력하기에 적합한 문자열을 내어준다. 

이처럼 열거 타입은 정수 열거 패턴의 단점들을 해소해준다. 여기서 끝이 아니다. 열거 타입에는 임의의 메서드나 필드를 추가할 수 있고 임의의 인터페이스를 구현하게 할 수도 있다. 
Object 메서드들을 높은 품질로 구현해놨고, Comparable과 Serializable을 구현했으며, 그 직렬화 형태도 웬만큼 변형을 가해도 문제없이 동작하게끔 구현해놨다. 

태양계의 여덟 행성은 거대한 열거 타입을 설명하기에 좋은 예다. 각 행성에는 질량 과 반지름이 있고, 이 두 속성을 이용해 표면 중력을 계산할 수 있다.
따라서 어떤 객체의 질량이 주어지면 그 객체가 행성 표면에 있을 때의 무게도 계산할 수 있다. 이 열거 타입의 못브은 다음과 같다. 각 열거 타입 상수 오른쪼 괄호 안 숫자는 생성자에 넘겨지는 매개변수로, 
이 예에서는 행성의 질량과 반지름을 뜻한다. 

코드 34-3 데이터와 메서드를 갖는 열거 타입
``` java
public enum Planet {
    MERCURY(3.302E+23, 2.439e6),
    VENUS (4.869E+24, 6.052e6),
    EARTH (5.975E+24, 6.378e6),
    MARS (6.419E+23, 3.393e6),
    JUPITER (1.899E+27, 7.149e7),
    SATURN (5.685E+26, 6.027e7),
    URANUS (8.683E+25, 2.556e7),
    NEPTUNE (1.024E+26, 2.477e7);
    
    private final double mass;                  // 질량(단위: 킬로그램)
    private final double radius;                // 반지름(단위: 미터)
    private final double surfaceGravity;        // 표면중력(단위: m / s^2)
    
    // 중력상수( 단위: m^3 / kg s^2)
    private static final double G = 6.67300E - 11;
    
    // 생성자
    Planet(double mass, double radius) {
        this.mass = mass;
        this.radius = radius;
        surfaceGravity = G * mass / (radius * radius);
    }
    public double mass()    { return mass; }
    public double radius()    { return radius; }
    public double surfaceGravity()    { return surfaceGravity; }
    
    public double surfaceWeight(double mass) {
        return mass * surfaceGravity;   // F = ma
    }
}
```
**열거 타입 상수 각각을 특정 데이터와 연결지으려면 생성자에서 데이터를 받아 인스턴스 필드에 저장하면 된다.**
열거 타입은 근본적으로 불변이라 모든 필드는 final이어야 한다. 필드를 public으로 선언해도 되지만, private으로 두고 별도의 
public 접근자 메서드를 두는 게 낫다. 

``` java
public class WeightTable {
    public static void main(String[] args) {
        double earthWeight = Double.parseDouble(args[0]);
        double mass = earthWeight / Planet.EARTH.surfaceGravity();
        for (Planet p : Planet.values()) {
            System.out.printf("%s에서의 무게는 %f이다. %n", p, p.surfaceWeight(mass));
        }
    }
}
```
열거 타입은 자신 안에 정의된 상수들의 값을 배열에 담아 반환하는 정적 메서드인 values를 제공한다. 값들은 선언된 순서로 저장된다. 

코드 34-4 값에 따라 분기하는 열거 타입 - 이대로 만족하는가?
``` java
public enum Operation {
    PLUS, MINUS, TIMES, DIVIDE;
    // 상수가 뜻하는 연산을 수행한다. 
    public double apply(double x, double y) {
        switch (this) {
            case PLUS:  return x + y;
            case MINUS: return x - y;
            case TIMES: return x * y;
            case DIVIDE: return x / y;
        }
        throw new AssertionError("알 수 없는 연산: " + this);
    }
}
```
동작은 하지만 그리 예쁘지 않다. 마지막의 throw문은 실제로는 도달할 일이 없지만 기술적으로 도달할 수 있기 때문에
생략하면 컴파일조차 되지 않는다. 더 나쁜 점은 깨지기 쉬운 코드라는 사실이다. 
예) 새로운 상수를 추가하면 case문도 추가해야 하며, 혹시라도 깜빡하다면, '알 수 없는 연산'이라는 런타임 오류를 내며 프로그램이 종료된다. 

코드 34-5 상수별 메서드 구현을 활용한 열거 타입
``` java
public enum Operation {
   PLUS {public double apply(double x, double y) { return x + y;}},
   MINUS {public double apply(double x, double y) { return x - y;}},
   TIMES {public double apply(double x, double y) { return x * y;}},
   DIVIDE {public double apply(double x, double y) { return x / y;}};
    
   public abstract double apply(double x, double y);
}
```
다음은 Operation의 toString을 재정의해 해당 연산을 뜻하는 기호를 반환하도록 한 예다. 
코드 34-6 상수별 클래스 몸체(class body)와 데이터를 사용한 열거 타입
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
다음은 이 toString 이 계산식 출력을 얼마나 편하게 해주는지를 보여준다. 
``` java
public class Calculator {
    public static void main(String[] args) {
        double x = Double.parseDouble(args[0]);
        double y = Double.parseDouble(args[1]);
        for (Operation op : Operation.values()) {
            System.out.printf("%f %s %f = %f%n", x, op, y, op.apply(x, y));
        }
    }
}
```


#### 핵심정리
- 열거 타입은 확실히 정수 상수보다 뛰어나다. 더 읽기 쉽고 안전하고 강력하다.
- 대다수 열거 타입이 명시적 생성자나 메서드 없이 쓰이지만, 각 상수를 특정 데이터와 연결짓거나 상수마다 다르게 동작하게 할 때는 필요하다. 
- 드물게는 하나의 메서드가 상수별로 다르게 동작해야 할 때도 있다. 이런 열거 타입에서는 switch 문 대신 상수별 메서드 구현을 사용하자. 
- 열거 타입 상수 일부가 같은 동작을 공유한다면 전략 열거 타입 패턴을 사용하자. 