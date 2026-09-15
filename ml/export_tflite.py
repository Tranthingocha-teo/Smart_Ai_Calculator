"""
Export and Quantization module for TensorFlow Lite.
Converts trained models (Keras or NumPy weights) into optimized TFLite models (< 2MB)
and deploys them directly into Android app assets directory.
"""

import os
import sys
import shutil
import argparse
import numpy as np

# Ensure ml directory is in sys.path
current_dir = os.path.dirname(os.path.abspath(__file__))
if current_dir not in sys.path:
    sys.path.insert(0, current_dir)

from dataset import CLASSES, NUM_CLASSES, generate_math_dataset
from tflite_builder import build_math_symbol_tflite


def deploy_to_android_assets(output_tflite_path):
    """Deploys .tflite model and labels.txt into app/src/main/assets/models/"""
    android_assets_dir = os.path.abspath(
        os.path.join(os.path.dirname(__file__), "..", "app", "src", "main", "assets", "models")
    )
    os.makedirs(android_assets_dir, exist_ok=True)
    dest_model = os.path.join(android_assets_dir, "math_symbol_model.tflite")
    dest_labels = os.path.join(android_assets_dir, "labels.txt")

    shutil.copyfile(output_tflite_path, dest_model)
    print(f"Copied model to Android assets: {dest_model}")

    with open(dest_labels, "w", encoding="utf-8") as f:
        for lbl in CLASSES:
            f.write(f"{lbl}\n")
    print(f"Written {len(CLASSES)} labels to: {dest_labels}")


def export_from_npz(weights_npz_path, output_tflite_path="math_symbol_model.tflite", deploy=True):
    """Builds TFLite model from saved .npz weights."""
    print(f"Loading weights from: {weights_npz_path}")
    data = np.load(weights_npz_path)
    w1, b1, w2, b2 = data['w1'], data['b1'], data['w2'], data['b2']
    build_math_symbol_tflite(w1, b1, w2, b2, output_tflite_path)

    if deploy:
        deploy_to_android_assets(output_tflite_path)
    return output_tflite_path


def export_from_keras(keras_model_path, output_tflite_path="math_symbol_model.tflite", quantize=True, deploy=True):
    """Converts Keras model to TFLite using tf.lite.TFLiteConverter."""
    import tensorflow as tf

    print(f"Loading Keras model from: {keras_model_path}")
    model = tf.keras.models.load_model(keras_model_path)

    converter = tf.lite.TFLiteConverter.from_keras_model(model)
    if quantize:
        print("Applying Post-Training Quantization (Dynamic Range / INT8)...")
        converter.optimizations = [tf.lite.Optimize.DEFAULT]

        def representative_data_gen():
            X, _ = generate_math_dataset(samples_per_class=10)
            for i in range(min(100, len(X))):
                sample = tf.expand_dims(X[i], axis=0)
                yield [sample]

        converter.representative_dataset = representative_data_gen

    tflite_model = converter.convert()
    with open(output_tflite_path, "wb") as f:
        f.write(tflite_model)

    size_kb = os.path.getsize(output_tflite_path) / 1024.0
    print(f"Successfully generated TFLite model: {output_tflite_path} ({size_kb:.2f} KB)")

    if deploy:
        deploy_to_android_assets(output_tflite_path)
    return output_tflite_path


if __name__ == '__main__':
    parser = argparse.ArgumentParser(description="Export trained model to TensorFlow Lite")
    parser.add_argument("--model", type=str, default="output/best_math_model.npz", help="Path to .keras or .npz weights")
    parser.add_argument("--output", type=str, default="output/math_symbol_model.tflite", help="Path for output .tflite")
    parser.add_argument("--no_deploy", action="store_true", help="Do not deploy to Android assets")
    args = parser.parse_args()

    deploy = not args.no_deploy
    if args.model.endswith(".npz"):
        export_from_npz(args.model, args.output, deploy=deploy)
    elif args.model.endswith(".keras"):
        try:
            export_from_keras(args.model, args.output, deploy=deploy)
        except ImportError:
            print("TensorFlow is required for .keras conversion.")
    else:
        # Check available files in output/
        if os.path.exists("output/best_math_model.npz"):
            export_from_npz("output/best_math_model.npz", args.output, deploy=deploy)
        elif os.path.exists("output/best_math_model.keras"):
            export_from_keras("output/best_math_model.keras", args.output, deploy=deploy)
        else:
            print("No trained model found. Please run train.py first.")
