# Item 52 다중정의는 신중히 사용하라 

--------------------------------------------

코드 52-1 컬렉션 분류기 - 오류! 이 프로그램은 무엇을 출력할까?

``` java
public class CollectionClassifier {
    public static String classify(Set<?> s) {
        return "집합";
    }
    
    public static String classify(List<?> lst) {
        return "리스트";
    }

    public static String classify(Collection<?> c) {
        return "그 외";
    }

    public static void main(String[] args) {
        Collection<?>[] collections = {
                new HashSet<String>(),
                new ArrayList<BigInteger>(),
                new HashMap<String, String>().values()
        };

        for (Collection<?> c : collections) {
            System.out.println(classify(c));
        }
    }
}

```
"집합", "리스트", "그 외"를 차례로 출력할 것 같지만, 실제로 수행해보면 "그 외"만 세 번 연달아 출력한다. 

이유는? 다중정의된 세 classify 중 **어느 메서드를 호출할지가 컴파일타임에 정해지기 때문이다**

컴파일 타임에는 for문 안의 c는 항상 Collection<?> 타입이다. 런타임에는 매번 달라지지만, 호출할 메서드를 선택하는 데는 영향을 주지 못한다. 

따라서 컴파일타임의 매개변수 타입을 기준으로 항상 세 번째 메서드인 classify(Collection<?>)만 호출하는 것이다. 

<hr>
이처럼 직관과 벗어나는 이유는?

→ 재정의한 메서드는 동적으로 선택되고, 다중정의한 메서드는 정적으로 선택되기 때문이다. 


<hr>
메서드를 재정의한 다음 '하위 클래스의 인스턴스'에서 그 메서드를 호출하면 재정의한 메서드가 실행된다. 

컴파일 타임에 그 인스턴스의 타입이 무엇이었냐는 상관없다. 

코드 52-2 재정의된 메서드 호출 매커니즘 - 이 프로그램은 무엇을 출력할까?
``` java
class Wine {
    String name() { return "포도주"; }
}

class SparklingWine extends Wine {
    @Override
    String name() {
        return "발포성 포도주";
    }
}

class Champagne extends SparklingWine {
    @Override
    String name() {
        return "샴페인";
    }
}


public class Overriding {
    public static void main(String[] args) {
        List<Wine> wineList = List.of(
                new Wine(), new SparklingWine(), new Champagne());

        for (Wine wine : wineList) {
            System.out.println(wine.name());
        }
    }
}
```
Wine 클래스에 정의된 name메서드는 하위 클래스인 SparklingWine과 Champagne에서 재정의된다. 

예상한 것처럼 이 프로그램은 "포도주", "발포성 포도주", "샴페인"을 차례로 출력한다. 

> for문에서의 컴파일 타임 타입이 모두 Wine인 것에 무관하게 항상 '가장 하위에서 재정의한' 재정의 메서드가 실행되는 것이다. 

<hr>

한편, 다중정의된 메서드 사이에서는 객체의 런타임 타입은 전혀 중요치 않다. 

선택은 컴파일타임에, 오직 매개변수의 컴파일타임 타입에 의해 이뤄진다. 


코드 52-1의 문제는 CollectionClassifier의 모든 classify 메서드를 하나로 합친 후 

instanceOf로 명시적으로 검사하면 말끔히 해결된다. 

``` java
public static String classify(Collection<?> c) {
    return c instanceOf Set ? "집합" : 
           c instanceOf List? "리스트": "그 외"; 
```
프로그래머에게는 재정의가 정상적인 동작 방식이고, 다중 정의가 예외적인 동작으로 보일 것이다. 

즉 재정의한 메서드는 프로그래머가 기대한 대로 동작하지만, CollectionClassifier 예에서처럼 다중정의는 이러한 기대를 가볍게 무시한다. 

헷갈릴 수 있는 코드는 작성하지 않는 게 좋다. 특히나 공개 API라면 더욱 신경 써야 한다. 

API 사용자가 매개변수를 넘기면서 어떤 다중정의 메서드가 호출될지를 모른다면 프로그램이 오동작하기 쉽다. 

런타임에 이상하게 행동할 것이며 API 사용자들은 문제를 진단하느라 긴 시간을 허비할 것이다. 

> 그러니 다중정의가 혼동을 일으키는 상황을 피해야 한다. 

<hr>
* 안전하고 보수적으로 가려면 매개변수 수가 같은 다중정의는 만들지 말자. 
가변인수(varargs) 를 사용하는 메서드라면 다중정의를 아예 하지 말아야 한다. 

> 다중정의 하는 대신 메서드 이름을 다르게 지어주는 길도 항상 열려 있으니 말이다. 

<hr>

ObjectOutputStream 클래스를 살펴보자 

이 클래스의 write 메서드는 모든 기본 타입과 일부 참조 타입용 변형을 가지고 있다. 

그런데 다중정의가 아닌, 모든 메서드에 다른 이름을 지어주는 길을 택했다. 

writeBoolean(boolean), writeInt(int), writeLong(long)같은 식이다. 

이 방식이 다중정의보다 나은 또 다른 점은 read 메서드의 이름과 짝을 맞추기 좋다는 것이다. 

예) readBoolean(), readInt(), readLong() 같은 식이다. 
<hr>





코드 52-3 인수를 포워드하여 두 메서드가 동일한 일을 하도록 보장한다. 
``` java
public boolean contentEquals(StringBuffer sb) {
    return contentEquals((CharSequence) sb);
}
```

자바 라이브러리는 이번 아이템의 정신을 지켜내려 애쓰고 있지만, 실패한 클래스도 몇 개 있다. 

예) String 클래스의 valueOf(char[])과 valueOf(Object)는 같은 객체를 건네더라도 전혀 다른 일을 수행한다. 

이렇게 해야할 이유가 없었음에도, 혼란을 불러올 수 있는 잘못된 사례로 남게 되었다. 




### 핵심 정리
- 프로그래밍 언어가 다중정의를 허용한다고 해서 다중정의를 꼭 활용하란 뜻은 아니다. 
- 일반적으로 매개변수 수가 같을 때는 다중정의를 피하는 게 좋다.
- 특히 생성자라면 이 조언을 따르기가 불가능할 수 있다. 
- 그럴 때는 헷갈릴만한 매개변수는 형변환하여 정확한 다중정의 메서드가 선택되도록 해야 한다. 
- 이것이 불가능하면, 예컨대 기존 클래스를 수정해 새로운 인터페이스를 구현해야 할 때는 같은 객체를 입력받는 다중정의 메서드들이 모두 동일하게 동작하도록 만들어야 한다.
- 그렇지 못하면 프로그래머들은 다중정의된 메서드나 생성자를 효과적으로 사용하지 못할 것이고, 의도대로 동작하지 않는 이유를 이해하지도 못할 것이다. 

