# Introduction #

This page gives a getting started guide for configuring a JOnAS instance ready to start the o3s application

# JOnAS installation #

  * see http://jonas.ow2.org for installation guide and download
  * install at least JOnAS 5.2.0-M3-SNAPSHOT
  * create a new JONAS\_BASE with newjb command

# Server side #
## PostGreSQL driver ##

The driver must be dropped into the _$JONAS\_BASE/lib/ext_ directory.
Ex : postgresql-8.4-701.jdbc4.jar

## JORAM Configuration ##

A distributed JORAM configuration is required for ensuring robustness and network fault tolerance between the arrival endpoint and the server side.

### RAR configuration ###

The _ra.xml_ file located in $JONAS\_BASE/repositories/maven2-internal/org/objectweb/joram/joram\_ra\_for\_jonas/5.3.1/joram\_ra\_for\_jonas-5.3.1.rar is set as following :
  * persistent mode is enabled
  * id is adapted

```
      <config-property>
         <config-property-name>PersistentPlatform</config-property-name>
         <config-property-type>java.lang.Boolean</config-property-type>
         <config-property-value>true</config-property-value>
      </config-property>
      <config-property>
         <config-property-name>ServerId</config-property-name>
         <config-property-type>java.lang.Short</config-property-type>
         <config-property-value>1</config-property-value>
      </config-property>
      <config-property>
         <config-property-name>ServerName</config-property-name>
         <config-property-type>java.lang.String</config-property-type>
         <config-property-value>s1</config-property-value>
      </config-property>
```


### a3servers.xml ###

  * serverid **1** is the server
  * serverid **2** and **3** are the arrival endpoints

```
<?xml version="1.0"?>
<config>
  <domain name="D1"/>
  <property name="Transaction" value="fr.dyade.aaa.util.NTransaction"/>
  <server id="1" name="s1" hostname="localhost">
    <network domain="D1" port="16301"/>
    <service class="org.objectweb.joram.mom.proxies.ConnectionManager"
             args="root root"/>
    <service class="org.objectweb.joram.mom.proxies.tcp.TcpProxyService"
             args="16010"/>
    <service class="fr.dyade.aaa.jndi2.distributed.DistributedJndiServer"
               args="16401"/>
  </server>

  <server id="2" name="s2" hostname="localhost">
     <network domain="D1" port="16302"/>
     <service class="org.objectweb.joram.mom.proxies.ConnectionManager"
             args="root root"/>
    <service class="org.objectweb.joram.mom.proxies.tcp.TcpProxyService"
             args="16020"/>
    <service class="fr.dyade.aaa.jndi2.distributed.DistributedJndiServer"
               args="16402 1 3"/>
  </server>

  <server id="3" name="s3" hostname="localhost">
     <network domain="D1" port="16303"/>
     <service class="org.objectweb.joram.mom.proxies.ConnectionManager"
             args="root root"/>
    <service class="org.objectweb.joram.mom.proxies.tcp.TcpProxyService"
             args="16030"/>
    <service class="fr.dyade.aaa.jndi2.distributed.DistributedJndiServer"
               args="16403 1 2"/>
  </server>

</config>
```

### jndi.properties ###

This file must be added in order to route the JNDI accesses towards the JORAM JNDI server (scn).

```
java.naming.factory.url.pkgs    org.objectweb.jonas.naming:fr.dyade.aaa.jndi2
scn.naming.factory.host         localhost
scn.naming.factory.port         16401
```

### joramAdmin.xml ###
```
<?xml version="1.0"?>

<JoramAdmin>

<!--
<AdminModule>
  <connect host="localhost"
           port="16010"
           name="root"
           password="root"/>
</AdminModule>
-->

<AdminModule>
  <collocatedConnect name="root" password="root"/>
</AdminModule>

<ConnectionFactory className="org.objectweb.joram.client.jms.tcp.TcpConnectionFactory">
  <tcp host="localhost"
       port="16010"/>
  <jndi name="JCF"/>
</ConnectionFactory>

<ConnectionFactory className="org.objectweb.joram.client.jms.tcp.QueueTcpConnectionFactory">
  <tcp host="localhost"
       port="16010"/>
  <jndi name="JQCF"/>
</ConnectionFactory>

<ConnectionFactory className="org.objectweb.joram.client.jms.tcp.TopicTcpConnectionFactory">
  <tcp host="localhost"
       port="16010"/>
  <jndi name="JTCF"/>
</ConnectionFactory>

<User name="anonymous"
      password="anonymous"
      serverId="1"/>

<Topic name="NotificationTopic" serverId="1">
   <freeReader/>
   <freeWriter/>
   <jndi name="scn:comp/NotificationTopic"/>
</Topic>


<Queue name="TrackingQueue" serverId="1">
   <freeReader/>
   <freeWriter/>
   <jndi name="scn:comp/TrackingQueue"/>
</Queue>


</JoramAdmin>
```

## JAAS configuration ##

A JAAS entry named _o3s_ has to be added like following. The server name must be adjusted according to the JOnAS instance name (default is jonas, here is set to fdcserver)

```
 o3s {

     // Do authentication and sign the Subject.

     org.ow2.jonas.security.auth.spi.JResourceLoginModule required

     resourceName="memrlm_1"
     serverName="fdcserver"

     ;

 

 };
```

## jonas.properties ##

The following parameters are touched :
```
jonas.name    fdcserver
jonas.development    false
jonas.service.ejb3.jpa.provider    eclipselink
```

## OSGi classloader filtering ##

The _conf/osgi/defaults.properties_ file must be modified in order to add the following packages:

```
javase-packages ${javase-${javase.version}} , \
 		com.sun.management,\
 	        sun.nio.ch 
```

## Traces ##

The _conf/traces.properties_ file can be adjusted to format the log files with the following lines

```
handler.tty.pattern  %d : %l : %h : %O{1}.%M[%L] : %m%n
...
handler.logf.pattern  %d : %l : %h : %O{1}.%M[%L] :    %m%n

#logger.net.o3s.level DEBUG
```

# Arrival endpoint #

The arrival endpoint configuration is very close from the server one. Only the differences are presented here :

## jonas.properties ##

The following parameters are touched :
```
jonas.name    fdcarr1
```

## JORAM configuration ##
### RAR configuration ###

The _ra.xml_ file located in $JONAS\_BASE/repositories/maven2-internal/org/objectweb/joram/joram\_ra\_for\_jonas/5.3.1/joram\_ra\_for\_jonas-5.3.1.rar is set as following :
  * persistent mode is enabled
  * id is adapted

```
      <config-property>
         <config-property-name>PersistentPlatform</config-property-name>
         <config-property-type>java.lang.Boolean</config-property-type>
         <config-property-value>true</config-property-value>
      </config-property>
      <config-property>
         <config-property-name>ServerId</config-property-name>
         <config-property-type>java.lang.Short</config-property-type>
         <config-property-value>2</config-property-value>
      </config-property>
      <config-property>
         <config-property-name>ServerName</config-property-name>
         <config-property-type>java.lang.String</config-property-type>
         <config-property-value>s2</config-property-value>
      </config-property>
```
### joramAdmin.xml ###
```
<Topic name="NotificationTopic" serverId="1">
   <freeReader/>
   <freeWriter/>
   <jndi name="scn:comp/NotificationTopic"/>
</Topic>


<Queue name="TrackingQueue" serverId="1">
   <freeReader/>
   <freeWriter/>
   <jndi name="scn:comp/TrackingQueue"/>
</Queue>
```