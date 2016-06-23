from collections import deque, defaultdict
import SocketServer
import threading
import signal
import time

import json

from bt_socket import BlueSock


class MyTCPHandler(SocketServer.BaseRequestHandler):
    """
    The request handler class for our server.

    It is instantiated once per connection to the server, and must
    override the handle() method to implement communication to the
    client.
    """

    allow_reuse_address = True

    def send_to_robots(self):
        for bt in bts :
            bt.send(self.data)

    def answer_clients(self):
        mess = defaultdict(list)
        while True:
            try:
                addr, val = qin.popleft().items()[0]
                mess[addr].append(val)
                print(addr, val, mess)
            except IndexError:
                break

        self.request.sendall(json.dumps(mess) + '\n')

    def stop(self):
        ctrl_c_handler(None, None)
        running = False

    def handle(self):
        # self.request is the TCP socket connected to the client
        print("new connection")
        print "Client connected with ", self.client_address
        running = True
        while running:
            self.data = self.request.recv(1024).strip()
            if self.data != "log":
                self.send_to_robots()
            else:
                self.answer_clients()
            if self.data == "stop":
                self.stop()
                break
        print("end handle TCP")

def ctrl_c_handler(signal, frame):
    time.sleep(1)
    print("In ctrl C handler...")
    [bt.send("STOP") for bt in bts]

    for bt in bts:
        bt.running = False
        bt.close()
        bt.join(1)
    print("After handler")

def connect_bt(addr, qin):
    try:
        time.sleep(0.1)
        bt = BlueSock(addr, qin)
        bt.debug = True
        connected = bt.connect()
        if connected:
            return bt
        return False
    except KeyboardInterrupt:
        ctrl_c_handler(None, None)

def connect_all_robots():
    while !connect_all_robots.stop:
        if not to_connect.empty():
            addr = addr_to_connect.popleft()
            bt = connect_bt(addr, qin, addr_to_connect)
            if not bt:  # error while connected
                to_connect.append(addr)
            connected["addr"]=bt
        else:
            time.sleep(1)

if __name__ == "__main__":
    HOST, PORT = "localhost", 9999
    # Create the server, binding to localhost on port 9999
    server = SocketServer.TCPServer((HOST, PORT), MyTCPHandler)

    qin = deque(maxlen=40)
    addrs = ["00:16:53:0C:C8:0A", "00:16:53:0F:F5:A9", "00:16:53:13:EF:A9"]
    to_connect = deque(addrs)
    connected =  {}
    connections = threading.Thread(None, connect_all_robots)
    connect_all_robots.stop=False
    connections.start()
    # Activate the server; this will keep running until you
    # interrupt the program with Ctrl-C
    try:
        server.serve_forever()
    except KeyboardInterrupt:
        connect_all_robots.stop
        ctrl_c_handler(None, None)