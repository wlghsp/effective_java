# Item 45 스트림은 주의해서 사용하라

--------------------------------------------

스트림 API는 다량의 데이터 처리 작업(순차적이든 병렬적이든)을 돕고자 자바 8에 추가되었다. 

이 API가 제공하는 추상 개념 중 핵심은 두 가지다. 

그 첫 번째인 스트림(stream)은 데이터 원소의 유한 혹은 무한 시퀀스(sequence)를 뜻한다.

두 번째인 스트림 파이프라인(stream pipepline)은 이 원소들로 수행하는 연산 단계를 표현하는 개념이다. 

스트림의 원소들은 어디로부터든 올 수 있다. 

대표적으로는 컬렉션, 배열, 파일, 정규표현식 패턴 매처(matcher), 난수 생성기, 혹은 다른 스트림이 있다. 

스트림 안의 데이터 원소들은 객체 참조나 기본 타입 값이다. 기본 타입 값으로는 int, long, double 이렇게 세 가지를 지원한다. 

<hr>

스트림 파이프라인은 소스 스트림에서 시작해 종단 연산(terminal operation)으로 끝나면, 그 사이에 하나 이상의 중간 연산(intermediate operation)이 있을 수 있다. 

각 중간 연산은 스트림을 어떠한 방식으로 변환(transform)한다. 

예컨대 각 원소에 함수를 적용하거나 특정 조건을 만족 못하는 원소를 걸러낼 수 있다. 

중간 연산들을 모두 한 스트림을 다른 스트림으로 변환하는데, 변환된 스트림의 원소 타입은 변환 전 스트림의 원소 타입과 같을 수도 있고 다를 수도 있다. 

종단 연산은 마지막 중간 연산이 내놓은 스트림에 최후의 연산을 가한다. 

원소를 정렬해 컬렉션에 담거나, 특정 원소 하나를 선택하거나, 모든 원소를 출력하는 식이다. 

<hr>

스트림 파이프라인은 지연 평가(lazy evaluation)된다. 

평가는 종단 연산이 호출될 때 이뤄지며, 종단 연산에 쓰이지 않는 데이터 원소는 계산에 쓰이지 않는다. 

이러한 지연 평가가 무한 스트림을 다룰 수 있게 해주는 열쇠다. 

종단 연산이 없는 스트림 파이프라인은 아무 일도 하지 않는 명령어인 no-op과 같으니, 종단 연산을 빼먹는 일이 절대 없도록 하자. 

<hr>

스트림 API는 메서드 연쇄를 지원하는 플루언트 API(fluent API)다. 

즉, 파이프라인 하나를 구성하는 모든 호출을 연결하여 단 하나의 표현식으로 완성할 수 있다. 

파이프라인 여러 개를 연결해 표현식 하나로 만들 수도 있다. 

<hr>

기본적으로 스트림 파이프라인은 순차적으로 수행된다. 

파이프라인을 병렬로 실행하려면 파이프라인을 구성하는 스트림 중 하나에서 parallel 메서드를 호출해주기만 하면 되나, 효과를 볼 수 있는 상황은 많지 않다. 

<hr>

스트림 API는 다재다능하여 사실상 어떠한 계산이라도 해낼 수 있다. 하지만 할 수 있다는 뜻이지, 해야 한다는 뜻은 아니다. 

스트림을 제대로 사용하면 프로그램이 짧고 깔끔해지지만, 잘못 사용하면 읽기 어렵고 유지보수도 힘들어진다. 

스트림을 언제 써야 하는지를 규정한는 확고부동한 규칙은 없지만, 참고할 만한 노하우는 있다. 

<hr>

## 스트림 쓰는 노하우 시작!


위 코드는 사전 파일에서 단어를 읽어 사용자가 지정한 문턱값보다 우너소 수가 많은 아나그램 그룹(anagram)을 출력한다. 

* 아나그램: 철자를 구성하는 알파벳이 같고 순서만 다른 단어 

이 프로그램은 사용자가 명시한 사전 파일에서 각 단어를 읽어 맵에 저장한다. 

맵의 키는 그 단어를 구성하는 철자들을 알파벳순으로 정렬한 값이다. 

즉 "staple"의 키는 "aelpst"가 되고, "petals"의 키도 "aelpst"가 된다. 

따라서 두 단어 아나그램이고, 아나그램끼리는 같은 키를 공유한다. 

맵의 값은 같은 키를 공유한 단어들을 담은 집합이다. 

코드 45-1 사전 하나를 훑어 원소 수가 많은 아나그램 그룹들을 출력한다.
```` java
public clas Anagrams {
    public static void main(String[] args) throws IOException {
        File dictionary = new File(args[0]);
        int minGroupSize = Integer.parseInt(args[1]);
        
        Map<String, Set<String>> groups = new HashMap<>();
        try(Scanner s = new Scanner(dictionary)) {
            while (s.hasNext()) {
                String word = s.next();
                groups.computeIfAbsent(alphabetize(word),
                (unused) -> new TreeSet<>()).add(word);
            }
        }
        
        for (Set<String> group : groups.values())
            if(group.size() >= minGroupSize)
                System.out.println(group.size() + ": " + group);
    }
    
    private static String alphbetize(String s) {
        char[] a = s.toCharArray();
        Arrays.sort(a);
        return new String(a);
    }
} 
````

``` java
  groups.computeIfAbsent(alphabetize(word),
                (unused) -> new TreeSet<>()).add(word);
```

이 부분에 주목하자. 맵에 각 단어를 삽입할 때 자바 8에서 추가된 computeIfAbsent 메서드를 사용했다. 

이 메서드는 맵 안에 키가 있는지 찾은 다음, 있으면 단순히 그 키에 매핑된 값을 반환한다. 

키가 없으면 건네진 함수 객체를 키에 적용하여 값을 계산해낸 다음 그 키와 값을 매핑해놓고, 계산된 값을 반환한다.

이처럼 computeIfAbset를 사용하면 각 키에 다수의 값을 매핑하는 맵을 쉽게 구현할 수 있다.
<hr>

### 잠깐 computeIfAbsent
``` java
Map<Key, Value> map = new HashMap();
Value value = map.get(key);
if (value == null) {
    value = getNewValue(key);
    map.put(key, value);
}
```
Map을 사용할 때 빈번하게 사용하는 코드패턴이다. 

자바 8에서 computeIfAbsent를 사용한다면 ?
``` java
Map<Key, Value> map = new HashMap();

Value value = map.computeIfAbsent(key, k -> getNewValue(key));
```
이렇게 4줄짜리 코드를 한 줄로 구현 가능하다. 

<hr>


코드 45-2 스트림을 과하게 사용했다. - 따라 하지 말 것!
``` java
public clas Anagrams {
    public static void main(String[] args) throws IOException {
        Path dictionary = Paths.get(args[0]);
        int minGroupSize = Integer.parseInt(args[1]);
        
        try(Stream<String> words = Files.lines(dictionary)) {
            words.collect(
                groupingBy(word -> word.chars().sorted()
                            .collect(StringBuilder::new,
                                (sb, c) -> sb.append((char) c),
                                StringBuilder::append).toString()))
                .values().stream()
                .filter(group -> group.size() >= minGroupSize)
                .map(group -> group.size() + ": " + group)
                .forEach(System.out::println);
        }
    } 
} 
```
코드는 짧지만 읽니느 어렵다. 

* 스트림을 과용하면 프로그램이 읽거나 유지보수하기 어려워진다. 

<hr>

코드 45-3 스트림을 적절히 활용하면 깔끔하고 명료해진다. 
``` java
public class Anagrams {
    public static void main(String[] args) throws IOException {
        Path dictionaryt = Paths.get(args[0);
        int minGroupSize = Integer.parseInt(args[1]);
        
        try(Stream<String> words = Files.lines(dictionary)) {
            words.collect(groupingBy(word -> alphabetize(word))
                .values().stream()
                .filter(group -> group.size() >= minGroupSize)
                .forEach(g -> System.out.println(g.size() + ": " + g));
        }
    }
     private static String alphbetize(String s) {
        char[] a = s.toCharArray();
        Arrays.sort(a);
        return new String(a);
    }
}
```

스트림을 전에 본 적 없더라도 이 코드는 이해하기 쉬울 것이다. 

try-with-resources 블록에서 사전 파일을 열고, 파일의 모든 라인으로 구성된 스트림을 얻는다. 

스트림 변수의 이름을 words로 지워 스트림 안의 각 원소가 단어(word)임을 명확히 했다. 


> 람다에서는 타입 이름을 자주 생략하므로 매개변수 이름을 잘 지어야 스트림 파이프라인의 가독성이 유지된다. 

> 단어의 절차를 알파벳순으로 정렬하는 일은 별도 메서드인 alphabetize에서 수행했다. 
> 연산에 적절한 이름을 지어주고 세부 구현을 주 프로그램 로직 밖으로 빼내 전체적인 가독성을 높인 것이다. 
> **도우미 메서드를 적절히 활용하는 일의 중요성은 일반 반복 코드에서보다는 스트림 파이프라인에서 훨씬 크다.** 파이프라인에서는 타입정보가 명시되지 않거나 임시 변수를 자주 사용하기 때문이다. 


alphabetize 메서드도 스트림을 사용해 다르게 구현할 수 있으나, 그렇게 하면 명확성이 떨어지고 잘못 구현할 가능성이 커진다. 심지어 느려질 수도 있다. 

자바가 기본 타입인 char용 스트림을 지원하지 않기 때문이다.
(그렇다고 자바가 char스트림을 지원했어야 한다는 뜻은 아니다. 그렇게 하는 건 불가능했다)

``` java
"Hello world!".chars().forEach(System.out::println);
```
위 결과값은 긴 정수가 출력된다. chars()가 반환하는 스트림의 원소는 int 값이다. 

올바르게 출력하려면 형변환을 명시적으로 해줘야 한다. 

``` java
"Hello world!".chars().forEach(x -> System.out.print((char) x);
```
> 하지만 char 값등르 처리할 때는 스트림을 삼가는 편이 낫다. 


#### 스트림은 가독성과 유지보수 측면에서 손해다!

#### 기존 코드는 스트림을 사용하도록 리팩터링하되, 새 코드가 더 나아 보일 때만 반영하자. 

### 함수객체로는 할 수 없지만 코드 블록으로 할 수 있는 일들
* 코드 블록에서는 범위 안의 지역변수를 읽고 수정할 수 있다. 하지만 람다에서는 final이거나 사실상 final인 변수만 읽을 수 있고, 지역변수를 수정하는 건 불가능하다. 
* 코드 블록에서는 return문을 사용해 메서드에서 빠져나가거나, break나 continue문으로 블록 바깥의 반복문을 종료하거나 반복을 한 번 건너뛸 수 있다. 또한 메서드 선언에 명시된 검사 예외를 던질 수 있다. 하자만 람다로는 이 중 어떤 것도 할 수 없다. 


### 반대로 스트림에 안성맞춤인 일들 

* 원소들의 시퀀스를 일관되게 변환한다. 
* 원소들의 시퀀스를 필터링한다.
* 원소들의 시퀀스를 하나의 연산을 사용해 결합한다(더하기, 연결하기, 최솟값 구하기 등)
* 원소들의 시퀀스를 컬렉션에 모은다(아마도 공통된 속성을 기준으로 묶어 가며)
* 원소들의 시퀀스에서 특정 조건을 만족하는 원소를 찾는다.

### 스트림으로 처리하기 어려운 일

한 데이터가 파이프라인의 여러 단계(stage)를 통과할 때 이 데이터의 각 단계에서의 값들에 동시에 접근하기는 어려운 경우다.

스트림 파이프라인은 일단 한 값을 다른 값에 매핑하고 나면 원래의 값은 잃는 구조이기 때문이다. 

코드 45-4 데카르트 곱 계산을 반복 방식으로 구현
``` java
private static List<Card> newDeck() {
    List<Card> result = new ArrayList<>();
    for (Suit suit : Suit.values())
        for(Rank rank : Rank.values())
            result.add(new Card(suit, rnak))
    return result;
}
```
코드 45-5 데카르트 곱 계산을 스트림 방식으로 구현
``` java
private static List<Card> newDeck() {
    return Stream.of(Suit.values())
    .flatMap(suit -> Stream.of(Rank.values())
    .map(rank -> new Card(suit, rank)))
    .collect(toList());
}
```

둘 중 선택은 자유. 알아서 상황에 맞게 사용하세요. 



### 핵심 정리 
- 스트림을 사용해야 멋지게 처리할 수 있는 일이 있고, 반복 방식이 더 알맞은 일도 있다. 
- 수많은 작업이 이 둘을 조합했을 때 가장 멋지게 해결된다. 
- 스트림과 본복 중 어느 쪽이 나은지 확신하기 어렵다면 둘 다 해보고 더 나은 쪽을 택하라. 