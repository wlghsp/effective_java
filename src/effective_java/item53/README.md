# Item 53 가변인수는 신중히 사용하라

--------------------------------------------

가변인수(varargs) 메서는 명시한 타입의 인수를 0개 이상 받을 수 있다. 

가변인수 메서드를 호출하면, 가장 먼저 인수의 개수와 길이가 같은 배열을 만들고 인수들을 이 배열에 저장하여 가변인수 메서드에 건네준다. 

다음은 입력받은 int 인수들의 합을 계산해주는 가변인수 메서드다. sum(1,2,3)은 6을, sum()은 0을 돌려준다. 


코드 53-1 간단한 가변인수 활용 예 
``` java
static int sum(int... args) {
    int sum = 0;
    for (int arg : args)
        sum += arg;
    return sum;
}
```

코드 53-2 인수가 1개 이상이어야 하는 가변인수 메서드 - 잘못 구현한 예!
``` java
static int min(int... args) {
    if (args.length == 0)
        throw new IllegalArgumentException("인수가 1개 이상 필요합니다.");
    int min = args[0];
    for (int i = 1; i < args.length; i++)
        if(args[i] < min)
            min = args[i];
    return min;
}
```
이 방식에는 문제가 몇 개 있는데, 가장 심각한 문제는 인수를 0개만 넣어 호출하면 (컴파일 타입이 아닌) 런타입에 실패한다는 점이다. 

코드도 지저분하다. args 유효성 검사를 명시적으로 해야 하고, min의 초기값을 Integer.MAX_VALUE로 설정하지 않고는 (더 명료한)for-each문도 사용할 수 없다. 

훨씬 나은 방법이 있다. 

다음 코드 처럼 매개변수를 2개 받도록 하면 된다. 즉 첫 번째로는 평범한 매개변수를 받고, 가변인수는 두 번째로 받으면 앞서의 문제가 사라진다.  
``` java
static int min( int firstArg, int... remainingArgs) {
    int min = firstArg;
    for (int agr: remainingArgs)
        if(arg < min)
            min = arg;
    return min;
}
```
가변인수는 인수 갯수가 정해지지 않았을 때 아주 유용하다. 

<hr>

성능에 민감한 상황이라면 가변인수가 걸림돌이 될 수 있다. 

가변인수 메서드는 호출될 때마다 배열을 새로 하나 할당하고 초기화한다. 다행히, 이 비용을 감당할 수는 없지만 가변인수의 유연성이 필요할 때 선택할 수 있느 멋진 패턴이 있다. 



### 핵심 정리
- 인수 개수가 일정하지 않은 메서드를 정의해야 한다면 가변인수가 반드시 필요하다. 
- 메서드를 정의할 때 필수 매개변수는 가변인수 앞에 두고, 가변인수를 사용할 때는 성능 문제까지 고려하자. 