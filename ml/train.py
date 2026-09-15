"""
Training script for Handwritten Math Symbol Recognition (22 Classes).
Supports both TensorFlow/Keras CNN training and Scikit-Learn MLP fallback
for lightweight environments without precompiled TensorFlow wheels.
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

from dataset import generate_math_dataset, split_train_val_test, NUM_CLASSES, CLASSES
from tflite_builder import build_math_symbol_tflite


def train_mlp(epochs=60, samples_per_class=500, output_dir="output", deploy_to_android=True):
    """
    Trains a 2-layer Multi-Layer Perceptron (784 -> 128 -> 22) using Scikit-Learn
    and exports a fully compliant TFLite FlatBuffer model.
    """
    from sklearn.neural_network import MLPClassifier

    os.makedirs(output_dir, exist_ok=True)
    tflite_output_path = os.path.join(output_dir, "math_symbol_model.tflite")
    weights_npz_path = os.path.join(output_dir, "best_math_model.npz")

    print(f"=== Starting Training Pipeline (MLP): {NUM_CLASSES} Math Classes ===")
    X, y = generate_math_dataset(samples_per_class=samples_per_class)
    (X_train, y_train), (X_val, y_val), (X_test, y_test) = split_train_val_test(X, y)

    # Flatten 28x28 images to 784-dim vectors
    X_train_flat = X_train.reshape(len(X_train), -1)
    X_val_flat = X_val.reshape(len(X_val), -1)
    X_test_flat = X_test.reshape(len(X_test), -1)

    print(f"Training MLP on {len(X_train)} samples, validating on {len(X_val)} samples...")
    clf = MLPClassifier(
        hidden_layer_sizes=(128,),
        activation='relu',
        solver='adam',
        alpha=1e-4,
        batch_size=64,
        learning_rate_init=1e-3,
        max_iter=epochs,
        early_stopping=True,
        n_iter_no_change=8,
        random_state=42,
        verbose=True
    )
    clf.fit(X_train_flat, y_train)

    train_acc = clf.score(X_train_flat, y_train)
    val_acc = clf.score(X_val_flat, y_val)
    test_acc = clf.score(X_test_flat, y_test)

    print(f"\n=== Final Evaluation on Test Set ===")
    print(f"Train Accuracy: {train_acc * 100:.2f}%")
    print(f"Val Accuracy:   {val_acc * 100:.2f}%")
    print(f"Test Accuracy:  {test_acc * 100:.2f}%")

    # Extract weights and biases
    # clf.coefs_[0]: (784, 128) -> TFLite FC1 weights: (128, 784)
    # clf.intercepts_[0]: (128,) -> TFLite FC1 bias: (128,)
    # clf.coefs_[1]: (128, 22) -> TFLite FC2 weights: (22, 128)
    # clf.intercepts_[1]: (22,) -> TFLite FC2 bias: (22,)
    w1 = clf.coefs_[0].T.astype(np.float32)
    b1 = clf.intercepts_[0].astype(np.float32)
    w2 = clf.coefs_[1].T.astype(np.float32)
    b2 = clf.intercepts_[1].astype(np.float32)

    # Save weights
    np.savez_compressed(weights_npz_path, w1=w1, b1=b1, w2=w2, b2=b2)
    print(f"Saved trained weights: {weights_npz_path}")

    # Build TFLite model directly
    build_math_symbol_tflite(w1, b1, w2, b2, tflite_output_path)

    if deploy_to_android:
        deploy_artifacts(tflite_output_path)

    return tflite_output_path


def train_keras(epochs=20, batch_size=64, learning_rate=1e-3, samples_per_class=1000, output_dir="output", deploy_to_android=True):
    """
    Trains CNN model via TensorFlow/Keras.
    """
    import tensorflow as tf
    from tensorflow.keras.callbacks import ModelCheckpoint, EarlyStopping, ReduceLROnPlateau
    from model import build_math_cnn
    from export_tflite import export_to_tflite

    os.makedirs(output_dir, exist_ok=True)
    best_model_path = os.path.join(output_dir, "best_math_model.keras")

    print(f"=== Starting Training Pipeline (Keras CNN): {NUM_CLASSES} Math Classes ===")
    X, y = generate_math_dataset(samples_per_class=samples_per_class)
    (X_train, y_train), (X_val, y_val), (X_test, y_test) = split_train_val_test(X, y)

    model = build_math_cnn(num_classes=NUM_CLASSES)
    model.compile(
        optimizer=tf.keras.optimizers.Adam(learning_rate=learning_rate),
        loss=tf.keras.losses.SparseCategoricalCrossentropy(),
        metrics=['accuracy']
    )

    callbacks = [
        ModelCheckpoint(best_model_path, monitor='val_accuracy', save_best_only=True, verbose=1),
        EarlyStopping(monitor='val_accuracy', patience=5, restore_best_weights=True, verbose=1),
        ReduceLROnPlateau(monitor='val_loss', factor=0.5, patience=2, min_lr=1e-5, verbose=1)
    ]

    print(f"Training on {len(X_train)} samples, validating on {len(X_val)} samples...")
    model.fit(
        X_train, y_train,
        validation_data=(X_val, y_val),
        epochs=epochs,
        batch_size=batch_size,
        callbacks=callbacks
    )

    test_loss, test_acc = model.evaluate(X_test, y_test, verbose=0)
    print(f"\n=== Final Evaluation on Test Set ===")
    print(f"Test Loss:     {test_loss:.4f}")
    print(f"Test Accuracy: {test_acc * 100:.2f}%")

    tflite_path = os.path.join(output_dir, "math_symbol_model.tflite")
    export_to_tflite(best_model_path, tflite_path, deploy_to_android=deploy_to_android)
    return tflite_path


def deploy_artifacts(tflite_path: str):
    """
    Deploys the generated TFLite model and labels.txt to Android app assets.
    """
    android_assets_dir = os.path.abspath(
        os.path.join(os.path.dirname(__file__), "..", "app", "src", "main", "assets", "models")
    )
    os.makedirs(android_assets_dir, exist_ok=True)
    dest_model = os.path.join(android_assets_dir, "math_symbol_model.tflite")
    dest_labels = os.path.join(android_assets_dir, "labels.txt")

    shutil.copyfile(tflite_path, dest_model)
    print(f"Deployed model to Android assets: {dest_model}")

    with open(dest_labels, "w", encoding="utf-8") as f:
        for lbl in CLASSES:
            f.write(f"{lbl}\n")
    print(f"Deployed labels.txt ({len(CLASSES)} classes) to: {dest_labels}")


if __name__ == '__main__':
    parser = argparse.ArgumentParser(description="Train Math Symbol Recognition Model")
    parser.add_argument("--epochs", type=int, default=60, help="Number of training epochs")
    parser.add_argument("--batch_size", type=int, default=64, help="Batch size")
    parser.add_argument("--samples", type=int, default=500, help="Samples per class")
    parser.add_argument("--output_dir", type=str, default="output", help="Directory to save model")
    parser.add_argument("--no_deploy", action="store_true", help="Do not deploy to Android assets")
    args = parser.parse_args()

    deploy = not args.no_deploy
    try:
        import tensorflow as tf
        train_keras(
            epochs=args.epochs,
            batch_size=args.batch_size,
            samples_per_class=args.samples,
            output_dir=args.output_dir,
            deploy_to_android=deploy
        )
    except ImportError:
        print("Notice: TensorFlow not found in environment. Using Scikit-Learn MLP + FlatBuffers TFLite pipeline.")
        train_mlp(
            epochs=args.epochs,
            samples_per_class=args.samples,
            output_dir=args.output_dir,
            deploy_to_android=deploy
        )
