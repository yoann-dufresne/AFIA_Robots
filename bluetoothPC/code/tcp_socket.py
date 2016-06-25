from collections import deque
import SocketServer
import signal
import time

import json
import collections

from bt_socket import BlueSock


ADRESSES = {"00:16:53:0F:F5:A9":1, "00:16:53:13:EF:A9":0}
ADRESSES_INV = {0:"00:16:53:13:EF:A9", 1:"00:16:53:0F:F5:A9"}

def ctrl_c_handler(signal, frame):
    time.sleep(1)
    print("In ctrl C handler...")
    [bt.send("STOP") for bt in bts]

    for bt in bts:
        bt.running = False
        bt.close()
        bt.join(1)
    print("After handler")

def connect_bt(addr, qin, bts):
    try:
        time.sleep(0.1)
        addr_simple = ADRESSES[addr]
        bt = BlueSock(addr, addr_simple, qin)
        bt.debug = True
        bt.connect()
        bts.append(bt)
        return bts
    except KeyboardInterrupt:
        ctrl_c_handler(None, None)


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

    def send_to_other_robots(self, addr, val):
        """Send a command to the other robots if needed
        """
        to_send = ("DISCOVERED", "CONFLICT",
                   "NEXT_POS", "PARTIAL",
                   "COMPUTE_PATH", "COMPUTED_PATH")

        command = val.split(";")[0]
        if command in to_send:
            for bt in set(bts) - set([addr]):
                print("sent {} to {}".format(command, bt))
                bt.send(val)


    def answer_clients(self):
        mess = collections.defaultdict(list)
        while True:
            try:
                addr, val = qin.popleft().items()[0]
                self.send_to_other_robots(addr, val)
                mess[addr].append(val)
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


if __name__ == "__main__":
    HOST, PORT = "localhost", 9999
    # Create the server, binding to localhost on port 9999
    server = SocketServer.TCPServer((HOST, PORT), MyTCPHandler)

    qin = deque(maxlen=40)

    addrs = [ADRESSES_INV[0]]

    bts = []
    for addr in addrs:
        bts = connect_bt(addr, qin, bts)

    # Activate the server; this will keep running until you
    # interrupt the program with Ctrl-C
    try:
        server.serve_forever()
    except KeyboardInterrupt:
        ctrl_c_handler(None, None)

