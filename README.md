## Java client/server
### Task:

Server send 1000 messages to clients with maximum productivity.

Communication through sockets.

Server UI, shows table with statistic. Table structure HTML:  
message_id, client_id, message_text, status{delivered, undelivered}, delivered_time

### Setup 

First you need to configure the client launch (Edit configuration -> Modify options 
-> Allow multiple instances). 

Next you assign a name to each Client in the console, after which the 
Server console will say that the user has joined. To start spamming, you must write the code word "CODEWORD" 
to Server console. 

You can see statistics of all messages on localhost:8082 (or json that was sent on /messageStatistics). U can sort
them by the name id, message id, delivered time, and status.
