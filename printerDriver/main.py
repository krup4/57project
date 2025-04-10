from flask import Flask, request, jsonify
import os
import subprocess
import tempfile
import win32print
import win32api
import time
from datetime import datetime, timedelta

app = Flask(__name__)

ALLOWED_EXTENSIONS = {'txt', 'pdf', 'doc', 'docx', 'png', 'jpg', 'jpeg'}


def allowed_file(filename):
    return '.' in filename and filename.rsplit('.', 1)[1].lower() in ALLOWED_EXTENSIONS


def is_printer_done(printer_name):
    printer = win32print.OpenPrinter(printer_name)
    try:
        status = win32print.GetPrinter(printer, 2)["Status"]
        return status == 0
    finally:
        win32print.ClosePrinter(printer)


@app.route('/check', methods=['GET'])
def check():
    return jsonify({"status": "ok"})


@app.route('/print', methods=['POST'])
def print_file():
    if 'file' not in request.files:
        return jsonify({"message": "File was not found"}), 400

    file = request.files['file']

    if file.filename == '':
        return jsonify({"message": "File was not found"}), 400

    if not allowed_file(file.filename):
        return jsonify({"message": "Unsupported type of file"}), 400

    file_ext = file.filename.rsplit('.', 1)[1].lower()

    temp_path = os.path.join("temp_uploads", file.filename)
    with open(temp_path, 'wb') as f:
        file.save(f)
    time.sleep(5)

    try:
        printer_name = win32print.GetDefaultPrinter()
        win32api.ShellExecute(
            0,
            "print",
            temp_path,
            f'"{printer_name}"',
            ".",
            0
        )

        for _ in range(30):
            if is_printer_done(printer_name):
                break
            time.sleep(1)

        return jsonify({"message": f"File {file.filename} was sent to printer"}), 200
    except subprocess.CalledProcessError as e:
        return jsonify({"message": f"Error in printing: {str(e)}"}), 500
    finally:
        os.remove(temp_path)


if name == '__main__':
    app.run(debug=True, host="0.0.0.0", port=8080)
