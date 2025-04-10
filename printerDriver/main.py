from flask import Flask, request, jsonify
import os
import subprocess

app = Flask(__name__)

ALLOWED_EXTENSIONS = {'txt', 'pdf', 'doc', 'docx', 'png', 'jpg', 'jpeg'}


def allowed_file(filename):
    return '.' in filename and filename.rsplit('.', 1)[1].lower() in ALLOWED_EXTENSIONS


@app.route('/print', methods=['POST'])
def print_file():
    if 'file' not in request.files:
        return jsonify({"message": "File was not found"}), 400

    file = request.files['file']

    if file.filename == '':
        return jsonify({"message": "File was not found"}), 400

    if not allowed_file(file.filename):
        return jsonify({"message": "Unsupported type of file"}), 400

    temp_path = os.path.join("temp_uploads", file.filename)
    file.save(temp_path)

    try:
        subprocess.run(['notepad', '/p', temp_path], check=True)
        return jsonify({"message": f"File was printed"}), 200
    except subprocess.CalledProcessError as e:
        return jsonify({"message": f"Error in printing"}), 500
    finally:
        os.remove(temp_path)


if __name__ == '__main__':
    os.makedirs("temp_uploads", exist_ok=True)
    app.run(debug=True)
