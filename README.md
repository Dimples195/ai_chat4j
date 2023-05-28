# ai_chat4j
  (Learning Demo)实现基于ChatGPT的QQbot
# 简介：
  这是一个简单的ChatGPT API调用的学习案例。

  qq实现基于[mamoe/mirai](https://github.com/mamoe/mirai.git)，本案例中提供了扫码登陆和密码登录
（推荐使用扫码登录，但是我现在还不知道怎么切换QR存储路径🤣）；

  **在国内你需要使用魔法的方式来实现连接GPT**；

  默认使用GPT-model：gpt-3.5-turbo；

  提供GPT多种API参数，使用参考：https://platform.openai.com/docs/api-reference；

  Open_ai_key获取地址：https://platform.openai.com/account/api-keys；
  
## 使用方式
  1.clone本项目
  
  2.拥有一个chatGPT的账号，获取key
  
  3.将参数配置在application.yml中
  ```yml
  server:
  port: 18081
# chatgpt 相关配置
chatgpt:
  proxy:
    host: "127.0.0.1"
    port: "10810"
  open_ai_key: ""

qq:
  enable: true
  account: 123
  password: "123"
  loginMethod: "1" #  登录方式 1为扫码 2为密码
  acceptNewFriend: true
  acceptNewGroup: false
  ```
  
## 工作方式
  1.实现单次的问与答；
  
  2.实现上下文对话；
  
  3.当前实现默认回答方式：群聊@类型消息使用单次问答，好友类消息使用上下文；
## 其他
  在使用过程中有任何问题欢迎您的指正💕💕💕。
  
