# Broker_Benchmarks
Benchmark for several message brokers

These Benchmarks are developed with Java so a working JDK should be installed on your machine (I used JDK 1.8, I don't know whether there are problems with lower versions.).

This Readme just contains short instructions how to run all benchmarks. If you need more information please look to the wiki page. There are several explanations to message protocols, message brokers and how to use these benchmarks in detail. Probably you can find some issues or improvements to my source code or wiki pages. If that's the case please let me know.

#Supported Brokers and its messaging protocols
- RabbitMQ AMQP (is using AMQP per default)
- ActiveMQ Openwire and AMQP (is using Openwire per default)
- Qpid AMQP (is using AMQP per default)
- OpenMQ UMS (is using UMS per default)

The Qpid and ActiveMQ AMQP benchmarks are using the JMS AMQP library because I found no other libraries. JMS is no messaging protocol but an standardised abstraction layer.
It's purpose is to ease and unify the usage of different messaging libraries.

#Components Clients:
- Sender and Receiver for every benchmark to abstract the server connection from the real benchmark measurements.

#Components Processors
- message configurator to configure the size and amount of generated messages.
- message generator to generate all messages with the configured properties
- message reader to analyse all received messages

#Benchmark all Brokers without serialization
1. Install RabbitMQ-Server, ActiveMQ-Server, QPID-C++ and QPID-Java Server, Glassfish.
2. clone all benchmark projects to your local machine
3. import all projects to your IDE (e.g. I used eclipse). Probably it is needed to create a new project with the given source.
4. if you just want to benchmark all brokers add the "Message_Processor" to every client Projects build path.
5. Look at the configuration Part on this projects wiki page to configure every broker to get comparable measurements.
6. On every project with a pom.xml, you have to run maven install to retrieve all dependencies.
7. Now you can start the first broker, run its related receiver.java and then run the sender.java in the same client project. 
8. Repeat this procedure for every message broker.


#Benchmark all Brokers with serialization
1. Install RabbitMQ-Server, ActiveMQ-Server, QPID-C++ and QPID-Java Server, Glassfish.
2. clone all benchmark projects to your local machine
3. import all projects to your IDE (e.g. I used eclipse). Probably it is needed to create a new project with the given source.
4. if you want to benchmark all brokers and the serialization framework Gson, add the "Message_Processorr Gson" to every client Projects build path.
5. if you want to benchmark all brokers and the serialization framework Jackson, add the "Message_Processorr Jackson" to every client Projects build path.
6. Reorganize the imports in all sender and receiver classes and add the variable i to the "generateMessages()" method. This is needed to provide more information at the end.
7. Look at the configuration Part on this projects wiki page to configure every broker to get comparable measurements.
8. On every project with a pom.xml, you have to run maven install to retrieve all dependencies.
9. Now you can start the first broker, run its related receiver.java and then run the sender.java in the same client project.
10. Repeat this procedure for every message broker.
