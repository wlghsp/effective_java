# Item 83 지연 초기화는 신중히 사용하라

--------------------------------------------

> 지연 초기화(lazy initialization)는 필드의 초기화 시점을 그 값이 처음 필요할 때까지 늦추는 기법이다. 

그래서 그 값이 전혀 쓰이지 않으면 초기화도 결코 일어나지 않는다. 

이 기법은 정적 필드와 인스턴스 필드 모두에 사용할 수 있다. 

지연 초기화는 주로 최적화 용도로 쓰이지만, 클래스와 인스턴스 초기화 때 발생하는 위험한 순환 문제를 해결하는 효과도 있다. 

<hr>

다른 모든 최적화와 마찬가지로 지연 초기화에 대해 해줄 최선의 조언은 "필요할 때까지는 하지 말라"다.

지연초기화는 양날의 검이다. 

클래스 혹은 인스턴스 생성 시의 초기화 비용은 줄지만 그 대신 지연 초기화는 필드에 접근하는 비용은 커진다. 

지연 초기화하려는 필드들 중 결국 초기화가 이뤄지는 비율에 따라, 

실제 초기화에 드는 비용에 따라, 초기화된 각 필드를 얼마나 빈번히 호출하느냐에 따라 지연초기화가 (다른 수많은 최적화와 마찬가지로) 실제로는 성능을 느려지게 할 수도 있다. 

<hr>

그럼에도 지연 초기화가 필요할 때가 있다. 해당 클래스의 인스턴스 중 그 필드를 사용하는 인스턴스의 비율이 낮은 반면, 

그 필드를 초기화하는 비용이 크다면 지연 초기화가 제 역할을 해줄 것이다. 

하지만 안타깝게도 정말 그런지를 알 수 있는 유일한 방법은 지연 초기화 적용 전후의 성능을 측정해보는 것이다. 

<hr>

멀티스레드 환경에서는 지연 초기화를 하기가 까다롭다. 

지연 초기화는 필드를 둘 이상의 스레드가 공유한다면 어떤 형태로든 반드시 동기화해야 한다. 

> 대부분의 상황에서 일반적인 초기화가 지연 초기화보다 낫다. 

다음은 인스턴스 필드를 선언할 때 수행하는 일반적인 초기화의 모습이다. 

final 한정자를 사용했음에 주목하자. 

코드 83-1 인스턴스 필드를 초기화하는 일반적인 방법
``` java
private final FiledType field = computeFieldValue();
```
> 지연 초기화가 초기화 순환성(initialization circularity)을 깨뜨릴 것 같으면 synchronized를 단 접근자를 사용하자.

코드 83-2 인스턴스 필드의 지연 초기화 - synchronized 접근자 방식
``` java
private FieldType field;

private synchronized FieldType getField() {
    if (field == null)
        filed = computeFieldValue();
    return field;
}
```
이상의 두 관용구 (보통의 초기화와 synchronized 접근자를 사용한 지연 초기화)는 정적 필드에도 똑같이 적용된다. 

물론 필드와 접근자 메서드 선언에 static 한정자를 추가해야 한다. 

<hr>

> 성능 때문에 정적 필드를 지연 초기화해야 한다면 지연 초기화 홀더 클래스(lazy initialization holder class) 관용구를 사용하자.

코드 83-3 정적 필드용 지연 초기화 홀더 클래스 관용구
``` java
private static class FieldHolder {
    static final FieldType field = computeFieldValue();
}

private static FieldType getField() { return FieldHolder.field; }
```

getField가 처음 호출되는 순간 FieldHolder.field가 처음 읽히면서, 비로소 FieldHolder 클래스 초기화를 촉발한다. 

이 관용구의 멋진 점은 getField 메서드가 필드에 접근하면서 동기화를 전혀 하지 않으니 성능이 느려질 거리가 전혀 없다는 것이다. 


> 성능 때문에 인스턴스 필드를 지연 초기화해야 한다면 이중검사(double-check) 관용구를 사용하라. 

이 관용구는 초기화된 필드에 접근할 때의 동기화 비용을 없애준다. 

필드의 값을 두 번 검사하는 방식으로, 한 번은 동기화 없이 검사하고, (필드가 아직 초기화되지 않았다면) 두 번째는 동기화하여 검사한다.


코드 83-4 인스턴스 필드 지연 초기화용 이중검사 관용구
``` java
private volatile FieldType field;

private FieldType getField() {
    FieldType result = field;
    if (result != null) { // 첫 번째 검사 (락 사용 안 함)
        return result;
    
    synchronized(this) {
        if(filed == null)  // 두 번째 검사( 락 사용)
            field = computeFieldValue();
        return field;
    }
}
```

코드 83-5 단일검사 관용구 - 초기화가 중복해서 일어날 수 있다!
``` java
private volatile FieldType field;

private FieldType getField() {
    FieldType result = field;
    if (result == null)
        field = result = computeFieldValue();
    return result;
}
```

### 핵심 정리
- 대부분의 필드는 지연시키지 말고 곧바로 초기화해야 한다. 
- 성능 때문에 혹은 위험한 초기화 순환을 막기 위해 꼭 지연 초기화를 써야 한다면 올바른 지연 초기화 기법을 사용하자. 
- 인스턴스 필드에는 이중검사 관용구를, 정적 필드에는 지연 초기화 홀더 클래스 관용구를 사용하자. 
- 반복해 초기화해도 괜찮은 인스턴스 필드에는 단일검사 관용구도 고려 대상이다. 