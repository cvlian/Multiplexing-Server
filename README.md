# 0. 과제 내용

- **Multiplexing 채팅** 프로그램 구현
- Server에서는 Multiplexing 구현
- Client에서는 메시지를 접속한 전체 Client에 전달하거나, 특정 Client에 전달하는 기능 포함

* Client UI 는 중요하지 않습니다. Console로 구현하셔도 됩니다.
* Server 쪽에서 Multiplexing 구현이 핵심
<br/>

# 1. 개념 정리

## 1.1 Multiplexing

Server의 I/O 처리 방식에는 **Multi process/Multiplexing** 방식이 존재한다.
<br/>

**Multi process**는

- Client마다 하나의 프로세스를 생성하여 개별적으로 입출력을 수행하는 방식
- Client 수에 비례하여 프로세스 수가 늘어나며 이들을  처리하기 위한 메모리 공간이 많이 요구됨

![Untitled](https://s3-us-west-2.amazonaws.com/secure.notion-static.com/4568dc87-50cd-408f-87f0-2f8303ac050b/Untitled.png)


**Multiplexing**은

- 하나의 채널을 통해서 둘 이상의 Client의 I/O를 처리하는 방식
- 기존의 자식 프로세스를 생성하여 서비스하는 **Multi process** **Server**와는 달리, Server 하나가 각각의 Socket에 대한 서비스를 제공하는 구조
- 많은 수의 Client를 처리하는 데 매우 적합한 방식

![Untitled](https://s3-us-west-2.amazonaws.com/secure.notion-static.com/256b9a21-f28d-4e3d-99df-12e9bbf72699/Untitled.png)

관련 자료: [https://dbehdrhs.tistory.com/98](https://dbehdrhs.tistory.com/98)
<br/>
<br/>

## 1.2 Java NIO (Non-blocking Input/Output)

일반적인 Java I/O 시스템은 **Multi Threads** 방식을 취한다. 

Socket을 accept()하고, 데이터를 read()할 때 해당 Thread가 Block 된다.

클라이언트가 늘어날수록 Thread의 수는 선형적으로 늘어나고, 이는 동시에 stack에 할당해야 하는 메모리의 양을 증폭 시킨다.

그에 반해 Java NIO는 Multiplexing 구조를 활용한다.

Client의 연결을 기다리거나 채널로부터 데이터를 읽어올 때 Server는 대기 상태로 전환하지 않는다.

그 대신 아래와 같은 메커니즘을 따라 동작하게 된다.

- Client의 연결 요청이 없을 경우 ServerSocketChannel.accept() method는 곧바로 null을 return
- 채널로부터 읽어올 데이터가 없는 경우 SocketChannel.read() method는 곧바로 return 되고, 인자로 전달한 ByteBuffer에는 어떤 내용도 입력되지 않음

참고 자료: [https://joochang.tistory.com/78](https://joochang.tistory.com/78) 
<br/>
<br/>

### 1.2.1 Selector
<br/>
<br/>

### 1.2.2 SocketChannel
<br/>
<br/>

### 1.2.3 ByteBuffer
<br/>
<br/>

# 2. 구현
