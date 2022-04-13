# Item 47 반환 타입으로는 스트림보다 컬렉션이 낫다

--------------------------------------------

원소 시퀀스, 즉 일련의 원소를 반환하는 메서드는 수없이 많다. 

자바 7까지는 이런 메서드의 반환 타입으로 Collection, Set, List 같은 컬렉션 인터페이스, 혹은

Iterable이나 배열을 썼다. 

기본은 컬렉션 인터페이스다.  for-each 문에서만 쓰이거나 반환된 원소 시퀀스가 (주로 contains(Object)같은)

일부 Collection 메서드를 구현할 수 없을 때는 Iterable 인터페이스를 섰다. 반환 원소들이 기본 타입이거나 성능에 

민감한 상왕이라면 배열을 썼다. 그런데 자바 8이 스트림이라는 개념을 들고 오면서 이 선택이 아주 복잡한 일이 되어버렸다. 

<hr>

스트림은 반복(iteration)을 지원하지 않는다.

따라서 스트림과 반복을 알맞게 조합해야 좋은 코드가 나온다. 

Stream 인터페이스는 Iterable 인터페이스가 정의한 추상 메서드를 전부 포함할 뿐만 아니라, Iterable 인터페이스가 정의한 방식대로 동작한다. 

그럼에도 for-each 로 스트림을 반복할 수 없는 까닭은 바로 Stream 이 Iterable을 확장(extend)하지 않아서다. 


<hr>


코드 47-1 자바 타입 추론의 한계로 컴파일되지 않는다. 
``` java
for (ProcessHandle ph : ProcessHandle.allProcesses()::iterator) {
    // 프로세스를 처리한다. 
}
```
이 코드는 다음의 컴파일 오류를 낸다.

이 오류를 바로잡으려면 메서드 참조를 매개변수화된 Iterable로 적절히 형변환해줘야 한다. 

코드 47-2 스트림을 반복하기 위한 '끔찍한' 우회 방법
``` java
for(ProcessHandle ph : (Iterable<ProcessHandle>) ProcessHandle.allProcesses()::iterator) {
    // 프로세스를 처리한다. 
}
```
작동은 하지만 실전에 쓰기에는 너무 난잡하고 직관성이 떨어진다. 

<hr>

어댑터 메서드를 사용하면 상황이 나아진다. 

자바는 이런 메서드를 제공하지 않지만 다음 코드와 같이 쉽게 만들어낼 수 있다. 

이 경우에는 자바의 타입 추론이 문맥을 잘 파악하여 어댑터 메서드 안에서 따로 형변환하지 않아도 된다. 


코드 47-3 Stream<E>를 Iterable<E>로 중개해주는 어댑터
``` java
public static <E> Iterable<E> iterableOf(Stream<E> stream) {
    return stream::iterator;
}
```
어댑터를 사용하면 어떤 스트림도 for-each 문으로 반복할 수 있다. 

``` java
for (ProcessHandle ph : iterableOf(ProcessHandle.allProcesses())) {
    // 프로세스를 처리한다. 
}
```

코드 47-4 Iterable<E>를 Stream<E>로 중개해주는 어댑터
``` java
public static <E> Stream<E> streamOf(Iterable<E> iterable) {
    return StreamSupport.stream(iterable.spliterator(), false);
}
```
공개  API를 작성할 때는 스트림 파이프라인을 사용하는 사람과 반복문에서 쓰려는 사람 모두를 배려해야 한다. 

<hr>

Collection 인터페이스는 Iterable의 하위 타입이고 stream 메서드도 제공하니 반복과 스트림을 동시에 지원한다. 

따라서 원소 시퀀스를 반환하는 공개 API의 반환 타입에는 Collection이나 그 하위 타입을 쓰는 게 일반적으로 최선이다. 

Arrays 역시 Arrays.asList와 stream.of 메서드로 손쉽게 반복과 스트림을 지원할 수 있다. 반환하는 시퀀스의 크기가 메모리에 

올려도 안전할만큼 작다면 ArrayList나 HashSet 같은 표준 컬렉션 구현체를 반환하는게 최선일 수 있다. 

하지만 ** 단지 컬렉션을 반환한다는 이유로 덩치 큰 시퀀스를 메모리에 올려서는 안된다 ** 

<hr>

반환할 시퀀스가 크지만 표현을 간결하게 할 수 있다면 전용 컬렉션을 구현하는 방안을 검토해보자. 

예컨대 주어진 집합의 멱집합(한 집합의 모든 부분집합을 원소로 하는 집합)을 반환하는 상황이다. 

{a, b, c}의 멱집합은 {{}, {a}, {b}, {c}, {a,b}, {a,c}, {b,c}, {a,b,c}}다. 

원소 개수가 n개면 멱집합의 원소 개수는 2^n개가 된다. 

멱집합을 표준 컬렉션 구현체에 저장하려는 생각은 위함하다. 하지만 AbstractList를 이용하면 훌륭한 전용 컬렉션을 손쉽게 구현할 수 있다. 

비결은 멱집합을 구성하는 각 원소의 인덱스를 비트 벡터로 사용하는 것이다. 

인덱스의 n번째 비트 값은 멱집합의 해당 원소가 원래 집합의 n번째 원소를 포함하는지 여부를 알려준다. 

따라서 0부터 2^n-1 까지의 이진수와 원소 n개인 집합의 멱집합과 자연스럽게 매핑된다. 

(주석: 예를 들어 {a, b, c}의 멱집합은 원소가 8개이므로 유효한 인덱스는 0~7이며, 이진수로는 000~111이다.
이때 인덱스를 이진수로 나타내면, 각 n번째 자리의 값이 각각 원소 a, b, c를 포함하는 여부를 알려준다. 즉 멱집합의 000번째 원소는 
{}, 001번째 원소는 {a}, 101번째는 {c,a}, 111번째 원소는 {c,b,a}가 되는 식이다.)

코드 47-5 입력 집합의 멱집합을 전용 컬렉션에 담아 반환한다. 

``` java
public class PowerSet {
    public static final <E>Collection<Set<E>> of(Set<E> s) {
        List<E> src = new ArrayList<>();
        if (src.size() > 30) 
            throw new IllegalArgumentException("집합에 원소가 너무 많습니다(최대 30개).: "+ s);
            
            return new AbstractList<Set<E>>() {
                @Override
                public int size() {
                    // 멱집합의 크기는 2를 원래 집합의 원소 수만큼 거듭제곱 것과 같다. 
                    return 1 << src.size();
                }

                @Override
                public boolean contains(Object o) {
                    return o instanceof Set && src.containsAll((Set) o);
                }

                @Override
                public Set<E> get(int index) {
                    Set<E> result = new HashSet<>();
                    for (int i = 0; index !=0; i++, index >>= 1) {
                        if ((index & 1) == 1) result.add(src.get(i));
                    }
                    return result;
                }
            };
    }
}

```
입력 집합의 원소 수가 30을 넘으면 PowerSet.of가 예외를 던진다. 

이는 Stream이나 Iterable이 아닌 Collection을 반환 타입으로 쓸 때의 단점을 잘 보여준다. 

다시 말해, Collection의 size메서드가 int값을 반환하므로 PowerSet.of가 반환되는 시퀀스의 

최대 길이는 Integer.MAX_VALUE 혹은 2^31-1로 제한된다. Collection명세에 따르면 

컬렉션이 더 크거나 심지어 무한대일 때 size가 2^31-1을 반환해도 되지만 완전히 만족스러운 해법은 아니다.  


<hr>

AbstractCollction을 활용해서 Collection 구현체를 작성할 때는 Iterable용 메서드 외에 2개만 더 구현하면 된다. 

바로 contains와 size다. 

이 메서드들은 손쉽게 효율적으로 구현할 수 있다. 

(반복이 시작되기 전에는 시퀀스의 내용을 확정할 수 없는 등의 사유로) contains와 size를 구현하는 게 불가능할 때는 

컬렉션보다는 스트림이나 Iterable을 반환하는 편이 낫다. 


코드 47-6 입력 리스트의 모든 부분리스트를 스트림으로 반환한다. 
``` java
public class SubLists {
    
    public static <E> Stream<List<E>> of(List<E> list) {
        return Stream.concat(Stream.of(Collections.emptyList()),
                prefixes(list).flatMap(SubLists::suffixes));
    }

    public static <E> Stream<List<E>> prefixes(List<E> list) {
        return IntStream.rangeClosed(1, list.size())
                .mapToObj(end -> list.subList(0, end));
    }

    public static <E> Stream<List<E>> suffixes(List<E> list) {
        return IntStream.range(0, list.size())
                .mapToObj(start -> list.subList(start, list.size()));
    }
}
```


코드 47-7  입력 리스트의 모든 부분리스트를 스트림으로 반환한다. 
``` java
public static <E> Stream<List<E>> of(List<E> list) {
    return IntStream.range(0, list.size())
        .mapToObj(start ->
            IntStream.rangeClosed(start + 1, list.size())
            .mapToObj(end -> list.subList(start, end)))
        .flatMap(x -> x);
}
```


### 핵심 정리 
- 원소 시퀀스를 반환하는 메서드를 작성할 때는, 이를 스트림으로 처리하기를 원하는 사용자와 반복으로 처리하길 원하는 사용자가 모두 있을 수 있음을 떠올리고 양쪽을 다 만족시키려 노력하자. 
- 컬렉션을 반환할 수 있다면 그렇게 하라. 
- 반환 전부터 이미 원소들을 컬렉션에 담아 관리하고 있거나 컬렉션을 하나 더 만들어도 될 정도로 원소 개수가 적다면 ArrayList 같은 표준 컬렉션에 담아 반환하라.
- 그렇지 않으면 앞서의 멱집합 예처럼 전용 컬렉션을 구현할지 고민하라. 
- 컬렉션을 반환하는 게 불가능하면 스트림과 Iterable 중 더 자연스러운 것을 반환하라. 
- 만약 나중에 Stream 인터페이스가 Iterable을 지원하도록 자바가 수정된다면, 그때는 안심하고 스트림을 반환하면 될 것이다(스트림 처리와 반복 모두에 사용할 수 있으니)