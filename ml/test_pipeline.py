"""
Unit tests for Math OCR Training Pipeline and TFLite Export.
"""

import os
import unittest
import numpy as np
from dataset import CLASSES, NUM_CLASSES, generate_synthetic_symbol, generate_math_dataset
from tflite_builder import build_math_symbol_tflite


class TestMathOCRPipeline(unittest.TestCase):

    def test_classes_count_and_content(self):
        self.assertEqual(NUM_CLASSES, 22)
        expected_classes = [
            '0', '1', '2', '3', '4', '5', '6', '7', '8', '9',
            '+', '-', '*', '/', '=', '.', '(', ')', 'x', 'y', '^', ','
        ]
        self.assertEqual(CLASSES, expected_classes)

    def test_synthetic_symbol_generation(self):
        for sym in CLASSES:
            img = generate_synthetic_symbol(sym, size=28)
            self.assertEqual(img.shape, (28, 28))
            self.assertEqual(img.dtype, np.float32)
            self.assertGreaterEqual(img.min(), 0.0)
            self.assertLessEqual(img.max(), 255.0)

    def test_generate_math_dataset(self):
        samples_per_class = 5
        X, y = generate_math_dataset(samples_per_class=samples_per_class)
        self.assertEqual(len(X), NUM_CLASSES * samples_per_class)
        self.assertEqual(len(y), NUM_CLASSES * samples_per_class)
        self.assertEqual(X.shape[1:], (28, 28, 1))
        self.assertGreaterEqual(X.min(), 0.0)
        self.assertLessEqual(X.max(), 1.0)

    def test_tflite_model_builder_and_asset_integrity(self):
        tmp_model = "/tmp/test_export.tflite"
        w1 = np.random.randn(128, 784).astype(np.float32)
        b1 = np.zeros(128, dtype=np.float32)
        w2 = np.random.randn(22, 128).astype(np.float32)
        b2 = np.zeros(22, dtype=np.float32)

        build_math_symbol_tflite(w1, b1, w2, b2, tmp_model)
        self.assertTrue(os.path.exists(tmp_model))

        with open(tmp_model, "rb") as f:
            data = f.read()

        # Check FlatBuffers TFLite identifier TFL3
        self.assertEqual(data[4:8], b"TFL3")
        # Ensure model is lightweight (< 2MB)
        self.assertLess(len(data), 2 * 1024 * 1024)

    def test_deployed_android_assets(self):
        assets_dir = os.path.abspath(
            os.path.join(os.path.dirname(__file__), "..", "app", "src", "main", "assets", "models")
        )
        model_path = os.path.join(assets_dir, "math_symbol_model.tflite")
        labels_path = os.path.join(assets_dir, "labels.txt")

        self.assertTrue(os.path.exists(model_path), "math_symbol_model.tflite missing from assets")
        self.assertTrue(os.path.exists(labels_path), "labels.txt missing from assets")

        # Verify model header and size
        with open(model_path, "rb") as f:
            data = f.read()
        self.assertEqual(data[4:8], b"TFL3")
        self.assertLess(len(data), 2 * 1024 * 1024)

        # Verify labels content
        with open(labels_path, "r", encoding="utf-8") as f:
            lines = [l.strip() for l in f if l.strip()]
        self.assertEqual(lines, CLASSES)


if __name__ == '__main__':
    unittest.main()
