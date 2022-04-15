# Item 50 적시에 방어적 복사본을 만들라

--------------------------------------------

자바는 안전한 언어다. 이것이 자바를 쓰는 즐거움 중 하나다. 

네이티브 메서드를 사용하지 않으니 C, C++ 같이 안전하지 않은 언어에서 흔히 보는 버퍼 오버런, 배열 오버런, 와일드 포인터 같은 

메모리 충돌 오류에서 안전하다. 자바로 작성한 클래스는 시스템의 다른 부분에서 무슨 짓을 하든 그 불변식이 지켜진다. 

메모리 전체를 하나의 거대한 배열로 다루는 언어에서는 누릴 수 없는 강점이다. 

<hr>

하지만 아무리 자바라 해도 다른 클래스로부터의 침범을 아무런 노력 없이 다 막을 수 있는 건 아니다. 

그러나 

> **클라이언트가 여러분의 불변식을 깨뜨리려 혈안이 되어 있다고 가정하고 방어적으로 프로그래밍해야 한다.**

어떤 객체든 그 객체의 허락 없이는 외부에서 내부를 수정하는 일은 불가능하다. 

하지만 주의를 기울이지 않으면 자기도 모르게 내부를 수정하도록 허락하는 경우가 생긴다.



 기간(period)을 표현하는 다음 클래스는 한번 값이 정해지면 변하지 않도록 할 생각이었다. 
 
코드 50-1 기간을 표현하는 클래스 - 불변식을 지키지 못한다. 
``` java
public final class Period {
    private final Date start;
    private final Date end;
    
    /**
     * @param start 시작 시각
     * @param end 종료 시각; 시작 시각보다 뒤여야 한다. 
     * @throws IllegalArgumentException 시작 시각이 종료 시각보다 늦을 때 발생한다. 
     * @throws NullPointerException start나 end가 null이면 발생한다. 
    */
    public Period(Date start, Date end) {
        if(start.compareTo(end) > 0)
            throw new IllegalArgumentException(
                start + "가 " + end + "보다 늦다.");
        this.start = start;
        this.end = end;
    }
    
    public Date start() {
        return start;
    }
    
    public Date end() {
        return end;
    }
    
    ... // 나머지 코드 생략
}
```

이 클래스는 불변처럼 보이고, 시작 시각이 종료 시각보다 늦을 수 없다는 불변식이 무리 없이 지켜질 것 같다. 

하지만 Date가 가변이라는 사실을 이용하면 어렵지 않게 그 불변식을 깨뜨릴 수 있다. 

코드 50-2 Period 인스턴스의 내부를 공격해보자. 
``` java
Date start = new Date();
Date end = new Date();
Period p = new Period(start, end);
end.setYear(78); // p의 내부를 수정했다!
```

다행히 자바 8이후로는 쉽게 해결 가능 

Date 대신 불변인 Instant를 사용하면 된다. 

(혹은 LocalDateTime이나 ZonedDataTime을 사용해도 된다). 

> Date는 낡은 API이니 새로운 코드를 작성할 때는 더 이상 사용하면 안된다. 

하지만 안 쓴다고 해결되는 건 아니다. 

Date처럼 가변인 낡은 값 타입을 사용하던 시절이 워낙 길었던 탓에 여전히 많은 API와 내부 구현에 그 잔재가 남아 있다. 

> 이번 아이템은 예전에 작성된 낡은 코드들을 대처하기 위한 것이다.

<hr>

외부 공격으로부터 Period 인스턴스의 내부를 보호하려면 

생성자에서 받은 가변 매개변수 각각을 방어적으로 복사(defensive copy) 해야 한다. 

그런 다음 Period 인스턴스 안에서는 원본이 아닌 복사본을 사용한다. 

코드 50-3 수정한 생성자 - 매개변수의 방어적 복사본을 만든다
``` java
public Period(Date start, Date end) {
    this.start = new Date(start.getTime());
    this.end = new Date(end.getTime());
    
    if (this.start.compareTo(this.end) > 0)
        throw enw IllegalArgumentException(
            this.start + "가" + this.end + "보다 늦다.");
}
```
새로 작성한 생성자를 사용하면 앞서의 공격은 더 이상 Period에 위협이 되지 않는다. 

### 매개변수의 유효성을 검사 하기 전에 방어적 복사본을 만들고, 이 복사본으로 유효성을 검사한 점에 주목하자. 

순서가 부자연스러워 보이겠지만 반드시 이렇게 작성해야 한다. 

멀티스레딩 환경이라면 원본 객체의 유효성을 검사한 후 복사본을 만드는 그 찰나의 취약한 순간에 다른 스레드가 

원본 객체를 수정할 위험이 있기 때무닝다. 

방어적 복사를 매개변수 유효성 검사 전에 수행하면 이런 위험에서 해방될 수 있다. 

> 컴퓨터 보안 커뮤니티에서는 이를 검사시점/사용시점(time-of-check/time-of-use)공격 혹은 영어표기를 줄여 TOCTOU 공격이라 한다. 


> 매개변수가 제 3자에 의해 확장될 수 있는 타입이라면 방어적 복사본을 만들 때 clone을 사용해서는 안 된다. 

코드 50-4 Period 인스턴스를 향한 두 번째 공격
``` java
Date start = new Date();
Date end = new Date();
Period p = new Period(start, end);
p.end().setYear(78); // p의 내부를 변경했다!
```
이 공격을 막아내려면 단순히 접근자가 **가변 필드의 방어적 복사본을 반환하면 된다**

코드 50-5 수정한 접근자 - 필드의 방어적 복사본을 반환한다. 
``` java
public Date start() {
    return new Date(start.getTime());
}

public Date end() {
    return new Date(end.getTime());
}
```
새로운 접근자까지 갖추면 Period는 완벽한 불변으로 거듭난다. 


### 핵심 정리
- 클래스가 클라이언트로부터 받는 혹은 클라이언트로 반환하는 구성요소가 가변이라면 그 요소는 반드시 방어적으로 복사해야 한다. 
- 복사 비용이 너무 크거나 클라이언트가 그 요소를 잘못 수정할 일이 없음을 신뢰한다면 방어적 복사를 수행하는 대신 해당 구성요소를 수정했을 때의 책임이 클아이언트에 있음을 문서에 명시하도록 하자. 


