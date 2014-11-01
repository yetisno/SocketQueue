# Description #
This project provide a simple socket-based message queue.

# Install #
	mvn clean install

# Run #
	java -jar SocketQueue.jar <bind address> <bind port>

# Command Type #
## 1. Put ##

Send data structure.

| **Type** | flag |		dataLength		| data	|
|   :--:   | :--: |			:--:		| :--:	|
| **Data** | 0x00 | 4 bytes (**int**)	| bytes	|

Recv data structure.

None.

## 2. Get ##

Send data structure.

| **Type** | flag |
|   :--:   | :--: |
| **Data** | 0x01 |

Recv data structure.

| **Type** |	dataLength		| data	|
|   :--:   |		:--:		| :--:	|
| **Data** | 4 bytes (**int**)	| bytes	|
