# vertx-requestalreadyread

This small project shows a bug in vertx.

curl -s -XPUT "http://localhost:8080/test" --data-binary "foo"

When a request is received, if a request is sent to a client before the body of the request is read,
then an exception "java.lang.IllegalStateException: Request has already been read" is thrown.

By commenting line 27 and 29 (and adding a semicolon at the end of line 28), you can see that if the body
is not read, everything goes well.

A workaround is to read the body before to send the request to the client.
