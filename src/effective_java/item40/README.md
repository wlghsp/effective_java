# Item 40 @Override 애너테이션을 일관되게 사용하라 

--------------------------------------------
자바가 기본으로 제공하는 애너테이션 중 보통의 프로그래머에게 가장 중요한 것은 **@Override**

@Override 특징
1. 메서드 선언에만 달 수 있음
2. 이 애너테이션이 달려 있다면, 상위 타입의 매서드를 재정의했음을 뜻함. 
3. 일관되게 사용하면 여러 가지 악명 높은 버그들을 예방해줌. 


코드 40-1 영어 알파벳 2개로 구성된 문자열을 표현한느 클래스 - 버그를 찾아보자. 
``` java
public clas Bigram {
    private final char first;
    private final char second;
    
    public Bigram(char first, char second) {
        this.first = first;
        this.second = second;
    }
    public boolean equals(Bigram b) {
        return b.first == first && b.second == second;
    }
    public int hashCode() {
        return 31 * first + second;
    }
    
    public static void main(String[] args) {
        Set<Bigram> s = new HashSet<>();
        for (int i = 0; i < 10; i++) {
            for(char ch = 'a'; ch <= 'z'; ch++)
                s.add(new Bigram(ch, ch));
        System.out.println(s.size());
    }
}
```
Set은 중복을 허용하지 않으니 26이 출력될 거 같지만, 260이 출력됨. 

equals '재정의'한 게 아니라 '다중정의' 해버림.

이 오류는 컴파일러가 찾아낼 수 있으나 Object.equals 를 재정의한다는 의도를 명시해야함. 
``` java
@Override public boolean equals(Bigram b) {
        return b.first == first && b.second == second;
}
```
이처럼 @Override 애너테이션을 달고 다시 컴파일하면 컴파일 오류가 발생 

잘못된 부분을 명확히 알려주므로 곧장 올바르게 수정 가능하다. 

``` java
@Override public boolean equals(Object o) {
        if(!(o instanceOf Bigram)) 
            return false;
        Bigram b = (Bigram) o;
        return b.first == first && b.second == second;
}
```
> 상위 클래스의 메서드를 재정의하려는 모든 메서드에 @Override 애너테이션을 달자.

#### 예외는 한 가지: 구체 클래스에서 상위 클래스의 추상 메서드를 재정의할 때는 달지 않아도 됨. 


<hr>

### 핵심정리
- 재정의한 모든 메서드에 @Override 애너테이션을 의식적으로 달면 실수했을 때 컴파일러가 알려줌
- 예외는 구체 클래스에서 상위클래스의 추상 메서드를 재정의한 경우엔, 이 애너테이션을 달지 않아도 됨.