from collections import deque
import SocketServer
import signal
import time

from bt_socket import BlueSock

def ctrl_c_handler(signal, frame):
    bt.running = False
    bt.join()

class MyTCPHandler(SocketServer.BaseRequestHandler):
    """
    The request handler class for our server.

    It is instantiated once per connection to the server, and must
    override the handle() method to implement communication to the
    client.
    """

    def handle(self):
        # self.request is the TCP socket connected to the client
        while True:
            self.data = self.request.recv(1024).strip()
            print("{} wrote:".format(self.client_address[0]))
            print(self.data)
            if self.data != "log":
                bt.send(self.data)
            else:
                print(qin)
                self.request.sendall(str(qin))
            if self.data == "stop":
                ctrl_c_handler(None, None)
                break

if __name__ == "__main__":
    HOST, PORT = "localhost", 9999
    # Create the server, binding to localhost on port 9999
    server = SocketServer.TCPServer((HOST, PORT), MyTCPHandler)

    qin = deque()
    bt = BlueSock("00:16:53:13:EF:A9", qin)
    bt.debug = True
    bt.connect()
    time.sleep(1)



    # Activate the server; this will keep running until you
    # interrupt the program with Ctrl-C
    server.serve_forever()