# Broker_Benchmarks
Benchmark for several message brokers

For these Benchmarks are developed with Java so a working JDK should be installed on your machine (I used JDK 1.8 so I don't know whether there are problems with lower versions.).

#Supported Brokers and its messaging protocols
- RabbitMQ AMQP (is using AMQP per default)
- ActiveMQ Openwire and AMQP (is using Openwire per default)
- Qpid AMQP (is using AMQP per default)
- OpenMQ UMS (is using UMS per default)

For Qpid and ActiveMQ AMQP benchmarks the JMS AMQP library is needed. JMS is no messaging protocol but an standardised abstraction layer.
It's purpose is to ease and unify the usage of different messaging libraries.

#Compoents Clients: 
- Sender and Receiver for every benchmark to abstract the server connection from the real benchmark measurements.

#Components Processors
- message configurator to configure the size and amount of generated messages.
- message generator to generate all messages with the configured properties
- message reader to analyse all received messages

#Benchmark all Brokers without serialization
1. follow step 1 of every benchmark explanation below.
2. clone all benchmark projects to your local machine
3. import all projects to your IDE (e.g. I used eclipse)
4. if you just want to benchmark all brokers add the "Message_Processor" to every client Projects build path.
5- Look at the configuration Part on this projects wiki page to configure every broker to get comparable measurements.
6. Now you can start the first broker, run its related receiver.java and then run the sender.java in the same client project. 
7. Repeat this procedure for every message broker.
8. If you have some ideas or find some issues or probably terrible source code, let me know.
