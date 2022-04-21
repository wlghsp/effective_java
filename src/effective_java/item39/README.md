# Item 39 명명 패턴보다 애너테이션을 사용하라 

--------------------------------------------
#### 명명 패턴의 단점 
1. 오타가 나면 안됨
2. 올바른 프로그램 요소에서만 사용되리라 보증할 방법이 없다. 
3. 프로그램 요소를 매개변수로 전달할 마땅한 방법이 없다.


## 애너테이션은 위 문제들을 모두 해결해준다. 



코드 39-1 마커(marker) 애너테이션 타입 선언

<hr>

``` java
import java.lang.annotation.*;

/**
 * 테스트 메서드임을 선언하는 애너테이션인다. 
 * 매개변수 없는 정적 메서드 전용이다. 
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface Test {
}
```
@Retention과 @Target과 같이  
애너테이션 선언에 다는 애너테이션을 메타애너테이션(meta-annotation)이라 한다. 

@Retention(RetentionPolicy.RUNTIME) 메타애너테이션은 @Test가 런타임에도 유지되어야 한다는 표시다. 
만약 이 메타애너테이션을 생략하면 테스트 도구는 @Test를 인식할 수 없다. 

한편, @Target(ElementType.METHOD) 메타애너테이션은 @Test가 반드시 메서드 선언에서만 사용돼야함을 알려준다. 
따라서 클래스 선언, 필드 선언 등 다른 프로그램 요소에는 달 수 없다. 


코드 39-2 마커 애너테이션을 사용한 프로그램 예
``` java
public class Sample {
    @Test public static void m1() { } // 성공해야 한다.
    public static void m2() {}
    @Test public static void m3() {  // 실패해야 한다.
        throw new RuntimeException("실패");
    }
    public static void m4() {}
    @Test public void m5() { } // 잘못 사용한 예: 정적 메서드가 아니다.
    @Test public static void m7() {  // 실패해야 한다.
        throw new RuntimeException("실패");
    }
    public static void m8() {}
```

코드 39-3 마커 애너테이션을 처리하는 프로그램
``` java
import java.lang.reflect.*;

public class RunTests {
    public static void main(String[] args) throws Exception {
        int tests = 0;
        int passed = 0;
        Class<?> testClass = Class.forName(args[0]);
        for (Method m : testClass.getDeclaredMethods()) {
            if(m.isAnnotationPresent(Test.class)) {
                test++;
                try {
                    m.invoke(null);
                    passed++;
                } catch (InvocationTargetException wrappedExc) {
                    Throwable exc = wrappedExc.getCause();
                    System.out.println(m + " 실패: " + exc);
                } catch (Exception exc) {
                    System.out.println("잘못 사용한 @Test: " + m);
                }
            }
        }
        System.out.printf("성공: %d, 실패: %d%n", passed, tests-passed);
    }
}
```




애너테이션으로 할 수 있는 일을 명명 패턴으로 처리할 이유는 없다. 

자바 프로그래머라면 예외 없이 자바가 제공하는 애너테이션 타입들은 사용해야 한다. 