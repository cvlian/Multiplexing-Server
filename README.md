# Multiplexing Server

A real-time chatting application built on multiplexing client-server model

## Usage

### prerequisites

* JDK version 8

### Execute program

    $ git clone https://github.com/cvlian/Multiplexing-Server
    $ cd Multiplexing-Server/bin
 
    // Server 구동
    $ java Server
    
    // 터미널을 독립적으로 여러 개 띄워서 Client를 실행
    $ java Client

* 'Enter your ID' 메시지가 뜨면 원하는 ID 입력
* 메시지를 보낼 떄는 to [user ID]: [message] 형식을 취해야함
* [user ID] 자리에 복수의 ID를 ','로 분리하여 메시지 전달이 가능하며, 'all' 키워드를 넣어 전체에게 메시지를 보낼 수도 있음.
</br>
</br>

  ![Test](https://user-images.githubusercontent.com/65039504/131438078-7664ba6f-308b-4a09-a058-8aea8a2ebe27.gif)


