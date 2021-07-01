# 1) FSMManager
FSMManager
  
  
## 2) FSM 종류
### 1. Basic FSM : 직접 만든 FSM
  
### 2. Squirrel FSM : Squirrel Framework 를 사용한 FSM
![스크린샷 2021-07-01 오전 9 18 13](https://user-images.githubusercontent.com/37236920/124047007-2d76d980-da4e-11eb-870f-12da88ae538b.png)
  
### 3. Akka FSM : Akka Actor 를 사용한 FSM
![스크린샷 2021-07-01 오전 9 18 00](https://user-images.githubusercontent.com/37236920/124046993-25b73500-da4e-11eb-9f99-d01c0a13baf3.png)
![스크린샷 2021-07-01 오전 9 17 16](https://user-images.githubusercontent.com/37236920/124046939-0b7d5700-da4e-11eb-9a69-abf6f7c8f0fc.png)
![스크린샷 2021-07-01 오전 9 17 31](https://user-images.githubusercontent.com/37236920/124046951-146e2880-da4e-11eb-9f64-85273d17ea7f.png)
  
  
## 3) FSM 구성도
### 1. Basic FSM
#### 1] 클래스 구조
![스크린샷 2021-07-01 오전 9 04 30](https://user-images.githubusercontent.com/37236920/124046160-441c3100-da4c-11eb-960e-c858d3854452.png)
  
#### 2] 호출 예시
![스크린샷 2021-07-01 오전 9 04 50](https://user-images.githubusercontent.com/37236920/124046180-4f6f5c80-da4c-11eb-85be-9f04b321e455.png)
![스크린샷 2021-07-01 오후 3 22 41](https://user-images.githubusercontent.com/37236920/124076345-169daa80-da81-11eb-95a8-e2653eefa8fb.png)
![스크린샷 2021-07-01 오후 2 29 42](https://user-images.githubusercontent.com/37236920/124070998-afc8c300-da79-11eb-8acd-491ccd6e09b3.png)
  
#### 3] 상태 추가
![스크린샷 2021-07-01 오전 9 06 42](https://user-images.githubusercontent.com/37236920/124046287-91989e00-da4c-11eb-9b44-727f36af40c2.png)
  
#### 4] 상태 삭제
![스크린샷 2021-07-01 오전 9 07 02](https://user-images.githubusercontent.com/37236920/124046306-9d846000-da4c-11eb-8768-87ffe34c8adb.png)
  
#### 5] 상태 천이
![스크린샷 2021-07-01 오전 9 08 59](https://user-images.githubusercontent.com/37236920/124046410-e2a89200-da4c-11eb-9c6c-b46fd35419a0.png)
  
  
### 2. Squirrel 
#### 1] 상태 천이
![스크린샷 2021-06-25 오후 4 16 02](https://user-images.githubusercontent.com/37236920/123386348-87e8e380-d5d1-11eb-827f-47df382f319e.png)
  
  
## 4) API
### 1. Basic FSM
  
### 2. Squirrel FSM
#### 1] addFsmContainer (String name, AbstractFsm abstractFsm, AbstractState abstractState, AbstractEvent abstractEvent)
@brief : 새로운 FSM 을 추가하는 함수 (FSM Container 클래스에 UntypedStateMachineBuilder 와 UntypedStateMachine 를 관리)  
@param name : FSM 이름  
@param abstractFsm : FSM 로직 클래스  
@param abstractState : FSM 상태 정의  
@param abstractEvent : FSM 이벤트 정의  
  
#### 2] removeFsmContainer (String name)  
@brief : 지정한 이름의 FSM 을 삭제하는 함수  
@param name : FSM 이름  
  
#### 3] getFsmContainer (String name)  
@brief : 지정한 이름의 FSM Container 클래스의 객체를 반환하는 함수  
@param name : FSM 이름  
  
#### 4] setFsmCondition (String name, String from, Strain to, String event)  
@brief : 지정한 이름의 FSM 에 새로운 상태 천이 조건을 추가하는 함수  
@param name : FSM 이름  
@param from : 천이 전 상태 이름  
@param to : 천이 후 상태 이름  
@param event : 상태 천이를 트리거할 이벤트 이름  
  
#### 5] setFsmOnEntry (String name, String state, String funcName)  
@brief : 지정한 상태 시작 시 실행할 FSM 의 함수를 지정하는 함수  
@param name : FSM 이름  
@param state : 상태 이름  
@param funcName : FSM 함수 이름  
  
#### 6] setFsmOnExit (String name, String state, String funcName)  
@brief : 지정한 상태 종료 시 실행할 FSM 의 함수를 지정하는 함수  
@param name : FSM 이름  
@param state : 상태 이름  
@param funcName : FSM 함수 이름  
  
#### 7] getFsmCurState (String name)  
@brief : 지정한 이름의 FSM 의 현재 상태를 반환하는 함수  
@param name : FSM 이름  
  
#### 8] getFsmLastState (String name)  
@brief : 지정한 이름의 FSM 의 바로 이전 상태를 반환하는 함수  
@param name : FSM 이름  
  
#### 9] setFsmFinalState (String name, String state)  
@brief : 지정한 이름의 FSM 의 마지막 상태를 지정하는 함수  
@param name : FSM 이름  
@param state : 마지막 상태 이름  
  
#### 10] buildFsm (String name, String initState, boolean isDebugMode)  
@brief : 지정한 이름의 FSM 을 초기화하여 새로 생성하는 함수  
@param name : FSM 이름  
@param initState : FSM 초기 상태 이름  
@param isDebugMode : FSM 내부 로그 출력 여부  
  
#### 11] fireFsm (String name, String event, FutureCallBack<Object> callback)  
@brief : 지정한 이름의 FSM 에 이벤트를 발생시키는 함수  
@param name : FSM 이름  
@param event : 발생할 이벤트 이름  
@param callback : 이벤트 발생 시 FSM 로직에서 실행할 callback 함수  
  
### 3. Akka FSM
  
