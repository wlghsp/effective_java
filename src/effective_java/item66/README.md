# Item 66 네이티브 메서드는 신중히 사용하라

--------------------------------------------

> 자바 네이티브 인터페이스 (Java Native Interface, JNI)

: 자바 프로그램이 네이티브 메서드를 호출하는 기술

* 네이티브 메서드란? C나 C++같은 네이티브 프로그래밍 언어로 작성한 메서드 

### 네이티브 메서드의 주요 쓰임 3가지

1. 레지스트리 같은 플랫폼 특화 기능 사용
2. 네이티브 코드로 작성된 기존 라이브러리 사용. 레거시 데이터를 사용하는 레거시 라이브러리
3. 성능 개선을 목적으로 성능에 결정적인 영향을 주는 영역만 따로 네이티브 언어로 작성한다. 

<hr>

플랫폼 특화 기능을 활용하려면 네이티브 메서드를 사용해야 한다. 

하지만 자바가 성숙해가면서 (OS 같은) 하부 플랫폼의 기능들을 점차 흡수하고 있다. 

그래서 네이티브 메서드를 사용할 필요가 계속 줄어들고 있다. 

예) 자바 9은 새로 process API를 추가해 OS 프로세스에 접근하는 길을 열어주었다. 

* 성능을 개선할 목적으로 네이티브 메서드를 사용하는 것은 거의 권장하지 않는다. 

자바 초기 시절(자바 3 전)이라면 이야기가 다르지만, JVM은 그동안 엄청난 속도로 발전해왔다. 

대부분 작업에서 지금의 자바는 다른 플랫폼에서 견줄마낳나 성능을 보인다. 

예) java.math가 처음 추가된 자바 1.1 시절 BigInteger는 C로 작성한 고성능 라이브러리에 의지했다. 

그러다 자바 3때 순수 자바로 다시 구현되면서 세심히 튜닝한 결과, 원래의 네이티브 구현보다도 더 빨라졌다. 

Sadly, 그 후로 BigInteger는 자바 8에서 큰수의 곱셈 성능을 개선한 것을 제외하고는 더 이상의 커다란 변화가 없었다. 

<hr>

한편 네이티브 라이브러리 쪽은 GNU 다중 정밀 연산 라이브러리(GMP)를 필두로 개선 작업이 계속돼왔다. 

그래서 정말로 고성능의 다중 정밀 연산이 필요한 자바 프로그래머라면 이제 네이티브 메서드를 통해 GMP를 사용하는 걸 

고려해도 좋다. 

<hr>

네이티브 메서드에는 심각한 단점이 있다. 

* 네이티브 언어가 안전하지 않으므로 네이티브 메서드를 사용하는 애플리케이션도 메모리 훼손 오류로부터 더 이상 안전하지 않다. 
* 네이티브 언어는 자바보다 플랫폼을 많이 타서 이식성도 낮다. 디버깅도 더 어렵다.
* 주의하지 않으면 속도가 오히려 느려질 수도 있다. 
* 가비지 컬렉터가 네이티브 메모리는 자동 회수하지 못하고, 심지어 추적조차 할 수 없다. 
* 자바 코드와 네이티브 코드의 경계를 넘나들 때마다 비용도 추가된다. 
* 네이티브 메서드와 자바 코드 사이의 '접착 코드(glue code)'를 작성해야 하는데, 이는 귀찮은 작업이기도 하거니와 가독성도 떨어진다. 

### 핵심 정리
- 네이티브 메서드를 사용하려거든 한번 더 생각하라. 
- 네이티브 메서드가 성능을 개선해주는 일은 많지 않다. 
- 저수준 자원이나 네이티브 라이브러리를 사용해야만 해서 어쩔 수 없더라도 네이티브 코드는 최소한만 사용하고 철저히 테스트하라. 
- 네이티브 코드 안에 숨은 단 하나의 버그가 여러분 애플리케이션 전체를 훼손할 수도 있다. 