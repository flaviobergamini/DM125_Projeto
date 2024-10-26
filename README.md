# DM125_Projeto


<h1 align="center">Exercício de DM122 - Projeto de um aplicativo Android para lista de tarefas</h1>

![check-list](https://github.com/user-attachments/assets/ccb1afc2-8be8-4c46-9954-6c83d8ef9576)

### :books: Descrição
<p>Projeto de um aplicativo Android desenvolvido em plataforma nativa usando a linguagem Kotlin que consiste em uma lista de tarefa, com notificações push e login por e-mail comum e serviços do Google pelo Gmail. Todos utlizando serviços do Google. O backend associado ao armazenamento e gerência de estado das tarefas foi feito em Spring Boot e é chamado em classes de repository com o Retrofit</p>

### :computer: Projeto
- Esse aplicativo Android foi desenvolvido com Kotlin, recursos de autenticação e mensageria do Google Firebase e persistências de dados em API externa feita com Spring Boot.

### :hammer_and_wrench: Recursos
- [IDE Android Studio](https://developer.android.com/studio?gad_source=1&gclid=CjwKCAjwg-24BhB_EiwA1ZOx8kSrKV_Q2HVqsw8Blc3KeTDEcVf-qY-aaPj_eOie7cQN1zJ7i7r9YBoCGV8QAvD_BwE&gclsrc=aw.ds&hl=pt-br)
- [Java Development Kit 17 - (JDK)](https://www.oracle.com/java/technologies/downloads/#java17)
- [Firebase](https://firebase.google.com/?hl=pt)

### :hammer_and_wrench: Instalação e Execução

Clone o repositório em seu computador para poder acessar o projeto:
```
git clone https://github.com/flaviobergamini/DM125_Projeto.git
```
Depois de clonar o repositório, gere no Firebase o arquivo google-services.json com os recursos Messaging e Authentication (Email/Senha e Google) criados. Adicione na pasta app do projeto, abra o projeto na IDE do Android Studio, aguerde o processo de build e pode gerar o app no emulador ou dispositivo Android configurado. O serviço da API precisa ser executado num computador devidadmente preparado e na mesma rede LAN, adicionando o endereço IP local do computador na classe com o objeto do Retrofit para as requisições HTTP.

#### Vídeo da execução:

https://github.com/user-attachments/assets/e6fc94ee-4e53-47fc-bab4-0cf6252ce875

## :question: Dúvidas
Envie um email ao desenvolvedor: flavio.bergamini@pg.inatel.br

## :gear: Autor

* **Flávio Henrique Madureira Bergamini** - [Flávio](https://github.com/flaviobergamini)
