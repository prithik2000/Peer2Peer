Prithik Karthikeyan

Execution Instructions

1. run a peer on each of the 6 servers using "java p2p" while in the p2p directory
2. type connect in any of the 6 servers to obtain a file
3. use leave to disconnect the client
4. use exit to shut down the peer and get back to command line.

To add more neighbours, initialise the required client objects and add the required IP addresses to the config_neighbors.txt files for each server.

The program easily handles duplicates by replacing the overwriting the file with the same name.
The program ensures that the the file being downloaded has the same name as that of the file requested.

I have coded this program to work like any other protocol, listening on two unique ports.
52180 for requests and responses and 52190 for file transfer.

As a result, the config_neighbors file contains only IP addresses.

You are welcome to use this code as a reference for. your projects.