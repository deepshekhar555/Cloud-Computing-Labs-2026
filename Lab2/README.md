Lab 2 - Client-Server Communication Between Two Virtual Machines

This folder contains the report and documentation for Experiment 2:
setting up two VMs on a shared NAT network and implementing a
TCP-based chat application between them.

server.py (runs on the Server VM):
import socket

HOST = '0.0.0.0'
PORT = 5000

server_socket = socket.socket(socket.AF_INET, socket.SOCK_STREAM)
server_socket.setsockopt(socket.SOL_SOCKET, socket.SO_REUSEADDR, 1)
server_socket.bind((HOST, PORT))
server_socket.listen(1)

print(f"[SERVER] Listening on {HOST}:{PORT} ...")

conn, addr = server_socket.accept()
print(f"[SERVER] Connected by {addr}")

while True:
    data = conn.recv(1024).decode()
    if not data or data.lower() == "bye":
        print("[CLIENT] Disconnected.")
        break
    print(f"[CLIENT]: {data}")

    msg = input("[SERVER]: ")
    conn.sendall(msg.encode())
    if msg.lower() == "bye":
        break

conn.close()
server_socket.close()
print("[SERVER] Connection closed.")


client.py (runs on the Client VM):
import socket

SERVER_IP = '10.0.2.15'
PORT = 5000

client_socket = socket.socket(socket.AF_INET, socket.SOCK_STREAM)
client_socket.connect((SERVER_IP, PORT))
print(f"[CLIENT] Connected to server at {SERVER_IP}:{PORT}")

while True:
    msg = input("[CLIENT]: ")
    client_socket.sendall(msg.encode())
    if msg.lower() == "bye":
        break

    data = client_socket.recv(1024).decode()
    if not data or data.lower() == "bye":
        print("[SERVER] Disconnected.")
        break
    print(f"[SERVER]: {data}")

client_socket.close()
print("[CLIENT] Connection closed.")
