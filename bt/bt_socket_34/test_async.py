#!/usr/bin/env python3.4
import asyncio
import socket

class EchoServer(asyncio.Protocol):
    def connection_made(self, transport):
        peername = transport.get_extra_info('peername')
        print('connection from {}'.format(peername))
        self.transport = transport

    def data_received(self, data):
        print('data received: {}'.format(data.decode()))
        fut = asyncio.async(self.sleeper())
        result = asyncio.wait_for(fut, 60)

    @asyncio.coroutine
    def sleeper(self):
        yield from asyncio.sleep(2)
        self.transport.write("Hello World".encode())
        self.transport.close()


@asyncio.coroutine
def bt():
    sock = socket.socket(socket.AF_BLUETOOTH, socket.SOCK_STREAM, socket.BTPROTO_RFCOMM)
    addr = ("00:16:53:13:EF:A9", 1)
    print("in bt")
    yield from loop.sock_connect(sock, addr)
    print("after connection")
    yield from loop.sock_sendall(sock, "from pc;".encode())
    print("after send")
    data = yield from loop.sock_recv(sock, 100)
    print("received", data)




loop = asyncio.get_event_loop()
coro = loop.create_server(EchoServer, '127.0.0.1', 8888)

server = loop.run_until_complete(bt())
asyncio.async(coro)
# print('serving on {}'.format(server.sockets[0].getsockname()))

try:
    loop.run_forever()
except KeyboardInterrupt:
    print("exit")
finally:
    server.close()
    loop.close()