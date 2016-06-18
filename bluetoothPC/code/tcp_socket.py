from collections import deque
import SocketServer
import signal
import time

from bt_socket import BlueSock

def ctrl_c_handler(signal, frame):
    time.sleep(1)
    print("In ctrl C handler...")
    [bt.send("STOP") for bt in bts]

    for bt in bts:
        bt.running = False
        bt.close()
        bt.join(1)
    print("After handler")

class MyTCPHandler(SocketServer.BaseRequestHandler):
    """
    The request handler class for our server.

    It is instantiated once per connection to the server, and must
    override the handle() method to implement communication to the
    client.
    """

    allow_reuse_address = True

    def handle(self):
        # self.request is the TCP socket connected to the client
        print("new connection")
        running = True
        while running:
            self.data = self.request.recv(1024).strip()
            print("{} wrote:".format(self.client_address[0]))
            print(self.data)
            if self.data != "log":
                 [bt.send(self.data) for bt in bts]
            else:
                print(qin)
                self.request.sendall(str(qin)+"\n")
            if self.data == "stop":
                ctrl_c_handler(None, None)
                running = False
                break
        print("end handle TCP")

def connect_bt(addr, qin, bts):
    try:
        time.sleep(0.1)
        bt = BlueSock(addr, qin)
        bt.debug = True
        bt.connect()
        bts.append(bt)
        return bts
    except KeyboardInterrupt:
        ctrl_c_handler(None, None)

if __name__ == "__main__":
    HOST, PORT = "localhost", 9999
    # Create the server, binding to localhost on port 9999
    server = SocketServer.TCPServer((HOST, PORT), MyTCPHandler)

    qin = deque()
    addrs = ["00:16:53:0C:C8:0A"]  #, "00:16:53:0F:F5:A9"]
    bts = []
    for addr in addrs:
        bts = connect_bt(addr, qin, bts)


    # Activate the server; this will keep running until you
    # interrupt the program with Ctrl-C
    try:
        server.serve_forever()
    except KeyboardInterrupt:
        ctrl_c_handler(None, None)