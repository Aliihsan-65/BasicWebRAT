#!/bin/bash

echo "Creating virtual environment..."
python -m venv rat_env

echo "Activating virtual environment..."
source rat_env/bin/activate

echo "Installing Python dependencies..."
pip install -r requirements.txt

echo "Starting RAT Server on port 8080..."
python app.py