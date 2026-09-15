"""
Dataset module for Handwritten Math Symbol Recognition (22 Classes).
Supports loading MNIST for digits and procedural synthesis/augmentation
for math operators (+, -, *, /, =, ., (, ), x, y, ^, ,).
"""

import os
import math
import random
import numpy as np

# 22 canonical classes for Smart AI Calculator
CLASSES = [
    '0', '1', '2', '3', '4', '5', '6', '7', '8', '9',
    '+', '-', '*', '/', '=', '.', '(', ')', 'x', 'y', '^', ','
]

NUM_CLASSES = len(CLASSES)
LABEL_TO_INDEX = {label: idx for idx, label in enumerate(CLASSES)}
INDEX_TO_LABEL = {idx: label for idx, label in enumerate(CLASSES)}


def get_classes():
    """Return list of supported symbol classes."""
    return list(CLASSES)


def load_mnist_subset(samples_per_digit=1000):
    """
    Load digit samples (0-9) from tf.keras.datasets.mnist if available,
    or generate procedural digits if running in an offline environment.
    """
    try:
        import tensorflow as tf
        (x_train, y_train), (x_test, y_test) = tf.keras.datasets.mnist.load_data()
        x_all = np.concatenate([x_train, x_test], axis=0)
        y_all = np.concatenate([y_train, y_test], axis=0)

        images = []
        labels = []
        for digit in range(10):
            idx = np.where(y_all == digit)[0][:samples_per_digit]
            images.append(x_all[idx])
            labels.extend([str(digit)] * len(idx))

        images = np.concatenate(images, axis=0)
        return images, labels
    except Exception as e:
        print(f"Warning: Could not load tf.keras MNIST ({e}). Using synthetic generator.")
        return generate_synthetic_digits(samples_per_digit)


def generate_synthetic_symbol(symbol: str, size: int = 28) -> np.ndarray:
    """
    Synthesizes a 28x28 grayscale image with white strokes on black background (0..255).
    Applies random line thickness, slight rotations, scaling, and stroke noise.
    """
    img = np.zeros((size, size), dtype=np.float32)
    center = size / 2.0

    # Draw symbol-specific strokes
    thickness = random.randint(2, 3)
    jitter_x = random.uniform(-2.0, 2.0)
    jitter_y = random.uniform(-2.0, 2.0)
    cx, cy = center + jitter_x, center + jitter_y

    def draw_line(x1, y1, x2, y2, stroke_w=thickness):
        dist = math.hypot(x2 - x1, y2 - y1)
        steps = max(int(dist * 2), 1)
        for s in range(steps + 1):
            t = s / steps
            px = int(round(x1 + t * (x2 - x1)))
            py = int(round(y1 + t * (y2 - y1)))
            for dy in range(-stroke_w, stroke_w + 1):
                for dx in range(-stroke_w, stroke_w + 1):
                    if dx * dx + dy * dy <= stroke_w * stroke_w:
                        nx, ny = px + dx, py + dy
                        if 0 <= nx < size and 0 <= ny < size:
                            img[ny, nx] = min(255.0, img[ny, nx] + random.uniform(200.0, 255.0))

    if symbol == '+':
        draw_line(cx - 7, cy, cx + 7, cy)
        draw_line(cx, cy - 7, cx, cy + 7)
    elif symbol == '-':
        draw_line(cx - 8, cy, cx + 8, cy)
    elif symbol == '*':
        draw_line(cx - 6, cy - 6, cx + 6, cy + 6)
        draw_line(cx - 6, cy + 6, cx + 6, cy - 6)
    elif symbol == '/':
        draw_line(cx - 6, cy + 8, cx + 6, cy - 8)
    elif symbol == '=':
        draw_line(cx - 8, cy - 3, cx + 8, cy - 3)
        draw_line(cx - 8, cy + 3, cx + 8, cy + 3)
    elif symbol == '.':
        for dy in range(-2, 3):
            for dx in range(-2, 3):
                if dx * dx + dy * dy <= 4:
                    ny, nx = int(cy + 6 + dy), int(cx + dx)
                    if 0 <= nx < size and 0 <= ny < size:
                        img[ny, nx] = 255.0
    elif symbol == '(':
        for deg in range(120, 241, 10):
            rad = math.radians(deg)
            px = cx + 2 + 7 * math.cos(rad)
            py = cy + 9 * math.sin(rad)
            draw_line(px, py, px, py, stroke_w=2)
    elif symbol == ')':
        for deg in range(-60, 61, 10):
            rad = math.radians(deg)
            px = cx - 2 + 7 * math.cos(rad)
            py = cy + 9 * math.sin(rad)
            draw_line(px, py, px, py, stroke_w=2)
    elif symbol == 'x':
        draw_line(cx - 6, cy - 6, cx + 6, cy + 6)
        draw_line(cx - 6, cy + 6, cx + 6, cy - 6)
    elif symbol == 'y':
        draw_line(cx - 6, cy - 7, cx, cy + 1)
        draw_line(cx + 6, cy - 7, cx - 4, cy + 8)
    elif symbol == '^':
        draw_line(cx - 6, cy + 3, cx, cy - 6)
        draw_line(cx, cy - 6, cx + 6, cy + 3)
    elif symbol == ',':
        draw_line(cx, cy + 4, cx - 2, cy + 8)
    else:
        # Fallback simple vertical stroke for digits if synthetic
        draw_line(cx, cy - 8, cx, cy + 8)

    # Slight Gaussian-like blur and noise
    noise = np.random.normal(0, 3, (size, size))
    img = np.clip(img + noise, 0, 255).astype(np.float32)
    return img


def generate_synthetic_digits(samples_per_digit=500):
    """Fallback generator for digits 0-9."""
    images = []
    labels = []
    for digit in range(10):
        for _ in range(samples_per_digit):
            img = generate_synthetic_symbol(str(digit))
            images.append(img)
            labels.append(str(digit))
    return np.array(images), labels


def generate_math_dataset(samples_per_class=1000):
    """
    Build complete dataset for all 22 classes.
    Returns:
        X: np.ndarray of shape (N, 28, 28, 1), normalized to [0, 1]
        y: np.ndarray of shape (N,), integer class labels 0..21
    """
    images = []
    labels = []

    print("Generating training dataset for 22 mathematical classes...")
    # 1. Digits 0-9
    digit_imgs, digit_labels = load_mnist_subset(samples_per_class)
    images.extend(digit_imgs)
    labels.extend(digit_labels)

    # 2. Math operators and symbols (+, -, *, /, =, ., (, ), x, y, ^, ,)
    non_digit_classes = [c for c in CLASSES if not c.isdigit()]
    for symbol in non_digit_classes:
        for _ in range(samples_per_class):
            img = generate_synthetic_symbol(symbol)
            images.append(img)
            labels.append(symbol)

    X = np.array(images, dtype=np.float32)
    if X.ndim == 3:
        X = np.expand_dims(X, axis=-1)

    # Normalize to [0.0, 1.0]
    X = X / 255.0

    # Convert string labels to integer indices
    y = np.array([LABEL_TO_INDEX[lbl] for lbl in labels], dtype=np.int32)

    # Shuffle
    indices = np.arange(len(X))
    np.random.shuffle(indices)
    X = X[indices]
    y = y[indices]

    print(f"Dataset generated: {len(X)} total samples across {NUM_CLASSES} classes.")
    return X, y


def split_train_val_test(X, y, train_ratio=0.8, val_ratio=0.1):
    """Split dataset into Train, Validation, and Test sets."""
    n = len(X)
    train_end = int(n * train_ratio)
    val_end = int(n * (train_ratio + val_ratio))

    X_train, y_train = X[:train_end], y[:train_end]
    X_val, y_val = X[train_end:val_end], y[train_end:val_end]
    X_test, y_test = X[val_end:], y[val_end:]

    return (X_train, y_train), (X_val, y_val), (X_test, y_test)


if __name__ == '__main__':
    X, y = generate_math_dataset(samples_per_class=100)
    print("X shape:", X.shape, "y shape:", y.shape)
    (x_tr, y_tr), (x_va, y_va), (x_te, y_te) = split_train_val_test(X, y)
    print(f"Splits: Train {len(x_tr)}, Val {len(x_va)}, Test {len(x_te)}")
