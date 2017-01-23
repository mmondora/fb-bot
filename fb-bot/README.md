# fb-bot

Read.me
=======

Progetto Spring per la creazione di un bot facebook per ricevere messaggi dal b2b


Configurazione AuthUpdater
--------------

```
#!sh
export BUS_USER="read”
export BUS_PASS="“
export SUBSCRIPTION_NAME=“b2b_bot"

export servicebustopic.policyname="write";
export servicebustopic.policykey=""
export servicebustopic.hostname="bus-b2bhub-dev.servicebus.windows.net"
export servicebustopic.topicname="b2b_notification"
export servicebustopic.subscription_name=“b2b_bot"
```

Compilazione
------------

```sh
mvn clean package
```

Avvio
-----

```sh
mvn clean spring-boot:run
```

Notes
-----
Per TeamSystem spa, Pesaro

2017 [mondora.com](https://mondora.com)

[michele.mondora](https://mondora.com/#!/user/2QY3oqMRqjE2tv6Z7)@mondora.com

[roberto.giana](https://mondora.com/#!/user/xmMTW3t5A2BZP8Njy)@mondora.com

[andrea.tibi](https://mondora.com/#!/user/iedMh7Xd5LyNi8jrf)@mondora.com